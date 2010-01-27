/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.spectrum.oscar;

import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.jdx.JDX2CMLConverter;
import org.xmlcml.cml.converters.spectrum.oscar.OSCAR2CMLSpectConverter;
import org.xmlcml.cml.converters.spectrum.svg.CMLSpect2SVGConverter;
import org.xmlcml.cml.converters.spectrum.svg.SVG2CMLSpectConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void oscar2cml() {
      RegressionSuite.run("spectrum/oscar2cml", "xml", "cml",
                            new OSCAR2CMLSpectConverter());
   }

}
