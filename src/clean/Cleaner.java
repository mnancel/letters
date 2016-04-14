package clean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cleaner {
	
	private Pattern pattern;
	private String replace;
	
	public Cleaner(String regex, String _replace) {
		
		pattern = Pattern.compile(regex);
		replace = _replace;
		
	}
	
	public String test(String s) {
		
		Matcher m = pattern.matcher(s);
//		while (m.find()) {
//			System.out.println(m.group());
//			System.out.println(">> " + m.group().replaceAll(pattern.pattern(), replace));
//		}
		if (m.find()) {
			return m.replaceAll(replace);
		}
		
		System.out.println("No match found.");
		return null;
		
	}
	
}
