package org.xmlcml.cml.converters.text;

import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class TransformTest {

	@Test 
	public void testPullup() {
		runTest("pullup", 
			"<transform process='pullup' xpath='.//plugh'/>",
			
			"<foo>" +
			"  <bar>" +
			"    <plugh/>" +
			"  </bar>" +
			"</foo>",
			
			"<foo>" +
			"  <plugh/>" +
			"  <bar/>" +
			"</foo>");
	}
	
	@Test 
	public void testPullup1() {
		runTest("pullup", 
				"<transform process='pullup' xpath='.//plugh' repeat='2'/>",
				
				"<foo>" +
				"  <bar>" +
				"    <plugh>" +
				"      <baz/>" +
				"    </plugh>" +
				"  </bar>" +
				"</foo>",
				
				"<foo>" +
				"  <plugh>" +
				"    <baz/>" +
				"  </plugh>" +
				"  <bar/>" +
				"</foo>");
	}
	
	private static void runTest(String message, String transformXML, String inputXML, String refXML) {
		Element transformElem = CMLUtil.parseXML(transformXML);
		Element testElement = CMLUtil.parseXML(inputXML);
		Element refElement = CMLUtil.parseXML(refXML);
		TransformElement transformElement = new TransformElement(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refXML, testElement, true);
	}
}
