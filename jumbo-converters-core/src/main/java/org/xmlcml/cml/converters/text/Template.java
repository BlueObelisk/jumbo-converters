package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.converters.format.LineReader.LineType;
import org.xmlcml.cml.converters.format.ReadLinesLineReader;
import org.xmlcml.cml.converters.format.RecordsLineReader;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

public class Template {
	
//	private static final String BLOCK = "block";

	private final static Logger LOG = Logger.getLogger(Template.class);

	public static final String CMLX_UNREAD = "cmlx:unread";
	private static final String ID = "id";
	private static final String MULTIPLE = "multiple";
	private static final String NAME = "name";
	private static final String PATTERN = "pattern";
	private static final String END_PATTERN = "endPattern";
//	private static final String SP_STAR = "\\s*";
	public static final String TAG = "template";

	private static final String OFFSET = "offset";
	private static final String END_OFFSET = "endOffset";
	
	protected Element theElement;
	private String id;
	private String name;
	private String multipleS;
	private String patternString;
	private String endPatternString;
	private PatternChunker endChunker;
	private PatternChunker startChunker;
	private Integer offset;
	private Integer endOffset;
	
	private int maxRepeatCount = Integer.MAX_VALUE;
	
	private JumboReader jumboReader;
	private OutputLevel outputLevel;

	private List<Deleter> deleterList;
	private TemplateContainer templateContainer;
	private List<LineReader> lineReaderList;

	private LineContainer lineContainer;

	public Template(Element element) {
		this.theElement = element;
		processChildElementsAndAttributes();
	}

	private void processChildElementsAndAttributes() {
		deleterList = new ArrayList<Deleter>();
		lineReaderList = new ArrayList<LineReader>();
		processAttributes();
		createSubclassedElementsFromChildElements();
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

	public PatternChunker getEndChunker() {
		return endChunker;
	}

	public PatternChunker getStartChunker() {
		return startChunker;
	}

	@Deprecated // will change to list structure
	public Pattern getPattern() {
		return (startChunker == null || startChunker.size() == 0) ? null : startChunker.get(0);
	}

	public List<LineReader> getLineReaderList() {
		return lineReaderList;
	}

	public List<Deleter> getDeleterList() {
		return deleterList;
	}

	public TemplateContainer getTemplateContainer() {
		return templateContainer;
	}

	public OutputLevel getOutputLevel() {
		return outputLevel;
	}
	
	public LineContainer getLineContainer() {
		return lineContainer;
	}

	private void processAttributes() {
		id = theElement.getAttributeValue(ID);
		checkNotNull(theElement, ID, id);
		name = theElement.getAttributeValue(NAME);
		checkNotNull(theElement, NAME, name);
		
		patternString = theElement.getAttributeValue(PATTERN);
		checkNotNull(theElement, PATTERN, patternString);
		endPatternString = theElement.getAttributeValue(END_PATTERN);
		if (endPatternString == null) {
			endPatternString = patternString;
		}
		multipleS = theElement.getAttributeValue(MULTIPLE);
		
		outputLevel = Outputter.extractOutputLevel(this.theElement);
		LOG.trace(outputLevel+"/"+this.theElement.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			System.out.println("OUTPUT "+outputLevel);
		}
		String offsetS = theElement.getAttributeValue(OFFSET);
		offset = (offsetS == null) ? 0 : new Integer(offsetS);
		offsetS = theElement.getAttributeValue(END_OFFSET);
		endOffset = (offsetS == null) ? offset : new Integer(offsetS);
		
		startChunker = new PatternChunker(createPatternList(patternString, multipleS), offset);
		endChunker = new PatternChunker(createPatternList(endPatternString, multipleS), endOffset);
	}

	/** used by Deleter - needs refactoring */
	public static List<Pattern> processMultipleAndPatternAttributes(Element element) {
		String patternS = element.getAttributeValue(PATTERN);
		String multipleS = element.getAttributeValue(MULTIPLE);
		return createPatternList(patternS, multipleS);
	}

	static List<Pattern> createPatternList(String patternS,
			String multipleS) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		if (patternS != null) {
			String[] pat = (multipleS == null) ? new String[]{patternS} : patternS.split(multipleS); 
			for (int i = 0; i < pat.length; i++) {
				patterns.add(Pattern.compile(pat[i]));
			}
		}
		return patterns;
	}


