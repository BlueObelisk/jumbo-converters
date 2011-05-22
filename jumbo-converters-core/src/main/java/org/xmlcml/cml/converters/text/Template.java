package org.xmlcml.cml.converters.text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.converters.format.LineReader.LineType;
import org.xmlcml.cml.converters.format.RecordReader;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Int2;

public class Template implements MarkupApplier {
	private static final String HTTP_WWW_W3_ORG_2001_X_INCLUDE = "http://www.w3.org/2001/XInclude";
	private final static Logger LOG = Logger.getLogger(Template.class);
	
	static{
	    LOG.setLevel(Level.ERROR);
	}
	
	public static final String EOI = "~";
	public static final String TAG = "template";
	public static final String ZERO_OR_ONE = "?";
	public static final String ZERO_OR_MORE = "*";
	public static final String ONE_OR_MORE = "+";
	// attributes
	public static final String CMLX_UNREAD = "cmlx:unread";
	public static final String CONVENTION = "convention";
	private static final String DICT_REF = "dictRef";
	private static final String END_OFFSET = "endOffset";
	private static final String END_PATTERN = "endPattern";
	private static final String ID = "id";
	private static final String MULTIPLE = "multiple"; // deprecated
	private static final String NAME = "name";
	private static final String NAMES = "names";
	private static final String NEWLINE = "newline";
	private static final String OFFSET = "offset";
	private static final String OUTPUT = "output";
	private static final String PATTERN = "pattern";
	private static final String REPEAT = "repeat";
	private static final String REPEAT_COUNT = "repeatCount"; // deprecated
	public  static final String TEMPLATE_REF = "templateRef";

	private static final String DEBUG = "debug"; // maybe somewhere better?
	private static final String BASE = "base"; // left by XInclude

	private static final String NULL_ID = "NULL_ID";
	private static final String DEFAULT_NEWLINE = CMLConstants.S_DOLLAR;

	public static XPathContext CML_CMLX_CONTEXT = null;
	static {
		CML_CMLX_CONTEXT = new XPathContext();
		CML_CMLX_CONTEXT.addNamespace(CMLConstants.CML_PREFIX, CMLConstants.CML_NS);
		CML_CMLX_CONTEXT.addNamespace(CMLConstants.CMLX_PREFIX, CMLConstants.CMLX_NS);
	}
	
	protected Element theElement;
	private String id;
	private String name;
	private String newlineS;
	private String patternString;
	private String endPatternString;
	private PatternContainer endChunker;
	protected PatternContainer startChunker;
	private boolean debug = false;
	private Integer offset;
	private Integer endOffset;
	private Integer minRepeatCount = 1;
	private Integer maxRepeatCount = 1;
	private OutputLevel outputLevel;

	private List<MarkupApplier> markerList;
	private LineContainer lineContainer;
	private String dictRef;
	private String[] names;
	private List<DictionaryContainer> dictionaryList;

	public Template(Element element) {
		this.theElement = element;
        try {
            ClassPathXIncludeResolver.resolveIncludes(element.getDocument());
		} catch (Exception e) {
			throw new RuntimeException("Bad XInclude", e);
		}
		CMLUtil.removeWhitespaceNodes(this.theElement);
		processChildElementsAndAttributes();
	}

