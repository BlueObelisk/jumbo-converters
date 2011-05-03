package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.MarkupApplier;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Real;

public abstract class LineReader extends Element implements MarkupApplier {

	private final static Logger LOG = Logger.getLogger(LineReader.class);

	private static final String MAKE_ARRAY = "makeArray";
	private static final String NULL_ID = "nullId";
	private static final String ID = "id";

	protected static final String ELEMENT_TYPE = "elementType";
	private static final String FORMAT_TYPE = "formatType";
	private static final String NEWLINE = "newline";
	private static final String MULTIPLE = "multiple"; // deprecated
	private static final String NAME = "name";    // obsolete
	private static final String NAMES = "names";
	private static final String OUTPUT = "output";
	@SuppressWarnings("unused")
	private static final String UNTIL = "until";
	@SuppressWarnings("unused")
	private static final String WHILE = "while";
	private static final String LINES_TO_READ = "linesToRead"; // obsolete
	public static final String T_FLAG = CMLConstants.S_TILDE;
	private static final String LINE_READER = "lineReader";
	protected static final String TRUE = "true";
	private static final FormatType DEFAULT_FORMAT_TYPE = FormatType.NONE;
	private static final String REPEAT = "repeat";
	private static final String REPEAT_COUNT = "repeatCount"; // deprecated
	private static String DEFAULT_CONTENT = "{X}";


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

	protected Integer repeatCount = null;
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
	protected String newlineSymbol;
	protected String makeArray = null;
	protected String names;
	protected LineContainer lineContainer;
	protected OutputLevel outputLevel;
	protected Template template;
	protected RegexProcessor regexProcessor;

	public String getId() {
		return (id == null) ? NULL_ID : id;
	}

	public LineReader() {
		super(LINE_READER);
		init();
	}

	protected void init() {
		repeatCount = null;
		fieldList = new ArrayList<Field>();
	}

	public LineReader(String tag, Element lineReaderElement, Template template) {
		super(tag);
		init();
		this.template = template;
		this.lineReaderElement = lineReaderElement;
		this.content = lineReaderElement.getValue();
		processAttributesAndContent();
		addDefaults();
	}

