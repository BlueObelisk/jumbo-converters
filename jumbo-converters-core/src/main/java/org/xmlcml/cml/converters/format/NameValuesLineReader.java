package org.xmlcml.cml.converters.format;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;

public class NameValuesLineReader extends LineReader {
	private static final Logger LOG = Logger.getLogger(NameValuesLineReader.class);

	public static final String NAME_VALUES_LINE_READER = "nameValuesLineReader";

	public NameValuesLineReader(Element childElement) {
		super(NAME_VALUES_LINE_READER, childElement);
	}
	
	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		throw new RuntimeException("NameVaklueReader NYI");
	}

}
