package org.xmlcml.cml.converters.filter;

import org.junit.Assert;

import org.junit.Test;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.cml.element.CMLScalar;


public class NotFilterTest {
	@Test
	public void test1() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		NotFilter notFilter = new NotFilter(lengthFilter24);
		CMLScalar scalar = new CMLScalar("123");
		Assert.assertFalse(notFilter.accept(scalar));
	}

	@Test
	public void test2() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		NotFilter notFilter = new NotFilter(lengthFilter24);
		CMLScalar scalar = new CMLScalar("1");
		Assert.assertTrue(notFilter.accept(scalar));
	}

	@Test
	public void test3() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter57 = new StringLengthFilter(new IntRange(5,7));
		NotFilter notFilter24 = new NotFilter(lengthFilter24);
		NotFilter notFilter57 = new NotFilter(lengthFilter57);
		AndFilter andFilter = new AndFilter(notFilter24, notFilter57);
		CMLScalar scalar = new CMLScalar("12");
		Assert.assertFalse(andFilter.accept(scalar));
	}

	@Test
	public void test3a() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter57 = new StringLengthFilter(new IntRange(5,7));
		NotFilter notFilter24 = new NotFilter(lengthFilter24);
		NotFilter notFilter57 = new NotFilter(lengthFilter57);
		AndFilter andFilter = new AndFilter(notFilter24, notFilter57);
		CMLScalar scalar = new CMLScalar("1");
		Assert.assertTrue(andFilter.accept(scalar));
	}

}
