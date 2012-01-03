package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
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
//			convertFile(converter, "gtest", "test002");
//			convertFile(converter, "in", "jobTest");
//			convertFile(converter, "in", "anna0");
			for (int i = 0; i < 10; i++) {
////				convertFile(converter, "in", "anna"+i);
				convertFile(converter, "gtest", "test00"+i);
			}
		}
	}

	private static void convertFile(CompchemText2XMLTemplateConverter converter, String dir, String fileRoot) {
		File in = new File("src/test/resources/compchem/gaussian/log/"+dir+"/"+fileRoot+".log");
		if (!in.exists()) {
			LOG.warn("File does not exist: "+in);
		} else {
			File out = new File("test/"+dir+"/"+fileRoot+".raw.xml");
			System.out.println("====== converting "+in+" ==========");
			try {
				converter.convert(in, out);
			} catch (Exception e) {
				LOG.warn("conversion failed for "+in+" ("+e+")");
			}
		}
	}
	
	@Override
	public String getRegistryInputType() {
		return GaussianCommon.LOG;
	}

	@Override
	public String getRegistryOutputType() {
		return GaussianCommon.LOG_XML;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert Gaussian Log to XML";
	}

}

