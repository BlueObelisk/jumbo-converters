package org.xmlcml.cml.converters.compchem.gaussian.log;





import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GaussianLog2XMLConverter extends Text2XMLConverter {
	
	public GaussianLog2XMLConverter() {
	}
	
	public String getMarkerResourceName() {
		return "org/xmlcml/cml/converters/compchem/gaussian/log/marker1.xml";
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new GaussianLogProcessor();
	}
	

}
