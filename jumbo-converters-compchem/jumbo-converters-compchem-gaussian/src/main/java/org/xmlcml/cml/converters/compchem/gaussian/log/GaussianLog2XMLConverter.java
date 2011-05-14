package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.util.ConverterUtils;

public class GaussianLog2XMLConverter extends CompchemText2XMLTemplateConverter {

	private static Logger LOG = Logger.getLogger(GaussianLog2XMLConverter.class);
	private static final String BASE_URI = 
		"classpath:/org/xmlcml/cml/converters/compchem/gaussian/log/templates/topTemplate.xml";
	
	public GaussianLog2XMLConverter() {
		this(BASE_URI, "templates/topTemplate.xml");
	}
	
	public GaussianLog2XMLConverter(String baseUri, String templateName) {
		this(ConverterUtils.buildElementIncludingBaseUri(baseUri, templateName, GaussianLog2XMLConverter.class));
	}
	
	public GaussianLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length == 1) {
			GaussianLog2XMLConverter converter = new GaussianLog2XMLConverter();
			ConverterUtils.convertFilesInDirectory(converter, args[0], ".out", ".cml");
		} else if (args.length == 2) {
			GaussianLog2XMLConverter converter = new GaussianLog2XMLConverter(BASE_URI,
			"templates/"+args[1]);
			ConverterUtils.convertFilesInDirectory(converter, args[0], ".out", ".cml");
		} else {
			CompchemText2XMLTemplateConverter converter = new GaussianLog2XMLConverter();
			File in = new File("src/test/resources/compchem/gaussian/log/in/anna0.log");
			File out = new File("test/anna0.xml");
			converter.convert(in, out);
		}
	}
}

