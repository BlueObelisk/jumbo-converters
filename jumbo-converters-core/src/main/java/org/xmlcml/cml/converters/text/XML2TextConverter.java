package org.xmlcml.cml.converters.text;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

public abstract class XML2TextConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(XML2TextConverter.class);

	public Type getInputType() {
		return Type.XML;
	}
	
	public Type getOutputType() {
		return Type.TXT;
	}
	
	public XML2TextConverter() {
	}
	
	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = null;
		return stringList;
	}

	
}
