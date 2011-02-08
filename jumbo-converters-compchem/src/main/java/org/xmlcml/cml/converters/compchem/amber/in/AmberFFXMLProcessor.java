package org.xmlcml.cml.converters.compchem.amber.in;

import org.xmlcml.cml.converters.AbstractCommon;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;

public class AmberFFXMLProcessor extends RawXML2CMLProcessor {

	public AmberFFXMLProcessor() {
		
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
