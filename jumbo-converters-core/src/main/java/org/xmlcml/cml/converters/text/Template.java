package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Attribute;
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
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Int2;

public class Template {
	
	private final static Logger LOG = Logger.getLogger(Template.class);

	public static final String TAG = "template";
	
	public static final String ZERO_OR_ONE = "?";
	public static final String ZERO_OR_MORE = "*";
	public static final String ONE_OR_MORE = "+";
	// attributes
	public static final String CMLX_UNREAD = "cmlx:unread";
	private static final String DICT_REF = "dictRef";
	private static final String END_OFFSET = "endOffset";
	private static final String END_PATTERN = "endPattern";
	private static final String ID = "id";
	private static final String MULTIPLE = "multiple";
	private static final String NAME = "name";
	private static final String NAMES = "names";
	private static final String OFFSET = "offset";
	private static final String OUTPUT = "output";
	private static final String PATTERN = "pattern";
	private static final String REPEAT_COUNT = "repeatCount";
	public  static final String TEMPLATE_REF = "templateRef";
	
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
	private Integer minRepeatCount = 1;
	private Integer maxRepeatCount = 1;
	
	private JumboReader jumboReader;
	private OutputLevel outputLevel;

	private List<Deleter> deleterList;
	private TemplateContainer templateContainer;
	private List<LineReader> lineReaderList;
	private LineContainer lineContainer;
	private String dictRef;
	private String[] names;

	public Template(Element element) {
		this.theElement = element;
		CMLUtil.removeWhitespaceNodes(this.theElement);
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
		checkIfAttributeNamesAreAllowed(theElement, new String[]{
			DICT_REF,
			END_OFFSET,
			END_PATTERN,
			ID, 
			MULTIPLE,
			NAME,
			NAMES,
			OFFSET,
			OUTPUT,
			PATTERN,
			REPEAT_COUNT,
		});
				
		id = theElement.getAttributeValue(ID);
//		checkNotNull(theElement, ID, id);
		name = theElement.getAttributeValue(NAME);
// name should not be mandatory		
//		checkNotNull(theElement, NAME, name);
		names = getStringsFromAttribute(NAMES);
		this.dictRef = theElement.getAttributeValue(DICT_REF);
		
		patternString = theElement.getAttributeValue(PATTERN);
//		checkNotNull(theElement, PATTERN, patternString);
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
		offset = readIntegerAttribute(theElement, OFFSET);
		endOffset = readIntegerAttribute(theElement, END_OFFSET);
		
		processRepeatCount();
		
		startChunker = new PatternChunker(createPatternList(patternString, multipleS), offset);
		endChunker = new PatternChunker(createPatternList(endPatternString, multipleS), endOffset);
	}

	private String[] getStringsFromAttribute(String names) {
		return names == null ? null : names.split(CMLConstants.S_WHITEREGEX);
	}

	public static Integer readIntegerAttribute(Element element, String attName) {
		String value = element.getAttributeValue(attName);
		return (value == null) ? 0 : new Integer(value);
	}

	private void processRepeatCount() {
		String repeatCountS = theElement.getAttributeValue(REPEAT_COUNT); 
		minRepeatCount = 1;
		maxRepeatCount = 1;
		if (ZERO_OR_ONE.equals(repeatCountS)) {
			minRepeatCount = 0;
			maxRepeatCount = 1;
		} else if (ZERO_OR_MORE.equals(repeatCountS)) {
			minRepeatCount = 0;
			maxRepeatCount = Integer.MAX_VALUE;
		} else if (ONE_OR_MORE.equals(repeatCountS)) {
			minRepeatCount = 1;
			maxRepeatCount = Integer.MAX_VALUE;
		} else if (repeatCountS != null) {
			String[] ss = repeatCountS.split(CMLConstants.S_COMMA);
			if (ss.length > 2) {
				throw new RuntimeException("Bad repeat Count: "+repeatCountS);
			} else if (ss.length == 2) {
				try {
					minRepeatCount = new Integer(ss[0]);
					maxRepeatCount = new Integer(ss[1]);
				} catch (Exception e) {
					throw new RuntimeException("Bad repeat Count: "+repeatCountS);
				}
			} else if (ss.length == 1) {
				try {
					minRepeatCount = new Integer(ss[0]);
					maxRepeatCount = new Integer(ss[0]);
				} catch (Exception e) {
					throw new RuntimeException("Bad repeat Count: "+repeatCountS);
				}
			} else {
				throw new RuntimeException("Bad repeat Count: "+repeatCountS);
			}
		}
	}

