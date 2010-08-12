/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.cif;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	@Test
	public void dummyWhenNoOtherTests() {
		Assert.assertTrue(
				"Doubt thou the stars are fire, " +
				"Doubt that the sun doth move, " +
				"Doubt truth to be a liar," +
				"But never doubt I love. ", true);
	}
	
   @Test
   @Ignore("Ignorable whitespace in mixed content causing problems agains")
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


}
