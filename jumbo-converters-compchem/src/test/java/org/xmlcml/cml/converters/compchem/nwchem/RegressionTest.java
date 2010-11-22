 package org.xmlcml.cml.converters.compchem.nwchem;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.nwchem.log.NWChemLog2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   @Ignore
	   public void nwchemOut2XML() {
	      RegressionSuite.run("compchem/nwchem/log", "out", "xml",
	                          new NWChemLog2XMLConverter());
	                              
	   }
}
