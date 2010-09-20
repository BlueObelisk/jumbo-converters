package org.xmlcml.cml.converters.filter;

import org.junit.Assert;

import org.junit.Test;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.cml.element.CMLScalar;

public class AndFilterTest {
	@Test
	public void test1() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		AndFilter andFilter = new AndFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("123");
		Assert.assertTrue(andFilter.accept(scalar));
	}

	@Test
	public void test2() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		AndFilter andFilter = new AndFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("12345");
		Assert.assertFalse(andFilter.accept(scalar));
	}

	@Test
	public void test3() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		AndFilter andFilter = new AndFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("12");
		Assert.assertFalse(andFilter.accept(scalar));
	}


}
