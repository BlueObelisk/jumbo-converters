package org.xmlcml.cml.converters.compchem.gaussian;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gaussian.input.CML2GaussianInputConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {


//   @Test
//   @Ignore("Largely deprecated")
//   public void gauLog2CML() {
//      RegressionSuite.run("compchem/gaussian/gaussian/log", "g03", "cml",
//                          new GaussianLog2CMLConverter());
//   }

   @Test
   @Ignore
   public void cmlFreq2gauIn() {
      RegressionSuite.run("compchem/gaussian/input/freq", "cml", "gau.in",
                          new CML2GaussianInputConverter());
   }

//   @Test
//   @Ignore // I think this uses old marker code
//   public void cmlNmr2gauIn() {
//      RegressionSuite.run("compchem/gaussian/input/nmr", "cml", "gau.in",
//                          new GaussianLog2CMLConverter());
//   }
   
   @Test
   @Ignore // not sure which version this runs
   public void log2XML() {
//      RegressionSuite.run("compchem/gaussian/log", "log", "xml",
//                          new GaussianLog2XMLConverter());
                              
   }

}
