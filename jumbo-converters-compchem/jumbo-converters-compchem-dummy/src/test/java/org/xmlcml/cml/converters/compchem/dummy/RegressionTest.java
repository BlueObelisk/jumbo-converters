 package org.xmlcml.cml.converters.compchem.dummy;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.dummy.log.DummyLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.dummy.mol.DummyMol2CMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   @Ignore
	   public void dummyOut2XML() {
	      RegressionSuite.run("compchem/dummy/log", "out", "xml",
	                          new DummyLog2XMLConverter());

	   }

	   @Test
	   @Ignore
	   public void dummyMol2XML() {
	      RegressionSuite.run("compchem/dummy/mol", "out", "xml",
	                          new DummyMol2CMLConverter());

	   }
}
