package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.io.IOException;



import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;

public class GamessusLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	private static final String GAMESSUS_LOG_TO_XML = "GamessUS Log to Log_XML";

	public GamessusLog2XMLConverter() {
		this(getDefaultTemplate("gamesus", "log", "topTemplate.xml", GamessusLog2XMLConverter.class));
	}
	
	public GamessusLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new GamessusLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	
	@Override
	public String getRegistryInputType() {
		return GamessUSCommon.GAMESSUS_LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return GamessUSCommon.GAMESSUS_LOG_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return GAMESSUS_LOG_TO_XML;
	}
}