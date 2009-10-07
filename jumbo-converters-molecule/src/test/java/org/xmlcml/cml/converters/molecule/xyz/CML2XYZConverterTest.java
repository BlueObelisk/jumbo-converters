package org.xmlcml.cml.converters.molecule.xyz;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;

public class CML2XYZConverterTest extends AbstractConverterTestBase {

	public final String getLocalDirName() {
		return "molecule/xyz/cml2xyz";
	}
	public final String getInputSuffix() {
		return "cml";
	}
	public final String getOutputSuffix() {
		return "xyz";
	}
	
	@Test
	public void testConverter() throws IOException {
                               setQuiet(true); runBasicConverterTest();
	}

}
