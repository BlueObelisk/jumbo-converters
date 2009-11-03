package org.xmlcml.cml.converters.compchem.gaussian;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gaussian.input.CML2GaussianInputConverter;
import org.xmlcml.cml.converters.compchem.gaussian.log.GaussianLog2CMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void gaussianArchive2CML() {
      RegressionSuite.build("compchem/gaussian/gaussian", "gau", "cml",
                            new GaussianArchive2CMLConverter()).run();
   }

   @Test
   public void gauCML2OWL() {
      RegressionSuite.build("compchem/gaussian/cml2owlrdf", "cml", "rdf",
                            new GaussianCML2OWLRDFConverter()).run();
   }

   @Test
   @Ignore("Largely deprecated")
   public void gauLog2CML() {
      RegressionSuite.build("compchem/gaussian/gaussian/log", "g03", "cml",
                            new GaussianLog2CMLConverter()).run();
   }

   @Test
   public void cmlFreq2gauIn() {
      RegressionSuite.build("compchem/gaussian/input/freq", "cml", "gau.in",
                            new CML2GaussianInputConverter()).run();
   }

   @Test
   public void cmlNmr2gauIn() {
      RegressionSuite.build("compchem/gaussian/input/nmr", "cml", "gau.in",
                            new GaussianLog2CMLConverter()).run();
   }
}
