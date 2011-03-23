 package org.xmlcml.cml.converters.compchem.qespresso;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.qespresso.log.QuantumEspressoLog2XMLConverterOld;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   @Ignore
	   public void nwchemOut2XML() {
	      RegressionSuite.run("compchem/qespresso/log", "out", "xml",
	                          new QuantumEspressoLog2XMLConverterOld());
	                              
	   }
}
