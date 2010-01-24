package org.xmlcml.cml.converters.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.xmlcml.euclid.EuclidConstants.S_WHITEREGEX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.testutil.JumboTestUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RegressionSuite {

	static Logger LOG = Logger.getLogger(RegressionSuite.class);
	static String OUTPUT_DIR_ROOT_NAME = "target/test";
	private final static File TEST_RESOURCES = new File("src/test/resources");
	static String DIRECTORY = "_DIR";
	protected String localDirName;
	private String inputSuffix;
	private String outputSuffix;
	private Converter converter;

	public static void run(String localDirName, String inputSuffix,
			String outputSuffix, Converter converter) {
		RegressionSuite rs = new RegressionSuite();
		rs.setLocalDirName(localDirName);
		rs.setInputSuffix(inputSuffix);
		rs.setOutputSuffix(outputSuffix);
		rs.setConverter(converter);
		rs.run();
	}

	private void compare(File refFile, File outFile) {
		LOG.debug("comparing " + refFile + " ... " + outFile);
		// try XML
		Element refElement = null;
		Element outElement = null;
		String refLine = readFirstLine(refFile);
		String outLine = readFirstLine(outFile);
		if (refLine != null && outLine != null) {
			if (refLine.length() > 0 && outLine.length() > 0) {
				if (refLine.charAt(0) == CMLConstants.C_LANGLE
						&& outLine.charAt(0) == CMLConstants.C_LANGLE) {
					try {
						refElement = new Builder().build(refFile)
								.getRootElement();
						outElement = new Builder().build(outFile)
								.getRootElement();
						// RDFXML?
						if (refElement.getLocalName().equals("RDF")
								&& refElement
										.getNamespaceURI()
										.equals(
												"http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
							compareRDF(refFile, outFile);
						} else {
							// ordinary XML
							JumboTestUtils.assertEqualsIncludingFloat(
									"Comparing XML files: " + outFile
											+ " against " + refFile,
									refElement, outElement, true, 0.00001);
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
			BufferedReader br = new BufferedReader(new FileReader(file));
			s = br.readLine();
		} catch (IOException e) {
		}
		return s;
	}

	/**
	 * at present all we can do is compare child count
	 * 
	 * @param ref
	 * @param test
	 */
	private void compareRDF(File refF, File testF) {
		List<Statement> refStmts = statementsFrom(refF);
		List<Statement> testStmts = statementsFrom(testF);
		for (int i = 0; i < refStmts.size(); i++) {
			assertStatementEquals("Statement " + i, refStmts.get(i), testStmts
					.get(i));
		}
	}

	private static void assertStatementEquals(String msg, Statement expected,
			Statement test) {
		Resource se = expected.getSubject();
		Resource st = test.getSubject();
		Property pe = expected.getPredicate();
		Property pt = test.getPredicate();
		RDFNode oe = expected.getObject();
		RDFNode ot = test.getObject();
		if (!isUUID(se)) {
			assertEquals(msg + "::Subject", se, st);
		}
		assertEquals(msg + "::Predicate", pe, pt);
		if (oe instanceof Resource) {
			if (!(ot instanceof Resource)) {
				fail(ot + " was not a Resource, and was expected to be.");
			} else if (!isUUID((Resource) oe)) {
				assertEquals(msg + "::Object", oe, ot);
			}
		} else {
			assertEquals(msg + "::Object", oe, ot);
		}
	}

	private static boolean isUUID(Resource r) {
		return r.getURI().indexOf("urn:uuid:") == 0;
	}

	private List<Statement> statementsFrom(File f) {
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(f.toURL().toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		List<Statement> stmts = m.listStatements().toList();
		Collections.sort(stmts, new Comparator<Statement>() {
			public int compare(Statement s1, Statement s2) {
				String subj1 = s1.getSubject().getURI();
				String pred1 = s1.getPredicate().getURI();
				String obj1 = s1.getObject().toString();
				String subj2 = s2.getSubject().getURI();
				String pred2 = s2.getPredicate().getURI();
				String obj2 = s2.getObject().toString();
				boolean useSubj = !isUUID(s1.getSubject());
				boolean useObj = !isUUID((Resource) s1.getObject());
				if (useSubj && !subj1.equals(subj2)) {
					return subj1.compareTo(subj2);
				} else if (!pred1.equals(pred2)) {
					return pred1.compareTo(pred2);
				} else {
					return useObj ? obj1.compareTo(obj2) : 0;
				}
			}
		});
		return stmts;
	}

	private void compareFilesLinewise(File ref, File test) {
		String refString = null;
		try {
			refString = FileUtils.readFileToString(ref).trim();
		} catch (Exception e) {
			fail("Cannot read file " + ref + " " + e);
		}
		String testString = null;
		try {
			testString = FileUtils.readFileToString(test).trim();
		} catch (Exception e) {
			fail("Cannot read file " + test + " " + e);
		}
		if (!refString.equals(testString)) {
			String[] refLines = refString.split(S_WHITEREGEX);
			String[] testLines = testString.split(S_WHITEREGEX);
			// Assert.assertEquals("line counts", refLines.length,
			// testLines.length);
			// if (!controls.contains("LINECOUNT")) {
			// for (int i = 0; i < refLines.length; i++) {
			// Assert.assertEquals("line(0) "+i, refLines[i], testLines[i]);
			// }
			// }
		}
	}

	/**
	 * TODO needs reimplementing without assuming access to input File objects
	 * (rather than streams) and without command line operation.
	 */
	public void run() {
		inputSuffix = getInputSuffix();
		outputSuffix = getOutputSuffix();
		File od = clean(getOutputDir());
		final File startDir = getStartDir();
		if (!startDir.exists()) {
			throw new RuntimeException("Cannot run regression test of "
					+ converter.getClass() + "; inputs directory " + startDir
					+ " does not exist");
		}
		File[] fs = startDir.listFiles((FileFilter) new SuffixFileFilter(
				inputSuffix));
		if ((fs == null) ? true : (fs.length == 0)) {
			throw new RuntimeException("No test files found in " + startDir
					+ " matching *." + inputSuffix + " for regression test of "
					+ converter.getClass());
		}
		for (File input : fs) {
			final File output = outputFileFor(input);
			final File reference = referenceFileFor(input);
			converter.convert(input, output);
			assertTrue("file should be created" + output, output
					.exists());
			compare(reference, output);
		}
	}

	public void setConverter(Converter c) {
		this.converter = c;
	}

	public File outputFileFor(File input) {
		return transformedExtensionInDir(input, getOutputDir());
	}

	public File referenceFileFor(File input) {
		return transformedExtensionInDir(input, getReferenceDir());
	}

	private File transformedExtensionInDir(File input, File outDir) {
		String inputName = input.getName();
		String baseName = (getInputSuffix() == null) ? FilenameUtils
				.removeExtension(inputName) : inputName.substring(0, inputName
				.length()
				- getInputSuffix().length() - 1);
		return new File(outDir, new StringBuilder(baseName).append(".").append(
				getOutputSuffix()).toString());
	}

	public File getStartDir() {
		File d = new File(TEST_RESOURCES, getLocalDirName());
		return new File(d, "in");
	}

	public File getOutputDir() {
		File d = new File(OUTPUT_DIR_ROOT_NAME, getLocalDirName());
		return new File(d, "out");
	}

	public File getReferenceDir() {
		File d = new File(TEST_RESOURCES, getLocalDirName());
		return new File(d, "ref");
	}

	public String getLocalDirName() {
		return localDirName;
	}

	public void setLocalDirName(String localDirName) {
		this.localDirName = localDirName;
	}

	/**
	 * @param outputDir
	 * @throws IOException
	 */
	private File clean(File dir) {
		try {
			FileUtils.forceMkdir(dir);
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			LOG.warn("Cannot make or clean " + dir + " " + e.getMessage(), e);
		}
		return dir;
	}

	public String getInputSuffix() {
		return inputSuffix;
	}

	public void setInputSuffix(String suffix) {
		this.inputSuffix = suffix;
	}

	public String getOutputSuffix() {
		return outputSuffix;
	}

	public void setOutputSuffix(String os) {
		outputSuffix = os;
	}
}
