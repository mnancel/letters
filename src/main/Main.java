package main;

import static count.CountLettersFromFile.MarkupCleaner;
import static count.CountLettersFromFile.SEP;
import static count.CountLettersFromFile.NEWLINE;
import static count.CountLettersFromFile.htmlCharsCleaner;
import static count.CountLettersFromFile.pCleaner;
import static count.CountLettersFromFile.punctuationCleaner1;
import static count.CountLettersFromFile.punctuationCleaner2;
import static count.CountLettersFromFile.punctuationCleaner3;
import static count.CountLettersFromFile.punctuationCleaner4;
import static count.CountLettersFromFile.format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
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
import java.util.regex.Pattern;

import clean.CapsCleaner;
import clean.Cleaner;
import clean.CleanerSet;
import clean.MultiLineCommentCleaner;
import clean.RegexpCleaner;
import count.CountLettersFromFile;

public class Main {
	
	
	
	/**
	 *  W0057
	 *  
	 *  No change needed.
	 */
	static void doW0057(CountLettersFromFile clf, int nbGrams) {
		File w0057Folder = new File("/Volumes/Uranium/ELDA/W0057");
		File[] fileListW0057 = w0057Folder.listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".fr");
			}
		});
		
		clf.analyzeInputFile(fileListW0057, nbGrams, null, null, 
				w0057Folder.getName());
	}
	
	
	
	/**
	 *  W0058
	 *  
	 *  No change needed.
	 */
	static void doW0058(CountLettersFromFile clf, int nbGrams) {
		File w0058Folder = new File("/Volumes/Uranium/ELDA/W0058");
		File[] fileListW0058 = w0058Folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".fr");
			}
		});
		
		clf.analyzeInputFile(fileListW0058, nbGrams, null, null,
				w0058Folder.getName());
	}
	
	
	
	/**
	 *  W0065
	 *  
	 *  XML format with some spacing issues with the punctuation.
	 *  Need to keep only the <p> content.
	 */
	static void doW0065(CountLettersFromFile clf, int nbGrams) {
		File w0065Folder = new File("/Volumes/Uranium/ELDA/W0065/ENV_FR");
		File[] fileListW0065 = w0065Folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		
		CleanerSet w0065Cleaners = new CleanerSet(
				pCleaner, 
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);
		
	//	System.out.println(w0065Cleaners.testFile(fileListW0065[20]));
		
		clf.analyzeInputFile(fileListW0065, nbGrams, null, w0065Cleaners,
				"W0065");
	}
	
	
	
	/**
	 *  W0066
	 *  
	 *  Same as W0065
	 */
	static void doW0066(CountLettersFromFile clf, int nbGrams) {
		File w0066Folder = new File("/Volumes/Uranium/ELDA/W0066/LAB_FR");
		File[] fileListW0066 = w0066Folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		
		CleanerSet w0066Cleaners = new CleanerSet(
				pCleaner, 
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);
		
	//	System.out.println(w0066Cleaners.testFile(fileListW0066[1]));
		
		clf.analyzeInputFile(fileListW0066, nbGrams, null, w0066Cleaners,
				"W0066");
	}
	
	
	
	/**
	 *  W0017
	 *  
	 *  XML format with some HTML codes and an "ISO-8859-1" encoding
	 *  Need to keep only the lines with no xml markups, apparently.
	 */
	static void doW0017(CountLettersFromFile clf, int nbGrams) {
		File w0017Folder = new File(
				"/Volumes/Uranium/ELDA/W0017/Data/FR/Sentences");
		File[] fileListW0017 = w0017Folder.listFiles();

		CleanerSet w0017Cleaners = new CleanerSet(
				MarkupCleaner, 
				htmlCharsCleaner,
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3);

		//	System.out.println(w0017Cleaners.testFile(fileListW0017[1],
		//		"ISO-8859-1"));

		clf.analyzeInputFile(fileListW0017, nbGrams, null, w0017Cleaners, 
				"W0017", "ISO-8859-1");
}
	
	
	
	/**
	 *  W0036
	 *  
	 *  HTML format with some HTML codes.
	 *  Article bodies seem contained within "<p>".
	 *  Trusting them for the spaces around punctuation.
	 */
	static void doW0036(CountLettersFromFile clf, int nbGrams) {
		File w0036Folder = new File(
				"/Volumes/Uranium/ELDA/W0036-01");
		File[] fileListW0036;
		ArrayList<File> files = new ArrayList<File>();
		
		
		for (File f : w0036Folder.listFiles()) {
			if (!f.isDirectory()) {
				continue;
			}
			for (File f2 : f.listFiles()) {
				if (!f2.isDirectory()) {
					continue;
				}
				for (File f3 : f2.listFiles(new FilenameFilter() {
					
					public boolean accept(File dir, String name) {
						return name.endsWith(".html");
					}
					
				})) {
					files.add(f3);
				}
			}
		}
		
		
		fileListW0036 = files.toArray(new File[0]);
		
		RegexpCleaner markupCleaner = new RegexpCleaner(
				"<[^>]+>", "", true);
		
		CleanerSet w0036Cleaners = new CleanerSet(
				pCleaner,
				markupCleaner,
				htmlCharsCleaner
				);
		
	//	System.out.println(w0036Cleaners.testFile(fileListW0036[20]));
		
		clf.analyzeInputFile(fileListW0036, nbGrams, null, w0036Cleaners,
				w0036Folder.getName());
	}
	
	
	
	/**
	 *  W0015
	 *  
	 *  HTML format with some HTML codes and an "ISO-8859-1" encoding.
	 *  Article bodies seem contained within "<p>".
	 *  Trusting them for the spaces around punctuation.
	 */
	static void doW0015(CountLettersFromFile clf, int nbGrams) {
		File w0015Folder = new File(
				"/Volumes/Uranium/ELDA/W0015");
		File[] fileListW0015;
		ArrayList<File> files = new ArrayList<File>();
		
		
		for (File f : w0015Folder.listFiles()) {
			if (!f.isDirectory()) {
				continue;
			}
			for (File f2 : f.listFiles()) {
				if (!f2.isDirectory()) {
					continue;
				}
				for (File f3 : f2.listFiles(new FilenameFilter() {
					
					public boolean accept(File dir, String name) {
						return name.endsWith(".xml");
					}
					
				})) {
					files.add(f3);
				}
			}
		}
		
		
		fileListW0015 = files.toArray(new File[0]);
		
		RegexpCleaner markupCleaner = new RegexpCleaner(
				"<[^>]+>", "", true);
		
		CleanerSet w0015Cleaners = new CleanerSet(
				pCleaner,
				markupCleaner,
				htmlCharsCleaner
				);
		
	//	System.out.println(w0015Cleaners.testFile(fileListW0015[20],
	//			"ISO-8859-1"));
		
		clf.analyzeInputFile(fileListW0015, nbGrams, null, w0015Cleaners, 
				w0015Folder.getName(), "ISO-8859-1");
	}
	
	
	
	
	/**
	 *  Est Républicain
	 *  
	 *  HTML format, no HTML codes.
	 *  Article bodies seem contained within "<p>".
	 *  Trusting them for the spaces around punctuation.
	 */
	static void doEstRep(CountLettersFromFile clf, int nbGrams) {
		File w00EstFolder = new File(
				"/Volumes/Uranium/Est Républicain");
		File[] fileListEst;
		ArrayList<File> files = new ArrayList<File>();
		
		
		for (File f : w00EstFolder.listFiles()) {
			if (!f.isDirectory()) {
				continue;
			}
			for (File f3 : f.listFiles(new FilenameFilter() {
					
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
				
			})) {
				files.add(f3);
			}
		}
		
		
		fileListEst = files.toArray(new File[0]);
		
		RegexpCleaner markupCleaner = new RegexpCleaner(
				"<[^>]+>", "", true);
		
		CleanerSet estCleaners = new CleanerSet(
				pCleaner,
				markupCleaner
				);
		
//		System.out.println(estCleaners.testFile(fileListEst[20]));
		
		clf.analyzeInputFile(fileListEst, nbGrams, null, estCleaners,
				w00EstFolder.getName());
	}
	
	
	
	
	/**
	 *  Wikipedia dump
	 *  
	 *  Wiki format with some bad removals that cause weird spacing around 
	 *  punctuation.
	 */
	static void doWikipedia(CountLettersFromFile clf, int nbGrams) {
		File wikiFolder = new File(
				"/Volumes/Uranium/wikipedia.txt.dump.20140608-fr.SZTAKI/");
		
		File[] fileListWiki = wikiFolder.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.contains("20140608") && !name.contains("index");
			}
		});
		
		// probably a lot of repetition there
		RegexpCleaner titlesCleaner = new RegexpCleaner(
				"==+\\s*([^=]+)\\s*==+", "", true);
		RegexpCleaner bracketsCleaner = new RegexpCleaner(
				"\\[\\[+\\s*([^\\]]+)\\s*\\]\\]+", "$1", true);
		RegexpCleaner bulletsCleaner = new RegexpCleaner(
				"^\\s*\\*+\\s+", "", true);
		RegexpCleaner commentsCleaner = new RegexpCleaner(
				"::+\\s*", "", true);
		
		CleanerSet wikiCleaners = new CleanerSet(
				titlesCleaner,
				bracketsCleaner,
				bulletsCleaner,
				commentsCleaner,
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3,
				punctuationCleaner4
				);
		
	//	System.out.println(wikiCleaners.testFile(fileListWiki[2], 100));
		
		clf.analyzeInputFile(fileListWiki, nbGrams, null, wikiCleaners, 
				"Wiki");
	}
	
	
	
	/**
	 *  Twitter
	 *  
	 *  One line per tweet.
	 *  Excluding RT prefixes.
	 */
	static void doTwitter(CountLettersFromFile clf, int nbGrams) {
		File twitterFolders[] = { 
				new File("/Volumes/Uranium/Twitter/TwitterPop"),
				new File("/Volumes/Uranium/Twitter/TwitterClassic") };
		
		for (int i = 0 ; i < twitterFolders.length ; i++) {
		
			File[] fileListTwitter;
			ArrayList<File> tFiles = new ArrayList<File>();
			
			for (File f : twitterFolders[i].listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return !name.startsWith(".");
				}
			})) {
				tFiles.add(f);
			}
			
			fileListTwitter = tFiles.toArray(new File[0]);
			
			RegexpCleaner rtCleaner = new RegexpCleaner(
					"^RT [^:]+: ", "", true);
			
			CleanerSet twitterCleaners = new CleanerSet(
					rtCleaner
			);
			
	//		System.out.println(twitterCleaners.testFile(fileListTwitter[2]));
			
			clf.analyzeInputFile(
					fileListTwitter, nbGrams, null, twitterCleaners,
					twitterFolders[i].getName());
			
		}
	}
	
	/**
	 *  Corpus Easy (emails)
	 *  
	 *  Excluding "doc" markups.
	 */
	static void doEasyEMails(CountLettersFromFile clf, int nbGrams) {
		File easyFolder = new File("/Volumes/Uranium/ELDA 2/E0034_Easy");
		
		File[] fileListEasy = easyFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});
		
		RegexpCleaner docCleaner = new RegexpCleaner(
				"^</?DOC>$", "", true);
		
		CleanerSet easyCleaners = new CleanerSet(
				docCleaner
		);
		
