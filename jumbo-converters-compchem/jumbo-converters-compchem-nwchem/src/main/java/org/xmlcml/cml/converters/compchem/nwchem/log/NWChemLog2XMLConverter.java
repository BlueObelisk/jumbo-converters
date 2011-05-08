package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class NWChemLog2XMLConverter extends CompchemTemplateConverter {
	
	public NWChemLog2XMLConverter() {
		this(getDefaultTemplate("nwchem", "log", "topTemplate.xml", NWChemLog2XMLConverter.class));
	}
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new NWChemLog2XMLConverter();
		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
		File out = new File("test-out.xml");
		converter.convert(in, out);
	}
}
