package org.xmlcml.cml.converters.compchem.nwchem.log;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
	@Test         public void testAcknow()    {testConverter("acknow");}
	@Test         public void testAng()       {testConverter("ang");}
	@Test         public void testArgument()  {testConverter("argument");}
	@Test         public void testAtmass()    {testConverter("atmass");}
	@Test         public void testAtombasis() {testConverter("atombasis");}
	@Test         public void testAuthors()   {testConverter("authors");}
	@Test         public void testBasis()     {testConverter("basis");}
	@Test         public void testCenter23()  {testConverter("center23");}
	@Test         public void testCentermass(){testConverter("centermass");}
	@Test         public void testCitation()  {testConverter("citation");}
	@Test         public void testConverge()  {testConverter("converge");}
	@Test         public void testDirinfo()   {testConverter("dirinfo");}
	@Test         public void testDft()       {testConverter("dft");}
	@Test         public void testDftvector() {testConverter("dftvector");}
	@Test         public void testGastats()   {testConverter("gastats");}
	@Test         public void testGeninfo()   {testConverter("geninfo");}
	@Test         public void testGeom()      {testConverter("geom");}
	@Test         public void testGrid()      {testConverter("grid");}
    @Test         public void testJob()       {testConverter("job");}
	@Test         public void testMemory()    {testConverter("memory");}
	@Test         public void testMomint()    {testConverter("momint");}
	@Test         public void testMultipole() {testConverter("multipole");}
	@Test         public void testNccp()      {testConverter("nccp");}
	@Test         public void testNonvar()    {testConverter("nonvar");}
	@Test         public void testNucdipole() {testConverter("nucdipole");}
	@Test         public void testNwcheminp() {testConverter("nwcheminp");}
	@Test         public void testScreen()    {testConverter("screen");}
	@Test         public void testSummary()  {testConverter("summary");}
	@Test         public void testSuperpos()  {testConverter("superpos");}
	@Test         public void testSymminfo()  {testConverter("symminfo");}
	@Test         public void testSymmolorb() {testConverter("symmolorb");}
	@Test         public void testXc()        {testConverter("xc");}
	@Test         public void testXyz()       {testConverter("xyz");}
	@Test         public void testZmat()      {testConverter("zmat");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new NWChemLog2XMLConverter(), name);
	}
	
	   @Test
	   public void nwchemOut2XML() {
	      RegressionSuite.run("compchem/nwchem/log", "out", "xml",
	                          new NWChemLog2XMLConverter());
	   }

}
