package org.xmlcml.cml.converters.cif.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;

public class DisorderFilterTest {

	@Test
	public void test() {
		DisorderFilter disorderFilter = new DisorderFilter();
		CMLElement element = CrystalFixture.OK_CML;
		Assert.assertFalse(disorderFilter.accept(element));
	}
	
}
