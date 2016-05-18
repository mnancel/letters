package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import clean.Cleaner;
import clean.CleanerSet;
import clean.HtmlCharsCleaner;
import clean.RegexpCleaner;

public class CountLettersFromFile {
	
	public static final String RESULTS_FOLDER = "results";
	
	public static final String TAB = "\t";
	public static final String ENTER = "\n";
	
	ArrayList<HashMap<String, Integer>> analyzeInputFileAndUpdate(
			File[] inputFiles, int maxNGrams, CleanerSet cleanerSet,
			File[] outputFiles) {
		
		return analyzeInputFileAndUpdate(inputFiles, maxNGrams, cleanerSet, 
				outputFiles, null);
		
	}
	
	/**
	 * Parses an inputFile to count n-grams (up to maxNGrams) and updates the 
	 * existing counts in separate outputFiles per n(-grams).
	 * 
	 * @param inputFile
	 * @param maxNGrams
	 * @param outputFiles
	 * @return
	 */
	@SuppressWarnings("boxing")
	ArrayList<HashMap<String, Integer>> analyzeInputFileAndUpdate(
			File[] inputFiles, int maxNGrams, CleanerSet cleanerSet,
			File[] outputFiles, String encoding) {
		
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
					
					String[] split = line.split("\t");
					
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
				encoding);
		
	}
	
	
	public ArrayList<HashMap<String, Integer>> analyzeInputFile(
			File[] inputFiles, int maxNGrams, 
			ArrayList<HashMap<String, Integer>> _count,
			CleanerSet cleanerSet) {
		return analyzeInputFile(inputFiles, maxNGrams, _count, cleanerSet, 
				null);
	}
	
	/**
	 * Parses an inputFile to count n-grams (up to maxNGrams). If _count is 
	 * null, starts counting from zero for all n-grams. Else, starts from the
	 * provided counts.
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
			CleanerSet cleanerSet, String encoding) {
		
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
								
								// Excluding tsv-involved characters
								if (nGram.contains(TAB) 
										|| nGram.contains(ENTER)) {
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
									+ (int)(rate * ((totalSize - totalSumSize)/1e6)) 
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
								+ (int)(rate * ((totalSize - totalSumSize)/1e6)) 
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
				
				String outputFileName; 
				
//				if (inputFiles.length == 1) {
//					
//					outputFileName = inputFiles[0].getName();
//					
//				} else {
//					
//					// Ask for file name
//					
//					String s = (String)JOptionPane.showInputDialog(
//							null,
//							"Provide output file name root.",
//							"Dialog tavu",
//							JOptionPane.PLAIN_MESSAGE,
//							null,
//							null,
//							null);
//
//					if ((s == null) || (s.length() > 0)) {
//						
//						System.err.println("No file name was provided. "
//								+ "Using inputFiles[0]'s parent.");
					
					outputFileName = 
							inputFiles[0].getParentFile().getName();
						
//					} else {
//						
//						outputFileName = s;
//						
//					}
//					
//				}
				
				if (outputFileName.matches("(\\.[\\d\\w]+)$")) {
					outputFileName = RESULTS_FOLDER + File.separator 
							+ outputFileName.replaceFirst(
									"(\\.[\\d\\w]+)$", "_output_" + (n+1) 
	//								+ "$1");
									+ ".tsv");
				} else {
					outputFileName = RESULTS_FOLDER + File.separator
							+ outputFileName + "_output_" + (n+1) + ".tsv";
				}
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (Entry<String, Integer> e : r) {
					writer.write(e.getKey() + "\t" + e.getValue() + "\n");
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
	 * Reads n-gram counts from an input file and calculates the corresponding
	 * frequencies, and writes them in a new file.
	 * 
	 * @param countFiles
	 */
	@SuppressWarnings("boxing")
	public void frequenciesFromCounts(File[] countFiles) {
		
		for (File f : countFiles) {
			
			double frequenciesTotal = 0;
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				
				long sum = 0;
				
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<Long> counts = new ArrayList<Long>();
				
				String line;
				
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					String[] split = line.split("\t");
					
					keys.add(split[0]);
					long count = Long.parseLong(split[1]);
					counts.add(count);
					sum += count;
					
				}
				
				reader.close();
				
				String outputFileName = RESULTS_FOLDER + File.separator 
						+ f.getName().replaceFirst(
								"(\\.[\\d\\w]+)$", 
								"_freqs.tsv");
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (int i = 0 ; i < keys.size() ; i++) {
					writer.write(keys.get(i) + "\t" 
							+ (1d * counts.get(i) / sum)
							+ "\n");
					frequenciesTotal += (1d * counts.get(i) / sum);
				}
				
				System.out.println("Wrote freqs for " + f.getName() + ". "
						+ "Totals: " + frequenciesTotal);
				
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println("All done.");
		
	}
	
	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		
		int nbGrams = 2;
		CountLettersFromFile clf = new CountLettersFromFile();
		
		// Keeps only the text between simple <p/> markers
		RegexpCleaner pIdCleaner = new RegexpCleaner(
				"^\\s+<p id=\"[^\"]+\">(.*)</p>$", "$1", false);
		
