package fortran.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTableContent;
import org.xmlcml.cml.element.CMLTableHeader;
import org.xmlcml.cml.element.CMLTableHeaderCell;
import org.xmlcml.cml.tools.TableTool;

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
			throw new RuntimeException("Cannot parse fortran", e);
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
		array.setDataType(dataType);
		int fieldsPerLine = -1;
		linesRead = 0;
		while (fieldsToRead > 0 && lineCount < lines.size()) {
			CMLArray lineArray = parseToSingleLineArray(format, lines.get(lineCount++), dataType);
			if (fieldsPerLine == -1) {
				fieldsPerLine = lineArray.getSize();
			}
			fieldsToRead -= fieldsPerLine;
			array.append(lineArray);
			linesRead++;
		}
		return array;
	}

	private static List<String> makeNameList(String[] names) {
		List<String> nameList = new ArrayList<String>();
		for (String name : names) {
			if (name.startsWith("I.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("F.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("E.")) {
				nameList.add(name.substring(2));
			} else if (name.startsWith("A.")) {
				nameList.add(name.substring(2));
			} else {
				throw new RuntimeException("Name must start with I. or E. or F. or A."); 
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
			} else if (name.startsWith("F.")) {
				classList.add(Double.class);
			} else if (name.startsWith("A.")) {
				classList.add(String.class);
			} else {
				throw new RuntimeException("Name must start with I. or E. or F. or A."); 
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


}