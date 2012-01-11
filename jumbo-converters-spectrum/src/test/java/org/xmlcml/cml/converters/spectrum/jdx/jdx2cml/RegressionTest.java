/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.spectrum.jdx.jdx2cml;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   @Ignore
   public void jdx2cml() {
      RegressionSuite.run(SpectrumCommon.JDX_JDX2CML_DIR, "jdx", "cml",
                            new JDX2CMLConverter());
   }

}
