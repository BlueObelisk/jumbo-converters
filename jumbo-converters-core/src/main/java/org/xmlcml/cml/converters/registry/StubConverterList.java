package org.xmlcml.cml.converters.registry;

import java.util.ArrayList;
import java.util.List;

public class StubConverterList implements ConverterList {

	public List<ConverterInfo> listConverters() {
		List<ConverterInfo> list = new ArrayList<ConverterInfo>();
		list.add(new ConverterInfo(StubHtml2TxtConverter.class));
		list.add(new ConverterInfo(StubTxt2PngConverter.class));
		return list;
	}

}
