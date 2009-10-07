package org.xmlcml.cml.converters.marker.regex;

public class Comment {

	public final static String COMMENT_START = "#";
	public final static String COMMENT_REGEX = COMMENT_START+"[^\\)\\n]+\\)";
	public final static String COMMENT_ATT = "comment";
	public final static String COMMENT_END = "\n";

}
