package org.xmlcml.cml.converters.graphics.svg;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CML2SVGConverterTest {

   @Test
   public void testConverter() {
      RegressionSuite.build("graphics/cml2svg", "cml", "svg",
                            new CML2SVGConverter()).run();
   }

   @Test
   @Ignore("Test output for mol.cml is nothing like the ref - large problem")
   public void test2CMLConverter() {
      RegressionSuite.build("graphics/svg2cml", "svg", "cml",
                            new SVG2CMLConverter()).run();
   }
}
