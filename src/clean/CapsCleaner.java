package clean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapsCleaner extends Cleaner {

	static final String PUNCT_REGEX = "[\\!\\.\\?]";
	
	protected Pattern pattern;
	boolean lastCharPunct = true;
	
	public CapsCleaner() {
		
		pattern = Pattern.compile( PUNCT_REGEX + "\\s+([a-z])" );
		
	}
	
	@Override
	public String test(String s) {
		
		if (s != null && s.length() > 0) {
		
			StringBuilder sb = new StringBuilder(s);
			
			if (lastCharPunct) {
				sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
			}
			
			Matcher m = pattern.matcher(s);
			
			while (m.find()) {
				String buf= sb.substring(m.start(), m.end()).toUpperCase();
				sb.replace(m.start(), m.end(), buf);
			}
			
			lastCharPunct = s.matches(".*" + PUNCT_REGEX + "\\s+$");
			
			return sb.toString();
		}
		
		return null;
		
	}

}
