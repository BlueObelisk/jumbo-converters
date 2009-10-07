package org.xmlcml.cml.converters.marker;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Text;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

/** utilities for parsing
 * 
 * @author pm286
 *
 */
public abstract class ParserUtil {
	private static final Logger LOG = Logger.getLogger(ParserUtil.class);

	/** constants
	 * 
	 */
	/** blank line (unlimited whitespace) */
	public final static String PARSER_BLANKLINE = "@b";
	public final static String REGEX_BLANKLINE = "^\\s+$";
	
	/** any nonspace characters */
	public final static String PARSER_CHARS = "@c";
	public final static String REGEX_CHARS = "(\\S+)"; 
	/** dont treat as capture group */
	public final static String REGEX_CHARS_NC = "(?:\\S+)";

	/** spaces chars spaces */
	public final static String PARSER_CHARS_PADDED = "@scs"; 
	// does not capture padding spaces
	public final static String REGEX_CHARS_PADDED = "\\s*"+REGEX_CHARS+"\\s*"; 
	
	/** chars spaces/chars chars*/
	public final static String PARSER_CHARS_SPACED = "@csc"; 
	// must start and end with non-spaces
	public final static String REGEX_CHARS_SPACED = "(\\S+[\\s\\S]*\\S+)"; 
	
	/** spaces chars spaces/chars chars spaces */
	public final static String PARSER_CHARS_SPACED_PADDED = "@scscs"; 
	// does not capture padding spaces
	public final static String REGEX_CHARS_SPACED_PADDED = "\\s*"+REGEX_CHARS_SPACED+"\\s*"; 
	
	/** integer digits (d dd ddd or -d -dd etc) */
	public final static String PARSER_INTEGER = "@i"; 
	public final static String REGEX_INTEGER = "(\\-?\\d+)"; 
	
	/** spaces (integer digits) spaces  .. does not capture spaces */
	public final static String PARSER_INTEGER_PADDED = "@sis"; 
	// does not capture padding spaces
	public final static String REGEX_INTEGER_PADDED = "\\s*"+REGEX_INTEGER+"\\s*"; 
	
	/** floats (d.ddd -dd. -dd.ddE-03 etc) */
	public final static String PARSER_FLOAT = "@f"; 
	public final static String REGEX_FLOAT = "(\\-?\\d*\\.?\\d+(?:[DdEeGg][\\+\\-]?\\d+)?)"; 
	// 
	
	/** spaces (float) spaces   .. spaces not captured */
	public final static String PARSER_FLOAT_PADDED = "@sfs"; 
	// does not capture padding spaces
	public final static String REGEX_FLOAT_PADDED = "\\s*"+REGEX_FLOAT+"\\b\\s*";
	
	/** any line (whole line captured)  */
	public final static String PARSER_ANYLINE = "@a"; 
	public final static String REGEX_ANYLINE = "^.*$";
	
	/** any line no capture */
	public final static String PARSER_ANYLINE_NC = "@aa"; 
	public final static String REGEX_ANYLINE_NC = "(^.*$)";

	public static final String MIN_COUNT ="minCount";
	public static final String MAX_COUNT ="maxCount";


	/** removes empty/whitspace lines at start and end.
	 * normally required when a text chunk is split and the leading and trailng newlines 
	 * get included as empty lines
	 * fails if list is hiding a Java array
	 * @param strings
	 */
	public static void trimEmptyLines(List<String> strings) {
		try {
			while (strings.size() > 0) {
				String s0 = strings.get(0);
				if (s0.trim().length() == 0) {
					strings.remove(0);
				} else {
					break;
				}
			}
			while (strings.size() > 0) {
				int ns = strings.size();
				String sn1 = strings.get(ns-1);
				if (sn1.trim().length() == 0) {
					strings.remove(ns-1);
				} else {
					break;
				}
			}
		} catch (UnsupportedOperationException e) {
			throw new RuntimeException("List cannot be modified (e.g. if created as Arrays.asList(String[]))");
		}
	}
	
