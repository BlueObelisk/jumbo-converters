package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.format.Field.FieldType;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;

public abstract class LineReader extends Element {
	private final static Logger LOG = Logger.getLogger(LineReader.class);

	private static final String MAKE_ARRAY = "makeArray";
	private static final String NULL_ID = "nullId";
	private static final String ID = "id";

	protected static final String ELEMENT_TYPE = "elementType";
	private static final String LOCAL_DICT_REF = "localDictRef";
	private static final String EXACT = "exact";
	private static final String REGEX = "regex";
	private static final String FORMAT_TYPE = "formatType";
	private static final String NAMES = "names";
	private static final String UNTIL = "until";
	private static final String WHILE = "while";
	private static final String LINES_TO_READ = "linesToRead";
	public static final String T_FLAG = CMLConstants.S_TILDE;

	public enum FormatType {
		FORTRAN,
		WHITESPACE,
		REGEX,
		;
	}
	public enum ReadingType {
		WHILE,
		UNTIL,
		ALL,
		ONCE,
		;
	}
	 public enum LineType { 
		COMMENT("comment"), 
		READLINES("readLines"),
		RECORD("record"),
		;

        private final String tag;
        private static final Map<String, LineType> lookup = new HashMap<String, LineType>();
        
        static {
            for (LineType d : LineType.values())
                lookup.put(d.getTag(), d);
        }
        private LineType(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }
        public static LineType get(String tag) {
            return lookup.get(tag);
        }
    }
		
	private static final String LINE_READER = "lineReader";

	protected static final String TRUE = "true";
	
	protected JumboReader jumboReader;
	protected Integer linesToRead = null;
	protected List<Field> fieldList;
	private LineType lineType;
	protected FormatType formatType;
	protected String content;
	private int fieldCount;
	protected String localDictRef;
	protected int currentCharInLine;
	protected Integer columns;
	protected Integer rows;
	protected Integer size;
	private Element lineReaderElement;
	protected String delimiter = CMLConstants.S_TILDE;
	protected boolean trim = true;
	protected String id;
	protected Pattern pattern;
	private String names;
	protected String makeArray = null;

	public String getId() {
		return (id == null) ? NULL_ID : id;
	}

	public LineReader() {
		super(LINE_READER);
		init();
	}

	private void init() {
		linesToRead = null;
		fieldList = new ArrayList<Field>();
	}

	public LineReader(String tag, Element element) {
		super(tag);
		init();
		this.lineReaderElement = element;
		this.content = element.getValue();
		readAndCreateFormatType();
		readAndCreateLinesToRead();
		readAndCreateNames();
		readAndCreateMakeArrays();
		readAndCreateId();
		createFields();
	}

	private void readAndCreateMakeArrays() {
		this.makeArray = lineReaderElement.getAttributeValue(MAKE_ARRAY);
		if (makeArray != null) {
			this.addAttribute(new Attribute(MAKE_ARRAY, makeArray));
		}
	}

	private void readAndCreateNames() {
		this.names = lineReaderElement.getAttributeValue(NAMES);
		if (names != null) {
			this.addAttribute(new Attribute(NAMES, names));
		}
	}

	public LineReader(List<Field> fieldList) {
		this();
		this.fieldList = fieldList;
		addFieldsAsXMLChildren();
	}

	private void readAndCreateFormatType() {
		String formatTypeS = lineReaderElement.getAttributeValue(FORMAT_TYPE);
		formatType = null;
		if (formatTypeS != null) {
			formatType = FormatType.valueOf(formatTypeS);
			if (formatType == null) {
				throw new RuntimeException("Unknown formatType: "+formatType);
			}
			this.addAttribute(new Attribute(FORMAT_TYPE, formatTypeS));
		}
	}

//	private void readAndCreateReadingType() {
//		String readingTypeS = lineReaderElement.getAttributeValue(READING_TYPE);
//		readingType = null;
//		if (readingTypeS != null) {
//			readingType = ReadingType.valueOf(readingTypeS);
//			if (readingType == null) {
//				throw new RuntimeException("Unknown readingType: "+readingTypeS);
//			}
//			this.addAttribute(new Attribute(READING_TYPE, readingTypeS));
//		}
//	}

	private void readAndCreateLinesToRead() {
		String linesToReadS = lineReaderElement.getAttributeValue(LINES_TO_READ);
		linesToRead = null;
		if (linesToReadS != null) {
			if (linesToReadS.equals("*")) {
				linesToRead = Integer.MAX_VALUE-1;
			} else {
				try {
					linesToRead = new Integer(linesToReadS);
				} catch (Exception e) {
					throw new RuntimeException("bad linesToRead: "+linesToReadS);
				}
			}
			this.addAttribute(new Attribute(LINES_TO_READ, linesToReadS));
		}
	}

	private void readAndCreateId() {
		this.id = lineReaderElement.getAttributeValue(ID);
		if (id != null) {
			this.addAttribute(new Attribute(ID, id));
		}
	}

	private void createFields() {
		if (formatType == null) {
//			throw new RuntimeException("No formatType given");
		} else if (FormatType.FORTRAN.equals(formatType)) {
			createFortranFields();
		} else if (FormatType.REGEX.equals(formatType)) {
			createRegexFields();
		} else if (FormatType.WHITESPACE.equals(formatType)) {
			
		} else {
			throw new RuntimeException("Unknown format: "+formatType);
		}
		addFieldsAsXMLChildren();
	}

	private void addFieldsAsXMLChildren() {
		if (fieldList == null) {
			throw new RuntimeException("null fieldList");
		}
		for (Field field : fieldList) {
			this.appendChild(field.copy());
		}
	}

