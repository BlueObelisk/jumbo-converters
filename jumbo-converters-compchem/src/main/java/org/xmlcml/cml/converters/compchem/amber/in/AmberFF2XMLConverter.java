package org.xmlcml.cml.converters.compchem.amber.in;





import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class AmberFF2XMLConverter extends Text2XMLConverter {
	
	public AmberFF2XMLConverter() {
		super();
	}
	
	public String getMarkerResourceName() {
		return "org/xmlcml/cml/converters/compchem/amber/in/marker1.xml";
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		return element;
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new AmberFFProcessor();
	}
}