//		System.out.println(easyCleaners.testFile(fileListEasy[2]));
		
		clf.analyzeInputFile(
				fileListEasy, nbGrams, null, easyCleaners, 
				easyFolder.getName());
			
	}
	
	/**
	 *  Corpus Ester (speech retranscriptions)
	 *  
	 *  Excluding "doc" markups.
	 */
	static void doEster(CountLettersFromFile clf, int nbGrams) {
		File esterFolder[] = {
				new File("/Volumes/Uranium/ELDA 2/S0241_Ester"),
				new File("/Volumes/Uranium/ELDA 2/S0338_Ester 2")
		};

		
		ArrayList<File> intermediateStruct = new ArrayList<File>();
		
		for (File d : esterFolder) {
			for (File f2 : d.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			})) {
				for (File f3 : f2.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".trs");
					}
				})) {
					intermediateStruct.add(f3);
				}
			}
		}
		
		File[] fileListEster = new File[0];
		fileListEster = intermediateStruct.toArray(fileListEster);
		
		Cleaner startingSpaceCleaner = new RegexpCleaner(
				"^ ", "", true);
		
		Cleaner capsCleaner = new CapsCleaner();
		
		CleanerSet esterCleaners = new CleanerSet(
				MarkupCleaner,
				punctuationCleaner1, punctuationCleaner2, punctuationCleaner3,
				punctuationCleaner4,
				startingSpaceCleaner,
				capsCleaner
		);
		
