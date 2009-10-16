package org.xmlcml.cml.converters.compchem.gaussian;

import org.junit.Test;
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
}
