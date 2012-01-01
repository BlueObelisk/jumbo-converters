package org.xmlcml.cml.converters.cmllite;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;

public class CML2CMLLiteConverter extends AbstractConverter implements
		Converter {
	private static final Logger LOG = Logger.getLogger(CML2CMLLiteConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts a CML object to CMLLite. returns cml:cml
	 * 
	 */
	public Element convertToXML(Element element) {
		CMLElement cmlIn = CMLBuilder.ensureCML(element);
		CMLLiteHelper converter = new CMLLiteHelper(cmlIn);
		CMLCml cml = converter.getCML();
		return cml;
	}

	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}
