package org.xmlcml.cml.converters;

import org.junit.Test;
import org.xmlcml.euclid.Util;


public class ConverterDirectoryWalkerTest {

//FIXME Hack to please maven.
	@Test
	public void testDummy() {
//		Util.println("=============ConverterDirectoryWalkerTest==============");
	}
	
	// FIXME replace with constructor
//	@Test
//	public void testCreateConverterDirectoryWalkerStringString() {
//		Class converterClass = null;
//		ConverterDirectoryWalker 
//		walker = ConverterDirectoryWalker.createWalker("xyz", "cml", converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker("mol", "cml", converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker("cml", "cml", converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker("cml", "mol", converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker("cml", "xyz", converterClass);
//		Assert.assertNotNull("walker", walker);
//		try {
//			walker = ConverterDirectoryWalker.createWalker("foo", "xyz", converterClass);
//			Assert.fail("should throw unknown type");
//		} catch (RuntimeException e) {
//		}
//	}

	// FIXME
//	@Test
//	public void testCreateConverterDirectoryWalkerTypeType() {
//		Class converterClass = null;
//		ConverterDirectoryWalker 
//		walker = ConverterDirectoryWalker.createWalker(Type.XYZ, Type.CML, converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker(Type.MDL, Type.CML, converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker(Type.CML, Type.CML, converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker(Type.CML, Type.MDL, converterClass);
//		Assert.assertNotNull("walker", walker);
//		walker = ConverterDirectoryWalker.createWalker(Type.CML, Type.XYZ, converterClass);
//		Assert.assertNotNull("walker", walker);
//	}

//	@Test
//	@Ignore
//	public void testConvertFoo2Bar() {
//		Class converterClass = null;
//		String insuffix = "xyz";
//		String outsuffix = "cml";
//		String startDirectory = "src/test/resources/xyz";
//		String outputDirectory = "../cml1";
//		convert(insuffix, outsuffix, startDirectory, outputDirectory, converterClass);
//		
//		insuffix = "cml";
//		outsuffix = "xyz";
//		startDirectory = "src/test/resources/cml";
//		outputDirectory = "../xyz1";
//		convert(insuffix, outsuffix, startDirectory, outputDirectory, converterClass);
//		
//		insuffix = "mol";
//		outsuffix = "cml";
//		startDirectory = "src/test/resources/mdl";
//		outputDirectory = "../cml2";
//		convert(insuffix, outsuffix, startDirectory, outputDirectory, converterClass);
//		
//		insuffix = "cml";
//		outsuffix = "mol";
//		startDirectory = "src/test/resources/cml";
//		outputDirectory = "../mdl1";
//		convert(insuffix, outsuffix, startDirectory, outputDirectory, converterClass);
//		
//		insuffix = "cml";
//		outsuffix = "cml";
//		startDirectory = "src/test/resources/cml";
//		outputDirectory = "../cml3";
//		convert(insuffix, outsuffix, startDirectory, outputDirectory, converterClass);
//	}

//	private void convert(String insuffix, String outsuffix,
//			String startDirectory, String outputDirectory, Class converterClass) {
//		ConverterDirectoryWalker walker = ConverterDirectoryWalker.createWalker(insuffix, outsuffix, converterClass);
//		walker.setStartDirectory(new File(startDirectory));
//		walker.setOutputDirectory(new File(outputDirectory));
//		try {
//			walker.start();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

}
