package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;

public class GamessusLog2XMLConverter extends CompchemTemplateConverter {
	
	public GamessusLog2XMLConverter() {
		this(getDefaultTemplate("gamesus", "log", "topTemplate.xml", GamessusLog2XMLConverter.class));
	}
	
	public GamessusLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static void main(String[] args) throws IOException {
		CompchemTemplateConverter converter = new GamessusLog2XMLConverter();
//		File in = new File("D:\\projects\\nwchem-tests\\in\\ch3f_rot\\ch3f_rot.out");
//		File out = new File("test-out.xml");
//		converter.convert(in, out);
	}
}