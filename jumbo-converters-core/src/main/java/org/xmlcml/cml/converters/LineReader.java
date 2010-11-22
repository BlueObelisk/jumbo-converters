package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.LineReader.LineType;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLScalar;

public class LineReader extends Element {
	private final static Logger LOG = Logger.getLogger(LineReader.class);

	private static final String LINES_TO_READ = "linesToRead";
	public static final String T_FLAG = CMLConstants.S_TILDE;

	 public enum LineType { 
		MATRIX("matrix"),
		NAMEVALUES("nameValues"),
		READLINES("readLines"),
		SCALAR("scalars"), 
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
	
	private int linesToRead;
	private List<Field> fieldList;
	private LineType lineType;
	private String line;
	
	public LineReader(String line) {
		super(LINE_READER);
		linesToRead = 0;
		fieldList = new ArrayList<Field>();
		this.line = line;
	}

	public int getLinesToRead() {
		return linesToRead;
	}

	public void setLinesToRead(int toRead) {
		this.linesToRead = toRead;
		this.addAttribute(new Attribute(LINES_TO_READ, ""+toRead));
		this.lineType = LineType.READLINES;
	}

	public void addField(Field field) {
		this.appendChild(field);
		fieldList.add(field);
	}

	public int processLine(String[] dictRefNames, int count) {
		String[] fieldStrings = line.split(T_FLAG);
		int fieldCount = 0;
		for (String fieldString : fieldStrings) {
			if (fieldString.length() > 0) {
				fieldString = Field.C_FLAG+fieldString;
				Field field = new Field(fieldString);
				if (field.hasScalarType()) {
					field.setLocalDictRef(dictRefNames[count++]);
					fieldCount++;
				}
				this.addField(field);
				Field overflow = field.getOverflow();
				if (overflow != null) {
					this.addField(overflow);
				}
			}
		}
		return fieldCount;
	}

	public void process(JumboReader jumboReader) {
		if (false) {
		} else if (LineType.MATRIX.equals(lineType)) {
			LOG.warn(""+LineType.MATRIX+" not yet implemented");
		} else if (LineType.NAMEVALUES.equals(lineType)) {
			LOG.warn(""+LineType.NAMEVALUES+" not yet implemented");
		} else if (LineType.READLINES.equals(lineType)) {
			jumboReader.readLines(linesToRead);
		} else if (LineType.SCALAR.equals(lineType)) {
			String line = jumboReader.readLine();
			int startCount = 0;
			for (Field field : fieldList) {
				int fieldLength = field.getLast()+1;
				int endCount = startCount+fieldLength;
				String ff = null;
				// some lines don't use all fields
				if (endCount > line.length()) {
					endCount = line.length();
				}
				if (endCount > startCount) {
					ff = line.substring(startCount, endCount);
					CMLScalar scalar = JumboReader.createScalar(field.getParseClass(), ff);
					if (scalar != null) {
						jumboReader.addElementWithDictRef(scalar, field.getLocalDictRef());
						jumboReader.getParentElement().debug("JJJJJJJJJJ");
					}
				}
				startCount = endCount;
			}
		} else {
			this.debug();
			throw new RuntimeException("Cannot process lineReaderType "+lineType);
		}
		System.out.println("XXXXXXXX");
	}

	public void setType(LineType type) {
		this.lineType = type;
	}

	public void debug() {
		System.out.println(">>LINE>>"+line);
	}
}
