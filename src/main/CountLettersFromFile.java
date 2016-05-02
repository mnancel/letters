package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CountLettersFromFile {
	
	
	
	public ArrayList<HashMap<String, Integer>> analyzeInputFile(
			File inputFile, int maxNGrams) {
		
		ArrayList<HashMap<String, Integer>> result = 
				new ArrayList<HashMap<String, Integer>>(maxNGrams);
		for (int n = 0 ; n < maxNGrams ; n++) {
			result.add(new HashMap<String, Integer>());
		}
		
		try {
			
			// Reading and counting
			
			BufferedReader reader = new BufferedReader(
					new FileReader(inputFile));
			
			String line;
			
			for (line = reader.readLine() ; line != null ; 
					line = reader.readLine()) {
				
				// TODO filters
				
				char[] ca = line.toCharArray();
				
				// for each character in the line
				for (int i = 0 ; i < ca.length ; i++) {
					
					// for each nGram size
					for (int n = 0 ; n < maxNGrams ; n++) {
						
						if (i >= n) {
							
							String nGram = line.substring(i-n);
							if (result.get(n).containsKey(nGram)) {
								result.get(n).put(
										nGram, 
										result.get(n).get(nGram)+1); 
							} else {
								result.get(n).put(nGram, 1);
							}
							
						}
						
					}
					
				}
				
			}
			
			reader.close();
			
			// Writing
			
			for (int n = 0 ; n < maxNGrams ; n++) {
				
				String outputFileName = inputFile.getAbsolutePath()
						.replaceFirst("(\\.[\\d\\w]+)$", "_output$1");
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFileName));
				
				
				
				writer.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}
