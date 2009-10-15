package org.xmlcml.cml.converters.chemdraw;


import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CDX2CDXMLConverterTest {	
	@Test
	public void testConvertToXMLElement() throws IOException {
		CDX2CDXMLConverter c = new CDX2CDXMLConverter();
		RegressionSuite r = new RegressionSuite();
      r.setConverter(c);
      r.setLocalDirName("cdx/cdx");
      r.setInputSuffix("cdx");
      r.setOutputSuffix("cdx.xml");
      r.run();
	}
}
