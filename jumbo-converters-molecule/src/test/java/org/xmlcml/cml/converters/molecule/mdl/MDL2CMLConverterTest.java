package org.xmlcml.cml.converters.molecule.mdl;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;

public class MDL2CMLConverterTest extends AbstractConverterTestBase {

	public final String getLocalDirName() {
		return "molecule/mdl/mdl2cml";
	}
	public final String getInputSuffix() {
		return "mol";
	}
	public final String getOutputSuffix() {
		return "cml";
	}
	
	@Test
	public void testConverter() throws IOException {
                               setQuiet(true); runBasicConverterTest();
	}

}
