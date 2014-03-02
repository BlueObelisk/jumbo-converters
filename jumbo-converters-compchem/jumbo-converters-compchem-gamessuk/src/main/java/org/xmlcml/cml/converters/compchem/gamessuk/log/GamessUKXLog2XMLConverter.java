package org.xmlcml.cml.converters.compchem.gamessuk.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKXCommon;

public class GamessUKXLog2XMLConverter extends CompchemText2XMLTemplateConverter {
	
	private static final String GAMESSUK_LOG_TO_XML = "GamessUK_LOG to GamessUK_LOG_XML";

	public GamessUKXLog2XMLConverter() {
		this(getDefaultTemplate("gamessuk", "log", "topTemplate.xml", GamessUKXLog2XMLConverter.class));
	}
	
	public GamessUKXLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new GamessUKXLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
	
	@Override
	public String getRegistryInputType() {
		return GamessUKXCommon.GAMESSUK_LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return GamessUKXCommon.GAMESSUK_LOG_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return GAMESSUK_LOG_TO_XML;
	}
}