	public static void checkIfAttributeNamesAreAllowed(Element theElement, String[] allowedNames) {
		for (int i = 0; i < theElement.getAttributeCount(); i++) {
			String attName = theElement.getAttribute(i).getLocalName();
			boolean allowed = false;
			for (String allowedName : allowedNames) {
				if (attName.equals(allowedName)) {
					allowed = true;
					break;
				}
			}
			if (!allowed) {
				CMLUtil.debug(theElement, "FORBIDDEN ATT");
				throw new RuntimeException("Forbidden attribute name: "+attName);
			}
		}
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
			} else if (Template.TAG.equals(name)) {
				Template childTemplate = new Template(childElement);
				LOG.warn("TEMPLATE child");
				System.out.println(theElement.getAttributeValue(ID)+"/"+childElement.getAttributeValue(ID));
				throw new RuntimeException("TODO fix recursive template");
			} else if (TemplateContainer.TAG.equals(name)) {
				if (templateContainer != null) {
					throw new RuntimeException("Only one templateList allowed");
				}
				templateContainer = new TemplateContainer(childElement);
			} else {
				CMLUtil.debug(theElement, "UNKNOWN CHILD");
				throw new RuntimeException("unknown child: "+name);
			}
		}
//		if (lineReaderList.size() > 0 &&
//		   (deleterList.size() > 0 || templateContainer != null)) {
//				throw new RuntimeException(
//						"Cannot have both deleters+templateList and readLines+record");
//		}
	}

	private void ignore() {
		// TODO Auto-generated method stub
		
	}

	// ========================================================
	
		
	private static void checkNotNull(Element element, String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(element, "CHECK null attVal");
			throw new RuntimeException(element.getLocalName()+": must give "+attName+" attribute");
		}
	}
	
	// =========================================================

	public void applyMarkup(String line) {
		LOG.debug("LINE: "+line+" / "+line.split(CMLConstants.S_NEWLINE).length);
		lineContainer = new LineContainer(line);
		applyMarkup(lineContainer);
	}
	
	public void applyMarkup(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		Element linesElement = lineContainer.getLinesElement();
		applyDeleters();
		applyChildTemplateList();
		applyLineReaders();
		linesElement.addAttribute(new Attribute(Template.TEMPLATE_REF, this.getId()));

	}
	
	private void applyDeleters() {
		for (Deleter deleter : deleterList) {
			deleter.deleteLines(lineContainer, Integer.MAX_VALUE);
		}
	}

	private void applyChildTemplateList() {
		if (templateContainer != null) {
			LOG.debug(id+" applying childTemplates");
			for (Template childTemplate : templateContainer.getTemplateList()) {
//				template.debug();
				List<Element> elements = childTemplate.resetNodeIndexAndApplyChunkers(lineContainer);
				LOG.debug("found child elements after wrap: "+elements.size());
				for (Element element : elements) {
					element.addAttribute(new Attribute(TEMPLATE_REF, childTemplate.getId()));
					LineContainer childLineContainer = new LineContainer(element);
					childTemplate.applyMarkup(childLineContainer);
				}
			}
		}
	}

	public List<Element> resetNodeIndexAndApplyChunkers(LineContainer lineContainer) {
		lineContainer.setCurrentNodeIndex(0);
		int repeatCount = 0; // use this later
		List<Element> chunkedElements = new ArrayList<Element>();
		while (repeatCount < this.maxRepeatCount) {
			Element chunk = this.findNextChunk(lineContainer);
			if (chunk == null) {
				break;
			}
			chunk.addAttribute(new Attribute(TEMPLATE_REF, this.id));
			chunkedElements.add(chunk);
			repeatCount++;
		}
		return chunkedElements;
	}

	Element findNextChunk(LineContainer lineContainer) {
		Element chunk = null;
		Int2 startRange = lineContainer.findNextMatch(lineContainer.getCurrentNodeIndex(), this.startChunker);
		if (startRange != null) {
			int start = startRange.getX()+this.startChunker.getOffset();
			int end = startRange.getY();
			Int2 endRange = lineContainer.findNextMatch(end+1, this.endChunker);
			if (endRange != null) {
				end = endRange.getX();
				lineContainer.setCurrentNodeIndex(endRange.getY());
			}
			chunk = lineContainer.createChunk(start, end+endChunker.getOffset());
			LOG.trace("line1 "+lineContainer.peekCurrentNode());
		}
		return chunk;
	}

	private void applyLineReaders() {
		for (LineReader lineReader : lineReaderList) {
			Element chunk = lineReader.apply(lineContainer);
			if (chunk != null) {
				chunk.addAttribute(new Attribute(TEMPLATE_REF, lineReader.getId()));
				lineContainer.insertChunk(chunk);
			} else {
				throw new RuntimeException("null chunk");
			}
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

	public String toString() {
		String s = "";
		s += "startChunker "+startChunker.toString()+"\n";
		s += "endChunker "+endChunker.toString()+"\n";
		return s;
	}
	
}
