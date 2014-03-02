package org.xmlcml.cml.converters.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.IntRange;


public class OrFilterTest {
	@Test
	public void test1() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		OrFilter orFilter = new OrFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("123");
		Assert.assertTrue(orFilter.accept(scalar));
	}

	@Test
	public void test2() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		OrFilter orFilter = new OrFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("12345");
		Assert.assertTrue(orFilter.accept(scalar));
	}

	@Test
	public void test3() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		OrFilter orFilter = new OrFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("12");
		Assert.assertTrue(orFilter.accept(scalar));
	}

	@Test
	public void test4() {
		StringLengthFilter lengthFilter24 = new StringLengthFilter(new IntRange(2,4));
		StringLengthFilter lengthFilter35 = new StringLengthFilter(new IntRange(3,5));
		OrFilter orFilter = new OrFilter(lengthFilter24, lengthFilter35);
		CMLScalar scalar = new CMLScalar("1");
		Assert.assertFalse(orFilter.accept(scalar));
	}

}
