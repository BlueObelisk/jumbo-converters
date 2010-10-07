package org.xmlcml.cml.converters.compchem.gamessuk;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.euclid.Util;

public class GamessUKXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String GAMESSUK_PREFIX = "gamessuk";
	public static final String GAMESSUK_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessuk";
	
	
	public Type getInputType() {
		return Type.GAMESSUK_PUNCH;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml topCml = new CMLCml();
		GamessUKPunchProcessor converter = new GamessUKPunchProcessor();
		converter.read(lines);
		List<CMLElement> cmlElementList = converter.getBlockList();
		for (CMLElement cmlElement : cmlElementList) {
			topCml.appendChild(cmlElement);
		}
		try {
			ensureId(topCml);
		} catch (RuntimeException e) {
			// no id
		}
//		topCml = processParamsTopMetadataNamespaces(topCml);
		return topCml;
	}

	public void addNamespaces(CMLCml topCml) {
		addCommonNamespaces(topCml);
		topCml.addNamespaceDeclaration(GAMESSUK_PREFIX, GAMESSUK_URI);
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
