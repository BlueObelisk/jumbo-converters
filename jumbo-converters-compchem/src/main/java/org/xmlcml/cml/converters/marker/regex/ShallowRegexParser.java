package org.xmlcml.cml.converters.marker.regex;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class ShallowRegexParser {
	final static Logger LOG = Logger.getLogger(ShallowRegexParser.class);

	Regex regexXML;
	TaggedSequence taggedSequence;

	public ShallowRegexParser(String regexString) {
		regexXML = Regex.parseRegexIntoXML(regexString);
	}

	public ShallowRegexParser(Regex regex) {
		this.regexXML = regex;
	}
	
	public void debug(String string) {
		CMLUtil.debug(regexXML, string);
	}

	public AbstractRegexElement getRegexElement() {
		return regexXML;
	}


	public boolean match(TaggedSequence taggedSequence) {
		return regexXML.match(taggedSequence);
	}

	public Pattern getPattern() {
		return regexXML.getPattern();
	}

	public static ShallowRegexParser addTagMarkupAndCreateParser(String regex) {
		regex = TaggedString.markupTagsAndAddSerialRegex(regex);
		return new ShallowRegexParser(regex);
	}
}