//		System.out.println(esterCleaners.testFile(fileListEster[2], 
//				"ISO-8859-1"));
		
		clf.analyzeInputFile(
				fileListEster, nbGrams, null, esterCleaners, 
				"Ester", "ISO-8859-1");
			
	}
	
	/**
	 *  Corpus CODE CRAWL (programming language projects)
	 *  
	 *  Excluding comments.
	 */
	static void doCode(CountLettersFromFile clf, int nbGrams) {
		
		// "// Comment"
		Cleaner cJavaJsCssCleaner1 = new RegexpCleaner(
				"//.*$", "", true);
		
		// "/* Comment */"
		Cleaner cJavaJsCssCleaner2 = new RegexpCleaner(
				"/\\*.*\\*/", "", true);
		
		// "/* Comment on multiple lines */"
		Cleaner cJavaJsCssCleaner3 = new MultiLineCommentCleaner(
				"/\\*", "\\*/");
		
		// "# Comment"
		Cleaner pythonCleaner1 = new RegexpCleaner(
				"#.*$", "", true);
		
		// " """ Comment on multiple lines """ "
		Cleaner pythonCleaner2 = new MultiLineCommentCleaner(
				"\"\"\"", "\"\"\"");
		
		
		File codeMainFolder = 
				new File("/Volumes/Uranium/CODECRAWL");
		
		File[] languageFolders = codeMainFolder.listFiles(
				new FileFilter() {
			
			public boolean accept(File pathname) {
				return pathname.isDirectory() 
						&& !pathname.getName().startsWith(".");
			}
		});
		
		for (File dir : languageFolders) {

			System.out.println("Language " + dir.getName());
			
			ArrayList<File> intermediateStruct = new ArrayList<File>();
			
			File[] projectFolders = dir.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.isDirectory() 
							&& !pathname.getName()
							.startsWith(".");
				}
			});
			
			for (File dir2 : projectFolders) {

				File[] codeFiles = dir2.listFiles(
						new FileFilter() {

					public boolean accept(File pathname) {
						return !pathname.getName().startsWith(".");
					}
				});
				
				for (File f : codeFiles) {
					intermediateStruct.add(f);
				}

			}
			
			File[] allLanguageFiles = intermediateStruct.toArray(new File[]{});
			
			CleanerSet cleaners = new CleanerSet();
			
			if (dir.getName().contains("Python")) {
				cleaners.add(pythonCleaner1);
				cleaners.add(pythonCleaner2);
			}
			else if (dir.getName().contains("HTML")) {
				// TODO
			} else {
				
				cleaners.add(cJavaJsCssCleaner2);
				cleaners.add(cJavaJsCssCleaner3);
				cleaners.add(cJavaJsCssCleaner1);
				
			}
			
