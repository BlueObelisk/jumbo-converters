package org.xmlcml.cml.converters.compchem.qespresso.log;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class QuantumEspressoLog2XMLConverterOld extends Text2XMLConverter {
	
	public QuantumEspressoLog2XMLConverterOld() {
		super();
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		return element;
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		throw new RuntimeException("changing processor type");
//		return new QuantumEspressoLogProcessor();
	}
}
