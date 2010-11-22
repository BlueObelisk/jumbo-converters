package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.LineReader.LineType;
import org.xmlcml.cml.converters.util.JumboReader;

public class Template {
	private static final String DICT_REF_NAMES = "dictRefNames";

	private final static Logger LOG = Logger.getLogger(Template.class);

	private static final String SP_STAR = "\\s*";
	private static final String FIELD = "field";
//	private static final String SCALAR = "scalar";
	private static final String PATTERN = "pattern";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String GROUP_L = CMLConstants.S_LBRAK;
	private static final String GROUP_R = CMLConstants.S_RBRAK;
	
	private Element element;
	private String id;
	private String name;
	private String patternString;
	private Pattern pattern;
	private Elements childElements;
	private LegacyProcessor legacyProcessor;
	private AbstractCommon common;
	private JumboReader jumboReader;
	private List<LineReader> lineReaderList;
	private String[] dictRefNames;

	public Template(LegacyProcessor legacyProcessor, Element element) {
		this.legacyProcessor = legacyProcessor;
		this.common = legacyProcessor.getCommon();
		this.element = element;
		lineReaderList = new ArrayList<LineReader>();
		processAttributes();
		processChildren();
	}

	private void processChildren() {
		childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = (Element) childElements.get(i);
			String name = childElement.getLocalName();
			if (false) {
			} else if (LineType.MATRIX.getTag().equals(name)) {
				processMatrix(childElement);
			} else if (LineType.NAMEVALUES.getTag().equals(name)) {
				processNameValues(childElement);
			} else if (LineType.READLINES.getTag().equals(name)) {
				processReadLines(childElement);
			} else if (LineType.SCALAR.getTag().equals(name)) {
				processScalar(childElement);
//				CMLUtil.debug(childElement, "CCCCCC");
			} else {
				CMLUtil.debug(element, "EEEEEE");
				throw new RuntimeException("unknown child "+name);
			}
		}
	}

	private void processMatrix(Element element) {
		//
	}

	private void processNameValues(Element element) {
		//
	}

	private void processReadLines(Element element) {
		//
	}

	private void processScalar(Element scalarElement) {
		String dictRefNameString = scalarElement.getAttributeValue(DICT_REF_NAMES);
		if (dictRefNameString == null) {
			throw new RuntimeException("scalar must have dictRefNames");
		}
		dictRefNameString = dictRefNameString.trim();
		dictRefNames = dictRefNameString.split(CMLConstants.S_WHITEREGEX);
		String[] lines = scalarElement.getValue().split(CMLConstants.S_NEWLINE);
		int lineCount = 0;
		int scalarCount = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() == 0 && i == 0) {
				continue;	// skip first empty line
			}
			if (line.contains(LineReader.T_FLAG)) {
				saveReadLinesReader(lineCount);
				lineCount = 0;
				int fieldCount = createScalarLineReader(line, scalarCount);
				scalarCount += fieldCount;
			} else {
				lineCount++;
			}
		}
		if (scalarCount != dictRefNames.length) {
			CMLUtil.debug(element, "SCALLLLLLLLAR");
			throw new RuntimeException("scalarCount ("+scalarCount+") != dictRefNames ("+dictRefNames.length+")");
		}
	}

	private int createScalarLineReader(String line, int count) {
		LineReader lineReader = createAndAddLineReader(line);
		lineReader.setType(LineType.SCALAR);
		int fieldCount = lineReader.processLine(dictRefNames, count);
		return fieldCount;
	}

	private void saveReadLinesReader(int lineCount) {
		LineReader lineReader = createAndAddLineReader("");
		lineReader.setLinesToRead(lineCount);
		lineReader.setType(LineType.READLINES);
	}

	private LineReader createAndAddLineReader(String line) {
		LineReader lineReader = new LineReader(line);
		lineReaderList.add(lineReader);
		return lineReader;
	}
	
	private void processAttributes() {
		id = element.getAttributeValue(ID);
		checkNotNull(ID, id);
		name = element.getAttributeValue(NAME);
		checkNotNull(NAME, name);
		patternString = element.getAttributeValue(PATTERN);
		if (patternString == null) {
			patternString = GROUP_L+name+GROUP_R;	// add regex brackets to extract name
		} 
		pattern = Pattern.compile(SP_STAR+patternString+SP_STAR);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Pattern getPattern() {
		return pattern;
	}

	private void checkNotNull(String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(element, "CHECK");
			throw new RuntimeException("Must give "+attName+" attribute");
		}
	}

	public void process(AbstractBlock block) {
		block.makeModule();
		for (LineReader lineReader : lineReaderList) {
			lineReader.process(block.jumboReader);
		}
	}

	public void debug() {
		CMLUtil.debug(element, "TEMPLATE");
	}
	
}
