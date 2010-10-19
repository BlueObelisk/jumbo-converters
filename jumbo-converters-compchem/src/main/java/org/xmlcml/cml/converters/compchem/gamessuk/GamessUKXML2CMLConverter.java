package org.xmlcml.cml.converters.compchem.gamessuk;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class GamessUKXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKXML2CMLConverter.class);
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
		RawXML2CMLProcessor converter = new GamessUKPunchXMLProcessor();
		converter.process(xml);
		CMLElement cml = converter.getCMLElement();
		addNamespaces(cml);
		return cml;
	}

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX, GamessUKPunch2XMLConverter.GAMESSUK_URI);
	}

}
