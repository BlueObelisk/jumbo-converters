package org.xmlcml.cml.converters.compchem.gamessus;

import org.xmlcml.cml.converters.RawXML2CMLProcessor;

public class GamessUSPunchXMLProcessor extends RawXML2CMLProcessor {

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
