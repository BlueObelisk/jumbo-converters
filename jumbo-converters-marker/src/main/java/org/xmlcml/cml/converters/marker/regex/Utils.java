package org.xmlcml.cml.converters.marker.regex;

public class Utils {

	static String reEscapeMetacharacters(String string) {
		string = string.replaceAll(Utils.BSLASH_TEMP, Utils.BSLASH_ESC);
		string = string.replaceAll(Utils.LBRACK_TEMP, Utils.LBRACK_ESC);
		string = string.replaceAll(Utils.RBRACK_TEMP, Utils.RBRACK_ESC);
		string = string.replaceAll(Utils.LSQUARE_TEMP, Utils.LSQUARE_ESC);
		string = string.replaceAll(Utils.RSQUARE_TEMP, Utils.RSQUARE_ESC);
		string = string.replaceAll(Utils.LSQUARE_TEMP, Utils.LSQUARE_ESC);
		string = string.replaceAll(Utils.RSQUARE_TEMP, Utils.RSQUARE_ESC);
		return string;
	}

	static String deescapeMetacharacters(String string) {
		string = string.replaceAll(Utils.BSLASH_ESC, Utils.BSLASH_TEMP);
		string = string.replaceAll(Utils.LBRACK_ESC, Utils.LBRACK_TEMP);
		string = string.replaceAll(Utils.RBRACK_ESC, Utils.RBRACK_TEMP);
		string = string.replaceAll(Utils.LSQUARE_ESC, Utils.LSQUARE_TEMP);
		string = string.replaceAll(Utils.RSQUARE_ESC, Utils.RSQUARE_TEMP);
		string = string.replaceAll(Utils.LSQUARE_ESC, Utils.LSQUARE_TEMP);
		string = string.replaceAll(Utils.RSQUARE_ESC, Utils.RSQUARE_TEMP);
		return string;
	}

	public final static String BSLASH = "\\";
	public final static String BSLASH_ESC = BSLASH+BSLASH;
	public final static String BSLASH_TEMP = "__BS";
	public final static String LBRACK_ESC = BSLASH_ESC+BSLASH+"(";
	public final static String LBRACK_TEMP = "__LB";
	public final static String RBRACK_ESC = BSLASH_ESC+BSLASH+")";
	public final static String RBRACK_TEMP = "__RB";
	public final static String LCURLY_ESC = BSLASH_ESC+BSLASH+"{";
	public final static String LCURLY_TEMP = "__LC";
	public final static String RCURLY_ESC = BSLASH_ESC+BSLASH+"}";
	public final static String RCURLY_TEMP = "__RC";
	public final static String LSQUARE_ESC = BSLASH_ESC+BSLASH+"[";
	public final static String LSQUARE_TEMP = "__LSQ";
	public final static String RSQUARE_ESC = BSLASH_ESC+BSLASH+"]";
	public final static String RSQUARE_TEMP = "__RSQ";
	static Integer parseInteger(String value) {
		Integer intValue = null;
		try {
			if (value == null) {
				value = "1";
			} else if (value.equals(AbstractRegexElement.ANY)) {
				value = ""+Integer.MAX_VALUE;
			}
			intValue = new Integer(value);
		} catch (Exception e) {
			AbstractRegexElement.LOG.debug("Canot parse as integer "+value);
		}
		return intValue;
	}

}