//			System.out.println(cleaners.testFile(allLanguageFiles[2]));
			
			clf.analyzeInputFile(
					allLanguageFiles, nbGrams, null, cleaners, dir.getName());

		}
		
	}
	
	
	
	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		
		int nbGrams = 2;
		CountLettersFromFile clf = new CountLettersFromFile();
		
//		doW0057(clf, nbGrams);
//		doW0058(clf, nbGrams);
//		doW0065(clf, nbGrams);
//		doW0066(clf, nbGrams);
//		doW0017(clf, nbGrams);
//		doW0036(clf, nbGrams);
//		doW0015(clf, nbGrams);
//		doEstRep(clf, nbGrams);
//		doWikipedia(clf, nbGrams);
//		doTwitter(clf, nbGrams);
//		doEasyEMails(clf, nbGrams);
//		doEster(clf, nbGrams);
//		doCode(clf, nbGrams);
		
		System.out.println();

		///////////////////////
		///////////////////////
		///////////////////////
		///////////////////////
		
//		if (true) {
//			return;
//		}
		
		// Frequencies (and distances) for each result file
		
		final Set<String> excludes = new HashSet<String>();
		excludes.add("freqs");		// excluding existing freq files
		excludes.add("all");		// excluding existing summary files
		excludes.add("distances");	// excluding distances folder
		excludes.add("Est Républicain");
		
		File[] countFiles = new File("results/").listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				boolean keep = !name.startsWith(".") && name.endsWith(".tsv");
				for (String s : excludes) {
					keep &= !name.contains(s);
				}
