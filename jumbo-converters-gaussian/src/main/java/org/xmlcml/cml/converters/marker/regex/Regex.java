package org.xmlcml.cml.converters.marker.regex;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

public class Regex extends AbstractRegexElement {
	private static Logger LOG = Logger.getLogger(AbstractRegexElement.class);
	
	public static final String REGEX_TAG = "regex";
	public final static String REGEX_START_TAG = "<"+REGEX_TAG+">";
	public final static String REGEX_END_TAG = "</"+REGEX_TAG+">";

	private Pattern pattern;
	private String regexString;
	private TaggedSequence taggedSequence;

	public Regex() {
		super(REGEX_TAG);
	}

	static Regex parseRegexIntoXML(String regexString) {
		Pattern pattern = Pattern.compile(regexString, Pattern.COMMENTS);
		regexString = Utils.deescapeMetacharacters(regexString);
		regexString = CaptureGroupElement.replaceCaptureGroupStarts(regexString);
		regexString = CaptureGroupElement.replaceCaptureGroupEnds(regexString);
		Regex regexXML = parseTaggedStringIntoXML(regexString);
		regexXML.pattern = pattern;
		AbstractRegexElement.createSubclasses(regexXML);
		regexXML.tidyQuantifiers();
		regexXML.tidyCountExpressions();
		regexXML.addNumericRegexToEndTagToCaptureSerialNumbersInInput();
		regexXML.extractCaptureGroupContentIntoAttributes();
		regexXML.createCommentsFromTags();
		regexXML.createChoiceChildren();
		return regexXML;
	}

	private void createChoiceChildren() {
		Nodes captures = this.query("//"+CaptureGroupElement.CAPTURE_GROUP_TAG);
		for (int i = 0; i < captures.size(); i++) {
			CaptureGroupElement captureGroupElement = (CaptureGroupElement) captures.get(i);
			captureGroupElement.createChoiceChildren();
		}
	}

	private static Regex parseTaggedStringIntoXML(String regexString) {
		Regex regexXML = null;
		try {
			regexXML = new Regex();
			regexXML.regexString = regexString;
			Element foo =  new Builder().build(new StringReader(
					Regex.REGEX_START_TAG+regexString+Regex.REGEX_END_TAG)).getRootElement();
			CMLElement.copyAttributesFromTo(foo, regexXML);
			CMLUtil.transferChildren(foo, regexXML);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse regexXML", e);
		}
		return regexXML;
	}

	private void addNumericRegexToEndTagToCaptureSerialNumbersInInput() {
		regexString = regexString.replaceAll(Tag.ETAG, Tag.ETAG+Tag.WORD_CAPTURE);
	}
	
	public boolean match(TaggedSequence taggedSequence) {
		this.taggedSequence = taggedSequence;
		String targetString = taggedSequence.getStringOfTags();
		Matcher matcher = pattern.matcher(targetString);
		List<String> groupList = new ArrayList<String>();
		boolean matches = matcher.matches();
		int count = 0;
		if (matches) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				String group = matcher.group(i);
				groupList.add(group);
				
			}
			this.addMatchesForDescendantTags(groupList);
			this.debug("REGEX");
			count = this.processOptionalCounts();
//			this.removeCapturesAndText();
		} else {
			LOG.error(targetString+"failed to match\n"+pattern);
		}
		if (count != taggedSequence.size()) {
			LOG.error("count ("+count+") should match taggedSequence ("+taggedSequence.size()+")");
		}
		return matches;
	}
	
	protected void addMatchesForDescendantTags(List<String> groupList) {
		this.debug("AFTER PARSE");
		String xpath = "//"+CaptureGroupElement.CAPTURE_GROUP_TAG+"[not (ancestor::"+Choice.CHOICE_TAG+")]";
		LOG.debug("................."+xpath);
		Nodes brackets = query(xpath);
		for (int i = 0; i < brackets.size(); i++) {
			String captureGroup = groupList.get(i+1);
			if (captureGroup == null) {
				ShallowRegexParser.LOG.error("Null capture group: "+i);
				captureGroup = "DUMMY";
			}
			Element bracket = ((Element)brackets.get(i));
			bracket.addAttribute(new Attribute(AbstractRegexElement.GROUP_ATT, captureGroup));
			Pattern pattern = Pattern.compile(Tag.TAG_SERIAL);
			this.extractCommentAttributeForLeafNodes(captureGroup, bracket, pattern);
			this.taggedSequence.extractContentAttribute(captureGroup, bracket, pattern);
		}
	}

	private void addMatches(List<String> groupList) {
		this.addMatchesRecursively(groupList, 0);
	}

	protected void addMatchesRecursively(List<String> groupList, int i) {
		this.addAttribute(new Attribute(AbstractRegexElement.TRANSLATED_ATT, groupList.get(i++)));
		for (int j = 0; j < this.getChildElements().size(); j++) {
			AbstractRegexElement abstractRegexElement = (AbstractRegexElement) this.getChildElements().get(j);
			abstractRegexElement.addMatchesRecursively(groupList, i++);
		}
	}

	int processOptionalCounts() {
		String captureGroup = this.getAttributeValue(AbstractRegexElement.GROUP_ATT);
		LOG.debug(captureGroup);
		if (captureGroup == null) {
			throw new RuntimeException("missing captureGroup");
		}
		TaggedSequence captureSequence = TaggedSequence.createCaptureSequence(captureGroup);
		LOG.debug(captureSequence);

		int counter = 0;
		Elements childElements = this.getChildElements(CaptureGroupElement.CAPTURE_GROUP_TAG);
		for (int i = 0; i < childElements.size(); i++) {
			AbstractRegexElement childElement = (AbstractRegexElement) childElements.get(i);
			String comment = childElement.getComment();
			counter = childElement.processOptionalCounts(counter);
		}
		return counter;
	}


	public Pattern getPattern() {
		return pattern;
	}

	public String getRegexString() {
		return regexString;
	}

	public TaggedSequence getTaggedSequence() {
		return taggedSequence;
	}

	int processOptionalCounts(int counter) {
		LOG.debug("Ignoring capture group for top regex (group 0)");
		super.processOptionalCounts(counter);
		return counter+1;
	}
	
}
