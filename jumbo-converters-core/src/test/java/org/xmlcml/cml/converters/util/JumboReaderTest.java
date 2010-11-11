package org.xmlcml.cml.converters.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class JumboReaderTest {
	
	public static CMLDictionary DICTIONARY = getDictionary();
	public static CMLDictionary getDictionary() {
		CMLDictionary dictionary = null;
		try {
			CMLCml cml = (CMLCml) new CMLBuilder().build(
					Util.getInputStreamFromResource("fortran/fortran/testDict.xml")).getRootElement();
			dictionary = (CMLDictionary) cml.getFirstChildElement(CMLDictionary.TAG, CMLConstants.CML_NS);
		} catch (Exception e) {
			throw new RuntimeException("cannot read dictionary", e);
		}
		return dictionary;
	}
	
	public String getPrefix() {
		return "pref";
	}

	@Test
	public void testGetDictionary() {
		CMLDictionary dictionary = getDictionary();
		Assert.assertNotNull(dictionary);
	}
	@Test
	public void textExpandStrings() {
		String expanded = JumboReader.expandStringsInFormatIntoNX("I4'bc'F10.3");
		Assert.assertEquals("expand", "(I4,2X,F10.3)", expanded);
	}

	@Test
	public void textParseWithExpandedStrings() {
		List<Object> objects = JumboReader.parseFortranLine("(I4'bc'F10.3)", " 123bc  1234.567");
		Assert.assertEquals("expand", 123, Integer.parseInt(objects.get(0).toString()));
		Assert.assertEquals("expand", 1234.567, new Double(objects.get(1).toString()).doubleValue(), 0.000001);
	}
	
	@Test
	public void testJumboReaderNullLine() {
		String line = null;
		try {
			JumboReader jumboReader = new JumboReader(DICTIONARY, "pref", line);
			Assert.fail("Should catch null input");
		} catch (Exception e) {
		}
	}

	@Test
	public void testJumboReaderNullLines() {
		String[] lines = null;
		try {
			new JumboReader(DICTIONARY, "pref", lines);
			Assert.fail("Should catch null input");
		} catch (Exception e) {
		}
	}

	@Test
	public void testJumboReaderNullLineList() {
		List<String> lines = null;
		try {
			new JumboReader(DICTIONARY, "pref", lines);
			Assert.fail("Should catch null input");
		} catch (Exception e) {
		}
	}

	public void testJumboReaderLine() {
		JumboReader jumboReader = new JumboReader(DICTIONARY, "pref", "line0");
		Assert.assertEquals("lines", 1, jumboReader.getLinesToBeParsed().size());
	}

	@Test
	public void textMultiField() {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "('        IVIB=',I4,' IATOM=',I4,' ICOORD=',I4,' E='F20.10)";
		List<Object> results = JumboReader.parseFortranLine(format, data);
		Assert.assertEquals("multi", 4, results.size());

	}
	
//	@Test
//	public void testXAlpha2x() throws Exception {
//		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
//		String format = "(13X,I4,7X,I4,8X,I4,3X,F20.10)";
//		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
////		List<CMLScalar> scalarList = JumboReader.parseFortranLine(format, data, names);
//		List<Object> scalarList = new JumboReader().parseScalars(format, data);
//		List<Object> test = new ArrayList<Object>();
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Double(-417.0146230240));
//		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
//	}

//	@Test
//	public void testXAlpha2xx() throws Exception {
//		String lineToParse = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
//		String format = "'        IVIB='I4' IATOM='I4' ICOORD='I4' E='F20.10";
//		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
//		JumboReader jumboReader = new JumboReader().setDictionaryPrefix("xyz");
//		List<CMLScalar> scalarList = jumboReader.parseScalars(format, lineToParse, names, false);
//		List<Object> test = new ArrayList<Object>();
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Double(-417.0146230240));
//		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
//	}


