package org.xmlcml.cml.converters.compchem.qespresso.log;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.qespresso.QespressoCommon;

public class QuantumEspressoLogXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(QuantumEspressoLogXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public QuantumEspressoLogXML2CMLConverter() {
	}

   @Override
   protected AbstractCommon getCommon() {
	   return new QespressoCommon();
   }

	/**
	 * converts an Foo to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new QuantumEspressoLogXMLProcessor();
		return convert(xml);
	}

}
