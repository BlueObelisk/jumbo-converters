package org.xmlcml.cml.converters.compchem.amber.mdout;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class AmberMdoutXMLProcessor extends RawXML2CMLProcessor {

	public AmberMdoutXMLProcessor() {
		
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
