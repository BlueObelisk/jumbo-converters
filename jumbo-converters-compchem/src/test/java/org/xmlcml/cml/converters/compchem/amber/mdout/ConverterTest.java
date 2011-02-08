package org.xmlcml.cml.converters.compchem.amber.mdout;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
	@Test         public void testResource1()    {testConverter("resource1");}
	
	@Test @Ignore         public void testResults()      {testConverter("results");}
	@Test @Ignore        public void testResults1()      {testConverter("results1");}
	@Test         public void testSander()       {testConverter("sander");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new AmberMdout2XMLConverter(), name);
	}
	
	   @Test
	   public void amberMdout2XML() {
	      RegressionSuite.run("compchem/amber/mdout", "out", "xml",
	                          new AmberMdout2XMLConverter());
	   }

}
