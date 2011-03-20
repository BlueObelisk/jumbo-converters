package org.xmlcml.cml.converters.compchem.dalton.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class DaltonLog2XMLConverter extends CompchemTemplateConverter {
	
	public DaltonLog2XMLConverter(Element templateElement) {
		super(templateElement, "dalton", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "dalton", "log", "topTemplate.xml");
	}
}