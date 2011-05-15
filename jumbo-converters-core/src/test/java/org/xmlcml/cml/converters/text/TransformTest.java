package org.xmlcml.cml.converters.text;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class TransformTest {

	@Test 
	public void testXPath() {
		runTest("xpath", 
			"<transform process='addAttribute' xpath='.' name='bar' value='baz'/>",
			"<foo/>",
			"<foo bar='baz'/>"
			);
	}
	
	@Test 
	public void testXPath1() {
		runTest("xpath", 
			"<transform process='addAttribute' xpath='./plugh[1]' name='bar' value='baz'/>",
			"<foo><plugh/><plugh/><plugh/><plugh/></foo>",
			"<foo><plugh bar='baz'/><plugh/><plugh/><plugh/></foo>"
			);
	}
	
	@Test 
	public void testXPath2() {
		runTest("xpath", 
			"<transform process='addAttribute' xpath='./plugh[3]' name='bar' value='baz'/>",
			"<foo><plugh/><plugh/><plugh/><plugh/></foo>",
			"<foo><plugh/><plugh/><plugh bar='baz'/><plugh/></foo>"
			);
	}
	
	@Test 
	public void testXPath3() {
		runTest("xpath", 
			"<transform process='addAttribute' xpath='./plugh[last()]' name='bar' value='baz'/>",
			"<foo><plugh/><plugh/><plugh/><plugh/></foo>",
			"<foo><plugh/><plugh/><plugh/><plugh bar='baz'/></foo>"
			);
	}
	
	@Test 
	public void testXPath4() {
		runTest("xpath", 
			"<transform process='addAttribute' xpath='./plugh[5]' name='bar' value='baz'/>",
			"<foo><plugh/><plugh/><plugh/><plugh/></foo>",
			"<foo><plugh/><plugh/><plugh/><plugh/></foo>"
			);
	}
	
	@Test 
	public void testAddAttribute() {
		runTest("addAttribute", 
			"<transform process='addAttribute' xpath='.' name='bar' value='baz'/>",
			"<foo/>",
			"<foo bar='baz'/>"
			);
	}
	
	@Test 
	public void testAddAttribute1() {
		runTest("addAttribute", 
			"<transform process='addAttribute' xpath='.' name='bar' value='baz'/>",
			"<foo><plugh/></foo>",
			"<foo bar='baz'><plugh/></foo>"
			);
	}
	
	@Test 
	public void testAddAttribute2() {
		runTest("addAttribute", 
			"<transform process='addAttribute' xpath='. | .//*' name='bar' value='baz'/>",
			"<foo><plugh/></foo>",
			"<foo bar='baz'><plugh bar='baz'/></foo>"
			);
	}
	
	@Test 
	public void testAddAttribute3() {
		runTest("addAttribute", 
			"<transform process='addAttribute' xpath='. | .//*' name='bar' value='plinge'/>",
			"<foo bar='baz'><plugh/></foo>",
			"<foo bar='plinge'><plugh bar='plinge'/></foo>"
			);
	}
	
	@Test 
	public void testAddAttribute4() {
		try {
		runTest("addAttribute", 
			"<transform process='addAttribute' xpath='.//*' name='bar'/>",
			"<foo bar='baz'><plugh/></foo>",
			"<foo bar='plinge'><plugh bar='plinge'/></foo>"
			);
			Assert.fail("should throw exception missing value");
		} catch (Exception e) {
			
		}
	}
	
	@Test 
	public void testAddChild() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.//plugh' elementName='atom' id='plughChild'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/></plugh></foo>"
			);
	}
	
	@Test 
	public void testAddChild2() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh/><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/></foo>"
			);
	}
	
	@Test 
	public void testAddChild3() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild' position='1'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh/><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/></foo>"
			);
	}
	
	@Test 
	public void testAddChild4() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild' position='0'/>",
			"<foo><plugh/></foo>",
			"<foo><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/><plugh/></foo>"
			);
	}
	
	@Test 
	public void testAddChild5() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.|.//*' elementName='atom' id='plughChild' position='0'/>",
			"<foo><plugh/></foo>",
			"<foo><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/><plugh><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/></plugh></foo>"
			);
	}
	
	@Test 
	public void testAddDictRef() {
		runTest("addDictRef", 
			"<transform process='addDictRef' xpath='.//*' value='a:b'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh dictRef='a:b'/></foo>"
			);
	}
	
	@Test 
	/** note does NOT check for qname
	 */
	public void testAddDictRef1() {
		runTest("addDictRef", 
			"<transform process='addDictRef' xpath='.//*' value='boo'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh dictRef='boo'/></foo>"
			);
	}
	
	@Test 
	/** note does NOT check for qname
	 */
	public void testAddDictRef2() {
		runTest("addDictRef", 
			"<transform process='addDictRef' xpath='.//*' value='a:b'/>",
			"<foo><plugh dictRef='boo'/></foo>",
			"<foo><plugh dictRef='a:b'/></foo>"
			);
	}
	
	@Test 
	/** note does NOT check for qname
	 */
	public void testAddLabel() {
		runTest("addLabel", 
			"<transform process='addLabel' xpath='.//*' dictRef='a:b' value='lab'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh><label dictRef='a:b' value='lab' xmlns='http://www.xml-cml.org/schema'/></plugh></foo>"
			);
	}
	
	@Test 
	@Ignore
	// TODO
	public void testAddLink() {
		runTest("addLink", 
			"<transform process='addLabel' xpath='.//*' dictRef='a:b' value='lab'/>",
			"<foo><map><link from='a' to='b'/></map><plugh/></foo>",
			"<foo><plugh><label dictRef='a:b' value='lab' xmlns='http://www.xml-cml.org/schema'/></plugh></foo>"
			);
	}
	
	@Test 
	public void testAddNamespace() {
		runTest("addNamespace", 
			"<transform process='addNamespace' xpath='.//*' name='a' value='http://boo'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh xmlns:a='http://boo'/></foo>"
			);
	}
	
	@Test 
	@Ignore
	public void testAddUnits() {
		runTest("addUnits", 
			"<transform process='addUnits' xpath='.//*' value='nonSi:dalton'/>",
			"<foo><cml:scalar cml='http://www.xml-cml.org/schema'></foo>",
			"<foo><plugh units='nonSi:dalton'/></foo>"
			);
	}
	
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
