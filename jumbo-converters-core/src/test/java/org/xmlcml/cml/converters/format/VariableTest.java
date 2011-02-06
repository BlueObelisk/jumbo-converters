package org.xmlcml.cml.converters.format;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class VariableTest {

	@Test
	public void testVariableString() {
		Variable variable = Variable.createVariable("abc12");
		Assert.assertNotNull("abc12", variable);
		Symbol symbol = variable.getSymbol();
		Assert.assertNotNull("symbol", symbol);
		String name = symbol.getName();
		Assert.assertEquals("name", "abc12", name);
	}
	
	@Test
	public void testVariableString2() {
		Variable variable = Variable.createVariable("3abc12");
		Assert.assertNull("3abc12", variable);
	}
	
	@Test
	public void testVariableDouble() {
		Variable variable = Variable.createVariable("-2.1");
		Assert.assertNotNull("number", variable);
		Double d = variable.getDouble();
	}
	
}
