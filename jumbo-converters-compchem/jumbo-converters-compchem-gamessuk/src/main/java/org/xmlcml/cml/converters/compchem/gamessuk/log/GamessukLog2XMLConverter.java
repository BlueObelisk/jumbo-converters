package org.xmlcml.cml.converters.compchem.gamessuk.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GamessukLog2XMLConverter extends CompchemTemplateConverter {
	
	public GamessukLog2XMLConverter(Element templateElement) {
		super(templateElement, "gamessuk", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "gamessuk", "log", "topTemplate.xml");
	}
}