package org.xmlcml.cml.converters.pdf;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;


public class PDF2SVGConverterTest  {
		@Test
		public void testConverter() throws IOException {
			RegressionSuite.build("pdf/pdf2svg", "pdf", "svg", new PDF2SVGConverter());
		}

}
