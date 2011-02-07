package org.xmlcml.cml.converters.compchem.gaussian.log;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
	@Test         public void testAnisospin()    {testConverter("anisospin");}
	@Test         public void testMulliken()    {testConverter("mulliken");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new GaussianLog2XMLConverter(), name);
	}
	
	   @Test
	   public void gaussianOut2XML() {
	      RegressionSuite.run("compchem/gaussian/log", "log", "xml",
	                          new GaussianLog2XMLConverter());
	   }

}
