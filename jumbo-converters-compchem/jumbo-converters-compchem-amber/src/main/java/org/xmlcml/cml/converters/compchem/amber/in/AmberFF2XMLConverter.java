package org.xmlcml.cml.converters.compchem.amber.in;





import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class AmberFF2XMLConverter extends Text2XMLConverter {
	
    public static final String AMBER_FF_TO_AMBER_FF_XML = "Amber-FF to Amber-FF-XML";
	
	public AmberFF2XMLConverter() {
		super();
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		return element;
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return null;
	}
	
	@Override
	public String getRegistryInputType() {
		return AmberCommon.AMBER_FF;
	}
	
	@Override
	public String getRegistryOutputType() {
		return AmberCommon.AMBER_FF_XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return AMBER_FF_TO_AMBER_FF_XML;
	}
}
