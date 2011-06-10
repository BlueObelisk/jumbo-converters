package org.xmlcml.cml.converters.format;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.PatternContainer;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.euclid.Util;

public class RegexProcessor {
	private static final String REGEX_LIST = 
		"org/xmlcml/cml/converters/format/regexList.xml";

	private static final String NAME = "name";
	
	private static String ANY_START_REGEX;
	private static String ANY_FIELD_REGEX;
	private static String EXPONENTIAL_END_REGEX;
	private static String EXPONENT_NOWIDTH_REGEX;
	private static String DATE_START_REGEX;
	private static String DATE_1_REGEX;
	private static String FLOAT_MID_REGEX;
	private static String FLOAT_NOWIDTH_REGEX;
	private static String INTEGER_NOWIDTH_REGEX;
	private static String INTEGER_WIDTH_START_REGEX;
	private static String STRING_NOWIDTH_REGEX;
	private static String STRING_WIDTH_START_REGEX;
	private static String WIDTH_END_REGEX;

	// this is hard-coded and must be kept consistent with the external file
	private static final String MULTIPLIER_SEPARATOR = CMLConstants.S_UNDER;
	// these are now read in
	private static Pattern FIELD_PATTERN_0 = null;
	private static Pattern FIELD_PATTERN_1 = null;

	static {
		Element regexList = null;
		try {
			InputStream is = Util.getResourceUsingContextClassLoader(
				REGEX_LIST, RegexProcessor.class);
			regexList = new Builder().build(is).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read regexes", e);
		}
		Elements childElements = regexList.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element regexElement = childElements.get(i);
			String name = regexElement.getAttributeValue(NAME);
			String regex = regexElement.getValue();
			if (name == null) {
				throw new RuntimeException("no name given");
			} else if (name.equals("ANY_START")) {
				ANY_START_REGEX = regex;
			} else if (name.equals("ANY_FIELD")) {
				ANY_FIELD_REGEX = regex;
			} else if (name.equals("EXPONENTIAL_END")) {
				EXPONENTIAL_END_REGEX = regex;
			} else if (name.equals("EXPONENT_NOWIDTH")) {
				EXPONENT_NOWIDTH_REGEX = regex;
			} else if (name.equals("DATE_START")) {
				DATE_START_REGEX = regex;
			} else if (name.equals("DATE_PATTERN_1")) {
				DATE_1_REGEX = regex;
			} else if (name.equals("FLOAT_MID")) {
				FLOAT_MID_REGEX = regex;
			} else if (name.equals("FLOAT_NOWIDTH")) {
				FLOAT_NOWIDTH_REGEX = regex;
			} else if (name.equals("INTEGER_NOWIDTH")) {
				INTEGER_NOWIDTH_REGEX = regex;
			} else if (name.equals("INTEGER_WIDTH_START")) {
				INTEGER_WIDTH_START_REGEX = regex;
			} else if (name.equals("STRING_NOWIDTH")) {
				STRING_NOWIDTH_REGEX = regex;
			} else if (name.equals("STRING_WIDTH_START")) {
				STRING_WIDTH_START_REGEX = regex;
			} else if (name.equals("WIDTH_END")) {
				WIDTH_END_REGEX = regex;
			} else if (name.equals("FIELD_PATTERN_STRING_0")) {
				FIELD_PATTERN_0  = Pattern.compile(regex);
			} else if (name.equals("FIELD_PATTERN_STRING_1")) {
				FIELD_PATTERN_1  = Pattern.compile(regex);
			} else {			
				throw new RuntimeException("unknown name: "+name);
			}
//			System.out.println("____"+regex+"____");
		}
	}

	private final static Logger LOG = Logger.getLogger(RegexProcessor.class);

	private static final String X_NAME = "dummy:dummy";


	private List<RegexField> fieldList;
	private String content;
	private String multipleLineDelimiter;
	protected PatternContainer patternContainer;
	
