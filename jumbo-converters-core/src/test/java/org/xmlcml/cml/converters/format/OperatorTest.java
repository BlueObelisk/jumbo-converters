package org.xmlcml.cml.converters.format;

import org.junit.Assert;
import org.junit.Test;

public class OperatorTest {
	
	@Test
	public void testOperator() {
		Assert.assertTrue("and", Operator.isOperator(Operator.AND));
	}
	
}
