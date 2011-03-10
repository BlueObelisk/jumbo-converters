package org.xmlcml.cml.converters.compchem.amber.mdout;

import org.junit.Ignore;
import org.junit.Test;

@Ignore // till we have templates
public class ConverterTest {
	@Test
	public void dummy() {
		
	}
	
	@Test         public void testAverage()      {testConverter("average");}
	@Test     @Ignore    public void testResults()      {testConverter("results");}
	@Test         public void testResource()     {testConverter("resource");}
	@Test         public void testSander()       {testConverter("sander");}

	private void testConverter(String name) {
//		TestUtils.runConverterTest(new AmberMdout2XMLConverter(), name);
	}
	
   @Test
   public void amberMdout2XML() {
//      RegressionSuite.run("compchem/amber/mdout", "out", "xml",
//                          new AmberMdout2XMLConverter());
   }

}
