package org.xmlcml.cml.converters.compchem.foo;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class FooXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(FooXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public FooXML2CMLConverter() {
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an Foo to CML. returns cml:cml/cml:molecule
	 * 
	 * @param xml
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new FooXMLProcessor();
		return convert(xml);
	}

}
