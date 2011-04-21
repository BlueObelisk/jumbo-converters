package org.xmlcml.cml.converters.compchem.dalton.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class DaltonLog2XMLConverter extends CompchemTemplateConverter {
	
	public DaltonLog2XMLConverter() {
		this(getDefaultTemplate("dalton", "log", "topTemplate.xml", DaltonLog2XMLConverter.class));
	}
	
	public DaltonLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new DaltonLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}