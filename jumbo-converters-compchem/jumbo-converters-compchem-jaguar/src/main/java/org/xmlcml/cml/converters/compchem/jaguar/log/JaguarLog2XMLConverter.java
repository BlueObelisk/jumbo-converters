package org.xmlcml.cml.converters.compchem.jaguar.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class JaguarLog2XMLConverter extends CompchemTemplateConverter {
	
	public JaguarLog2XMLConverter() {
		this(getDefaultTemplate("jaguar", "log", "topTemplate.xml", JaguarLog2XMLConverter.class));
	}
	
	public JaguarLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new JaguarLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}