package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateTest {
	private static final String CODE_BASE = "gaussian";
	private static final String FILE_TYPE = "log";
	
	private static final String LOG_DIR = "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEST_DIR = "src/test/resources/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEMPLATE_DIR = LOG_DIR+"templates/pmr/";
//	private static final String TEMPLATE_DIR = LOG_DIR;
	static String INPUT_TEMPLATE_S = LOG_DIR+"topTemplate.xml";
//	static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
	static String BASE_URI = "classpath:/"+LOG_DIR;
	static Class TEMPLATE_TEST_CLASS = org.xmlcml.cml.converters.compchem.gaussian.log.TemplateTest.class;
	
	@Test
	@Ignore
	public void templateTester() {
		TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
	}

	@Test
	@Ignore
	public void testLog() throws IOException {
		runTest("anna1");
	}

	private void runTest(String test) throws FileNotFoundException, IOException {
		String inputName = TEST_DIR+"in/"+test+".log";
		InputStream inputStream = new FileInputStream(inputName);
		InputStream refStream = new FileInputStream(TEST_DIR+"ref/"+test+".xml");
		OutputStream outputStream = new FileOutputStream(TEST_DIR+"out/"+test+".xml");
		TemplateTestUtils.testDocument(inputStream, refStream, outputStream, 
				INPUT_TEMPLATE_S, BASE_URI, true);
	}

	@Test	public void testAnisospin()              {runTemplateTest("anisospin");}

	private void runTemplateTest(String templateName) {
		Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
		TemplateTestUtils.runCommentExamples(template);
	}


}
