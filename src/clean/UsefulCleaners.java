package clean;

public class UsefulCleaners {
	
	/**
	 * Keeps only the text between simple <p/> markers
	 */
	public static final RegexpCleaner pIdCleaner = new RegexpCleaner(
			"^\\s+<p id=\"[^\"]+\">(.*)</p>$", "$1", false);
	
	/**
	 * Keeps only the text between simplER <p/> markers
	 */
	public static final RegexpCleaner pCleaner = new RegexpCleaner(
			"<[pP][^>]*>(.*)</[pP]>$", "$1", false);
	
	/**
	 * Keeps only the lines that don't start with '<' or end with '>'
	 */
	public static final RegexpCleaner MarkupCleaner = new RegexpCleaner(
			"^([^<].*[^>])$", "$1", false);
	
	/**
	 * Space before a punctuation mark when there shouldn't be one.
	 */
	public static final RegexpCleaner punctuationCleaner1 = new RegexpCleaner(
			" ([,\\.\\)])", "$1", true);
	
	/**
	 * No space before a punctuation mark when there should be one
	 */
	public static final RegexpCleaner punctuationCleaner2 = new RegexpCleaner(
			"([\\w\\d])([!\\?;:\\(])", "$1 $2", true);
	
	/**
	 * No space after a punctuation mark when there should be one.
	 * EDIT: kept the comma and period out because of numbers.
	 */
	public static final RegexpCleaner punctuationCleaner3 = new RegexpCleaner(
			"([!\\?;:\\)…])([\\w\\d])", "$1 $2", true);
	
	/**
	 * Special case of space after period and comma within a number
	 */
	public static final RegexpCleaner punctuationCleaner4 = new RegexpCleaner(
			"(\\d) ?([,\\.]) ?(\\d)", "$1$2$3", true);
	
	public static final HtmlCharsCleaner htmlCharsCleaner = 
			new HtmlCharsCleaner();
	
	public static final String noAccentRegex = "[a-zA-Z]";
	
}
