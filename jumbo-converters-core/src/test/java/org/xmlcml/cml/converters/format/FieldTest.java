package org.xmlcml.cml.converters.format;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class FieldTest {

	@Test
	public void testReadInteger() {
		SimpleFortranFormat sff = new SimpleFortranFormat("(I5{natoms})");
		CMLScalar scalar = (CMLScalar) sff.getFieldList().get(0).read(" 1234", "dummy");
		Assert.assertNotNull("scalar", scalar);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xml-cml.org/schema' dictRef='dummy:natoms' dataType='xsd:integer'>1234</scalar>");
		JumboTestUtils.assertEqualsCanonically("scalar", ref, scalar, true);
	}

	@Test
	public void testReadDouble() {
		SimpleFortranFormat sff = new SimpleFortranFormat("(F10.3{x})");
		CMLScalar scalar = (CMLScalar) sff.getFieldList().get(0).read("  3456.789", "dummy");
		Assert.assertNotNull("scalar", scalar);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xml-cml.org/schema' dictRef='dummy:x' dataType='xsd:double'>3456.789</scalar>");
		JumboTestUtils.assertEqualsCanonically("scalar", ref, scalar, true);
	}

	@Test
	public void testReadString() {
		SimpleFortranFormat sff = new SimpleFortranFormat("(A5{name})");
		CMLScalar scalar = (CMLScalar) sff.getFieldList().get(0).read("abcde", "dummy");
		Assert.assertNotNull("scalar", scalar);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar xmlns='http://www.xml-cml.org/schema' dictRef='dummy:name' dataType='xsd:string'>abcde</scalar>");
		JumboTestUtils.assertEqualsCanonically("scalar", ref, scalar, true);
	}

	@Test
	public void testReadSpace() {
		SimpleFortranFormat sff = new SimpleFortranFormat("('5X')");
		CMLElement element = (CMLElement) sff.getFieldList().get(0).read("     ", "dummy");
		JumboTestUtils.assertEqualsCanonically("spaces", 
				"<scalar dataType='xsd:string' cmlx:jumboReader='misread' xmlns='http://www.xml-cml.org/schema' " +
				"xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'/>", element, true);
	}

	@Test
	public void testReadSpace1() {
		SimpleFortranFormat sff = new SimpleFortranFormat("('abcde')");
		CMLElement element = (CMLElement) sff.getFieldList().get(0).read("     ", "dummy");
		JumboTestUtils.assertEqualsCanonically("spaces", 
				"<scalar dataType='xsd:string' cmlx:jumboReader='misread' xmlns='http://www.xml-cml.org/schema' " +
				"xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'/>", element, true);
	}

}
