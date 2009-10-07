package org.xmlcml.cml.converters.molecule.xyz;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.converters.AbstractConverterTestBase;
public class XYZ2CMLConverterTest extends AbstractConverterTestBase {
	private static Logger LOG = Logger.getLogger(XYZ2CMLConverterTest.class);
	public final String getLocalDirName() {
		return "molecule/xyz/xyz2cml";
	}
	public final String getInputSuffix() {
		return "xyz";
	}
	public final String getOutputSuffix() {
		return "cml";
	}
	
	@Test
	public void testConverter() throws IOException {
		LOG.debug("TESTXYZ");
                               setQuiet(true); runBasicConverterTest();
	}

	
}
