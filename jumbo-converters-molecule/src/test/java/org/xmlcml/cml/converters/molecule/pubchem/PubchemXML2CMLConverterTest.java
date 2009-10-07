package org.xmlcml.cml.converters.molecule.pubchem;

import java.io.IOException;

import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;

public class PubchemXML2CMLConverterTest extends AbstractConverterTestBase {

	public final String getLocalDirName() {
		return "molecule/pubchem";
	}
	public final String getInputSuffix() {
		return "xml";
	}
	public final String getOutputSuffix() {
		return "cml";
	}
	public final String getAuxiliaryFileName() {
		return "src/test/resources/molecule/pubchem/config/config.xml";
	}

	@Test
	public void testConverter() throws IOException {
                               setQuiet(true); runBasicConverterTest();
	}

}
