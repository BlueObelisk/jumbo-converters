package org.xmlcml.cml.converters.documents.epo;


import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class EPO2XMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(EPO2XMLConverter.class);
	
	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.XML;
	}

	private boolean analyze = false;
	
	/**
	 * converts an EPO XML to XML. 
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element xml) {
		EPOProcessor processor = new EPOProcessor();
		processor.setInput(xml);
		if (analyze) {
			processor.analyze();
		} else {
			processor.process();
		}
		Element element = processor.getOutput();
		return element;
	}


	@Override
	public int getConverterVersion() {
		return 0;
	}

}
