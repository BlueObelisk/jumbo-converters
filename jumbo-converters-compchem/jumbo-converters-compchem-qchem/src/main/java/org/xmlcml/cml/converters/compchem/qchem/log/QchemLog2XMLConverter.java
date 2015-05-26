package org.xmlcml.cml.converters.compchem.qchem.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.cml.converters.compchem.qchem.QchemCommon;

public class QchemLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	private static final String QCHEM_LOG_TO_XML = "Q-Chem Log to Log_XML";

	public QchemLog2XMLConverter() {
		this(getDefaultTemplate("qchem", "log", "topTemplate.xml", QchemLog2XMLConverter.class));
	}
	
	public QchemLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length == 2) {
			CompchemText2XMLTemplateConverter converter = new QchemLog2XMLConverter();
			File in = new File(args[0]);
			File out = new File(args[1]);
			converter.convert(in, out);
		} else {
			CompchemText2XMLTemplateConverter converter = new QchemLog2XMLConverter();
//			File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//			File out = new File("test-out.xml");
//			converter.convert(in, out);
		}
	}
	
	@Override
	public String getRegistryInputType() {
		return QchemCommon.QCHEM_LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return QchemCommon.QCHEM_LOG_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return QCHEM_LOG_TO_XML;
	}
}