package org.xmlcml.cml.converters.marker.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

public class TaggedSequence {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(TaggedSequence.class);
	
	private List<TaggedString> taggedStringList = new ArrayList<TaggedString>();
	public TaggedSequence() {
		
	}
	
	public void add(TaggedString ts) {
		if (ts == null) {
			throw new RuntimeException("null tagged string");
		}
		taggedStringList.add(ts);
		ts.getTag().setInteger(taggedStringList.size());
	}
	
	public void add(List<String> tagList, List<String> textList) {
		if (tagList == null || textList == null) {
			throw new RuntimeException("null arguments");
		}
		if (tagList.size() != textList.size()) {
			throw new RuntimeException("unequal arrays");
		}
		for (int i = 0; i < tagList.size(); i++) {
			add(new TaggedString(tagList.get(i), textList.get(i)));
		}
	}

	public String getStringOfTags() {
		String s = "";
		if (taggedStringList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < taggedStringList.size(); i++) {
				TaggedString ts = taggedStringList.get(i);
				Tag tag = ts.getTag();
				sb.append(tag.getMarkedTag()+(i+1));
			}
			s = sb.toString();
		}
		return s;
	}

	public TaggedString get(int i) {
		if (i < 0 || i >= taggedStringList.size()) {
			throw new RuntimeException("bad index for taggedList "+i);
		}
		return taggedStringList.get(i);
	}

	public int size() {
		return (taggedStringList == null) ? 0 : taggedStringList.size();
	}

	public static TaggedSequence createCaptureSequence(String captureGroup) {
		TaggedSequence sequence = new TaggedSequence();
		Pattern pattern = Pattern.compile(Tag.CAPTURE_REGEX);
		Matcher matcher = pattern.matcher(captureGroup);
		int start = 0;
		while (matcher.find(start)) {
			String tag = matcher.group(1);
			String value = matcher.group(2);
			start = matcher.end();
			TaggedString taggedString = new TaggedString(tag, value);
			sequence.add(taggedString);
		}
		if (start != captureGroup.length()) {
			throw new RuntimeException("Cannot match after position "+start+" in: "+captureGroup);
		}
		return sequence;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (TaggedString taggedString : taggedStringList) {
			sb.append("["+taggedString.getTag()+","+taggedString.getText()+"]");
		}
		sb.append("}");
		return sb.toString();
	}

	void extractContentAttribute(String captureGroup, Element bracket, Pattern pattern) {
		Matcher matcher = pattern.matcher(captureGroup);
		int start = 0;
		StringBuilder sb = new StringBuilder();
		int serial = 0;
		while (matcher.find(start)) {
			serial = Integer.parseInt(matcher.group(2));
			start = matcher.end();
			sb.append(get(serial-1).getText());
			sb.append(" ");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		} else {
			sb = new StringBuilder("DUMMY");
		}
		bracket.addAttribute(new Attribute(AbstractRegexElement.TRANSLATED_ATT, sb.toString()));
	}
	
}
