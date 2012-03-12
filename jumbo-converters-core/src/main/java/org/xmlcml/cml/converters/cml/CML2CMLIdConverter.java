package org.xmlcml.cml.converters.cml;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;


public class CML2CMLIdConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CML2CMLIdConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * 
	 */
	public Element convertToXML(Element cml) {
		Nodes nodes = cml.query("//*");
		String XMLNS = "http://www.w3.org/XML/1998/namespace";
		for (int i = 0; i < nodes.size(); i++) {
			Attribute xmlid = new Attribute("xml:id", XMLNS, "id"+(i+1));
			((Element)nodes.get(i)).addAttribute(xmlid);
		}
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
		return "add id";
	}
}
