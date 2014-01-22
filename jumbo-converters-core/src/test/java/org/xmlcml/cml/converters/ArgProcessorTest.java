package org.xmlcml.cml.converters;

import junit.framework.Assert;

import org.junit.Test;

public class ArgProcessorTest {

	@Test
	public void testArgs0() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{""});
		} catch (Exception e) {
			Assert.assertEquals("no args", "Input file does not exist: ", e.getMessage());
		}
	}
	
	@Test
	public void testArgsIO() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"-i", "a",  "-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args i o", "Input file does not exist: a", e.getMessage());
		}
	}
	
	@Test
	public void testUnlabelledArgs() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"x", "y",  "-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args i o", "Input file does not exist: x", e.getMessage());
		}
	}
	
	@Test
	public void testStdin() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args stdin", "Reading from STDIN not yet implemented", e.getMessage());
		}
	}
}
