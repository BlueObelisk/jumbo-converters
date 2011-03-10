package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;

public class BracketedLineReader extends LineReader {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(BracketedLineReader.class);

	public static final String BRACKETED_LINE_READER = "bracketedLineReader";

	public BracketedLineReader(Element childElement) {
		super(BRACKETED_LINE_READER, childElement, null);
	}

	public BracketedLineReader(List<Field> fieldList) {
		super(fieldList);
	}

//	@Override
//	public CMLElement readLinesAndParse(JumboReader jumboReader) {
//		throw new RuntimeException("NYI");
//	}

}
