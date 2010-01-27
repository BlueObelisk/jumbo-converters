package org.xmlcml.cml.converters.graphics.svg;

import org.junit.Ignore;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CML2SVGConverterTest {

   @Test
   public void testConverter() {
      RegressionSuite.run("graphics/cml2svg", "cml", "svg",
                          new CML2SVGConverter());
   }

   @Test
   @Ignore("Test output for mol.cml is nothing like the ref - large problem")
   public void test2CMLConverter() {
      RegressionSuite.run("graphics/svg2cml", "svg", "cml",
                          new SVG2CMLConverter());
   }
}
