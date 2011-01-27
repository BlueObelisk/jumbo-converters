package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.RealArray;

public class CaptureGroup {
	static Logger LOG = Logger.getLogger(CaptureGroup.class);
	static {
//		LOG.setLevel(Level.TRACE);
		LOG.setLevel(Level.DEBUG);
	}
	
	// of form \\s*((?:\\-?\\d+\\b\\s*){1,3}) or {1,} or {3}
	// ((?:\-?\d+\.\d+\s+){1,3})
//	private static String testTarget = "((?:\\-?\\d+\\.\\d+\\s+){1,3})";
	private static String QUANTIFIER0_S = 
		"\\(      # first capture bracket in target\n" +
		"\\?\\:   # and its non-capture flag\n" +
		"(.*)     # the first group we want to capture\n" +
		"\\)      # end of first capture bracket in target\n" +
		"\\{(\\d+\\,?\\d*)\\} # quantifier and capture group\n";
		
	private static String QUANTIFIER_S =
			"\\(      # leading bracket\n" +
			QUANTIFIER0_S+
			"\\)     # bracket\n";
	// may optionally have matched surrounding brackets
	private static Pattern QUANTIFIER_P = Pattern.compile(QUANTIFIER_S, Pattern.COMMENTS);
	private static Pattern QUANTIFIER0_P = Pattern.compile(QUANTIFIER0_S, Pattern.COMMENTS);
		
	
	private static Pattern COUNT_PATTERN1 = Pattern.compile("(\\d+)");
	private static Pattern COUNT_PATTERN2 = Pattern.compile("(\\d+),");
	private static Pattern COUNT_PATTERN3 = Pattern.compile("(\\d+),(\\d+)");
	
	private static String DOUBLE_SUFFIX = "(d)";
	private static String INTEGER_SUFFIX = "(i)";
	
	String captureExpression;
	private CaptureGroupList subCaptureGroupList;
	private String subCaptureExpression;
	private String matchedValue;
	private int minCount = -1;
	private int maxCount = -1;
	private List<String> matchedValues;
	private String dataType;
	private String templateDictRef;
	
	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public String getTemplateDictRef() {
		return templateDictRef;
	}

	private String templatePrefix;
	private String dictRef;
	private String variableName;
	private CMLArray dataArray;
	private CMLScalar dataScalar;
	
	public CaptureGroup(String captureExpression, String templateDictRef, String templatePrefix) {
		this.captureExpression = captureExpression;
		this.templateDictRef = templateDictRef;
		this.templatePrefix = templatePrefix;
		extractQuantifierAndSubCaptureGroup();
	}
	
	public CaptureGroupList getSubCaptureGroupList() {
		return subCaptureGroupList;
	}

	public String getDictRef() {
		return dictRef;
	}

	public String getDataType() {
		return dataType;
	}

	public String getMatchedValue() {
		return matchedValue;
	}

	public void setMatchedValue(String matchedValue) {
		this.matchedValue = matchedValue;
	}

	CMLElement getCMLElement() {
		CMLElement element = null;
		if (dataArray != null) {
			element = dataArray;
		} else if (dataScalar != null) {
			element = dataScalar;
		}
		((HasDictRef)element).setDictRef(dictRef);
		return element;
	}
	
	public List<String> getMatchedValues() {
		return matchedValues;
	}

