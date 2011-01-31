package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.converters.format.LineReader.LineType;
import org.xmlcml.cml.converters.format.ReadLinesLineReader;
import org.xmlcml.cml.converters.format.RecordsLineReader;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

public class Template {
	

	public static final String CMLX_UNREAD = "cmlx:unread";
	
	private static final String ROWS = "rows";
	private static final String COLUMNS = "columns";
	private final static Logger LOG = Logger.getLogger(Template.class);
	private static final String LOCAL_DICT_REF = "localDictRef";
	private static final String DICT_REF_NAMES = "dictRefNames";
	private static final String FORMAT = "format";

	private static final String SP_STAR = "\\s*";
	private static final String FIELD = "field";
//	private static final String SCALAR = "scalar";
	private static final String PATTERN = "pattern";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String GROUP_L = CMLConstants.S_LBRAK;
	private static final String GROUP_R = CMLConstants.S_RBRAK;
	
	private Element templateElement;
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
	private String formatType;
	private OutputLevel outputLevel;

	public Template(LegacyProcessor legacyProcessor, Element element) {
		this.legacyProcessor = legacyProcessor;
		this.common = legacyProcessor.getCommon();
		this.templateElement = element;
		lineReaderList = new ArrayList<LineReader>();
		processAttributes();
		createLineReadersFromChildElements();
		if (!OutputLevel.NONE.equals(outputLevel)) {
			this.debug();
		}
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

	private void processAttributes() {
		id = templateElement.getAttributeValue(ID);
		checkNotNull(ID, id);
		name = templateElement.getAttributeValue(NAME);
		checkNotNull(NAME, name);
		patternString = templateElement.getAttributeValue(PATTERN);
		if (patternString == null) {
			patternString = GROUP_L+name+GROUP_R;	// add regex brackets to extract name
		} 
		pattern = Pattern.compile(SP_STAR+patternString+SP_STAR);
		outputLevel = Outputter.extractOutputLevel(this.templateElement);
		LOG.trace(outputLevel+"/"+this.templateElement.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			System.out.println("OUTPUT "+outputLevel);
		}
	}


	private void createLineReadersFromChildElements() {
		childElements = templateElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			LineReader lineReader = null;
			Element childElement = (Element) childElements.get(i);
			String name = childElement.getLocalName();
			if (name == null) {
				ignore();
			} else if (LineType.COMMENT.getTag().equals(name)) {
				ignore();
			} else if (LineType.READLINES.getTag().equals(name)) {
				lineReader = new ReadLinesLineReader(childElement, this);
			} else if (LineType.RECORD.getTag().equals(name)) {
				lineReader = new RecordsLineReader(childElement, this);
			} else {
				CMLUtil.debug(templateElement, "UNKNOWN CHILD");
				throw new RuntimeException("unknown child "+name);
			}
			if (lineReader != null) {
				lineReaderList.add(lineReader);
			}
		}
	}

	private void ignore() {
		// TODO Auto-generated method stub
		
	}

	// ========================================================
	
		
	private void checkNotNull(String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(templateElement, "CHECK");
			throw new RuntimeException("Must give "+attName+" attribute");
		}
	}

	public void markupBlock(AbstractBlock block) {
		jumboReader = block.jumboReader;
		CMLModule module = block.makeModule();
		CMLElement lastElement = null;
		for (LineReader lineReader : lineReaderList) {
			try {
				debug("LINE "+jumboReader.peekLine(), OutputLevel.NORMAL);
				lastElement = lineReader.readLinesAndParse(jumboReader);
			} catch (Exception e) {
				e.printStackTrace();
				lineReader.debug();
				System.err.println("CANNOT PARSE BLOCK: "+lineReader+" / "+e+ " ["+jumboReader.peekLine()+"]");
			}
		}
		tidyUnusedLines(jumboReader, lastElement);
	}

	/**
	 * gets remaining lines in block and adds them to parentElement
	 */
	public static void tidyUnusedLines(JumboReader jumboReader, CMLElement parentElement) {
		StringBuilder sb = new StringBuilder();
		boolean empty = true;
		while (jumboReader.hasMoreLines()) {
			String s = jumboReader.readLine();
			if (s.trim().length() != 0) {
				empty = false;
			}
			sb.append(s);
			sb.append(CMLConstants.S_NEWLINE);
		}
		if (!empty) {
			CMLScalar scalar = new CMLScalar(sb.toString());
			scalar.setDictRef(Template.CMLX_UNREAD);
			if (parentElement == null) {
				List<CMLElement> cmlChildElements = jumboReader.getParentElement().getChildCMLElements();
				parentElement = (cmlChildElements.size() == 0) ? jumboReader.getParentElement() : cmlChildElements.get(cmlChildElements.size()-1);
			}
			parentElement.appendChild(scalar);
		}
	}

	public void debug(String string, OutputLevel maxLevel) {
		if (Outputter.canOutput(outputLevel, maxLevel)) {
			LOG.debug(string);
		}
	}
	
	public void debug() {
		System.out.println("========"+this.getId()+"=========");
		CMLUtil.debug(templateElement, "TEMPLATE");
		LOG.debug("linereaders: "+lineReaderList.size());
		for (LineReader lineReader : lineReaderList) {
			lineReader.debug();
		}
		System.out.println("==========================");
	}
	
}
