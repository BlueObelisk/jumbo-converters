package org.xmlcml.cml.converters.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.cml.tools.TableTool;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;

import fortran.format.FortranFormat;

public class JumboReader {
	private static final String UNKNOWN = "unknown";

	private static Logger LOG = Logger.getLogger(JumboReader.class);

	private CMLDictionary dictionary;
	private String dictionaryPrefix;
	private List<String> lines;
	private int currentLineNumber;
	private int previousLineNumber;
	private CMLElement parentElement;

	private DictionaryTool dictionaryTool;

	public static final Boolean DONT_ADD = false;
	public static final Boolean ADD = true;

	/** Use for single lines
	 * 
	 */
	public JumboReader() {
		
	}

	/** 
	 * forces caller to set dictionary and prefix as well as lines to be parsed
	 * @param dictionaryTool
	 * @param prefix
	 * @param lines
	 */
	public JumboReader(CMLDictionary dictionary, String prefix, List<String> lines) {
		this();
		setLinesToBeParsed(lines);
		setDictionary(dictionary);
		setDictionaryPrefix(prefix);
	}
	
	/** 
	 * forces caller to set dictionary and prefix as well as lines to be parsed
	 * @param dictionary
	 * @param prefix
	 * @param line single line
	 */
	public JumboReader(CMLDictionary dictionary, String prefix, String line) {
		this();
		if (line == null) {
			throw new RuntimeException("line should not be null");
		}
		setLinesToBeParsed(new String[]{line});
		setDictionary(dictionary);
		setDictionaryPrefix(prefix);
	}
	
	/** 
	 * forces caller to set dictionary and prefix as well as lines to be parsed
	 * @param dictionary
	 * @param prefix
	 * @param line single line
	 */
	public JumboReader(CMLDictionary dictionary, String prefix, String[] line) {
		this();
		setLinesToBeParsed(line);
		setDictionary(dictionary);
		setDictionaryPrefix(prefix);
	}
	
	public CMLDictionary getDictionary() {
		return dictionary;
	}

	/** a dictionary is mandatory and can be set in the constructor
	 * 
	 * @param dictionary
	 * @return
	 */
	public JumboReader setDictionary(CMLDictionary dictionary) {
		if (dictionary == null) {
			throw new RuntimeException("null dictionary");
		}
		this.dictionary = dictionary;
		this.dictionaryTool = DictionaryTool.getOrCreateTool(dictionary);
		return this;
	}

	public String getDictionaryPrefix() {
		return dictionaryPrefix;
	}

	/**
	 * sets default dictionary prefix. 
	 * dictionaryPrefix is required and can be set in the constructor
	 * @param dictionaryPrefix
	 * @return
	 */
	public JumboReader setDictionaryPrefix(String dictionaryPrefix) {
		if (dictionaryPrefix == null) {
			throw new RuntimeException("null dictionaryPrefix");
		}
		this.dictionaryPrefix = dictionaryPrefix;
		return this;
	}

	public List<String> getLinesToBeParsed() {
		return lines;
	}

	/**
	 * (re)sets the lines to parse
	 * resets currentLineNumber to 0
	 * @param lines
	 * @return
	 */
	public JumboReader setLinesToBeParsed(List<String> lines) {
		if (lines == null) {
			throw new RuntimeException("null lines");
		}
		this.lines = lines;
		return this;
	}

	/**
	 * (re)sets the lines to parse
	 * resets currentLineNumber to 0
	 * @param lines
	 * @return
	 */
	public JumboReader setLinesToBeParsed(String[] lines) {
		if (lines == null) {
			throw new RuntimeException("null lines");
		}
		List<String> lineList = new ArrayList<String>();
		for (String line : lines) {
			lineList.add(line);
		}
		this.setLinesToBeParsed(lineList);
		return this;
	}

	/** current line number
	 * initially set to 0 whenever new lines read in
	 * reset after parse to next unparsed line
	 * resettable by setCurrentLineNumber()
	 * @return
	 */
	public int getCurrentLineNumber() {
		return currentLineNumber;
	}

	/** set point to start parsing from
	 * resets previousLineNumber to currentLineNumber
	 * @param currentLineNumber
	 */
	public void setCurrentLineNumber(int currentLineNumber) {
		if (currentLineNumber < 0 || currentLineNumber >= lines.size()) {
			throw new RuntimeException("line number must be in range 0 -> "+(lines.size()-1));
		}
		this.currentLineNumber = currentLineNumber;
	}
	
