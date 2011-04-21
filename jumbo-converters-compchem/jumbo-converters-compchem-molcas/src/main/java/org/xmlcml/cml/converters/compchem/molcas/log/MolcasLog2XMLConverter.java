package org.xmlcml.cml.converters.compchem.molcas.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class MolcasLog2XMLConverter extends CompchemTemplateConverter {
	
	public MolcasLog2XMLConverter() {
		this(getDefaultTemplate("molcas", "log", "topTemplate.xml", MolcasLog2XMLConverter.class));
	}
	
	public MolcasLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new MolcasLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}