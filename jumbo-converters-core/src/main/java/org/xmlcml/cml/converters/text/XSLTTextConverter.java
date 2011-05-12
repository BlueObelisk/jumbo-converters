package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class XSLTTextConverter extends XML2TextConverter {
	private static final Logger LOG = Logger.getLogger(XSLTTextConverter.class);
	protected XSLTransform transform;

	protected XSLTTextConverter() {
	}
	
	public XSLTTextConverter(Document xsltDocument) {
		try {
	        transform = new XSLTransform(xsltDocument);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create XSLTTransform", e);
		}
	}
	
	public void setParameter(String parameterName, String parameterValue) {
		transform.setParameter(parameterName, parameterValue);
	}

	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = new ArrayList<String>();
		try {
		     Nodes nodes = transform.transform(xmlInput.getDocument());
		     for (int i = 0; i < nodes.size(); i++) {
		    	 Node node = nodes.get(i);
		    	 stringList.add(node.getValue());
		     }
		} catch (Exception e) {
			throw new RuntimeException("Cannot transform ", e);
		}
		return stringList;
	}

	protected static String convertToText(String inputFile, String xslFile) {
		String s = null;
		try {
			Document inputDoc = new Builder().build(new FileInputStream(inputFile));
			Document xslDoc = new Builder().build(new FileInputStream(xslFile));
			XSLTTextConverter converter = new XSLTTextConverter(xslDoc);
			List<String> lines = converter.convertToText(inputDoc.getRootElement());
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(line);
			}
			s = sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Failed XSLT transformation", e);
		}
		return s;
	}
	
	public static void main(String[] args) throws Exception {
		String inputFile = (args.length > 0) ? args[0] : "src/main/java/org/xmlcml/cml/converters/text/test.xml";
		String xslFile = (args.length > 1) ? args[1] : "src/main/java/org/xmlcml/cml/converters/text/test.xsl";
		String textFile = (args.length > 2) ? args[2] : "test/test.out";
		String text = convertToText(inputFile, xslFile);
		FileUtils.write(new File(textFile), text);
	}

}
