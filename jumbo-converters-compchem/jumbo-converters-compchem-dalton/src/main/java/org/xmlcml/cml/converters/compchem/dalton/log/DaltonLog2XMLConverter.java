package org.xmlcml.cml.converters.compchem.dalton.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.dalton.DaltonCommon;

public class DaltonLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	public static final String DALTON_LOG_TO_XML_CONVERTER = "Dalton Log to XML Converter";

	public DaltonLog2XMLConverter() {
		this(getDefaultTemplate("dalton", "log", "topTemplate.xml", DaltonLog2XMLConverter.class));
	}
	
	public DaltonLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new DaltonLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	
	@Override
	public String getRegistryInputType() {
		return DaltonCommon.DALTON_LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return DaltonCommon.DALTON_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return DALTON_LOG_TO_XML_CONVERTER;
	}
}