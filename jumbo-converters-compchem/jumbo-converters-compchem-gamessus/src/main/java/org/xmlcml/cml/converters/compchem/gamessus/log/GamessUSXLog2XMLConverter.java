package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSXCommon;

public class GamessUSXLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	private static final String GAMESSUS_LOG_TO_XML = "GamessUS Log to Log_XML";

	public GamessUSXLog2XMLConverter() {
		this(getDefaultTemplate("gamessus", "log", "topTemplate.xml", GamessUSXLog2XMLConverter.class));
	}
	
	public GamessUSXLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length == 2) {
			CompchemText2XMLTemplateConverter converter = new GamessUSXLog2XMLConverter();
			File in = new File(args[0]);
			File out = new File(args[1]);
			converter.convert(in, out);
		} else {
			CompchemText2XMLTemplateConverter converter = new GamessUSXLog2XMLConverter();
//			File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//			File out = new File("test-out.xml");
//			converter.convert(in, out);
		}
	}
	
	@Override
	public String getRegistryInputType() {
		return GamessUSXCommon.GAMESSUS_LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return GamessUSXCommon.GAMESSUS_LOG_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return GAMESSUS_LOG_TO_XML;
	}
}