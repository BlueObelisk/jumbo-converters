/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.spectrum.graphics.svg2cml;

import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {


   @Test
   public void svg2cmlspect() {
      RegressionSuite.run(SpectrumCommon.GRAPHICS_SVG2CML_DIR, "svg", "cml",
                            new SVG2CMLSpectConverter());
   }

}
