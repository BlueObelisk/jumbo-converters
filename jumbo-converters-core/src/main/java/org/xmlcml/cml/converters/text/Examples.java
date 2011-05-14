package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

public class Examples {

	public static CMLElement runExample(String templateFileName, String inputFilename, String outputFilename) throws Exception {
		Text2XMLTemplateConverter converter = new Text2XMLTemplateConverter(new File(templateFileName));
		CMLElement outElement = (CMLElement) converter.convertToXML(new File(inputFilename));
		if (outputFilename != null) {
			CMLUtil.debug(outElement, new FileOutputStream(outputFilename), 0);
		}
		return outElement;
	}
	
	public static void main(String[] args) throws Exception {
		String templateFilename = args.length > 0 ? args[0] : "examples/amber.template.xml";
		String inputFilename = args.length > 1 ? args[1] : "examples/amber.inp";
		String outputFilename = args.length > 2 ? args[2] : "examples/amber.out.xml";
		CMLElement exampleElement = runExample(templateFilename, inputFilename, outputFilename);
		exampleElement.debug("output");
	}
}