//		Keeps only the text between simplER <p/> markers
		RegexpCleaner pCleaner = new RegexpCleaner(
				"<[pP][^>]*>(.*)</[pP]>$", "$1", false);
		
		// Keeps only the lines that don't start with '<' or end with '>'
		RegexpCleaner SCleaner = new RegexpCleaner(
				"^([^<].*[^>])$", "$1", false);
		
		// Space before a punctuation mark when there shouldn't be one
		RegexpCleaner punctuationCleaner1 = new RegexpCleaner(
				" ([,\\.\\)])", "$1", true);
		// No space before a punctuation mark when there should be one
		RegexpCleaner punctuationCleaner2 = new RegexpCleaner(
				"([\\w\\d])([!\\?;:\\(])", "$1 $2", true);
		// No space after a punctuation mark when there should be one
		// EDIT: kept the comma and period out because of numbers.
		RegexpCleaner punctuationCleaner3 = new RegexpCleaner(
				"([!\\?;:\\)…])([\\w\\d])", "$1 $2", true);
		
		// Special case of space after period and comma within a number
		RegexpCleaner punctuationCleaner4 = new RegexpCleaner(
				"(\\d) ?([,\\.]) ?(\\d)", "$1$2$3", true);
		
		HtmlCharsCleaner htmlCharsCleaner = new HtmlCharsCleaner();
		
		
		
		/*
		 *  W0057
		 *  
		 *  No change needed.
		 */
		
//		File w0057Folder = new File("/Volumes/Uranium/ELDA/W0057");
//		File[] fileListW0057 = w0057Folder.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.getName().endsWith(".fr");
//			}
//		});
//		
//		clf.analyzeInputFile(fileListW0057, nbGrams, null, null);
		
		
		
		/*
		 *  W0058
		 *  
		 *  No change needed.
		 */
		
//		File w0058Folder = new File("/Volumes/Uranium/ELDA/W0058");
//		File[] fileListW0058 = w0058Folder.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.getName().endsWith(".fr");
//			}
//		});
//		
//		clf.analyzeInputFile(fileListW0058, nbGrams, null, null);
		
		
		
		/*
		 *  W0065
		 *  
		 *  XML format with some spacing issues with the punctuation.
		 *  Need to keep only the <p> content.
		 */
		
//		File w0065Folder = new File("/Volumes/Uranium/ELDA/W0065/ENV_FR");
//		File[] fileListW0065 = w0065Folder.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.getName().endsWith(".xml");
//			}
//		});
//		
//		CleanerSet w0065Cleaners = new CleanerSet(
//				pCleaner, 
//				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);
//		
////		System.out.println(w0065Cleaners.testFile(fileListW0065[20]));
//		
//		clf.analyzeInputFile(fileListW0065, nbGrams, null, w0065Cleaners);
		
		
		
		/*
		 *  W0066
		 *  
		 *  Same as W0065
		 */
		
//		File w0066Folder = new File("/Volumes/Uranium/ELDA/W0066/LAB_FR");
//		File[] fileListW0066 = w0066Folder.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.getName().endsWith(".xml");
//			}
//		});
//		
//		CleanerSet w0066Cleaners = new CleanerSet(
//				pCleaner, 
//				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);
//		
////		System.out.println(w0066Cleaners.testFile(fileListW0066[1]));
//		
//		clf.analyzeInputFile(fileListW0066, nbGrams, null, w0066Cleaners);
		
		
		
