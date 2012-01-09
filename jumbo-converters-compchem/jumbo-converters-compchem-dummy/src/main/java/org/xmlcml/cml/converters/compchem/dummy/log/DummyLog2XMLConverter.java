package org.xmlcml.cml.converters.compchem.dummy.log;

import java.io.IOException;


import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.dummy.DummyCommon;

public class DummyLog2XMLConverter extends CompchemText2XMLTemplateConverter {

	public static final String DUMMY_LOG_TO_XML_CONVERTER = "Dummy Log to XML Converter";

	public DummyLog2XMLConverter() {
		this(getDefaultTemplate("dummy", "log", TEMPLATE_XML_REL_TO_CLAZZ, DummyLog2XMLConverter.class));
	}

	public DummyLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}

	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new DummyLog2XMLConverter();
	}

	@Override
	public String getRegistryInputType() {
		return DummyCommon.DUMMY_LOG;
	}

	@Override
	public String getRegistryOutputType() {
		return DummyCommon.DUMMY_XML;
	}

	@Override
	public String getRegistryMessage() {
		return DUMMY_LOG_TO_XML_CONVERTER;
	}
}