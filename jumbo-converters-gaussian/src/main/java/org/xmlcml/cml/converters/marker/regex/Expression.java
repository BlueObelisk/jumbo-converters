package org.xmlcml.cml.converters.marker.regex;


public class Expression extends AbstractRegexElement {
	public final static String EXPRESSION_TAG = "expression";
	public final static String QUEST_REGEX = "?";
	public final static String QUEST_COUNT = "{0,1}";
	public final static String STAR_REGEX = "*";
	public final static String STAR_COUNT = "{0,}";
	public final static String PLUS_REGEX = "+";
	public final static String PLUS_COUNT = "{1,}";
	public final static String DEFAULT_COUNT = "{1,1}";
	public final static String QUANTIFIER_EXPRESSION = "\\{(\\d+)(,(\\d+))?\\}";
	public final static String TRAILING_QUANTIFIER_EXPRESSION_REGEX = "(\\)"+QUANTIFIER_EXPRESSION+")";

	public Expression() {
		super(EXPRESSION_TAG);
	}

}
