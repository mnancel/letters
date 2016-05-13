package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CountLettersFromFile {
	
	public static final String TAB = "\t";
	public static final String ENTER = "\n";
	
	@SuppressWarnings("boxing")
	ArrayList<HashMap<String, Integer>> analyzeInputFileAndUpdate(
			File inputFile, int maxNGrams, File[] outputFiles) {
		
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
		
		return analyzeInputFile(inputFile, maxNGrams, count);
		
	}
	
	@SuppressWarnings("boxing")
	public ArrayList<HashMap<String, Integer>> analyzeInputFile(
			File inputFile, int maxNGrams, 
			ArrayList<HashMap<String, Integer>> _count) {
		
		ArrayList<HashMap<String, Integer>> count;
		
		if (_count == null) {
			count = new ArrayList<HashMap<String,Integer>>();
			for (int n = 0 ; n < maxNGrams ; n++) {
				count.add(new HashMap<String, Integer>());
			}
		} else {
			count = _count;
		}
		
		try {
			
			double size = inputFile.length();
			
			// Reading and counting
			
			BufferedReader reader = new BufferedReader(
					new FileReader(inputFile));
			
			char[] buffer = new char[1000];
			int nbCharsRead;
			
			long startTime = System.currentTimeMillis();
			
			for (nbCharsRead = reader.read(buffer) ; nbCharsRead != -1 ; 
					nbCharsRead = reader.read(buffer)) {
				
				// TODO filters
				
				// for each character in the line
				for (int i = 0 ; i < nbCharsRead ; i++) {
					
					// for each nGram size
					for (int n = 0 ; n < maxNGrams ; n++) {
						
						if (i >= n) {
							
							String nGram = new String(buffer, i-n, n+1);
							
							// Excluding tsv-involved characters
							if (nGram.contains(TAB) || nGram.contains(ENTER)) {
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
			
			double duration = 1d*(System.currentTimeMillis() - startTime)/1000d;
			
			double rate = Math.round(100*duration / (size/1e6)) / 100d;
			
			System.out.println(rate + " seconds per Mo");
			
			reader.close();
			
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
				
				String outputFileName = inputFile.getAbsolutePath()
						.replaceFirst("(\\.[\\d\\w]+)$", "_output_" + (n+1) 
//								+ "$1");
								+ ".tsv");
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (Entry<String, Integer> e : r) {
					writer.write(e.getKey() + "\t" + e.getValue() + "\n");
				}
				
				writer.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done counting characters in " 
				+ inputFile.getName());
		return count;
		
	}
	
	@SuppressWarnings("boxing")
	public void probabilitiesFromCounts(File[] countFiles) {
		
		for (File f : countFiles) {
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				
				int sum = 0;
				
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<Integer> counts = new ArrayList<Integer>();
				
				String line;
				
				for (line = reader.readLine() ; line != null ; 
						line = reader.readLine()) {
					
					String[] split = line.split("\t");
					
					keys.add(split[0]);
					int count = Integer.parseInt(split[1]);
					counts.add(count);
					sum += count;
					
				}
				
				reader.close();
				
				String outputFileName = f.getAbsolutePath()
						.replaceFirst("(\\.[\\d\\w]+)$", 
								"_stats.tsv");
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				for (int i = 0 ; i < keys.size() ; i++) {
					writer.write(keys.get(i) + "\t" 
							+ (1d * counts.get(i) / sum)
							+ "\n");
				}
				
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println("All done.");
		
	}
	
	public static void main(String[] args) {
		
		int nbGrams = 3;
		CountLettersFromFile clf = new CountLettersFromFile();
		clf.analyzeInputFile(new File("TestFiles/test.txt"), nbGrams, null);
		
		File fs[] = new File[nbGrams];
		for (int n = 0 ; n < nbGrams ; n++) {
				fs[n] = new File("TestFiles/test_output_" + (n+1) + ".tsv");
		}
		clf.probabilitiesFromCounts(fs);
		
	}
	
}
