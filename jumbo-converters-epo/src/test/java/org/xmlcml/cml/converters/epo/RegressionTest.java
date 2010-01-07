package org.xmlcml.cml.converters.epo;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.documents.epo.EPO2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class RegressionTest {

   @Test
   @Ignore("The handling needs to be more sophisticated here - there's ignorable" +
   " whitespace in mixed content in the output that the comparisons aren't " +
   "taking account of")
   public void epoDocConverter() {
      RegressionSuite.run("documents/epo", "xml", "seg.xml",
                          new EPO2XMLConverter());
   }
}
