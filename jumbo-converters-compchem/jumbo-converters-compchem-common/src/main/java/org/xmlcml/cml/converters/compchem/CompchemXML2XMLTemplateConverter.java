package org.xmlcml.cml.converters.compchem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.euclid.Util;

public class CompchemXML2XMLTemplateConverter extends XML2XMLTransformConverter {

	public CompchemXML2XMLTemplateConverter(Element templateElement) {
		super(templateElement);
//		this.template = new Template(templateElement);
	}

	public static XML2XMLTransformConverter createTemplateConverter(
			InputStream templateStream, String codeBase, String fileType) throws IOException {
		try {
			Element templateElement = 
				new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			return new CompchemXML2XMLTemplateConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("cannot read/parse input template",e);
		}
	}

	public static String createBaseURI(String codeBase, String fileType) {
        return "classpath:/org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/";
	}

	private static XML2XMLTransformConverter createTemplateConverter(String codeBase,
			String fileType, String topTemplate) throws IOException {
		InputStream templateStream = createTemplateStream(codeBase, fileType, topTemplate);
		XML2XMLTransformConverter tc = CompchemXML2XMLTemplateConverter.createTemplateConverter(
				templateStream, codeBase, fileType);
		return tc;
	}

	protected static InputStream createTemplateStream(String codeBase,
			String fileType, String topTemplate) throws IOException {
		String templateXML = "org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/"+topTemplate;
		InputStream templateStream = Util.getInputStreamFromResource(templateXML);
		return templateStream;
	}

	@Override
	public void usage() {
		System.err.println("Usage : <infile> <outfile> [<templateFile>]");
	}


	public static void runMain(String[] args, String code, String fileType,
			String topTemplate) throws IOException {
		if (args.length == 0) {
			usage();
		} else {
			XML2XMLTransformConverter tc = CompchemXML2XMLTemplateConverter.createTemplateConverter(code, fileType, topTemplate);
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	protected static Element getDefaultTemplate(String codeType, String fileType,
		String templateName, Class<?> clazz) {
		String baseUri = "classpath:/org/xmlcml/cml/converters/compchem/"+codeType+"/"+fileType+"/topTemplate.xml";
		return getDefaultTemplate(baseUri, templateName, clazz);
	}

	protected static Element getDefaultTemplate(String baseUri, String templateName, Class<?> clazz) {
		try {
			InputStream in = clazz.getResourceAsStream(templateName);
			if (in == null) {
				throw new FileNotFoundException("File not found: "+templateName);
			}
			try {
				Builder builder = new Builder();
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
