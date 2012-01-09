package org.xmlcml.cml.converters.compchem.dlpoly.log;

import org.junit.Test;
import org.xmlcml.cml.converters.testutils.TemplateTester;

public class TemplateTest {
	
	private TemplateTester templateTester = new TemplateTester("dlpoly", "log", ".log", this.getClass());
	
	@Test
	public void testCommentExamples() {
		templateTester.runCommentExamples();
	}
	
	@Test	public void testDiffusionComment()    {templateTester.runTemplateCommentExamples("diffusion");}
	
	@Test	public void testWalkerFile()           {templateTester.runTestOnFile("walker");}
	

}
