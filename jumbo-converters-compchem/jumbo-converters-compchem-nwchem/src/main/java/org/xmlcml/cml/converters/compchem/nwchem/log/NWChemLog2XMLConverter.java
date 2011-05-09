package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class NWChemLog2XMLConverter extends CompchemTemplateConverter {

	public NWChemLog2XMLConverter() {
//		this(getDefaultTemplate("nwchem", "log", "topTemplate.xml", NWChemLog2XMLConverter.class));
		this(getDefaultTemplate("classpath:/org/xmlcml/cml/converters/compchem/nwchem/log/templates/topTemplate.xml",
				"templates/topTemplate.xml", NWChemLog2XMLConverter.class));
	}
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	private static void runNWTests() {
		String allfileName = "D:/projects/nwchem-tests/allfiles";
		File allfileDir = new File(allfileName);
		File[] files = allfileDir.listFiles();
		CompchemTemplateConverter converter = new NWChemLog2XMLConverter();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".out")) {
				File out = new File(file.getAbsolutePath()+".cml");
				if (!out.exists()) {
					System.out.println("converting "+file+" to "+out);
					converter.convert(file, out);
				}
			}
		}
	}
	public static void main(String[] args) throws IOException {
		if (args.length == 1 && "ALL".equals(args[0])) {
			runNWTests();
		} else {
			CompchemTemplateConverter converter = new NWChemLog2XMLConverter();
			File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
			File out = new File("ch3f_rot.xml");
			converter.convert(in, out);
		}
	}
}
