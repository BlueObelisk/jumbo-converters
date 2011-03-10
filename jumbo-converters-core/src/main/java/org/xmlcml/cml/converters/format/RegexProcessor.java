package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.PatternContainer;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;

public class RegexProcessor {
	private final static Logger LOG = Logger.getLogger(RegexProcessor.class);

	private static final String X_NAME = "dummy:dummy";

	private Pattern FIELD_PATTERN = Pattern.compile("\\{(\\d*[FIAXE][^\\}]*)\\}");
	private Pattern FIELD_PATTERN_1 = Pattern.compile(
		"\\{(\\d*)([FIAXE])(\\d*)\\.?(\\d*)" +
		"\\,?(([A-Za-z0-9]+:[A-Za-z0-9][A-Za-z0-9\\-\\.\\_]*)?)" +
		"\\,?(([A-Za-z0-9]+:[A-Za-z0-9][A-Za-z0-9\\-\\.\\_]*)?)" +
		"\\}");

	private List<RegexField> fieldList;
	private String content;
	private String multipleLineDelimiter;
	protected PatternContainer patternContainer;
	
	private String multipliers = "";
	private String types = "";
	private String decimals = "";
	private String widths = "";
	private String names = "";
	private String units = "";
	
	private LineContainer lineContainer;
	private List<Pattern> patternList;
	private RegexField regexField;

	private Integer multiplier;


	public RegexProcessor(String content, String multipleLineDelimiter) {
		if (content == null) {
			throw new RuntimeException("null content");
		}
		// remove all newlines+immediatewhitespace but not other whitespace
		this.content = content.replaceAll("\\s*\\n\\s*", "");
		this.multipleLineDelimiter = multipleLineDelimiter;
		createRegexFields();
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
	
	private void createRegexFields() {
		expandSymbolsToRegexes();
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
		Matcher matcher = FIELD_PATTERN.matcher(content);
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
		if (isSkip(matcher)) {
			addMultiplier0(null);
			addType0(RegexField.X);
			addWidth(matcher);
			addDecimal0(null);
			addName0(X_NAME);
			addUnits0(null);
		} else {
			addMultiplier(matcher);
			addType(matcher);
			addWidth(matcher);
			addDecimal(matcher);
			addName(matcher);
			addUnits(matcher);
		}
		String newString = regexField.createExpandedField(string);
		return newString;
	}

	private boolean isSkip(Matcher matcher) {
		return RegexField.X.equals(matcher.group(2));
	}

	private void addMultiplier(Matcher matcher) {
		multiplier = (matcher.group(1).equals("")) ? null : new Integer(matcher.group(1));
		addMultiplier0(multiplier);
	}

	private void addMultiplier0(Integer multiplierI) {
		regexField.setMultiplier(multiplierI);
		multipliers += " "+multiplierI;
	}

	private void addType(Matcher matcher) {
		String type = RegexField.createType(matcher.group(2));
		addType0(type);
	}

	private void addType0(String type) {
		regexField.setType(type);
		types += " "+type;
	}

	private void addWidth(Matcher matcher) {
		Integer width = (matcher.group(3).equals("")) ? 0 : new Integer(matcher.group(3)); 
		regexField.setWidth(width);
		widths += " "+width;
	}

	private void addDecimal(Matcher matcher) {
		Integer decimal = (matcher.group(4).equals("")) ? 0 : new Integer(matcher.group(4)); 
		addDecimal0(decimal);
	}

	private void addDecimal0(Integer decimal) {
		regexField.setDecimal(decimal);
		decimals = " "+decimal;
	}

	private void addName(Matcher matcher) {
		String name = matcher.group(5);
		addName0(name);
	}

	private void addName0(String name) {
		regexField.setName(name);
		names += " "+name;
	}

	private void addUnits(Matcher matcher) {
		String unit = matcher.group(7);
		addUnits0(unit);
	}

	private void addUnits0(String unit) {
		regexField.setUnit(unit);
		units += " "+unit;
	}

	public static String createSkipField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			// obviously dangerous if misused
			newString = ".*";
		} else {
			newString = "([.]{"+width+"})";
		}
		return newString;
	}

	public static String createStringField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			newString = "\\s*(\\S+)\\s*";
		} else {
			newString = "((?=[ ]*\\S+)[ \\S]{"+width+"})";
		}
		return newString;
	}


	public static String createIntegerField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			newString = "\\s*(\\-?\\d+)\\s*";
		} else {
			newString = "((?=[ ]*\\-?\\d+)[ \\-\\d]{"+width+"})";
		}
		return newString;
	}

	public static String createFloatField(Integer width, Integer decimal) {
		String newString;
		if (width == null || width == 0) {
			newString = "\\s*(\\-?\\d+\\.?\\d*)\\s*";
		} else {
			if (decimal == null) {
				throw new RuntimeException("decimal must be given: ");
			}
			int first = width - decimal -1;
			if (first < 1) {
				throw new RuntimeException("bad width/decimal: ");
			}
			newString = "((?=[ ]*\\-?\\d+)[ \\-\\d]{"+first+"}\\.\\d{"+decimal+"})";
		}
		return newString;
	}

	public static String createDateField(Integer width) {
		String newString;
		if (width == null || width == 0) {
			throw new RuntimeException("width must be given: ");
		} else {
			newString = "([A-Za-z0-9\\-\\+\\:]{"+width+"})";
		}
		return newString;
	}

	public static String createExponentialField(Integer width, Integer decimal) {
		String newString;
		if (width == null || width == 0) {
			newString = "\\s*(\\-?\\d+\\.\\d+[DE][\\+\\-]\\d\\d)\\s*";
		} else {
			if (decimal == null) {
				throw new RuntimeException("decimal must be given: ");
			}
			int first = width - decimal -1;
			if (first < 1) {
				throw new RuntimeException("bad width/decimal: ");
			}
			newString = "((?=[ ]*\\-?\\d+)[ \\-\\d]{"+first+"}\\.\\d{"+decimal+"}[DE][\\+\\-]\\d\\d)";
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
			LOG.trace("<match?>"+pattern+">>["+line+"]"+matches+" "+matcher.groupCount());
			if (matches) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String matcherGroup = matcher.group(i);
					LOG.trace("matcher "+matcherGroup);
					if (matcherGroup != null) {
						RegexField field = (fieldList.size() >= i) ? fieldList.get(fieldCounter) : null;
//						field.debug();
						CMLScalar scalar = null;
						if (field != null) {
							scalar = field.createNamedTypedScalar(matcher, i, matcherGroup);
						} else {
							scalar = new CMLScalar(matcherGroup);
						}
						list.add(scalar);
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
