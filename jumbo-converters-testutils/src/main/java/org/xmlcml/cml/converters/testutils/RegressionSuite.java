package org.xmlcml.cml.converters.testutils;

import static org.xmlcml.euclid.EuclidConstants.S_WHITEREGEX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.testutil.TestUtils;

public class RegressionSuite {

   static Logger LOG = Logger.getLogger(RegressionSuite.class);
   static String OUTPUT_DIR_ROOT_NAME = "target/test";
   private final static File TEST_RESOURCES = new File("src/test/resources");
   static String DIRECTORY = "_DIR";
   protected String localDirName;
   private String inputSuffix;
   private String outputSuffix;
   private Converter converter;

   private void compare(File refFile, File outFile) {
      LOG.debug("comparing " + refFile + " ... " + outFile);
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
                          refElement.getNamespaceURI().equals(
                          "http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
                     compareRDF(refElement, outElement);
                  } else {
                     // ordinary XML
                     TestUtils.assertEqualsIncludingFloat("compare XML files",
                                                          refElement, outElement,
                                                          true, 0.00001);
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
         Assert.fail("Cannot read file " + ref + " " + e);
      }
      String testString = null;
      try {
         testString = FileUtils.readFileToString(test).trim();
      } catch (Exception e) {
         Assert.fail("Cannot read file " + test + " " + e);
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
   public void run() {
      inputSuffix = getInputSuffix();
      outputSuffix = getOutputSuffix();
      File od = clean(getOutputDir());
      final File startDir = getStartDir();
      if (!startDir.exists()) {
         throw new RuntimeException(
                 "Cannot run regression test of " +
                 converter.getClass() + "; inputs directory " +
                 startDir + " does not exist");
      }
      File[] fs = startDir.listFiles((FileFilter) new SuffixFileFilter(
              inputSuffix));
      if ((fs == null) ? true : (fs.length == 0)) {
         throw new RuntimeException("No test files found for regression test of " + converter.
                 getClass());
      }
      for (File input : fs) {
         final File output = outputFileFor(input);
         final File reference = referenceFileFor(input);
         converter.convert(input, output);
         Assert.assertTrue("file should be created" + output, output.exists());
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
      String baseName = FilenameUtils.removeExtension(inputName);
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
