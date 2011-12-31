package org.xmlcml.cml.converters.registry;

import java.util.ArrayList;
import java.util.List;

public class StubConverterList implements ConverterList {

	public List<ConverterInfo> listConverters() {
		List<ConverterInfo> list = new ArrayList<ConverterInfo>();
		list.add(new ConverterInfo("html", "txt", StubHtml2TxtConverter.class, "HTML to Text Converter"));
		list.add(new ConverterInfo("txt", "png", StubTxt2PngConverter.class, "Text to PNG Converter"));
		return list;
	}

}
