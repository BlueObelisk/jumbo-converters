package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class NWChemLog2XMLConverterOld extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(NWChemLog2XMLConverterOld.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public NWChemLog2XMLConverterOld() {
	}
   @Override
   protected AbstractCommon getCommon() {
	   return new NWChemCommon();
   }

	public Type getInputType() {
		return Type.TXT;
	}

	public Type getOutputType() {
		return Type.XML;
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
