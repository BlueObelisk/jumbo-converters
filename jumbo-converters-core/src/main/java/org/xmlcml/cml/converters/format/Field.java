package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Real;

public abstract class Field extends Element {
	private final static Logger LOG = Logger.getLogger(Field.class);
	
	private static final String LITERAL = "__literal";
	private static final String CHECK_FLAG = "check";
	public static final String DECIMAL = "decimal";
	public static final String DATA_TYPE_CLASS = "dataTypeClass";
	public static final String FIELD_TYPE = "fieldType";
	public static final String LOCAL_DICT_REF = "localDictRef";
	public static final String WIDTH_TO_READ = "widthToRead";
	public static final String UNITS_ATT = "units";
	public static final String EXPECTED_VALUE = "expectedValue";

	private static final String DATA_STRUCTURE_CLASS = "dataStructureClass";
	private static final String MULTIPLIER = "multiplier";

	/** this is a mess
	 * it's meant to be synchronized with Fortran
	 * @author pm286
	 *
	 */
	public enum FieldType {
		A("A"),
		B("B"),
		D("D"),
		E("E"),
		F("F"),
		G("G"),
		I("I"),
		N("/"),
		Q("Q"),
		S("S"),
		X("X"),
		FLAG("~"),
		;
		private String value;
		private FieldType(String v) {
			this.value = v;
		}
		public static FieldType getFieldType(String s) {
			FieldType t = null;
			for (FieldType typeChar : values()) {
				if (typeChar.value.equals(s)) {
					t = typeChar;
					break;
				}
			}
			return t;
		}
	}
	
	public static final String FIELD = "field";
	public static final String FIELD_PREFIX = "foo:field";

//	private static final Attribute UNITS = null;

	protected boolean check = false;
	protected Integer widthToRead;
	private Integer decimalToRead;
	protected Integer multiplier;
	protected FieldType fieldType;
	private Class<?> dataTypeClass;
	private String localDictRef;
	private String units = null;
	private int endCharInLine;
	private Integer totalWidthRead;
	private Class<?> dataStructureClass;
	private List<Field> fieldList;
	protected StringBuilder dataBuilder; // is this used?
	protected int dataStartChar;
	protected SimpleFortranFormat simpleFortranFormat;
	protected String expectedValue = null;

	private LineReader lineReader;

	
	public Field(SimpleFortranFormat sff, FieldType fieldType, LineReader lineReader) {
		super(FIELD);
		this.fieldType = fieldType;
		this.simpleFortranFormat = sff;
		init(lineReader);
	}

	protected Field(String tag, LineReader lineReader) {
		super(tag);
		init(lineReader);
	}
	
	private void init(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	public void dictRefAndUnits(String dictRef) {
		String[] bits = dictRef.split(CMLConstants.S_COMMA);
		String unitsx = (bits.length > 1) ? bits[1] : null;
		if (unitsx != null) {
			this.units = bits[1];
			this.addAttribute(new Attribute(UNITS_ATT, units));
		}
		this.localDictRef = bits[0];
		this.addAttribute(new Attribute(LOCAL_DICT_REF, localDictRef));
	}

	public Class<?> getDataTypeClass() {
		return dataTypeClass;
	}

	public FieldType getFieldType() {
		return fieldType;
	}
	public String getLocalDictRef() {
		return localDictRef;
	}

	public Integer getWidth() {
		return widthToRead;
	}

	public void setWidth(int width) {
		this.widthToRead = width;
		this.addAttribute(new Attribute(WIDTH_TO_READ, ""+widthToRead));
	}

	public Integer getDecimal() {
		return decimalToRead;
	}

	public void setDecimal(Integer decimal) {
		this.decimalToRead = decimal;
		this.addAttribute(new Attribute(DECIMAL, ""+decimal));
	}

	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
		if (multiplier != null) {
			this.addAttribute(new Attribute(MULTIPLIER, ""+multiplier));
		}
		if (isCMLElement(fieldType)) {
			this.setDataStructureClass((multiplier == null) ? CMLScalar.class : CMLArray.class);
		}
	}
	
	private boolean isCMLElement(FieldType fieldType) {
		return (fieldType != null && !FieldType.X.equals(fieldType));
	}

	public Integer getMultiplier() {
		return multiplier;
	}

	public Integer getTotalWidthRead() {
		if (totalWidthRead == null) {
			if (multiplier == null || widthToRead == null) {
				totalWidthRead = widthToRead;
			} else {
				totalWidthRead = widthToRead * multiplier;
			}
		}
		return totalWidthRead;
	}

