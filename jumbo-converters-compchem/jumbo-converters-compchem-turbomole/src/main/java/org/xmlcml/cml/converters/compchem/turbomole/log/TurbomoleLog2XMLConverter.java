package org.xmlcml.cml.converters.compchem.turbomole.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class TurbomoleLog2XMLConverter extends CompchemTemplateConverter {
	
	public TurbomoleLog2XMLConverter() {
		this(getDefaultTemplate("turbomole", "log", "topTemplate.xml", TurbomoleLog2XMLConverter.class));
	}
	
	public TurbomoleLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new TurbomoleLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}