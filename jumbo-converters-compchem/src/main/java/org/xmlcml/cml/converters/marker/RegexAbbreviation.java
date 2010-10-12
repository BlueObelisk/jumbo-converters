package org.xmlcml.cml.converters.marker;

/** still being developed
 * 
 * @author pm286
 *
 */
public class RegexAbbreviation {
	public final static String R_CHARS = "(\\S+)";
	// does not capture padding spaces
	public final static String R_CHARS_NC = "(?:\\S+)";
	// must start and end with non-spaces
	public final static String R_CHARS_SPACED = "(\\S+[\\s\\S]*\\S+)"; 
	public enum RA {
		/** blankline */
		BLANKLINE ("^\\s+$", "@b"),
		
		/** any nonspace characters */
		CHARS  (R_CHARS, "@c"),
		/** dont treat as capture group */

		/** spaces chars spaces */
		CHARS_PADDED ("\\s*"+R_CHARS+"\\s*",  "@scs"), 
		
//		/** chars spaces/chars chars*/
//		public final static String PARSER_CHARS_SPACED = "@csc"; 
//		// must start and end with non-spaces
//		public final static String REGEX_CHARS_SPACED = "(\\S+[\\s\\S]*\\S+)"; 
//		
//		/** spaces chars spaces/chars chars spaces */
//		public final static String PARSER_CHARS_SPACED_PADDED = "@scscs"; 
//		// does not capture padding spaces
//		public final static String REGEX_CHARS_SPACED_PADDED = "\\s*"+REGEX_CHARS_SPACED+"\\s*"; 
//		
//		/** integer digits (d dd ddd or -d -dd etc) */
//		public final static String PARSER_INTEGER = "@i"; 
//		public final static String REGEX_INTEGER = "(\\-?\\d+)"; 
//		
//		/** spaces (integer digits) spaces  .. does not capture spaces */
//		public final static String PARSER_INTEGER_PADDED = "@sis"; 
//		// does not capture padding spaces
//		public final static String REGEX_INTEGER_PADDED = "\\s*"+REGEX_INTEGER+"\\s*"; 
//		
//		/** floats (d.ddd -dd. -dd.ddE-03 etc) */
//		public final static String PARSER_FLOAT = "@f"; 
//		public final static String REGEX_FLOAT = "(\\-?\\d*\\.?\\d+(?:[DdEeGg][\\+\\-]?\\d+)?)"; 
//		
//		/** spaces (float) spaces   .. spaces not captured */
//		public final static String PARSER_FLOAT_PADDED = "@sfs"; 
//		// does not capture padding spaces
//		public final static String REGEX_FLOAT_PADDED = "\\s*("+REGEX_FLOAT+")\\s*";
//		
//		/** any line (whole line captured)  */
//		public final static String PARSER_ANYLINE = "@a"; 
//		public final static String REGEX_ANYLINE = "^.*$";
//		
//		/** any line no capture */
//		public final static String PARSER_ANYLINE_NC = "@aa"; 
//		public final static String REGEX_ANYLINE_NC = "(^.*$)";

		;
		private RegexAbbreviation regexAbbreviation;
		private RA(String reg, String abb) {
			regexAbbreviation = new RegexAbbreviation(reg, abb);
		}
		public String getAbbreviation() {
			return regexAbbreviation.abbreviation;
		}
		public String getRegex() {
			return regexAbbreviation.regex;
		}
	}
	
	private String abbreviation;
	private String regex;
	public RegexAbbreviation(String reg, String abb) {
		this.abbreviation = abb;
		this.regex = reg;
	}
}
