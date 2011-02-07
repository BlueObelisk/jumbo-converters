package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GamessUSLog2XMLConverter extends Text2XMLConverter {
	
	public GamessUSLog2XMLConverter() {
	}
	
	public LegacyProcessor createLegacyProcessor() {
		return new GamessUSLogProcessor();
	}
}
