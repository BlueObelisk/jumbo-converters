package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.Template;
import org.xmlcml.cml.converters.format.Field.FieldType;
import org.xmlcml.cml.converters.util.JumboReader;

public class ReadLinesLineReader extends LineReader {
	private final static Logger LOG = Logger.getLogger(ReadLinesLineReader.class);

	public static final String READ_LINES_LINE_READER = "readLinesLineReader";
	
	public ReadLinesLineReader(Element childElement, Template template) {
		super(READ_LINES_LINE_READER, childElement, template);
		createLinesFromContent();
		LOG.trace("Level "+outputLevel+"|"+(template == null ? "null" : template.getId())+"|"+this.getAttributeValue(Outputter.OUTPUT));
	}

	private void createLinesFromContent() {
		if (linesToRead == null && formatType == null) {
			String[] lines = content.split(CMLConstants.S_NEWLINE);
			linesToRead = Math.max(0, lines.length - 1); // neglect first and last newline
		}
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		String line = null;
		LOG.info("Template: "+(template == null ? "null" : this.template.getId()));
		debugLine("first line", OutputLevel.VERBOSE);
		LOG.trace("READ LINES AND PARSE "+this);
		if (linesToRead != null) {
			jumboReader.readLines(linesToRead);
		} else if (FormatType.FORTRAN.equals(formatType)) {
			LOG.trace("Start readLines: "+jumboReader.peekLine());
			List<Field> fieldList = this.getFieldList();
			for (Field field : fieldList) {
				LOG.trace("FFF "+field);
				// do we have to advance a line?
				if (FieldType.N.equals(field.fieldType)) {
					LOG.trace("peek: "+jumboReader.peekLine());
					line = jumboReader.readLine();
					LOG.trace("LLL "+line);
					LOG.trace("peek: "+jumboReader.peekLine());
				}
			}
			// advance a single line at end of FORMAT read
			line = jumboReader.readLine();
			LOG.trace("line: "+jumboReader.getCurrentLineNumber()+" peek: "+jumboReader.peekLine());
		}
		debug("Current line", OutputLevel.VERBOSE);
		return null;
	}


}
