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
import org.xmlcml.cml.converters.format.ArrayLineReader;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.converters.format.LineReader.LineType;
import org.xmlcml.cml.converters.format.MatrixLineReader;
import org.xmlcml.cml.converters.format.MoleculeLineReader;
import org.xmlcml.cml.converters.format.NameValuesLineReader;
import org.xmlcml.cml.converters.format.ReadLinesLineReader;
import org.xmlcml.cml.converters.format.RecordsLineReader;
import org.xmlcml.cml.converters.format.TableLineReader;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;

public class Template {
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

	public Template(LegacyProcessor legacyProcessor, Element element) {
		this.legacyProcessor = legacyProcessor;
		this.common = legacyProcessor.getCommon();
		this.templateElement = element;
		lineReaderList = new ArrayList<LineReader>();
		processAttributes();
		createLineReadersFromChildElements();
//		debug();
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
	}

//	private void getDictRefNames(Element element) {
//		String dictRefNameString = element.getAttributeValue(DICT_REF_NAMES);
//		if (dictRefNameString == null) {
//			LOG.info("names will be extracted from format or created as defaults");
////			throw new RuntimeException("element ("+element.getLocalName()+") must have dictRefNames");
//		} else {
//			dictRefNameString = dictRefNameString.trim();
//			dictRefNames = dictRefNameString.split(CMLConstants.S_WHITEREGEX);
//		}
//	}

