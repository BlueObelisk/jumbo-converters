package org.xmlcml.cml.converters.molecule.pubchem.sdf;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;

public class CML2PubchemSDFConverterTest extends AbstractConverterTestBase {

	public final String getLocalDirName() {
		return "molecule/pubchem/sdf";
	}
	public final String getInputSuffix() {
		return "cml.xml";
	}
	public final String getOutputSuffix() {
		return "sdf";
	}
	public final String getAuxiliaryFileName() {
		return "src/test/resources/molecule/pubchem/config/config.xml";
	}

	@Test
	public void testConverter() throws IOException {
                               setQuiet(true); runBasicConverterTest();
	}

}
