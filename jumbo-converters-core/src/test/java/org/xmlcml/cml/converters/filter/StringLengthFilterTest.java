package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.junit.Assert;
import org.junit.Test;

import org.xmlcml.euclid.IntRange;


public class StringLengthFilterTest {
	@Test
	public void test1() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		StringLengthFilter filter = new StringLengthFilter(3);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void test1a() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		StringLengthFilter filter = new StringLengthFilter(4);
		Assert.assertFalse(filter.accept(element));
	}

	@Test
	public void test2() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		StringLengthFilter filter = new StringLengthFilter(new IntRange(2, 4));
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void test2a() {
		CMLElement element = CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xmlcml.org/schema'>YES</scalar>");
		StringLengthFilter filter = new StringLengthFilter(new IntRange(0, 2));
		Assert.assertFalse(filter.accept(element));
	}


}
