 package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
@Ignore // till we have templates
public class RegressionTest {

	
	   /** note: writes to target\test\compchem\gamessus\log\out\test1.xml
	    * 
	    */
	   @Test
	   public void gamessusLog2XML() {
//	      RegressionSuite.run("compchem/gamessus/log", "log", "xml",
//	                          new GamessUSLog2XMLConverter());
	   }

	   /** note: writes to target\test\compchem\gamessus\log\out\test1.xml
	    * 
	    */
	   @Test
	   @Ignore
	   public void gamessusXML2CML() {
	      RegressionSuite.run("compchem/gamessus/log", "xml", "cml",
	                          new GamessUSXLogXML2CMLConverter());
	   }
}