//		/*
//		 *  W0017
//		 *  
//		 *  XML format with some HTML codes and an "ISO-8859-1" encoding
//		 *  Need to keep only the lines with no xml markups, apparently.
//		 */
//		
//		File w0017Folder = new File(
//				"/Volumes/Uranium/ELDA/W0017/Data/FR/Sentences");
//		File[] fileListW0017 = w0017Folder.listFiles();
//		
//		CleanerSet w0017Cleaners = new CleanerSet(
//				SCleaner, 
//				htmlCharsCleaner,
//				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);
//		
////		System.out.println(w0017Cleaners.testFile(fileListW0017[1]));
//		
//		clf.analyzeInputFile(fileListW0017, nbGrams, null, w0017Cleaners, 
//				"ISO-8859-1");
		
		
		
		/*
		 *  W0036
		 *  
		 *  HTML format with some HTML codes.
		 *  Article bodies seem contained within "<p>".
		 *  Trusting them for the spaces around punctuation.
		 */
		
//		File w0036Folder = new File(
//				"/Volumes/Uranium/ELDA/W0036-01");
//		File[] fileListW0036;
//		ArrayList<File> files = new ArrayList<File>();
//		
//		
//		for (File f : w0036Folder.listFiles()) {
//			if (!f.isDirectory()) {
//				continue;
//			}
//			for (File f2 : f.listFiles()) {
//				if (!f2.isDirectory()) {
//					continue;
//				}
//				for (File f3 : f2.listFiles(new FilenameFilter() {
//					
//					public boolean accept(File dir, String name) {
//						return name.endsWith(".html");
//					}
//					
//				})) {
//					files.add(f3);
//				}
//			}
//		}
//		
//		
//		fileListW0036 = files.toArray(new File[0]);
//		
//		RegexpCleaner markupCleaner = new RegexpCleaner(
//				"<[^>]+>", "", true);
//		
//		CleanerSet w0036Cleaners = new CleanerSet(
//				pCleaner,
//				markupCleaner,
//				htmlCharsCleaner
//				);
//		
////		System.out.println(w0036Cleaners.testFile(fileListW0036[20]));
//		
//		clf.analyzeInputFile(fileListW0036, nbGrams, null, w0036Cleaners);
		
		
		
		/*
		 *  W0015
		 *  
		 *  HTML format with some HTML codes and an "ISO-8859-1" encoding.
		 *  Article bodies seem contained within "<p>".
		 *  Trusting them for the spaces around punctuation.
		 */
		
//		File w0015Folder = new File(
//				"/Volumes/Uranium/ELDA/W0015");
//		File[] fileListW0015;
//		ArrayList<File> files = new ArrayList<File>();
//		
//		
//		for (File f : w0015Folder.listFiles()) {
//			if (!f.isDirectory()) {
//				continue;
//			}
//			for (File f2 : f.listFiles()) {
//				if (!f2.isDirectory()) {
//					continue;
//				}
//				for (File f3 : f2.listFiles(new FilenameFilter() {
//					
//					public boolean accept(File dir, String name) {
//						return name.endsWith(".xml");
//					}
//					
//				})) {
//					files.add(f3);
//				}
//			}
//		}
//		
//		
//		fileListW0015 = files.toArray(new File[0]);
//		
//		RegexpCleaner markupCleaner = new RegexpCleaner(
//				"<[^>]+>", "", true);
//		
//		CleanerSet w0015Cleaners = new CleanerSet(
//				pCleaner,
//				markupCleaner,
//				htmlCharsCleaner
//				);
//		
////		System.out.println(w0015Cleaners.testFile(fileListW0015[20]));
//		
//		clf.analyzeInputFile(fileListW0015, nbGrams, null, w0015Cleaners, 
//				"ISO-8859-1");
		
		
		
		
		/*
		 *  Est Républicain
		 *  
		 *  HTML format, no HTML codes.
		 *  Article bodies seem contained within "<p>".
		 *  Trusting them for the spaces around punctuation.
		 */
		
