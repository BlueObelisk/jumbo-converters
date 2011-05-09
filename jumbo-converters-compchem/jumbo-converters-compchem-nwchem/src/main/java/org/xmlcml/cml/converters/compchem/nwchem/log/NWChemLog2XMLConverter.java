package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class NWChemLog2XMLConverter extends CompchemTemplateConverter {

	private static Logger LOG = Logger.getLogger(NWChemLog2XMLConverter.class);
	private static final String BASE_URI = "classpath:/org/xmlcml/cml/converters/compchem/nwchem/log/templates/topTemplate.xml";
	public NWChemLog2XMLConverter() {
		this(BASE_URI, "templates/topTemplate.xml");
	}
	
	public NWChemLog2XMLConverter(String baseUri, String templateName) {
		this(getDefaultTemplate(baseUri, templateName, NWChemLog2XMLConverter.class));
	}
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	private void runNWTests(String dirName) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		if (files == null) {
			throw new RuntimeException("No files found in "+dir.getAbsolutePath());
		}
		LOG.info("Processing "+files.length+" files");
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".out")) {
				File out = new File(file.getAbsolutePath()+".cml");
//				if (!out.exists()) {
					System.out.println("converting "+file+" to "+out);
					this.convert(file, out);
//				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		// e.g. nwchem/parsable
		if (args.length == 1) {
			NWChemLog2XMLConverter converter = new NWChemLog2XMLConverter();
			converter.runNWTests(args[0]);
		} else if (args.length == 2) {
			NWChemLog2XMLConverter converter = new NWChemLog2XMLConverter(BASE_URI,
			"templates/"+args[1]);
			converter.runNWTests(args[0]);
		} else {
			CompchemTemplateConverter converter = new NWChemLog2XMLConverter();
			File in = new File("nwtests/parsable/ch3f_rot.out");
			File out = new File("ch3f_rot.xml");
			converter.convert(in, out);
		}
	}
}
