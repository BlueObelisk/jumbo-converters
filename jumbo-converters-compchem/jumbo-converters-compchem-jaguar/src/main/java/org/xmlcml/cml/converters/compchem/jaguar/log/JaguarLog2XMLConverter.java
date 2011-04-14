package org.xmlcml.cml.converters.compchem.jaguar.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class JaguarLog2XMLConverter extends CompchemTemplateConverter {
	
	public JaguarLog2XMLConverter(Element templateElement) {
		super(templateElement, "jaguar", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "jaguar", "log", "topTemplate.xml");
	}
}