package org.xmlcml.cml.converters.compchem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.euclid.Util;

public class CompchemTemplateConverter extends TemplateConverter {

	private String codeBase;
	private String fileType;

	public CompchemTemplateConverter(Element templateElement, String cBase, String fType) {
		super(templateElement);
		codeBase = cBase;
		fileType = fType;
		legacyProcessor = createLegacyProcessor();
		this.template = new Template(templateElement);
	}

	public static TemplateConverter createTemplateConverter(InputStream templateStream, String codeBase, String fileType) {
		Element templateElement = null;
		TemplateConverter converter = null;
		try {
			templateElement = new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			converter = new CompchemTemplateConverter(templateElement, codeBase, fileType);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template: ", e);
		}
		return converter;
	}

	public static String createBaseURI(String codeBase, String fileType) {
//		return "src/main/resources/org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/templates/";
        return "classpath:/org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/templates/";
	}

	public static void usage() {
		System.err.println("Usage : <infile> <outfile>");
	}


	public static void runMain(String[] args, String code, String fileType,
			String topTemplate) throws IOException {
		if (args.length != 2) {
			usage();
		} else {
			TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(code, fileType, topTemplate);
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	private static TemplateConverter createTemplateConverter(String code,
			String fileType, String topTemplate) throws IOException {
		InputStream templateStream = createTemplateStream(code, fileType, topTemplate);
		TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(templateStream, code, fileType);
		return tc;
	}

	protected static InputStream createTemplateStream(String code,
			String fileType, String topTemplate) throws IOException {
		String templateXML = "org/xmlcml/cml/converters/compchem/"+code+"/"+fileType+"/"+topTemplate;
		InputStream templateStream = Util.getInputStreamFromResource(templateXML);
		return templateStream;
	}

}
