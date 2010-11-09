 package org.xmlcml.cml.converters.compchem.nwchem;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void nwchemPunch2XML() {
	      RegressionSuite.run("compchem/nwchem", "out", "xml",
	                          new NWChem2XMLConverter());
	                              
	   }
}
