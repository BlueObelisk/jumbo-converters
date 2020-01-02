package org.xmlcml.cml.converters.compchem.qchem.log;

import org.junit.Ignore;
import org.junit.Test;

@Ignore // till we fix templates
public class ConverterTest {
	@Ignore
	@Test         public void testBasisOpt()    {testConverter("basisopt");}

	private void testConverter(String name) {
//		TestUtils.runConverterTest(new QchemLog2XMLConverter(), name);
	}
	
	   @Test
	   @Ignore
	   public void nwchemOut2XML() {
//	      RegressionSuite.run("compchem/qchem/log", "out", "xml",
//	                          new QchemLog2XMLConverter());
	   }

}
