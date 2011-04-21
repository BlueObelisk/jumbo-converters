package org.xmlcml.cml.converters.compchem.qespresso.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class QuantumEspressoLog2XMLConverter extends CompchemTemplateConverter {
	
	public QuantumEspressoLog2XMLConverter() {
		this(getDefaultTemplate("qespresso", "log", "topTemplate.xml", QuantumEspressoLog2XMLConverter.class));
	}
	
	public QuantumEspressoLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new QuantumEspressoLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}