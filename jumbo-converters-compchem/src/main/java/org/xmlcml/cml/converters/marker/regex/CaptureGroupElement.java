package org.xmlcml.cml.converters.marker.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

public class CaptureGroupElement extends AbstractRegexElement {
	private static Logger LOG = Logger.getLogger(CaptureGroupElement.class);
	
	public final static String CAPTURE_GROUP_TAG = "capture";
	public final static String CAPTURE_GROUP_START_TAG = "<"+CAPTURE_GROUP_TAG+">";
	public final static String CAPTURE_GROUP_END_TAG = "</"+CAPTURE_GROUP_TAG+">";

	public final static String CAPTURE_START_CHAR = "(";
	public final static String CAPTURE_END_CHAR = ")";
	public final static String CAPTURE_ATT = "captureZ";
	public final static String CAPTURE_DISABLE = "?:";
	public final static String CAPTURE_DISABLE_REGEX = "\\?\\:";
	public final static String CAPTURE_NO = "no";
	public final static String CAPTURE_YES = "yes";
	public final static String GROUP_START_REGEX = "\\((\\?\\:)?";
	public final static String GROUP_END_REGEX = "\\)([\\?\\*\\+]|\\{\\d+(,\\d+)?\\})?";
	public final static String MARKUP_TAG = "\\(("+CAPTURE_DISABLE_REGEX+")?([A-Z\\-]+(\\|[A-Z\\-]+)*)\\)";

	public final static String ESCAPED_OR_CHAR = "\\|";
	public final static String OR_CHAR = "|";

	public CaptureGroupElement() {
		super(CaptureGroupElement.CAPTURE_GROUP_TAG);
	}
	
	
	public CaptureGroupElement(Element element) {
		this();
		CMLElement.copyAttributesFromTo(element, this);
		CMLElement.copyChildrenFromTo(element, this);
		CMLUtil.debug(element, "bracket");
	}
	
	static String replaceCaptureGroupStarts(String regex) {
		Pattern pattern = Pattern.compile(CaptureGroupElement.GROUP_START_REGEX);
		int start = 0;
		while (true) {
			StringBuilder sb = new StringBuilder();
			Matcher matcher = pattern.matcher(regex);
			if (matcher.find(start)) {
				int s0 = matcher.start();
				int s1 = matcher.end();
				sb.append(regex.substring(0, s0));
				sb.append(CaptureGroupElement.CAPTURE_GROUP_START_TAG);
				if (matcher.group(1) != null) {
					String quantifier = matcher.group(1).trim();
					sb.append("<"+Quantifier.QUANTIFIER_TAG+">"+quantifier+"</"+Quantifier.QUANTIFIER_TAG+">");
				}
				sb.append(regex.substring(s1));
				regex = sb.toString();
			} else {
				break;
			}
		}
		return regex;
	}

	static String replaceCaptureGroupEnds(String regex) {
		Pattern pattern = Pattern.compile(CaptureGroupElement.GROUP_END_REGEX);
		int start = 0;
		while (true) {
			StringBuilder sb = new StringBuilder();
			Matcher matcher = pattern.matcher(regex);
			if (matcher.find(start)) {
				int s0 = matcher.start();
				int s1 = matcher.end();
				sb.append(regex.substring(0, s0));
				if (matcher.groupCount() > 1) {
					if(matcher.group(1) != null) {
						String captureExpression = matcher.group(1).trim();
						sb.append("<"+Expression.EXPRESSION_TAG+">"+captureExpression+"</"+Expression.EXPRESSION_TAG+">");
					}
				}
				sb.append(CaptureGroupElement.CAPTURE_GROUP_END_TAG);				
				sb.append(regex.substring(s1));
				regex = sb.toString();
			} else {
				break;
			}
		}
		return regex;
	}

	public static String extractOptionalMaxCount(String max, Matcher matcher) {
		if (matcher.groupCount() == 3) {
			if (matcher.group(2) != null) {
				max = matcher.group(3);
				if (max == null) {
					max = ANY;
				}
			}
		}
		return max;
	}

	@Override
	int processOptionalCounts(int counter) {
		this.debug("CAPT");
		Elements childElements = this.getChildElements(CaptureGroupElement.CAPTURE_GROUP_TAG);
		for (int i = 0; i < childElements.size(); i++) {
			AbstractRegexElement childElement = (AbstractRegexElement) childElements.get(i);
//			String comment = childElement.getComment();
			if (childElement.query("*").size() > 0) {
				counter = childElement.processOptionalCounts(counter);
			} else {
				TaggedSequence taggedSequence = getTaggedSequence();
				TaggedString taggedString = taggedSequence.get(counter);
				LOG.debug("........."+taggedString);
				Integer tagCount = taggedString.getTag().getInteger();
				String group = childElement.getGroup();
				LOG.debug("........."+group);
				String tagString = childElement.getComment();
				Tag lastCapturedTag = Tag.createTagWithSerial(group);
				LOG.debug("........."+lastCapturedTag);
				if (lastCapturedTag == null) {
					counter++;
					continue;
				}
				Integer lastCapturedTagCount = lastCapturedTag.getInteger();
//				System.out.println(taggedString + "/"+childElement.getGroup()+"/"+lastCapturedTag.getTagString()+"/"+lastCapturedTagCount+"/"+counter+"/..."+tagCount);
				while (tagCount < lastCapturedTagCount) {
					TaggedString skipString = taggedSequence.get(tagCount-1);
					String skipTagString = skipString.getTag().getTagString();
					if (!skipTagString.equals(tagString)) {
						LOG.info("skipped tag ("+skipTagString+") should equal "+tagString);
						break;
					}
					System.out.println("Skipped: "+tagCount+"/"+skipString+"/"+skipTagString);
					createAddSkippedGroupAjustAttributes(childElement,
							tagCount, tagString, skipString);
					tagCount++;
					counter++;
				}
				counter++;
			}
		}
		return counter;
	}


	private void createAddSkippedGroupAjustAttributes(
			AbstractRegexElement childElement, Integer tagCount,
			String tagString, TaggedString skipString) {
		CaptureGroupElement newGroup = new CaptureGroupElement();
		CMLElement.copyAttributesFromTo(childElement, newGroup);
		newGroup.setTranslated(skipString.getText());
		newGroup.setGroup(new Tag(tagString, tagCount).getMarkedTagWithSerial());
		int pos  = this.indexOf(childElement);
		this.insertChild(newGroup, pos);
	}


	public void createChoiceChildren() {
		String group = this.getGroup();
		String[] groups = group.split(CaptureGroupElement.ESCAPED_OR_CHAR);
		if (groups.length > 1) {
			Choice option = new Choice();
			this.appendChild(option);
			for (int i = 0; i < groups.length;i++) {
				CaptureGroupElement captureGroupElement = new CaptureGroupElement();
				String newGroup = groups[i].trim();
				captureGroupElement.setGroup(newGroup);
				captureGroupElement.appendChild(newGroup);
				option.appendChild(captureGroupElement);
			}
		}
	}

}
