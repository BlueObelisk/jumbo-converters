package org.xmlcml.cml.converters.compchem.nwchem.log;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;

public class ConverterTest {
	@Test
	@Ignore
	public void testJob() {
		testConverter("job");
	}
	
	@Test
	@Ignore
	public void testMoi() {
		testConverter("moi");
	}

	@Test
	public void testNucdipole() {
		testConverter("nucdipole");
	}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new NWChemLog2XMLConverter(), name);
	}

}
