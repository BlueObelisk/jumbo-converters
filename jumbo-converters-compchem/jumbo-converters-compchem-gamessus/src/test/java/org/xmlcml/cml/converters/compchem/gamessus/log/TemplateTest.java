package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.TemplateTestUtils;

public class TemplateTest {
	private static final String CODE_BASE = "gamessus";
	private static final String FILE_TYPE = "log";

	private static final String LOG_DIR = "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEST_DIR = "src/test/resources/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEMPLATE_DIR = LOG_DIR+"templates/";
	static String INPUT_TEMPLATE_S = LOG_DIR+"topTemplate.xml";
	static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
	static Class<?> TEMPLATE_TEST_CLASS = org.xmlcml.cml.converters.compchem.gamessus.log.TemplateTest.class;

	@Test
	@Ignore
	public void templateTester() {
		TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
	}


	////////////  //////////// ADD TEMPLATE TESTS HERE: ////////////  ////////////
	@Ignore	@Test	public void testl0Entering()                 {runTemplateTest("l0.entering");}
			@Test	public void testCoordsTemp()                 {runTemplateTest("coordinates.angstom");}
			@Test	public void testEnvironScript()             {runTemplateTest("environ.01.script");}
			@Test	public void testEnvironProgInfoScript()             {runTemplateTest("environ.02.proginfo");}
			@Test	public void testDipoleMoment()             {runTemplateTest("dipole.moment");}
			@Test	public void testEnergyComponents()             {runTemplateTest("energy.components");}








	////////////  //////////// END OF TEMPLATE TESTS ////////////  ////////////

	// test functions:
	private void runTest(String test) throws FileNotFoundException, IOException {
		String inputName = TEST_DIR+"in/"+test+".log";
		String refName = TEST_DIR+"ref/"+test+".xml";
		String outName = TEST_DIR+"out/"+test+".xml";
		runTest(inputName, refName, outName);
	}

	private void runTest(String inputName, String refName, String outName)
			throws FileNotFoundException, IOException {
		InputStream inputStream = new FileInputStream(inputName);
		InputStream refStream = null;
//		refStream = new FileInputStream(refName);
		OutputStream outputStream = new FileOutputStream(outName);
		TemplateTestUtils.testDocument(inputStream, refStream, outputStream,
				INPUT_TEMPLATE_S, BASE_URI, false);
	}

	private void runTemplateTest(String baseUriOffset, String templateName) {
		Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+baseUriOffset+templateName+".xml", BASE_URI+baseUriOffset);
		if (template == null) {
			throw new RuntimeException("Cannot create template: "+templateName);
		}
		TemplateTestUtils.runCommentExamples(template);
	}

	private void runTemplateTest(String templateName) {
		Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
		if (template == null) {
			throw new RuntimeException("Cannot create template: "+templateName);
		}
		TemplateTestUtils.runCommentExamples(template);
	}


}
