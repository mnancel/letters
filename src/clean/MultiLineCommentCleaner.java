package clean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiLineCommentCleaner extends Cleaner {

	protected Pattern startPattern, endPattern;
	boolean unendedPattern = false;
	
	public MultiLineCommentCleaner(String _startRegex, String _endRegex) {
		
		startPattern = Pattern.compile(
				_startRegex
				+ ".*(?!" + _endRegex + ").*$");
		
		endPattern = Pattern.compile(".*" + _endRegex);
		
	}
	
	@Override
	public String test(String s) {
		
		if (s != null && s.length() > 0) {
		
			if (unendedPattern) {
				
				Matcher m = endPattern.matcher(s);
				
				if (!m.find()) {
					// Unended comment, and no end found: ignore the whole line
					return null;
				} 
					
				// Unended comment to which we've found the end
				unendedPattern = false;
				return s.substring(m.end());
					
			} 
				
			Matcher m = startPattern.matcher(s);
			
			if (!m.find()) {
				return s;
			}
			
			/*
			 * In principle, single-line comments have been cleared by a 
			 * previous cleaner.
			 * So no need to check for endPattern.
			 */
			unendedPattern = true;
//			System.out.println("[start found in \"" + s + "\"]");
			return s.substring(0, m.start());
			
		}
		
		return null;
		
	}
	
	public static void main(String[] args) {
		
		String test = 
				"ta mère\n"
				+ "ta mère en commentaire /* sur le boulevard\n"
				+ "\n"
				+ "Saint Denis*/ J'ai dit Saint Denis gros\n"
				+ "bâtard de tes morts.\n"
				+ "Tiens j'te cherche /* des putes\n"
				+ "/* des putes\n"
				+ "lol */ des poux dans la tête.";
		System.out.println("== Test:\n" + test + "\n=========\n");
		
		MultiLineCommentCleaner mlcc = new MultiLineCommentCleaner(
				"/\\*", "\\*/");
		
		String[] split = test.split("\\n");
		for (String s : split) {
			System.out.println(mlcc.test(s));
		}
		
	}

}
