/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.cif;

import org.junit.Ignore;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
//   @Ignore("Ignorable whitespace in mixed content causing problems agains")
   public void cif2cifxml() {
      RegressionSuite.run("cif/cif", "cif", "cif.xml",
                            new CIF2CIFXMLConverter());
   }

   @Test
   @Ignore("Ignorable whitespace in mixed content causing problems agains")
   public void cifxml2cml() {
      RegressionSuite.run("cif/cifxml", "xml", "cml",
                            new CIFXML2CMLConverter());
   }
   
   //@Ignore
   @Test
   public void rawcml2cml() {
      RegressionSuite.run("cif/rawcml", "cif.cml", "comp.cml",
                            new RawCML2CompleteCMLConverter());
   }

}
