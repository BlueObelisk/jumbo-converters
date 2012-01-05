package org.xmlcml.cml.converters.compchem.dlpoly.log;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.dlpoly.DLPolyCommon;

public class DLPolyLog2XMLConverter extends CompchemText2XMLTemplateConverter {

	public static final String DLPOLY_LOG_TO_XML_CONVERTER = "DLPoly Log to XML Converter";

	public DLPolyLog2XMLConverter() {
		this(getDefaultTemplate("dlpoly", "log", "topTemplate.xml", DLPolyLog2XMLConverter.class));
	}

	public DLPolyLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}

	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new DLPolyLog2XMLConverter();
	}

	@Override
	public String getRegistryInputType() {
		return DLPolyCommon.DLPOLY_LOG;
	}

	@Override
	public String getRegistryOutputType() {
		return DLPolyCommon.DLPOLY_XML;
	}

	@Override
	public String getRegistryMessage() {
		return DLPOLY_LOG_TO_XML_CONVERTER;
	}
}