//	private String multipliers = "";
	private String types = "";
	private String decimals = "";
	private String widths = "";
	private String names = "";
	private String units = "";
	
	private LineContainer lineContainer;
	private List<Pattern> patternList;
	private RegexField regexField;

	private Integer minMultiplier;
	private Integer maxMultiplier;


	public RegexProcessor(String content, String multipleLineDelimiter) {
		if (content == null) {
			throw new RuntimeException("null content");
		}
		// remove all newlines+immediate whitespace but not other whitespace
		this.content = content.replaceAll("\\s*\\n\\s*", "");
		this.multipleLineDelimiter = multipleLineDelimiter;
		expandSymbolsToRegexes();
		createPatternContainer();
	}
	
	public List<Pattern> getPatternList() {
		return patternContainer.getPatternList();
	}

	public String getNames() {
		return names.trim();
	}

	public String getTypes() {
		return types.trim();
	}
	
	private void createPatternContainer() {
		this.patternContainer = null;
		try {
			patternContainer = new PatternContainer(content, multipleLineDelimiter, 0);
			patternList = patternContainer.getPatternList();
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse regex: "+content, e);
		}
	}

	private void expandSymbolsToRegexes() {
		int start = 0;
		StringBuilder sb = new StringBuilder();
		Matcher matcher = FIELD_PATTERN_0.matcher(content);
		start = replaceSymbolsByRegexes(start, sb, matcher);
		sb.append(content.substring(start));
		content = sb.toString();
	}

	private int replaceSymbolsByRegexes(int start, StringBuilder sb, Matcher matcher) {
		int serial = 0;
		fieldList = new ArrayList<RegexField>();
		while (matcher.find(start)) {
			regexField = new RegexField();
			fieldList.add(regexField);
			int start0 = matcher.start();
			int end = matcher.end();			
			sb.append(content.substring(start, start0));
			String expandable = content.substring(start0, end);
			String transformS = expandSymbols(expandable, serial++);
			sb.append(transformS);
			start = end;
		}
		return start;
	}

	private String expandSymbols(String string, int serial) {
		Matcher matcher = FIELD_PATTERN_1.matcher(string);
		if (!matcher.matches()) {
			throw new RuntimeException("Bad symbolic regex: "+string);
		}
		int matchCount = matcher.groupCount();
		String multiplierField = matcher.group(1);
		String type = matcher.group(2);
		String width = matcher.group(3);
		String decimal = matcher.group(4);
		String name = matcher.group(5);
		String units = matcher.group(6);
		addMultipliers(multiplierField);
		addType(type);
		addWidth(width);
		addDecimal(decimal);
		addName(name);
		addUnits(units);
		string = removeMultiplierFieldFromString(string, multiplierField);
		String newString = regexField.createExpandedField(string);
		return newString;
	}

	private String removeMultiplierFieldFromString(String string,
			String multiplierField) {
		string = string.substring(0,1)+string.substring(1+multiplierField.length());
		return string;
	}

