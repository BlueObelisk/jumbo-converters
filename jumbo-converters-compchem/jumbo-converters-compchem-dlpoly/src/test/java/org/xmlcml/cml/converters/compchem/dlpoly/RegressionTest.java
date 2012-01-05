 package org.xmlcml.cml.converters.compchem.dlpoly;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.dlpoly.log.DLPolyLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.dlpoly.mol.DLPolyMol2CMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   @Ignore
	   public void dlpolyOut2XML() {
	      RegressionSuite.run("compchem/dlpoly/log", "out", "xml",
	                          new DLPolyLog2XMLConverter());

	   }

	   @Test
	   @Ignore
	   public void dlpolyMol2XML() {
	      RegressionSuite.run("compchem/dlpoly/mol", "out", "xml",
	                          new DLPolyMol2CMLConverter());

	   }
}
