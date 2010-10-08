package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.marker.AbstractParser;
import org.xmlcml.cml.element.CMLModule;

public class GaussianLog2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianLog2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String GAUSSIAN_PREFIX = "gaussian";
	public static final String GAUSSIAN_URI = "http://wwmm.ch.cam.ac.uk/dict/gaussian/log";
	
	protected AbstractParser parser;
	
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
		GaussianLogParser parser = new GaussianLogParser();
		parser.convertToXML(lines);
		CMLModule module = parser.getCmlModule();
		return module;
	}

	protected void addNamespaces(CMLElement cml) {
		if (cml != null) {
			addCommonNamespaces(cml);
			cml.addNamespaceDeclaration(GAUSSIAN_PREFIX, GAUSSIAN_URI);
		}
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
