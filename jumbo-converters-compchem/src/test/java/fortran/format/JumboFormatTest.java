package fortran.format;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class JumboFormatTest {

	@Test
	public void textExpandStrings() {
		String expanded = JumboFormat.expandStrings("I4'bc'F10.3");
		Assert.assertEquals("expand", "(I4,2X,F10.3)", expanded);
	}

	@Test
	public void textParseWithExpandedStrings() {
		List<Object> objects = new JumboFormat().parseFortranLine("(I4'bc'F10.3)", " 123bc  1234.567");
		Assert.assertEquals("expand", 123, Integer.parseInt(objects.get(0).toString()));
		Assert.assertEquals("expand", 1234.567, new Double(objects.get(1).toString()).doubleValue(), 0.000001);
	}

	@Test
	public void textMultiField() {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "('        IVIB=',I4,' IATOM=',I4,' ICOORD=',I4,' E='F20.10)";
		List<Object> results = new JumboFormat().parseFortranLine(format, data);
		Assert.assertEquals("multi", 4, results.size());

	}
	
	@Test
	public void testXAlpha2x() throws Exception {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "(13X,I4,7X,I4,8X,I4,3X,F20.10)";
		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
		List<CMLScalar> scalarList = new JumboFormat().parseToScalars("xyz", format, data, names);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Double(-417.0146230240));
		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
	}

	@Test
	public void testXAlpha2xx() throws Exception {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "'        IVIB='I4' IATOM='I4' ICOORD='I4' E='F20.10";
		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
		List<CMLScalar> scalarList = new JumboFormat().parseToScalars("xyz", format, data, names);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Double(-417.0146230240));
		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
	}


	@Test
	public void testXAlpha2xxx() throws Exception {
		String data = "   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "I4' IATOM='I4' ICOORD='I4' E='F20.10";
		String[] names = {"I.ivib", "I.iatom", "I.icoord", "F.e"};
		List<CMLScalar> scalarList = new JumboFormat().parseToScalars("xyz", format, data, names);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Double(-417.0146230240));
		Examples.assertEqualsScalars("testx", test, scalarList, 0.000000001);
	}

	@Test
	public void testParseToSingleLineArray() {
		String data = " 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05";
		String format = "(5E16.9)";
		CMLArray array = new JumboFormat().parseToSingleLineArray(
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
		JumboFormat jumboFormat = new JumboFormat();
		CMLArray array = jumboFormat.parseMultipleLinesToArray(
				format, lines, lineCount, fieldsToRead, CMLConstants.XSD_DOUBLE);
		CMLArray expected = new CMLArray(new double[] {
		   1.000,  2.000,  3.000,  4.000,  5.000,  6.000,  7.000,  8.000,  9.000, 10.000,
		  11.000, 12.000, 13.000, 14.000, 15.000, 16.000, 17.000, 18.000, 19.000, 20.000,
		  21.000, 22.000, 23.000});
		JumboTestUtils.assertEqualsIncludingFloat("testParseToSingleLineArray", expected, array, true, 0.000001);
		Assert.assertEquals("lines read", 3, jumboFormat.getLinesRead());
	}
		
}
