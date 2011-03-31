package org.xmlcml.cml.converters.compchem.nwchem.log;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;

public class NWChemLogXMLProcessor extends RawXML2CMLProcessor {

	public NWChemLogXMLProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
