package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.format.ArrayLineReader;
import org.xmlcml.cml.converters.format.Field;
import org.xmlcml.cml.converters.format.Field.FieldType;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.converters.format.RecordsLineReader;
import org.xmlcml.cml.converters.format.SimpleFortranFormat;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class SimpleFortranFormatTest {

	public static CMLDictionary getDictionary() {
		String dictionaryXML = "<dictionary xmlns='http://www.xml-cml.org/schema'/>";
		CMLDictionary dictionary = (CMLDictionary) CMLUtil.parseQuietlyIntoCML(dictionaryXML);
		return dictionary;
	}
	@Test
	public void testSimpleFortranFormat() {
		String format = "(I3)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
		"<fortranFormat>"+
		" <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
		"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testReplaceAlpha() {
		String format = "(I3,'abcde')";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		Assert.assertEquals("replace", "(I3,Q5{abcde})", sff.getTransformedFormat());
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			" <field widthToRead='5' dataStructureClass='CMLScalar' expectedValue='abcde' localDictRef='__literal'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testReplaceAlpha1() {
		String format = "(I3,'abcde',I7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		Assert.assertEquals("replace", "(I3,Q5{abcde},I7)", sff.getTransformedFormat());
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			" <field widthToRead='5' dataStructureClass='CMLScalar' expectedValue='abcde' localDictRef='__literal'/>"+
			" <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field2'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testReplaceAlpha2() {
		String format = "('abcde',I7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		Assert.assertEquals("replace", "(Q5{abcde},I7)", sff.getTransformedFormat());
		String expected =
			"<fortranFormat>"+
			" <field widthToRead='5' dataStructureClass='CMLScalar' expectedValue='abcde' localDictRef='__literal'/>"+
			" <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field1'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testReplaceAlpha3() {
		String format = "('abcde','xy',I7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		Assert.assertEquals("replace", "(Q5{abcde},Q2{xy},I7)", sff.getTransformedFormat());
		String expected =
			"<fortranFormat>"+
			" <field widthToRead='5' dataStructureClass='CMLScalar' expectedValue='abcde' localDictRef='__literal'/>"+
			" <field widthToRead='2' dataStructureClass='CMLScalar' expectedValue='xy' localDictRef='__literal'/>"+
			" <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field2'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testUnbalancedApos1() {
		String format = "('abcde','',I7)";
		try {
			SimpleFortranFormat sff = new SimpleFortranFormat(format);
			Assert.assertEquals("replace", "(Q5,I7)", sff.getTransformedFormat());
			Assert.fail("should throw zero length string exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testUnbalancedApos2() {
		String format = "('abc'de','xy',I7)";
		try {
			SimpleFortranFormat sff = new SimpleFortranFormat(format);
			Assert.assertEquals("replace", "(Q5,I7)", sff.getTransformedFormat());
			Assert.fail("should throw unbalanced APOS exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetIntegerField() {
		String format = "(I7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testGetIntegerFieldMultiplier() {
		String format = "(3I7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		Assert.assertEquals("fields", 1, sff.getFieldList().size());
		Field f = sff.getFieldList().get(0);
		Assert.assertEquals("type", FieldType.I, f.getFieldType());
		Assert.assertEquals("width", 7, (int)f.getWidth());
		Assert.assertEquals("multiplier", 3, (int)f.getMultiplier());
		Assert.assertEquals("name", "field0", f.getLocalDictRef());
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='7' multiplier='3' dataStructureClass='CMLArray' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testGetIntegerFieldMultiplier2() {
		String format = "(11I10)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='10' multiplier='11' dataStructureClass='CMLArray' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testGetFloatFields() {
		String format = "(F6.2)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='6' decimal='2' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}

	@Test
	public void testGetFloatFields1() {
		String format = "(F16.2)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='16' decimal='2' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testGetFloatFieldsMultiplier() {
		String format = "(12F6.2)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='6' decimal='2' multiplier='12'" +
			"  dataStructureClass='CMLArray' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testGetFloatFieldsMultiplier2() {
		String format = "(5F16.10)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field fieldType='F' dataTypeClass='Double' multiplier='5' widthToRead='16' decimal='10'" +
			"    dataStructureClass='CMLArray' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testGetMultipleIntegers() {
		String format = "(I5, I12)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='field0'/>"+
			" <field dataTypeClass='Integer' widthToRead='12' dataStructureClass='CMLScalar' localDictRef='field1'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testAlpha() {
		String format = "(5A8)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='String' multiplier='5' widthToRead='8' dataStructureClass='CMLArray' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testSpaceString() {
		String format = "('junk')";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field widthToRead='4' dataStructureClass='CMLScalar' expectedValue='junk' localDictRef='__literal'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testSpaceString1() {
		String format = "(11X)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field fieldType='X' widthToRead='11' localDictRef='field0'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testGetMultipleFields() {
		String format = "(3I5,'junk',2F12.3,5A8)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5' multiplier='3'" +
			"  dataStructureClass='CMLArray' localDictRef='field0'/>"+
			" <field widthToRead='4' " +
			"  dataStructureClass='CMLScalar' expectedValue='junk' localDictRef='__literal'/>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='12' decimal='3' multiplier='2'" +
			"  dataStructureClass='CMLArray' localDictRef='field2'/>"+
			" <field dataTypeClass='String' widthToRead='8' multiplier='5'" +
			"  dataStructureClass='CMLArray' localDictRef='field3'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	@Test
	public void testName() {
		String format = "(I5{natoms})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5'" +
			"  dataStructureClass='CMLScalar' localDictRef='natoms'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("integer", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNames() {
		String format = "(I5{natoms},F10.3{charge})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5'" +
			"  dataStructureClass='CMLScalar' localDictRef='natoms'/>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3'" +
			"  dataStructureClass='CMLScalar' localDictRef='charge'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNames1() {
		String format = "(I5{natoms},2X, 3F10.3{charge}, 3A7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5' " +
			"   dataStructureClass='CMLScalar' localDictRef='natoms'/>"+
			" <field fieldType='X' widthToRead='2' localDictRef='field1'/>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='3'" +
			"   dataStructureClass='CMLArray' localDictRef='charge'/>"+
			" <field dataTypeClass='String' widthToRead='7' multiplier='3' " +
			"   dataStructureClass='CMLArray' localDictRef='field3'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testDataStructures1() {
		String format = "(I5{natoms},2X, 3F10.3{charge}, 5A7)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='Integer' widthToRead='5' " +
			"   dataStructureClass='CMLScalar' localDictRef='natoms'/>"+
			" <field fieldType='X' widthToRead='2' localDictRef='field1'/>"+
			" <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='3'" +
			"   dataStructureClass='CMLArray' localDictRef='charge'/>"+
			" <field dataTypeClass='String' widthToRead='7' multiplier='5'" +
			"   dataStructureClass='CMLArray' localDictRef='field3'/>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested() {
		String format = "(3(I5, F10.3))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' dataStructureClass='CMLScalar' widthToRead='5' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' dataStructureClass='CMLScalar' widthToRead='10' decimal='3' localDictRef='field1'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested1() {
		String format = "(3(I5, 4F10.3))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' dataStructureClass='CMLScalar' widthToRead='5' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested2() {
		String format = "(3(I5, 4F10.3),A6,I7,2F19.8)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' dataStructureClass='CMLScalar' widthToRead='5' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			" <field dataTypeClass='String' widthToRead='6' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			" <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field2'/>" +
			" <field fieldType='F' dataTypeClass='Double' widthToRead='19' decimal='8' multiplier='2' " +
			"      dataStructureClass='CMLArray' localDictRef='field3'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested3() {
		String format = "(A6,3(I5,4F10.3))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field dataTypeClass='String' widthToRead='6' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			" <field multiplier='3' localDictRef='field1'>" +
			"   <field dataTypeClass='Integer' dataStructureClass='CMLScalar' widthToRead='5' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested4() {
		String format = "(3(I5, 4F10.3),7(A6, I7, 2F19.8))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' dataStructureClass='CMLScalar' widthToRead='5' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			" <field multiplier='7' localDictRef='field1'>" +
			"   <field dataTypeClass='String' widthToRead='6' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='19' decimal='8' multiplier='2' " +
			"      dataStructureClass='CMLArray' localDictRef='field2'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}
	
	@Test
	public void testNested5() {
		String format = "(3(I5, 4F10.3),I13,'junk',7(A6, I7, 2F19.8))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			" <field dataTypeClass='Integer' widthToRead='13' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			" <field widthToRead='4' dataStructureClass='CMLScalar' expectedValue='junk' localDictRef='__literal'/>" +
			" <field multiplier='7' localDictRef='field3'>" +
			"   <field dataTypeClass='String' widthToRead='6' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='19' decimal='8' multiplier='2' " +
			"      dataStructureClass='CMLArray' localDictRef='field2'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testNested6() {
		String format = "(3(I5, 4F10.3),I13,'junk',7(A6, I7, 2F19.8),'foo',3F10.4)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='field1'/>" +
			" </field>"+
			" <field dataTypeClass='Integer' widthToRead='13' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			" <field widthToRead='4' dataStructureClass='CMLScalar' expectedValue='junk' localDictRef='__literal'/>" +
			" <field multiplier='7' localDictRef='field3'>" +
			"   <field dataTypeClass='String' widthToRead='6' dataStructureClass='CMLScalar' localDictRef='field0'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field1'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='19' decimal='8' multiplier='2' " +
			"      dataStructureClass='CMLArray' localDictRef='field2'/>" +
			" </field>"+
			" <field widthToRead='3' dataStructureClass='CMLScalar' expectedValue='foo' localDictRef='__literal'/>" +
			" <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='4' multiplier='3' " +
			"      dataStructureClass='CMLArray' localDictRef='field5'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testNestedWithNames() {
		String format = "(3(I5{ia},4F10.3{fb}))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testNestedWithNamesAndNamesFields() {
		String format = "(3(I5{ia},4F10.3{fb}){eigen})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='eigen'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testNestedWithNamesAndNamesFieldsInContext() {
		String format = "(3(I5{ia},4F10.3{fb}){eigen},A7{bar})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='eigen'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			" <field dataTypeClass='String' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='bar'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testRepeatNestedWithNamesAndNamesFieldsInContext() {
		String format = "(3(I5{ia},4F10.3{fb}){eigen},A7{bar},6(I2{ib},8F12.7{fz}){baz})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='eigen'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' " +
			"     dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			" <field dataTypeClass='String' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='bar'/>" +
			" <field multiplier='6' localDictRef='baz'>" +
			"   <field dataTypeClass='Integer' widthToRead='2' dataStructureClass='CMLScalar' localDictRef='ib'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='12' decimal='7' multiplier='8' " +
			"     dataStructureClass='CMLArray' localDictRef='fz'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testRepeatNestedWithoutNames() {
		String format = "(3(I5{ia},4F10.3{fb}),A7{bar},6(I2{ib},8F12.7{fz}))";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='field0'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' " +
			"     dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			" <field dataTypeClass='String' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='bar'/>" +
			" <field multiplier='6' localDictRef='field2'>" +
			"   <field dataTypeClass='Integer' widthToRead='2' dataStructureClass='CMLScalar' localDictRef='ib'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='12' decimal='7' multiplier='8' " +
			"     dataStructureClass='CMLArray' localDictRef='fz'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}


	@Test
	public void testRecursiveNestedWithNamesAndNamesFieldsInContext() {
		String format = "(3(I5{ia},4F10.3{fb}){eigen},A7{bar},6(I2{ib},8F12.7{fz}){baz})";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			" <field multiplier='3' localDictRef='eigen'>" +
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3' multiplier='4' " +
			"     dataStructureClass='CMLArray' localDictRef='fb'/>" +
			" </field>"+
			" <field dataTypeClass='String' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='bar'/>" +
			" <field multiplier='6' localDictRef='baz'>" +
			"   <field dataTypeClass='Integer' widthToRead='2' dataStructureClass='CMLScalar' localDictRef='ib'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='12' decimal='7' multiplier='8' " +
			"     dataStructureClass='CMLArray' localDictRef='fz'/>" +
			" </field>"+
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}


	@Test
	public void testCheckFlag1() {
		String format = "(I5{ia}@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testCheckFlag2() {
		String format = "(I5{ia}@,F10.3{fb}@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia' check='true'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3'" +
			"     dataStructureClass='CMLScalar' localDictRef='fb' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testCheckFlag3() {
		String format = "('abc'@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field widthToRead='3' dataStructureClass='CMLScalar' expectedValue='abc' check='true' localDictRef='__literal'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}


	@Test
	public void testCheckFlag4() {
		String format = "(I5{ia}@,I7@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' " +
			"      localDictRef='ia' check='true'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' " +
			"      localDictRef='field1' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testCheckFlag5() {
		String format = "(I5{ia}@,F10.3{fb}@,I3,F8.2,I7@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia' check='true'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3'" +
			"     dataStructureClass='CMLScalar' localDictRef='fb' check='true'/>" +
			"   <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field2'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='8' decimal='2' " +
			"     dataStructureClass='CMLScalar' localDictRef='field3'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field4' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testCheckFlag6() {
		String format = "(I5{ia}@,F10.3{fb}@,I3,F8.2,'pqr',I7@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia' check='true'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3'" +
			"     dataStructureClass='CMLScalar' localDictRef='fb' check='true'/>" +
			"   <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field2'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='8' decimal='2' " +
			"     dataStructureClass='CMLScalar' localDictRef='field3'/>" +
			"   <field widthToRead='3' dataStructureClass='CMLScalar' expectedValue='pqr' localDictRef='__literal'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field5' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	@Test
	public void testCheckFlag9() {
		String format = "(I5{ia}@,F10.3{fb}@,'abc'@,I3,F8.2,'pqr',I7@)";
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		String expected =
			"<fortranFormat>"+
			"   <field dataTypeClass='Integer' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='ia' check='true'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='3'" +
			"     dataStructureClass='CMLScalar' localDictRef='fb' check='true'/>" +
			"   <field widthToRead='3' dataStructureClass='CMLScalar' expectedValue='abc' check='true' localDictRef='__literal'/>" +
			"   <field dataTypeClass='Integer' widthToRead='3' dataStructureClass='CMLScalar' localDictRef='field3'/>" +
			"   <field fieldType='F' dataTypeClass='Double' widthToRead='8' decimal='2' " +
			"     dataStructureClass='CMLScalar' localDictRef='field4'/>" +
			"   <field widthToRead='3' dataStructureClass='CMLScalar' expectedValue='pqr' localDictRef='__literal'/>" +
			"   <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='field6' check='true'/>" +
			"</fortranFormat>";
		JumboTestUtils.assertEqualsCanonically("float", expected, sff.getElement(), true);
	}

	// ================================= read ============================
	@Test
	public void testJumboReaderInteger() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{natoms})", 1);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:integer' dictRef='foo:natoms' xmlns='http://www.xml-cml.org/schema'>1234</scalar>");
		JumboTestUtils.assertEqualsCanonically("integer", ref, elements.get(0), true);
	}
	
	@Test
	public void testJumboReaderDouble() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  3456.789");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(F10.3{x})", 1);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:double' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>3456.789</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref, elements.get(0), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderString() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("abcde");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(A5{name})", 1);
		CMLScalar ref = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:string' dictRef='foo:name' xmlns='http://www.xml-cml.org/schema'>abcde</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref, elements.get(0), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderScalars() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234  3456.789");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{natoms},F10.3{x})", 2);
		CMLScalar ref0 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:integer' dictRef='foo:natoms' xmlns='http://www.xml-cml.org/schema'>1234</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref0, elements.get(0), true, 0.000000001);
		CMLScalar ref1 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:double' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>3456.789</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref1,  elements.get(1), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderScalars2() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234       3456.789 ten chars");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{natoms},5X,F10.3{x},A10)", 3);
		CMLScalar ref0 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:integer' dictRef='foo:natoms' xmlns='http://www.xml-cml.org/schema'>1234</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref0, elements.get(0), true, 0.000000001);
		CMLScalar ref1 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:double' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>3456.789</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref1, elements.get(1), true, 0.000000001);
		CMLScalar ref2 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
				"<scalar dataType='xsd:string' dictRef='foo:field3' xmlns='http://www.xml-cml.org/schema'> ten chars</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("integer", ref2,  elements.get(2), false, 0.000000001);
	}
	
	@Test
	public void testJumboReaderArrayDouble() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1234.689   -23.912");
		List<CMLElement> elements = getArrayParseToCML(lineList, "(2F10.3)", 1);
		CMLArray ref1 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:double' delimiter='' size='2' dictRef='foo:field0' xmlns='http://www.xml-cml.org/schema'>1234.689 -23.912</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref1, elements.get(0), true, 0.000000001);
	}

	@Test
	public void testJumboReaderArrays() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234 5678   76  1234.689   -23.912");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(3I5{charge},2F10.3{x})", 2);
		CMLArray ref0 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:integer' delimiter='' size='3' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234 5678 76</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref0, elements.get(0), true, 0.000000001);
		CMLArray ref1 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:double' delimiter='' size='2' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>1234.689 -23.912</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref1, elements.get(1), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderArray() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234 5678 9876");
		List<CMLElement> elements = getArrayParseToCML(lineList, "(3I5{charge})", 1);
		CMLArray ref0 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
				"<array dataType='xsd:integer' delimiter='' size='3' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234 5678 9876</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref0, elements.get(0), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderArrayShort() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234 5678     ");
		List<CMLElement> elements = getArrayParseToCML(lineList, "(3I5{charge})", 1);
		CMLArray ref0 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
				"<array dataType='xsd:integer' delimiter='' size='2' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234 5678</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref0, elements.get(0), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderArraysShort() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234 5678       1234.689   -23.912");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(3I5{charge},2F10.3{x})", 2);
		CMLArray ref0 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:integer' delimiter='' size='2' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234 5678</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref0, elements.get(0), true, 0.000000001);
		CMLArray ref1 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:double' delimiter='' size='2' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>1234.689 -23.912</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref1, elements.get(1), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderScalarsAndArrays() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234  7654.689   -23.912");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{charge},2F10.3{x})", 2);
		CMLScalar ref0 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
		"<scalar dataType='xsd:integer' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("scalar", ref0, elements.get(0), true, 0.000000001);
		CMLArray ref1 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:double' delimiter='' size='2' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>7654.689 -23.912</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref1, elements.get(1), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderScalarsAndArrays2() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1234  1234.689   -23.912 five");
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{charge},2F10.3{x},A5{num})", 3);
		CMLScalar ref0 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
		"<scalar dataType='xsd:integer' dictRef='foo:charge' xmlns='http://www.xml-cml.org/schema'>1234</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("scalar", ref0, elements.get(0), true, 0.000000001);
		CMLArray ref1 = (CMLArray) CMLUtil.parseQuietlyIntoCML(
		"<array dataType='xsd:double' delimiter='' size='2' dictRef='foo:x' xmlns='http://www.xml-cml.org/schema'>1234.689 -23.912</array>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref1, elements.get(1), true, 0.000000001);
		CMLScalar ref2 = (CMLScalar) CMLUtil.parseQuietlyIntoCML(
		"<scalar dataType='xsd:string' dictRef='foo:num' xmlns='http://www.xml-cml.org/schema'> five</scalar>");
		JumboTestUtils.assertEqualsIncludingFloat("array", ref2, elements.get(2), true, 0.000000001);
	}
	
	@Test
	public void testJumboReaderNested() {
		List<String> lineList = new ArrayList<String>();
		lineList.add(" 1.23 A2 3.45 B3 6.78 C4");
		CMLElement element = getBracketedParseAsCML(lineList, "(3(F5.2{aa},1X,A2))");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
		"<list xmlns='http://www.xml-cml.org/schema' dictRef='foo:field0'>" +
		"  <list xmlns='http://www.xml-cml.org/schema'>" +
		"    <scalar dataType='xsd:double' dictRef='foo:aa'>1.23</scalar>" +
		"    <scalar dataType='xsd:string' dictRef='foo:field2'>A2</scalar>" +
		"  </list>"+
		"  <list xmlns='http://www.xml-cml.org/schema'>" +
		"    <scalar dataType='xsd:double' dictRef='foo:aa'>3.45</scalar>" +
		"    <scalar dataType='xsd:string' dictRef='foo:field2'>B3</scalar>" +
		"  </list>"+
		"  <list xmlns='http://www.xml-cml.org/schema'>" +
		"    <scalar dataType='xsd:double' dictRef='foo:aa'>6.78</scalar>" +
		"    <scalar dataType='xsd:string' dictRef='foo:field2'>C4</scalar>" +
		"  </list>"+
		"</list>"
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}


	@Test
	public void testJumboReaderMatrix0() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1.23  3.45  6.78 21.29 23.49 26.79");
		CMLElement element = getBracketedParseAsCML(lineList, "(2(3F6.2{mat}))");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
				"<list dictRef='foo:field0' xmlns='http://www.xml-cml.org/schema'>"+
				"  <list>"+
				"    <array dataType='xsd:double' delimiter='' size='3' dictRef='foo:mat'>1.23 3.45 6.78</array>"+
				"  </list>"+
				"  <list>"+
				"    <array dataType='xsd:double' delimiter='' size='3' dictRef='foo:mat'>21.29 23.49 26.79</array>"+
				"  </list>"+
				"</list>"+
				""
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}


	@Test
	public void testJumboReaderArrayCells() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1.23  3.45  6.78 A1 21.29 23.49 26.79 B2");
		CMLElement element = getBracketedParseAsCML(lineList, "(2(3F6.2{mat},A3{ser}))");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
				"<list xmlns='http://www.xml-cml.org/schema' dictRef='foo:field0'>"+
				"  <list>"+
				"    <array dataType='xsd:double' delimiter='' size='3' dictRef='foo:mat'>1.23 3.45 6.78</array>"+
				"	 <scalar dataType='xsd:string' dictRef='foo:ser'> A1</scalar>"+
				"  </list>"+
				"  <list>"+
				"    <array dataType='xsd:double' delimiter='' size='3' dictRef='foo:mat'>21.29 23.49 26.79</array>"+
				"	 <scalar dataType='xsd:string' dictRef='foo:ser'> B2</scalar>"+
				"  </list>"+
				"</list>"+
				""
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}


	@Test
	public void testJumboReaderMatrixCells() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1.23  3.45  6.78 21.29 23.49 26.79");
		CMLElement element = getBracketedParseAsCML(lineList, "(2(3(F6.2)))");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
			"	<list xmlns='http://www.xml-cml.org/schema' dictRef='foo:field0'>"+
			"	  <list>"+
			"	    <list dictRef='foo:field0'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>1.23</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>3.45</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>6.78</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	  <list>"+
			"	    <list dictRef='foo:field0'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>21.29</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>23.49</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>26.79</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	</list>"+
			""
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}

	@Test
	public void testJumboNestedGroups() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1.23 A1  3.45 B2  6.78 C3 21.29 D4 23.49 E5 26.79 F6");
		CMLElement element = getBracketedParseAsCML(lineList, "(2(3(F6.2, A3)))");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
			"	<list xmlns='http://www.xml-cml.org/schema' dictRef='foo:field0'>"+
			"	  <list>"+
			"	    <list dictRef='foo:field0'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>1.23</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> A1</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>3.45</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> B2</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>6.78</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> C3</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	  <list>"+
			"	    <list dictRef='foo:field0'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>21.29</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> D4</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>23.49</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> E5</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:field0'>26.79</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:field1'> F6</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	</list>"+
			""
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}
	
	@Test
	public void testJumboNestedNamedGroups() {
		List<String> lineList = new ArrayList<String>();
		lineList.add("  1.23 A1  3.45 B2  6.78 C3 21.29 D4 23.49 E5 26.79 F6");
		CMLElement element = getBracketedParseAsCML(lineList, "(2(3(F6.2{aa}, A3{bb}){cc}){dd})");
		CMLList ref0 = (CMLList) CMLUtil.parseQuietlyIntoCML(
			"	<list xmlns='http://www.xml-cml.org/schema' dictRef='foo:dd'>"+
			"	  <list>"+
			"	    <list dictRef='foo:cc'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>1.23</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> A1</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>3.45</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> B2</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>6.78</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> C3</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	  <list>"+
			"	    <list dictRef='foo:cc'>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>21.29</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> D4</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>23.49</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> E5</scalar>"+
			"	      </list>"+
			"	      <list>"+
			"	        <scalar dataType='xsd:double' dictRef='foo:aa'>26.79</scalar>"+
			"	        <scalar dataType='xsd:string' dictRef='foo:bb'> F6</scalar>"+
			"	      </list>"+
			"	    </list>"+
			"	  </list>"+
			"	</list>"+
			""
		);
		JumboTestUtils.assertEqualsIncludingFloat("list", ref0, element, true, 0.000000001);
	}

	@Test
	public void testReadCheckFlag1() {
		List<String> lineList = Arrays.asList(new String[]{" 1234"});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{ia}@)", 1);
		String expected = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer' dictRef='foo:ia'>1234</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);
	}

	@Test
	public void testReadCheckFlag1a() {
		List<String> lineList = Arrays.asList(new String[]{"1234 "});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(I5{ia}@)", 1);
		String expected = 
			"<scalar dataType='xsd:string' cmlx:jumboReader='misread' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx' " +
			"    cmlx:dataTypeClass='Integer' cmlx:localDictRef='ia' cmlx:widthToRead='5'>1234 </scalar>";
		CMLUtil.parseCML(expected);
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);
	}

	@Test
	public void testReadCheckFlag2() {
		List<String> lineList = Arrays.asList(new String[]{" 12345.678"});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(F10.3{fb}@)", 1);
		String expected = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:double' dictRef='foo:fb'>12345.678</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);
	}

	@Test
	public void testReadCheckFlag2b() {
		List<String> lineList = Arrays.asList(new String[]{" 123456.78"});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "(F10.3{fb}@)", 1);
		String expected = "<scalar xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx' dataType='xsd:string' " +
				"cmlx:jumboReader='misread' cmlx:decimal='3' cmlx:dataTypeClass='Double' cmlx:fieldType='F' " +
				"cmlx:localDictRef='fb' cmlx:widthToRead='10'> 123456.78</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);
	}

	@Test
	public void testReadCheckFlag3a() {
		List<String> lineList = Arrays.asList(new String[]{"abc"});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "('abc'@)", 1);
		String expected = "<scalar dataType='xsd:string' cmlx:jumboReader='misread'" +
				" xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>abc</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);

	}

	@Test
	public void testReadCheckFlag3b() {
		List<String> lineList = Arrays.asList(new String[]{"abx"});
		List<CMLElement> elements = getScalarsParseToCML(lineList, "('abc'@)", 1);
		String expected = "<scalar dataType='xsd:string' cmlx:jumboReader='misread' " +
				"xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>abx</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expected, elements.get(0), true, 0.000000001);
	}
	// =================================== helpers ===========================
	
	private List<CMLElement> getScalarsParseToCML(List<String> lineList, String format, int expectedCount) {
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		LineReader lineReader = new RecordsLineReader(sff.getFieldList());
		JumboReader jumboReader = new JumboReader(getDictionary(), "foo", lineList);
		List<CMLElement> elements = jumboReader.readElements(lineReader);
		for (CMLElement element : elements) {
			element.debug();
		}
		Assert.assertEquals("elements", expectedCount, elements.size());
		return elements;
	}
	
	private List<CMLElement> getArrayParseToCML(List<String> lineList, String format, int expectedCount) {
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		LineReader lineReader = new ArrayLineReader(sff.getFieldList());
		JumboReader jumboReader = new JumboReader(getDictionary(), "foo", lineList);
		List<CMLElement> elements = jumboReader.readElements(lineReader);
		Assert.assertEquals("elements", expectedCount, elements.size());
		return elements;
	}

	private CMLElement getBracketedParseAsCML(List<String> lineList, String format) {
		SimpleFortranFormat sff = new SimpleFortranFormat(format);
		LineReader lineReader = new BracketedLineReader(sff.getFieldList());
		JumboReader jumboReader = new JumboReader(getDictionary(), "foo", lineList);
		List<CMLElement> elements = jumboReader.readElements(lineReader);
		Assert.assertEquals("elements", 1, elements.size());
		return elements.get(0);
	}


	
}