//		File w00EstFolder = new File(
//				"/Volumes/Uranium/Est Républicain");
//		File[] fileListEst;
//		ArrayList<File> files = new ArrayList<File>();
//		
//		
//		for (File f : w00EstFolder.listFiles()) {
//			if (!f.isDirectory()) {
//				continue;
//			}
//			for (File f3 : f.listFiles(new FilenameFilter() {
//					
//				public boolean accept(File dir, String name) {
//					return name.endsWith(".xml");
//				}
//				
//			})) {
//				files.add(f3);
//			}
//		}
//		
//		
//		fileListEst = files.toArray(new File[0]);
//		
//		RegexpCleaner markupCleaner = new RegexpCleaner(
//				"<[^>]+>", "", true);
//		
//		CleanerSet estCleaners = new CleanerSet(
//				pCleaner,
//				markupCleaner
//				);
//		
////		System.out.println(estCleaners.testFile(fileListEst[20]));
//		
//		clf.analyzeInputFile(fileListEst, nbGrams, null, estCleaners);
		
		
		
		
		/*
		 *  Wikipedia dump
		 *  
		 *  Wiki format with some bad removals that cause weird spacing around 
		 *  punctuation.
		 */
		
//		File wikiFolder = new File(
//				"/Volumes/Uranium/wikipedia.txt.dump.20140608-fr.SZTAKI/");
//		
//		File[] fileListWiki = wikiFolder.listFiles(new FilenameFilter() {
//			
//			public boolean accept(File dir, String name) {
//				return name.contains("20140608") && !name.contains("index");
//			}
//		});
//		
//		RegexpCleaner titlesCleaner = new RegexpCleaner(
//				"==+\\s*([^=]+)\\s*==+", "", true);	// probably a lot of repetition there
//		RegexpCleaner bracketsCleaner = new RegexpCleaner(
//				"\\[\\[+\\s*([^\\]]+)\\s*\\]\\]+", "$1", true);
//		RegexpCleaner bulletsCleaner = new RegexpCleaner(
//				"^\\s*\\*+\\s+", "", true);
//		RegexpCleaner commentsCleaner = new RegexpCleaner(
//				"::+\\s*", "", true);
//		
//		CleanerSet wikiCleaners = new CleanerSet(
//				titlesCleaner,
//				bracketsCleaner,
//				bulletsCleaner,
//				commentsCleaner,
//				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3,
//				punctuationCleaner4
//				);
//		
////		System.out.println(wikiCleaners.testFile(fileListWiki[2], 100));
//		
//		clf.analyzeInputFile(fileListWiki, nbGrams, null, wikiCleaners);
		
		
		
		// Frequencies for each result file
		
		File[] files = new File("results/").listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".tsv") && !name.contains("freqs")
						&& !name.contains("all");
			}
			
		});
		
		clf.frequenciesFromCounts(files);
		
		
		// Summing counts for all result files
		
		final HashMap<String, Long> count_1 = new HashMap<String, Long>();
		final HashMap<String, Long> count_2 = new HashMap<String, Long>();
		HashMap<String, Long> currentCount = null;
		
		long total_1 = 0, total_2 = 0;
		
		double frequenciesTotal_1 = 0, frequenciesTotal_2 = 0;
		
		try {
			for (File f : files) {
				
				if (f.getName().endsWith("_1.tsv")) {
					currentCount = count_1;
				} else if (f.getName().endsWith("_2.tsv")) {
					currentCount = count_2;
				} else {
					System.err.println("Something's fishy here. " + f.getName());
				}
				
				BufferedReader reader = new BufferedReader(new FileReader(f));
				
				String line;
				String[] split;
				long num;
				
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					split = line.split(TAB);
					num = Long.parseLong(split[1]);
					
					if (currentCount.containsKey(split[0])) {
						currentCount.put(split[0], currentCount.get(split[0]) 
								+ num );
					} else {
						currentCount.put(split[0], num );
					}
					
					if (currentCount == count_1) {
						total_1 += num;
					} else if (currentCount == count_2) {
						total_2 += num;
					} else {
						System.err.println("Something's fishy there. " + f.getName());
					}
					
				}
				
				reader.close();
			}
			
			System.out.println("Summing: all output files read.");
			
			
			
			BufferedWriter writer_1 = new BufferedWriter(
					new FileWriter("results/all_sums_freqs_1.tsv"));
			BufferedWriter writer_2 = new BufferedWriter(
					new FileWriter("results/all_sums_freqs_2.tsv"));
			
			ArrayList<String> keys_1 = new ArrayList<String>(count_1.keySet());
			Collections.sort(keys_1, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return count_1.get(o2).compareTo(count_1.get(o1));
				}
			});
			
			ArrayList<String> keys_2 = new ArrayList<String>(count_2.keySet());
			Collections.sort(keys_2, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return count_2.get(o2).compareTo(count_2.get(o1));
				}
			});
			
			for (String k1 : keys_1) {
				writer_1.write(k1 + "\t" + (1d*count_1.get(k1)/total_1) + "\n");
				frequenciesTotal_1 += (1d*count_1.get(k1)/total_1);
			}
			
			for (String k2 : keys_2) {
				writer_2.write(k2 + "\t" + (1d*count_2.get(k2)/total_2) + "\n");
				frequenciesTotal_2 += (1d*count_2.get(k2)/total_2);
			}
			
			writer_1.close();
			writer_2.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		System.out.println("Summing: all output files written. Totals: "
				+ frequenciesTotal_1 + ", " + frequenciesTotal_2);
		
		
		
		// Averaging frequencies for all result files
		
		frequenciesTotal_1 = 0;
		frequenciesTotal_2 = 0;
		
		File[] avgFiles = new File("results/").listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".tsv") && name.contains("freqs") 
						&& !name.contains("all");
			}
			
		});
		
		final HashMap<String, Double> avg_1 = new HashMap<String, Double>();
		final HashMap<String, Double> avg_2 = new HashMap<String, Double>();
		HashMap<String, Double> currentAvg = null;
		
		double a_1 = 0, a_2 = 0;
		
		try {
			for (File f : avgFiles) {
				
				if (f.getName().endsWith("_1_freqs.tsv")) {
					currentAvg = avg_1;
				} else if (f.getName().endsWith("_2_freqs.tsv")) {
					currentAvg = avg_2;
				} else {
					System.err.println("Something's fishy here. " + f.getName());
				}
				
				BufferedReader reader = new BufferedReader(new FileReader(f));
				
				String line;
				String[] split;
				double num;
				
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					split = line.split(TAB);
					num = Double.parseDouble(split[1]);
					
					if (currentAvg.containsKey(split[0])) {
						currentAvg.put(split[0], currentAvg.get(split[0]) 
								+ num );
					} else {
						currentAvg.put(split[0], num );
					}
					
				}
				
				if (currentAvg == avg_1) {
					a_1 ++;
				} else if (currentAvg == avg_2) {
					a_2 ++;
				} else {
					System.err.println("Something's fishy there. " + f.getName());
				}
				
				reader.close();
			}
			
			System.out.println("Averaging: all output files read.");
			
			
			
			BufferedWriter writerAvg_1 = new BufferedWriter(
					new FileWriter("results/all_Avg_freqs_1.tsv"));
			BufferedWriter writerAvg_2 = new BufferedWriter(
					new FileWriter("results/all_Avg_freqs_2.tsv"));
			
			ArrayList<String> keys_1 = new ArrayList<String>(avg_1.keySet());
			Collections.sort(keys_1, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return avg_1.get(o2).compareTo(avg_1.get(o1));
				}
			});
			
			ArrayList<String> keys_2 = new ArrayList<String>(avg_2.keySet());
			Collections.sort(keys_2, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return avg_2.get(o2).compareTo(avg_2.get(o1));
				}
			});
			
			for (String k1 : keys_1) {
				writerAvg_1.write(k1 + "\t" + (1d*avg_1.get(k1)/a_1) + "\n");
				frequenciesTotal_1 += (1d*avg_1.get(k1)/a_1);
			}
			
			for (String k2 : keys_2) {
				writerAvg_2.write(k2 + "\t" + (1d*avg_2.get(k2)/a_2) + "\n");
				frequenciesTotal_2 += (1d*avg_2.get(k2)/a_2);
			}
			
			writerAvg_1.close();
			writerAvg_2.close();
			
			System.out.println("Averaging: all output files written. Totals: "
					+ frequenciesTotal_1 + ", " + frequenciesTotal_2);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
