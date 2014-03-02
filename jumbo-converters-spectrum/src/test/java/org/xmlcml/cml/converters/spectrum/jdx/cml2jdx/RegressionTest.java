/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.spectrum.jdx.cml2jdx;

import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cml2jdx() {
      RegressionSuite.run(SpectrumCommon.JDX_CML2JDX_DIR, "cml", "jdx",
                            new CMLSpect2JDXConverter());
   }


}
