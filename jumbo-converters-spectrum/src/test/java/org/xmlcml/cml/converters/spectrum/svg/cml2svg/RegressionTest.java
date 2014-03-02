package org.xmlcml.cml.converters.spectrum.svg.cml2svg;

import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void cml2svg() {
      RegressionSuite.run(SpectrumCommon.SVG_CML2SVG_DIR, "cml", "svg",
                            new CMLSpect2SVGConverter());
   }

}
