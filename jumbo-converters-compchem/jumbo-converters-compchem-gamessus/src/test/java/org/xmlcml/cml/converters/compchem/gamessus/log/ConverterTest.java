package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.junit.Ignore;
import org.junit.Test;

@Ignore // till we fix templates
public class ConverterTest {
	@Ignore
	@Test         public void testBasisOpt()    {testConverter("basisopt");}

	private void testConverter(String name) {
//		TestUtils.runConverterTest(new GamessUSLog2XMLConverter(), name);
	}
	
	   @Test
	   @Ignore
	   public void nwchemOut2XML() {
//	      RegressionSuite.run("compchem/nwchem/log", "out", "xml",
//	                          new GamessUSLog2XMLConverter());
	   }

}
