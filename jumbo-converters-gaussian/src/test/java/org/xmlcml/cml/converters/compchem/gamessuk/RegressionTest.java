package org.xmlcml.cml.converters.compchem.gamessuk;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKPunch2CMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

   @Test
   public void gamessuk2CML() {
      RegressionSuite.run("compchem/gamessuk", "pun", "cml",
                          new GamessUKPunch2CMLConverter());
                              
   }
}
