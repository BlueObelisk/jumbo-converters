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
			@Test   public void testCoordinatesAngstrom()       {runTemplateTest("coordinates.angstom");}
			@Test   public void testDipoleMoment()              {runTemplateTest("dipole.moment");}
			@Test   public void testEnergyComponents()          {runTemplateTest("energy.components");}
			@Test   public void testEnvironScript()             {runTemplateTest("environ.01.script");}
			@Test   public void testEnvironProgInfoScript()     {runTemplateTest("environ.03.proginfo");}
			@Test   public void testFreqAtomicWeights()         {runTemplateTest("freq.normCoords.atomicWeights");}
			@Test   public void testFreqFrequenciesShort()      {runTemplateTest("freq.normCoords.frequenciesShort");}
			@Test   public void testFreqThermochemistry()       {runTemplateTest("freq.thermochemistry");}
			@Test   public void testRunTitle()                  {runTemplateTest("init.runTitle");}
			@Test   public void testControlOptions()            {runTemplateTest("init.control.options");}
			@Test   public void testSystemOptions()             {runTemplateTest("init.system.options");}
			@Test   public void testCompositeG3MP2Summary()     {runTemplateTest("composite.G3MP2.summary");}
			@Test   public void testCompositeG4MP2Summary()     {runTemplateTest("composite.G4MP2.summary");}
	@Ignore @Test   public void testKickoff()                   {runTemplateTest("environ.02.kickoff");}

	////////////  //////////// READY TO WORK ON: ////////////  ////////////
	@Ignore @Test   public void testBondOrder()                 {runTemplateTest("bondOrder.valence");}
	@Ignore	@Test   public void testFreqNormalCoordAnalysis()   {runTemplateTest("freq.normalCoordAnalysis");}
	@Ignore @Test   public void testDFTOptions()                {runTemplateTest("init.gridbased.dft.options");}



	////////////  //////////// KEEP FOR LATER: ////////////  ////////////
	@Ignore @Test   public void testInputCard()                 {runTemplateTest("init.01.inputcard");}
	@Ignore @Test   public void testBasisOptions()              {runTemplateTest("init.02.basis.options");}
	@Ignore	@Test   public void testFreqForceConstantMatrix()   {runTemplateTest("freq.forceConstantMatrix");}
	@Ignore	@Test   public void testFreqFrequenciesLong()       {runTemplateTest("freq.normCoords.frequenciesLong");}
	




	////////////  //////////// END OF TEMPLATE TESTS ////////////  ////////////

	////////////  //////////// ADD LOGFILE TESTS HERE: ////////////  ////////////

	@Ignore
			@Test   public void testMethane_B3LYP_631G_Opt()      {try {runTest("ch4_B3LYP_631G_Opt_20090112R1");  } catch (Exception e){System.out.println(e) ;} }
	@Ignore
			@Test   public void testMethane_B3LYP_631Gd_Opt()     {try {runTest("ch4_B3LYP_631Gd_Opt_20130501R1"); } catch (Exception e){System.out.println(e) ;} }
	@Ignore
			@Test   public void testMethane_B3LYP_631Gd_Freq()    {try {runTest("ch4_B3LYP_631Gd_Freq_20130501R1");} catch (Exception e){System.out.println(e) ;} }

	@Ignore
			@Test public void runDocumentTests() {
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
