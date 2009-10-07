package org.xmlcml.cml.converters.testutils;

import static org.xmlcml.euclid.EuclidConstants.S_WHITEREGEX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.testutil.TestUtils;

public abstract class ConverterExercise {
	
	static Logger LOG = Logger.getLogger(ConverterExercise.class);
	static String OUTPUT_DIR_ROOT_NAME = "target/test";
	static String DIRECTORY = "_DIR";
	private File outputDir;
	private String startDirName;
	protected String localDirName;
	private String referenceDirName;
	private String inputSuffix;
	private String outputSuffix;
	private String converterName;
	private String auxiliaryFileName;
	private boolean verbose = false;
	private boolean quiet = false;
	
	private String testDir;
		
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public void setConverterName(String className) {
		this.converterName = className;
	}
	
	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public Type getInputType() {
		return inputType;
	}

	public void setInputType(Type inputType) {
		this.inputType = inputType;
	}

	public Type getOutputType() {
		return outputType;
	}

	public void setOutputType(Type outputType) {
		this.outputType = outputType;
	}

	public AbstractConverter getConverterInstance() {
		return converterInstance;
	}

	public void setConverterInstance(AbstractConverter converterInstance) {
		this.converterInstance = converterInstance;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public String getConverterName() {
		return converterName;
	}

	public void setInputSuffix(String inputSuffix) {
		this.inputSuffix = inputSuffix;
	}

	public void setOutputSuffix(String outputSuffix) {
		this.outputSuffix = outputSuffix;
	}

	public void setAuxiliaryFileName(String auxiliaryFileName) {
		this.auxiliaryFileName = auxiliaryFileName;
	}

	public void setTestRoot(String testRoot) {
		this.testRoot = testRoot;
	}
	
	private void testFiles(String[] args, File inputDir) {
		List<File> refFiles = setUpFileTestingAndOutput(inputDir);
		runCommandLineInterpreter(converterInstance, args);
		testFileResults(refFiles);
	}
	
	/**
	 * @param c
	 * @param outputDir
	 */
	public void testDirectories(String[] args, File inputDir) {
		List<File> refFiles = setupDirTestingAndRefFiles(inputDir);
		runCommandLineInterpreter(converterInstance, args);

		File[] outFiles = outputDir.listFiles();
		Assert.assertTrue("generated files must now exist in: "+getOutputDir(), outFiles != null && outFiles.length > 0);
		Assert.assertEquals("generated files must number those in ref", refFiles.size(), outFiles.length);
	}

	private List<File> setupDirTestingAndRefFiles(File inputDir) {
		setupAndCheckInputFiles(inputDir, getInputSuffix());
		createAndCleanOutputDir();
		List<File> refFiles = createAndCheckRefFiles();
		return refFiles;
	}

	private List<File> setUpFileTestingAndOutput(File inputDir) {
		List<File> fileList = setupDirTestingAndRefFiles(inputDir);
		return fileList;
	}

	private void setupAndCheckInputFiles(File inputDir, String suffix) {
		List<File> inFiles = getFiles(inputDir, suffix);
		Assert.assertTrue("input files (."+suffix+") must exist in: "+getStartDirName(), inFiles.size() > 0);
	}

	private List<File> createAndCheckRefFiles() {
		List<File> refFiles = getFiles(new File(getReferenceDirName()), getOutputSuffix());
		Assert.assertTrue("files must exist in: "+getReferenceDirName(), refFiles.size() > 0);
		return refFiles;
	}

	private void createAndCleanOutputDir() {
		outputDir = new File(getOutputDirName());
		makeAndCleanDirectory(outputDir);
		Assert.assertEquals("clean "+outputDir, 0, getFiles(outputDir).size());
	}

	private void testFileResults(List<File> refFiles) {
		List<File> outFiles = getFiles(outputDir, getOutputSuffix());
		Assert.assertTrue("output files must exist in: "+getOutputDir(), outFiles.size() > 0);
		Assert.assertEquals("generated files", refFiles.size(), outFiles.size());
		for (File refFile : refFiles) {
			File outFile = new File(outputDir, refFile.getName());
			Assert.assertTrue("file should be created"+outFile, outFile.exists());
			compare(refFile, outFile);
		}
	}	

	private void compare(File refFile, File outFile) {
		LOG.debug("comparing "+refFile+" ... "+outFile);
		// try XML
		Element refElement = null;
		Element outElement = null;
		String refLine = readFirstLine(refFile);
		String outLine = readFirstLine(outFile);
		if (refLine != null && outLine != null) {
			if (refLine.length() > 0 && outLine.length() > 0) {
				if (refLine.charAt(0) == CMLConstants.C_LANGLE && 
					outLine.charAt(0) == CMLConstants.C_LANGLE) {
					try {
						refElement = new Builder().build(refFile).getRootElement();
						outElement = new Builder().build(outFile).getRootElement();
						// RDFXML?
						if (refElement.getLocalName().equals("RDF") && 
								refElement.getNamespaceURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
							compareRDF(refElement, outElement);
						} else {
							// ordinary XML
							TestUtils.assertEqualsIncludingFloat("compare XML files", refElement, outElement, true, 0.00001);
						}
					} catch (Exception e) {
						LOG.warn("Cannot compare non-xml files");
					}
				}
			}
		}
		if (refElement == null && outElement == null) {
			compareFilesLinewise(refFile, outFile);
		}
	}
	
	private String readFirstLine(File file) {
		String s = null;
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			s = br.readLine();
		} catch (IOException e) {
		}
		return s;
	}

