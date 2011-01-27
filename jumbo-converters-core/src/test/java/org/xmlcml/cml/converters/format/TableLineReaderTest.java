package org.xmlcml.cml.converters.format;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TableLineReaderTest {

	static String TABLE1 = 
		"<table formatType=\"FORTRAN\" id=\"atmass\" reading=\"WHILE\">" +
		"(A5{tag},F10.6{mass})" +
		"</table>";
	
	static String TABLE1a = 
		"<table formatType=\"FORTRAN\" id=\"atmass\" linesToRead='3'>" +
		"(A5{tag},F10.6{mass})" +
		"</table>";
	
	static List<String> LINES1 = Arrays.asList(new String[]{
			"abcde123.456789",
			"    b  9.876543",
			"    c  7.121212",
			"    d  3.456609",
	});
	
	static String TABLE2 = 
		"<table formatType=\"FORTRAN\" id=\"atmass\" reading=\"WHILE\">" +
		"('    ',A5{tag},8X,I7{serial},F10.6{mass})" +
		"</table>";
	
	static List<String> LINES2 = Arrays.asList(new String[]{
			"    abcde        1234567123.456789",
			"        x              9  9.876543",
	});
	
	@Test 
	public void testDummy() {
		Assert.assertTrue(true);
	}
//	@Test
//	@Ignore
//	public void testTableLineReader1() {
//		TableLineReader tlr = new TableLineReader(CMLUtil.parseXML(TABLE1));
//		String expected = 
//			"<tableLineReader formatType='FORTRAN' reading='WHILE' id='atmass'>" +
//			"  <field dataTypeClass='String' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='tag'/>" +
//			"  <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='6' dataStructureClass='CMLScalar' localDictRef='mass'/>" +
//			"</tableLineReader>";
//		JumboTestUtils.assertEqualsCanonically("table", expected, tlr, true);
//	}
//
//	@Test
//	@Ignore
//	public void testTableLineReader2() {
//		TableLineReader tlr = new TableLineReader(CMLUtil.parseXML(TABLE2));
//		String expected = 
//			"<tableLineReader formatType='FORTRAN' reading='WHILE' id='atmass'>" +
//			"  <field widthToRead='4' dataStructureClass='CMLScalar' expectedValue='____' localDictRef='__literal'/>"+
//			"  <field dataTypeClass='String' widthToRead='5' dataStructureClass='CMLScalar' localDictRef='tag'/>" +
//			"  <field fieldType='X' widthToRead='8' localDictRef='field2'/>"+
//			"  <field dataTypeClass='Integer' widthToRead='7' dataStructureClass='CMLScalar' localDictRef='serial'/>"+
//			"  <field fieldType='F' dataTypeClass='Double' widthToRead='10' decimal='6' dataStructureClass='CMLScalar' localDictRef='mass'/>" +
//			"</tableLineReader>";
//		JumboTestUtils.assertEqualsCanonically("table", expected, tlr, true);
//	}
//
//	@Test
//	@Ignore
//	public void testReadLinesAndParse1() {
//		TableLineReader tlr = new TableLineReader(CMLUtil.parseXML(TABLE1));
//		JumboReader jumboReader = new JumboReader(new CMLDictionary(), "foo", LINES1); 
//		CMLElement element = tlr.readLinesAndParse(jumboReader);
//		String expected =
//		"<arrayList xmlns='http://www.xml-cml.org/schema'>"+
//		"  <array dictRef='foo:tag' dataType='xsd:string' size='4' delimiter=''>abcde b c d</array>"+
//		"  <array dictRef='foo:mass' dataType='xsd:double' size='4' delimiter=''>123.456789 9.876543 7.121212 3.456609</array>"+
//		"</arrayList>";
//		JumboTestUtils.assertEqualsCanonically("table", expected, element, true);
//	}
//	
//	@Test
//	@Ignore
//	public void testReadLinesAndParseTruncatedRead() {
//		TableLineReader tlr = new TableLineReader(CMLUtil.parseXML(TABLE1a));
//		JumboReader jumboReader = new JumboReader(new CMLDictionary(), "foo", LINES1); 
//		CMLElement element = tlr.readLinesAndParse(jumboReader);
//		String expected =
//		"<arrayList xmlns='http://www.xml-cml.org/schema'>"+
//		"  <array dictRef='foo:tag' dataType='xsd:string' size='3' delimiter=''>abcde b c</array>"+
//		"  <array dictRef='foo:mass' dataType='xsd:double' size='3' delimiter=''>123.456789 9.876543 7.121212</array>"+
//		"</arrayList>";
//		JumboTestUtils.assertEqualsCanonically("table", expected, element, true);
//		Assert.assertEquals("unparsedLine", "    d  3.456609", jumboReader.peekLine());
//	}
//
//	@Test
//	@Ignore
//	public void testReadLinesAndParse2() {
//		TableLineReader tlr = new TableLineReader(CMLUtil.parseXML(TABLE2));
//		
//		JumboReader jumboReader = new JumboReader(new CMLDictionary(), "foo", LINES2); 
//		CMLElement element = tlr.readLinesAndParse(jumboReader);
//		String expected =
//		"<arrayList xmlns='http://www.xml-cml.org/schema'>"+
//		"  <array delimiter='' dictRef='foo:tag' dataType='xsd:string' size='2'>abcde x</array>"+
//		"  <array delimiter='' dictRef='foo:serial' dataType='xsd:integer' size='2'>1234567 9</array>"+
//		"  <array delimiter='' dictRef='foo:mass' dataType='xsd:double' size='2'>123.456789 9.876543</array>"+
//		"</arrayList>";
//		JumboTestUtils.assertEqualsCanonically("table", expected, element, true);
//	}

}