//				System.out.println("keep = " + keep);
				return keep;
			}
			
		});
		
		clf.frequenciesFromCounts(countFiles);
		
		
		///////////////////////
		///////////////////////
		///////////////////////
		///////////////////////
		
		
		
//		if (true) {
//			return;
//		}
		
		
		// Summing counts for all result files
		// Now by group
		
		
		final HashMap<String, ArrayList<File>> countGroups = 
				new HashMap<String, ArrayList<File>>();
		
		countGroups.put("Twitter", new ArrayList<File>());
		countGroups.put("Code", new ArrayList<File>());
		countGroups.put("Formals", new ArrayList<File>());
		
		final Pattern tweetPattern = Pattern.compile("Twitter");
		final Pattern codePattern = Pattern.compile(
				"(?:Java)|(?:Python)|(?:CSS)|(?:CC++)|(?:HTML)");
		
		for (File f : countFiles) {
			if (tweetPattern.matcher(f.getName()).find()) {
				countGroups.get("Twitter").add(f);
			} else if (codePattern.matcher(f.getName()).find()) {
				countGroups.get("Code").add(f);
			} else {
				countGroups.get("Formals").add(f);
			}
		}
		
		for (String gName : countGroups.keySet()) {
			System.out.println(gName + ": ");
			for (File fName : countGroups.get(gName)) {
				System.out.println("\t" + fName.getName());
			}
		}

		for (String gName : countGroups.keySet()) {
			
			final HashMap<String, Long> count_1 = new HashMap<String, Long>();
			final HashMap<String, Long> count_2 = new HashMap<String, Long>();
			HashMap<String, Long> currentCount = null;
			
			long total_1 = 0, total_2 = 0;
			
			double frequenciesTotal_1 = 0, frequenciesTotal_2 = 0;
			
			try {
				for (File f : countGroups.get(gName)) {
					
					if (f.getName().endsWith("_1.tsv")) {
						currentCount = count_1;
					} else if (f.getName().endsWith("_2.tsv")) {
						currentCount = count_2;
					} else {
						System.err.println("Something's fishy here. " 
								+ f.getName());
					}
					
					BufferedReader reader = new BufferedReader(
							new FileReader(f) );
					
					String line;
					String[] split;
					long num;
					
					for (line = reader.readLine() ; line != null ; 
							line = reader.readLine()) {
						
						split = line.split("\t");
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
							System.err.println("Something's fishy there. " 
									+ f.getName());
						}
						
					}
					
					reader.close();
				}
				
				System.out.println("[" + gName + "] Summing: "
						+ "all " + countGroups.get(gName).size() 
						+ " output files read.");
				System.out.println("\t" + count_1.keySet().size()
						+ " keys in count_1");
				System.out.println("\t" + count_2.keySet().size()
						+ " keys in count_2");
				
				
				BufferedWriter writer_1 = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_sums_freqs_1.tsv"));
				BufferedWriter writer_2 = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_sums_freqs_2.tsv"));
				
				ArrayList<String> keys_1 = new ArrayList<String>(
						count_1.keySet());
				Collections.sort(keys_1, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return count_1.get(o2).compareTo(count_1.get(o1));
					}
				});
				
				ArrayList<String> keys_2 = new ArrayList<String>(
						count_2.keySet());
				Collections.sort(keys_2, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return count_2.get(o2).compareTo(count_2.get(o1));
					}
				});
				
				for (String k1 : keys_1) {
					writer_1.write( format(k1) + SEP + (1d*count_1.get(k1)
							/total_1) 
							+ NEWLINE);
					frequenciesTotal_1 += (1d*count_1.get(k1)/total_1);
				}
				
				for (String k2 : keys_2) {
					writer_2.write( format(k2) + SEP + (1d*count_2.get(k2)
							/total_2) 
							+ NEWLINE);
					frequenciesTotal_2 += (1d*count_2.get(k2)/total_2);
				}
				
				writer_1.close();
				writer_2.close();
				
				System.out.println("[" + gName + "] Summing: all_sums files "
						+ "written. Totals: "
						+ frequenciesTotal_1 + ", " + frequenciesTotal_2);
				System.out.println("\t" + keys_1.size()
						+ " keys in keys_1");
				System.out.println("\t" + keys_2.size()
						+ " keys in keys_2");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			
			// Averaging frequencies for all result files
			
			frequenciesTotal_1 = 0;
			frequenciesTotal_2 = 0;
			
			File[] freqFiles = new File[countGroups.get(gName).size()];
			for (int i = 0 ; i < countGroups.get(gName).size() ; i++) {
				freqFiles[i] = new File(countGroups.get(gName).get(i)
						.getAbsolutePath().replace(".tsv", "_freqs.tsv"));
//				System.out.println(freqFiles[i]);
			}
			
			final HashMap<String, Double> sum_1 = new HashMap<String, Double>();
			final HashMap<String, Double> sum_2 = new HashMap<String, Double>();
			HashMap<String, Double> currentSum = null;
			
			double a_1 = 0, a_2 = 0;
			
			try {
				for (File f : freqFiles) {
					
					if (f.getName().endsWith("_1_freqs.tsv")) {
						currentSum = sum_1;
					} else if (f.getName().endsWith("_2_freqs.tsv")) {
						currentSum = sum_2;
					} else {
						System.err.println("Something's fishy here. " 
								+ f.getName());
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
							
							currentSum.put(key, currentSum.get(key) 
									+ num );
							
						} else {
							
							currentSum.put(key, num );
							
						}
						
					}
					
					if (currentSum == sum_1) {
						a_1 ++;
					} else if (currentSum == sum_2) {
						a_2 ++;
					} else {
						System.err.println("Something's fishy there. " 
								+ f.getName());
					}
					
					reader.close();
				}
				
				System.out.println("[" + gName + "] Averaging: "
						+ "all " + freqFiles.length + " output files read.");
				System.out.println("\t" + sum_1.keySet().size()
						+ " keys in sum_1");
				System.out.println("\t" + sum_2.keySet().size()
						+ " keys in sum_2");
				
				
				
				BufferedWriter writerAvg_1 = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_Avg_freqs_1.tsv"));
				BufferedWriter writerAvg_2 = new BufferedWriter(
						new FileWriter("results/" + gName 
								+ "_all_Avg_freqs_2.tsv"));
				
				ArrayList<String> keys_1 = new ArrayList<String>(
						sum_1.keySet());
				Collections.sort(keys_1, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return sum_1.get(o2).compareTo(sum_1.get(o1));
					}
				});
				
				ArrayList<String> keys_2 = new ArrayList<String>(
						sum_2.keySet());
				Collections.sort(keys_2, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return sum_2.get(o2).compareTo(sum_2.get(o1));
					}
				});
				
				for (String k1 : keys_1) {
					writerAvg_1.write( format(k1) + SEP + (1d*sum_1.get(k1)/a_1) 
							+ NEWLINE);
					frequenciesTotal_1 += (1d*sum_1.get(k1)/a_1);
				}
				
				for (String k2 : keys_2) {
					writerAvg_2.write( format(k2) + SEP + (1d*sum_2.get(k2)/a_2) 
							+ NEWLINE);
					frequenciesTotal_2 += (1d*sum_2.get(k2)/a_2);
				}
				
				writerAvg_1.close();
				writerAvg_2.close();
				
				System.out.println("[" + gName + "] Averaging: "
						+ "all_Avg files written. Totals: "
						+ frequenciesTotal_1 + ", " + frequenciesTotal_2);
				System.out.println("\t" + keys_1.size()
						+ " keys in count_1");
				System.out.println("\t" + keys_2.size()
						+ " keys in count_2");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		
		
	}
	
}
