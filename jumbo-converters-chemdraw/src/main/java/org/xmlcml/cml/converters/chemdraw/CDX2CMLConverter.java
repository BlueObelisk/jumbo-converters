package org.xmlcml.cml.converters.chemdraw;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class CDX2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CDX2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CDX;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts a CDK object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param bytes
	 */
	public Element convertToXML(byte[] bytes) {
		CDX2CDXMLConverter cdx2cdxmlConverter = new CDX2CDXMLConverter();
		Element cdxml = cdx2cdxmlConverter.convertToXML(bytes);
		CDXML2CMLConverter cdxml2CmlConverter = new CDXML2CMLConverter();
		return cdxml2CmlConverter.convertToXML(cdxml);
	}

}
