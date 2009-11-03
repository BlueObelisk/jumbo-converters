/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.molecule;

import org.junit.Test;
import org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter;
import org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter;
import org.xmlcml.cml.converters.molecule.pubchem.PubchemXML2CMLConverter;
import org.xmlcml.cml.converters.molecule.pubchem.sdf.CML2PubchemSDFConverter;
import org.xmlcml.cml.converters.molecule.xyz.CML2XYZConverter;
import org.xmlcml.cml.converters.molecule.xyz.XYZ2CMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cml2mdl() {
      RegressionSuite.build("molecule/mdl/cml2mdl", "cml", "mol",
                            new CML2MDLConverter()).run();
   }

   @Test
   public void mdl2cml() {
      RegressionSuite.build("molecule/mdl/mdl2cml", "mol", "cml",
                            new MDL2CMLConverter()).run();
   }

   @Test
   public void xyz2cml() {
      RegressionSuite.build("molecule/xyz/xyz2cml", "xyz", "cml",
                            new XYZ2CMLConverter()).run();
   }

   @Test
   public void cml2xyz() {
      RegressionSuite.build("molecule/xyz/cml2xyz", "cml", "xyz",
                            new CML2XYZConverter()).run();
   }

   @Test
   public void cml2pubchem() {
      RegressionSuite.build("molecule/pubchem/sdf", "cml.xml", "sdf",
                            new CML2PubchemSDFConverter()).run();
   }

   @Test
   public void pubchemxml2cml() {
      RegressionSuite.build("molecule/pubchem", "xml", "cml",
                            new PubchemXML2CMLConverter("molecule/pubchem/config/config.xml")).run();
   }
}
