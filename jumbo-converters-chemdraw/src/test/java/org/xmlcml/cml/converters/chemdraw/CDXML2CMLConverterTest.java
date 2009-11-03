package org.xmlcml.cml.converters.chemdraw;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CDXML2CMLConverterTest {

   @Test
   public void testConverter() throws IOException {
      RegressionSuite.build("cdx/cdxml", "cdxml", "cml",
                            new CDXML2CMLConverter()).run();
   }
}
