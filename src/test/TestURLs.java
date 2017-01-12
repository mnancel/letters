package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestURLs {
	
	public static void main(String[] args) {

		// Soit http://abc.com soit abc.def.com
		
		Pattern p = Pattern.compile(
				"(((\\w+://)|(\\w+\\.))(\\w+\\.)+\\p{L}\\p{L}\\p{L}?\\p{L}?[^\\s]*)");
		
		int found = 0;
		
//		String[] tests = {
//				"cool https://www.nancel.net/ta/mere.htm?toi=pute#21",
//				"cool www.google.co lol lol",
//				"mailto:lolilol@loula.net",
//				"setting://ta.race race race",
//				"loli.l",
//				"loli.lol",
//				"ftp://loli.lol",
//				"ftp://loli.l"
//		};
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/Volumes/Uranium/Twitter/TwitterClassic/AFNOR"));
			
			
//			for (String s: tests) {
			for (String s = reader.readLine(); s != null ; s = reader.readLine()) {
				Matcher m = p.matcher(s);
				if (m.find()) {
					do {
						System.out.print(m.group(1) + "    ");
						found++;
					} while (m.find());
					System.out.println();
				}
				else {
					System.out.println("Not found in " + s);
				}
			}
			
			System.out.println();
			System.out.println(found + " found");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
