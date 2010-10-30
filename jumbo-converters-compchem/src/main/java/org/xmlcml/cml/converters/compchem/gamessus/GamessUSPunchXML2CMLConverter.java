package org.xmlcml.cml.converters.compchem.gamessus;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class GamessUSPunchXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUSPunchXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public GamessUSPunchXML2CMLConverter() {
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new GamessUSPunchXMLProcessor();
		return convert(xml);
	}

}
