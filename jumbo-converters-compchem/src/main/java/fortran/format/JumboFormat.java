package fortran.format;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.tools.TableTool;
import org.xmlcml.euclid.JodaDate;

public class JumboFormat {
	private static Logger LOG = Logger.getLogger(JumboFormat.class);

	public List<Object> parseFortranLine(String format, String line) {
		String newFormat = JumboFormat.expandStrings(format);
		LOG.trace("format:"+newFormat);
		List<Object> results = new ArrayList<Object>();
		try {
			FortranFormat fortranFormat = new FortranFormat(newFormat);
			results = fortranFormat.parse(line);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse fortran :"+line+":", e);
		}
		return results;
	}

	/**
	 * replaces all character strings ('abc') by equivalent spaces (3X)
	 * @param format
	 * @return
	 */
	public static String expandStrings(String format) {
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

	public List<CMLScalar> parseToScalars(
			String prefix, String format, String data, String[] names) {
		List<CMLScalar> scalarList = new ArrayList<CMLScalar>();
		List<String> nameList = makeNameList(names);
		List<Class> classList = makeClassList(names);
		List<Object> results = this.parseFortranLine(format, data);
		if (results.size() != nameList.size()) {
			throw new RuntimeException("Unmatched lengths of names and results "+results.size()+" != "+nameList.size());
		}
		for (int i = 0; i < nameList.size(); i++) {
			Object result = results.get(i);
			Object clazz = classList.get(i);
			String name = nameList.get(i);
			CMLScalar scalar = null;
			if (result instanceof Integer && clazz.equals(Integer.class)) {
				scalar = new CMLScalar((Integer)result);
			} else if (result instanceof Double && clazz.equals(Double.class)) {
				scalar = new CMLScalar((Double)result);
			} else if (result instanceof String && clazz.equals(String.class)) {
				scalar = new CMLScalar((String)result);
			} else if (result instanceof String && clazz.equals(DateTime.class)) {
				DateTime dateTime = JodaDate.parseDate(result.toString().trim());
				scalar = new CMLScalar(dateTime);
			} else {
				throw new RuntimeException("Bad name/class "+result+" / "+clazz+" / "+name);
			}
			scalar.setDictRef(DictRefAttribute.createValue(prefix, name));
			scalarList.add(scalar);
		}
		return scalarList;
	}

	public List<CMLScalar> parseToScalars(
			String prefix, Pattern pattern, String data, String[] names) {
		List<CMLScalar> scalarList = new ArrayList<CMLScalar>();
		List<String> nameList = makeNameList(names);
		List<Class> classList = makeClassList(names);
		Matcher matcher = pattern.matcher(data);
		if (!matcher.find()) {
			throw new RuntimeException("Cannot parse :"+data+": with :"+pattern+":");
		} else if (matcher.groupCount() != nameList.size()) {
			throw new RuntimeException("Unmatched lengths of names and results "+matcher.groupCount()+" != "+nameList.size());
		}
		for (int i = 0; i < nameList.size(); i++) {
			String result = matcher.group(i+1); // groups count from 1
			Object clazz = classList.get(i);
			String name = nameList.get(i);
			CMLScalar scalar = null;
			if (clazz.equals(Integer.class)) {
				scalar = new CMLScalar(new Integer(result));
			} else if (clazz.equals(Double.class)) {
				scalar = new CMLScalar(new Double(result));
			} else if (clazz.equals(String.class)) {
				scalar = new CMLScalar(result);
			} else if (clazz.equals(DateTime.class)) {
				DateTime dateTime = JodaDate.parseDate(result.trim());
				scalar = new CMLScalar(dateTime);
			} else {
				throw new RuntimeException("Bad name/class "+result+" / "+clazz+" / "+name);
			}
			scalar.setDictRef(DictRefAttribute.createValue(prefix, name));
			scalarList.add(scalar);
		}
		return scalarList;
	}

	private int linesRead;

	public CMLArray parseToSingleLineArray(
			String format, String data, String dataType) {
		CMLArray array = new CMLArray();
		array.setDataType(dataType);
		List<Object> results = this.parseFortranLine(format, data);
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

	public CMLArray parseMultipleLinesToArray(
			String format, List<String> lines, int lineCount, int fieldsToRead, String dataType) {
		CMLArray array = new CMLArray();
		CMLArray lineArray = null;
		array.setDataType(dataType);
		int fieldsPerLine = -1;
		linesRead = 0;
		while (/*fieldsToRead > 0 && */lineCount < lines.size()) {
			try {
				lineArray = parseToSingleLineArray(format, lines.get(lineCount++), dataType);
			} catch (Exception e) {
				if (fieldsToRead < 0) {
					// allowed if we are trying to read until end
					break;
				} else {
					throw new RuntimeException("unexpected end of read", e);
				}
			}
			if (fieldsPerLine == -1) {
				fieldsPerLine = lineArray.getSize();
			}
			array.append(lineArray);
			linesRead++;
			if (fieldsToRead >=0) {
				fieldsToRead -= fieldsPerLine;
				if (fieldsToRead <= 0) {
					break;
				}
			}
		}
		return array;
	}

	// REFACTOR THIS
	//"(5X,5E15.8)" // CMLConstants.XSD_DOUBLE
//	public CMLMatrix readMatrix(int rows, int cols, String format, List<String> lines, String dataType) {
//		double[][] matrixx = new double[rows][];
//		linesRead = 0;
//	    for (int i = 0; i < rows; i++) {
//	    	JumboFormat jumboFormat = new JumboFormat();
//	    	CMLArray array = jumboFormat.parseMultipleLinesToArray(
//	    			format, lines, cols, dataType);
//	    	linesRead += jumboFormat.getLinesRead();
//	    	matrixx[i] = array.getDoubles();
//	    }
//		return new CMLMatrix(matrixx);
//	}
	
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

	public List<CMLScalar> addParsedScalars(CMLElement element,
			String prefix, String format, String data, String[] names) {
		List<CMLScalar> scalarList = this.parseToScalars(prefix, format, data, names);
		for (CMLScalar scalar : scalarList) {
			element.appendChild(scalar);
		}
		return scalarList;
	}
	
	public void addParsedScalars(CMLElement element, String prefix,
			Pattern pattern, String data, String[] names) {
		List<CMLScalar> scalarList = this.parseToScalars(prefix, pattern, data, names);
		for (CMLScalar scalar : scalarList) {
			element.appendChild(scalar);
		}
	}


	/**
	 * creates the columns in an array
	 * @param prefix
	 * @param format
	 * @param lineCount
	 * @param lines
	 * @param linesToRead
	 * @param names
	 * @return
	 */
	public List<CMLArray> createTableColumns(String prefix, String format,
			int lineCount, List<String> lines, int linesToRead, String[] names) {
		int lineCount0 = lineCount;
		int linesLeft = lines.size() - lineCount;
		if (linesToRead > linesLeft) {
			throw new RuntimeException("Not enough lines to read "+linesToRead+ " > "+linesLeft);
		}
		CMLTable table = new CMLTable();
		List<CMLArray> arrayList = new ArrayList<CMLArray>();
		linesRead = 0;
		while(linesToRead < 0 ||
			(lineCount - lineCount0 < linesToRead)) {
			List<CMLScalar> scalars = this.parseToScalars(
					prefix, format, lines.get(lineCount++), names);
			ensureArrayList(arrayList, scalars);
			for (int jcol = 0; jcol < arrayList.size(); jcol++ ) {
				addToArray(scalars.get(jcol), arrayList.get(jcol));
			}
			linesRead++;
		}
		return arrayList;
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

	private static void ensureArrayList(List<CMLArray> arrayList,
			List<CMLScalar> scalars) {
		if (arrayList.size() == 0) {
			for (CMLScalar scalar : scalars) {
				CMLArray array = new CMLArray();
				array.setDictRef(scalar.getDictRef());
				array.setDataType(scalar.getDataType());
				arrayList.add(array);
			}
		}
	}

	public static CMLTable createTableFromColumns(List<CMLArray> columns) {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		for (CMLArray column : columns) {
			tableTool.addArray(column);
		}
		return table;
	}

	public int getLinesRead() {
		return linesRead;
	}

	public CMLArrayList createTableColumnsAsArrayList(String prefix,
			String format, int lineCount, List<String> lines, int linesToRead, String[] names) {
		CMLArrayList arrayList = new CMLArrayList();
		List<CMLArray> arrays = this.createTableColumns(prefix, format, lineCount, lines, linesToRead, names);
		for (CMLArray array : arrays) {
			arrayList.addArray(array);
		}
		return arrayList;
	}


}