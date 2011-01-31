package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;

class ArrayLineReader extends LineReader {
	private static final Logger LOG = Logger.getLogger(ArrayLineReader.class);

	public static final String ARRAY_LINE_READER = "arrayLineReader";
	
	public ArrayLineReader(Element childElement) {
		super(ARRAY_LINE_READER, childElement, null);
	}
	
	public ArrayLineReader(List<Field> fieldList) {
		super(fieldList);
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		this.jumboReader = jumboReader;
		CMLArray array = readArray();
		if (array == null) {
			LOG.error("null array");
		} else {
			jumboReader.addElementWithDictRef(array, localDictRef);
		}
		return array;
	}
	
	/*
	typeChar:X; last:null; localDictRef:field0; endCharInLine:0; width:1; decimal:null; multiplier:null;
	    dataType:null; structureType:null
	typeChar:I; last:null; localDictRef:symminfo; endCharInLine:0; width:5; decimal:null; multiplier:10; 
	    dataType:class java.lang.Integer; structureType:class org.xmlcml.cml.element.CMLArray
	 */
	public CMLArray readArray() {
		Field arrayField = getArrayField();
		return readLinesIntoArray(arrayField);
	}

	private Field getArrayField() {
		Field arrayField = null;
		currentCharInLine = 0;
		LOG.trace(jumboReader.peekLine());
		if (fieldList.size() > 2) {
			throw new RuntimeException("Cannot parse more than 2 fields in array");
		} else if (fieldList.size() == 0) {
			throw new RuntimeException("No fields in array");
		} else if (fieldList.size() == 2) {
			arrayField = fieldList.get(1);
		} else {
			arrayField = fieldList.get(0);
		}
		if (arrayField.getMultiplier() == null) {
			throw new RuntimeException("multiplier required for array");
		}
		return arrayField;
	}

	private int getSkippedFieldWidth() {
		int width = 0;
		if (fieldList.size() == 2) {
			Field field = fieldList.get(0);
			if (field.getDataTypeClass() == null) {
				 width = field.getWidth();
			}
		}
		return width;
	}

	private CMLArray readLinesIntoArray(Field arrayField) {
		CMLArray totalArray = new CMLArray();
		int linesRead = 0;
		while (true) {
			CMLArray lineArray = readLineIntoArray(arrayField);
			if (totalArray.getDataTypeAttribute() == null) {
				totalArray.setDataType(lineArray.getDataType());
				totalArray.setDelimiter(lineArray.getDelimiter());
			}
			if (lineArray != null) {
				linesRead++;
				totalArray.append(lineArray);
				if (linesToRead != null && linesToRead == linesRead) {
					break;
				}
			} else {	// failed to read 
				if (linesToRead != null) {
					throw new RuntimeException("failed to read "+linesToRead+" lines; found: "+linesRead);
				}
				break;
			}
		}
		return totalArray;
	}

	/** not sure this is good design
	 * 
	 * @param jumboReader
	 * @param arrayField
	 * @return
	 */
	private CMLArray readLineIntoArray(Field arrayField) {
		int multiplier = arrayField.getMultiplier();
		CMLArray array = null;
		currentCharInLine += getSkippedFieldWidth();
		for (int i = 0; i < multiplier; i++) {
			HasDataType hasDataType = parseSingleScalarOrInlineArray(arrayField);
			if (hasDataType == null) {
				break;
			} else if (hasDataType instanceof CMLArray) {
				continue;
			}
			if (i == 0) {
				array = new CMLArray();
			}
			JumboReader.addScalarToArray((CMLScalar)hasDataType, array, delimiter, trim);
		}
		jumboReader.readLine();
		currentCharInLine = 0;
		return array;
	}


}
