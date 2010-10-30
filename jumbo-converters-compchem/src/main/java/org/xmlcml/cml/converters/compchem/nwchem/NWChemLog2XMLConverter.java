package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;

public class NWChemLog2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(NWChemLog2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public NWChemLog2XMLConverter() {
	}
   @Override
   protected AbstractCommon getCommon() {
	   return new NWChemCommon();
   }

	public Type getInputType() {
		return Type.FOO;
	}

	public Type getOutputType() {
		return Type.FOO_XML;
	}

	/**
	 * converts an Foo object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		legacyProcessor = new NWChemProcessor();
		return readAndProcess(lines);
	}

}
