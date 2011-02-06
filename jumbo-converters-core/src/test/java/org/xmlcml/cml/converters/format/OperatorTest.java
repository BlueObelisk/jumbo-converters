package org.xmlcml.cml.converters.format;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class OperatorTest {
	
	@Test
	public void testOperator() {
		Assert.assertTrue("and", Operator.isOperator(Operator.AND));
	}
	
}
