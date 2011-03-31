package org.xmlcml.cml.converters.compchem.qespresso.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class QuantumEspressoLog2XMLConverter extends CompchemTemplateConverter {
	
	public QuantumEspressoLog2XMLConverter(Element templateElement) {
		super(templateElement, "qespresso", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "qespresso", "log", "topTemplate.xml");
	}
}