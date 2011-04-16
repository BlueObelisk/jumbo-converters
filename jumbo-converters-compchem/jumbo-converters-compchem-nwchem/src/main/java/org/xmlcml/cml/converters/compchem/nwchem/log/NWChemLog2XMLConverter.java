package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class NWChemLog2XMLConverter extends CompchemTemplateConverter {
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement, "nwchem", "log");
	}

	public static void main(String[] args) throws IOException {
		String templateS = (args.length == 3) ? args[2] : "topTemplate.xml";
		runMain(args, "nwchem", "log", templateS);
	}
}