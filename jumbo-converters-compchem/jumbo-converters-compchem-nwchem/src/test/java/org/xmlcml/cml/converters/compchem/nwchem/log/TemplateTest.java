package org.xmlcml.cml.converters.compchem.nwchem.log;

import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateTest {
	private static final String CODE_BASE = "nwchem";
	private static final String FILE_TYPE = "log";
	private static final String BASE_DIR = 
		"org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEMPLATE_DIR = 
		BASE_DIR+"templates/";
	static String INPUT_TEMPLATE_S = 
		"org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/topTemplate.xml";
	static String BASE_URI = "classpath:/"+BASE_DIR;
	
	@Test
	public void dummy() {
		
	}
	
	@Test
	@Ignore
	public void templateTesterEXAMPLE() {
		TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
	}

	@Test	@Ignore public void testAtmassEXAMPLE()              {runTemplateTest("atmass");}
	@Test	@Ignore public void testBrillouinzpEXAMPLE()         {runTemplateTest("brillouinzp");}
	@Test	@Ignore public void testCenterOfChargeEXAMPLE()      {runTemplateTest("centerofcharge");}

	private void runTemplateTest(String templateName) {
		Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
		TemplateTestUtils.runCommentExamples(template);
	}

}
