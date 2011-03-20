package org.xmlcml.cml.converters.compchem.molcas.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class MolcasLog2XMLConverter extends CompchemTemplateConverter {
	
	public MolcasLog2XMLConverter(Element templateElement) {
		super(templateElement, "molcas", "log");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "molcas", "log", "topTemplate.xml");
	}
}