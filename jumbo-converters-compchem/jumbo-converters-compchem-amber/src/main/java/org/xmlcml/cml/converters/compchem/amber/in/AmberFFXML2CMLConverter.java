package org.xmlcml.cml.converters.compchem.amber.in;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class AmberFFXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(AmberFFXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public AmberFFXML2CMLConverter() {
	}

	/**
	 * converts an Foo to CML. returns cml:cml/cml:molecule
	 * 
	 * @param xml
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new AmberFFXMLProcessor();
		return convert(xml);
	}

}
