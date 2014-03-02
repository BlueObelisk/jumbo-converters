package org.xmlcml.cml.converters.format;

import org.junit.Assert;
import org.junit.Test;

public class SymbolTest {

	@Test
	public void testSymbol() {
		Symbol symbol = Symbol.createSymbol("abc");
		Assert.assertNotNull("abc", symbol);
	}
	
}
