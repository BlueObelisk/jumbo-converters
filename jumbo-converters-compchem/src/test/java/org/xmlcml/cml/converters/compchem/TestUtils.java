package org.xmlcml.cml.converters.compchem;

import java.io.IOException;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class TestUtils {
	
	private static final String IN_SUFFIX = ".in";
	private static final String XML_SUFFIX = ".xml";
	private static final String SUBDIR = "templates";

	@Test
	public void testDummy() {
		Assert.assertTrue(true);
	}
	/**
	 * runs test on name.in input against ref name.xml 
	 * generates full resourcename from class of converter
	 * @param converter
	 * @param name
	 */
	public static void runConverterTest(Converter converter, String name) {
		List<String> lines = readLines(converter, name);
		Element xmlOut = converter.convertToXML(lines);
		Element xmlRef = readXml(converter, name);
		JumboTestUtils.assertEqualsIncludingFloat(name, xmlRef, xmlOut, true, 0.00000001);
	}

	private static Element readXml(Converter converter, String name) {
		Element element = null;
		String resourceName = getResourcePath(converter)+"/"+SUBDIR+"/"+name+XML_SUFFIX;
		try {
			element = new Builder().build(
					Util.getInputStreamFromResource(resourceName)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read xml "+resourceName, e);
		}
		return element;
	}

	private static List<String> readLines(Converter converter, String name) {
		String resourcePath = getResourcePath(converter)+"/"+SUBDIR+"/"+name+IN_SUFFIX;
		List<String> lineList = null;
		try {
			lineList = (List<String>) IOUtils.readLines(
				Util.getInputStreamFromResource(resourcePath));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read lines from "+resourcePath, e);
		}
		return lineList;
	}

	private static String getResourcePath(Converter converter) {
		String converterResourcePath = converter.getClass().getPackage().getName();
		String thisResourcePath = new TestUtils().getClass().getPackage().getName();
		if (!converterResourcePath.startsWith(thisResourcePath)) {
			throw new RuntimeException("cannot work out paths");
		}
		String resourcePath = "compchem."+converterResourcePath.substring(thisResourcePath.length()+1);
		resourcePath = resourcePath.replaceAll(
				CMLConstants.S_BACKSLASH+CMLConstants.S_PERIOD, CMLConstants.S_SLASH);
		return resourcePath;
	}

}