	public LineReader(List<Field> fieldList) {
		this();
		this.fieldList = fieldList;
		addFieldsAsXMLChildren();
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

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setType(LineType type) {
		this.lineType = type;
	}

	public String getNames() {
		return names;
	}

	private void addDefaults() {
		if (formatType == null) {
			formatType = DEFAULT_FORMAT_TYPE;
		}
		if (repeatCount == null) {
			repeatCount = 1;
		}
	}

	private void processAttributesAndContent() {
		Template.checkIfAttributeNamesAreAllowed(lineReaderElement, new String[]{
			ID,
			FORMAT_TYPE,
			LINES_TO_READ,
			MAKE_ARRAY,
			MULTIPLE,
			NEWLINE,
			NAME,
			NAMES,
			OUTPUT,
			REPEAT,
			REPEAT_COUNT,
		});
		processAttributeNewline();
		processAttributeFormatType();
		processAttributeLinesToRead();
		processAttributeRepeatCount();
		processAttributeNames();
		processAttributeMakeArrays();
		processAttributeId();
		processAttributeOutputLevel();
		processContent();
		outputLevel = Outputter.extractOutputLevel(this);
	}

	private void processAttributeMakeArrays() {
		this.makeArray = lineReaderElement.getAttributeValue(MAKE_ARRAY);
		if (makeArray != null) {
			this.addAttribute(new Attribute(MAKE_ARRAY, makeArray));
		}
	}

	private void processAttributeNewline() {
		this.newlineSymbol = lineReaderElement.getAttributeValue(NEWLINE);
		if (newlineSymbol == null) {
			this.newlineSymbol = lineReaderElement.getAttributeValue(MULTIPLE);
			if (newlineSymbol != null) {
				LOG.warn("multiple is deprecated - use newline");
			}
		}
		if (newlineSymbol != null) {
			newlineSymbol = Template.regexEscape(newlineSymbol);
			this.addAttribute(new Attribute(NEWLINE, newlineSymbol));
		}
	}

	private void processAttributeNames() {
		this.names = lineReaderElement.getAttributeValue(NAMES);
		if (names != null) {
			this.addAttribute(new Attribute(NAMES, names));
		}
	}

	private void processAttributeFormatType() {
		formatType = null;
		String formatTypeS = lineReaderElement.getAttributeValue(FORMAT_TYPE);
		if (formatTypeS == null) {
			formatType = FormatType.REGEX;
			formatTypeS = formatType.toString();
		} else {
			formatType = FormatType.valueOf(formatTypeS);
			if (formatType == null) {
				throw new RuntimeException("Unknown formatType: "+formatType);
			}
		}
		this.addAttribute(new Attribute(FORMAT_TYPE, formatTypeS));
	}

	private void processAttributeLinesToRead() {
		String linesToReadS = lineReaderElement.getAttributeValue(LINES_TO_READ);
		if (linesToReadS != null) {
			LOG.warn("@linesToRead is deprecated, use @repeat");
			lineReaderElement.addAttribute(new Attribute(REPEAT, linesToReadS));
		}
	}

	private void processAttributeRepeatCount() {
		repeatCount = null;
		String repeatCountS = lineReaderElement.getAttributeValue(REPEAT);
		if (repeatCountS == null) {
			repeatCountS = lineReaderElement.getAttributeValue(REPEAT_COUNT);
			if (repeatCountS != null) {
				LOG.warn("repeatCount is deprecated, use reapeat");
			}
		}
		if (repeatCountS != null) {
			if (repeatCountS.equals("*")) {
				repeatCount = Integer.MAX_VALUE-1;
			} else {
				try {
					repeatCount = new Integer(repeatCountS);
				} catch (Exception e) {
					throw new RuntimeException("bad @repeatCount: "+repeatCountS);
				}
			}
			this.addAttribute(new Attribute(REPEAT, repeatCountS));
		}
	}

	private void processAttributeId() {
		this.id = lineReaderElement.getAttributeValue(ID);
		if (id == null) {
			if (content.contains(CMLConstants.S_COLON)) {
				CMLUtil.debug(lineReaderElement, "LINEREADER");
				LOG.warn("Missing id on "+this.getLocalName());
			}
			id = "missingID";
		}
		if (id != null) {
			this.addAttribute(new Attribute(ID, id));
		}
		LOG.trace("ID: "+id);
	}

	private void processAttributeOutputLevel() {
		String outputS = lineReaderElement.getAttributeValue(Outputter.OUTPUT);
		if (outputS != null) {
			this.addAttribute(new Attribute(Outputter.OUTPUT, outputS));
		}
	}

	private void processContent() {
		if (formatType == null) {
//			throw new RuntimeException("No formatType given");
		} else if (FormatType.FORTRAN.equals(formatType)) {
			createFortranFields();
		} else if (FormatType.REGEX.equals(formatType)) {
			if (content == null || content.trim().equals("")) {
				content = DEFAULT_CONTENT;
			}
			regexProcessor = new RegexProcessor(content, newlineSymbol);
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

// ============================== reading ======================


	static CMLScalar createScalar(String value, String dType) {
		value = value.trim();
		CMLScalar scalar = null;
		if (dType == null || dType.equals(CMLConstants.XSD_STRING)) {
			scalar = new CMLScalar(value);
		} else if (dType.equals(CMLConstants.XSD_DOUBLE) || dType.equals(RegexField.E)) {
			try {
				double dd = Real.parseDouble(value);
				scalar = new CMLScalar(dd);
			} catch (Exception e) {
				throw new RuntimeException("bad float", e);
			}
		} else if (dType.equals(CMLConstants.XSD_INTEGER)) {
			scalar = new CMLScalar(new Integer(value));
		} else if (dType.equals(CMLConstants.XSD_DATE)) {
			scalar = new CMLScalar(JodaDate.parseDate(value));
		} else if (dType.equals(RegexField.X)) {
			scalar = new CMLScalar(value);
		} else {
			throw new RuntimeException("unsupported data type in creating scalar: "+dType);
		}
		return scalar;
	}

	public void applyMarkup(LineContainer lineContainer) {
		throw new RuntimeException("must override this method in subclass: "+this.getClass());
	}

	public void applyMarkup(Element element) {
		// not relevant
	}


	public void debug(String string, OutputLevel maxLevel) {
		if (Outputter.canOutput(outputLevel, maxLevel)) {
			LOG.debug(this.getClass().getSimpleName()+" ["+this.getId()+"] "+string);
		}
	}

	public void debug() {
		LOG.debug("LINEREADER "+(template == null ? "" : "in "+template.getId())+"\n"+
				((regexProcessor == null) ? "" : " regexProcessor: "+regexProcessor.toString()+"\n")+
				" formatType: "+formatType+
				" > "+content+"\n"+toString()+"\n>>Fields: "+fieldList.size());
		for (Field field :fieldList) {
			LOG.debug(""+field);
		}
		if (regexProcessor != null) {
			regexProcessor.debug();
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
		((repeatCount == null) ? "" : " ltr:"+repeatCount+"; ")+
		((outputLevel == null) ? "" : " output:"+outputLevel+"; ")+
		"";
	}

	public String getTemplateId() {
		return (template == null) ? null : template.getId();
	}

}