	private void processChildElementsAndAttributes() {
		processAttributes();
		createSubclassedElementsFromChildElements();
		if (!OutputLevel.NONE.equals(outputLevel)) {
//			this.debug();
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public PatternContainer getEndChunker() {
		return endChunker;
	}

	public PatternContainer getStartChunker() {
		return startChunker;
	}

	@Deprecated // will change to list structure
	public Pattern getPattern() {
		return (startChunker == null || startChunker.size() == 0) ? null : startChunker.get(0);
	}

	public OutputLevel getOutputLevel() {
		return outputLevel;
	}
	
	public LineContainer getLineContainer() {
		return lineContainer;
	}

	private void processAttributes() {
		checkIfAttributeNamesAreAllowed(theElement, new String[]{
			BASE,
			CONVENTION,
			DEBUG,
			DICT_REF,
			END_OFFSET,
			END_PATTERN,
			ID, 
			MULTIPLE,
			NAME,
			NAMES,
			NEWLINE,
			OFFSET,
			OUTPUT,
			PATTERN,
			REPEAT,
			REPEAT_COUNT,
		});
				
		id = theElement.getAttributeValue(ID);
		if (id == null) {
			id = NULL_ID;
		}
		name = theElement.getAttributeValue(NAME);
		names = getStringsFromAttribute(NAMES);
		this.dictRef = theElement.getAttributeValue(DICT_REF);
		
		patternString = theElement.getAttributeValue(PATTERN);
		endPatternString = theElement.getAttributeValue(END_PATTERN);
		if (endPatternString == null) {
			// special end-of-information symbol
			endPatternString = EOI;
		}
		processNewline();
		
		outputLevel = Outputter.extractOutputLevel(this.theElement);
		LOG.trace(outputLevel+"/"+this.theElement.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
//			System.out.println("OUTPUT "+outputLevel);
		}
		debug = (theElement.getAttributeValue(DEBUG) != null);
		offset = readIntegerAttribute(theElement, OFFSET);
		endOffset = readIntegerAttribute(theElement, END_OFFSET);
		
		processRepeatCount();
		
		startChunker = new PatternContainer(patternString, newlineS, offset);
		endChunker = new PatternContainer(endPatternString, newlineS, endOffset);
	}

	private void processNewline() {
		newlineS = theElement.getAttributeValue(NEWLINE);
		if (newlineS == null) {
			newlineS = theElement.getAttributeValue(MULTIPLE);
			if (newlineS != null) {
				LOG.warn("multiple is deprecated, use newline");
			}
		}
		if (newlineS == null) {
			newlineS = DEFAULT_NEWLINE;
		}
		if (newlineS != null) {
			newlineS = escape(newlineS);
		}
	}

	public static String escape(String s) {
		if ("\"$%^*-+.".contains(s)) {
			s =  CMLConstants.S_BACKSLASH+s;
		}
		return s;
	}

	private String[] getStringsFromAttribute(String names) {
		return names == null ? null : names.split(CMLConstants.S_WHITEREGEX);
	}

	public static Integer readIntegerAttribute(Element element, String attName) {
		String value = element.getAttributeValue(attName);
		return (value == null) ? 0 : new Integer(value);
	}

	private void processRepeatCount() {
		String repeatCountS = theElement.getAttributeValue(REPEAT); 
		if (repeatCountS == null) {
			repeatCountS = theElement.getAttributeValue(REPEAT_COUNT); 
			if (repeatCountS != null) {
				LOG.warn("repeatCount is deprecated, use repeat");
			}
		}
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
				System.out.println("Allowed options:");
				for (String name : allowedNames) {
					System.out.println(">     "+name);
				}
				CMLUtil.debug(theElement, "FORBIDDEN ATT "+attName);
				throw new RuntimeException("Forbidden attribute name: "+attName);
			}
		}
	}


