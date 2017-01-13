package run;

import static clean.UsefulCleaners.MarkupCleaner;
import static count.LetterCounter.NEWLINE;
import static count.LetterCounter.SEP;
import static count.LetterCounter.format;
import static clean.UsefulCleaners.htmlCharsCleaner;
import static clean.UsefulCleaners.punctuationCleaner1;
import static clean.UsefulCleaners.punctuationCleaner2;
import static clean.UsefulCleaners.punctuationCleaner3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clean.CleanerSet;
import count.LetterCounter;

public class SimpleExample {
	
	/**
	 * Identifies the separator character or pattern used in a count file.
	 */
	static Pattern CountFormatPattern = Pattern.compile("([^\\d])\\d+$");
	
	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		
		// Maximum n-gram size to consider
		int nbGrams = 2;
		
		LetterCounter clf = new LetterCounter();
		
		
		
		/*
		 * 1- Get to the folder that contains the text file(s).
		 * Alternatively that could be achieved with a JFileChooser.
		 */
		
		File folder = new File(
				"testFiles");
		
		File[] fileList = folder.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});
		
		System.out.println("Files in " + folder.getAbsolutePath());
		for (File f : fileList) {
			System.out.println("\t" + f);
		}
		
		/*
		 * 2- Declare a set of "cleaners" that will go through every line of 
		 * every file in the list and perform replacements as defined in the
		 * cleaners.
		 * Can be an empty set if all is good.
		 * 
		 * It's actually not necessary for this file, but I'm showing these as 
		 * examples.
		 */
		
		CleanerSet cleaners = new CleanerSet(
				
				// Keep only the lines that don't start with '<'or end with '>
				MarkupCleaner,
				
				// Replaces html characters like &nbsp; by their utf equivalent
				htmlCharsCleaner,
				
				// Various punctuation cleaners, see count.LetterCounter
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);

		/*
		 * 3- Go through the files, clean them using the specified cleaners, 
		 * count everything, and write the results in count files.
		 * 
		 * Note that there is an optional additional argument of 
		 * analyzeInputFile() that allows to specify an encoding, e.g.
		 * "ISO-8859-1". Not used here.
		 */
		clf.countNGrams(fileList, nbGrams, cleaners, 
				"test_big");
		
		
		System.out.println();

		
		///////////////////////
		///////////////////////
		///////////////////////
		///////////////////////

		
		// Calculate frequencies (and distances) for each count file
		
		final Set<String> excludes = new HashSet<String>();
		excludes.add("freqs");		// excluding existing frequency files
		excludes.add("all");		// excluding existing summary files
		excludes.add("distances");	// excluding distances folder
		
		File[] countFiles = 
				new File("results/").listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				
				// Excluding hidden files and files that aren't in tsv format
				boolean keep = !name.startsWith(".") && name.endsWith(".tsv");
				
				for (String s : excludes) {
					keep &= !name.contains(s);
				}

				return keep;
			}
			
		});
		
		
		
		clf.frequenciesFromCounts(countFiles);
		
		
		///////////////////////
		///////////////////////
		///////////////////////
		///////////////////////
		
		
		/* 
		 * Summing counts for all result files.
		 * countGroups were originally made for when there was groups of files
		 * (Twitter, Code etc.).
		 * 
		 * Right now it's a bit redundant but it might still be useful so I'm 
		 * leaving it here.
		 */
		
		final HashMap<String, ArrayList<File>> countGroups = 
				new HashMap<String, ArrayList<File>>();
		
//		countGroups.put("Twitter", new ArrayList<File>());
//		countGroups.put("Code", new ArrayList<File>());
//		countGroups.put("Formals", new ArrayList<File>());
		countGroups.put("test", new ArrayList<File>());
		
