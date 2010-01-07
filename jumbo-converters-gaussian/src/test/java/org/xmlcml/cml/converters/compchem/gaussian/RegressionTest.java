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
      RegressionSuite.run("compchem/gaussian/gaussian", "gau", "cml",
                          new GaussianArchive2CMLConverter());
   }

   @Test
   public void gauCML2OWL() {
      RegressionSuite.run("compchem/gaussian/cml2owlrdf", "cml", "rdf",
                          new GaussianCML2OWLRDFConverter());
   }

   @Test
   @Ignore("Largely deprecated")
   public void gauLog2CML() {
      RegressionSuite.run("compchem/gaussian/gaussian/log", "g03", "cml",
                          new GaussianLog2CMLConverter());
   }

   @Test
   public void cmlFreq2gauIn() {
      RegressionSuite.run("compchem/gaussian/input/freq", "cml", "gau.in",
                          new CML2GaussianInputConverter());
   }

   @Test
   public void cmlNmr2gauIn() {
      RegressionSuite.run("compchem/gaussian/input/nmr", "cml", "gau.in",
                          new GaussianLog2CMLConverter());
   }
}
