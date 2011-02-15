package org.xmlcml.cml.converters.compchem.amber.mdout;





import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class AmberMdout2XMLConverter extends Text2XMLConverter {
	
	public AmberMdout2XMLConverter() {
		super();
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		return element;
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new AmberMdoutProcessor();
	}
}
