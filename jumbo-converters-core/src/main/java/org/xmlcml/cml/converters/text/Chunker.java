package org.xmlcml.cml.converters.text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * manages strategy for marking text documents
 * @author pm286
 *
 */
public class Chunker {


	private final static Logger LOG = Logger.getLogger(Chunker.class);
	
	private static final String DELETE = "delete";
	public static final String MARK = "mark";
	private static final String MARKUP = "markup";
	private static final String MULTIPLE = "multiple";
	static final String OFFSET = "offset";
	public static final String REGEX = "regex";
	private static final String Y = "y";

//	private static final String NEWLINE_S = CMLConstants.S_DOLLAR;
	
	private String regexS;
	private Pattern[] patterns;
	private String[] regexes;
	private String mark;
	private int offset = 0;
	private String multiple;
	private boolean delete;
	
	public Chunker() {
		
	}
	public Chunker(Element childElement) {
		this();
		multiple = childElement.getAttributeValue(MULTIPLE);
		setRegex(childElement.getAttributeValue(REGEX));
		mark = childElement.getAttributeValue(MARK);
		if (regexes == null || mark == null) {
			throw new RuntimeException("must give regex and mark");
		}
		String offsetS = childElement.getAttributeValue(OFFSET);
		offset = (offsetS == null) ? 0 : Integer.parseInt(offsetS);
		delete = (Y.equals(childElement.getAttributeValue(DELETE)));
		offset = (offsetS == null) ? 0 : Integer.parseInt(offsetS);

	}
	public String getRegex() {
		return regexS;
	}
	public void setRegex(String regex) {
		this.regexS = regex;
		regexes = (multiple != null) ? regexS.split(multiple) : new String[]{regexS};
		patterns = new Pattern[regexes.length];
		for (int i = 0; i < regexes.length; i++) {
			patterns[i] = Pattern.compile(regexes[i]);
			LOG.trace("pattern "+patterns[i]);
		}
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public Pattern[] getPatterns() {
		return patterns;
	}
	public String createLineOfEmptyMarkup(int offset) {
		Element element = new Element(MARKUP);
		element.addAttribute(new Attribute(MARK, mark));
		element.addAttribute(new Attribute(OFFSET, ""+offset));
		return element.toXML();
	}
	public int getOffset() {
		return offset;
	}
	
	public void insertMarkupLine(int lineCount, List<String> lines, List<String> linesCopy) {
		int offset = this.getOffset();
		String markup = this.createLineOfEmptyMarkup(offset);
		LOG.debug("Markup: ["+markup+"]");
		linesCopy.add(linesCopy.size()+Math.min(0, offset), markup);
	}

	public int insertMatchedLineAndReturnCount(int lineCount, List<String> lines, List<String> linesCopy) {
		Pattern[] linePatterns = this.getPatterns();
		boolean matched = true;
		for (int i = 0; i < linePatterns.length; i++) {
			// ran off end
			if (i + lineCount >= lines.size()) {
				matched = false;
				break;
			}
			// do all lines match?
			Matcher matcher = linePatterns[i].matcher(lines.get(lineCount+i));
			if (!matcher.matches()) {
				matched = false;	// no
				break;
			}
		}
		int linesMatched = 0;
		if (matched) {
			if (delete) {
				// negative means delete
				linesMatched = -linePatterns.length;				
			} else {
				this.insertMarkupLine(lineCount, lines, linesCopy);
				linesMatched = linePatterns.length;				
			}
		}
		return linesMatched;
	}


	public String toString() {
		String s = 	"";
		s += "regex: "+regexS;
		s += "; multiple: "+multiple;
		s += "; mark: "+mark;
		s += "; patterns: "+patterns;
		s += "; offset: "+offset;
		return s;
	}
}
