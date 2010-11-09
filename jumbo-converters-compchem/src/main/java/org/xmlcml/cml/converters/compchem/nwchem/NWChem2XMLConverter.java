package org.xmlcml.cml.converters.compchem.nwchem;





import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class NWChem2XMLConverter extends Text2XMLConverter {
	
	public NWChem2XMLConverter() {
	}
	
	public String getMarkerResourceName() {
		return "org/xmlcml/cml/converters/compchem/nwchem/marker1.xml";
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		legacyProcessor = new NWChemProcessor();
		legacyProcessor.read((CMLElement)element);
		return legacyProcessor.getCMLElement();
	}

	
}