	private void createSubclassedElementsFromChildElements() {
		Elements childElements = theElement.getChildElements();
		markerList = new ArrayList<MarkupApplier>();
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
				markerList.add(deleter);
			} else if (DictionaryContainer.TAG.equals(name)) {
				addDictionary(new DictionaryContainer(childElement));
			} else if (LineType.READLINES.getTag().equals(name)) {
				throw new RuntimeException("readLines is deprecated");
			} else if (LineType.RECORD.getTag().equals(name)) {
				lineReader = new RecordReader(childElement, this);
				markerList.add(lineReader);
			} else if (Template.TAG.equals(name)) {
				System.out.println(theElement.getAttributeValue(ID)+"/"+childElement.getAttributeValue(ID));
				throw new RuntimeException("Template cannot be child of Template; use templateList");
			} else if (TemplateListElement.TAG.equals(name)) {
				TemplateListElement templateContainer = new TemplateListElement(childElement);
				markerList.add(templateContainer);
			} else if (TransformElement.TAG.equals(name)) {
				TransformElement transformer = new TransformElement(childElement, this);
				markerList.add(transformer);
			} else if (TransformListElement.TAG.equals(name)) {
				TransformListElement transformerList = new TransformListElement(childElement, this);
				markerList.add(transformerList);
			} else if (Template.DEBUG.equals(name)) {
				markerList.add(new Debug(this));
			} else {
				CMLUtil.debug(theElement, "UNKNOWN CHILD");
				throw new RuntimeException("unknown child: "+name);
			}
		}
	}

	private void addDictionary(DictionaryContainer dictionary) {
		ensureDictionaryList();
		this.getDictionaryContainerList().add(dictionary);
	}

	private void ignore() {
		// TODO Auto-generated method stub
		
	}

	// =========================OUTPUT/MARKUP===============================
	
		
	public void applyMarkup(String line) {
		LOG.trace("LINE: "+line+" / "+line.split(CMLConstants.S_NEWLINE).length);
		lineContainer = new LineContainer(line, this);
		applyMarkup(lineContainer);
	}
	
	public void applyMarkup(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		Element linesElement = lineContainer.getLinesElement();
		copyNamespaces(lineContainer.getLinesElement());
		CMLElement.addCMLXAttribute(linesElement, Template.TEMPLATE_REF, this.getId());
		for (MarkupApplier marker : markerList) {
			LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
			try {
				marker.applyMarkup(lineContainer);
			} catch (Exception e) {
				processException(lineContainer, marker, e);
			}
		}
		removeEmptyLists(linesElement);
	}

