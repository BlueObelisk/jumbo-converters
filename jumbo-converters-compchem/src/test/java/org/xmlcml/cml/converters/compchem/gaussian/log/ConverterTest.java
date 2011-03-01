package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.compchem.gaussian.log.old.GaussianLog2XMLConverterOld;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.euclid.Util;

public class ConverterTest {
	@Test    @Ignore     public void testAnisospin()    {testConverter("anisospin");}
	@Test         public void testMulliken()    {testConverter("mulliken");}

	private void testConverter(String name) {
		try {
			InputStream templateStream = Util.getInputStreamFromResource(
			"org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml");
			GaussianLog2XMLConverter converter = GaussianLog2XMLConverter.createGaussianLog2XMLConverter(templateStream);
			TestUtils.runConverterTest(converter, name);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template or run converter", e);
		}
	}
	
	   @Test
	   @Ignore // till we have templates
	   public void gaussianOut2XML() {
	      RegressionSuite.run("compchem/gaussian/log", "log", "xml",
	                          new GaussianLog2XMLConverterOld());
	   }

}
