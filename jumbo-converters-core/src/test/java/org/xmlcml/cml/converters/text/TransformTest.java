package org.xmlcml.cml.converters.text;

import junit.framework.Assert;
import nu.xom.Attribute;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLScalar;
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
			"<foo><plugh><atom id='plughChild'/></plugh></foo>"
			);
	}
	
	@Test 
	public void testAddChild1() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.//plugh' elementName='cml:atom' id='plughChild'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh><atom id='plughChild' xmlns='http://www.xml-cml.org/schema'/></plugh></foo>"
			);
	}
	
	@Test 
	public void testAddChild2() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh/><atom id='plughChild'/></foo>"
			);
	}
	
	@Test 
	public void testAddChild3() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild' position='1'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh/><atom id='plughChild'/></foo>"
			);
	}
	
	@Test 
	public void testAddChild4() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.' elementName='atom' id='plughChild' position='0'/>",
			"<foo><plugh/></foo>",
			"<foo><atom id='plughChild'/><plugh/></foo>"
			);
	}
	
	@Test 
	public void testAddChild5() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.|.//*' elementName='atom' id='plughChild' position='0'/>",
			"<foo><plugh/></foo>",
			"<foo><atom id='plughChild'/><plugh><atom id='plughChild'/></plugh></foo>"
			);
	}
	
	   @Test 
	    public void testAddChild6() {
	        runTest("addChild", 
	            "<transform process='addChild' xpath='./foo2a' elementName='cml:scalar' id='child' position='0' value='$string(../..//foo3)'/>",
	            "<foo1><foo2a/><foo2b><foo3>hello</foo3></foo2b></foo1>",
	            "<foo1><foo2a><scalar id='child' dataType='xsd:string' xmlns='http://www.xml-cml.org/schema'>hello</scalar></foo2a><foo2b><foo3>hello</foo3></foo2b></foo1>"
	            );
	    }
	
	@Test 
	public void testAddUnNamespacedChild() {
		try {
			runTest("addChild", 
				"<transform process='addChild' xpath='.|.//*' elementName='dc:author' id='plughChild' position='0'/>",
				"<foo><plugh/></foo>",
				"<foo><atom id='plughChild'/><plugh><atom id='plughChild'/></plugh></foo>"
				);
			throw new RuntimeException("should throw exception - no namespace given");
		} catch (Exception e) {
		}
	}
	
	@Test 
	@Ignore // not yet running
	public void testReflection() {
		runTestCML("moleculeReflection", 
			"<transform process='reflect' xpath='.' method='calculateFormalCharge' />",
			"<cml:molecule xmlns:cml='http://www.xml-cml.org/schema'><cml:atomArray><cml:atom id='a1' formalCharge='-2'/></cml:atomArray></cml:molecule>",
			"<molecule xmlns='http://www.xml-cml.org/schema'><atomArray><atom id='a1' formalCharge='-2'/></atomArray></molecule>"

			);
	} 
	
	@Test 
	@Ignore // it actually works but my comparator doesn't compare prefixed and no-prifixed elements OK
	public void testAddNamespacedChild() {
		runTest("addChild", 
			"<transform process='addChild' xpath='.|.//*' elementName='dc:author' id='plughChild' position='0'/>",
			"<foo xmlns:dc='http://purl.org/dc/elements/1.1/'><plugh/></foo>",
			"<foo xmlns:dc='http://purl.org/dc/elements/1.1/'><dc:author id='plughChild'/><plugh><dc:author id='plughChild'/></plugh></foo>"
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
	public void testAddLink() {
		Element foo = new Element("foo");
		CMLMap map = new CMLMap();
		foo.appendChild(map);
		Element a = new Element("a");
		a.addAttribute(new Attribute("id", "a1"));
		foo.appendChild(a);
		a = new Element("a");
		a.addAttribute(new Attribute("id", "a2"));
		foo.appendChild(a);
		String listXML = 
		    "<foo>" +
			"  <map xmlns='http://www.xml-cml.org/schema'/>" +
			"  <a id='a1'/>" +
			"  <a id='a2'/>" +
			"</foo>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, foo, true, 0.000001);
		runTest("addLink", 
			"<transform process='addLink' xpath='.' value='id' "+
			"    from=\"./*[@id='a1']\" " +
			"    to=\"./*[@id='a2']\" map='./cml:map'/>",
			foo,
			"<foo>" +
			"  <map xmlns='http://www.xml-cml.org/schema'>" +
			"    <link to='a2' from='a1'/>" +
			"  </map>" +
			"  <a id='a1'/>" +
			"  <a id='a2'/>" +
			"</foo>"
			);
	}
	
	/** requires to and from attributes */
//	@Test 
//	public void testAddMap() {
//		runTest("addMap", 
//			"<transform process='addMap' xpath='.' id='map' />",
//			"<foo></foo>",
//			"<foo><map id='map' xmlns='http://www.xml-cml.org/schema'/></foo>"
//			);
//	}
	
	@Test 
	public void testAddNamespace() {
		runTest("addNamespace", 
			"<transform process='addNamespace' xpath='.//*' name='a' value='http://boo'/>",
			"<foo><plugh/></foo>",
			"<foo><plugh xmlns:a='http://boo'/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='0'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><a/><b/><x id='xx'/><c/><d/><e/><f/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling1() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./a' elementName='x' id='xx' position='0'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><x id='xx'/><a/><b/><c/><d/><e/><f/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling2() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='-2'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><x id='xx'/><a/><b/><c/><d/><e/><f/></foo>"
			);
	}
	
	
	@Test 
	public void testAddSibling3() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='-3'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling4() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='1'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><a/><b/><c/><x id='xx'/><d/><e/><f/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling5() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='4'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><a/><b/><c/><d/><e/><f/><x id='xx'/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling6() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='5'/>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>",
			"<foo><a/><b/><c/><d/><e/><f/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling7() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='0'/>",
			"<foo><a/><b/><c/><a/><b/><c/></foo>",
			"<foo><a/><b/><x id='xx'/><c/><a/><b/><x id='xx'/><c/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling8() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='1'/>",
			"<foo><a/><b/><c/><a/><b/><c/></foo>",
			"<foo><a/><b/><c/><x id='xx'/><a/><b/><c/><x id='xx'/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling9() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='0'/>",
			"<foo><c/><a/><c/><a/><c/><a/><c/></foo>",
			"<foo><x id='xx'/><c/><a/><x id='xx'/><c/><a/><x id='xx'/><c/><a/><x id='xx'/><c/></foo>"
			);
	}
	
	@Test 
	public void testAddSibling10() {
		runTest("addSibling", 
			"<transform process='addSibling' xpath='./c' elementName='x' id='xx' position='0'/>",
			"<foo><c/><c/><c/><c/></foo>",
			"<foo><x id='xx'/><c/><x id='xx'/><c/><x id='xx'/><c/><x id='xx'/><c/></foo>"
			);
	}
	
    @Test 
    public void testAddSibling11() {
        runTest("addSibling", 
            "<transform process='addSibling' xpath='./foo2a' elementName='cml:scalar' id='child' position='1' value='$string(..//foo3)'/>",
            "<foo1><foo2a/><foo2b><foo3>hello</foo3></foo2b></foo1>",
            "<foo1><foo2a/><scalar id='child' dataType='xsd:string' xmlns='http://www.xml-cml.org/schema'>hello</scalar><foo2b><foo3>hello</foo3></foo2b></foo1>"
            );
    }
	
	@Test 
	public void testAddUnits() {
		runTest("addUnits", 
			"<transform process='addUnits' xpath='.' value='nonSi:dalton'/>",
			new CMLScalar(),
			"<scalar xmlns='http://www.xml-cml.org/schema' units='nonSi:dalton'/>"
			);
	}
	
	@Test 
	public void testAddUnits1() {
		runTest("addUnits", 
			"<transform process='addUnits' xpath='.' value='nonSi:dalton'/>",
			"<foo/>",
			"<foo/>"
			);
	}

	
	@Test 
	public void testCopy() {
		runTest("copy", 
				"<transform process='copy' xpath='./c[1]' to='/foo/bar'/>",
				"<foo><c id='c1'/><c id='c2'/><c id='c3'/><c id='c4'/><bar/></foo>",
				"<foo><c id='c1'/><c id='c2'/><c id='c3'/><c id='c4'/><bar><c id='c1.copy'/></bar></foo>"
				);
	}
	
	@Test 
	public void testCopy1() {
		runTest("copy", 
				"<transform process='copy' xpath='./c' to='/foo/c'/>",
				"<foo><c id='c1'/><c id='c2'/><c id='c3'/><c id='c4'/></foo>",
				"<foo><c id='c1'><c id='c1.copy'/></c><c id='c2'><c id='c2.copy'/></c><c id='c3'><c id='c3.copy'/></c><c id='c4'><c id='c4.copy'/></c></foo>"
				);
	}
	

	@Test 
	public void testCreateArray() {
		Element foo = new Element("foo");
		CMLScalar scalar = new CMLScalar("a b c d e");
		foo.appendChild(scalar);
		runTest("createArray", 
			"<transform process='createArray' xpath='.' from='./cml:scalar'/>",
			foo,
			"<foo>" +
			"  <array dataType='xsd:string' size='5' xmlns='http://www.xml-cml.org/schema'>a b c d e</array>" +
			"</foo>"
			);
	}
	
	@Test 
	public void testCreateArray1() {
		Element foo = new Element("foo");
		foo.appendChild(new CMLScalar("a"));
		foo.appendChild(new CMLScalar("b"));
		foo.appendChild(new CMLScalar("c"));
		foo.appendChild(new CMLScalar("d"));
		foo.appendChild(new CMLScalar("e"));
		String listXML = 
		    "<foo>" +
			"  <scalar xmlns='http://www.xml-cml.org/schema'>a</scalar>" +
			"  <scalar xmlns='http://www.xml-cml.org/schema'>b</scalar>" +
			"  <scalar xmlns='http://www.xml-cml.org/schema'>c</scalar>" +
			"  <scalar xmlns='http://www.xml-cml.org/schema'>d</scalar>" +
			"  <scalar xmlns='http://www.xml-cml.org/schema'>e</scalar>" +
			"</foo>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, foo, true, 0.000001);
		runTest("createArray", 
			"<transform process='createArray' xpath='.' from='./cml:scalar'/>",
			foo,
			"<foo>" +
			"  <array dataType='xsd:string' size='5' xmlns='http://www.xml-cml.org/schema'>a b c d e</array>" +
			"</foo>"
			);
	}
	
	@Test 
        @Ignore
	public void testCreateDate() {
		Element foo = new Element("foo");
		CMLScalar scalar = new CMLScalar("Thu Apr  7 17:04:52 2011");
		foo.appendChild(scalar);
		runTest("createDate", 
			"<transform process='createDate' xpath='./cml:scalar' format='EEE MMM d HH:mm:ss yyyy'/>",
			foo,
			"<foo><scalar dataType='xsd:string' xmlns='http://www.xml-cml.org/schema'>Thu Apr  7 17:04:52 2011</scalar></foo>"
			);
	}
	
	@Test 
	@Ignore
	public void testCreateDate1() {
		Element foo = new Element("foo");
		CMLScalar scalar = new CMLScalar("Thu Apr  7 17:04:52 2011");
		foo.appendChild(scalar);
		runTest("createDate", 
			"<transform process='createDate' xpath='./cml:scalar' format='EEE MMM  d HH:mm:ss yyyy'/>",
			foo,
			"<foo><scalar dataType='xsd:date' xmlns='http://www.xml-cml.org/schema'>2011-04-07T17:04:52Z</scalar></foo>"
			);
	}
	
	@Test 
	@Ignore
	public void testCreateDate2() {
		Element foo = new Element("foo");
		CMLScalar scalar = new CMLScalar("Thu Apr 14 17:04:52 2011");
		foo.appendChild(scalar);
		runTest("createDate", 
			"<transform process='createDate' xpath='./cml:scalar' format='EEE MMM dd HH:mm:ss yyyy'/>",
			foo,
			"<foo><scalar dataType='xsd:date' xmlns='http://www.xml-cml.org/schema'>2011-04-14T17:04:52Z</scalar></foo>"
			);
	}
	
	@Test 
	public void testCreateDouble0() {
		runTest("createDouble", 
			"<transform process='createString' xpath='.'/>",
			new CMLScalar("1.23"),
			"<scalar dataType='xsd:string' xmlns='http://www.xml-cml.org/schema'>1.23</scalar>"
			);
	}
	
	@Test 
	public void testCreateDouble() {
		runTest("createDouble", 
			"<transform process='createDouble' xpath='.'/>",
			new CMLScalar("1.23"),
			"<scalar dataType='xsd:double' xmlns='http://www.xml-cml.org/schema'>1.23</scalar>"
			);
	}
	
	@Test 
	public void testCreateFormula() {
		CMLElement element = new CMLList();
		CMLScalar scalar = new CMLScalar("C 2 H 4 O 1");
		element.appendChild(scalar);
		runTest("createFormula", 
			"<transform process='createFormula' xpath='./cml:scalar'/>",
			element,
			"<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <formula concise='C 2 H 4 O 1'>" +
			"    <atomArray elementType='C H O' count='2.0 4.0 1.0'/>" +
			"  </formula>" +
			"</list>"
			);
	}
	
