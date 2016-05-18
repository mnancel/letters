package clean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;

public class CleanerSet extends ArrayList<Cleaner> {
	
	private static final long serialVersionUID = 5301120338460475172L;

	public CleanerSet(boolean keep, String... data) {
		
		super();
		
		if (data.length % 2 != 0) {
			System.err.println("new CleanerSet(String...): there should be an "
					+ "even number of parameters.");
			return;
		}
		
		for (int d = 0 ; d < data.length ; d+=2) {
			add(new RegexpCleaner(data[d], data[d+1], keep));
		}
		
	}
	
	public CleanerSet(File f, boolean keep) {
		
		super();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			String line;
			String[] split;
			
			for (line = reader.readLine() ; line != null ; 
					line = reader.readLine()) {
				
				split = line.split("\t");
				if (split.length != 2) {
					System.err.println("new CleanerSet(File): there should be "
							+ "an even number of parameters in each line.");
					return;
				}
				
				add(new RegexpCleaner(split[0], split[1], keep));
				
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public CleanerSet(Cleaner... cleaners) {
		super();
		for (Cleaner c : cleaners) {
			add(c);
		}
	}
	
	public String testFile(File f, int nbLines) {
		
		System.out.println("Testing " + f.getAbsolutePath());
		
		String result = "";
		
		try {
			InputStreamReader isr = new InputStreamReader(
					new FileInputStream(f), 
//					Charset.forName("ISO-8859-1")
					Charset.defaultCharset()
					);
			BufferedReader reader = new BufferedReader(isr);
			String line;
			
			System.out.println(">>>>> " + isr.getEncoding());
			
			int lineNb = 0;
			
			for (line = reader.readLine() ; line != null && lineNb < nbLines ; 
					line = reader.readLine()) {
				
				lineNb++;
				
				String r = Normalizer.normalize(line, Normalizer.Form.NFC);
				for (Cleaner c : this) {
					r = c.test(r);
					if (r == null) {
						break;
					}
				}
				if (r != null) {
					result += r + "\n";
				}
				
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String testFile(File f) {
		return testFile(f, Integer.MAX_VALUE);
	}
	
}
