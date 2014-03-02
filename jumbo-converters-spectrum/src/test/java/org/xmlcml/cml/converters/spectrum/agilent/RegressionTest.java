package org.xmlcml.cml.converters.spectrum.agilent;

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
   @Ignore // FAIL
   public void agilent2cml() {
      RegressionSuite.run(SpectrumCommon.AGILENT_DIR, "txt", "xml",
                            new AgilentLCMS2CMLConverter());
   }

}