	public String getCaptureExpression() {
		return captureExpression;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public String getVariableName() {
		return variableName;
	}

	public CMLArray getDataArray() {
		return dataArray;
	}

	public CMLScalar getDataScalar() {
		return dataScalar;
	}

	void processCaptureGroupOrSubCaptureGroups(String matchedValue) {
		try {
			if (subCaptureExpression != null) {
				// there is a problem with left justified floats, so don't trim
				reParseAgainstSubCaptureGroups(matchedValue);
				createDataArray();
			} else {
				createdataScalar(matchedValue.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("matching: "+matchedValue);
			LOG.info("CaptureGroup: "+this.toString());
			throw new RuntimeException("CaptureGroup parsing failed ("+e.getMessage()+"), see log", e);
		}
	}

	private void createdataScalar(String matchedValue) {
		dataScalar = null;
		if (dataType.equals(CMLConstants.XSD_DOUBLE)) {
			dataScalar = new CMLScalar(Real.parseDouble(matchedValue.trim()));
		} else if (dataType.equals(CMLConstants.XSD_INTEGER)) {
			dataScalar = new CMLScalar(Integer.parseInt(matchedValue.trim()));
		} else {
			dataScalar = new CMLScalar(matchedValue);
		}
	}

	private void createDataArray() {
		dataArray = null;
		if (dataType.equals(CMLConstants.XSD_DOUBLE)) {
			RealArray ra = new RealArray(matchedValues.toArray(new String[0]));
			dataArray = new CMLArray(ra);
		} else if (dataType.equals(CMLConstants.XSD_INTEGER)) {
			IntArray ia = new IntArray(matchedValues.toArray(new String[0]));
			dataArray = new CMLArray(ia.getArray());
			dataArray.debug("ARRAY");
		} else {
			dataArray = new CMLArray(matchedValues.toArray(new String[0]));
		}
	}

	private void reParseAgainstSubCaptureGroups(String matchedValue) {
		Pattern pattern = null;
		String patternS = CMLConstants.S_LBRAK+subCaptureExpression+CMLConstants.S_RBRAK;
		try {
			pattern = Pattern.compile(patternS);
		} catch (Exception e) {
			LOG.error("bad regex "+patternS);
			throw new RuntimeException("Bad regular expression ("+patternS+")", e);
		}
		Matcher matcher = pattern.matcher(matchedValue);
		int start = 0;
		matchedValues = new ArrayList<String>();
		while (true) {
			if (!matcher.find(start)) {
				break;
			}
			int end = matcher.end();
			String value = matchedValue.substring(start, end).trim();
			matchedValues.add(value);
			start = end;
		}
	}
	
	private void generateNameAndDataType(String name) {
		dataType = CMLConstants.XSD_STRING;
		String vn = name.toLowerCase();
		variableName = name;
		if (vn.endsWith(DOUBLE_SUFFIX)) {
			dataType = CMLConstants.XSD_DOUBLE;
			variableName = name.substring(0, name.length()-DOUBLE_SUFFIX.length());
		} else if (vn.endsWith(INTEGER_SUFFIX)) {
			dataType = CMLConstants.XSD_INTEGER;
			variableName = name.substring(0, name.length()-INTEGER_SUFFIX.length());
		}
	}

	// currently only one quantifier supported
	private void extractQuantifierAndSubCaptureGroup() {
		// check against two regexes, one with brackets
		// this is messy but I couldn't get a single regex to work
		if (!extractQuantifierAndCaptureSubGroup(QUANTIFIER_P)) {
			extractQuantifierAndCaptureSubGroup(QUANTIFIER0_P);
		}
		
	}

	private boolean extractQuantifierAndCaptureSubGroup(Pattern quantifierPattern) {
		Matcher matcher = quantifierPattern.matcher(captureExpression);
		boolean matches = matcher.matches();
		if (matches) {
			LOG.trace("matched "+captureExpression+" against "+quantifierPattern);
			subCaptureExpression = matcher.group(1);
			if (matcher.groupCount() >= 2) {
				setCountExpression(matcher.group(2));
			}
			subCaptureGroupList = new CaptureGroupList();
			for (int i = 0; i < 1; i++) {
				CaptureGroup subCaptureGroup = new CaptureGroup(
						subCaptureExpression, templateDictRef+"_"+i, templatePrefix);
				subCaptureGroup.extractQuantifierAndSubCaptureGroup();
				subCaptureGroupList.add(subCaptureGroup);
			}
		} else {
			LOG.trace("failed to match "+captureExpression+" against "+quantifierPattern);
		}
		return matches;
	}

	static void createCaptureGroupsFromGroupList(List<String> nameList,
			CaptureGroupList groupList) {
		for (int i = 0; i < groupList.size(); i++) { 
			CaptureGroup captureGroup = groupList.get(i);
			captureGroup.generateNameAndDataType(nameList.get(i));
			captureGroup.createDictRef();
		}
	}

	/**
	 * processes {1}, {1,}, {1,3} etc.
	 * @param countExpression
	 */
	private void setCountExpression(String countExpression) {
		Matcher matcher = COUNT_PATTERN1.matcher(countExpression);
		if (matcher.matches()) {
			minCount = Integer.parseInt(matcher.group(1));
			maxCount = minCount;
			return;
		}
		matcher = COUNT_PATTERN2.matcher(countExpression);
		if (matcher.matches()) {
			minCount = Integer.parseInt(matcher.group(1));
			maxCount = Integer.MAX_VALUE;
			return;
		}
		matcher = COUNT_PATTERN3.matcher(countExpression);
		if (matcher.matches()) {
			minCount = Integer.parseInt(matcher.group(1));
			maxCount = Integer.parseInt(matcher.group(2));
			return;
		}
		if (minCount < 0 || maxCount < 0 || maxCount < minCount) {
			throw new RuntimeException("Bad count values: "+minCount+", "+maxCount+"; from "+countExpression);
		}
		throw new RuntimeException("Bad count expression: "+countExpression);
	}

	private String createDictRef() {
		String localId = templateDictRef + CMLConstants.S_PERIOD + getVariableName();
		dictRef = ParserUtil.makeDictRefValue(templatePrefix, localId);
		return dictRef;
	}

	String getSubCaptureExpression() {
		return subCaptureExpression;
	}

	List<CMLElement> processCountExpressions(CMLElement element) {
		List<CMLElement> elementList = new ArrayList<CMLElement>();
		elementList.add(element);
		return elementList;
	}
	
	public String toString() {
		String s = "";
		s += "capExp: " + captureExpression+"    ";
		s += "subCapExp: " + subCaptureExpression+"    ";
		s += "matchedVal: " + matchedValue+"    ";
		s += "minCnt: " + minCount+"    ";
		s += "maxCnt: " + maxCount+"    ";
		s += "dType: " + dataType+"    ";
		s += "temDictRef: " + templateDictRef+"    ";
		s += "temPref: " + templatePrefix+"    ";
		s += "dictRef: " + dictRef+"    ";
		s += "varName: " + variableName+"    ";
		s += "dArr: " + ((dataArray != null) ? dataArray.toXML() : "")+"    ";
		s += "dScal: " + ((dataScalar != null) ? dataScalar.toXML() : "")+"    ";
		s += "subCaptureGroupList: " + "    ";
		if (subCaptureGroupList != null) {
			s += "......>>>\n";
			for (CaptureGroup subCaptureGroup : subCaptureGroupList.getGroups()) {
				s += "... "+subCaptureGroup.toString();
			}
			s += "<<<......\n";
		}
		if (matchedValues != null) {
			s += "matchedValues: " + "\n";
			for (String matchedValue : matchedValues) {
				s += "... "+matchedValue+"\n";
			}
		}
		return s;
	}
}
