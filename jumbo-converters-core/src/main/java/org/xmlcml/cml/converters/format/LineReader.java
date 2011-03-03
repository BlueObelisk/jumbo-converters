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
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.format.Field.FieldType;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.MarkupApplier;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;

public abstract class LineReader extends Element implements MarkupApplier {
	private static final String A = "A";
	private static final String B = "B";
	private static final String D = "D";
	private static final String F = "F";
	private static final String I = "I";

	private final static Logger LOG = Logger.getLogger(LineReader.class);

	private static final String MAKE_ARRAY = "makeArray";
	private static final String NULL_ID = "nullId";
	private static final String ID = "id";

	protected static final String ELEMENT_TYPE = "elementType";
	private static final String FORMAT_TYPE = "formatType";
	private static final String NAME = "name";    // obsolete
	private static final String NAMES = "names";
	private static final String OUTPUT = "output";
	@SuppressWarnings("unused")
	private static final String UNTIL = "until";
	@SuppressWarnings("unused")
	private static final String WHILE = "while";
	private static final String LINES_TO_READ = "linesToRead";
	public static final String T_FLAG = CMLConstants.S_TILDE;

	public enum FormatType {
		FORTRAN,
		NONE,
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
	private static final FormatType DEFAULT_FORMAT_TYPE = FormatType.NONE;
	private static String DEFAULT_CONTENT = ".*";
	
	protected JumboReader jumboReader;
	protected Integer linesToRead = null;
	protected List<Field> fieldList;
	private   LineType lineType;
	protected FormatType formatType;
	protected String content;
	private   int fieldCount;
	protected String localDictRef;
	protected int currentCharInLine;
	protected Integer columns;
	protected Integer rows;
	protected Integer size;
	private   Element lineReaderElement;
	protected String delimiter = CMLConstants.S_TILDE;
	protected boolean trim = true;
	protected String id;
	protected Pattern pattern;
	private   String names;
	private   String types;
	protected String makeArray = null;
	protected LineContainer lineContainer;
	protected OutputLevel outputLevel;
	protected Template template;

	private Pattern FIELD_PATTERN = Pattern.compile("\\{[FIA]([^\\}])*\\}");

	private List<String[]> typeAndNameList;

	public String getId() {
		return (id == null) ? NULL_ID : id;
	}

	public LineReader() {
		super(LINE_READER);
		init();
	}

	protected void init() {
		linesToRead = null;
		fieldList = new ArrayList<Field>();
	}

	public LineReader(String tag, Element element, Template template) {
		super(tag);
		init();
		this.template = template;
		this.lineReaderElement = element;
//		CMLUtil.debug(lineReaderElement, "LINEREADER");
		this.content = element.getValue();
		processAttributes();
		addDefaults();
	}

	private void addDefaults() {
		if (content == null || content.trim().equals("")) {
			content = DEFAULT_CONTENT;
		}
		if (formatType == null) {
			formatType = DEFAULT_FORMAT_TYPE;
		}
		if (linesToRead == null) {
			linesToRead = 1;
		}
	}

	private void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(lineReaderElement, new String[]{
			ID, 
			FORMAT_TYPE,
			LINES_TO_READ,
			MAKE_ARRAY,
			NAME,
			NAMES,
			OUTPUT,
		});
		readAndCreateFormatType();
		readAndCreateLinesToRead();
		readAndCreateNames();
		readAndCreateMakeArrays();
		readAndCreateId();
		readAndCreateOutputLevel();
		createFields();
		outputLevel = Outputter.extractOutputLevel(this);
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
		LOG.trace("ID: "+id);
	}

	private void readAndCreateOutputLevel() {
		String outputS = lineReaderElement.getAttributeValue(Outputter.OUTPUT);
		if (outputS != null) {
			this.addAttribute(new Attribute(Outputter.OUTPUT, outputS));
		}
	}

	private void createFields() {
		if (formatType == null) {
//			throw new RuntimeException("No formatType given");
		} else if (FormatType.FORTRAN.equals(formatType)) {
			createFortranFields();
		} else if (FormatType.REGEX.equals(formatType)) {
			createRegexFields();
		} else if (FormatType.NONE.equals(formatType)) {
			// no-op
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
			LOG.trace("FIELD "+field);
		}
	}

	private void createRegexFields() {
		if (content == null) {
			throw new RuntimeException("null content in LineReader: "+this.getLocalName());
		}
		expandSymbols();
		if (names == null) {
			names = createAttributes(1);
		}
		types = createAttributes(0);
		this.pattern = null;
		try {
			pattern = Pattern.compile(content);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse regex: "+content);
		}
	}

