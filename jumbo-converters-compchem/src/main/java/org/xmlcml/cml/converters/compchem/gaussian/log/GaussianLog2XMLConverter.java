package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GaussianLog2XMLConverter extends Text2XMLConverter {
	
	public GaussianLog2XMLConverter() {
		super();
	}
	
	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new GaussianLogProcessor();
	}
}
