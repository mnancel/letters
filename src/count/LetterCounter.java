package count;

import static java.lang.Math.abs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clean.Cleaner;
import clean.CleanerSet;
import clean.HtmlCharsCleaner;
import clean.RegexpCleaner;

public class CountLettersFromFile {
	
	public static final String RESULTS_FOLDER = "results";
	
	public static final String SEP = " "; 	// "\t";
	public static final String NEWLINE = "\n";
	
	
	
	/**
	 * Keeps only the text between simple <p/> markers
	 */
	public static final RegexpCleaner pIdCleaner = new RegexpCleaner(
			"^\\s+<p id=\"[^\"]+\">(.*)</p>$", "$1", false);
	
	/**
	 * Keeps only the text between simplER <p/> markers
	 */
	public static final RegexpCleaner pCleaner = new RegexpCleaner(
			"<[pP][^>]*>(.*)</[pP]>$", "$1", false);
	
	/**
	 * Keeps only the lines that don't start with '<' or end with '>'
	 */
	public static final RegexpCleaner MarkupCleaner = new RegexpCleaner(
			"^([^<].*[^>])$", "$1", false);
	
	/**
	 * Space before a punctuation mark when there shouldn't be one.
	 */
	public static final RegexpCleaner punctuationCleaner1 = new RegexpCleaner(
			" ([,\\.\\)])", "$1", true);
	
	/**
	 * No space before a punctuation mark when there should be one
	 */
	public static final RegexpCleaner punctuationCleaner2 = new RegexpCleaner(
			"([\\w\\d])([!\\?;:\\(])", "$1 $2", true);
	
	/**
	 * No space after a punctuation mark when there should be one.
	 * EDIT: kept the comma and period out because of numbers.
	 */
	public static final RegexpCleaner punctuationCleaner3 = new RegexpCleaner(
			"([!\\?;:\\)…])([\\w\\d])", "$1 $2", true);
	
	/**
	 * Special case of space after period and comma within a number
	 */
	public static final RegexpCleaner punctuationCleaner4 = new RegexpCleaner(
			"(\\d) ?([,\\.]) ?(\\d)", "$1$2$3", true);
	
	public static final HtmlCharsCleaner htmlCharsCleaner = 
			new HtmlCharsCleaner();
	
