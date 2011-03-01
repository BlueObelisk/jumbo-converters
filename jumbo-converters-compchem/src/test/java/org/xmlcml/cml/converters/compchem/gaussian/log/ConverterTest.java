package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.compchem.gaussian.log.old.GaussianLog2XMLConverterOld;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.euclid.Util;

public class ConverterTest {
	@Test    @Ignore     public void testAnisospin()    {testConverter("anisospin");}
	@Test   public void testAtomicCharges()    {testConverter("atomiccharges");}
	@Test   public void testMulliken()    {testConverter("mulliken");}

    @Test
    public void gaussianOut2XML() {
		GaussianLog2XMLConverter converter = createConverter("templateList.xml");
        RegressionSuite.run("compchem/gaussian/log", "log", "xml", converter);
    }
   
	private void testConverter(String name) {
		GaussianLog2XMLConverter converter = createConverter("templateListAll.xml");
		TestUtils.runConverterTest(converter, name);
	}
		
	private GaussianLog2XMLConverter createConverter(String templateXML) {
		GaussianLog2XMLConverter converter = null;
		try {
			InputStream templateStream = Util.getInputStreamFromResource(
			"org/xmlcml/cml/converters/compchem/gaussian/log/"+templateXML);
			converter = GaussianLog2XMLConverter.createGaussianLog2XMLConverter(templateStream);
		} catch (Exception e) {
			throw new RuntimeException("Cannot make template ", e);
		}
		return converter;
	}
	

}
