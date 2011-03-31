package org.xmlcml.cml.converters.compchem.gaussian.log;


import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GaussianLog2XMLConverter extends CompchemTemplateConverter {
	
	public GaussianLog2XMLConverter(Element templateElement) {
		super(templateElement, "gaussian", "log");
	}
	
	public static void main(String[] args) throws IOException {
		runMain(args, "gaussian", "log", "topTemplate.xml");
	}
}
