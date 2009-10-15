package org.xmlcml.cml.converters.chemdraw;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class CDXML2CMLConverterTest  {
	@Test
	public void testConverter() throws IOException {
      RegressionSuite ce = new RegressionSuite();
      ce.setInputSuffix("cdxml");
      ce.setOutputSuffix("cml");
      ce.setLocalDirName("cdx/cdxml");
      ce.setConverter(new CDXML2CMLConverter());
      ce.run();
	}
}
