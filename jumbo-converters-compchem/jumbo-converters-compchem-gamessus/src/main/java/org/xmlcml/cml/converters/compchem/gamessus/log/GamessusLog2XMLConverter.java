package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GamessusLog2XMLConverter extends CompchemTemplateConverter {
	
	public GamessusLog2XMLConverter(Element templateElement) {
		super(templateElement, "gamessus", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "gamessus", "log", "topTemplate.xml");
	}
}