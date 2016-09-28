package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnaccentuatedLetters {
	
	public static void main(String[] args) {
		
		Pattern p = Pattern.compile("[a-zA-Z]+");
		
		String[] tests = {"abcde", "abcdé", "ABcDe", "ÀbÇdÈ"};
		
		for (String t : tests) {
			
			System.out.print("For " + t + ": ");
			
			Matcher m = p.matcher(t);
			while (m.find()) {
				
				System.out.print(m.group() + ", ");
				
			}
			
			System.out.println();
			
		}
		
	}
	
}