	private void createLineReadersFromChildElements() {
		childElements = templateElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			LineReader lineReader = null;
			Element childElement = (Element) childElements.get(i);
			String name = childElement.getLocalName();
			if (name == null) {
				ignore();
//			} else if (LineType.ARRAY.getTag().equals(name)) {
//				lineReader = new ArrayLineReader(childElement);
			} else if (LineType.COMMENT.getTag().equals(name)) {
				ignore();
//			} else if (LineType.MATRIX.getTag().equals(name)) {
//				lineReader = new MatrixLineReader(childElement);
//			} else if (LineType.MOLECULE.getTag().equals(name)) {
//				lineReader = new MoleculeLineReader(childElement);
//			} else if (LineType.NAMEVALUES.getTag().equals(name)) {
//				lineReader = new NameValuesLineReader(childElement);
			} else if (LineType.READLINES.getTag().equals(name)) {
				lineReader = new ReadLinesLineReader(childElement);
			} else if (LineType.RECORD.getTag().equals(name)) {
				lineReader = new RecordsLineReader(childElement);
//			} else if (LineType.TABLE.getTag().equals(name)) {
//				lineReader = new TableLineReader(childElement);
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
	
//	public LineReader createAndAddLineReaderAndFields(Element subElement) {
////		formatType = subElement.getAttributeValue(FORMAT);
////		String line = subElement.getValue();
////		line = LineReader.trimLeadingAndTrailingNewLines(line);
//		LineReader lineReader = createAndAddArrayLineReaderWithFields(subElement);
////		lineReader.setFormat(format);
//		if (dictRefNames != null && dictRefNames.length != lineReader.getFieldCount()) {
//			CMLUtil.debug((Element)subElement.getParent(), "LINEREADER");
//			throw new RuntimeException("dictRefNames ("+dictRefNames.length+
//					") != fieldCount ("+lineReader.getFieldCount()+")");
//		}
//		return lineReader;
//	}

//	private void createArrayLineReader(Element arrayElement) {
//		getDictRefNames(arrayElement);
//		if (dictRefNames != null && dictRefNames.length != 1) {
//			throw new RuntimeException("dictRefNames for array should have one name");
//		}
//		
//		LineReader lineReader = createAndAddLineReaderAndFields(arrayElement);
//		lineReader.setLocalDictRef(templateElement.getAttributeValue(LOCAL_DICT_REF));
//		lineReader.setLineType(LineType.ARRAY);
//		lineReader.setColumns(templateElement.getAttributeValue(COLUMNS));
//		lineReader.setArraySize(templateElement.getAttributeValue("size"));
//	}

//	private LineReader createAndAddArrayLineReaderWithFields(Element element) {
//		LineReader lineReader = createAndAddLineReader(element);
//		lineReader.setType(LineType.ARRAY);
//		lineReader.createAndAddFieldDefinitions(dictRefNames, 0);
//		return lineReader;
//	}

//	private void createMatrixLineReader(Element element) {
////		String line = element.getValue();
////		line = LineReader.trimLeadingAndTrailingNewLines(line);
//		LineReader lineReader = createAndAddScalarLineReader(element, 0);
//		lineReader.setLocalDictRef(element.getAttributeValue(LOCAL_DICT_REF));
//		lineReader.setLineType(LineType.MATRIX);
//		lineReader.setColumns(element.getAttributeValue(COLUMNS));
//		lineReader.setRows(element.getAttributeValue(ROWS));
//	}

//	private void createMoleculeLineReader(Element element) {
//		createArrayTemplate(element, LineType.MOLECULE);
//	}

//	private void createReadLinesLineReader(Element childElement) {
//		LineReader lineReader = new LineReader();
//		lineReaderList.add(lineReader);
//		lineReader.addReadLinesCommand(childElement);
//	}

//	private void createNameValuesLineReader(Element element) {
//		//
//	}
//	private void createScalarsLineReader(Element subElement) {
//		getDictRefNames(subElement);
//		String[] lines = subElement.getValue().split(CMLConstants.S_NEWLINE);
//		int lineCount = 0;
//		int scalarCount = 0;
//		for (int i = 0; i < lines.length; i++) {
//			String line = lines[i];
//			if (line.length() == 0 && i == 0) {
//				continue;	// skip first empty line
//			}
////			if (line.contains(LineReader.T_FLAG)) {
////				createAndAddReadLinesReader(lineCount);
////				lineCount = 0;
////				LineReader lineReader = createAndAddScalarLineReader(line, scalarCount);
////				scalarCount += lineReader.getFieldCount();
////			} else {
////				lineCount++;
////			}
//		}
//		if (dictRefNames != null && scalarCount != dictRefNames.length) {
//			CMLUtil.debug(subElement, "SCALLLLLLLLAR");
//			throw new RuntimeException("scalarCount ("+scalarCount+") != dictRefNames ("+dictRefNames.length+")");
//		}
//	}

//	private void createTableLineReader(Element element) {
//		createArrayTemplate(element, LineType.TABLE);
//		
//	}

	// ========================================================
//	private void createArrayTemplate(Element subElement, LineType lineType) {
////		getDictRefNames(element);
////		
////		String line = element.getValue();
////		line = LineReader.trimLeadingAndTrailingNewLines(line);
//		LineReader lineReader = createAndAddScalarLineReader(subElement, 0);
//		
//		if (dictRefNames !=  null && dictRefNames.length != lineReader.getFieldCount()) {
//			CMLUtil.debug((Element)subElement.getParent(), "EEE");
//			System.err.println("DictRef"+org.xmlcml.euclid.Util.concatenate(dictRefNames, " ~ "));
//			LOG.warn("dictRefNames ("+dictRefNames.length+
//					") != fieldCount ("+lineReader.getFieldCount()+")");
//		}
//		lineReader.setLocalDictRef(subElement.getAttributeValue(LOCAL_DICT_REF));
//		lineReader.setLineType(lineType);
//	}

//	private LineReader createAndAddScalarLineReader(Element element, int count) {
////		String content = subElement.getValue();
//		LineReader lineReader = createAndAddLineReader(element);
//		lineReader.setType(LineType.SCALARS);
//		lineReader.createAndAddFieldDefinitions(dictRefNames, count);
//		return lineReader;
//	}

//	private void createAndAddReadLinesReader(int lineCount) {
//		LineReader lineReader = createAndAddLineReader("");
//		lineReader.setLinesToRead(lineCount);
//		lineReader.setType(LineType.READLINES);
//	}

//	private LineReader createAndAddLineReader(Element subElement) {
//		LineReader lineReader = new LineReader(subElement);
//		lineReaderList.add(lineReader);
//		return lineReader;
//	}
		
	private void checkNotNull(String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(templateElement, "CHECK");
			throw new RuntimeException("Must give "+attName+" attribute");
		}
	}

	public void markupBlock(AbstractBlock block) {
		CMLModule module = block.makeModule();
		for (LineReader lineReader : lineReaderList) {
			CMLElement element = lineReader.readLinesAndParse(block.jumboReader);
		}
	}

	public void debug() {
		CMLUtil.debug(templateElement, "TEMPLATE");
		LOG.debug("linereaders: "+lineReaderList.size());
		for (LineReader lineReader : lineReaderList) {
			lineReader.debug();
		}
	}
	
}
