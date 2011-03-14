package org.xmlcml.cml.converters.compchem.nwchem.log;

import nu.xom.Element;

import org.xmlcml.cml.converters.text.TemplateConverter;

public class NWChemLog2XMLConverter extends TemplateConverter {
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement, "nwchem", "log");
	}
	

}
