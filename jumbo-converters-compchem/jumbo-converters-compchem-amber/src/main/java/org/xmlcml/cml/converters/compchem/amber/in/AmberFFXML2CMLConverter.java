package org.xmlcml.cml.converters.compchem.amber.in;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;

public class AmberFFXML2CMLConverter extends AbstractCompchem2CMLConverter{
	
	private static final Logger LOG = Logger.getLogger(AmberFFXML2CMLConverter.class);
    public static final String AMBER_FF_XML_TO_AMBER_FF_CML = "Amber-FF-XML to Amber-FF-CML";

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

	@Override
	public String getRegistryInputType() {
		return AmberCommon.AMBER_FF_XML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return AmberCommon.AMBER_FF_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return AMBER_FF_XML_TO_AMBER_FF_CML;
	}
}
