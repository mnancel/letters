package clean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import count.CountLettersFromFile;

/**
 * Replaces all web characters (e.g. '&nbsp;') by their UTF8 equivalent.
 * 
 * @author Mathieu Nancel
 *
 */
public class HtmlCharsCleaner extends Cleaner {

	HashMap<String, String> replacements;
	
	public HtmlCharsCleaner() {
		
		replacements = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"cleanerFiles/HtmlCharacters.tsv"));
			
			String line;
			String[] split;
			
			for (line = reader.readLine() ; line != null ;
					line = reader.readLine()) {
				
				split = line.split("\t");
				
				if (split.length != 2) {
					System.err.println("Error reading HTML chars file: "
							+ "wrong number of separators "
							+ "(" + split.length + ").");
				}
				if (split[1].length() != 1) {
					System.err.println("Error reading HTML chars file: "
							+ "wrong size of value ( " + line + " ).");
				}
				
				replacements.put(split[0], split[1]);
				
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String test(String s) {
		
		if (s != null && s.length() > 0) {
		
			String result = s;
			
			for (String key : replacements.keySet()) {
				result = result.replaceAll(key, replacements.get(key));
			}
			
			return result;
		}
		
		return null;
	}

}
