package org.xmlcml.cml.converters.epo;

import org.junit.Test;
import org.xmlcml.cml.converters.documents.epo.EPO2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class RegressionTest {

   @Test
   public void epoDocConverter() {
      RegressionSuite.build("documents/epo", "xml", "seg.xml",
                            new EPO2XMLConverter());
   }
}
