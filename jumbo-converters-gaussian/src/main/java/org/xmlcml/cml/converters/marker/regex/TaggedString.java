package org.xmlcml.cml.converters.marker.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class TaggedString {
	private static Logger LOG = Logger.getLogger(TaggedString.class);
	
	private String rawText;
	private Tag tag;
	
	public TaggedString(String tagString, String text) {
		rawText = text;
		this.tag = new Tag(tagString);
	}
	
	private void noOp() {
	}

	public String getText() {
		return rawText;
	}
	
	public Tag getTag() {
		return tag;
	}

	public String toString() {
		return tag.getMarkedTag()+rawText;
	}
	static String markupTagsAndAddSerialRegex(String regex) {
		
		Pattern pattern = Pattern.compile(CaptureGroupElement.MARKUP_TAG);
		int start = 0;
		while (true) {
			Matcher matcher = pattern.matcher(regex);
			if (!matcher.find()) {
				break;
			}
			regex = markNextTag(regex, matcher);
		}
		return regex;
	}

	private static String markNextTag(String regex, Matcher matcher) {
		int start;
		String group1 = matcher.group(1);
//		int lastGroupNumber = matcher.groupCount();
//		String lastGroupS = matcher.group(lastGroupNumber-1);
		String groups = matcher.group(2);
		StringBuilder sb = new StringBuilder();
		int s0 = matcher.start();
		if (group1 != null) {
			s0 += CaptureGroupElement.CAPTURE_DISABLE.length();
		}
		int s1 = matcher.end();
		sb.append(regex.substring(0, s0+1));
		String[] groupArray = groups.split(CaptureGroupElement.ESCAPED_OR_CHAR);
		for (int i = 0; i < groupArray.length; i++) {
			if (i > 0) {
				sb.append(CaptureGroupElement.OR_CHAR);
			}
			sb.append((new Tag(groupArray[i])).createMatchingRegex());
		}
		sb.append(")");
		sb.append(regex.substring(s1));
		regex = sb.toString();
		start = s1;
		return regex;
	}

}
