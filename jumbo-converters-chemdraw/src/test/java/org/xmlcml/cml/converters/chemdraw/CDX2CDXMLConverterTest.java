package org.xmlcml.cml.converters.chemdraw;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CDX2CDXMLConverterTest {

	@Test
	public void testDummy() {
		
	}
	
   @Test
   @Ignore
   public void testConvertToXMLElement() throws IOException {
      RegressionSuite.run("cdx/cdx", "cdx", "cdx.xml",
                            new CDX2CDXMLConverter());
   }
}
