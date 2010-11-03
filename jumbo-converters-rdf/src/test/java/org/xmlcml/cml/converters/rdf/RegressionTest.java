package org.xmlcml.cml.converters.rdf;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.rdf.cml.CML2N3Converter;
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
   @Ignore
   public void cml2owlrdf() {
      RegressionSuite.run("rdf/cml2owlrdf", "cml", "rdf",
                          new CML2OWLRDFConverter());
   }

   @Test
   public void cml2rdf() {
      RegressionSuite.run("rdf/cml2rdf", "cml", "rdf", new CML2RDFConverter());
   }

   @Test
   @Ignore
   public void cml2owl() {
      RegressionSuite.run("rdf/cml2owl", "xml", "owl", new CML2OWLConverter());
   }
   
   @Test
   public void cml2n3() {
      RegressionSuite.run("rdf/cml2n3", "cml", "n3", new CML2N3Converter());
   }

 
}
