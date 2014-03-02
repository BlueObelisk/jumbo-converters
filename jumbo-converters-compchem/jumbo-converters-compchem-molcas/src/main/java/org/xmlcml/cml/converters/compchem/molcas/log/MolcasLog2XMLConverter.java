package org.xmlcml.cml.converters.compchem.molcas.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.molcas.MolcasCommon;

public class MolcasLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	public MolcasLog2XMLConverter() {
		this(getDefaultTemplate("molcas", "log", "topTemplate.xml", MolcasLog2XMLConverter.class));
	}
	
	public MolcasLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new MolcasLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	
	@Override
	public String getRegistryInputType() {
		return MolcasCommon.LOG;
	}

	@Override
	public String getRegistryOutputType() {
		return MolcasCommon.LOG_XML;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert Molcas Log to XML";
	}

}