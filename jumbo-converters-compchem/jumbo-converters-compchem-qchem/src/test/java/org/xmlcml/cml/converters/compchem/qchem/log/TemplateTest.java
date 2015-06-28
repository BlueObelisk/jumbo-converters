package org.xmlcml.cml.converters.compchem.qchem.log;

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
	private static final String CODE_BASE = "qchem";
	private static final String FILE_TYPE = "log";

	private static final String LOG_DIR = "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEST_DIR = "src/test/resources/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEMPLATE_DIR = LOG_DIR+"templates/";
	static String INPUT_TEMPLATE_S = LOG_DIR+"topTemplate.xml";
	static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
	static Class<?> TEMPLATE_TEST_CLASS = org.xmlcml.cml.converters.compchem.qchem.log.TemplateTest.class;

	@Test
	@Ignore

	public void templateTester() {
		TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
	}

	////////////  //////////// ADD TEMPLATE TESTS HERE: ////////////  ////////////
	@Test   public void testEnvironPlatformOld()	{runTemplateTest("env.platform");}
	@Test   public void testEnvironPlatformNew()	{runTemplateTest("env.platform.new");}
	@Test   public void testEnvironVersion()		{runTemplateTest("env.version");}
	@Test   public void testStdNuclOrient()			{runTemplateTest("standard.nuclear.orientation");}
	@Test   public void testCoordsAngstoms()     	{runTemplateTest("coordinates.angstoms");}
	@Test   public void testEnvironHostname()	    {runTemplateTest("env.hostname");}
	@Test   public void testEnvironTimestamp()	    {runTemplateTest("env.timestamp");}
	@Test   public void testInitBasis()				{runTemplateTest("init.basis");}
	@Test   public void testMullikenCharges()	    {runTemplateTest("mulliken.charges");}
	@Test   public void testThermoChemistry()		{runTemplateTest("thermochemistry");}
	@Test   public void testMultipoleMoments()		{runTemplateTest("multipole.moments");}
	@Test   public void testPolarizabilityMatrix()	{runTemplateTest("polarizability.matrix");}
	@Test   public void testDistanceMatrix()	    {runTemplateTest("distance.matrix");}
	@Test   public void testFreqForceConstMatrix()	{runTemplateTest("freq.forceConstantMatrix");}
	@Test   public void testFreqVibAnalalysis()		{runTemplateTest("freq.vib.analysis");}
	@Test 	public void testGradients()				{runTemplateTest("gradients");}
	@Test 	public void testEnvironAuthors()	 	{runTemplateTest("env.authors");}

	////////////  //////////// READY TO WORK ON: ////////////  ////////////


	////////////  //////////// KEEP FOR LATER: ////////////  ////////////
	@Ignore @Test   public void testInitUserInput()			{runTemplateTest("init.userInput");}


	////////////  //////////// END OF TEMPLATE TESTS ////////////  ////////////

	////////////  //////////// ADD LOGFILE TESTS HERE: ////////////  ////////////

	@Ignore @Test   public void testMethane_B3LYP_631G_Opt()      {try {runTest("ch4_B3LYP_631G_Opt_20090112R1");  } catch (Exception e){System.out.println(e) ;} }
	@Ignore @Test   public void testMethane_B3LYP_631Gd_Opt()     {try {runTest("ch4_B3LYP_631Gd_Opt_20130501R1"); } catch (Exception e){System.out.println(e) ;} }
	@Ignore @Test   public void testMethane_B3LYP_631Gd_Freq()    {try {runTest("ch4_B3LYP_631Gd_Freq_20130501R1");} catch (Exception e){System.out.println(e) ;} }
	@Ignore @Test   public void testMethane_B3LYP_631Gd_OptFreq() {try {runTest("ch4_B3LYP_631Gd_Opt_HessEnd_20130501R1");} catch (Exception e){System.out.println(e) ;} }

	@Ignore @Test public void runDocumentTests() {
		for (int i=0; i<=3 ; i++) {
			try{
				System.out.println("Running test"+i) ;
				runTest("test"+i) ;
			} catch (Exception e) {
				System.out.println(e) ;
			}
		}

	}


	////////////  //////////// END OF LOGFILE TESTS ////////////  ////////////

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
