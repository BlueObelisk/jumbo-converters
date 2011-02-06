package org.xmlcml.cml.converters.compchem.amber.in;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
	@Test         public void testAngl()    {testConverter("angl");}
	@Test         public void testBond()       {testConverter("bond");}
	@Test         public void testDihe()       {testConverter("dihe");}
	
	@Test         public void testAtomname()   {testConverter("atomname");}
	@Test         public void testPointer()    {testConverter("pointer");}
	@Test         public void testTitle()      {testConverter("title");}
	@Test         public void testVersion()    {testConverter("version");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new AmberFF2XMLConverter(), name);
	}
	
	   @Test
	   @Ignore
	   public void amberFF2XML() {
	      RegressionSuite.run("compchem/amber/in", "txt", "xml",
	                          new AmberFF2XMLConverter());
	   }
	   @Test
	   public void testParm() {
	      RegressionSuite.run("compchem/amber/in", "dat", "xml",
	                          new AmberFF2XMLConverter());
	   }

}
