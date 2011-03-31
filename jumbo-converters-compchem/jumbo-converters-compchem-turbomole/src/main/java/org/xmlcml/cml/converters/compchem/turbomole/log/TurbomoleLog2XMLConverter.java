package org.xmlcml.cml.converters.compchem.turbomole.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class TurbomoleLog2XMLConverter extends CompchemTemplateConverter {
	
	public TurbomoleLog2XMLConverter(Element templateElement) {
		super(templateElement, "turbomole", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "turbomole", "log", "topTemplate.xml");
	}
}