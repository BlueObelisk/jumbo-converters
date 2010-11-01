 package org.xmlcml.cml.converters.compchem.gamessus;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void gamessusPunch2XML() {
	      RegressionSuite.run("compchem/gamessus/punch", "dat", "xml",
	                          new GamessUSPunch2XMLConverter());
	                              
	   }
	   @Test
	   public void gamessusPunchXML2CML() {
	      RegressionSuite.run("compchem/gamessus/punch", "xml", "cml",
	                          new GamessUSPunchXML2CMLConverter());
	                              
	   }
	   @Test
	   public void gamessusInput2XML() {
	      RegressionSuite.run("compchem/gamessus/inp", "inp", "xml",
	                          new GamessUSInput2XMLConverter());
	                              
	   }
	   @Test
	   public void gamessusLog2XML() {
	      RegressionSuite.run("compchem/gamessus/log", "log", "xml",
	                          new GamessUSLog2XMLConverter());
	                              
	   }
}
