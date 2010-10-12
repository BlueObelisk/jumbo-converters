package org.xmlcml.cml.converters.marker.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {

	public final static String STAG = "@";
	public final static String ETAG = "~";
	
	public final static String WORD_CAPTURE = "[^"+STAG+"]+";
	public final static String TAG_CAPTURE =  STAG+"[^"+ETAG+"]+";
	public final static String TAG_SERIAL = "@([^~]+)~(\\d+)";
	public final static String CAPTURE_TAG = "[^"+STAG+"]*"+STAG+"([^"+ETAG+"]+)"+ETAG;
	public final static String CAPTURE_REGEX = ""+STAG+"([^"+ETAG+"]+)"+ETAG+"([^"+STAG+"]*)";

	private String tagString;
	private Integer integer;

	public Tag(String tagString) {
		this.tagString = tagString;
	}

	public Tag(String tagString, int i) {
		this.tagString = tagString;
		this.integer = i;
	}

	public static Tag createTagWithSerial(String s) {
		Tag newTag = null;
		Pattern pattern = Pattern.compile(TAG_SERIAL);
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
			newTag = new Tag(matcher.group(1), new Integer(matcher.group(2)));
		}
		return newTag;
	}
	
	public String getTagString() {
		return tagString;
	}

	public Integer getInteger() {
		return integer;
	}
	
	public String getMarkedTag() {
		return STAG+tagString+ETAG;
	}
	
	public String getMarkedTagWithSerial() {
		return getMarkedTag()+integer;
	}
	
	public String createMatchingRegex() {
		return Tag.STAG+tagString+Tag.ETAG+"\\d+";
	}

	public void setTagString(String tagString) {
		this.tagString = tagString;
	}

	public void setInteger(Integer integer) {
		this.integer = integer;
	}
}
