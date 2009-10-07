package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Util;

public class RegexInterpreter {
	private final static Logger LOG = Logger.getLogger(RegexInterpreter.class);

	private Regex regex;
	// regex doing the matching
	private String regexString = CMLConstants.S_EMPTY;
	// pattern corresponding to regexString
	private Pattern parserRegexPattern;

	public RegexInterpreter(Regex regex) {
		this.regex = regex;
	}

	public String getRegexString() {
		return regexString;
	}

	void setRegexString(String regexString) {
		this.regexString = regexString;
		this.parserRegexPattern = Pattern.compile(regexString);
	}

	public Pattern getParserRegexPattern() {
		return parserRegexPattern;
	}

	void interpretAttributesAndContent() {
		interpretRegexStringAndCreatePattern();
		createCaptureGroups();
	}

	private void interpretRegexStringAndCreatePattern() {
		this.regexString = regex.getXMLElement().getValue();
		if (regexString.split(CMLConstants.S_NEWLINE).length != 1) {
			throw new RuntimeException("primitive regex cannot span lines");
		}
		translateRegexAbbreviations();
		try {
			parserRegexPattern = Pattern.compile(regexString);
		} catch (Exception e) {
			LOG.error("Cannot compile regex: "+regexString);
			throw new RuntimeException("Bad regex", e);
		}
	}

	private void translateRegexAbbreviations() {
		String translate = regex.getXMLElement().getAttributeValue(Marker.TRANSLATE);
		if (Marker.YES.equals(translate)) {
			this.regexString = ParserUtil.translateTemplateToRegex(Util.rightTrim(regexString));
		}
	}
	
	private void createCaptureGroups() {
		List<String> nameList = getNames();
		regex.captureGroupList = regex.createCaptureGroupsAndAddNames(nameList);
		regex.fieldNameList = new FieldNameList();
		for (int i = 0; i < regex.captureGroupList.size(); i++) {
			regex.fieldNameList.add(regex.captureGroupList.get(i).getVariableName());
		}
	}
	
	private List<String> getNames() {
		Element regexElement = regex.getXMLElement();
		List<String> nameList = new ArrayList<String>();
		String names = regexElement.getAttributeValue(Marker.NAMES);
		if (names != null) {
			String[] nn = names.replaceAll(CMLConstants.S_WHITEREGEX, 
					CMLConstants.S_SPACE).trim().split(CMLConstants.S_SPACE);
			for (String name : nn) {
				nameList.add(name);
			}
		} else {
			CMLUtil.debug(regexElement, "regex");
		}
		return nameList;
	}

}
