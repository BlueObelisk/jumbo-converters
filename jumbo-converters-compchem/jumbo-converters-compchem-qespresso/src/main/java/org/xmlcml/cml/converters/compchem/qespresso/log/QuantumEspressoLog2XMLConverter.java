package org.xmlcml.cml.converters.compchem.qespresso.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;

public class QuantumEspressoLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	public QuantumEspressoLog2XMLConverter() {
		this(getDefaultTemplate("qespresso", "log", "topTemplate.xml", QuantumEspressoLog2XMLConverter.class));
	}
	
	public QuantumEspressoLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new QuantumEspressoLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	
	@Override
	public String getRegistryInputType() {
		return QEspressoCommon.LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return QEspressoCommon.XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "Convert Quantum Espresso log files to compchem";
	}
}