	/** adds backslash to (fortuiitous) metacharacters
	 * 
	 * @param s
	 * @return
	 */
	private static String escapeRegexMetacharacters(String s) {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (".?+()[]\\^${}|*".indexOf(s.charAt(i)) != -1) {
				sb.append(CMLConstants.S_BACKSLASH);
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	static Pattern DECURLY = Pattern.compile(".*\\{(\\d+,\\d+)\\}.*");
	static Pattern RECURLY = Pattern.compile("LCURLY(\\d+,\\d+)RCURLY");
	/**
	 * escapes counts such as {1,3} 
	 * @param s
	 * @return
	 */
	private static String escapeRegexCounts(String s) {
		Matcher matcher = DECURLY.matcher(s);
		if (matcher.matches()) {
			s = matcher.replaceAll("LCURLY"+matcher.group(1)+"RCURLY");
		}
		return s;
	}
	
	/**
	 * re-escapes counts to such as {1,3} 
	 * @param s
	 * @return
	 */
	private static String reEscapeRegexCounts(String s) {
		Matcher matcher = RECURLY.matcher(s);
		if (matcher.matches()) {
			s = matcher.replaceAll(CMLConstants.S_LCURLY+matcher.group(1)+CMLConstants.S_RCURLY);
		}
		return s;
	}
	
	/** translates symbolic regexes.
	 * example @c3 is \S{3}
	 * @param t to translate
	 * @return
	 */
	private static String replaceCharNumbers(String t) {
		int start;
		int end = 0;
		Pattern pattern = Pattern.compile(PARSER_CHARS+"(\\d+)");
		while (true) {
			Matcher matcher = pattern.matcher(t);
			if (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				String replace = ParserUtil.interpretCharsNumberRegex(t.substring(start, end));
				t = t.substring(0, start) + replace + t.substring(end);
			} else {
				break;
			}
		}
		return t;
	}

	/** translates symbolic regexes.
	 * example @i3 is [\-\d]\d\d
	 * @param t to translate
	 * @return
	 */
	private static String replaceIntegerNumbers(String t) {
		int start;
		int end = 0;
		Pattern pattern = Pattern.compile(PARSER_INTEGER+"(\\d+)");
		while (true) {
			Matcher matcher = pattern.matcher(t);
			if (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				String replace = ParserUtil.interpretIntegerNumberRegex(t.substring(start, end));
				t = t.substring(0, start) + replace + t.substring(end);
			} else {
				break;
			}
		}
		return t;
	}
	
	/** gets type of object regex captures
	 * at present simple lookup from preprepared regexes 
	 * @return
	 */
	static Class getRegexCaptureType(String regex) {
		Class clazz = String.class;
		if (regex.equals(ParserUtil.REGEX_FLOAT)) {
			clazz = Double.class;
		} else if(regex.equals(ParserUtil.REGEX_INTEGER)) {
			clazz = Integer.class;
		} else if(regex.matches("\\(\\[\\\\-\\\\d\\](\\\\d)*\\)")) {
			clazz = Integer.class;
		}
		return clazz;
	}
	
	/** finds balanced bracket allowing for metabrackets
	 * mainly for identifying capture groups
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	static int getBalancedBracketSkippingEscapes(String s, int start) {
		int pos = -1;
		
		if (s != null && s.length() > 1 &&
				!isEscaped(s, start) && s.charAt(start) == CMLConstants.C_LBRAK) {
			int nbrak = 1;
			for (int i = start+1; i < s.length(); i++) {
				char c = s.charAt(i);
				if (!isEscaped(s, i)) {
					if (c == CMLConstants.C_LBRAK) {
						nbrak++;
					} else if (c == CMLConstants.C_RBRAK) {
						nbrak--;
						if (nbrak == 0) {
							pos = i;
							break;
						}
					}
				}
			}
		}
		return pos;
	}
	
	/** is a character escaped (i.e. preceding char is backslash
	 * 
	 * @param s
	 * @param pos
	 * @return
	 */
	private static boolean isEscaped(String s, int pos) {
		return s != null && pos > 0 && s.length() > 1 && s.charAt(pos-1) == CMLConstants.C_BACKSLASH;
	}
	
	/** find first leading non-escaped bracket.
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	static int getFirstBalanceableBracketSkippingEscapes(String s, int start) {
		int pos = -1;
		int i = start;
		if (s != null) {
			while (i < s.length()) {
				if (!isEscaped(s, i) && s.charAt(i) == CMLConstants.C_LBRAK) {
					pos = i;
					break;
				}
				i++;
			}
		}
		return pos;
	}
		
	/**	
	 * translate raw template to complete regexes
	 *  e.g. converter symbolic regex components to regex
	 * ... @s - > REGEX_SPACELESS
	 * ... @scs -> REGEX_SPACEFILLED
	 * ... @csc -> REGEX_SPACELESS_PADDED
	 * ... @cscsc -> REGEX_SPACEFILLED_PADDED
	 */
	static String translateTemplateToRegex(String t) {
		if (t != null) {
			t = ParserUtil.escapeRegexCounts(t);
			t = ParserUtil.escapeRegexMetacharacters(t);
			t = ParserUtil.reEscapeRegexCounts(t);
			t = t.replace(ParserUtil.PARSER_BLANKLINE, ParserUtil.REGEX_BLANKLINE);
			// complex strings first
			t = t.replace(ParserUtil.PARSER_CHARS_SPACED_PADDED, ParserUtil.REGEX_CHARS_SPACED_PADDED);
			t = t.replace(ParserUtil.PARSER_CHARS_PADDED, ParserUtil.REGEX_CHARS_PADDED);
			t = t.replace(ParserUtil.PARSER_CHARS_SPACED, ParserUtil.REGEX_CHARS_SPACED);
			t = t.replace(ParserUtil.PARSER_INTEGER_PADDED, ParserUtil.REGEX_INTEGER_PADDED);
			t = t.replace(ParserUtil.PARSER_FLOAT_PADDED, ParserUtil.REGEX_FLOAT_PADDED);
			t = t.replace(ParserUtil.PARSER_FLOAT, ParserUtil.REGEX_FLOAT);
			t = ParserUtil.replaceCharNumbers(t);
			t = ParserUtil.replaceIntegerNumbers(t);
			// only now the simple ones
			t = t.replace(ParserUtil.PARSER_CHARS, ParserUtil.REGEX_CHARS);
			t = t.replace(ParserUtil.PARSER_INTEGER, ParserUtil.REGEX_INTEGER);
			t = t.replace(ParserUtil.PARSER_ANYLINE, ParserUtil.REGEX_ANYLINE);
		}
		LOG.trace(t);
		return t;
	}
	
	/** interpret symbols for integer regex
	 * 
	 * @param s
	 * @return
	 */
	private static String interpretIntegerNumberRegex(String s) {
		String ss = null;
		if (s != null && s.startsWith(ParserUtil.PARSER_INTEGER)) {
			Matcher matcher = Pattern.compile(ParserUtil.PARSER_INTEGER+"(\\d+)").matcher(s);
			if (matcher.matches()) {
				int ii = new Integer(matcher.group(1));
				if (ii > 0) {
					ss = "([\\-\\d]";
					for (int i = 1; i < ii; i++) {
						ss += "\\d";
					}
					ss += ")";
				}
			}
		}
		return ss;
	}
	
	/** converts mnemonic into regex
	 * e.g. 
	 * ... @s - > REGEX_SPACELESS
	 * ... @scs -> REGEX_SPACEFILLED
	 * ... @csc -> REGEX_SPACELESS_PADDED
	 * ... @cscsc -> REGEX_SPACEFILLED_PADDED
	 * @param mnemonic
	 * @return
	 */
	private static String interpretCharsNumberRegex(String s) {
		String ss = null;
		if (s != null && s.startsWith(ParserUtil.PARSER_CHARS)) {
			Matcher matcher = Pattern.compile(ParserUtil.PARSER_CHARS+"(\\d+).*").matcher(s);
			if (matcher.matches()) {
				int ii = new Integer(matcher.group(1)).intValue();
				ss = "(";
				for (int i = 0; i < ii; i++) {
					ss += "\\S";
				}
				ss += ")";		
			}
		}
		return ss;
	}
	
	/** replaces one cmlx attribute by another.
	 * 
	 * @param element
	 * @param attName
	 * @param attName1
	 * @param attValue
	 */
	public static void replaceCMLXAttribute(Element element, String attName,
		String attName1, String attValue) {
		Attribute startAttribute = element.getAttribute(attName, CMLConstants.CMLX_NS);
		if (startAttribute != null) {
			startAttribute.detach();
		}
		element.addAttribute(new Attribute("cmlx:"+attName1, CMLConstants.CMLX_NS, attValue));
	}

	/** removes xsd:string as datatype
	 * purely cosmetic
	 */
	static void removeXSDStringDataTypes(CMLElement expectedModuleToParse) {
		Nodes nodes = expectedModuleToParse.query(".//@dataType[.='xsd:string']");
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}
	
	/** makes sure that a dictRef is a QName
	 * bit kludgy 
	 * @param prefix
	 * @param id
	 */
	static String makeDictRefValue(String prefix, String id) {
		String dictRef = null;
		String idPrefix = (id == null) ? null : NamespaceRefAttribute.getPrefix(id);
		String idLocal = (id == null) ? "bar" : NamespaceRefAttribute.getLocalName(id);
		if (prefix == null) {
			prefix = (idPrefix == null) ? "foo" : idPrefix;
			dictRef = NamespaceRefAttribute.createValue(prefix, idLocal);
		} else {
			dictRef = NamespaceRefAttribute.createValue(prefix, idLocal);
		}
		return dictRef;
	}
	
	static int parseAttribute(Element element, String attName, int defalt) {
		int value = defalt;
		String attVal = element.getAttributeValue(attName);
		if (attVal != null) {
			try {
				value = Integer.parseInt(attVal);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("bad "+attName+" value: "+value);
			}
		}
		return value;
	}

	public static String getLastDotField(String s) {
		int idx = s.lastIndexOf(".");
		return s.substring(idx+1);
	}


}