	public int getLinesRead() {
		return currentLineNumber - previousLineNumber;
	}

	public CMLElement getParentElement() {
		return parentElement;
	}

	/** the parent object for all ADD operations
	 * 
	 * @param parentElement
	 */
	public void setParentElement(CMLElement parentElement) {
		if (parentElement == null) {
			throw new RuntimeException("null parentElement");
		}
		this.parentElement = parentElement;
	}

	/**
	 * @return line number before the operation was commenced
	 */
	public int getPreviousLineNumber() {
		return previousLineNumber;
	}
	
	/** immediate wrapper for the iChemLabs parser
	 * Should not normally be used for JUMBO parsing
	 * @param format
	 * @param line
	 * @return list of parsed objects (classes = String, Double, Integer)
	 */
	public static List<Object> parseFortranLine(String format, String line) {
		String newFormat = JumboReader.expandStringsInFormatIntoNX(format);
		List<Object> results = new ArrayList<Object>();
		FortranFormat fortranFormat = null;
		try {
			fortranFormat = new FortranFormat(newFormat);
			results = fortranFormat.parse(line);
		} catch (Exception e) {
			// might be due to D notation
			Exception ee = null;
			try {
				line = convertScientificNotation(line);
				results = fortranFormat.parse(line);
			} catch (Exception e1) {
				ee = e1;
			}
			if (ee != null) {
				throw new RuntimeException("Cannot parse fortran :"+line+":", e);
			}
		}
		return results;
	}

	private static String convertScientificNotation(String line) {
		line = line.replaceAll("D\\+", "E\\+");
		line = line.replaceAll("D\\-", "E\\-");
		return line;
	}

	/**
	 * replaces all character strings ('abc') by equivalent spaces (3X)
	 * @param format
	 * @return
	 */
	public static String expandStringsInFormatIntoNX(String format) {
		char apos = '\'';
		int c0 = -1;
		int c1 = -1;
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		while (true) {
			c0 = format.indexOf(apos, c1);
			c1 = Math.max(0, c1);
			if (c0 < 0) {
				sb.append(format.substring(c1));
				break;
			}
			sb.append(format.substring(c1, c0));
			c1 = format.indexOf(apos, c0+1);
			if (c1 == -1) {
				throw new RuntimeException("unbalanced quotes in "+format);
			}
			sb.append(","+(c1-c0-1)+"X,");
			c1++;
			c0 = c1;
		}
		sb.append(")");
		String s = sb.toString();
		s = s.replaceAll("\\,+", ",");
		s = s.replaceAll("\\(,", "\\(");
		return s;
	}

	/**
	 * creates a name-value pair from applying pattern to currentLine
	 * @param pattern (required to have two groups, name and value)
	 * @param add should the CMLElement be added to the parentElement
	 * @return the CMLList or CMLScalar
	 */
	public CMLElement parseNameValue(Pattern pattern, String dataType, boolean add) {
		resetPreviousLineNumber();
		String lineToParse = lines.get(currentLineNumber++);
		CMLScalar scalar = null;
		if (lineToParse.trim().length() != 0) {
			scalar = parseAndAddNameValue(pattern, dataType, add, lineToParse);
		}
		return scalar;
	}

