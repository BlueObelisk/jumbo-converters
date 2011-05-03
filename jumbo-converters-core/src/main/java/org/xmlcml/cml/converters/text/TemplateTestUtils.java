package org.xmlcml.cml.converters.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class TemplateTestUtils {

	private static final String EXAMPLE_INPUT = "example.input";
	private static final String EXAMPLE_OUTPUT = "example.output";
//	private static final String EXAMPLE_OUTPUT_CML = "example.output.cml";
	private static final String ID = "id";
	
	public static void runCommentExamples(String templateResourceName, String baseUri) {
		Element templateElement = getTemplate(templateResourceName, baseUri);
		Nodes templatesWithExamples = templateElement.query(
				".//template[comment[@class='"+EXAMPLE_INPUT+"' and @id]]");
		for (int i = 0; i < templatesWithExamples.size(); i++) {
			runCommentExamples((Element) templatesWithExamples.get(i));
		}
	}

	public static void testDocument(InputStream inputStream, InputStream refStream, 
			OutputStream outputStream,
			String templateResourceName, String baseUri, boolean compare) throws IOException {
		Element templateElement = getTemplate(templateResourceName, baseUri);
		TemplateConverter tc = new TemplateConverter(templateElement);
		String text = IOUtils.toString(inputStream);
		Element testXml = parseText(tc, text);
		CMLUtil.debug(testXml, outputStream, 1);
		if (compare) {
			Element refXml = CMLUtil.parseQuietlyToDocument(refStream).getRootElement();
			JumboTestUtils.assertEqualsCanonically("test template", refXml, testXml, true);
		}
	}
	
	public static Element getTemplate(String templateResourceName, String baseUri) {
		Element templateElement = null;
		try {
			InputStream is = Util.getResourceUsingContextClassLoader(templateResourceName, TemplateTestUtils.class);
			templateElement = new CMLBuilder().build(is, baseUri).getRootElement();
			ClassPathXIncludeResolver.resolveIncludes(templateElement.getDocument(), new CMLBuilder());
		} catch (Exception e) {
			throw new RuntimeException("cannot create template", e);
		}
		return templateElement;
	}

	public static void runCommentExamples(Element template) {
		TemplateConverter tc = new TemplateConverter(template);
		Nodes exampleInputComments = template.query("comment[@class='"+EXAMPLE_INPUT+"' and @id]"); 
		if (exampleInputComments.size() == 0) {
			throw new RuntimeException("No examples found");
		}
		for (int j = 0; j < exampleInputComments.size(); j++) {
			Element exampleInput = (Element) exampleInputComments.get(j);
			String id = exampleInput.getAttributeValue(ID);
			if (id == null) {
				throw new RuntimeException("outputElement must have id: ");
			}
			Element outputElement = getOutputElement(template, id);
			if (outputElement == null) {
				throw new RuntimeException("Cannot create OutputElement: "+id);
			}
			String exampleContent = exampleInput.getValue();
			Element outputXML = parseText(tc, exampleContent);
			JumboTestUtils.assertEqualsCanonically(
					"template", outputElement, outputXML, true);
		}
	}

	private static Element parseText(TemplateConverter converter, String content) {
		if (content.startsWith(CMLConstants.S_NEWLINE)) {
			content = content.substring(1);
		}
		List<String> stringList = Arrays.asList(content.split(CMLConstants.S_NEWLINE));
		Element outputXML = converter.convertToXML(stringList);
		return outputXML;
	}

	private static Element getOutputElement(Element template, String id) {
		Nodes nodes = template.query("comment[@class='"+EXAMPLE_OUTPUT+"' and @id='"+id+"']");
		if (nodes.size() != 1) {
			throw new RuntimeException("Missing output for: "+id+"; found "+nodes.size()+" nodes");
		}
		Element content = (Element) nodes.get(0).getChild(0);
		if (content == null) {
			throw new RuntimeException("output must have content: "+id);
		}
		return content;
	}

}
