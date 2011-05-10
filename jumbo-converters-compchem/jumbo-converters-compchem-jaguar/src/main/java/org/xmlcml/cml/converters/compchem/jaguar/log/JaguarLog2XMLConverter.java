package org.xmlcml.cml.converters.compchem.jaguar.log;

import java.io.File;

import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class JaguarLog2XMLConverter extends CompchemTemplateConverter {
	
	private static Logger LOG = Logger.getLogger(JaguarLog2XMLConverter.class);
	private static final String BASE_URI = "classpath:/org/xmlcml/cml/converters/compchem/jaguar/log/templates/topTemplate.xml";
	public JaguarLog2XMLConverter() {
		this(BASE_URI, "templates/topTemplate.xml");
	}
	
	public JaguarLog2XMLConverter(String baseUri, String templateName) {
		this(getDefaultTemplate(baseUri, templateName, JaguarLog2XMLConverter.class));
	}
	
	public JaguarLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void usage() {
		System.out.println("JaguarConverter <infile> <outfile>");
	}
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			usage();
		} else {
			CompchemTemplateConverter converter = new JaguarLog2XMLConverter();
			File in = new File(args[0]);
			File out = new File(args[1]);
			converter.convert(in, out);
		}
	}
}