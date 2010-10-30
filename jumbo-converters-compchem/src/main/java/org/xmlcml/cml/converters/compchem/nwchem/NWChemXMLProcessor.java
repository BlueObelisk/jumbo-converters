package org.xmlcml.cml.converters.compchem.nwchem;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class NWChemXMLProcessor extends RawXML2CMLProcessor {

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
