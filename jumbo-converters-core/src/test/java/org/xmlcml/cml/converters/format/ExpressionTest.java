package org.xmlcml.cml.converters.format;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class ExpressionTest {

	@Test
	public void testParseInfix1() {
		Expression exp = new Expression();
		String s = "Aq+b/c";
		String[] array = exp.parseTokens(s).toArray(new String[0]);
		JumboTestUtils.assertEquals("parse", "Aq + b / c".split(" "), array);
	}
	
	@Test
	@Ignore
	public void testParseInfix2() {
		Expression exp = new Expression();
		String s = "Aq+(b+c)/2.0";
		String[] array = exp.parseTokens(s).toArray(new String[0]);
		JumboTestUtils.assertEquals("parse", "Aq + ( b + c ) / 2.0".split(" "), array);
	}
	
	@Test
	@Ignore
	public void testParseInfix3() {
		Expression exp = new Expression();
		String s = "Aq+sin(b+c)/2.0";
		String[] array = exp.parseTokens(s).toArray(new String[0]);
		JumboTestUtils.assertEquals("parse", "Aq + sin ( b + c ) / 2.0".split(" "), array);
	}
	
	@Test
	@Ignore
	public void testParseInfix4() {
		Expression exp = new Expression();
		String s = "Aq+sin(b+c*d)/2.0 > z && q < z";
		String[] array = exp.parseTokens(s).toArray(new String[0]);
		JumboTestUtils.assertEquals("parse", "Aq + sin ( b + c * d ) / 2.0 > z && q < z".split(" "), array);
	}
}
