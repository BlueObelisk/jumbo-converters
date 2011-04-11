package org.xmlcml.cml.converters.compchem.mopac.auxx;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class MopacAux2XMLConverter extends CompchemTemplateConverter {
	
	public MopacAux2XMLConverter(Element templateElement) {
		super(templateElement, "mopac", "aux");
	}

	public static void main(String[] args) throws IOException {
		runMain(args, "mopac", "aux", "topTemplate.xml");
	}
}