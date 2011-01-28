package org.xmlcml.cml.converters.format;

import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.format.Field.FieldType;

/**
 * reads a FORTRAN format string and parses it into List<Field>
 * Fields can be nested
 * @author pm286
 *
 */
public class SimpleFortranFormat {
	private static final String FORTRAN_FORMAT = "fortranFormat";
	private final static Logger LOG = Logger.getLogger(SimpleFortranFormat.class);
	private static final char L_NAME_DELIM = CMLConstants.C_LCURLY;
	private static final char R_NAME_DELIM = CMLConstants.C_RCURLY;
	private static final char CHECK_FLAG_CHAR = CMLConstants.C_ATSIGN;
	private static final Integer MAX_WIDTH = 1024;

	public enum FortranType {
		A,
		B,
		E,
		F,
		G,
		I,
		L,
		N,
		Q,
		S,
		T,
		X,
		;
	}
	
	public static Pattern APOS_STRING_PATTERN = Pattern.compile("([^']*)'([^']*)'");
	public static Pattern W_DOT_D_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)");

	
	private String format;
	private String transformedFormat;
	private List<Field> fieldList;
	private int formatStartChar;
	private Integer formatMultiplier;
	private StringBuilder formatStringBuilder;
	private Element element;

	public SimpleFortranFormat(String format) {
		this.format = format;
		fieldList = new ArrayList<Field>();
		parseFormat();
	}

	/**
Purpose 	Edit Descriptors
Reading/writing INTEGERs 	Iw 	Iw.m
Reading/writing REALs 	Decimal form 	Fw.d
Exponential form 	Ew.d 	Ew.dEe
Scientific form 	ESw.d 	ESw.dEe
Engineering form 	ENw.d 	ENw.dEe
Reading/writing LOGICALs 	Lw
Reading/writing CHARACTERs 	A 	Aw
Positioning 	Horizontal 	nX
Tabbing 	Tc 	TLc and TRc
Vertical 	/
Others 	Grouping 	r(....)
Format Scanning Control 	:
Sign Control 	S, SP and SS
Blank Control 	BN and BZ	 

We support Iw, Fw.d, Ew.d (D,G,H), A, Aw, nX, /
*/
	private void parseFormat() {
		if (!format.startsWith(CMLConstants.S_LBRAK) ||
				!format.endsWith(CMLConstants.S_RBRAK)) {
			throw new RuntimeException("format must start/end with brackets :"+format+":");
		}
		this.transformedFormat = format;
		removeNewlines();
		extractAposQuotes();
		extractFields();
		element = new Element(FORTRAN_FORMAT);
		addFieldsAsXMLChildren();
	}
	
	private void addFieldsAsXMLChildren() {
		for (Field field : fieldList) {
			element.appendChild(field);
		}
	}
	
	public Element getElement() {
		return element;
	}

	private void removeNewlines() {
		this.transformedFormat = transformedFormat.replaceAll(CMLConstants.S_NEWLINE, CMLConstants.S_EMPTY);
	}

	private void extractFields() {
		// remove brackets
		formatStringBuilder = new StringBuilder(transformedFormat.substring(1, transformedFormat.length()-1));
		formatStartChar = 0;
		while (formatStartChar < formatStringBuilder.length()) {
			parseNextField();
		}
	}

	private void parseNextField() {
		eatCommasAndSpaces();
		formatMultiplier = readDigits();
		if (formatStringBuilder.length() == formatStartChar) {
			throw new RuntimeException("empty typeCharacter in: "+format);
		} else if (formatStringBuilder.charAt(formatStartChar) == CMLConstants.C_LBRAK) {
			parseBracketedField();
		} else {
			readAndAddField();
		}
	}

	private void readAndAddField() {
		Field field = null;
		String charx = (""+formatStringBuilder.charAt(formatStartChar)).toUpperCase();
		formatStartChar++;
		FieldType fieldType = Field.FieldType.getFieldType(charx);
		if (fieldType == null) {
			throw new RuntimeException("cannot parse field type:"+charx);
		} else if (fieldType.toString().equals(FortranType.A.toString()) ||
		           fieldType.toString().equals(FortranType.I.toString()) ||
		           fieldType.toString().equals(FortranType.Q.toString()) ||
		           fieldType.toString().equals(FortranType.L.toString())) {
			field = new WidthField(this, fieldType);
		} else if (fieldType.toString().equals(FortranType.E.toString()) ||
		           fieldType.toString().equals(FortranType.F.toString())) {
			field = new FloatField(this, fieldType);
		} else if (fieldType.toString().equals(FortranType.N.toString())) {
			field = new NewlineField(this, fieldType);
		} else if (fieldType.toString().equals(FortranType.X.toString())) {
			field = new SpaceField(this, fieldType);
		} else if (
				fieldType.toString().equals(FortranType.B.toString()) ||
				fieldType.toString().equals(FortranType.S.toString()) ||
				fieldType.toString().equals(FortranType.T.toString())) {	
			throw new RuntimeException("format not supported: "+fieldType);
		} else {
			throw new RuntimeException("Cannot interpret type: "+fieldType);
		}
		field.setMultiplier(formatMultiplier);
		readAndAddFieldNameAndCheckFlag(field);
		if (field != null) {
			fieldList.add(field);
		}
	}

	private void readAndAddFieldNameAndCheckFlag(Field field) {
		readAndAddFieldName(field);
		readAndAddCheckFlag(field);
	}

	private void readAndAddCheckFlag(Field field) {
		if (formatStartChar < formatStringBuilder.length() && 
				formatStringBuilder.charAt(formatStartChar) == CHECK_FLAG_CHAR) {
			field.setCheck(true);
			formatStartChar++;
		}
	}

	private void readAndAddFieldName(Field field) {
		if (formatStartChar < formatStringBuilder.length() && 
				formatStringBuilder.charAt(formatStartChar) == L_NAME_DELIM) {
			int idx = formatStringBuilder.indexOf(""+R_NAME_DELIM, formatStartChar);
			if (idx == -1) {
				throw new RuntimeException("Cannot balance name after "+formatStringBuilder.substring(formatStartChar));
			}
			String localDictRef = formatStringBuilder.substring(formatStartChar+1, idx);
			field.saveQFieldOrSetLocalDictRef(localDictRef);
			formatStartChar = idx+1;
		} else {
			field.setLocalDictRef(Field.FIELD_PREFIX+fieldList.size());
		}
		LOG.trace("DBG"+field.toString());
	}

	private void eatCommasAndSpaces() {
		while(formatStartChar < formatStringBuilder.length()) { 
			char c = formatStringBuilder.charAt(formatStartChar++);
			if (c != CMLConstants.C_COMMA && c != CMLConstants.C_SPACE) {
				formatStartChar--;
				break;
			}
		}
	}

	/** reads from startChar while current character is digit
	 * returns with endChar set at first non-digit or end
	 */
	private Integer readDigits() {
		int startChar0 = formatStartChar;
		Integer multiplier = null;
		if (formatStringBuilder.charAt(startChar0) == '*') {
			multiplier = Integer.MAX_VALUE-1;
			formatStartChar++;
		} else {
			while (formatStartChar < formatStringBuilder.length() && Character.isDigit(formatStringBuilder.charAt(formatStartChar))) {
				formatStartChar++;
			}
			multiplier = (formatStartChar == startChar0) ? null : new Integer(formatStringBuilder.substring(startChar0, formatStartChar));
		}
		return  multiplier;
	}

	Integer readWidth() {
		Integer width = readDigits();
		if (width == null) {
			LOG.trace("no width given: "+this.transformedFormat);
			width = MAX_WIDTH;
		}
		return width;
	}

	private void parseBracketedField() {
		int idx = org.xmlcml.euclid.Util.indexOfBalancedBracket(
				CMLConstants.C_LBRAK, formatStringBuilder.substring(formatStartChar));
		if (idx == -1) {
			throw new RuntimeException("Unbalanced brackets in: "+format);
		} else if (formatMultiplier == null) {
			throw new RuntimeException("Cannot parse unmultiplied brackets");
		} else {
			int bracketChar = formatStartChar+(idx+1);
			String bracketContents = formatStringBuilder.substring(formatStartChar, bracketChar);
			LOG.trace(bracketContents);
			formatStartChar = bracketChar; // skip to field after bracket
			SimpleFortranFormat sff = new SimpleFortranFormat(bracketContents);
			Field bracketedField = new BracketedField(this);
			bracketedField.setMultiplier(formatMultiplier);
			bracketedField.setFieldList(sff.getFieldList());
			readAndAddFieldName(bracketedField);
			fieldList.add(bracketedField);
		}
	}

	private void extractAposQuotes() {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = APOS_STRING_PATTERN.matcher(transformedFormat);
		List<String> aFieldList = new ArrayList<String>();
		replaceAllAposStringsByQ(sb, matcher, aFieldList);
		transformedFormat = sb.toString();
	}

	private void replaceAllAposStringsByQ(StringBuilder sb,
			Matcher matcher, List<String> aFieldList) {
		int start = 0;
		while (matcher.find(start)) {
			String aField = matcher.group(2);
			if (aField.length() == 0) {
				throw new RuntimeException("Zero length string constant ('') not allowed in: "+format);
			}
			sb.append(matcher.group(1));
			aFieldList.add(aField);
			String quotedField = matcher.group(2);
			sb.append(FortranType.Q.toString()+quotedField.length());
			sb.append(L_NAME_DELIM+quotedField+R_NAME_DELIM);
			start = matcher.end();
		}
		String rest = transformedFormat.substring(start);
		if (rest.indexOf(CMLConstants.S_APOS) != -1) {
			throw new RuntimeException("unbalanced APOS (') in format: "+format);
		}
		sb.append(rest);
	}

	public String getTransformedFormat() {
		return transformedFormat;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void debug() {
		LOG.debug("ParsedFields "+fieldList.size());
		for (Field field : fieldList) {
			LOG.debug("field> "+field);
		}
	}

	void parseFloatFormatInto(FloatField floatField) {
		String s = this.formatStringBuilder.toString();
		Matcher matcher = W_DOT_D_PATTERN.matcher(s);
		if (matcher.find(formatStartChar-1)) {
			floatField.setWidth(new Integer(matcher.group(1)));
			floatField.setDecimal(new Integer(matcher.group(2)));
			formatStartChar += matcher.group(1).length()+1+matcher.group(2).length();
		}
	}

	Integer parseSpaceFormatInto(SpaceField spaceField, FieldType fieldType) {
		Integer width = null;
		if (fieldType.equals(FieldType.X)) {
			width = formatMultiplier;
			if (width == null) {
				throw new RuntimeException("Multiplier required for X");
			}
			formatMultiplier = null;
		} else if (fieldType.equals(FieldType.Q)) {
			width = this.readWidth();
		}
		return width;
	}
}