	public static final String noAccentRegex = "[a-zA-Z]";
	
	
	/* ---------- */
	
	
	/**
	 * Parses an inputFile to count n-grams (up to maxNGrams). If _count is 
	 * null, starts counting from zero for all n-grams. Else, starts from the
	 * provided counts.
	 * Specifies an encoding.
	 * 
	 * The count structure is as follows:
	 * count.get(n) is a hashmap linking (n+1)-grams to its # of occurrences.
	 * So for instance, count.get(1).get("ab") will return the number of times
	 * "ab" was found in the inputFiles. Remember that to access, say, bigrams,
	 * the argument of count.get() has to be 2-1=1 since lists indexes start at
	 * zero. 
	 * 
	 * @param inputFile
	 * @param maxNGrams
	 * @param _count
	 * @return
	 */
	@SuppressWarnings("boxing")
	public ArrayList<HashMap<String, Integer>> analyzeInputFile(
			File[] inputFiles, int maxNGrams, 
			ArrayList<HashMap<String, Integer>> _count,
			CleanerSet cleanerSet, String _outputFileName, 
			String encoding) {
		
		ArrayList<HashMap<String, Integer>> count;
		
		if (_count == null) {
			count = new ArrayList<HashMap<String,Integer>>();
			for (int n = 0 ; n < maxNGrams ; n++) {
				count.add(new HashMap<String, Integer>());
			}
		} else {
			count = _count;
		}
		
		double totalSize = 0, totalSumSize = 0;
		for (File inputFile : inputFiles) {
			totalSize += inputFile.length();
		}
		
		try {
			
			int fileNb = -1;
			double sumSize = 0, totalDuration = 0;
			
			for (File inputFile : inputFiles) {
				
				fileNb++;
			
				double size = inputFile.length();
				
				// Reading and counting
				
				Charset charset;
				if (encoding == null) {
					charset = Charset.defaultCharset();
				} else {
					charset = Charset.forName(encoding);
				}
				
				InputStreamReader isr = new InputStreamReader(
						new FileInputStream(inputFile), charset);
				BufferedReader reader = new BufferedReader(isr);
				
				String line;
				
				long startTime = System.currentTimeMillis();
				
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					if (cleanerSet != null) {
						for (Cleaner c : cleanerSet) {
							line = c.test(line);
						}
					}
					
					if (line == null) {
						continue;
					}
					
					line.trim();
					
					// for each character in the line
					for (int i = 0 ; i < line.length() ; i++) {
						
						// for each nGram size
						for (int n = 0 ; n < maxNGrams ; n++) {
							
							if (i >= n) {
								
//								String nGram = new String(buffer, i-n, n+1);
								String nGram = line.substring(i-n, i+1);
//								nGram = nGram.replace(" ", "space");
								
								// Excluding tsv-involved characters
								if (
//										nGram.contains(SEP) 
//										|| 
										nGram.contains(NEWLINE)) {
									continue;
								}
								
								if (count.get(n).containsKey(nGram)) {
									count.get(n).put(
											nGram, 
											count.get(n).get(nGram)+1); 
								} else {
									count.get(n).put(nGram, 1);
								}
								
							}
							
						}
						
					}
					
				}
				
				double duration = 1d*(System.currentTimeMillis() - startTime)
						/1000d;
				
				if (inputFiles.length < 11) {
					
					double rate = Math.round(100*duration / (size/1e6)) / 100d;
					
					System.out.println(inputFile.getName() + " : " 
							+ rate + " seconds per Mo, ~"
									+ (int)(rate * ((totalSize - totalSumSize)
											/ 1e6 )) 
									+ "s remaining" );
				} else {
					
					sumSize += size;
					totalSumSize += size;
					totalDuration += duration;
					
					if (fileNb % 100 == 0 || fileNb == inputFiles.length - 1) {
						
						double rate = Math.round(
								100 * totalDuration / (sumSize/1e6)) / 100d;
						
						System.out.println(fileNb + "th file / "
								+ inputFiles.length + " : "
								+ rate + " seconds per Mo, ~"
								+ (int)(rate * ((totalSize - totalSumSize)
										/ 1e6 ))
								+ "s remaining" );
						
						totalDuration = 0;
						sumSize = 0;
						
					}
				}
				
				reader.close();
			
			}
			
			// Writing
			
			for (int n = 0 ; n < maxNGrams ; n++) {
				
				ArrayList<Entry<String, Integer>> r = 
						new ArrayList<Map.Entry<String,Integer>>(
								count.get(n).entrySet());
				
				Collections.sort(r, new Comparator<Entry<String, Integer>>() {
					public int compare(Entry<String, Integer> o1, 
							Entry<String, Integer> o2) {
						return o2.getValue().compareTo(o1.getValue());
					}
				});
				
				String outputFileName = RESULTS_FOLDER + File.separator
						+ _outputFileName + "_output_" + (n+1) + ".tsv"; 
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (Entry<String, Integer> e : r) {
					writer.write(
							format(e.getKey()) + SEP + e.getValue() + NEWLINE);
				}
				
				System.out.println("Wrote " + outputFileName);
				
				writer.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return count;
		
	}
	
	/**
	 * /**
	 * Parses an inputFile to count n-grams (up to maxNGrams). If _count is 
	 * null, starts counting from zero for all n-grams. Else, starts from the
	 * provided counts.
	 * Does not specify an encoding.
	 * 
	 * The count structure is as follows:
	 * count.get(n) is a hashmap linking (n+1)-grams to its # of occurrences.
	 * So for instance, count.get(1).get("ab") will return the number of times
	 * "ab" was found in the inputFiles. Remember that to access, say, bigrams,
	 * the argument of count.get() has to be 2-1=1 since lists indexes start at
	 * zero. 
	 * 
	 * @param inputFiles
	 * @param maxNGrams
	 * @param _count
	 * @param cleanerSet
	 * @param outputFileName
	 * @return
	 */
	public ArrayList<HashMap<String, Integer>> analyzeInputFile(
			File[] inputFiles, int maxNGrams, 
			ArrayList<HashMap<String, Integer>> _count,
			CleanerSet cleanerSet, String outputFileName) {
		
		return analyzeInputFile(inputFiles, maxNGrams, _count, cleanerSet, 
				outputFileName, null);
	}
	
	
	/**
	 * Parses an inputFile to count n-grams (up to maxNGrams) and updates the 
	 * existing counts in separate outputFiles per n(-grams).
	 * Specifies a character encoding.
	 * 
	 * The count structure is as follows:
	 * count.get(n) is a hashmap linking (n+1)-grams to its # of occurrences.
	 * So for instance, count.get(1).get("ab") will return the number of times
	 * "ab" was found in the inputFiles. Remember that to access, say, bigrams,
	 * the argument of count.get() has to be 2-1=1 since lists indexes start at
	 * zero.
	 * 
	 * @param inputFiles
	 * @param maxNGrams
	 * @param cleanerSet
	 * @param outputFiles
	 * @param outputFileName
	 * @param encoding
	 * @return
	 */
	@SuppressWarnings("boxing")
	ArrayList<HashMap<String, Integer>> analyzeInputFileAndUpdate(
			File[] inputFiles, int maxNGrams, CleanerSet cleanerSet,
			File[] outputFiles, String outputFileName, String encoding) {
		
		ArrayList<HashMap<String, Integer>> count = 
				new ArrayList<HashMap<String,Integer>>();
		
		for (int n = 0 ; n < outputFiles.length ; n++) {
			
			count.add(new HashMap<String, Integer>());
			
			try {
				BufferedReader reader = new BufferedReader(
						new FileReader(outputFiles[n]));
				
				String line;
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					String[] split = line.split("[ \t]");
					
					count.get(n).put(split[0], Integer.parseInt(split[1]));
					
				}
				
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return analyzeInputFile(inputFiles, maxNGrams, count, cleanerSet, 
				outputFileName, encoding);
		
	}
	
	/**
	 * Parses an inputFile to count n-grams (up to maxNGrams) and updates the 
	 * existing counts in separate outputFiles per n(-grams).
	 * Does not specify a character encoding.
	 * 
	 * The count structure is as follows:
	 * count.get(n) is a hashmap linking (n+1)-grams to its # of occurrences.
	 * So for instance, count.get(1).get("ab") will return the number of times
	 * "ab" was found in the inputFiles. Remember that to access, say, bigrams,
	 * the argument of count.get() has to be 2-1=1 since lists indexes start at
	 * zero.
	 * 
	 * @param inputFiles
	 * @param maxNGrams
	 * @param cleanerSet
	 * @param outputFiles
	 * @param outputFileName
	 * @param encoding
	 * @return
	 */
	ArrayList<HashMap<String, Integer>> analyzeInputFileAndUpdate(
			File[] inputFiles, int maxNGrams, CleanerSet cleanerSet,
			File[] outputFiles, String outputFileName) {
		
		return analyzeInputFileAndUpdate(inputFiles, maxNGrams, cleanerSet, 
				outputFiles, outputFileName, null);
		
	}
	
	
	/**
	 * Replaces special characters by their textual representation, e.g. " "
	 * becoomes "space".
	 * 
	 * @param c
	 * @return
	 */
	static public String replaceSpecChars(String c) {
		return c.replaceAll("\t", "tab")
				.replaceAll("\n", "return")
				.replaceAll(" ", "space");
	}
	
	/**
	 * Applies replaceSpecChars() to a String.
	 * 
	 * @param _key
	 * @return
	 */
	static public String format(String _key) {
		
		if (_key.length() == 0) {
			System.err.println("Weird: empty key");
			return _key;
		}
		
		// 1st check if it's the right length or if it's already been formatted
		
		if ( _key.length() > 2 ) {
			return _key;
		}
		
		String key = "";
		
		String[] keyArray = new String[_key.length()];
		for (int i = 0 ; i < _key.length() ; i++) {
			keyArray[i] = ""+_key.toCharArray()[i];
			
			keyArray[i] = replaceSpecChars(keyArray[i]);
			
			key += keyArray[i];
			if (i < _key.length()-1) {
				key += " ";
			}
		}
		
//		if (key.contains("space")) {
//			System.out.println("Format " + _key + " into " + key);
//		}
		
		return key;
	}
	
	/**
	 * Reads n-gram counts from a set of input files and calculates the 
	 * corresponding frequencies, and writes them in a new file.
	 * 
	 * countFiles is a set of keys and number of occurrences, as is output by
	 * analyzeInputFileAndUpdate().
	 * 
	 * @param countFiles
	 */
	@SuppressWarnings("boxing")
	public void frequenciesFromCounts(File[] countFiles) {
		
		TreeMap<String, HashMap<String, Double>> probas = 
				new TreeMap<String, HashMap<String, Double>>();
		
		int nbLines;
		
		for (File file : countFiles) {
			
			double frequenciesTotal = 0;
			
			nbLines = 0;
			
			probas.put(file.getName(), new HashMap<String, Double>());
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				long sum = 0;
				
				final HashMap<String, Long> counts = 
						new HashMap<String, Long>();
				
				
				String line = reader.readLine();
//				String sep = ""+line.charAt(1); // ??
				Pattern p = Pattern.compile("([^\\d])\\d+$");
				Matcher m = p.matcher(line);
				m.find();
				String sep = m.group(1);
				
				for ( ; line != null ; 
						line = reader.readLine()) {
					
					String[] split = line.split(sep);
					
//					if (split.length == 1) {
//						System.err.print(
//								file + ":\t"
//								+ "Wrong size for split: " + split.length
//								+ "(separator ");
//						if (sep.equalsIgnoreCase(" ")) {
//							System.err.println("[space] )");
//						} else if (sep.equalsIgnoreCase("\t")) {
//							System.err.println("[tab] )");
//						} else {
//							System.err.println(sep + " )");
//						}
//						System.err.print("\t");
//						for (String s : split) {
//							System.err.print(s + "\t");
//						}
//						System.err.println();
//						continue;
//					}
					
					long count = Long.parseLong(split[split.length-1].trim());
					
					// Last thing I did: construct key instead of split[0]
					String key = "";
					
					if (split.length == 3) {
						
						key = replaceSpecChars(split[0]) + " " 
								+ replaceSpecChars(split[1]);
						
					} else if (split.length == 2) {
						
						if (split[0].length() == 1 
								|| split[0].equalsIgnoreCase("space")) {
							
							key = replaceSpecChars(split[0]);
							
						} else if (split[0].length() > 1) {
							
							if (split[0].matches("[^\\s]\\s?space")) {
								
								key = split[0].charAt(0) + " space";
								
							} else if (split[0].matches("space\\s?[^\\s]")) {
								
								key = "space " 
										+ split[0].charAt(split[0].length()-1);
								
							} else if (split[0].length() == 2
									|| split[0].matches("[^\\s]\\s?[^\\s]")) {
								
								key = replaceSpecChars(""+split[0].charAt(0)) 
										+ " " 
										+ replaceSpecChars(
												"" + split[0].charAt(
														split[0].length()-1));
										
							} else {
								System.err.println(
										"Something wrong with split[0]: " 
										+ split[0]);
							}
							
						} else {
							System.err.println("split[0] seems to be empty: "
									+ split[0]);
						}
						
					} else {
						System.err.println("Something weird with split. Line: "
								+ line);
					}
//					
//					for (int i = 0 ; i < split.length-1 ; i++) {
//						System.out.println(split[i]);
//						key += split[i] + " ";
//					}
//					key = key.trim();
//					System.out.println("\tConstructed " + key);
					
					counts.put(key, count);
					sum += count;
					nbLines++;
					
				}
				
				reader.close();
				
				String outputFileName = RESULTS_FOLDER + File.separator 
						+ file.getName().replaceFirst(
								"(\\.[\\d\\w]+)$", 
								"_freqs.tsv");
				
				ArrayList<String> orderedKeys = new ArrayList<String>();
				orderedKeys.addAll(counts.keySet());
				Collections.sort(orderedKeys, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return -counts.get(o1).compareTo(
								counts.get(o2));
					}
				});
				
				System.out.println(orderedKeys.size() + " ?= " 
						+ counts.keySet().size());				
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (String _key : orderedKeys) {
					
//					if (outputFileName.contains("TwitterClassic_output_1"))
//						System.out.println(_key);
					
					double freq = 1d * counts.get(_key) / sum;
					
					String key = format(_key);
					
					writer.write(key + SEP + freq + NEWLINE);
					frequenciesTotal += freq;
					probas.get(file.getName()).put(key, freq);
					
				}
				
				System.out.println("Wrote freqs for " + file.getName() + ""
						+ "(" + nbLines + " lines, " 
						+ counts.keySet().size() + " keys, "
						+ orderedKeys.size() + " ordered). "
						+ "Totals: " + frequenciesTotal);
				
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println();
		
		
		ArrayList<HashMap<String, HashMap<String, Double>>> distances = 
				calculateDistances(probas, true);

		// Writing in files
		
		for (int n = 0 ; n < distances.size() ; n++) {
		
			try {
				
				File dir = new File(
						RESULTS_FOLDER + File.separator + "distances");
				
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				String fileName = RESULTS_FOLDER + File.separator 
						+ "distances" + File.separator
						+ "distances_" + (n+1) + ".tsv";
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						fileName));
				
				// Column titles
				writer.write("corpus 1"
						+ "\t" + "corpus 2"
						+ "\t" + "distance"
						+ NEWLINE);
				
				for ( String f1 : distances.get(n).keySet() ) {
					for (String f2 : distances.get(n).get(f1).keySet()) {
						
						writer.write(f1 + "\t" + f2 + "\t" 
								+ distances.get(n).get(f1).get(f2) + NEWLINE);
						
					}
				}
				
				writer.close();
				
				System.out.println("Distance file " + fileName + " created.");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * TODO 
	 * 
	 * @param probas
	 * @param ignoreAlpha
	 * @return
	 */
	public ArrayList<HashMap<String, HashMap<String, Double>>> 
			calculateDistances( 
					TreeMap<String, HashMap<String, Double>> probas,
					boolean ignoreAlpha) {
		
		/*
		 *  Calculating distances
		 */
		
		// Keys of the TreeMaps should already be sorted
		
		ArrayList<HashMap<String, HashMap<String, Double>>> result = 
				new ArrayList<HashMap<String,HashMap<String,Double>>>();
		
		Pattern pattern = Pattern.compile("_(\\d)");
		
		for (String f1 : probas.keySet()) {
			
			for (String f2: probas.keySet()) {
				
				int n = -1;
				
				// Checking that we're working with the same type of n-gram...
				Matcher m1 = pattern.matcher(f1);
				Matcher m2 = pattern.matcher(f2);
				
				if (m1.find() && m2.find()) {
					
					int n1 = Integer.parseInt(m1.group().substring(1));
					int n2 = Integer.parseInt(m2.group().substring(1));
					
					if (n1 == n2) {
						n = n1 - 1;
					} else {
//						System.err.println("Different gram size between "
//								+ f1 + " and " + f2);
						continue;
					}
				} else {
					System.err.println("Weird, can't find the gram size in "
							+ f1 + " or " + f2);
					continue;
				}
				
				while (result.size() <= n) {
					result.add(new HashMap<String, HashMap<String,Double>>());
				}
				
				if (f1.compareToIgnoreCase(f2) > 0) {
					
					if (!result.get(n).containsKey(f1)) {
						result.get(n).put(f1, new HashMap<String, Double>());
					}
					if (result.get(n).get(f1).containsKey(f2)) {
						System.err.println("Um that's weird."
								+ "\n\t" + f1 + "\n\t" + f2);
					}
					
					/*
					 *  Calculate the difference between all entries, ignoring 
					 *  the ones that are not in both files.
					 */
					
					double score = 0;
					String nGramRegex = "";
					for (int i = 0 ; i < n+1 ; i++) {
						nGramRegex += noAccentRegex;
					}
					Pattern noAccentPattern = Pattern.compile(nGramRegex);
					
					for (String key : probas.get(f1).keySet()) {
						
						if ( probas.get(f2).containsKey(key) ) {
							
							if (ignoreAlpha 
									&& noAccentPattern.matcher(key).matches()) {
//								System.out.println("Excluding " + key);
								continue;
							}
							
							score += abs( 
									probas.get(f1).get(key) 
									- probas.get(f2).get(key) );
							
						}
						// otherwise, don't affect the score
					}
					
					result.get(n).get(f1).put(f2, score);
					
				}
				
			}
			
		}
		
//		for (int i = 0 ; i < result.size() ; i++) {
//			for (String f1: result.get(i).keySet()) {
//				for (String f2: result.get(i).get(f1).keySet()) {
//					System.out.println( f1 + "\t" + f2 + "\t"
//							+ result.get(i).get(f1).get(f2));
//				}
//			}
//		}
		
		return result;
		
	}
	
//	public static void main(String[] args) {
//		
//		TreeMap<String, HashMap<String, Double>> probas = 
//				new TreeMap<String, HashMap<String, Double>>();
//		
//		probas.put("file1_1", new HashMap<String, Double>());
//		probas.get("file1_1").put("a", 1d);
//		probas.get("file1_1").put("b", 2d);
//		probas.get("file1_1").put("c", 3d);
//		probas.get("file1_1").put("d", 4d);
//		probas.get("file1_1").put("e", 5d);
//		probas.put("file2_1", new HashMap<String, Double>());
//		probas.get("file2_1").put("b", 200d);
//		probas.get("file2_1").put("c", 300d);
//		probas.get("file2_1").put("d", 400d);
//		probas.get("file2_1").put("e", 500d);
//		probas.get("file2_1").put("f", 600d);
//		probas.put("file3_1", new HashMap<String, Double>());
//		probas.get("file3_1").put("c", 30d);
//		probas.get("file3_1").put("d", 40d);
//		probas.get("file3_1").put("e", 50d);
//		probas.get("file3_1").put("f", 60d);
//		probas.get("file3_1").put("g", 70d);
//		
//		new CountLettersFromFile().calculateDistances(probas);
//		
//	}
	
}