	private CMLScalar parseAndAddNameValue(Pattern pattern, String dataType,
			boolean add, String lineToParse) {
		CMLScalar scalar;
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.matches() || matcher.groupCount() != 2) {
			throw new RuntimeException("Cannot parse {"+lineToParse+"} with "+pattern);
		}
		String dictRef = matcher.group(1).trim();
		dictRef = dictRef.replaceAll("[^a-zA-Z0-9\\.\\-]", CMLConstants.S_UNDER);
		String value = matcher.group(2).trim();
		if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
			scalar = new CMLScalar(Double.parseDouble(value.toString()));
		} else if (CMLConstants.XSD_INTEGER.equals(dataType)) {
			scalar = new CMLScalar(Integer.parseInt(value.toString()));
		} else {
			scalar = new CMLScalar(value);
		}
		addDictRef(scalar, dictRef);
		addElementToParentElement(add, scalar);
		return scalar;
	}

	/**
	 * creates a list of CMLScalars from parsing the currentLine
	 * If there is just one CMLScalar, returns that
	 * for 2 or more returns a CMLList with the CMLScalars as children
	 * @param format
	 * @param names indicating type and localDictRef 
	 * @param add should the CMLElement be added to the parentElement
	 * @return the CMLList or CMLScalar
	 */
	public CMLElement parseScalars(String format, String[] names, boolean add) {
		resetPreviousLineNumber();
		String lineToParse = lines.get(currentLineNumber++);
		List<Object> results = JumboReader.parseFortranLine(format, lineToParse);
		CMLElement element = convertToSingleScalarOrList(results, names, null);
		addElementToParentElement(add, element);
		return element;
	}

	public CMLElement parseScalars(Pattern pattern, String[] names, boolean add) {
		resetPreviousLineNumber();
		String lineToParse = lines.get(currentLineNumber++);
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.find()) {
			throw new RuntimeException("Cannot parse :"+lineToParse+": with :"+pattern+":");
		} 
		List<Object> results = makeResults(matcher);
		CMLElement element = convertToSingleScalarOrList(results, names, pattern);
		addElementToParentElement(add, element);
		return element;
	}
	
	public CMLArray parseMultipleLinesToArray(String format, int fieldsToRead, String localDictRef, String dataType, boolean add) {
		CMLArray array = parseMultipleLinesToArray(format, fieldsToRead, dataType);
		addDictRef(array, dictionaryPrefix, localDictRef);
		if (add) {
			parentElement.appendChild(array);
		}
		return array;
	}

	public CMLMatrix parseMatrix(
			int rows, int cols, String fortranFormat, String dataType, String dictRef, boolean add) {
		resetPreviousLineNumber();
		CMLMatrix matrix = null;
		if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
			matrix = makeDoubleMatrix(rows, cols, fortranFormat, dataType);
		} else if (CMLConstants.XSD_INTEGER.equals(dataType)) {
			matrix = makeIntegerMatrix(rows, cols, fortranFormat, dataType);
		} else {
			throw new RuntimeException("Only double and integer matrices allowed, found: "+dataType);
		}
		
		addElementWithDictRef(matrix, dictRef);
		return matrix;
	}

	public void addElementWithDictRef(CMLElement element, String dictRef) {
		addElementToParentElement(true, element);
		addDictRef(element, dictRef);
	}

	private CMLMatrix makeDoubleMatrix(int rows, int cols,
			String fortranFormat, String dataType) {
		CMLMatrix matrix;
		double[][] matrixx = new double[rows][];
		for (int i = 0; i < rows; i++) {
			CMLArray array = this.parseMultipleLinesToArray(
					fortranFormat, cols, dataType);
			matrixx[i] = array.getDoubles();
		}
		matrix = new CMLMatrix(matrixx);
		return matrix;
	}

	private CMLMatrix makeIntegerMatrix(int rows, int cols,
			String fortranFormat, String dataType) {
		CMLMatrix matrix;
		int[][] matrixx = new int[rows][];
		for (int i = 0; i < rows; i++) {
			CMLArray array = this.parseMultipleLinesToArray(
					fortranFormat, cols, dataType);
			matrixx[i] = array.getInts();
		}
		matrix = new CMLMatrix(matrixx);
		return matrix;
	}

	/**
	 * reads rows of elements expecting some/all 
	 * elementSymbol, atnum, label, x, y, z
	 * this order would give serial {0, 1, 2, 3, 4}
	 * order can be arbitrary. -1 is missing
	 * pointers {-1, 1, 0, 2, 3, 4} expects {label, atnum, x, y, z}
	 * @param natoms
	 * @param format
	 * @param pointers 
	 * @param add
	 * @return
	 */
	public CMLMolecule parseMoleculeAsColumns(int natoms, String format, int[] pointers, boolean add) {
		CMLMolecule molecule = new CMLMolecule();
		resetPreviousLineNumber();
		for (int i = 0; i < natoms; i++) {
			List<Object> scalars = JumboReader.parseFortranLine(format, lines.get(currentLineNumber++));
			CMLAtom atom = new CMLAtom("a"+(i+1));
			setElementType(pointers, scalars, atom);
			addAtomLabel(pointers, scalars, atom);
			Double x =      (pointers[3] < 0 ) ? null : (Double)scalars.get(pointers[3]);
			Double y =      (pointers[4] < 0 ) ? null : (Double)scalars.get(pointers[4]);
			Double z =      (pointers[5] < 0 ) ? null : (Double)scalars.get(pointers[5]);
			atom.setXYZ3(new Point3(x, y, z));
			molecule.addAtom(atom);
		}
		addElementToParentElement(add, molecule);
		return molecule;
	}

	private void addAtomLabel(int[] pointers, List<Object> scalars, CMLAtom atom) {
		String labelS = (pointers[2] < 0 ) ? null : (String) scalars.get(pointers[2]);
		if (labelS != null) {
			CMLLabel label = new CMLLabel();
			label.setCMLValue(labelS);
			atom.addLabel(label);
		}
	}

	private Integer setElementType(int[] pointers, List<Object> scalars,
			CMLAtom atom) {
		String elsymS = (pointers[0] < 0 ) ? null : (String) scalars.get(pointers[0]);
		Integer atNum = (pointers[1] < 0 ) ? null : (int) Math.round((Double)scalars.get(1));
		String elementType = null;
		if (elsymS != null) {
			elementType = elsymS.trim();
		} else if (atNum != null) {
			elementType = ChemicalElement.getElement(atNum).getSymbol();
		}
		if (elementType == null) {
			throw new RuntimeException("no valid element symbol/number");
		}
		atom.setElementType(Util.capitalise(elementType));
		return atNum;
	}

	public CMLArray parseArray(String format, String dataType, String name, boolean add) {
		resetPreviousLineNumber();
		CMLArray array = parseArray(format, lines.get(currentLineNumber++), dataType);
		addElementToParentElement(add, array);
		addDictRef(array, getDictionaryPrefix(), name);
		return array;
	}

	public CMLArray readArrayGreedily(String fortranFormat, String dataType) {
		CMLArray array = this.parseMultipleLinesToArray(
				fortranFormat, -1, dataType);
		return array;
	}

	public CMLArrayList parseTableColumnsAsArrayList(String format, int linesToRead, String[] names, boolean add) {
		CMLArrayList arrayList = new CMLArrayList();
		resetPreviousLineNumber();
		List<CMLArray> arrays = this.createTableColumns(format, linesToRead, names);
		for (CMLArray array : arrays) {
			arrayList.addArray(array);
		}
		addElementToParentElement(add, arrayList);
		return arrayList;
	}

	/**
	 * reads currentLine, extracts double with format, creates dictRef and adds to parent
	 * @param format single F statemnet (with optional strings and spaces)
	 * @param dictRef local dictRef
	 */
	public void addDouble(String format, String dictRef) {
		this.parseScalars(format, new String[]{"F."+dictRef}, JumboReader.ADD);
	}
	
	/** read until non-blank line (trimmed if necessary).
	 * currentLine is positioned at this line
	 * 
	 */
	public void readEmptyLines() {
		resetPreviousLineNumber();
		while (currentLineNumber < lines.size()) {
			if (lines.get(currentLineNumber++).trim().length() != 0) {
				currentLineNumber--;
				break;
			}
		}
	}

	public boolean hasMoreLines() {
		return currentLineNumber < lines.size();
	}

	public String peekLine() {
		return (lines.size() > currentLineNumber) ? lines.get(currentLineNumber) : null;
	}

	public String readLine() {
		resetPreviousLineNumber();
		return lines.get(currentLineNumber++);
	}

	/** reads complete line and ass as scalar
	 * typical use is for titles
	 * @param dictRef
	 * @param add
	 */
	public CMLScalar readLineAsScalar(String dictRef, boolean add) {
		CMLScalar line = new CMLScalar(readLine());
		addDictRef(line, getDictionaryPrefix(), dictRef);
		addElementToParentElement(add, line);
		return line;
	}

	public List<String> readLines(int linesToRead) {
		List<String> lineList = new ArrayList<String>();
		if (currentLineNumber + linesToRead >= lines.size()) {
			LOG.warn("not enough lines to read");
		} else {
			resetPreviousLineNumber();
			for (int i = 0; i < linesToRead; i++) {
				lineList.add(lines.get(currentLineNumber++));
			}
		}
		return lineList;
	}
	
	/** reads lines against a list of expected lines held as
	 * newline-separated text
	 * 
	 * @param textToRead
	 */
	public void skipCheckedLines(String textToRead) {
		String[] linesToRead = textToRead.split(CMLConstants.S_NEWLINE);
		skipCheckedLines(linesToRead);
	}

	/** reads lines against a list of expected lines
	 * 
	 * @param toRead
	 */
	public void skipCheckedLines(String[] linesToRead) {
		resetPreviousLineNumber();
		int mark = currentLineNumber;
		for (String lineToRead : linesToRead) {
			String foundLine = readLine();
			if (!lineToRead.equals(foundLine)) {
				currentLineNumber = mark;
				throw new RuntimeException("cannot find line :"+lineToRead+"\nfound :"+foundLine);
			}
		}
	}


	public CMLArray parseArray(String format, String lineToBeParsed, String dataType) {
		CMLArray array = new CMLArray();
		array.setDataType(dataType);
		List<Object> results = JumboReader.parseFortranLine(format, lineToBeParsed);
		for (Object result : results) {
			if (result == null) {
				break;
			}
			if (CMLConstants.XSD_STRING.equals(dataType)) {
				array.append(result.toString());
			} else if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
				array.append(((Double)result).doubleValue());
			} else if (CMLConstants.XSD_INTEGER.equals(dataType)) {
				array.append(((Integer)result).intValue());
			} else {
				throw new RuntimeException("Bad result "+result+" / "+dataType);
			}
		}
		return array;
	}

	/**
	 * reads enough lines to fill fieldsToRead
	 * if fieldsToRead is negative reads until end of lines or fails to parse
	 * @param format
	 * @param fieldsToRead
	 * @param dataType
	 * @return
	 */
	public CMLArray parseMultipleLinesToArray(String format, int fieldsToRead, String dataType) {
		CMLArray array = new CMLArray();
		CMLArray lineArray = null;
		array.setDataType(dataType);
		int fieldsPerLine = -1;
		resetPreviousLineNumber();
		while (currentLineNumber < lines.size()) {
			try {
				lineArray = parseArray(format, lines.get(currentLineNumber++), dataType);
			} catch (Exception e) {
				if (fieldsToRead < 0) {
					// allowed if we are trying to read until end
					// rest line to the first one that failed
					currentLineNumber--;
					break;
				} else {
					throw new RuntimeException("unexpected end of read", e);
				}
			}
			// use first line to define number of fields to parse
			if (fieldsPerLine == -1) {
				fieldsPerLine = lineArray.getSize();
			}
			array.append(lineArray);
			if (fieldsToRead >=0) {
				fieldsToRead -= fieldsPerLine;
				if (fieldsToRead <= 0) {
					break;
				}
			}
		}
		return array;
	}

	public	void appendChild(CMLElement element) {
		checkParent();
		parentElement.appendChild(element);
	}

	public void resetCurrentLine() {
		this.currentLineNumber = previousLineNumber;
	}

	private void addElementToParentElement(boolean add, CMLElement element) {
		if (add) {
			checkParent();
			parentElement.appendChild(element);
		}
	}

	private CMLElement convertToSingleScalarOrList(List<Object> results, String[] names, Pattern pattern) {
		if (results.size() != names.length) {
			throw new RuntimeException(
					"Unmatched lengths of names and results "+results.size()+" != "+names.length);
		}
		List<CMLScalar> scalarList = new ArrayList<CMLScalar>();
		List<String> nameList = makeNameList(names);
		List<Class> classList = makeClassList(names);
		for (int i = 0; i < nameList.size(); i++) {
			Object result = results.get(i);
			Object clazz = classList.get(i);
			String name = nameList.get(i);
			CMLScalar scalar = null;
			if (result == null) {
				throw new RuntimeException("Null result "+result+" / "+clazz+" / "+name);
			} else if ((result instanceof Integer || result instanceof String && pattern != null) &&
					clazz.equals(Integer.class)) {
				scalar = new CMLScalar(new Integer(result.toString()));
			} else if ((result instanceof Double || result instanceof String && pattern != null )&& 
			        clazz.equals(Double.class)) {
				scalar = new CMLScalar(new Double(result.toString()));
			} else if (result instanceof String && clazz.equals(String.class)) {
				scalar = new CMLScalar(result.toString());
			} else if (result instanceof String && clazz.equals(DateTime.class)) {
				DateTime dateTime = JodaDate.parseDate(result.toString().trim());
				scalar = new CMLScalar(dateTime);
			} else {
				throw new RuntimeException("Bad name/class "+result+" / "+clazz+" / "+name);
			}
			addDictRef(scalar, dictionaryPrefix, name);
			scalarList.add(scalar);
		}
		return createSingleScalarOrList(scalarList);
	}

	private CMLElement createSingleScalarOrList(List<CMLScalar> scalarList) {
		CMLElement element = null;
		if (scalarList.size() == 1) {
			element = scalarList.get(0);
		} else {
			element = new CMLList();
			for (CMLScalar scalar : scalarList) {
				element.appendChild(scalar);
			}
		}
		return element;
	}

	/**
	 * creates the columns in an array
	 * @param prefix
	 * @param format
	 * @param currentLineNumber
	 * @param lines
	 * @param linesToRead
	 * @param names
	 * @return
	 */
	private List<CMLArray> createTableColumns(String format, int linesToRead, String[] names) {
		checkLines();
		int lineCount0 = currentLineNumber;
		int linesLeft = lines.size() - currentLineNumber;
		if (linesToRead > linesLeft) {
			throw new RuntimeException("Not enough lines to read "+linesToRead+ " > "+linesLeft);
		}
		CMLTable table = new CMLTable();
		List<CMLArray> arrayList = new ArrayList<CMLArray>();
		Exception ee = null;
		while(linesToRead < 0 ||
			(currentLineNumber - lineCount0 < linesToRead)) {
			CMLElement element;
			try {
				element = this.parseScalars(format, names, DONT_ADD);
			} catch (Exception e) {
				if (linesToRead < 0) {
					ee = e;
					// end of table
					currentLineNumber--;
					break;
				}
				throw new RuntimeException("Cannot parse line: "+this.peekLine());
			}
			ensureArrayList(arrayList, element);
			List<CMLScalar> cells = getScalarList(element);
			for (int jcol = 0; jcol < arrayList.size(); jcol++ ) {
				addToArray(cells.get(jcol), arrayList.get(jcol));
			}
		}
		if (currentLineNumber == lineCount0) {
			throw new RuntimeException("failed to parse any lines", ee);
		}
		return arrayList;
	}

	private static CMLTable createTableFromColumns(List<CMLArray> columns) {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		for (CMLArray column : columns) {
			tableTool.addArray(column);
		}
		return table;
	}

	private void addDictRef(CMLElement element, String dictionaryPrefix, String name) {
		checkPrefix();
		checkId(name);
		String dictRef = DictRefAttribute.createValue(dictionaryPrefix, name);
		if (element instanceof HasDictRef) {
			((HasDictRef)element).setDictRef(dictRef);
		} else {
			LOG.warn("element "+element+" does not implement dictRef attribute");
			element.addAttribute(new Attribute(DictRefAttribute.NAME, dictRef));
		}
	}

	public void addDictRef(CMLElement element, String localDictRef) {
		addDictRef(element, getDictionaryPrefix(), localDictRef);
	}

	private void checkId(String entryId) {
		if (!dictionaryTool.isIdInDictionary(entryId)) {
			LOG.warn("entryId "+entryId+" not found in dictionary: "+dictionaryTool);
		}
	}

	private void checkPrefix() {
		if (dictionaryPrefix == null) {
			throw new RuntimeException("Must set dictionaryPrefix");
		}
	}

	private void checkLines() {
		if (lines == null) {
			throw new RuntimeException("Must set lines to parse");
		}
	}

	private List<Object> makeResults(Matcher matcher) {
		List<Object> objectList = new ArrayList<Object>();
		// matched groups count from 1
		for (int i = 1; i <=matcher.groupCount(); i++) {
			objectList.add(matcher.group(i));
		}
		return objectList;
	}
	
	private void resetPreviousLineNumber() {
		this.previousLineNumber = currentLineNumber;
	}

	private static List<String> makeNameList(String[] names) {
		List<String> nameList = new ArrayList<String>();
		for (String name : names) {
			if (name.startsWith("I.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("F.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("D.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("E.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("A.")) {
				nameList.add(name.substring(2));
			} else {
				throw new RuntimeException("Name must start with I.,D.,E.,F. or A."); 
			}
		}
		return nameList;
	}

	private static List<Class> makeClassList(String[] names) {
		List<Class> classList = new ArrayList<Class>();
		for (String name : names) {
			if (name.startsWith("I.")) {
				classList.add(Integer.class);
			} else if (name.startsWith("E.")) {
				classList.add(Double.class);
			} else if (name.startsWith("D.")) {
				classList.add(DateTime.class);
			} else if (name.startsWith("F.")) {
				classList.add(Double.class);
			} else if (name.startsWith("A.")) {
				classList.add(String.class);
			} else {
				throw new RuntimeException("Name must start with I., D., E., F. or A."); 
			}
		}
		return classList;
	}

//	/** reads line into scalars and adds those to element
//	 * 
//	 * @param element
//	 * @param format
//	 * @param lineToParse
//	 * @param names
//	 * @return
//	 */
//	public List<CMLScalar> addParsedScalarsAndIncrement(String format, String[] names) {
//		String lineToParse = readLine();
//		List<CMLScalar> scalarList = this.parseScalars(format, lineToParse, names);
//		checkParent();
//		for (CMLScalar scalar : scalarList) {
//			parentElement.appendChild(scalar);
//		}
//		return scalarList;
//	}
	
//	/** reads line into scalars and adds those to element
//	 * 
//	 * @param element
//	 * @param pattern
//	 * @param lineToParse
//	 * @param names
//	 * @return
//	 */
//	public void addParsedScalars(Pattern pattern, String lineToParse, String[] names) {
//		List<CMLScalar> scalarList = this.parseScalars(pattern, lineToParse, names);
//		checkParent();
//		for (CMLScalar scalar : scalarList) {
//			parentElement.appendChild(scalar);
//		}
//	}


	private void checkParent() {
		if (parentElement == null) {
			throw new RuntimeException("null parent element");
		}
	}

	private static List<CMLScalar> getScalarList(CMLElement element) {
		List<CMLScalar> scalars = new ArrayList<CMLScalar>();
		if (element instanceof CMLList) {
			if (element instanceof CMLList) {
				Elements childElements = element.getChildElements();
				for (int i = 0; i < childElements.size(); i++) {
					scalars.add((CMLScalar)childElements.get(i));
				}
			} else {
				scalars.add((CMLScalar)element);
			}
		}
		return scalars;
	}

	private static void addToArray(CMLScalar scalar, CMLArray array) {
		String dataType = scalar.getDataType();
		if (CMLConstants.XSD_STRING.equals(dataType)) {
			array.append(scalar.getStringContent());
		} else if (CMLConstants.XSD_INTEGER.equals(dataType)) {
			array.append(scalar.getInt());
		} else if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
			array.append(scalar.getDouble());
		} else if (CMLConstants.XSD_DATE.equals(dataType)) {
			array.append(scalar.getDate());
		}
	}

	private static void ensureArrayList(
			List<CMLArray> arrayList, CMLElement scalarElements) {
		if (arrayList.size() == 0) {
			List<CMLScalar> scalars = getScalarList(scalarElements);
			for (CMLScalar scalar : scalars) {
				CMLArray array = new CMLArray();
				array.setDictRef(scalar.getDictRef());
				array.setDataType(scalar.getDataType());
				arrayList.add(array);
			}
		}
	}

	/**
	 * reads lines until condition is not mateched
	 * currentLineNumber is set to line that caused break
	 * @param pattern
	 * @param matches if true read while pattern matches; if false read until
	 * @return list of lines read
	 */
	public List<String> readLinesWhile(Pattern pattern, boolean matches) {
		ArrayList<String> linesRead = new ArrayList<String>();
		while (currentLineNumber < lines.size()) {
			String line = readLine();
			if (pattern.matcher(line).matches() != matches) {
				break;
			}
			linesRead.add(line);
		}
		return linesRead;
	}

	public void makeUnknownScalar(boolean add) {
		String line = lines.get(currentLineNumber++);
		CMLScalar scalar = new CMLScalar(line.trim());
		addDictRef(scalar, UNKNOWN);
		addElementToParentElement(add, scalar);
	}

	public void makeNameValues(Pattern pattern, boolean add) {
		CMLElement scalar = null;
		while (hasMoreLines()) {
			try {
				scalar = parseNameValue(pattern, CMLConstants.XSD_STRING, add);
				if (scalar == null) {
					break;
				}
			} catch (Exception e) {
				break;
			}
		}
	}

	public void increment(int offset) {
		currentLineNumber += offset;
	}


}