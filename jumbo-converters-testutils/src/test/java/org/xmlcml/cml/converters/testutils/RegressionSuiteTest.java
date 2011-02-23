package org.xmlcml.cml.converters.testutils;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xmlcml.cml.converters.AbstractConverter;

/**
 *
 * @author ojd20
 */
public class RegressionSuiteTest {

   private static final Logger LOG = Logger.getLogger(RegressionSuiteTest.class.
           getName());

   @Test
   public void testFilesCalculatedProperly() {
      RegressionSuite rs = new RegressionSuite();
      rs.setLocalDirName("cdx");
      Assert.assertEquals(absPath("src/test/resources/cdx/in"),
                          absPath(rs.getStartDir()));
      Assert.assertEquals(absPath("target/test/cdx/out"), absPath(rs.
              getOutputDir()));
      Assert.assertEquals(absPath("src/test/resources/cdx/ref"), absPath(rs.
              getReferenceDir()));
   }

   @Test
   public void testOutputAndReferenceFileCalculatedProperly() {
      RegressionSuite rs = new RegressionSuite();
      rs.setLocalDirName("cdx");
      rs.setOutputSuffix("cml");
      final File inputFile = new File("src/test/resources/cdx/in/foo.cdx");
      Assert.assertEquals(absPath("target/test/cdx/out/foo.cml"),
                          absPath(rs.outputFileFor(
              inputFile)));
      Assert.assertEquals(absPath("src/test/resources/cdx/ref/foo.cml"),
                          absPath(rs.referenceFileFor(
              inputFile)));
      rs.setInputSuffix("cdx");
      Assert.assertEquals(absPath("target/test/cdx/out/foo.cml"),
                          absPath(rs.outputFileFor(
              inputFile)));
      Assert.assertEquals(absPath("src/test/resources/cdx/ref/foo.cml"),
                          absPath(rs.referenceFileFor(
              inputFile)));
   }

   @Test
   public void outputAndReferenceFileCalculatedProperlyWithDoubleSuffixInput() {
      RegressionSuite rs = new RegressionSuite();
      rs.setLocalDirName("foo");
      rs.setInputSuffix("cml.xml");
      rs.setOutputSuffix("bar");
      final File inputFile = new File("src/test/resources/foo/in/baz.cml.xml");
      Assert.assertEquals(absPath("target/test/foo/out/baz.bar"),
                          absPath(rs.outputFileFor(
              inputFile)));
      Assert.assertEquals(absPath("src/test/resources/foo/ref/baz.bar"),
                          absPath(rs.referenceFileFor(
              inputFile)));
   }

   @Test
   public void testConversionsAndComparisonsPerformed() {
      RegressionSuite rs = new RegressionSuite();
      rs.setLocalDirName("regressionSuite");
      rs.setOutputSuffix("cml");
      rs.setInputSuffix("cdx");
      AbstractConverter c = mock(AbstractConverter.class);
      rs.setConverter(c);
      final File inputFile = new File(
              "src/test/resources/regressionSuite/in/foo.cdx");
      final File outputFile = new File("target/test/regressionSuite/out/foo.cml");
      doAnswer(copyFile(inputFile, outputFile)).when(c).convert(
              eq(inputFile),
              eq(outputFile));
      rs.run();
      verify(c).convert(
              inputFile,
              outputFile);
   }

   @Test
   public void testConversionsAndComparisonsPerformedFails() {
      RegressionSuite rs = new RegressionSuite();
      rs.setLocalDirName("regressionSuite2");
      rs.setOutputSuffix("txt");
      rs.setInputSuffix("txt");
      AbstractConverter c = mock(AbstractConverter.class);
      rs.setConverter(c);
      final File inputFile = new File(
              "src/test/resources/regressionSuite2/in/foo.cdx");
      final File outputFile = new File(
              "target/test/regressionSuite2/out/foo.cml");
      doAnswer(copyFile(inputFile, outputFile)).when(c).convert(
              eq(inputFile),
              eq(outputFile));
      boolean success = false;
      try {
         rs.run();
      } catch (AssertionError e) {
         LOG.debug("Files didn't match");
         success = true;
      }
      if (!success) {
         Assert.fail("Files shouldn't have matched!");
      }
   }

   private Answer<?> copyFile(final File inputFile, final File outputFile) {
      return new Answer() {

         public Object answer(InvocationOnMock invocation) {
            try {
               FileUtils.copyFile(
                       inputFile, outputFile);
            } catch (IOException ex) {
               throw new RuntimeException(ex);
            }
            return null;
         }
      };
   }

   private String absPath(String path) {
      return new File(path).getAbsolutePath();
   }

   private String absPath(File f) {
      return f.getAbsolutePath();
   }
}
