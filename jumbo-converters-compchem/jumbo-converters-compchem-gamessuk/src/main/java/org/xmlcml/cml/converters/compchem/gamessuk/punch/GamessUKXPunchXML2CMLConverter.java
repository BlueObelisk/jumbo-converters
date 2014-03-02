package org.xmlcml.cml.converters.compchem.gamessuk.punch;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKXCommon;

public class GamessUKXPunchXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKXPunchXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public GamessUKXPunchXML2CMLConverter() {

	}

   @Override
   protected AbstractCommon getCommon() {
	   return new GamessUKXCommon();
   }

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 *
	 * @param xml
	 */
	public Element convertToXML(Element xml) {
		RawXML2CMLProcessor converter = new GamessUKXPunchXMLProcessor();
		converter.process(xml);
		CMLElement cml = converter.getCMLElement();
		addNamespaces(cml);
		return cml;
	}

	@Override
	public String getRegistryInputType() {
		return GamessUKXCommon.PUNCH;
	}

	@Override
	public String getRegistryOutputType() {
		return GamessUKXCommon.PUNCH_XML;
	}

	@Override
	public String getRegistryMessage() {
		return "GamessUK Punch output to XML";
	}

}
