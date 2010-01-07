package org.xmlcml.cml.converters.rdf;

import org.junit.Test;
import org.xmlcml.cml.converters.rdf.cml.CML2OWLRDFConverter;
import org.xmlcml.cml.converters.rdf.cml.CML2RDFConverter;
import org.xmlcml.cml.converters.rdf.owl.CML2OWLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cml2owlrdf() {
      RegressionSuite.run("rdf/cml2owlrdf", "cml", "rdf",
                          new CML2OWLRDFConverter());
   }

   @Test
   public void cml2rdf() {
      RegressionSuite.run("rdf/cml2rdf", "cml", "rdf", new CML2RDFConverter());
   }

   @Test
   public void cml2owl() {
      RegressionSuite.run("rdf/cml2owl", "xml", "owl", new CML2OWLConverter());
   }
}
