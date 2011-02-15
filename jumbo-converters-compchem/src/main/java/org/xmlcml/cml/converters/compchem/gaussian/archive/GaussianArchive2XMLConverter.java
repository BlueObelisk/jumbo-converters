package org.xmlcml.cml.converters.compchem.gaussian.archive;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class GaussianArchive2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianArchive2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public GaussianArchive2XMLConverter() {
	}

	public Type getInputType() {
		return Type.GAU_ARC;
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
		legacyProcessor = new GaussianArchiveProcessor();
		CMLElement cmlElement = readAndProcess(lines);
		return cmlElement;
	}

}
