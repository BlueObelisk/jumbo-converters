package org.xmlcml.cml.converters.compchem.gaussian.log;


import java.io.File;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GaussianLog2XMLConverter extends CompchemTemplateConverter {
	
	public GaussianLog2XMLConverter() {
		this(getDefaultTemplate(
				"gaussian", "log", "topTemplate.xml", GaussianLog2XMLConverter.class));
	}
	
	public GaussianLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new GaussianLog2XMLConverter();
		File in = new File("D:\\projects\\anna-gaussian\\in\\1\\output.log");
		File out = new File("test-out.xml");
		converter.convert(in, out);
	}
}
