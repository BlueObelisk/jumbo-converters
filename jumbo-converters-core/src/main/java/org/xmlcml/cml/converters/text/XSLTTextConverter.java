package org.xmlcml.cml.converters.text;

import java.io.FileInputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.xslt.XSLTransform;

import org.apache.log4j.Logger;

public class XSLTTextConverter extends XML2TextConverter {
	private static final Logger LOG = Logger.getLogger(XSLTTextConverter.class);
	private Document xsltDocument;

	public XSLTTextConverter(Document xsltDocument) {
		this.xsltDocument = xsltDocument;
	}
	
	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = null;
		try {
		     XSLTransform transform = new XSLTransform(xsltDocument);
		     Nodes nodes = transform.transform(xmlInput.getDocument());
		} catch (Exception e) {
			throw new RuntimeException("Cannot transform ", e);
		}
		return stringList;
	}

	public static void main(String[] args) throws Exception {
		String inputFile = (args.length > 0) ? args[0] : "test.xml";
		Document inputDoc = new Builder().build(new FileInputStream(inputFile));
		String xslFile = (args.length > 1) ? args[1] : "test.xsl";
		Document xslDoc = new Builder().build(new FileInputStream(xslFile));
		String textFile = (args.length > 2) ? args[2] : "test.out";
		XSLTTextConverter converter = new XSLTTextConverter(xslDoc);
		List<String> lines = converter.convertToText(inputDoc.getRootElement());
	}
}
