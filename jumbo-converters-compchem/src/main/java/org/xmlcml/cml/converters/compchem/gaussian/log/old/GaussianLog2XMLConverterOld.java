package org.xmlcml.cml.converters.compchem.gaussian.log.old;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GaussianLog2XMLConverterOld extends Text2XMLConverter {
	
	public GaussianLog2XMLConverterOld() {
		super();
	}
	
	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new GaussianLogProcessor();
	}
}
