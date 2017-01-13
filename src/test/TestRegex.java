package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {
	
	static Pattern p = Pattern.compile("([\\pL-'])");
	
	public static void main(String[] args) {
		
		String test = "abcdefghijklàèú'yÉ    Ü12345678@-@'@#$%ˆ&*(çÇ-'";
		
		Matcher m = p.matcher(test);
		
		while(m.find()) {
			System.out.print(m.group());
		}
		
	}
	
}
