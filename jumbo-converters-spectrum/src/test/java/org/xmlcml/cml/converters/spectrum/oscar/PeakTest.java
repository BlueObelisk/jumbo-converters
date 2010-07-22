package org.xmlcml.cml.converters.spectrum.oscar;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLPeak;

public class PeakTest {

	@Test
	public void propagateUnits() throws Exception {
		InputStream in = ClassLoader.getSystemResourceAsStream("spectrum/oscar2cml/peakMissingCouplings.xml");
		CMLPeak cmlPeak = (CMLPeak) new CMLBuilder().build(in).getRootElement();
		Peak peak = new Peak();
		peak.setCMLPeak(cmlPeak);
		assertEquals(1, cmlPeak.cmlQuery("//cml:peakStructure[@units]").size());
		
		peak.propagateUnits();
		assertEquals(3, cmlPeak.cmlQuery("//cml:peakStructure[@units]").size());
		
		peak.propagateUnits();
		assertEquals(3, cmlPeak.cmlQuery("//cml:peakStructure[@units]").size());
	}
}