	public void setDataTypeClass(FieldType fieldType) {
		dataTypeClass = null;
		if (fieldType == null) {
			throw new RuntimeException("null fieldType");
		} else if (fieldType == FieldType.A) {
			dataTypeClass = String.class;
		} else if (fieldType == FieldType.B) {
			dataTypeClass = Boolean.class;
		} else if (fieldType == FieldType.D) {
			dataTypeClass = DateTime.class;
		} else if (fieldType == FieldType.F) {
			dataTypeClass = Double.class;
		} else if (fieldType == FieldType.I) {
			dataTypeClass = Integer.class;
		} else if (fieldType == FieldType.Q) {
		} else if (fieldType == FieldType.S) {
			dataTypeClass = String.class;
		} else if (fieldType == FieldType.X) {
		} else {
			dataTypeClass = null;
			LOG.warn("Uninterpretable field (?skipped):"+fieldType);
		}
		if (dataTypeClass != null) {
			this.addAttribute(new Attribute(DATA_TYPE_CLASS, dataTypeClass.getSimpleName()));
		}
	}
	
	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String value) {
		this.expectedValue = value;
		if (value != null) {
			this.addAttribute(new Attribute(EXPECTED_VALUE, value));
		}
	}

	public boolean hasScalarType() {
		return (
			fieldType == FieldType.B || 
			fieldType == FieldType.D ||
			fieldType == FieldType.F ||
			fieldType == FieldType.I ||
			fieldType == FieldType.S
		);
	}

	CMLScalar readScalar(String line, int startCharInLine) {
		CMLScalar scalar = null;
		if (line != null) {
			endCharInLine = startCharInLine+widthToRead;
			String ff = null;
			// some lines don't use all fields
			if (endCharInLine > line.length()) {
				endCharInLine = line.length();
			}
			if (endCharInLine > startCharInLine) {
				ff = line.substring(startCharInLine, endCharInLine);
				if (ff != null && ff.trim().length() > 0) {
					try {
						scalar = this.createScalar(ff);
					} catch (Exception e) {
						throw new RuntimeException("Cannot parse ~"+ff+"~ in "+line, e);
					}
					addDictRefAndUnits(scalar);
				}
			}
		}
		return scalar;
	}
	
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
		this.addAttribute(new Attribute(FIELD_TYPE, ""+fieldType));
	}

	public int getCurrentCharInLine() {
		return endCharInLine;
	}
	
	public void setDataStructureClass(Class<?> clazz) {
		this.dataStructureClass = clazz;
		if (dataStructureClass != null) {
			this.addAttribute(new Attribute(DATA_STRUCTURE_CLASS, dataStructureClass.getSimpleName()));
		}
	}

	public Class<?> getDataStructureClass() {
		return this.dataStructureClass;
	}

	public CMLElement read(StringBuilder stringBuilder, int startCh) {
		this.dataBuilder = stringBuilder;
		this.dataStartChar = startCh;
		CMLElement element = null;
		if (stringBuilder == null) {
			throw new RuntimeException("null stringBuilder");
		} else if (fieldList != null) {
			element = readSubFields();
		} else if(multiplier == null) {
			element = this.createScalar(stringBuilder.substring(dataStartChar, dataStartChar+widthToRead));
			this.dataStartChar += widthToRead;
		} else {
//			element = JumboReader.createArray(getDataTypeClass(), stringBuilder.substring(dataStartChar), this);
			dataStartChar += this.getTotalWidthRead();
		}
		addDictRefAndUnits(element);
		return element;
	}

	private void addDictRefAndUnits(CMLElement element) {
//		if (element != null && localDictRef != null && !JumboReader.isMisread(element)) {
//			((HasDictRef)element).setDictRef(localDictRef);
//		}
//		if (element != null && units != null && !JumboReader.isMisread(element)) {
//			element.addAttribute(new Attribute(UNITS_ATT, units));
//		}
	}

	public int getStartChar() {
		return dataStartChar;
	}

	private CMLElement readSubFields() {
		CMLElement element;
		element = new CMLList();
		for (int i = 0; i < multiplier; i++) {
			CMLList childElement = new CMLList();
			element.appendChild(childElement);
			for (Field field : fieldList) {
				CMLElement grandChildElement = field.read(dataBuilder, dataStartChar);
				if (grandChildElement != null) {
					childElement.appendChild(grandChildElement);
				}
				dataStartChar = field.getStartChar();
			}
		}
		return element;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
		addFieldsAsXML();
	}

	private void addFieldsAsXML() {
		for (Field field : fieldList) {
			this.appendChild(field.copy());
		}
	}

	public String toString() {
		String s = "";
		if (lineReader != null) {
			s += "template: "+lineReader.getTemplateId()+" lineReader: "+lineReader.getId()+" ";
		}
		if (fieldList != null) {
			s += fieldList.size()+"*"+multiplier;
			for (Field field : fieldList) {
				s += "\n..."+field;
			}
		} else {
			s = "typeChar:"+fieldType+
			"; localDictRef:"+localDictRef+
			"; endCharInLine:"+endCharInLine +
			"; width:"+widthToRead+
			"; decimal:"+decimalToRead+
			"; multiplier:"+multiplier+
			"; dataType:"+dataTypeClass+
			"; structureType:"+dataStructureClass+
			"; expectedValue:"+expectedValue+
			"";
		}
		return s;
	}

	public CMLElement read(String string) {
		return read(new StringBuilder(string), 0);
	}

	public void setStartChar(int start) {
		this.dataStartChar = start;
	}

	public void setTotalWidthRead(int twr) {
		this.totalWidthRead = twr;
	}

	public void setCheck(boolean b) {
		this.addAttribute(new Attribute(CHECK_FLAG, (b ? "true" : "false")));
		check = b;
	}

	public boolean isCheck() {
		return check;
	}

	public CMLScalar createMisreadScalar(String value) {
		CMLScalar scalar = new CMLScalar(value);
//		JumboReader.addMisreadAttribute(scalar);
		this.addNonNullCMLXAttribute(DECIMAL, scalar);
		this.addNonNullCMLXAttribute(DATA_TYPE_CLASS, scalar);
		this.addNonNullCMLXAttribute(FIELD_TYPE, scalar);
		this.addNonNullCMLXAttribute(LOCAL_DICT_REF, scalar);
		this.addNonNullCMLXAttribute(WIDTH_TO_READ, scalar);
		return scalar;
	}

	public void addNonNullCMLXAttribute(String attName, CMLScalar scalar) {
		Attribute attribute = this.getAttribute(attName);
		if (attribute != null) {
			scalar.setCMLXAttribute(attName, attribute.getValue());
		}
	}

	/**
	 * @param dataType
	 * @param value
	 * @return
	 */
	public CMLScalar createScalar(String value) {
		Class<?> dataClass = this.getDataTypeClass();
		Integer decimalPlaces = this.getDecimal();
		Boolean check = this.isCheck();
		CMLScalar scalar = null;
		if (Double.class.equals(dataClass)) {
			if (check && decimalPlaces != null &&
					value.charAt(value.length()-1-decimalPlaces) != CMLConstants.C_PERIOD) {
				scalar = this.createMisreadScalar(value);
			} else {
				Double d = null;
				try {
					d = Real.parseDouble(value.trim());
				} catch (NumberFormatException e) {
					throw new RuntimeException("Cannot parse double: "+this, e);
				}
				scalar = new CMLScalar(d);
			}
		} else if (Integer.class.equals(dataClass)) {
			if (check && !Character.isDigit(value.charAt(value.length()-1))) {
				scalar = this.createMisreadScalar(value);
			} else {
				Integer ii = null;
				try {
					ii = Integer.parseInt(value.trim());
				} catch (NumberFormatException e) {
					throw new RuntimeException("Cannot parse integer: "+this, e);
				}
				scalar = new CMLScalar(ii);
			}
		} else if (Boolean.class.equals(dataClass)) {
			scalar = new CMLScalar(new Boolean(value));
		} else if (DateTime.class.equals(dataClass)) {
			scalar = new CMLScalar(JodaDate.parseDate(value));
		} else if (this.getDataTypeClass() == null) {
			if (FieldType.X.equals(this.getFieldType())) {
				// don't include
			} else if (FieldType.Q.equals(this.getFieldType())) { // Q space
				scalar = new CMLScalar((String)value);
//				JumboReader.addIsSpaceAttribute(scalar);
//				if (!this.getLocalDictRef().equals(value)) { // needs checking
//					JumboReader.addMisreadAttribute(scalar);
//				}
			} else {
				throw new RuntimeException("Unknown field type: "+this.getFieldType());
			}
		} else {
			scalar = new CMLScalar((String)value);
		}
		return scalar;
	}

	void saveQFieldOrSetLocalDictRef(String localDictRef) {
		if (localDictRef != null) {
			if (FieldType.Q.equals(fieldType)) {
				this.setExpectedValue(localDictRef.replaceAll(CMLConstants.S_SPACE, CMLConstants.S_UNDER));
				localDictRef = LITERAL;
			}
			dictRefAndUnits(localDictRef);
		}
	}
	
}
