package org.xmlcml.cml.converters.compchem.dummy.log;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.TemplateTester;

public class TemplateTest {
	
	private TemplateTester templateTester = new TemplateTester("dummy", "log", ".log", this.getClass());
	
	@Test
	public void testCommentExamples() {
		templateTester.runCommentExamples();
	}
	
	@Test	public void testIsotopeComment()       {templateTester.runTemplateCommentExamples("isotope");}
	
	@Test	public void testDummyLogFile()         {templateTester.runTestOnFile("dummy");}
	

}