	private void createSubclassedElementsFromChildElements() {
		Elements childElements = theElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			LineReader lineReader = null;
			Element childElement = (Element) childElements.get(i);
			String name = childElement.getLocalName();
			if (name == null) {
				ignore();
			} else if (LineType.COMMENT.getTag().equals(name)) {
				ignore();
			} else if (Deleter.TAG.equals(name)) {
				Deleter deleter = new Deleter(childElement);
				deleterList.add(deleter);
			} else if (LineType.READLINES.getTag().equals(name)) {
				lineReader = new ReadLinesLineReader(childElement, this);
				lineReaderList.add(lineReader);
			} else if (LineType.RECORD.getTag().equals(name)) {
				lineReader = new RecordsLineReader(childElement, this);
				lineReaderList.add(lineReader);
			} else if (TemplateContainer.TAG.equals(name)) {
				if (templateContainer != null) {
					throw new RuntimeException("Only one templateList allowed");
				}
				templateContainer = new TemplateContainer(childElement);
			} else {
				CMLUtil.debug(theElement, "UNKNOWN CHILD");
				throw new RuntimeException("unknown child "+name);
			}
		}
		if (lineReaderList.size() > 0 &&
		   (deleterList.size() > 0 || templateContainer != null)) {
				throw new RuntimeException(
						"Cannot have both deleters+templateList and readLines+record");
		}
	}

	private void ignore() {
		// TODO Auto-generated method stub
		
	}

	// ========================================================
	
		
	private static void checkNotNull(Element element, String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(element, "CHECK");
			throw new RuntimeException(element.getLocalName()+": must give "+attName+" attribute");
		}
	}
	
	// =========================================================

	public void applyMarkup(String line) {
		lineContainer = new LineContainer(line);
		applyMarkup(lineContainer);
	}
	
	public void applyMarkup(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		applyDeleters();
		applyChildTemplateList();
		applyLineReaders();
	}
	
	private void applyDeleters() {
		for (Deleter deleter : deleterList) {
			deleter.deleteLines(lineContainer, Integer.MAX_VALUE);
		}
	}

	private void applyChildTemplateList() {
		if (templateContainer != null) {
			LOG.debug(id+" applying childTemplates");
			templateContainer.apply(lineContainer);
		}
	}

	private void applyLineReaders() {
		for (LineReader lineReader : lineReaderList) {
			lineReader.apply(lineContainer);
		}
	}

	public void markupBlock(AbstractBlock block) {
		jumboReader = block.getJumboReader();
		@SuppressWarnings("unused")
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
		CMLUtil.debug(theElement, "TEMPLATE");
		LOG.debug("linereaders: "+lineReaderList.size());
		for (LineReader lineReader : lineReaderList) {
			lineReader.debug();
		}
/*
	private Element templateElement;
	private String id;
	private String name;
	private String multipleS;
	private String patternString;
	private String endPatternString;
	private PatternChunker endChunker;
	private PatternChunker startChunker;
	private Integer offset;
	private Integer endOffset;
	
	private int maxRepeatCount = Integer.MAX_VALUE;
	
	private JumboReader jumboReader;
	private OutputLevel outputLevel;

	private List<Deleter> deleterList;
	private TemplateContainer templateContainer;
	private List<LineReader> lineReaderList;

	private LineContainer lineContainer;
 */
		System.out.println("startChunker "+startChunker.toString());
		System.out.println("endChunker "+endChunker.toString());
		System.out.println("==========================");
	}

	public List<Element> applyChunkers(LineContainer lineContainer) {
		List<Element> elements = lineContainer.applyChunkers(maxRepeatCount, startChunker, endChunker, id);
		return elements;
	}
	
	public String toString() {
		String s = "";
		s += "startChunker "+startChunker.toString()+"\n";
		s += "endChunker "+endChunker.toString()+"\n";
		return s;
	}
	
//	private void getMatchWithEndPatterOrRestOfLines(
//			LineContainer lineContainer, int nodeIndex) {
//		Int2 end;
//		end = lineContainer.matchLines(nodeIndex, endPatternChunker);
//		if (end == null) {
//			// or the rest of the lines
//			end = lineContainer.getContiguousTextRange(nodeIndex);
//		}
//	}

}
