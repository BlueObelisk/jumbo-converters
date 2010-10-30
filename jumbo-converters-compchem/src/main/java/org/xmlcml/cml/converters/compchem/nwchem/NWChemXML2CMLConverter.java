package org.xmlcml.cml.converters.compchem.nwchem;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSPunchXMLProcessor;
import org.xmlcml.cml.element.CMLCml;

public class NWChemXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(NWChemXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public NWChemXML2CMLConverter() {
	}

   @Override
   protected AbstractCommon getCommon() {
	   return new NWChemCommon();
   }

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an Foo to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new NWChemXMLProcessor();
		return convert(xml);
	}

}
