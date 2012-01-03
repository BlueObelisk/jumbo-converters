package org.xmlcml.cml.converters.compchem.mopac.auxx;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.mopac.MopacCommon;

public class MopacAux2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	public MopacAux2XMLConverter() {
		this(getDefaultTemplate("mopac", "auxx", "topTemplate.xml", MopacAux2XMLConverter.class));
	}
	
	public MopacAux2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new MopacAux2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	@Override
	public String getRegistryInputType() {
		return MopacCommon.AUX;
	}

	@Override
	public String getRegistryOutputType() {
		return MopacCommon.XML;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert Mopac Aux to XML";
	}


}