//	private boolean isSkip(Matcher matcher) {
//		return RegexField.X.equals(matcher.group(2));
//	}

	private void addMultipliers(String multiplierGroup) {
		Pattern MULTIPLIER_MATCHER = Pattern.compile("(\\d+)"+MULTIPLIER_SEPARATOR+"(\\d*)");
		Matcher matcher = MULTIPLIER_MATCHER.matcher(multiplierGroup);
		if (matcher.matches()) {
			minMultiplier = new Integer(matcher.group(1));
			maxMultiplier = (CMLConstants.S_EMPTY.equals(matcher.group(2))) ? Integer.MAX_VALUE :
				new Integer(matcher.group(2));
		} else {
			minMultiplier = (multiplierGroup.equals("")) ? 1 : new Integer(multiplierGroup);
			maxMultiplier = minMultiplier;
		}
		regexField.setMinMultiplier(minMultiplier);
		regexField.setMaxMultiplier(maxMultiplier);
	}

	private void addType(String type) {
		regexField.setType(type);
		types += " "+type;
	}

	private void addWidth(String widthS) {
		Integer width = (widthS.equals("")) ? 0 : new Integer(widthS); 
		regexField.setWidth(width);
		widths += " "+width;
	}

	private void addDecimal(String decimalS) {
		Integer decimal = (decimalS.equals("")) ? 0 : new Integer(decimalS); 
		addDecimal0(decimal);
	}

	private void addDecimal0(Integer decimal) {
		regexField.setDecimal(decimal);
		decimals = " "+decimal;
	}

	private void addName(String nameS) {
		regexField.setName(nameS);
		names += " "+nameS;
	}

	private void addUnits(String unitsS) {
		regexField.setUnit(unitsS);
		units += " "+unitsS;
	}

	public static String createAnyField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			// obviously dangerous if misused
			newString = ANY_FIELD_REGEX;
		} else {
			newString = ANY_START_REGEX+width+WIDTH_END_REGEX;
		}
		return newString;
	}

	public static String createStringField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			newString = STRING_NOWIDTH_REGEX;
		} else {
			newString = STRING_WIDTH_START_REGEX+width+WIDTH_END_REGEX;
		}
		return newString;
	}


	public static String createIntegerField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			newString = INTEGER_NOWIDTH_REGEX;
		} else {
			newString = INTEGER_WIDTH_START_REGEX+width+WIDTH_END_REGEX;
		}
		return newString;
	}

	public static String createFloatField(Integer width, Integer decimal) {
		String newString;
		if (width == null || width == 0) {
			newString = FLOAT_NOWIDTH_REGEX;
		} else {
			if (decimal == null) {
				throw new RuntimeException("decimal must be given: ");
			}
			int first = width - decimal -1;
			if (first < 1) {
				throw new RuntimeException("bad width/decimal: ");
			}
			newString = INTEGER_WIDTH_START_REGEX+first+FLOAT_MID_REGEX+decimal+WIDTH_END_REGEX;
		}
		return newString;
	}

	public static String createDateField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			newString = DATE_1_REGEX;
//			throw new RuntimeException("width must be given: ");
		} else {
			newString = DATE_START_REGEX+width+WIDTH_END_REGEX;
		}
		return newString;
	}

	public static String createExponentialField(Integer width, Integer decimal) {
		String newString;
		if (width == null || width == 0) {
			newString = EXPONENT_NOWIDTH_REGEX;
		} else {
			if (decimal == null) {
				throw new RuntimeException("decimal must be given: ");
			}
			int first = width - decimal -1;
			if (first < 1) {
				throw new RuntimeException("bad width/decimal: ");
			}
			newString = INTEGER_WIDTH_START_REGEX+first+FLOAT_MID_REGEX+decimal+EXPONENTIAL_END_REGEX;
		}
		return newString;
	}

	// ======================= processing ====================

	protected List<HasDataType> readRecord(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		List<HasDataType> list = new ArrayList<HasDataType>();
		int fieldCounter = 0;
		for (Pattern pattern : patternContainer.getPatternList()) {
			String line = this.lineContainer.peekLine();
			if (line == null) {
				break;
			}
			Matcher matcher = pattern.matcher(line);
			boolean matches = matcher.matches();
//			System.err.println("<match?>"+pattern+" .. ["+line+"]"+matches+" "+matcher.groupCount());
			if (matches) {
				int groupCount = matcher.groupCount();
				for (int i = 1; i <= groupCount; i++) {
					String matcherGroup = matcher.group(i);
					LOG.trace("matcher "+matcherGroup);
					if (matcherGroup != null) {
						RegexField field = (fieldList.size() >= i) ? fieldList.get(fieldCounter) : null;
//						field.debug();
						HasDataType hasDataType = null;
						if (field != null) {
							try {
								hasDataType = field.createNamedTypedScalar(matcher, i, matcherGroup);
							} catch (Exception e){
								LOG.error("Field "+field+"; matcher|"+matcherGroup+"|"+line+"|");
								throw new RuntimeException("cannot create scalar/array", e);
							}
						} else {
							hasDataType = new CMLScalar(matcherGroup);
						}
						list.add(hasDataType);
					}
					fieldCounter++;
				}
				lineContainer.readLine();
			}
		}
		return list;
	}

	public String toString() {
		return patternContainer.toString();
	}

	public void debug() {
		for (RegexField field : fieldList) {
			field.debug();
		}
	}

}
