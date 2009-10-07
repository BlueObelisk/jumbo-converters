package org.xmlcml.cml.converters.chemdraw;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.ConverterExercise;

public class CDXML2CMLConverterTest  {
	public final String getLocalDirName() {
		return "cdx/cdxml";
	}
	public final String getInputSuffix() {
		return "cdxml";
	}
	public final String getOutputSuffix() {
		return "cml";
	}
	
	@Test
	public void testConverter() throws IOException {
      ConverterExercise ce = new ConverterExercise();
      ce.setInputSuffix("cdxml");
      ce.setOutputSuffix("cml");
      ce.setLocalDirName("cdx/cdxml");
      ce.setConverterInstance(new CDXML2CMLConverter());
      ce.runBasicConverterTest();
	}


}
