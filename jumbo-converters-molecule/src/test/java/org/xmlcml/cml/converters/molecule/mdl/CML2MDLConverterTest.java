package org.xmlcml.cml.converters.molecule.mdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;

public class CML2MDLConverterTest extends AbstractConverterTestBase {

	public final String getLocalDirName() {
		return "molecule/mdl/cml2mdl";
	}
	public final String getInputSuffix() {
		return "cml";
	}
	public final String getOutputSuffix() {
		return "mol";
	}
	
	@Test
	public void testConverter() throws IOException {
		setQuiet(true);
		runBasicConverterTest();
	}

	
}
