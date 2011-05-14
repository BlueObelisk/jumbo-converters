package org.xmlcml.cml.converters;

import nu.xom.Element;

/**
 * @author pmr
 */
public abstract class XML2XMLConverter extends AbstractConverter {

	
	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.XML;
	}
	
	public XML2XMLConverter() {
		
	}

	public abstract Element convertToXML(Element element);
}
