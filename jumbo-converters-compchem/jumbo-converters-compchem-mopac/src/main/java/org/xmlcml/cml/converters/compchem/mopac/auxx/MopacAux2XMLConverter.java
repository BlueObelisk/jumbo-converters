package org.xmlcml.cml.converters.compchem.mopac.auxx;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class MopacAux2XMLConverter extends CompchemTemplateConverter {
	
	public MopacAux2XMLConverter() {
		this(getDefaultTemplate("mopac", "aux", "topTemplate.xml", MopacAux2XMLConverter.class));
	}
	
	public MopacAux2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new MopacAux2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}