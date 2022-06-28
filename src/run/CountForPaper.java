package run;

import static count.CountLettersFromFile.MarkupCleaner;
import static count.CountLettersFromFile.htmlCharsCleaner;
import static count.CountLettersFromFile.pCleaner;
import static count.CountLettersFromFile.punctuationCleaner1;
import static count.CountLettersFromFile.punctuationCleaner2;
import static count.CountLettersFromFile.punctuationCleaner3;
import static count.CountLettersFromFile.punctuationCleaner4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clean.CapsCleaner;
import clean.Cleaner;
import clean.CleanerSet;
import clean.MultiLineCommentCleaner;
import clean.RegexpCleaner;
import count.CountLettersFromFile;

public class CountForPaper {
	
	static final String ROOT = "/Volumes/Porsche Trunk/French Revolution/"; 
	
	/**
	 *  W0057
	 *  
	 *  No change needed.
	 */
	static void doW0057(CountLettersFromFile clf, int nbGrams) {
		File w0057Folder = new File(ROOT + "ELDA/W0057");
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
		File w0058Folder = new File(ROOT + "ELDA/W0058");
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
		File w0065Folder = new File(ROOT + "ELDA/W0065/ENV_FR");
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
		File w0066Folder = new File(ROOT + "ELDA/W0066/LAB_FR");
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
				ROOT + "ELDA/W0017/Data/FR/Sentences");
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
				ROOT + "ELDA/W0036-01");
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
				ROOT + "ELDA/W0015");
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
				ROOT + "Est Républicain");
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
				ROOT + "wikipedia.txt.dump.20140608-fr.SZTAKI/");
		
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
	 *  NEW: excluding 3/4 of URLs
	 */
	static void doTwitter(CountLettersFromFile clf, int nbGrams, 
			final int keep_, final int reset_) {
		
		File twitterFolders[] = { 
				new File(ROOT + "Twitter/TwitterPop"),
				new File(ROOT + "Twitter/TwitterClassic") };
		
		RegexpCleaner rtCleaner = new RegexpCleaner(
				"^RT [^:]+: ", "", true);
		
		RegexpCleaner urlFilter = new RegexpCleaner(
				"(((\\w+://)|(\\w+\\.))(\\w+\\.)+"
				+ "\\p{L}\\p{L}\\p{L}?\\p{L}?[^\\s]*)", 
				"", true) {
			
			int nbTests = -1;
			
			@Override
			public String test(String s) {
				
				nbTests = (nbTests+1) % reset_;
				
				if (nbTests < keep_) {
					return s;
				} 
				
				return super.test(s);
				
			}
			
		};
		
		rtCleaner = new RegexpCleaner(
				"^RT [^:]+: ", "", true);
		
		urlFilter = new RegexpCleaner(
				"(((\\w+://)|(\\w+\\.))(\\w+\\.)+"
				+ "\\p{L}\\p{L}\\p{L}?\\p{L}?[^\\s]*)", 
				"", true) {
			
			int nbTests = -1;
			
			@Override
			public String test(String s) {
				
				nbTests = (nbTests+1) % reset_;
				
				if (nbTests < keep_) {
					return s;
				} 
				
				return super.test(s);
				
			}
			
		};
		
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
			
			CleanerSet twitterCleaners = new CleanerSet(
					rtCleaner,
					urlFilter
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
		File easyFolder = new File(ROOT + "ELDA 2/E0034_Easy");
		
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
				new File(ROOT + "ELDA 2/S0241_Ester"),
				new File(ROOT + "ELDA 2/S0338_Ester 2")
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
				new File(ROOT + "CODECRAWL");
		
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
	
	/**
	 *  HTML examples
	 *  
	 *  HTML format with some HTML codes.
	 */
	static void doHtml(CountLettersFromFile clf, int nbGrams) {
		File htmlFolder = new File(
				ROOT + "websites");
		File[] fileListHtml = htmlFolder.listFiles();

		CleanerSet htmlCleaners = new CleanerSet(htmlCharsCleaner);

//			System.out.println(htmlCleaners.testFile(fileListHtml[1]));

		clf.analyzeInputFile(fileListHtml, nbGrams, null, htmlCleaners, "HTML");
	}
	
	/**
	 *  Facebook examples
	 *  
	 *  Simple text format.
	 */
	static void doFacebook(CountLettersFromFile clf, int nbGrams) {
		File facebookFolder = new File(
				ROOT + "facebook_statuses");
		File[] fileListFacebook = facebookFolder.listFiles();

		CleanerSet facebookCleaners = new CleanerSet();

//			System.out.println(htmlCleaners.testFile(fileListHtml[1]));

		clf.analyzeInputFile(fileListFacebook, nbGrams, null, facebookCleaners, 
				"Facebook");
	}
	
	
	
	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		
		File resultsFile = new File("results/");
		
		if (!resultsFile.exists() | !resultsFile.isDirectory()) {
			System.err.println("Error: Container folder doesn't exist. Abort.");
			System.exit(-1);
		}
		
		ArrayList<String[]> groups = new ArrayList<String[]>();
		groups.add(new String[]{"Wiki", "Wikipedia"});
		groups.add(new String[]{"Twitter", "Twitter"});
		groups.add(new String[]{"Facebook", "Facebook"});
		groups.add(new String[]{"Python", "Code"});
		groups.add(new String[]{"Javascript", "Code"});
		groups.add(new String[]{"C++", "Code"});
		groups.add(new String[]{"Java", "Code"});
		groups.add(new String[]{"CSS", "Code"});
		groups.add(new String[]{"HTML", "Code"});
		groups.add(new String[]{"W0057", "Environment"});
		groups.add(new String[]{"W0065", "Environment"});
		groups.add(new String[]{"W0058", "Work"});
		groups.add(new String[]{"W0066", "Work"});
		groups.add(new String[]{"W0017", "EUOJ"});
		groups.add(new String[]{"W0036", "News"});
		groups.add(new String[]{"W0015", "News"});
		groups.add(new String[]{"Est Républicain", "News"});
		groups.add(new String[]{"Ester", "Radio"});
		groups.add(new String[]{"E0034", "Emails"});
		
		
		FileFilter ff = new FileFilter() {
			String pattern = ".*_output_\\d\\.tsv";
			public boolean accept(File pathname) {
				return !pathname.isDirectory() 
						&& Pattern.matches(pattern, pathname.getName());
			}
		};

		Pattern p = Pattern.compile("^.*[^\\d](\\d+)$");
		
		boolean found;
		HashMap<String, ArrayList<Long>> result = 
				new HashMap<String, ArrayList<Long>>(); 
		
		for (File f : resultsFile.listFiles(ff)) {
			
			found = false;
			
			// Ugly, sure, but I need constant order
			for ( String[] g : groups ) {
				
				if (f.getName().contains(g[0])) {
					found = true;
					
					// First occurrence of group: create Arraylist & initialize
					if ( !result.containsKey(g[1]) ) {
						result.put(g[1], new ArrayList<Long>());
						result.get(g[1]).add((long) 0);
						result.get(g[1]).add((long) 0);
					}
					
					// Identify current ngram
					int ngram = -1;
					if (f.getName().endsWith("1.tsv")) {
						ngram = 0;
					} else if (f.getName().endsWith("2.tsv")) {
						ngram = 1;
					} else {
						System.err.println("Couldn't associate file "
								+ f.getName() + " to nGram.");
						System.exit(-2);
					}
					
					
					// Sum non-space characters up
					try {
						BufferedReader reader = new BufferedReader(
								new FileReader(f));

						String line;
						for (line = reader.readLine() ; line != null ; 
								line = reader.readLine()) {
							
							Matcher m = p.matcher(line);
							
							if (m.matches()) {
								result.get(g[1]).set(ngram, 
										result.get(g[1]).get(ngram)
										+ Integer.parseInt(m.group(1)));
							}
							
						}
						
						reader.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					break;
				}
				
			}
			
			if (!found) {
				System.err.println("Couldn't find a match for "
						+ f.getName());
			}
			
		}
		
		for (String group : result.keySet()) {
			System.out.println(group);
			for (long ngram : result.get(group)) {
				System.out.println("\t" + ngram/1e6);
			}
		}
		
	}
	
}
