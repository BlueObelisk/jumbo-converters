/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.cif;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cif2cifxml() {
      RegressionSuite.build("cif/cif", "cif", "cif.xml",
                            new CIF2CIFXMLConverter()).run();
   }

   @Test
   public void cifxml2cml() {
      RegressionSuite.build("cif/cifxml", "xml", "cml",
                            new CIFXML2CMLConverter()).run();
   }
}