	private String createAttributes(int serial) {
		String attVal = "";
		for (int i = 0; i < typeAndNameList.size(); i++) {
			attVal += typeAndNameList.get(i)[serial];
			if (i < typeAndNameList.size()-1) {
				attVal += " ";
			}
		}
		return attVal;
	}

	private List<String[]> expandSymbols() {
		int start = 0;
		typeAndNameList = new ArrayList<String[]>();
		StringBuilder sb = new StringBuilder();
		Matcher matcher = FIELD_PATTERN.matcher(content);
		int serial = 0;
		while (matcher.find(start)) {
			int start0 = matcher.start();
			int end = matcher.end();			
			sb.append(content.substring(start, start0));
			String transformS = expand(content.substring(start0, end), serial++);
			sb.append(transformS);
			start = end;
		}
		sb.append(content.substring(start));
		content = sb.toString();
		return typeAndNameList;
	}

	private String expand(String string, int serial) {
		string = string.substring(1, string.length()-1);
		String type = createType(string.substring(0,1));
		string = string.substring(1);
		String[] ss = string.split(CMLConstants.S_COMMA);
		String name = (ss.length < 2) ? null : ss[1];
		String[] typeName = new String[]{type, name};
		typeAndNameList.add(typeName);
		String[] wd = ss[0].split("\\.");
		Integer width = (wd[0] == null || wd[0].trim().length() == 0) ? null : new Integer(wd[0]); 
		Integer decimal = (wd.length == 1 || wd[1] == null || wd[1].trim().length() == 0) ? null : new Integer(wd[1]); 
		LOG.trace("<"+type+">"+width+"."+decimal);
		String newString = "";
		if (type.equals(CMLConstants.XSD_STRING)) {
			newString = "\\s*([^\\s]+)\\s*";
		} else if (type.equals(CMLConstants.XSD_BOOLEAN)) {
//			newString = createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_INTEGER)) {
			newString = createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_DOUBLE)) {
			newString = createFloatField(string, width, decimal);
		} else if (type.equals(CMLConstants.XSD_DATE)) {
//			newString = createFloatField(string, width, decimal);
		} else {
			throw new RuntimeException("bad type in: "+string);
		}
		return newString;
	}

	private String createType(String string) {
		String type = null;
		if (string.equals(A)) {
			type = CMLConstants.XSD_STRING;
		} else if (string.equals(I)) {
			type = CMLConstants.XSD_INTEGER;
		} else if (string.equals(D)) {
			type = CMLConstants.XSD_DATE;
		} else if (string.equals(F)) {
			type = CMLConstants.XSD_DOUBLE;
		} else if (string.equals(B)) {
			type = CMLConstants.XSD_BOOLEAN;
		} else {
			throw new RuntimeException("bad type: "+string);
		}
		return type;
	}

	private String createFloatField(String string, Integer width,
			Integer decimal) {
		String newString;
		if (width == null) {
			newString = "\\s*(\\-?\\d+\\.?\\d*)\\s*";
		} else {
			if (decimal == null) {
				throw new RuntimeException("decimal must be given: "+string);
			}
			int first = width - decimal -1;
			if (first < 1) {
				throw new RuntimeException("bad width/decimal: "+string);
			}
			newString = "(?=[ ]*\\-?\\d+)[ \\-\\d]{"+first+"}\\.\\d{"+decimal+"}";
		}
		return newString;
	}

	private String createIntegerField(Integer width) {
		String newString;
		if (width == null) {
			newString = "\\s*(\\-?\\d+)\\s*";
		} else {
			newString = "(?=[ ]*\\-?\\d+)[ \\-\\d]{"+width+"}";
		}
		return newString;
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
	
	public Element apply(LineContainer lineContainer) {
		throw new RuntimeException("subclass must override apply(LineContainer)");
	}

	public abstract CMLElement readLinesAndParse(JumboReader jumboReader);

	/** parses into scalars 
	 * 
	 * @param line
	 * @param jumboReader needed as the fields may include slashes
	 * @return
	 */
	public List<HasDataType> parseInlineHasDataTypes(JumboReader jumboReader) {
		this.jumboReader = jumboReader;
		return parseInlineHasDataTypes();
		
	}

	/** parses 
	 * 
	 * @param lineContainer
	 * @return
	 */
	public List<HasDataType> parseInlineHasDataTypes(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		return parseInlineHasDataTypes();
		
	}

	protected List<HasDataType> parseInlineHasDataTypes() {
		String line = peekLine();
		List<HasDataType> dataTypeList = null;
		if (line != null) {
			List<Field> fieldList = this.getFieldList();
			FieldType type0 = (fieldList.size() == 0) ? null : fieldList.get(0).getFieldType();
			LOG.trace("FT "+type0);
			// if the first fieldType is N (newline) allow line length 0
			if (line.trim().length() > 0 || FieldType.N.equals(type0)) {
				currentCharInLine = 0;
				if (pattern != null) {
					dataTypeList = parseWithPattern();
				} else {
					dataTypeList = parseWithFields();
				}
				// advance only on successful reads
				if (dataTypeList != null) {
					readLine();
				}
			}
		}
		return dataTypeList;
	}
	
	private String readLine() {
		String s = null;
		if (jumboReader != null) {
			s = jumboReader.readLine();
		} else if (lineContainer != null) {
			s = lineContainer.readLine();
		}
		return s;
	}
	
	public String peekLine() {
		String s = null;
		if (jumboReader != null) {
			s = jumboReader.peekLine();
		} else if (lineContainer != null) {
			s = lineContainer.peekLine();
		}
		return s;
	}
	

	private List<HasDataType> parseWithPattern() {
		String line = peekLine();
		Matcher matcher = pattern.matcher(line);
		List<HasDataType> list = null;
		if (matcher.matches()) {
			list = new ArrayList<HasDataType>();
			String localDictRef[] = (names == null) ? null : names.split(CMLConstants.S_SPACE);
			String dataType[] = (types == null || types.trim().length() == 0) ? null : types.split(CMLConstants.S_SPACE);
			LOG.trace("N T "+names+" / "+types);
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String matcherGroup = matcher.group(i);
				if (matcherGroup != null) {
					String dType = (dataType == null) ? null : dataType[i-1]; 
					CMLScalar scalar = createScalar(matcherGroup.trim(), dType);
					list.add(scalar);
					if (localDictRef != null && localDictRef.length == matcher.groupCount()) {
						scalar.setDictRef(localDictRef[i-1]);
					}
				}
			}
		}
		return list;
	}

	private CMLScalar createScalar(String value, String dType) {
		CMLScalar scalar = null;
		if (dType == null || dType.equals(CMLConstants.XSD_STRING)) {
			scalar = new CMLScalar(value);
		} else if (dType.equals(CMLConstants.XSD_DOUBLE)) {
			scalar = new CMLScalar(new Double(value));
		} else if (dType.equals(CMLConstants.XSD_INTEGER)) {
			scalar = new CMLScalar(new Integer(value));
		} else {
			throw new RuntimeException("unsupported data type in creating scalar: "+dType);
		}
		return scalar;
	}

	public void applyMarkup(LineContainer lineContainer) {
		throw new RuntimeException("must override this method in subclass: "+this.getClass());
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
		LOG.trace(field.toString());
//		String line = jumboReader.peekLine();
		String line = peekLine();
		if (field.getDataTypeClass() == null) {
			if (FieldType.N.equals(field.getFieldType())) { // newline
//				line = jumboReader.readLine();
				line = readLine();
				currentCharInLine = 0;
			} else {
				Integer width = field.getWidth();
				currentCharInLine += (width == null ? 0 : width);
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

	public void debug(String string, OutputLevel maxLevel) {
		if (Outputter.canOutput(outputLevel, maxLevel)) {
			LOG.debug(this.getClass().getSimpleName()+" ["+this.getId()+"] "+string);
		}
	}
	
	public void debug() {
		LOG.debug("LINEREADER "+(template == null ? "" : "in "+template.getId())+
				" formatType: "+formatType+
				" > "+content+"\n"+toString()+"\n>>Fields: "+fieldList.size());
		for (Field field :fieldList) {
			LOG.debug(""+field);
		}
	}
	
	public void debugLine(String title, OutputLevel level) {
//		LOG.debug("LINE "+jumboReader.getCurrentLineNumber());
//		jumboReader.peekLine();
//		debug(title+" ("+jumboReader.getCurrentLineNumber()+"):"+jumboReader.peekLine(), level);
	}


	public String toString() {
		return "lineType: "+lineType+
		((rows == null) ? "" : " rows:"+rows+"; ")+
		((columns == null) ? "" : " columns:"+columns+"; ")+
		((size == null) ? "" : " size:"+size+"; ")+
		((localDictRef == null) ? "" : " ldr:"+localDictRef+"; ")+
		((linesToRead == null) ? "" : " ltr:"+linesToRead+"; ")+
		((outputLevel == null) ? "" : " output:"+outputLevel+"; ")+
		"";
	}

}