	private void createFortranFields() {
		if (content == null) {
			throw new RuntimeException("null content in LineReader: "+this.getLocalName());
		}
		SimpleFortranFormat simpleFortranFormat = new SimpleFortranFormat(content.trim());
		fieldList = simpleFortranFormat.getFieldList();
		for (Field field : fieldList) {
			LOG.debug("FIELD "+field);
		}
	}

	private void createRegexFields() {
		if (content == null) {
			throw new RuntimeException("null content in LineReader: "+this.getLocalName());
		}
		this.pattern = null;
		try {
			pattern = Pattern.compile(content);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse regex: "+content);
		}
	}

	public boolean isTrim() {
		return trim;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public String getLocalDictRef() {
		return localDictRef;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public int getFieldCount() {
		return fieldCount;
	}

	public Integer getLinesToRead() {
		return linesToRead;
	}

	public void setType(LineType type) {
		this.lineType = type;
	}

	public String getNames() {
		return names;
	}

// ============================== reading ======================
	
	public abstract CMLElement readLinesAndParse(JumboReader jumboReader);

	/** parses into scalars 
	 * 
	 * @param line
	 * @param jumboReader needed as the fields may include slashes
	 * @return
	 */
	public List<HasDataType> parseInlineHasDataTypes(JumboReader jumboReader) {
		this.jumboReader = jumboReader;
		List<HasDataType> dataTypeList = null;
		String line = jumboReader.peekLine();
		if (line != null && line.trim().length() > 0) {
			currentCharInLine = 0;
			if (pattern != null) {
				parseWithPattern();
			} else {
				dataTypeList = parseWithFields();
			}
			jumboReader.readLine();
		}
		return dataTypeList;
		
	}

	private CMLList parseWithPattern() {
		String line = jumboReader.peekLine();
		Matcher matcher = pattern.matcher(line);
		CMLList list = null;
		if (matcher.matches()) {
			list = new CMLList();
			String localDictRef[] = (names == null) ? null : names.split(CMLConstants.S_SPACE);
			for (int i = 1; i <= matcher.groupCount(); i++) {
				CMLScalar scalar = new CMLScalar(matcher.group(i).trim());
				list.appendChild(scalar);
				if (localDictRef != null && localDictRef.length == matcher.groupCount()) {
					scalar.setDictRef(DictRefAttribute.createValue(jumboReader.getDictionaryPrefix(), localDictRef[i-1]));
				}
			}
		}
		if (list != null) {
			jumboReader.appendChild(list);
		}
		return list;
	}

	private List<HasDataType> parseWithFields() {
		List<HasDataType> dataTypeList;
		dataTypeList = new ArrayList<HasDataType>();
		for (Field field : fieldList) {
			HasDataType hasDataType = parseSingleScalarOrInlineArray(field);
			if (hasDataType != null) {
				dataTypeList.add(hasDataType);
			}
		}
		return dataTypeList;
	}

	public HasDataType parseSingleScalarOrInlineArray(Field field) {
		HasDataType hasDataType = null;
		String dictionaryPrefix = jumboReader.getDictionaryPrefix();
		LOG.trace(field.toString());
		field.setDictionaryPrefix(dictionaryPrefix);
		String line = jumboReader.peekLine();
		if (field.getDataTypeClass() == null) {
			if (FieldType.N.equals(field.getFieldType())) { // newline
				line = jumboReader.readLine();
				currentCharInLine = 0;
			} else {
				currentCharInLine += field.getWidth();
			}
		} else {
			Integer multiplier = field.getMultiplier();
			if (multiplier != null) {
				List<CMLScalar> scalarList = new ArrayList<CMLScalar>();
				for (int i = 0; i < multiplier ; i++) {
					CMLScalar scalar = readScalar(field, line);
					if (scalar == null) {
						break;
					}
					scalarList.add(scalar);
				}
				hasDataType = CMLArray.createArray(scalarList);
			} else {
				hasDataType = readScalar(field, line);
			}
//			((HasDictRef)hasDataType).setDictRef(DictRefAttribute.createValue(dictionaryPrefix, value)dictRef);
		}
		return hasDataType;
	}

	private CMLScalar readScalar(Field field, String line) {
		CMLScalar scalar = field.readScalar(line, currentCharInLine);
		currentCharInLine = field.getCurrentCharInLine();
		return scalar;
	}


	public void debug() {
		LOG.debug(">>LINEREADER>>"+content+"\n"+toString()+"\n>>Fields: "+fieldList.size());
		for (Field field :fieldList) {
			LOG.debug(""+field);
		}
	}

	public String toString() {
		return "lineType: "+lineType+
		((rows == null) ? "" : " rows:"+rows+"; ")+
		((columns == null) ? "" : " columns:"+columns+"; ")+
		((size == null) ? "" : " size:"+size+"; ")+
		((localDictRef == null) ? "" : " ldr:"+localDictRef+"; ")+
		((linesToRead == null) ? "" : " ltr:"+linesToRead+"; ")+
		"";
	}

//	/*
//	protected Integer linesToRead = null;
//	protected List<Field> fieldList;
//	private LineType lineType;
//	protected FormatType formatType;
//	private ReadingType readingType;
//	private String content;
//	private String until;
//	private int fieldCount;
//	protected String localDictRef;
//	protected int currentCharInLine;
//	protected Integer columns;
//	protected Integer rows;
//	protected Integer size;
//	private Element lineReaderElement;
//	protected String delimiter = CMLConstants.S_TILDE;
//
//	protected boolean trim = true;
//
//	 */
//	public Element createXML() {
//		return this;
//	}
}