	/** at present all we can do is compare child count
	 * 
	 * @param ref
	 * @param test
	 */
	private void compareRDF(Element ref, Element test) {
		Assert.assertEquals("RDF children", 
				ref.getChildElements().size(), 
				test.getChildElements().size());
	}
	
	private void compareFilesLinewise(File ref, File test) {
		String refString = null;
		try {
			refString = FileUtils.readFileToString(ref).trim();
		} catch (Exception e) {
			Assert.fail("Cannot read file "+ref+" "+e);
		}
		String testString = null;
		try {
			testString = FileUtils.readFileToString(test).trim();
		} catch (Exception e) {
			Assert.fail("Cannot read file "+test+" "+e);
		}
		if (!refString.equals(testString)) {
			String[] refLines = refString.split(S_WHITEREGEX);
			String[] testLines = testString.split(S_WHITEREGEX);
//			Assert.assertEquals("line counts", refLines.length, testLines.length);
//			if (!controls.contains("LINECOUNT")) {
//				for (int i = 0; i < refLines.length; i++) {
//					Assert.assertEquals("line(0) "+i, refLines[i], testLines[i]);
//				}
//			}
		}
	}

   /**
    * TODO needs reimplementing without assuming access to input File objects
    * (rather than streams) and without command line operation. 
    */
	public void runBasicConverterTest() {

		getStartDirName();
		getOutputDirName();
		inputSuffix = getInputSuffix();
		outputSuffix = getOutputSuffix();
		auxiliaryFileName = getAuxiliaryFileName();
		List<String> argList = new ArrayList<String>();
		addTrueValue(argList, "-quiet", quiet);
		addTrueValue(argList, "-verbose", verbose);
		addNonNullValue(argList, "-sd", startDirName);
		addNonNullValue(argList, "-odir", (outputDir == null) ? null : outputDir.getAbsolutePath());
		addNonNullValue(argList, "-aux", auxiliaryFileName);
		addNonNullValue(argList, "-is", inputSuffix);
		addNonNullValue(argList, "-os", outputSuffix);
		String converterName = getConverterNameFromThis();
		if (converterName == null) {
			throw new RuntimeException("No converter instance");
		}
		addNonNullValue(argList, "-converter", converterName);
		ensureConverter();
		String args[] = argList.toArray(new String[0]);
		runConverterTest1(args);
	}

	public File getOutputDirRoot() {
		return new File(OUTPUT_DIR_ROOT_NAME);
	}
	
	public String getTestDirName() {
		testDir = getLocalDirName();
		if (testDir.indexOf(":") != -1) {
			
		} else {
			testDir = getTestRoot()+File.separator+testDir;
		}
		return testDir;
	}
	// customised for each class
	public abstract String getLocalDirName();

	
	public void setLocalDirName(String localDirName) {
		this.localDirName = localDirName;
	}

	public String getStartDirName() {
		if (startDirName == null) {
			startDirName = getTestDirName();
			// colon means absolute name
			if (startDirName.indexOf(":") == -1) {
				startDirName += File.separator+"in";
			}
		}
		return startDirName;
	}
	
	public void setStartDirName(String startDirName) {
		this.startDirName = startDirName;
	}
	
	public String getAuxiliaryFileName() {
		return auxiliaryFileName;
	}
	/** makes output directory
	 * 
	 * @return
	 */
	public String getOutputDirName() {
		if (outputDir == null) {
			outputDir = new File(getOutputDirRoot(), getLocalDirName());
		}
		return outputDir.getAbsolutePath();
	}

	/**
	 * @param outputDir
	 * @throws IOException 
	 */
	private void makeAndCleanDirectory(File dir) {
		try {
			FileUtils.forceMkdir(dir);
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
			LOG.warn("Cannot make or clean "+dir+" "+e.getMessage() , e);
		}
		LOG.debug("cleaned output directory: "+dir);
	}
	public String getReferenceDirName() {
		if (referenceDirName == null) {
			referenceDirName = getTestDirName()+File.separator+"ref";
		}
		return referenceDirName;
	}
	
	public abstract String getInputSuffix();
	public abstract String getOutputSuffix();
	
	/** creates Converter by stripping "Test" from name of test class.
	 * 
	 * @return
	 */
	public final String getConverterNameFromThis() {
		String className = this.getClass().getName();
		String converterName = (!className.endsWith("Test")) ? "BADNAME ("+className+" must end in Test)" :
			className.substring(0, className.length()-"Test".length());
		return converterName;
	}
	
	/**
	 * @param dir
	 * @return
	 */
	private List<File> getFiles(File dir) {
		List<File> fileList = new ArrayList<File>();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				fileList.add(file);
			}
		}
		return fileList;
	}
	
	/**
	 * @param dir
	 * @return
	 */
	private List<File> getFiles(File dir, String suffix) {
		List<File> fileList = new ArrayList<File>();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (suffix == null) {
					throw new RuntimeException("cannot process null suffix");
				} else if (!suffix.trim().equals(DIRECTORY)) {
					if (file.toString().endsWith(CMLConstants.S_PERIOD+suffix)) {
						fileList.add(file);
					}
				} else {
					if (file.isDirectory()) {
						fileList.add(file);
					}
				}
			}
		}
		return fileList;
	}
}
