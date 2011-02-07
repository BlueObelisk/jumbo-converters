package org.xmlcml.cml.converters.compchem.gaussian.logold;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.converters.marker.AbstractParser;
import org.xmlcml.cml.element.CMLModule;

public class GaussianLog2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianLog2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	protected AbstractParser parser;
	
	public GaussianLog2CMLConverter() {
		
	}
	
   @Override
   protected AbstractCommon getCommon() {
	   return new GaussianCommon();
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
		GaussianLogParser parser = new GaussianLogParser();
		parser.convertToXML(lines);
		CMLModule module = parser.getCmlModule();
		return module;
	}

}
