package org.xmlcml.cml.converters.compchem.gaussian.log;

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
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateTest {
	private static final String CODE_BASE = "gaussian";
	private static final String FILE_TYPE = "log";
	
	private static final String LOG_DIR = "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEST_DIR = "src/test/resources/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
	private static final String TEMPLATE_DIR = LOG_DIR+"templates/";
	static String INPUT_TEMPLATE_S = LOG_DIR+"topTemplate.xml";
//	static String BASE_URI = "classpath:/"+LOG_DIR;
	static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
	static Class<?> TEMPLATE_TEST_CLASS = org.xmlcml.cml.converters.compchem.gaussian.log.TemplateTest.class;
	
	@Test
	@Ignore
	public void templateTester() {
		TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
	}

	
	@Test	public void testl0Entering()                  {runTemplateTest("l0.entering");}
	
	@Test	public void testl1Control()                  {runTemplateTest("l1.control");}
	@Test	public void testl1Keywords()                 {runTemplateTest("l1.keywords");}
	@Test	public void testl1Options()                  {runTemplateTest("l1.options");}
	@Test	public void testl1Version()                  {runTemplateTest("l1.version");}
	
	@Test	public void testl1002Minotr()                {runTemplateTest("l1002.minotr");}
	
	@Test	public void testl101Isotope()                {runTemplateTest("l101.isotope");}
	@Test	public void testl101RedundantCoords()        {runTemplateTest("l101.redundantcoords");}
	@Test	public void testl101Title()                  {runTemplateTest("l101.title");}
	@Test	public void testl101Variables()              {runTemplateTest("l101.variables");}
	@Test	public void testl101Zmat()                   {runTemplateTest("l101.zmat");}
	@Test	public void testl101ZmatVariables()          {runTemplateTest("l101.zmatvariables");}
	
	@Test	public void testl103Deltas()                 {runTemplateTest("l103.deltas");}
	@Test	public void testl103Init()                   {runTemplateTest("l103.init");}
//	@Test	public void testl103ItemConverge()           {runTemplateTest("l103.itemconverge");}
	@Test	public void testl103LocalminSaddle()         {runTemplateTest("l103.localmin");}
	@Test	public void testl103LocalminNegeig()         {runTemplateTest("l103.localmin.negativeeig");}
	@Test	public void testl103OptimizedParam()         {runTemplateTest("l103.optimizedparam");}
	@Test	public void testl103Rfo()                    {runTemplateTest("l103.rfo");}
	@Test	public void testl103Trust()                  {runTemplateTest("l103.trust");}
	
	@Test	public void testl202Distmat()                {runTemplateTest("l202.distmat");}
	@Test	public void testl202Orient()                 {runTemplateTest("l202.orient");}
	@Test	public void testl202Rotconsts()              {runTemplateTest("l202.rotconst");}
	@Test	public void testl202Stoichiometry()          {runTemplateTest("l202.stoich");}
	
	@Test	public void testl301Basis()                  {runTemplateTest("l301.basis");}
	@Test	public void testl302Basis()                  {runTemplateTest("l302.basis");}
	@Test	public void testl302aBasis()                 {runTemplateTest("l302a.basis");}
	
	@Ignore @Test	public void testl401OrbsymTRAIL()                 {runTemplateTest("l401.orbsym");}
	
	@Test   public void testl502Footer()                 {runTemplateTest("l502.footer");}
	
	@Test 	public void testl601Alphabeta()              {runTemplateTest("l601.alphabetaeigen");}
	@Test   public void testl601Anisospin()              {runTemplateTest("l601.anisospin");}
	@Ignore @Test   public void testl601CondensedBADARRAYS()              {runTemplateTest("l601.condensed");}
	@Test 	public void testl601Fermi()                  {runTemplateTest("l601.fermi");}
	@Test   public void testl601Mulliken()               {runTemplateTest("l601.mulliken");}
	@Test	public void testl601Multipole()              {runTemplateTest("l601.multipole");}
	@Test	public void testl601NoNMR()                  {runTemplateTest("l601.nonmr");}
	@Test	public void testl601Polariz()                {runTemplateTest("l601.polariz");}
	@Test	public void testl601State()                  {runTemplateTest("l601.state");}
	
	@Test	public void testl701()                       {runTemplateTest("l701");}
	@Test	public void testl702()                       {runTemplateTest("l702");}
	@Test	public void testl703()                       {runTemplateTest("l703");}
	
	@Test	public void testl716Diagvib()                {runTemplateTest("l716.diagvib");}
	@Test	public void testl716Dipole()                 {runTemplateTest("l716.dipole");}
	@Test	public void testl716Forces()                 {runTemplateTest("l716.forces");}
	@Test   public void testl716ForceConstants()         {runTemplateTest("l716.forceconstants");}
	@Test	public void testl716FreqChunk()              {runTemplateTest("l716.freq.chunk");}
	@Test	public void testl716IRSpectrum()             {runTemplateTest("l716.irspectrum");}
	@Test	public void testl716LowFreq()                {runTemplateTest("l716.lowfreq");}
	@Test	public void testl716Polarizability()         {runTemplateTest("l716.polarizability");}
	@Test	public void testl716SecondDerivative()       {runTemplateTest("l716.secondderiv");}
	@Test	public void testl716ThermochemMass()         {runTemplateTest("l716.thermochemistry.mass");}
	@Test	public void testl716ThermochemMoi()          {runTemplateTest("l716.thermochemistry.moi");}
	@Test	public void testl716ThermochemRotConsts()    {runTemplateTest("l716.thermochemistry.rotconsts");}
	@Test	public void testl716ThermochemRotSymnum()    {runTemplateTest("l716.thermochemistry.rotsymnum");}
	@Test	public void testl716ThermochemRotTemp()      {runTemplateTest("l716.thermochemistry.rottemp");}
	@Test	public void testl716ThermochemTempPress()    {runTemplateTest("l716.thermochemistry.temperature");}
	@Test	public void testl716ThermochemTop()          {runTemplateTest("l716.thermochemistry.top");}
	@Test	public void testl716ThermochemVibTemp()      {runTemplateTest("l716.thermochemistry.vibtemp");}
	@Test	public void testl716ThermochemZpe()          {runTemplateTest("l716.thermochemistry.zpe");}
	@Test	public void testl716Thermoprops()            {runTemplateTest("l716.thermoprops");}
	@Test	public void testl716Zeropoint()              {runTemplateTest("l716.zeropoint");}

	@Test	public void testl801Zeropoint()              {runTemplateTest("l801");}

//	@Test	public void testl9999ArchiveFORMULA()               {runTemplateTest("l9999.archive");}
	@Test	public void testl9999Final()                 {runTemplateTest("l9999.final");}
	@Test	public void testl9999Notarchived()           {runTemplateTest("l9999.notarchived");}
	@Test	public void testl9999Punched()               {runTemplateTest("l9999.punched");}
	
	@Test	public void testJobcpu()                     {runTemplateTest("jobcpu");}

	// XInclude doesn't work here
	@Ignore @Test	public void testl9999ArchiveINCLUDE()               {runTemplateTest("l9999.archive");}
	@Ignore @Test	public void testl103INCLUDE()                       {runTemplateTest("l103.");}
	@Ignore @Test	public void testl103PreddeltaINCLUDE()              {runTemplateTest("l103.preddelta");}
	@Ignore @Test	public void testl401AlphabetaINCLUDE()              {runTemplateTest("l401.alphabeta");}
	@Ignore @Test	public void testl601PopanalINCLUDE()                {runTemplateTest("l601.popanal");}
	@Ignore @Test	public void testl716ThermochemINCLUDE()             {runTemplateTest("l716.thermochemistry");}
	@Ignore @Test   public void testl716ForceMatrixINCLUDE()            {runTemplateTest("l716.forcematrix");}
	@Ignore @Test   public void testl716INCLUDE()                       {runTemplateTest("l716");}
	@Ignore @Test   public void testl1EndXINCLUDE()                     {runTemplateTest("l1.end");}
	
	@Ignore @Test   public void testl502CycleREPEAT()                   {runTemplateTest("l502.cycle");}
	@Ignore @Test	public void testl9999ArchiveFORMULA()               {runTemplateTest("l9999.archive");}

	@Test
	@Ignore
	public void testLog() throws IOException {
		runTest("anna1");
	}
	
	@Test @Ignore public void testAnna1() {testAnna("1");}
	@Test @Ignore public void testAnna3() {testAnna("3");}
	@Test @Ignore public void testAnna4() {testAnna("4");}
	@Test @Ignore public void testAnna5() {testAnna("5");}
	@Test @Ignore public void testAnna6() {testAnna("6");}
	@Test @Ignore public void testAnna7() {testAnna("7");}
	@Test @Ignore public void testAnna8() {testAnna("8");}
	@Test @Ignore public void testAnna9() {testAnna("9");}
	@Test @Ignore public void testAnna10() {testAnna("10");}
	@Test @Ignore public void testAnna11() {testAnna("11");}
	@Test @Ignore public void testAnna12() {testAnna("12");}
	@Test @Ignore public void testAnna13() {testAnna("13");}
	@Test @Ignore public void testAnna14() {testAnna("14");}
	@Test @Ignore public void testAnna15() {testAnna("15");}
	@Test @Ignore public void testAnna16() {testAnna("16");}
	@Test @Ignore public void testAnna17() {testAnna("17");}
	@Test @Ignore public void testAnna18() {testAnna("18");}
	@Test @Ignore public void testAnna19() {testAnna("19");}
	@Test @Ignore public void testAnna20() {testAnna("20");}
	@Test @Ignore public void testAnna43() {testAnna("43");}
	@Test @Ignore public void testAnna65() {testAnna("65");}
	
	@Test
	@Ignore
	public void runAnna() {
		for (int i = 66; i <= 100; i++) {
			try {
				testAnna(""+i);
			} catch (Exception e) {}
			System.err.println("test "+i);
		}
	}
	
	@Test
	@Ignore
	public void testHenry() throws IOException {
		testHenry("2246");
	}
	
	public void testHenry(String test) throws IOException {
		String testDir = "D:\\projects\\henry-gaussian\\";
		String inputName = testDir+"10042/to-"+test+".log";
		String refName = testDir+"ref/"+test+".xml";
		String outName = testDir+"out/to-"+test+"/output.xml";
		runTest(inputName, refName, outName);
	}

	public void testAnna(String test) {
		String testDir = "D:\\projects\\anna-gaussian\\";
		String inputName = testDir+"in/"+test+"/output.log";
		String refName = testDir+"ref/"+test+".xml";
		String outName = testDir+"out/"+test+".xml";
		String outName1 = testDir+"out/"+test+".cml";
		try {
			runTest(inputName, refName, outName);
		} catch (IOException e) {
			System.err.println("CANNOT TEST/WRITE "+outName);
			throw new RuntimeException("Cannot find/read file", e);
		}
		try {
			FileUtils.copyFile(new File(outName), new File(outName1));
		} catch (Exception e) {
			throw new RuntimeException("cannot write file: ", e);
		}
	}

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

	private void runTemplateTest(String templateName) {
		Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
		if (template == null) {
			throw new RuntimeException("Cannot create template: "+templateName);
		}
		TemplateTestUtils.runCommentExamples(template);
	}


}
