package org.xmlcml.cml.converters.text;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.XML2XMLConverter;

public class XML2XMLTransformConverter extends XML2XMLConverter {

	protected TransformListElement transformListElement;

	public XML2XMLTransformConverter(Element element) {
		this.transformListElement = new TransformListElement(element);
	}

	public XML2XMLTransformConverter(InputStream transformStream) {
		this.transformListElement = new TransformListElement(makeTemplateElement(transformStream));
	}

	public XML2XMLTransformConverter(File transformFile) throws IOException {
		this(new FileInputStream(transformFile));
	}
	
	private Element makeTemplateElement(InputStream templateStream) {
		try {
			return new Builder().build(templateStream).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot build template: ", e);
		}
	}

	@Override
	public Element convertToXML(Element element) {
		transformListElement.applyMarkup(element);
		return element;
	}
	
	public static void main(String[] args) throws Exception {
		String transformFilename = null; 
		String inputFilename = null; 
		String outputFilename = null; 
		XML2XMLTransformConverter converter = null;
		if (args.length == 3) {
			transformFilename = args[0]; 
			inputFilename = args[1]; 
			outputFilename = args[2]; 
		} else {
			transformFilename = "src/main/resources/org/xmlcml/cml/converters/text/testTransform.xml"; 
			inputFilename = "src/main/resources/org/xmlcml/cml/converters/text/testInput.xml"; 
			outputFilename = "test/testTransform.output.xml";
		}
		converter = new XML2XMLTransformConverter(new File(transformFilename));
		converter.convert(new File(inputFilename), new File(outputFilename));
	}
}
