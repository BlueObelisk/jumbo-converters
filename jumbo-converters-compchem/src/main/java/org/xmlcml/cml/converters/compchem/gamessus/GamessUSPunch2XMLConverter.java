package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.foo.FooProcessor;

public class GamessUSPunch2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUSPunch2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public GamessUSPunch2XMLConverter() {
		this.abstractCommon = new GamessUSCommon();
	}

	public Type getInputType() {
		return Type.GAMESSUS_PUNCH;
	}

	public Type getOutputType() {
		return Type.GAMESSUS_PUNCH_XML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		legacyProcessor = new GamessUSPunchProcessor();
		CMLElement cmlElement = readAndProcess(lines);
		return cmlElement;
	}

}
