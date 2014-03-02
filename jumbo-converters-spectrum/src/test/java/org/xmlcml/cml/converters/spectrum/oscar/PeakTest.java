package org.xmlcml.cml.converters.spectrum.oscar;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.euclid.Util;

public class PeakTest {

	@Test
	@Ignore // BUG in load stream
	public void propagateUnits() throws Exception {
		String instreamName = SpectrumCommon.OSCAR_DIR+"/ref/peakMissingCouplings.xml";
		System.err.println(instreamName);
//		InputStream in = ClassLoader.getSystemResourceAsStream(instreamName);
		InputStream in = Util.getInputStreamFromResource(instreamName);
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