//	@Test 
//	@Ignore
//	public void testCreateGroup() {
//		runTest("createGroup", 
//			"<transform process='createGroup' xpath='.'/>",
//			"<cml/>",
//			"<scalar dataType='xsd:double' xmlns='http://www.xml-cml.org/schema'>1.23</scalar>"
//			);
//	}
	
	@Test 
	public void testCreateInteger() {
		runTest("createDouble", 
			"<transform process='createInteger' xpath='.'/>",
			new CMLScalar("12"),
			"<scalar dataType='xsd:integer' xmlns='http://www.xml-cml.org/schema'>12</scalar>"
			);
	}
	
	@Test 
	public void testCreateList() {
		runTest("createList", 
			"<transform process='addSibling' xpath='cml:module' elementName='cml:list' id='list' position='0'/>",
			"<cml xmlns='http://www.xml-cml.org/schema'><module id='mod'><scalar id='scal' dataType='xsd:string'>X</scalar></module></cml>",
			"<cml xmlns='http://www.xml-cml.org/schema'><list id='list'/><module id='mod'><scalar id='scal' dataType='xsd:string'>X</scalar></module></cml>"
			);
	}
	
	@Test 
	public void testCreateMatrix33() {
		Element foo = new Element("foo");
		for (int i = 0; i < 9; i++) {
			foo.appendChild(new CMLScalar(1.1*i));
		}
		runTest("createMatrix33", 
			"<transform process='createMatrix33' xpath='.' from='cml:scalar' dictRef='test:mat'/>",
			foo,
			"<foo>" +
			"  <matrix rows='3' columns='3' dataType='xsd:double' dictRef='test:mat' " +
			"    xmlns='http://www.xml-cml.org/schema'>0.0 1.1 2.2 3.3000000000000003 4.4 5.5 6.6000000000000005 7.700000000000001 8.8</matrix>" +
			"</foo>"
			);
	}
	
	@Test 
	public void testCreateMolecule() {
		CMLList list = makeListArrays();
		String listXML = 
			    "<list xmlns='http://www.xml-cml.org/schema'>" +
				"  <array dataType='xsd:string' size='3' dictRef='cc:elementType'>C H N</array>" +
				"  <array dataType='xsd:double' size='3' dictRef='cc:x3'>1.2 2.3 3.4</array>" +
				"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		runTest("createMolecule", 
			"<transform process='createMolecule' xpath='./cml:array' id='mol'/>",
			list,
			
			"<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <molecule id='mol'>" +
			"    <atomArray>" +
			"      <atom id='a1' elementType='C' x3='1.2'>" +
			"        <scalar dataType='xsd:integer' dictRef='cc:atomicNumber'>6</scalar>" +
			"      </atom>" +
			"      <atom id='a2' elementType='H' x3='2.3'>" +
			"        <scalar dataType='xsd:integer' dictRef='cc:atomicNumber'>1</scalar>" +
			"      </atom>" +
			"      <atom id='a3' elementType='N' x3='3.4'>" +
			"        <scalar dataType='xsd:integer' dictRef='cc:atomicNumber'>7</scalar>" +
			"      </atom>" +
			"    </atomArray>" +
			"    <formula formalCharge='0' concise='C 1 H 1 N 1'>" +
			"      <atomArray elementType='C H N' count='1.0 1.0 1.0'/>" +
			"    </formula>" +
			"    <property dictRef='cml:molmass'>" +
			"      <scalar dataType='xsd:double' units='unit:dalton' xmlns:unit='http://www.xml-cml.org/unit/si/'>26.017400000000002</scalar>" +
			"    </property>" +
			"  </molecule>" +
			"</list>"
			);
	}

	private CMLList makeListArrays() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new String[]{"C", "H", "N"});
		array.setDictRef("cc:elementType");
		list.appendChild(array);
		array = new CMLArray(new double[]{1.2, 2.3, 3.4});
		array.setDictRef("cc:x3");
		list.appendChild(array);
		return list;
	}
	
	@Test 
	public void testCreateNameValue() {
		Element foo = new Element("foo");
		for (int i = 0; i < 2; i++) {
			CMLList list = new CMLList();
			foo.appendChild(list);
			CMLScalar a = new CMLScalar("Humpty"+i);
			a.setDictRef("x:name");
			list.appendChild(a);
			CMLScalar b = new CMLScalar((i+1)*1.1);
			b.setDictRef("x:value");
			list.appendChild(b);
		}
		String listXML = 
		    "<foo>" +
		    "  <list xmlns='http://www.xml-cml.org/schema'>" +
			"    <scalar dataType='xsd:string' dictRef='x:name'>Humpty0</scalar>" +
			"    <scalar dataType='xsd:double' dictRef='x:value'>1.1</scalar>" +
			"  </list>" +
		    "  <list xmlns='http://www.xml-cml.org/schema'>" +
			"    <scalar dataType='xsd:string' dictRef='x:name'>Humpty1</scalar>" +
			"    <scalar dataType='xsd:double' dictRef='x:value'>2.2</scalar>" +
			"  </list>" +
			"</foo>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, foo, true, 0.000001);
		
		runTest("createNameValue", 
			"<transform process='createNameValue' xpath='./cml:list'" +
			"   name=\"*[@dictRef='x:name']\" value=\"*[@dictRef='x:value']\"/>",
			foo,
			
			"<foo>" +
			"  <list xmlns='http://www.xml-cml.org/schema'>" +
			"    <scalar dictRef='x:Humpty0' dataType='xsd:string'>1.1</scalar>" +
			"  </list>" +
			"  <list xmlns='http://www.xml-cml.org/schema'>" +
			"    <scalar dictRef='x:Humpty1' dataType='xsd:string'>2.2</scalar>" +
			"  </list>" +
			"</foo>"
			);
	}
	
	@Test 
	public void testCreateString() {
		runTest("createString", 
			"<transform process='createString' xpath='.'/>",
			new CMLScalar("foo"),
			"<scalar dataType='xsd:string' xmlns='http://www.xml-cml.org/schema'>foo</scalar>"
			);
	}
	
	@Test
	public void testCreateTriangularMatrix(){
		/**
		 * Test for extracting lower triangle from a square matrix.
		 */
		// create input XML with cmlxom datatypes
		CMLList list = new CMLList();
		list.appendChild(new CMLArray(new double[]{ 0.0, 0.1, 0.2, 0.3, 0.4 }));
		list.appendChild(new CMLArray(new double[]{ 1.0, 1.1, 1.2, 1.3, 1.4 }));
		list.appendChild(new CMLArray(new double[]{ 2.0, 2.1, 2.2, 2.3, 2.4 }));
		list.appendChild(new CMLArray(new double[]{ 3.0, 3.1, 3.2, 3.3, 3.4 }));
		list.appendChild(new CMLArray(new double[]{ 4.0, 4.1, 4.2, 4.3, 4.4 }));
		// String representation of input data
		String listXML =
			"<list xmlns='http://www.xml-cml.org/schema' >" +
			  "<array dataType='xsd:double' size='5'>0.0 0.1 0.2 0.3 0.4</array>" +
			  "<array dataType='xsd:double' size='5'>1.0 1.1 1.2 1.3 1.4</array>" +
			  "<array dataType='xsd:double' size='5'>2.0 2.1 2.2 2.3 2.4</array>" +
			  "<array dataType='xsd:double' size='5'>3.0 3.1 3.2 3.3 3.4</array>" +
			  "<array dataType='xsd:double' size='5'>4.0 4.1 4.2 4.3 4.4</array>" +
			"</list>" ;
		// compare input data with String representation
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		// run the transform
		runTest("createTriangularMatrix",
			"<transform process='createTriangularMatrix' xpath='.' from='cml:array' dictRef='x:y' />",
			list,
			"<list xmlns='http://www.xml-cml.org/schema' >" +
			  "<array dataType='xsd:double' dictRef='x:y' size='15'>0.0 1.0 1.1 2.0 2.1 2.2 3.0 3.1 3.2 3.3 4.0 4.1 4.2 4.3 4.4</array>" +
			"</list>"
		);
	}

	@Test
	public void testCreateTriangularMatrix2(){
		/**
		 * Test for extracting lower triangle from a non-square matrix.
		 */
		// create input XML with cmlxom datatypes
		CMLList list = new CMLList();
		list.appendChild(new CMLArray(new double[]{0.0, 0.1}));
		list.appendChild(new CMLArray(new double[]{1.0, 1.1}));
		list.appendChild(new CMLArray(new double[]{2.0, 2.1, 2.2, 2.3}));
		list.appendChild(new CMLArray(new double[]{3.0, 3.1, 3.2, 3.3}));
		list.appendChild(new CMLArray(new double[]{4.0, 4.1, 4.2, 4.3, 4.4}));
		// String representation of input data
		String listXML =
			"<list xmlns='http://www.xml-cml.org/schema' >" +
			  "<array dataType='xsd:double' size='2'>0.0 0.1</array>" +
			  "<array dataType='xsd:double' size='2'>1.0 1.1</array>" +
			  "<array dataType='xsd:double' size='4'>2.0 2.1 2.2 2.3</array>" +
			  "<array dataType='xsd:double' size='4'>3.0 3.1 3.2 3.3</array>" +
			  "<array dataType='xsd:double' size='5'>4.0 4.1 4.2 4.3 4.4</array>" +
			"</list>" ;
		// compare input data with String representation
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		// run the transform
		runTest("createTriangularMatrix",
			"<transform process='createTriangularMatrix' xpath='.' from='cml:array' dictRef='x:y' />",
			list,
			"<list xmlns='http://www.xml-cml.org/schema' >" +
			  "<array dataType='xsd:double' dictRef='x:y' size='15'>0.0 1.0 1.1 2.0 2.1 2.2 3.0 3.1 3.2 3.3 4.0 4.1 4.2 4.3 4.4</array>" +
			"</list>"
		);
	}

	@Test 
	public void testCreateVector3() {
		CMLList list = new CMLList();
		CMLScalar scalar = new CMLScalar(1.1);
		list.appendChild(scalar);
		scalar = new CMLScalar(2.2);
		list.appendChild(scalar);
		scalar = new CMLScalar(3.3);
		list.appendChild(scalar);
		String listXML = 
		    "<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <scalar dataType='xsd:double'>1.1</scalar>" +
			"  <scalar dataType='xsd:double'>2.2</scalar>" +
			"  <scalar dataType='xsd:double'>3.3</scalar>" +
			"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		runTest("createVector3", 
			"<transform process='createVector3' xpath='.' from='cml:scalar' dictRef='x:y'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'><vector3 dictRef='x:y'>1.1 2.2 3.3</vector3></list>"
			);
	}
	
	@Test
	public void testCreateWrapper() {
		runTest("createWrapper", 
			"<transform process='createWrapper' xpath='./bar' elementName='list'/>",
			"<foo><bar/></foo>",
			"<foo><list><bar/></list></foo>"
			);
	}
	
	@Test
	public void testCreateWrapper1() {
		runTest("createWrapper", 
			"<transform process='createWrapper' xpath='./bar' elementName='cml:list'/>",
			"<foo><bar/></foo>",
			"<foo><list xmlns='http://www.xml-cml.org/schema'><bar xmlns=''/></list></foo>"
			);
	}
	
	@Test
	@Ignore
	public void testCreateZMatrix() {
		String s = 
			  "<cml xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>" +
			  "  <list cmlx:templateRef='atom1'>" +
			  "    <scalar dataType='xsd:string' dictRef='cc:elementType'>C</scalar>" + 
			  "  </list>" +
			  "  <list cmlx:templateRef='atom2'>" +
			  "    <scalar dataType='xsd:string' dictRef='cc:elementType'>H</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>1</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>B1</scalar>" + 
			  "  </list>" +
			  "  <list cmlx:templateRef='atom3'>" +
			  "    <scalar dataType='xsd:string' dictRef='cc:elementType'>H</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>1</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>B2</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>2</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>A1</scalar>" + 
			  "  </list>" +
			  "  <list cmlx:templateRef='atom4'>" +
			  "    <scalar dataType='xsd:string' dictRef='cc:elementType'>H</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>1</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>B3</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>2</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>A2</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>3</scalar>" + 
			  "    <scalar dataType='xsd:string' dictRef='cc:name'>D1</scalar>" + 
			  "    <scalar dataType='xsd:integer' dictRef='cc:serial'>0</scalar>" + 
			  "  </list>" +
			  "</cml>"
			  ;

		CMLElement cml = (CMLElement) CMLUtil.parseCML(s);
		runTest("createZMatrix", 
			"<transform process='createZMatrix' xpath='.'/>",
			cml,
			"<foo/>"
			);
	}
	
	@Test
	public void testDelete() {
		runTest("delete", 
			"<transform process='delete' xpath='./bar'/>",
			"<foo><bar/></foo>",
			"<foo/>"
			);
	}
	
	@Test
	public void testDelete1() {
		runTest("delete", 
			"<transform process='delete' xpath='./*/@zz'/>",
			"<foo><bar zz='yy'/></foo>",
			"<foo><bar/></foo>"		
			);
	}
	
	@Test
	public void testDelete2() {
		runTest("delete", 
			"<transform process='delete' xpath='.'/>",
			"<foo><bar/></foo>",
			"<foo><bar/></foo>"
			);
	}
	
	@Test
	public void testDelete3() {
		runTest("delete", 
			"<transform process='delete' xpath='./bar[2]'/>",
			"<foo><bar id='a1'/><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testGroupSiblings() {
		runTest("groupSiblings", 
			"<transform process='groupSiblings' xpath='./post'/>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <post id='p1'/>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <post id='p2'/>" +
			"  <bar id='a4'/>" +
			"  <post id='p3'/>" +
			"  <bar id='a5'/>" +
			"  <bar id='a6'/>" +
			"  <post id='p4'/>" +
			"  <bar id='a7'/>" +
			"</foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <post id='p1'>" +
			"    <bar id='a2'/>" +
			"    <bar id='a3'/>" +
			"  </post>" +
			"  <post id='p2'>" +
			"    <bar id='a4'/>" +
			"  </post>" +
			"  <post id='p3'>" +
			"    <bar id='a5'/>" +
			"    <bar id='a6'/>" +
			"  </post>" +
			"  <post id='p4'/>" +
			"  <bar id='a7'/>" +
			"</foo>"
			);
	}
	
	
	@Test
	public void testGroupSiblings1() {
		runTest("groupSiblings", 
			"<transform process='groupSiblings' xpath='./post'/>",
			"<foo><a/><post/><b/><c/><d/><post/><e/><f/><post/><g/></foo>",
			"<foo><a/><post><b/><c/><d/></post><post><e/><f/></post><post/><g/></foo>"
		);
	}

	@Test
	public void testJoinArrays0() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{1.1,2.2,3.3,4.4,5.5});
		list.appendChild(array);
		array = new CMLArray(new double[]{6.6,7.7});
		list.appendChild(array);
		String listXML = 
		    "<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <array dataType='xsd:double' size='5'>1.1 2.2 3.3 4.4 5.5</array>" +
			"  <array dataType='xsd:double' size='2'>6.6 7.7</array>" +
			"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		runTest("joinArrays", 
			"<transform process='joinArrays' xpath='./cml:array'/>",
			list,
		    "<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <array dataType='xsd:double' size='7'>1.1 2.2 3.3 4.4 5.5 6.6 7.7</array>" +
			"</list>"
			);
	}
	
	@Test
	public void testJoinArrays() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{1.1,2.2,3.3,4.4,5.5});
		array.setDictRef("a:b");
		list.appendChild(array);
		array = new CMLArray(new double[]{6.6,7.7});
		array.setDictRef("a:b");
		list.appendChild(array);
		String listXML = 
		    "<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <array dataType='xsd:double' dictRef='a:b' size='5'>1.1 2.2 3.3 4.4 5.5</array>" +
			"  <array dataType='xsd:double' dictRef='a:b' size='2'>6.6 7.7</array>" +
			"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("test", listXML, list, true, 0.000001);
		runTest("joinArrays", 
			"<transform process='joinArrays' xpath='./cml:array'/>",
			list,
		    "<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <array dataType='xsd:double' dictRef='a:b' size='7'>1.1 2.2 3.3 4.4 5.5 6.6 7.7</array>" +
			"</list>"
			);
	}
	
	@Test
	public void testMove() {
		runTest("move", 
			"<transform process='move' xpath='./bar[2]' to='./bar[4]'/>",
			"<foo><bar id='a1'/><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'>" +
			"    <bar id='a2'/>" +
			"  </bar>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove1() {
		// cannot move attributes
		runTest("move", 
			"<transform process='move' xpath='./bar[2]/@id' to='.'/>",
			"<foo><bar id='a1'/><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo><bar id='a1'/><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>"
			);
	}
	
	@Test
	public void testMove2() {
		runTest("move", 
			"<transform process='move' xpath='./bar[2]' to='.' position='1'/>",
			"<foo><bar id='a1'/><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a2'/>" +
			"  <bar id='a1'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove3a() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='1'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <plugh/>" +
			"  <bar id='a1'/>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove3b() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='2'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <plugh/>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove3c() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='5'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"  <plugh/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove3d() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='6'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'>" +
			"    <plugh/>" +
			"  </bar>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove3e() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='0'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'>" +
			"    <plugh/>" +
			"  </bar>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <bar id='a4'/>" +
			"</foo>"
			);
	}
	
	@Test
	public void testMove4() {
		runTest("move", 
			"<transform process='move' xpath='.//plugh' to='.' position='4'/>",
			"<foo><bar id='a1'><plugh/></bar><bar id='a2'/><bar id='a3'/><bar id='a4'/></foo>",
			"<foo>" +
			"  <bar id='a1'/>" +
			"  <bar id='a2'/>" +
			"  <bar id='a3'/>" +
			"  <plugh/>" +
			"  <bar id='a4'/>" +
			"</foo>"
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
	
	@Test 
	@Ignore
	public void testPullupSingleton() {
		runTest("pullupSingleton", 
			"<transform process='pullupSingleton' xpath='.//bar'/>",
			"<foo><bar><list><plugh/></list></bar></foo>",
			"<foo/>"
			);
	}
				
	@Test 
	@Ignore
	public void testRead() {
		runTest("read", 
			"<transform process='read' xpath='.//plugh'/>",
			"<foo/>",
			"<foo/>"
			);
	}
				
    @Test
    public void testRename() {
        runTest("rename",
                "<transform process='rename' xpath='.//foo'  elementName='bar' />",
                "<one><foo id='foo' dictRef='bar:foo'/></one>",
                "<one><bar id='foo' dictRef='bar:foo'/></one>");
    }

    @Test
    public void testRename1() {
        runTest("rename",
                "<transform process='rename' xpath='.//foo'  elementName='bar' />",
                "<one><foo/></one>", "<one><bar/></one>");
    }

    @Test
    public void testRename2() {
        runTest("rename",
                "<transform process='rename' xpath='.//foo'  elementName='bar' id='foo' dictRef='bar:foo'/>",
                "<one><foo/></one>",
                "<one><bar id='foo' dictRef='bar:foo'/></one>");
    }
    
    @Test
    public void testRename3() {
        runTest("rename",
                "<transform process='rename' xpath='.//foo'  elementName='bar'/>",
                "<one><foo><child1><child1c/></child1><child2/><child3/><child4/></foo></one>",
                "<one><bar><child1><child1c/></child1><child2/><child3/><child4/></bar></one>");
    }

	@Test 
	public void testReparse() {
		
		String transformXML = 
		"<template>" +
		"  <record id='record'>VAL={A,x:y}</record>" +
		"  <transform process='reparse' xpath='./bar' regexPath=\".//record[@id='record']\"/>" +
		"</template>";
		
		String inputXML = "<foo><bar>VAL=1.23</bar></foo>";
		String refXML =
		"<foo>" +
		"  <module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>" +
		"    <list cmlx:templateRef='foo'>" +
		"      <scalar dataType='xsd:string' dictRef='x:y'>1.23</scalar>" +
		"    </list>" +
		"  </module>" +
		"</foo>";
		
		
		runReparse(transformXML, inputXML, refXML);
	}

	@Test 
	public void testReparse1() {
//		runTest1("rename", 
//			"<template>" +
//			"  <record id='record'>VAL={A,x:y}\\sFLOAT={F,x:z}</record>" +
//			"  <transform process='reparse' xpath='./bar' regexPath=\".//record[@id='record']\"/>" +
//			"</template>",
//			"<foo><bar>VAL= abc FLOAT= 2.34</bar></foo>",
//			"<foo>" +
//			"  <module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>" +
//			"    <list cmlx:templateRef='foo'>" +
//			"      <list>" +
//			"        <scalar dataType='xsd:string' dictRef='x:y'>abc</scalar>" +
//			"        <scalar dataType='xsd:double' dictRef='x:z'>2.34</scalar>" +
//			"      </list>" +
//			"    </list>" +
//			"  </module>" +
//			"</foo>"
//			);
		
		String transformXML = 
			"<template>" +
			"  <record id='record'>VAL={A,x:y}\\sFLOAT={F,x:z}</record>" +
			"  <transform process='reparse' xpath='./bar' regexPath=\".//record[@id='record']\"/>" +
			"</template>";
			
			String inputXML = "<foo><bar>VAL= abc FLOAT= 2.34</bar></foo>";
			String refXML =
				"<foo>" +
				"  <module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>" +
				"    <list cmlx:templateRef='foo'>" +
				"      <list>" +
				"        <scalar dataType='xsd:string' dictRef='x:y'>abc</scalar>" +
				"        <scalar dataType='xsd:double' dictRef='x:z'>2.34</scalar>" +
				"      </list>" +
				"    </list>" +
				"  </module>" +
				"</foo>";
			
		runReparse(transformXML, inputXML, refXML);
	}
				
	@Test 
	public void testSetValue() {
		runTest("setValue", 
			"<transform process='setValue' xpath='./bar' value='vvv'/>",
			"<foo><bar/></foo>",
			"<foo><bar>vvv</bar></foo>"
			);
	}
				
	@Test 
	public void testSetValue1() {
		// cannot set value if child
		runTest("setValue", 
			"<transform process='setValue' xpath='.' value='vvv'/>",
			"<foo><bar/></foo>",
			"<foo><bar/></foo>"
			);
	}
				
	@Test 
	public void testSetValue2() {
		runTest("setValue", 
			"<transform process='setValue' xpath='./bar' value='vvv'/>",
			"<foo><bar>xxx</bar></foo>",
			"<foo><bar>vvv</bar></foo>"
			);
	}
				
	@Test 
	public void testSetValue3() {
		runTest("setValue", 
			"<transform process='setValue' xpath='./bar/@zz' value='qq'/>",
			"<foo><bar zz='yy'>xxx</bar></foo>",
			"<foo><bar zz='qq'>xxx</bar></foo>"
			);
	}
				
	@Test 
	public void testSplit() {
		CMLList list = new CMLList();
		CMLScalar scalar = new CMLScalar("1 2 3 4 5");
		list.appendChild(scalar);
		runTest("split", 
			"<transform process='split' xpath='./cml:scalar'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <list>" +
			"    <scalar dataType='xsd:string'>1</scalar>" +
			"    <scalar dataType='xsd:string'>2</scalar>" +
			"    <scalar dataType='xsd:string'>3</scalar>" +
			"    <scalar dataType='xsd:string'>4</scalar>" +
			"    <scalar dataType='xsd:string'>5</scalar>" +
			"  </list>" +
			"</list>"
			);
	}
				
	@Test 
	public void testSplit1() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>" +
			"  <list>" +
			"    <scalar dataType='xsd:double'>1.1</scalar>" +
			"    <scalar dataType='xsd:double'>2.2</scalar>" +
			"    <scalar dataType='xsd:double'>3.3</scalar>" +
			"    <scalar dataType='xsd:double'>4.4</scalar>" +
			"    <scalar dataType='xsd:double'>5.5</scalar>" +
			"  </list>" +
			"</list>"
			);
	}
	
	@Test 
	public void testSplitCols1() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='1'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' size='1'>0.0</array>"+
			"   <array dataType='xsd:double' size='1'>1.1</array>"+
			"   <array dataType='xsd:double' size='1'>2.2</array>"+
			"   <array dataType='xsd:double' size='1'>3.3</array>"+
			"   <array dataType='xsd:double' size='1'>4.4</array>"+
			"   <array dataType='xsd:double' size='1'>5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
	
	
	@Test 
	public void testSplitCols2() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='2'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' size='2'>0.0 1.1</array>"+
			"   <array dataType='xsd:double' size='2'>2.2 3.3</array>"+
			"   <array dataType='xsd:double' size='2'>4.4 5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
	
	@Test 
	public void testSplitCols3() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='3'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' size='3'>0.0 1.1 2.2</array>"+
			"   <array dataType='xsd:double' size='3'>3.3 4.4 5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
				
	@Test 
	public void testSplitCols6() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='6'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' size='6'>0.0 1.1 2.2 3.3 4.4 5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
				
	
	@Test (expected=RuntimeException.class)
	public void testSplitColsBad() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='4'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' size='6'>0.0 1.1 2.2 3.3 4.4 5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
				
	
	@Test 
	public void testSplitColsDictRef() {
		CMLList list = new CMLList();
		CMLArray array = new CMLArray(new double[]{0.0, 1.1, 2.2, 3.3, 4.4, 5.5});
		array.setDictRef("foo:bar");
		list.appendChild(array);
		runTest("split", 
			"<transform process='split' xpath='./cml:array' cols='3'/>",
			list,
			"<list xmlns='http://www.xml-cml.org/schema'>"+
			" <list>"+
			"   <array dataType='xsd:double' dictRef='foo:bar' size='3'>0.0 1.1 2.2</array>"+
			"   <array dataType='xsd:double' dictRef='foo:bar' size='3'>3.3 4.4 5.5</array>"+
			" </list>"+
			"</list>"+
			""
			);
	}
				
	@Test 
	@Ignore
	public void testWrapWithProperty() {
		runTest("wrapWithProperty", 
			"<transform process='wrapWithProperty' xpath='./bar/@zz' value='qq'/>",
			"<foo><bar zz='yy'>xxx</bar></foo>",
			"<foo><bar zz='qq'>xxx</bar></foo>"
			);
	}
				
	@Test 
	@Ignore
	public void testWrite() {
		runTest("write", 
			"<transform process='write' xpath='./bar/@zz' value='qq'/>",
			"<foo><bar zz='yy'>xxx</bar></foo>",
			"<foo><bar zz='qq'>xxx</bar></foo>"
			);
	}
	
	private static void runTest(String message, String transformXML, String inputXML, String refXML) {
		Element transformElem = CMLUtil.parseXML(transformXML);
		Element testElement = CMLUtil.parseXML(inputXML);
//		CMLElement testElementCML = CMLElement.createCMLElement(testElement);
		MarkupApplier transformElement = new TransformElement(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refXML, testElement, true);
	}
	
	private static void runTestCML(String message, String transformXML, String inputXML, String refXML) {
		Element transformElem = CMLUtil.parseXML(transformXML);
		CMLElement testElement = CMLUtil.parseCML(inputXML);
		MarkupApplier transformElement = new TransformElement(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refXML, testElement, true);
	}
	
	private static void runTest1(String message, String transformXML, String inputXML, String refXML) {
		Element transformElem = (Element) CMLUtil.parseXML(transformXML).query("./transform").get(0);
		Element testElement = CMLUtil.parseXML(inputXML);
		MarkupApplier transformElement = new TransformElement(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refXML, testElement, true);
	}
	
	
	private static void runTest(String message, String transformXML, Element testElement, String refXML) {
		Element transformElem = CMLUtil.parseXML(transformXML);
		Element refElement = CMLUtil.parseXML(refXML);
		MarkupApplier transformElement = new TransformElement(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refElement, testElement, true);
	}
	
	static void runTest(String message, String transformXML, Element testElement, Element refElement) {
		Element transformElem = CMLUtil.parseXML(transformXML);
		MarkupApplier transformElement = TransformElement.createTransformer(transformElem);
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically(message, refElement, testElement, true);
	}
	
	private void runReparse(String transformXML, String inputXML,
			String refXML) {
		Element transformElem = (Element) CMLUtil.parseXML(transformXML).query("./transform").get(0);
		Element templateElem = (Element) CMLUtil.parseXML(transformXML);
		Template template = new Template(templateElem);
		Element testElement = CMLUtil.parseXML(inputXML);
		TransformElement transformElement = new TransformElement(transformElem);
		transformElement.setTemplate(template);
		
		transformElement.applyMarkup(testElement);
		JumboTestUtils.assertEqualsCanonically("reparse", refXML, testElement, true);
	}
		
}
