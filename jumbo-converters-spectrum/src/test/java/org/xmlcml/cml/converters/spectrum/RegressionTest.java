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
      RegressionSuite.run("spectrum/cml2jdx", "cml", "jdx",
                            new CMLSpect2JDXConverter());
   }

   @Test
   public void jdx2cml() {
      RegressionSuite.run("spectrum/jdx2cml", "jdx", "cml",
                            new JDX2CMLConverter());
   }

   @Test
   public void cmlspect2SVG() {
      RegressionSuite.run("graphics/cmlspect2svg", "cml", "svg",
                            new CMLSpect2SVGConverter());
   }

   @Test
   public void svg2cmlspect() {
      RegressionSuite.run("graphics/svg2cmlspect", "svg", "cml",
                            new SVG2CMLSpectConverter());
   }
}
