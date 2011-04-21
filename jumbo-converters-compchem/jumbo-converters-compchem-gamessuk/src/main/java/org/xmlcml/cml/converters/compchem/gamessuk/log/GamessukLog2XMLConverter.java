package org.xmlcml.cml.converters.compchem.gamessuk.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GamessukLog2XMLConverter extends CompchemTemplateConverter {
	
	public GamessukLog2XMLConverter() {
		this(getDefaultTemplate("gamesuk", "log", "topTemplate.xml", GamessukLog2XMLConverter.class));
	}
	
	public GamessukLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new GamessukLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}