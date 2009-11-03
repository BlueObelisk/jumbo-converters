/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.spectrum;

import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.jdx.CMLSpect2JDXConverter;
import org.xmlcml.cml.converters.spectrum.jdx.JDX2CMLConverter;
import org.xmlcml.cml.converters.spectrum.svg.CMLSpect2SVGConverter;
import org.xmlcml.cml.converters.spectrum.svg.SVG2CMLSpectConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cml2jdx() {
      RegressionSuite.build("spectrum/cml2jdx", "cml", "jdx",
                            new CMLSpect2JDXConverter()).run();
   }

   @Test
   public void jdx2cml() {
      RegressionSuite.build("spectrum/jdx2cml", "jdx", "cml",
                            new JDX2CMLConverter()).run();
   }

   @Test
   public void cmlspect2SVG() {
      RegressionSuite.build("graphics/cmlspect2svg", "cml", "svg",
                            new CMLSpect2SVGConverter()).run();
   }

   @Test
   public void svg2cmlspect() {
      RegressionSuite.build("graphics/svg2cmlspect", "svg", "cml",
                            new SVG2CMLSpectConverter()).run();
   }
}