//	private void removeXIncludeAll(Element linesElement) {
//		Nodes elements = linesElement.query(".//*");
//		for (int i = 0; i < elements.size(); i++) {
//			removeXInclude((Element)elements.get(i));
//		}
//	}
//
//	private void removeXInclude(Element element) {
//		int ndec = element.getNamespaceDeclarationCount();
//		for (int i = 0; i < ndec; i++) {
//			String prefix = element.getNamespacePrefix(i);
//			String namespaceURI = element.getNamespaceURI(prefix);
//			if (HTTP_WWW_W3_ORG_2001_X_INCLUDE.equals(namespaceURI)) {
//				throw new RuntimeException("XINCLUDE");
//			}
//		}
//	}

	private void processException(LineContainer lineContainer,
			MarkupApplier marker, Exception e) {
		lineContainer.debug("LINE CONTAINER");
		String line = lineContainer.peekLine();
		int nline = lineContainer.getCurrentNodeIndex();
		System.err.println("PREVIOUS..."+nline);
		for (int i = (Math.max(0, nline-6)); i < nline; i++) {
			System.err.println(lineContainer.getLinesElement().getChild(i));
		}
		System.out.println("========DEBUG======="+marker.getId()+">>>>>");
		marker.debug();
		System.out.println("<<<<<"+marker.getId()+"========DEBUG=======");
		if (line == null) {
			if (debug) {
				LOG.error("Null line ("+nline+")", e);
			} else {
				throw new RuntimeException("Null line ("+nline+")", e);
			}
		} else {
			throw new RuntimeException("Failed; current line ("+nline+")"+line, e);
		}
	}
	
	public void applyMarkup(Element element) {
		copyNamespaces(element);
		CMLElement.addCMLXAttribute(element, Template.TEMPLATE_REF, this.getId());
		for (MarkupApplier marker : markerList) {
			LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
			try {
				marker.applyMarkup(element);
			} catch (Exception e) {
				throw new RuntimeException("Bad xml element", e);
			}
		}
	}
	
	private void copyNamespaces(Element targetElement) {
		copyNamespaces(this.theElement, targetElement);
	}

	public static void copyNamespaces(Element fromElement, Element targetElement) {
		int count = fromElement.getNamespaceDeclarationCount();
		for (int i = 0; i < count; i++) {
			String prefix = fromElement.getNamespacePrefix(i);
			String namespaceURI = fromElement.getNamespaceURI(prefix);
			if (!(HTTP_WWW_W3_ORG_2001_X_INCLUDE.equals(namespaceURI))) {
				if (!hasNamespacePrefix(targetElement, prefix)) {
					targetElement.addNamespaceDeclaration(prefix, namespaceURI);
				}
			}
		}
	}

	private static boolean hasNamespacePrefix(Element targetElement, String prefix) {
		for (int i = 0; i < targetElement.getNamespaceDeclarationCount(); i++) {
			if (prefix.equals(targetElement.getNamespacePrefix(i))) {
				return true;
			}
		}
		return false;
	}

	public List<Element> resetNodeIndexAndApplyChunkers(LineContainer lineContainer) {
//		if (resetCounter) {
			lineContainer.setCurrentNodeIndex(0);
//		}
		int repeatCount = 0; // use this later
		List<Element> chunkedElements = new ArrayList<Element>();
		while (repeatCount < this.maxRepeatCount) {
			Element chunk = this.findNextChunk(lineContainer);
			if (chunk == null) {
				break;
			}
			CMLElement.addCMLXAttribute(chunk, Template.TEMPLATE_REF, this.getId());
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

	/**
	 * gets remaining lines in block and adds them to parentElement
	 */
	public static void tidyUnusedLines(LineContainer lineContainer, CMLElement parentElement) {
		StringBuilder sb = new StringBuilder();
		boolean empty = true;
		while (lineContainer.hasMoreNodes()) {
			String s = lineContainer.readLine();
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
//				List<CMLElement> cmlChildElements = jumboReader.getParentElement().getChildCMLElements();
//				parentElement = (cmlChildElements.size() == 0) ? jumboReader.getParentElement() : cmlChildElements.get(cmlChildElements.size()-1);
			}
			parentElement.appendChild(scalar);
		}
	}
	
	public static Element removeNamelessScalars(Element list) {
		Nodes namelessScalars = list.query(".//*[local-name()='scalar' and not(@dictRef)]");
		for (int i = 0; i < namelessScalars.size(); i++) {
			namelessScalars.get(i).detach();
		}
		return list;
	}

	public static Element removeEmptyLists(Element list) {
		boolean change = true;
		while (change) {
			Nodes emptyLists = list.query(".//*[local-name()='list' and count(*)=0]");
			change = emptyLists.size() > 0;
			for (int i = 0; i < emptyLists.size(); i++) {
				emptyLists.get(i).detach();
			}
		}
		return list;
	}

	public static Element flattenSingletonLists(Element list) {
		Nodes singletonLists = list.query(".//*[local-name()='list' and count(*)=1 and count(*[local-name()='scalar' or local-name()='array'])=1 and count(*[@dictRef])=1]");
		for (int i = 0; i < singletonLists.size(); i++) {
			CMLList singletonList = (CMLList) singletonLists.get(i);
			CMLElement scalarOrArray = (CMLElement) singletonList.getChildElements().get(0);
			scalarOrArray.detach();
			singletonList.detach();
			list.appendChild(scalarOrArray);
		}
		return list;
	}


	public void debug(String string, OutputLevel maxLevel) {
		if (Outputter.canOutput(outputLevel, maxLevel)) {
			LOG.debug(string);
		}
	}
	
	public void debug() {
		System.out.println("=====DBG==="+this.getId()+"===DBG======");
		CMLUtil.debug(theElement, "TEMPLATE");
		LOG.debug("children: "+markerList.size());
		for (MarkupApplier marker : markerList) {
			marker.debug();
		}
		System.out.println("startChunker "+startChunker.toString());
		System.out.println("endChunker "+endChunker.toString());
		System.out.println("=====END DBG=======");
	}

	public String toString() {
		String s = "";
		s += "startChunker "+startChunker.toString()+"\n";
		s += "endChunker "+endChunker.toString()+"\n";
		return s;
	}

	public static String regexEscape(String multiple) {
		if (CMLConstants.S_DOLLAR.equals(multiple)) {
			multiple = CMLConstants.S_BACKSLASH+multiple;
		}
		return multiple;
	}

	public String getConvention() {
		return theElement.getAttributeValue(CONVENTION);
	}


	private void ensureDictionaryList() {
		if (this.getDictionaryContainerList() == null) {
			this.setDictionaryList(new ArrayList<DictionaryContainer>());
		}
	}

	public void setDictionaryList(List<DictionaryContainer> dictionaryList) {
		this.dictionaryList = dictionaryList;
	}

	public List<DictionaryContainer> getDictionaryContainerList() {
		return dictionaryList;
	}


}
