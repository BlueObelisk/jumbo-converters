package org.xmlcml.cml.converters.compchem.gaussian.log;


import nu.xom.Element;

import org.xmlcml.cml.converters.text.TemplateConverter;

public class GaussianLog2XMLConverter extends TemplateConverter {
	
	public GaussianLog2XMLConverter(Element templateElement) {
		super(templateElement, "gaussian", "log");
	}
}
