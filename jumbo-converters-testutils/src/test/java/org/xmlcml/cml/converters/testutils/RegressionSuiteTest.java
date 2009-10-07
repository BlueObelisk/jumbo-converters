/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.testutils;

import java.io.File;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author ojd20
 */
public class RegressionSuiteTest {

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
   }

   private String absPath(String path) {
      return new File(path).getAbsolutePath();
   }

   private String absPath(File f) {
      return f.getAbsolutePath();
   }
}
