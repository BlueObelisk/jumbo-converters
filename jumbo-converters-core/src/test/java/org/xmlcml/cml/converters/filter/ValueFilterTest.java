package org.xmlcml.cml.converters.filter;

import org.junit.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;


public class ValueFilterTest {
	@Test
	public void test1() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		ValueFilter valueFilter = new ValueFilter("YES");
		Assert.assertTrue(valueFilter.accept(element));
	}

	@Test
	public void test2() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		ValueFilter valueFilter = new ValueFilter("NO");
		Assert.assertFalse(valueFilter.accept(element));
	}


}
