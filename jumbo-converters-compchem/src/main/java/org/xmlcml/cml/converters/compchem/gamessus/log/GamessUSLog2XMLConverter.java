package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GamessUSLog2XMLConverter extends Text2XMLConverter {
	
	public GamessUSLog2XMLConverter() {
	}
	
	public String getMarkerResourceName() {
		return "org/xmlcml/cml/converters/compchem/gamessus/log/marker1.xml";
	}
	
	public LegacyProcessor createLegacyProcessor() {
		return new GamessUSLogProcessor();
	}
}
