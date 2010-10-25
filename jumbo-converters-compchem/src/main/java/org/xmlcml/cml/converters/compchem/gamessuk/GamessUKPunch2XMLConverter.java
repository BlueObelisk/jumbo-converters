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

public class GamessUKPunch2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKPunch2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String GAMESSUK_PREFIX = "gamessuk";
	public static final String GAMESSUK_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessuk";
	
	
	public Type getInputType() {
		return Type.GAMESSUK_PUNCH;
	}

	public Type getOutputType() {
		return Type.GAMESSUK_PUNCH_XML;
	}

	/**
	 * converts a Gamess punch object to raw CML. 
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml topCml = new CMLCml();
		addCommonNamespaces(topCml);
		GamessUKPunchProcessor processor = new GamessUKPunchProcessor();
		processor.read(lines);
		List<CMLElement> cmlElementList = processor.getBlockList();
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

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(GAMESSUK_PREFIX, GAMESSUK_URI);
	}

}
