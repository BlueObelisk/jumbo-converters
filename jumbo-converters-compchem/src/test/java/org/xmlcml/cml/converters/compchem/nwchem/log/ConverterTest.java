package org.xmlcml.cml.converters.compchem.nwchem.log;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
	@Test         public void testAcknow()    {testConverter("acknow");}
	@Test         public void testAng()       {testConverter("ang");}
	@Test         public void testArgument()  {testConverter("argument");}
	@Test         public void testAtmass()    {testConverter("atmass");}
	@Test         public void testAuthors()   {testConverter("authors");}
	@Test         public void testCitation()  {testConverter("citation");}
	@Test         public void testDirinfo()   {testConverter("dirinfo");}
	@Test         public void testGastats()   {testConverter("gastats");}
	@Test         public void testGeom()      {testConverter("geom");}
    @Test         public void testJob()       {testConverter("job");}
	@Test         public void testMemory()    {testConverter("memory");}
	@Test         public void testMomint()    {testConverter("momint");}
// fail	@Test         public void testNccp()      {testConverter("nccp");}
	@Test         public void testNucdipole() {testConverter("nucdipole");}
	@Test         public void testSymminfo()  {testConverter("symminfo");}
// fail	@Test         public void testXyz()       {testConverter("xyz");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new NWChemLog2XMLConverter(), name);
	}
	
	   @Test
	   @Ignore
	   public void nwchemOut2XML() {
	      RegressionSuite.run("compchem/nwchem/log", "out", "xml",
	                          new NWChemLog2XMLConverter());
	   }

}
