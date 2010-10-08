package org.xmlcml.cml.converters.compchem.gamessuk;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.element.CMLCml;

public class GamessUKXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String GAMESSUK_PREFIX = "gamessuk";
	public static final String GAMESSUK_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessuk";
	
	
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
	public Element convertToXML(Element punchXML) {
		GamessUKPunchXMLProcessor converter = new GamessUKPunchXMLProcessor();
		converter.process(punchXML);
		CMLElement cml = converter.getCML();
		addNamespaces(cml);
		return cml;
	}

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(GAMESSUK_PREFIX, GAMESSUK_URI);
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
