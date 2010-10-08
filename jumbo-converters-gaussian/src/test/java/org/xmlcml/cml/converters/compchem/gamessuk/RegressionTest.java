 package org.xmlcml.cml.converters.compchem.gamessuk;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKPunch2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void gamessuk2XML() {
	      RegressionSuite.run("compchem/gamessuk/punch", "pun", "xml",
	                          new GamessUKPunch2XMLConverter());
	                              
	   }
	   @Test
	   public void gamessukXML2CML() {
	      RegressionSuite.run("compchem/gamessuk/complete", "xml", "cml",
	                          new GamessUKXML2CMLConverter());
	                              
	   }
}