//	@Test
//	public void testXAlpha2xxx() throws Exception {
//		String data = "   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
//		String format = "I4' IATOM='I4' ICOORD='I4' E='F20.10";
//		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
//		List<CMLScalar> scalarList = new JumboReader().parseScalars(format, data, names);
//		List<Object> test = new ArrayList<Object>();
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Integer(0));
//		test.add(new Double(-417.0146230240));
//		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
//	}

	@Test
	public void testParseToSingleLineArray() {
		String data = " 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05";
		String format = "(5E16.9)";
		CMLArray array = new JumboReader().parseArray(
				format, data, CMLConstants.XSD_DOUBLE);
		CMLArray expected = new CMLArray(new double[] {
		7.799249640E-06,
		-1.877789578E-08,
		9.009626448E-06,
		5.782921410E-06,
		1.610009308E-05,});
		JumboTestUtils.assertEqualsIncludingFloat("testParseToSingleLineArray", expected, array, true, 0.000001);
	}
		
	@Test
	public void testMultipleLinesToArray() {
		List<String> lines = new ArrayList<String>();
		lines.add("--- ignore ---");
		lines.add("--- ignore1 ---");
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  21.000  22.000  23.000");
		String format = "(10F8.3)";
		int lineCount = 2;
		int fieldsToRead = 23;
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), lines);
		jumboReader.setCurrentLineNumber(2);
		CMLArray array = jumboReader.parseMultipleLinesToArray(
				format, fieldsToRead, CMLConstants.XSD_DOUBLE);
		CMLArray expected = new CMLArray(new double[] {
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  21.000, 22.000, 23.000});
		JumboTestUtils.assertEqualsIncludingFloat("testParseToSingleLineArray", expected, array, true, 0.000001);
		Assert.assertEquals("lines read", 3, jumboReader.getLinesRead());
	}
		
	@Test
	public void testMultipleLinesToArrayReadToEnd() {
		List<String> lines = new ArrayList<String>();
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  21.000  22.000  23.000");
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  51.000  52.000  23.000");
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  81.000  82.000  23.000");
		String format = "(10F8.3)";
		int fieldsToRead = -1;
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), lines);
		CMLArray array = jumboReader.parseMultipleLinesToArray(
				format, fieldsToRead, CMLConstants.XSD_DOUBLE);
		CMLArray expected = new CMLArray(new double[] {
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  21.000, 22.000, 23.000,
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  51.000, 52.000, 23.000,
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  81.000, 82.000, 23.000});

		JumboTestUtils.assertEqualsIncludingFloat("testParseToSingleLineArray", expected, array, true, 0.000001);
		Assert.assertEquals("lines read", 9, jumboReader.getLinesRead());
	}
		
	@Test
	public void testMultipleLinesToArrayReadToFirstNonCompliantLine() {
		List<String> lines = new ArrayList<String>();
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  21.000  22.000  23.000");
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  XXXXXXXXX");
		lines.add("  51.000  52.000  23.000");
		lines.add("   1.000   2.000   3.000   4.000   5.000   6.000   7.000   8.000   9.000  10.000");
		lines.add("  11.000  12.000  13.000  14.000  15.000  16.000  17.000  18.000  19.000  20.000");
		lines.add("  81.000  82.000  23.000");
		String format = "(10F8.3)";
		int fieldsToRead = -1;
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), lines);
		CMLArray array = jumboReader.parseMultipleLinesToArray(
				format, fieldsToRead, CMLConstants.XSD_DOUBLE);
		CMLArray expected = new CMLArray(new double[] {
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  21.000, 22.000, 23.000,
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		   });

		JumboTestUtils.assertEqualsIncludingFloat("testParseToSingleLineArray", expected, array, true, 0.000001);
		Assert.assertEquals("lines read", 5, jumboReader.getLinesRead());
	}
	
	@Test
	public void testJumboReaderCMLDictionaryStringListOfString() {
		List<String> stringList = new ArrayList<String>();
		stringList.add("foo");
		stringList.add("bar");
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), stringList);
		Assert.assertEquals("lines", 2, jumboReader.getLinesToBeParsed().size());
	}

	@Test
	public void testJumboReaderCMLDictionaryStringString() {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), data);
		Assert.assertEquals("lines", 1, jumboReader.getLinesToBeParsed().size());
	}

	@Test
	public void testJumboReaderCMLDictionaryStringStringArray() {
		String[] strings = new String[] {
			"foo",
			"bar"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("lines", 2, jumboReader.getLinesToBeParsed().size());
	}

	@Test
	public void testGetLinesToBeParsed() {
		String[] strings = new String[] {
				"foo",
				"bar"};
			JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
			Assert.assertEquals("lines", 2, jumboReader.getLinesToBeParsed().size());
	}

	@Test
	public void testSetLinesToBeParsedListOfString() {
		String[] strings = new String[] {
				"foo",
				"bar"};
			JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
			List<String> newlines = new ArrayList<String>();
			newlines.add("a");
			newlines.add("b");
			newlines.add("c");
			jumboReader.setLinesToBeParsed(newlines);
			JumboTestUtils.assertEquals("lines", new String[]{"a", "b", "c"}, 
					jumboReader.getLinesToBeParsed().toArray(new String[0]));
	}

	@Test
	public void testSetLinesToBeParsedStringArray() {
		String[] strings = new String[] {
				"foo",
				"bar"};
			JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
			String[] newlines = new String[]{"a", "b", "c"};
			jumboReader.setLinesToBeParsed(newlines);
			JumboTestUtils.assertEquals("lines", new String[]{"a", "b", "c"}, 
					jumboReader.getLinesToBeParsed().toArray(new String[0]));
	}

	@Test
	public void testGetCurrentLineNumber() {
		String[] strings = new String[] {"a", "b", "c"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("line", 0, jumboReader.getCurrentLineNumber());
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getCurrentLineNumber());
	}

	@Test
	public void testSetCurrentLineNumber() {
		String[] strings = new String[] {"a", "b", "c"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.setCurrentLineNumber(1);
		Assert.assertEquals("line", 1, jumboReader.getCurrentLineNumber());
	}

	@Test
	public void testGetLinesRead() {
		String[] strings = new String[] {"a", "b", "c"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getLinesRead());
	}

	@Test
	public void testGetParentElement() {
		String[] strings = new String[] {"a", "b", "c"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertNull("null parent", jumboReader.getParentElement());
		CMLScalar scalar = new CMLScalar("test");
		jumboReader.setParentElement(scalar);
		Assert.assertEquals("parent", scalar, jumboReader.getParentElement());
	}

	@Test
	public void testGetPreviousLineNumber() {
		String[] strings = new String[] {"a", "b", "c"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getLinesRead());
		Assert.assertEquals("previous line", 0, jumboReader.getPreviousLineNumber());
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getLinesRead());
		Assert.assertEquals("previous line", 1, jumboReader.getPreviousLineNumber());
	}

	@Test
	public void testParseScalarsStringStringArrayBoolean() {
		String[] strings = new String[] {"3  1.2"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseScalars("(I2, F5.1)", new String[]{"I.foo", "F.bar"}, false);
		String expectedS = "<list xmlns=\"http://www.xml-cml.org/schema\">"+
		  "<scalar dataType=\"xsd:integer\" dictRef=\"pref:foo\">3</scalar>"+
		  "<scalar dataType=\"xsd:double\" dictRef=\"pref:bar\">1.2</scalar>"+
		"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testParseScalarsPatternStringArrayBoolean() {
		String[] strings = new String[] {"3  1.2"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseScalars(Pattern.compile("(\\d+)\\s+(\\d+\\.\\d+)"), new String[]{"I.foo", "F.bar"}, false);
		String expectedS = "<list xmlns=\"http://www.xml-cml.org/schema\">"+
		  "<scalar dataType=\"xsd:integer\" dictRef=\"pref:foo\">3</scalar>"+
		  "<scalar dataType=\"xsd:double\" dictRef=\"pref:bar\">1.2</scalar>"+
		"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testParseMultipleLinesToArrayStringIntString() {
		String[] strings = new String[] {" 1 2 3 4 5", " 6 7 8 910", "111213"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseMultipleLinesToArray("(5I2)", 11, CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"13\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5 6 7 8 9 10 11 12 13</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}
	@Test
	public void testParseMultipleLinesToArrayStringIntString1() {
		String[] strings = new String[] {" 1 2 3 4 5", " 6 7 8 910", "111213"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseMultipleLinesToArray("(5I2)", 2, CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"5\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}
	@Test
	public void testParseMultipleLinesToArrayStringIntString2() {
		String[] strings = new String[] {" 1 2 3 4 5", " 6 7 8 910", "111213"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseMultipleLinesToArray("(5I2)", 8, CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"10\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5 6 7 8 9 10</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testParseMatrix() {
		String[] strings = new String[] {" 1 2 3 4 5", " 6 7 8 910", "1112131415"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.setParentElement(new CMLModule());
		CMLElement element = jumboReader.parseMatrix(
				3, 5, "(5I2)", CMLConstants.XSD_INTEGER, "myMatrix", false);
		String expectedS = "<matrix delimiter=\"\" dataType=\"xsd:integer\" rows=\"3\" columns=\"5\" dictRef=\"pref:myMatrix\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5 6 7 8 9 10 11 12 13 14 15</matrix>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testParseMoleculeAsColumns() {
		String[] strings = new String[] {"C1 C 0. 0. 0.", "O2 O 1. 0. 0.",};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		int elsym = 1;
		int label = 0;
		int atnum = -1;
		CMLMolecule molecule = jumboReader.parseMoleculeAsColumns(
				2, "(A2,A2,3(F3.1))", new int[]{elsym, atnum, label, 2, 3, 4}, false);
		String expectedS = "<molecule xmlns=\"http://www.xml-cml.org/schema\">" +
				"  <atomArray>" +
				"    <atom id=\"a1\" elementType=\"C\" x3=\"0.0\" y3=\"0.0\" z3=\"0.0\">" +
				"      <label value=\"C1\"/>" +
				"    </atom>" +
				"    <atom id=\"a2\" elementType=\"O\" x3=\"1.0\" y3=\"0.0\" z3=\"0.0\">" +
				"      <label value=\"O2\"/>" +
				"    </atom>" +
				"  </atomArray>" +
				"</molecule>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, molecule, true, 0.000001);
	}

	@Test
	public void testParseArrayStringStringStringBoolean() {
//		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		JumboReader jumboReader = new JumboReader();
		CMLElement element = jumboReader.parseArray("(5I2)", " 1 2 3 4 5", CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"5\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testReadArrayGreedily() {
		String[] strings = new String[] {" 1 2 3 4 5", " 6 7 8 910", "XXX", "111213"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.readArrayGreedily("(5I2)", CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"10\" " +
				"xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5 6 7 8 9 10</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testParseTableColumnsAsArrayList() {
		String[] strings = new String[] {" C 2 3.4", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLElement element = jumboReader.parseTableColumnsAsArrayList(
				"(A2,I2, F4.2)", 2, new String[]{"A.a", "I.b", "F.c"}, false);
		String expectedS = "<arrayList xmlns=\"http://www.xml-cml.org/schema\">" +
				"  <array dictRef=\"pref:a\" dataType=\"xsd:string\" size=\"2\" delimiter=\"\">C X</array>" +
				"  <array dictRef=\"pref:b\" dataType=\"xsd:integer\" size=\"2\" delimiter=\"\">2 7</array>" +
				"  <array dictRef=\"pref:c\" dataType=\"xsd:double\" size=\"2\" delimiter=\"\">3.4 8.9</array>" +
				"</arrayList>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}

	@Test
	public void testEatEmptyLines() {
		String[] strings = new String[] {" C 2 3.4", "", "", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.readLine();
		Assert.assertEquals("read line", 1, jumboReader.getCurrentLineNumber());
		jumboReader.eatEmptyLines();
		Assert.assertEquals("eat empty", 3, jumboReader.getCurrentLineNumber());
	}

	@Test
	public void testHasMoreLines() {
		String[] strings = new String[] {" C 2 3.4", "", "", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		jumboReader.readLine();
		Assert.assertTrue("more", jumboReader.hasMoreLines());
		Assert.assertEquals("read line", 1, jumboReader.getCurrentLineNumber());
		jumboReader.eatEmptyLines();
		Assert.assertTrue("more", jumboReader.hasMoreLines());
		Assert.assertEquals("eat empty", 3, jumboReader.getCurrentLineNumber());
		jumboReader.readLine();
		Assert.assertFalse("no more", jumboReader.hasMoreLines());
	}

	@Test
	public void testPeekLine() {
		String[] strings = new String[] {" C 2 3.4", "", "", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("started", 0, jumboReader.getCurrentLineNumber());
		Assert.assertEquals("peek", " C 2 3.4", jumboReader.peekLine());
		jumboReader.readLine();
		Assert.assertEquals("read line", 1, jumboReader.getCurrentLineNumber());
		Assert.assertEquals("peek", "", jumboReader.peekLine());
		jumboReader.eatEmptyLines();
		Assert.assertTrue("more", jumboReader.hasMoreLines());
		Assert.assertEquals("eat empty", 3, jumboReader.getCurrentLineNumber());
		Assert.assertEquals("peek", " X 7 8.9", jumboReader.peekLine());
		jumboReader.readLine();
		Assert.assertNull("no more", jumboReader.peekLine());
	}

	@Test
	public void testReadLineAsScalar() {
		String[] strings = new String[] {" C 2 3.4", "", "", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLScalar scalar = jumboReader.readLineAsScalar("scalar", false);
		String expectedS = "<scalar dataType=\"xsd:string\" dictRef=\"pref:scalar\" xmlns=\"http://www.xml-cml.org/schema\"> C 2 3.4</scalar>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, scalar, true, 0.000001);
	}

	@Test
	public void testReadLines() {
		String[] strings = new String[] {" C 2 3.4", "AA", "BBBBB", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		List<String> ss = jumboReader.readLines(3);
		Assert.assertEquals("read line", 3, jumboReader.getCurrentLineNumber());
		String[] sss = new String[] {" C 2 3.4", "AA", "BBBBB"};
		JumboTestUtils.assertEquals("readLInes", sss, ss.toArray(new String[0]));
	}

	@Test
	public void testSkipCheckedLinesString() {
		String[] strings = new String[] {" C 2 3.4", "AA", "BBBBB", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("line", 0, jumboReader.getCurrentLineNumber());
		try {
			jumboReader.skipCheckedLines("AA\nBBBBB");
			Assert.fail("should fail to read");
		} catch (Exception e) {
		}
		Assert.assertEquals("line", 0, jumboReader.getCurrentLineNumber());
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getCurrentLineNumber());
		jumboReader.skipCheckedLines("AA\nBBBBB");
		Assert.assertEquals("read line", 3, jumboReader.getCurrentLineNumber());
	}

	@Test
	public void testSkipCheckedLinesStringArray() {
		String[] strings = new String[] {" C 2 3.4", "AA", "BBBBB", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("line", 0, jumboReader.getCurrentLineNumber());
		try {
			jumboReader.skipCheckedLines(new String[]{"AA", "BBBBB"});
			Assert.fail("should fail to read");
		} catch (Exception e) {
		}
		Assert.assertEquals("line", 0, jumboReader.getCurrentLineNumber());
		jumboReader.readLine();
		Assert.assertEquals("line", 1, jumboReader.getCurrentLineNumber());
		jumboReader.skipCheckedLines(new String[]{"AA", "BBBBB"});
		Assert.assertEquals("read line", 3, jumboReader.getCurrentLineNumber());
	}

	@Test
	public void testParseArrayStringStringString() {
		JumboReader jumboReader = new JumboReader();
		CMLElement element = jumboReader.parseArray("(5I2)", " 1 2 3 4 5", CMLConstants.XSD_INTEGER);
		String expectedS = "<array delimiter=\"\" dataType=\"xsd:integer\" size=\"5\" xmlns=\"http://www.xml-cml.org/schema\">1 2 3 4 5</array>";
		JumboTestUtils.assertEqualsIncludingFloat("element", expectedS, element, true, 0.000001);
	}


	@Test
	public void testAppendChild0() {
		JumboReader jumboReader = new JumboReader();
		try {
			jumboReader.appendChild(new CMLScalar());
			Assert.fail("should throw null parent");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testAppendChild1() {
		JumboReader jumboReader = new JumboReader();
		CMLList list = new CMLList();
		jumboReader.setParentElement(list);
		jumboReader.appendChild(new CMLScalar("foo"));
		jumboReader.appendChild(new CMLScalar("bar"));
		String expectedS = "<list xmlns=\"http://www.xml-cml.org/schema\">" +
				"  <scalar dataType=\"xsd:string\">foo</scalar>" +
				"  <scalar dataType=\"xsd:string\">bar</scalar>" +
				"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expectedS, list, true, 0.00001);
	}

	@Test
	public void testAppendChild2() {
		String[] strings = new String[] {" 1 2 3", " 4 5 6"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		CMLList list = new CMLList();
		jumboReader.setParentElement(list);
		jumboReader.parseArray("(3I2)", CMLConstants.XSD_INTEGER, "I.aaa", true);
		String expectedS = 
			"<list xmlns=\"http://www.xml-cml.org/schema\">" +
			"  <array delimiter=\"\" dataType=\"xsd:integer\" size=\"3\" dictRef=\"pref:I.aaa\">1 2 3</array>" +
			"</list>";
		JumboTestUtils.assertEqualsIncludingFloat("list", expectedS, list, true, 0.00001);
	}

	@Test
	public void testResetCurrentLine() {
		String[] strings = new String[] {" C 2 3.4", "", "", " X 7 8.9"};
		JumboReader jumboReader = new JumboReader(getDictionary(), getPrefix(), strings);
		Assert.assertEquals("started", 0, jumboReader.getCurrentLineNumber());
		Assert.assertEquals("peek", " C 2 3.4", jumboReader.peekLine());
		jumboReader.readLine();
		Assert.assertEquals("read line", 1, jumboReader.getCurrentLineNumber());
		Assert.assertEquals("peek", "", jumboReader.peekLine());
		jumboReader.eatEmptyLines();
		Assert.assertTrue("more", jumboReader.hasMoreLines());
		Assert.assertEquals("eat empty", 3, jumboReader.getCurrentLineNumber());
		jumboReader.resetCurrentLine();
		Assert.assertEquals("reset", 1, jumboReader.getCurrentLineNumber());
	}

}
