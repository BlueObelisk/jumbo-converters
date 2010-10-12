package org.xmlcml.cml.converters.compchem.gamessus;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class GamessUSPunchXMLProcessor extends RawXML2CMLProcessor {

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
