package clean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpCleaner extends Cleaner {
	
	protected Pattern pattern;
	protected String replace;
	protected boolean keep;
	
	public RegexpCleaner(String regex, String _replace, boolean _keep) {
		
		pattern = Pattern.compile(regex);
		replace = _replace;
		keep = _keep;
		
	}
	
	@Override
	public String test(String s) {
		
		if (s != null && s.length() > 0) {
		
			Matcher m = pattern.matcher(s);
//			while (m.find()) {
//				System.out.println(m.group());
//				System.out.println(">> " + m.group().replaceAll(pattern.pattern(), replace));
//			}
			if (m.find()) {
				return m.replaceAll(replace);
			}
			
//			System.err.println("No match found.");
			if (keep) {
				return s;
			}
		}
		
		return null;
		
	}
	
}