//		final Pattern tweetPattern = Pattern.compile("Twitter");
//		final Pattern codePattern = Pattern.compile(
//				"(?:Java)|(?:Python)|(?:CSS)|(?:CC++)|(?:HTML)");
		
		for (File f : countFiles) {
//			if (tweetPattern.matcher(f.getName()).find()) {
//				countGroups.get("Twitter").add(f);
//			} else if (codePattern.matcher(f.getName()).find()) {
//				countGroups.get("Code").add(f);
//			} else {
//				countGroups.get("Formals").add(f);
//			}
			
			countGroups.get("test").add(f);
		}
		
		// Listing files in groups for check.
		for (String gName : countGroups.keySet()) {
			System.out.println(gName + ": ");
			for (File fName : countGroups.get(gName)) {
				System.out.println("\t" + fName.getName());
			}
		}

		
		for (String gName : countGroups.keySet()) {
			
			/*
			 * Will contain the nGram counts.
			 * gramCounts.get(n) describes the number of occurrences of 
			 * (n-1)-grams. 
			 */
			final ArrayList<HashMap<String, Long>> gramCounts = 
					new ArrayList<HashMap<String,Long>>();
			
			for (int i = 0 ; i < nbGrams ; i++) {
				gramCounts.add(new HashMap<String, Long>());
			}
			
			/*
			 * Will contain the words count.
			 */
			final HashMap<String, Long> wordCounts = 
					new HashMap<String, Long>();
			
			long[] totals = new long[nbGrams];
			long totalWords = 0;
			
			double[] frequenciesTotal = new double[nbGrams];
			double frequenciesWords = 0;
			
			
			
			try {
				for (File f : countGroups.get(gName)) {
					
					boolean wordCountFile = false;
					int currentGram = 0;
					
					Matcher fileMatcher = LetterCounter.FileNamePattern.matcher(f.getName());
					if (fileMatcher.find()) {
						currentGram = Integer.parseInt(fileMatcher.group(1)) -1;
					} else {
						wordCountFile = true;
					}
					
					BufferedReader reader = new BufferedReader(
							new FileReader(f) );
					
					String line = reader.readLine();
					
					Matcher formatMatcher = CountFormatPattern.matcher(line);
					formatMatcher.find();
					String sep = formatMatcher.group(1);

					String[] split;
					long num;
					
					for ( ; line != null ; 
							line = reader.readLine()) {
						
						HashMap<String, Long> currentCount;
						if (wordCountFile) {
							currentCount = wordCounts;
						} else {
							currentCount = gramCounts.get(currentGram);
						}
						
						split = line.split(sep);
						num = Long.parseLong(split[split.length-1]);
						
						String key = "";
						for (int i = 0 ; i < split.length-2 ; i++) {
							key += split[i] + " ";
						}
						key += split[split.length-2];
						
						if (currentCount.containsKey(key)) {
							
							currentCount.put(key, 
									currentCount.get(key) + num );
							
						} else {
							currentCount.put(key, num );
						}
						
						if (wordCountFile) {
							totalWords += num;
						} else {
							totals[currentGram] += num;
						}
						
					}
					
					reader.close();
				}
				
				System.out.println("[" + gName + "] Summing: "
						+ "all " + countGroups.get(gName).size() 
						+ " output files read.");
				for (int i = 0 ; i < nbGrams ; i++) {
					System.out.println("\t" + gramCounts.get(i).keySet().size()
						+ " keys in " + (i+1) + "-grams.");
				}
				System.out.println("\t" + wordCounts.keySet().size()
						+ " keys in word counts.");
				
				BufferedWriter[] gramWriters = new BufferedWriter[nbGrams];
				for (int i = 0 ; i < nbGrams ; i++) {
					gramWriters[i] = new BufferedWriter(
							new FileWriter("results/" + gName 
									+ "_all_sums_freqs_" + (i+1) + ".tsv"));
				}
				BufferedWriter wordsWriter = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_sums_freqs_words.tsv"));
				
				ArrayList<ArrayList<String>> gramKeys = 
						new ArrayList<ArrayList<String>>();
				
				for (int i = 0 ; i < nbGrams ; i++) {
					
					gramKeys.add(
							new ArrayList<String>(gramCounts.get(i).keySet()));
					
					// Hack...
					final HashMap<String, Long> _gc = gramCounts.get(i);
					
					// Sorting keys in decreasing order of number of occurrences
					
					Collections.sort(gramKeys.get(i), new Comparator<String>() {
						public int compare(String o1, String o2) {
							return _gc.get(o2).compareTo(_gc.get(o1));
						}
					});
				}
				
				final ArrayList<String> wordsKeys = new ArrayList<String>(
						wordCounts.keySet());
				
				// Sorting keys in decreasing order of number of occurrences
				
				Collections.sort(wordsKeys, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return wordCounts.get(o2).compareTo(wordCounts.get(o1));
					}
				});
				
				
				for (int i = 0 ; i < nbGrams ; i++) {
					for (String k : gramKeys.get(i)) {
						
						gramWriters[i].write( format(k) + SEP 
								+ (1d*gramCounts.get(i).get(k) / totals[i]) 
								+ NEWLINE);
						
						frequenciesTotal[i] += 
								(1d*gramCounts.get(i).get(k)/totals[i]);
					}
					
					gramWriters[i].close();
				}
				
				for (String k : wordsKeys) {
					wordsWriter.write( k + SEP 
							+ (1d*wordCounts.get(k) / totalWords) 
							+ NEWLINE);
					frequenciesWords += (1d*wordCounts.get(k)/totalWords);
				}
				
				wordsWriter.close();
				
				
				System.out.println("[" + gName + "] Summing: all_sums files "
						+ "written.");
				
				for (int i = 0 ; i < nbGrams ; i++) {
					
					System.out.println( "\t" + (i+1) + "-grams: "
							+ frequenciesTotal[i] + " in total, "
							+ gramKeys.get(i).size() + " keys.");
					
				}
				
				System.out.println( "\twords: "
						+ frequenciesWords + " in total, "
						+ wordsKeys.size() + " keys.");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			
			// XXX Averaging frequencies for all result files
			
			totals = new long[nbGrams];
			totalWords = 0;
			
			frequenciesTotal = new double[nbGrams];
			frequenciesWords = 0;
			
			for (int i = 0 ; i < nbGrams ; i++) {
				frequenciesTotal[i] = 0;
			}
			frequenciesWords = 0;
			
			File[] freqFiles = new File[countGroups.get(gName).size()];
			for (int i = 0 ; i < countGroups.get(gName).size() ; i++) {
				freqFiles[i] = new File(countGroups.get(gName).get(i)
						.getAbsolutePath().replace(".tsv", "_freqs.tsv"));
//				System.out.println(freqFiles[i]);
			}
			
			final ArrayList<HashMap<String, Double>> gramSums = 
					new ArrayList<HashMap<String,Double>>();
			
			for (int i = 0 ; i < nbGrams ; i++) {
				gramSums.add(new HashMap<String, Double>());
			}
			
			final HashMap<String, Double> wordsSum = 
					new HashMap<String, Double>();
			
			HashMap<String, Double> currentSum = null;
			
			try {
				for (File f : freqFiles) {
					
					boolean wordCountFile = false;
					int currentGram = 0;
					
					Matcher fileMatcher = LetterCounter.FileNamePattern.matcher(f.getName());
					if (fileMatcher.find()) {
						currentGram = Integer.parseInt(fileMatcher.group(1)) -1;
					} else {
						wordCountFile = true;
					}
					
					if (wordCountFile) {
						currentSum = wordsSum;
					} else {
						currentSum = gramSums.get(currentGram);
					}
					
					BufferedReader reader = new BufferedReader(
							new FileReader(f));
					
					String line;
					String[] split;
					double num;
					String key;
					
					for (line = reader.readLine() ; line != null ; 
							line = reader.readLine()) {
						
						split = line.split(SEP);
						num = Double.parseDouble(split[split.length-1]);
						key = "";
						for (int i = 0 ; i < split.length-2 ; i++) {
							key += split[i] + " ";
						}
						key += split[split.length-2];
						
						if (currentSum.containsKey(key)) {
							
							currentSum.put(key, currentSum.get(key) + num );
							
						} else {
							
							currentSum.put(key, num );
							
						}
						
					}
					
					if (wordCountFile) {
						totalWords++;
					} else {
						totals[currentGram]++;
					}
					
					reader.close();
				}
				
				System.out.println("[" + gName + "] Averaging: "
						+ "all " + freqFiles.length + " output files read.");
				
				for (int i = 0 ; i < nbGrams ; i++) {
					System.out.println("\t" + gramSums.get(i).keySet().size()
							+ " keys in sum_" + i);
				}
				System.out.println("\t" + wordsSum.keySet().size()
						+ " keys in sum_words");
				
				
				
				BufferedWriter[] gramWriters = new BufferedWriter[nbGrams];
				for (int i = 0 ; i < nbGrams ; i++) {
					gramWriters[i] = new BufferedWriter(
							new FileWriter("results/" + gName 
									+ "_all_Avg_freqs_" + (i+1) + ".tsv"));
				}
				
				BufferedWriter wordsWriter = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_Avg_freqs_words.tsv"));
				
				ArrayList<ArrayList<String>> gramKeys = 
						new ArrayList<ArrayList<String>>();
				
				for (int i = 0 ; i < nbGrams ; i++) {
					gramKeys.add(new ArrayList<String>(
							gramSums.get(i).keySet()));
					
					// Hack...
					final HashMap<String, Double> _gs = gramSums.get(i);
					
					// Sorting keys in decreasing order of number of occurrences
					
					Collections.sort(gramKeys.get(i), new Comparator<String>() {
						public int compare(String o1, String o2) {
							return _gs.get(o2).compareTo(_gs.get(o1));
						}
					});
				}
				
				ArrayList<String> wordsKeys = new ArrayList<String>(
						wordsSum.keySet());
				Collections.sort(wordsKeys, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return wordsSum.get(o2).compareTo(wordsSum.get(o1));
					}
				});
				
				
				for (int i = 0 ; i < nbGrams ; i++) {
					for (String k : gramKeys.get(i)) {
						gramWriters[i].write( format(k) + SEP 
								+ (1d*gramSums.get(i).get(k) / totals[i])
								+ NEWLINE);
						frequenciesTotal[i] += 
								(1d*gramSums.get(i).get(k) / totals[i]);
					}
					
					gramWriters[i].close();
				}
				
				for (String k : wordsKeys) {
					wordsWriter.write( k + SEP 
							+ (1d*wordsSum.get(k) / totalWords)
							+ NEWLINE);
					frequenciesWords += 
							(1d*wordsSum.get(k) / totalWords);
				}
				wordsWriter.close();
				
				
				System.out.println("[" + gName + "] Averaging: "
						+ "all_Avg files written.");
				
				for (int i = 0 ; i < nbGrams ; i++) {
					
					System.out.println( "\t" + (i+1) + "-grams: "
							+ frequenciesTotal[i] + " in total, "
							+ gramKeys.get(i).size() + " keys.");
					
				}
				
				System.out.println( "\twords: "
						+ frequenciesWords + " in total, "
						+ wordsKeys.size() + " keys.");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		
		
	}
	
}
