package org.xmlcml.cml.converters.compchem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.euclid.Util;

public class CompchemTemplateConverter extends TemplateConverter {

	public CompchemTemplateConverter(Element templateElement) {
		super(templateElement);
		legacyProcessor = createLegacyProcessor();
		this.template = new Template(templateElement);
	}

	public static TemplateConverter createTemplateConverter(
			InputStream templateStream, String codeBase, String fileType) throws IOException {
		try {
			Element templateElement = 
				new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			return new CompchemTemplateConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("cannot read/parse input template",e);
		}
	}

	public static String createBaseURI(String codeBase, String fileType) {
        return "classpath:/org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/";
	}

	private static TemplateConverter createTemplateConverter(String codeBase,
			String fileType, String topTemplate) throws IOException {
		InputStream templateStream = createTemplateStream(codeBase, fileType, topTemplate);
		TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(
				templateStream, codeBase, fileType);
		return tc;
	}

	protected static InputStream createTemplateStream(String codeBase,
			String fileType, String topTemplate) throws IOException {
		String templateXML = "org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/"+topTemplate;
		InputStream templateStream = Util.getInputStreamFromResource(templateXML);
		return templateStream;
	}

	public static void usage() {
		System.err.println("Usage : <infile> <outfile> [<templateFile>]");
	}


	public static void runMain(String[] args, String code, String fileType,
			String topTemplate) throws IOException {
		if (args.length == 0) {
			usage();
		} else {
			TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(code, fileType, topTemplate);
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	protected static Element getDefaultTemplate(String codeType, String fileType,
			String templateName, Class<?> clazz) {
				try {
					InputStream in = clazz.getResourceAsStream(templateName);
					if (in == null) {
						throw new FileNotFoundException("File not found: "+templateName);
					}
					try {
						Builder builder = new Builder();
						String baseUri = "classpath:/org/xmlcml/cml/converters/compchem/"+codeType+"/"+fileType+"/topTemplate.xml";
						Document doc = builder.build(in, baseUri);
						return doc.getRootElement();
					} finally {
						IOUtils.closeQuietly(in);
					}
				} catch (Exception e) {
					throw new RuntimeException("Error loading default template", e);
				}
			}

}
