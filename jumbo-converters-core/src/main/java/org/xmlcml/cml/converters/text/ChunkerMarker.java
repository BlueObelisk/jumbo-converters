package org.xmlcml.cml.converters.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * manages strategy for marking text documents
 * @author pm286
 *
 */
public class ChunkerMarker {
	
	public static final String GROUP = "group";
	public static final String LINE = "line";
	public static final String MARKUP = "markup";
	public static final String MARK = "mark";
	public static final String REGEX = "regex";
	
	private String regex;
	private String mark;
	private Pattern pattern;
	private Matcher matcher;
	
	public ChunkerMarker() {
		
	}
	public ChunkerMarker(Element childElement) {
		this();
		setRegex(childElement.getAttributeValue(REGEX));
		mark = childElement.getAttributeValue(MARK);
		if (regex == null || mark == null) {
			throw new RuntimeException("must give regex and mark");
		}
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
		pattern = Pattern.compile(regex);
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public boolean matches(String line) {
		matcher = pattern.matcher(line);
		return matcher.matches();
	}
	public String getMarkup(String line) {
		Element element = new Element(MARKUP);
		element.addAttribute(new Attribute(MARK, mark));
		element.addAttribute(new Attribute(LINE, line));
		for (int i = 0; i < matcher.groupCount(); i++) {
			element.addAttribute(new Attribute(GROUP+(i+1), matcher.group(i+1)));
		}
		return element.toXML();
	}
}
