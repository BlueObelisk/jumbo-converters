package org.xmlcml.cml.converters.compchem;

import java.io.IOException;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class TestUtils {
	
	private static final String SUBDIR = "templates";

	@Test
	public void testDummy() {
		Assert.assertTrue(true);
	}
	
	
	/**
	 * runs test on name.in input against ref name.xml 
	 * generates full resourcename from class of converter
	 * @param converter
	 * @param resourcePathLines
	 * @param resourcePathXML
	 */
	public static void runConverterTest(Converter converter, String resourcePathLines, String resourcePathXML) {
		runConverterTest(converter, resourcePathLines, resourcePathXML, false);
	}

	/**
	 * runs test on name.in input against ref name.xml 
	 * generates full resourcename from class of converter
	 * @param converter
	 * @param resourcePathLines
	 * @param resourcePathXML
	 * @param normalizeText
	 */
	public static void runConverterTest(Converter converter, String resourcePathLines, String resourcePathXML, boolean normalizeText) {
		List<String> lines = readLines(resourcePathLines);
		Element xmlOut = converter.convertToXML(lines);
		Element xmlRef = readXml(resourcePathXML);
		try {
			if (normalizeText) {
				CMLUtil.normalizeWhitespaceInTextNodes(xmlOut);
			}
			JumboTestUtils.assertEqualsIncludingFloat(resourcePathLines, xmlRef, xmlOut, true, 0.00000001);
		} catch (Exception e) {
			System.out.println("============XMLDIFF failure=============");
			CMLUtil.debug(xmlRef, "REFERENCE");
			System.out.println("======================================");
			CMLUtil.debug(xmlOut, "TEST");
			System.out.println("============XMLDIFF end    ===========");
		}
	}

	private static Element readXml(String resourceName) {
		Element element = null;
		try {
			element = new Builder().build(
					Util.getInputStreamFromResource(resourceName)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read xml "+resourceName, e);
		}
		return element;
	}

	private static List<String> readLines(String resourcePath) {
		return readResourcePathLines(resourcePath);
	}
	private static List<String> readResourcePathLines(String resourcePath) {
		List<String> lineList = null;
		try {
			lineList = (List<String>) IOUtils.readLines(
				Util.getInputStreamFromResource(resourcePath));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read lines from "+resourcePath, e);
		}
		return lineList;
	}

}
