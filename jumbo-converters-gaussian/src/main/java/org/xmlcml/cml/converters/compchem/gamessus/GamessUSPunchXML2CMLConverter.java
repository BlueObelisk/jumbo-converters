package org.xmlcml.cml.converters.compchem.gamessus;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class GamessUSPunchXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUSPunchXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
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
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new GamessUSPunchXMLProcessor();
		return convert(xml);
	}

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(GamessUSPunch2XMLConverter.GAMESSUS_PREFIX, GamessUSPunch2XMLConverter.GAMESSUS_URI);
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
