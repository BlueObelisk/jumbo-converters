package org.xmlcml.cml.converters;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class ArgProcessorTest {

	private final static Logger LOG = Logger.getLogger(ArgProcessorTest.class);
	
	@Test
	public void testArgs0() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{""});
		} catch (Exception e) {
			Assert.assertEquals("no args", "Input file does not exist: ", e.getMessage());
		}
	}
	
	@Test
	public void testArgsIO() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"-i", "a",  "-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args i o", "Input file does not exist: a", e.getMessage());
		}
	}
	
	@Test
	public void testUnlabelledArgs() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"x", "y",  "-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args i o", "Input file does not exist: x", e.getMessage());
		}
	}
	
	@Test
	@Ignore
	public void testStdin() {
		SimpleConverter converter = new SimpleConverter();
		try {
			converter.runArgs(new String[]{"-o",  "b"});
		} catch (Exception e) {
			Assert.assertEquals("args stdin", "Reading from STDIN not yet implemented", e.getMessage());
		}
	}
	
	@Test
	public void testImplicitOutputDirectory1Input() throws Exception {
		SimpleConverter converter = new SimpleConverter();
		String outputFilename = "target/simple";
		File outputFile = new File(outputFilename);
		if (outputFile.exists()) {
			LOG.info("deleted previous: "+outputFile);
			FileUtils.forceDelete(outputFile);
		}
		Assert.assertTrue("file does not exist", !outputFile.exists());
		try {
			converter.runArgs(new String[]{"-i", "src/test/resources/org/xmlcml/cml/converters/text/text1.txt", 
					"-o",  outputFile.toString()});
		} catch (Exception e) {
			throw new RuntimeException("failed", e);
		}
		Assert.assertTrue("file exists", outputFile.exists());
		Assert.assertTrue("file", !outputFile.isDirectory());
		Assert.assertTrue("content", FileUtils.readFileToString(outputFile).length() > 0);
	}
	
	@Test
	public void testImplicitOutputDirectoryMultipleInput() throws Exception {
		SimpleConverter converter = new SimpleConverter();
		String outputFilename = "target/simple";
		File outputDir = new File(outputFilename);
		if (outputDir.exists()) {
			FileUtils.forceDelete(outputDir);
		}
		Assert.assertTrue("dir does not exist", !outputDir.exists());
		try {
			converter.runArgs(new String[]{
					"src/test/resources/org/xmlcml/cml/converters/text/text1.txt", 
					"src/test/resources/org/xmlcml/cml/converters/text/marker1.xml", 
					"-o",  outputDir.toString()});
		} catch (Exception e) {
			throw new RuntimeException("failed", e);
		}
		Assert.assertTrue("dir exists", outputDir.exists());
		Assert.assertTrue("dir exists", outputDir.isDirectory());
		Assert.assertEquals("dir", 2, outputDir.listFiles().length);
	}
	
}
