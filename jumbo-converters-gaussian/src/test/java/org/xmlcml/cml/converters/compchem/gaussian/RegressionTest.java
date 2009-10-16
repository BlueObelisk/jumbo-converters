package org.xmlcml.cml.converters.compchem.gaussian;

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
   public void gauIn2CML() {
      RegressionSuite.build("compchem/gaussian/in", "cml", "gau.in",
                            new CML2GaussianInputConverter());
   }

   @Test
   public void gauLog2CML() {
      RegressionSuite.build("compchem/gaussian/gaussian/log", "g03", "cml",
                            new GaussianLog2CMLConverter());
   }

   @Test
   public void cmlFreq2gauIn() {
      RegressionSuite.build("compchem/gaussian/input/freq", "cml", "gau.in",
                            new CML2GaussianInputConverter());
   }

   @Test
   public void cmlNmr2gauIn() {
      RegressionSuite.build("compchem/gaussian/input/nmr", "cml", "gau.in",
                            new GaussianLog2CMLConverter());
   }
}
