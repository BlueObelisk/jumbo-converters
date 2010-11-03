package org.xmlcml.cml.converters.compchem.nwchem;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class NWChemXMLProcessor extends RawXML2CMLProcessor {

	public NWChemXMLProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
