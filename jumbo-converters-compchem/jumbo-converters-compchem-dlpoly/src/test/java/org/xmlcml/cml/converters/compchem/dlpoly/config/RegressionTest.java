 package org.xmlcml.cml.converters.compchem.dlpoly.config;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void config2CML() {
	      RegressionSuite.run("compchem/dlpoly/config", "config", "xml",
	                          new DLPolyConfig2CMLConverter());
	   }
}
