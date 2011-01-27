package org.xmlcml.cml.converters.format;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArrayList;

public class TableLineReader extends LineReader {
	private final static Logger LOG = Logger.getLogger(TableLineReader.class);

	public static final String TABLE_LINE_READER = "tableLineReader";
	
	public TableLineReader(Element childElement) {
		super(TABLE_LINE_READER, childElement);
	}
	
	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		CMLArrayList arrayList = jumboReader.readTableColumnsAsArrayList(this);
		jumboReader.addElementWithDictRef(arrayList, localDictRef);
		return arrayList;
	}



}
