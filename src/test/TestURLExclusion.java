package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clean.RegexpCleaner;

public class TestURLExclusion {
	
	public static void main(String[] args) {

		final int keep_ = 1, reset_ = 4;
		
		RegexpCleaner urlFilter = new RegexpCleaner(
				"(((\\w+://)|(\\w+\\.))(\\w+\\.)+"
				+ "\\p{L}\\p{L}\\p{L}?\\p{L}?[^\\s]*)", 
//				"[[[$1]]]", true) {
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
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/Volumes/Uranium/Twitter/TwitterClassic/AFNOR"));
			
			
//			for (String s: tests) {
			for (String s = reader.readLine(); s != null ; s = reader.readLine()) {
				System.out.println(urlFilter.test(s));
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
