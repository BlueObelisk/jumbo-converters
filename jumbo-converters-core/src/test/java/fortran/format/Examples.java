package fortran.format;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.xmlcml.cml.element.CMLScalar;

public class Examples {

	@Test
	public void testInteger() throws Exception {
		String data = "123";
		String format = "(I3)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(123));
		assertEquals("integer", test, results, 0.00001);
	}

	@Test
	public void testDouble() throws Exception {
		String data = "123.3";
		String format = "(F5.1)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Double(123.3));
		assertEquals("double", test, results, 0.000000001);
	}

	@Test
	public void testIntegerDouble() throws Exception {
		String data = " 23 23.3";
		String format = "(I3,F5.1)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(23));
		test.add(new Double(23.3));
		assertEquals("integer double", test, results, 0.000000001);
	}

	@Test
	public void testIntegerDoubles() throws Exception {
		String data = " 23 23.3-11.3";
		String format = "(I3,2F5.1)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(23));
		test.add(new Double(23.3));
		test.add(new Double(-11.3));
		assertEquals("integer double double", test, results, 0.000000001);
	}

	@Test
	public void testString() throws Exception {
		String data = "Hello World!";
		String format = "(A12)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add("Hello World!");
		assertEquals("string", test, results, 0.000000001);
	}

	@Test
	public void testBrackets() throws Exception {
		String data = " 12 23.4 11.4 23 67.8 34.8";
		String format = "(2( I3, 2F5.1))";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(12));
		test.add(new Double(23.4));
		test.add(new Double(11.4));
		test.add(new Integer(23));
		test.add(new Double(67.8));
		test.add(new Double(34.8));
		assertEquals("brackets", test, results, 0.000000001);
	}

	@Test
	public void testSpaces() throws Exception {
		String data = " 12AB  1.2";
		String format = "(I3,2X,F5.1)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(12));
		test.add(new Double(1.2));
		assertEquals("spaces", test, results, 0.000000001);
	}

	@Test
	public void testError() throws Exception {
		String data = " 12ABC 1.2";
		String format = "(I3,2X,F5.1)";
		try {
			List<Object> results = FortranFormat.read(data, format);
			Assert.fail("should throw");
		} catch (Exception e) {
			Assert.assertTrue("error", true);
		}
	}

	@Test
	public void testLines() throws Exception {
		String data = " 12\n 23";
		String format = "(I3/I3)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(12));
		test.add(new Integer(23));
		assertEquals("lines", test, results, 0.000000001);
	}

	@Test
	public void testUnderrun() throws Exception {
		String data = " 12 23";
		String format = "(I3)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(12));
		assertEquals("underrun", test, results, 0.000000001);
	}

	@Test
	public void testScientific() throws Exception {
		String data = " 0.123E+01";
		String format = "(F10.2)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Double(1.23));
		assertEquals("scientific", test, results, 0.000000001);
	}

	@Test
	public void testBlankInteger() throws Exception {
		String data = "   ";
		String format = "(I3)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(null);
		assertEquals("blank integer", test, results, 0.000000001);
	}

	@Test
	public void testBlankDouble() throws Exception {
		String data = "     ";
		String format = "(F5.1)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(null);
		assertEquals("blank double", test, results, 0.000000001);
	}

	@Test
	public void testStars() throws Exception {
		String data = "***";
		String format = "(I3)";
		try {
			List<Object> results = FortranFormat.read(data, format);
			Assert.fail("should throw");
		} catch (Exception e) {
			Assert.assertTrue("error", true);
		}
	}

	@Test
	public void testAlpha() throws Exception {
		String data = "Hello 3";
		String format = "('Hello',I2)";
		ArrayList<Object> input = new ArrayList<Object>();
		input.add(7);
		String output = null;
		boolean shouldWork = false; // maybe this is not implemented
		try {
			output = FortranFormat.write(input, format);
			Assert.assertTrue("works", shouldWork);
			Assert.assertEquals("alpha", "Hello 7", output);
//			List<Object> results = FortranFormat.read(data, format);
//			List<Object> test = new ArrayList<Object>();
//			test.add(new Integer(3));
//			assertEquals("alpha", test, results, 0.000000001);
		} catch (Exception e) {
			Assert.assertFalse("should not work", shouldWork);
		}
	}

	@Test
	public void testFortranFormat() throws Exception {
		String data = "     ";
		String format = "(F5.1)";
		FortranFormat fortranFormat = new FortranFormat(format);
		List<Object> results = fortranFormat.parse(data);
		List<Object> test = new ArrayList<Object>();
		test.add(null);
		assertEquals("blank double null", test, results, 0.000000001);
		
		fortranFormat.getOptions().setReturnZeroForBlanks(true);
		results = fortranFormat.parse(data);
		test = new ArrayList<Object>();
		test.add(new Double(0.0));
		assertEquals("blank double", test, results, 0.000000001);
	}
	
	@Test
	public void testX() throws Exception {
		String data = "  1223";
		String format = "(2X,I4)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(1223));
		assertEquals("testx", test, results, 0.000000001);
	}

	@Test
	public void testXAlpha() throws Exception {
		String data = "PQ1227";
		String format = "(2X,I4)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(1227));
		assertEquals("testx", test, results, 0.000000001);
	}

	@Test
	public void testXAlpha2() throws Exception {
		String data = "        IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240";
		String format = "(13X,I4,7X,I4,8X,I4,3X,F20.10)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Integer(0));
		test.add(new Double(-417.0146230240));
		assertEquals("testx", test, results, 0.000000001);
	}

	@Test
	public void testRepeat() throws Exception {
		String data = " 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05";
		String format = "(5E16.9)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Double(7.799249640E-06));
		test.add(new Double(-1.877789578E-08));
		test.add(new Double(9.009626448E-06));
		test.add(new Double(5.782921410E-06));
		test.add(new Double(1.610009308E-05));
		assertEquals("testx", test, results, 0.000000001);
	}
	
	@Test
	public void testUnfilledRepeat() throws Exception {
		String data = " 7.799249640E-06-1.877789578E-08 9.009626448E-06";
		String format = "(5E16.9)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Double(7.799249640E-06));
		test.add(new Double(-1.877789578E-08));
		test.add(new Double(9.009626448E-06));
		test.add(null);
		test.add(null);
		assertEquals("testx", test, results, 0.000000001);
	}
	
	@Test
	public void testUnfilledRepeatLines() throws Exception {
		String data =
" 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05\n"+
"-6.564999713E-06 1.749591384E-05 2.550531014E-06";
		String format = "(5E16.9/5E16.9)";
		List<Object> results = FortranFormat.read(data, format);
		List<Object> test = new ArrayList<Object>();
		test.add(new Double(7.799249640E-06));
		test.add(new Double(-1.877789578E-08));
		test.add(new Double(9.009626448E-06));
		test.add(new Double(5.782921410E-06));
		test.add(new Double(1.610009308E-05));
		test.add(new Double(-6.564999713E-06));
		test.add(new Double(1.749591384E-05));
		test.add(new Double(2.550531014E-06));
		test.add(null);
		test.add(null);
		assertEquals("testx", test, results, 0.000000001);
	}
	
//	 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05
//	 -6.564999713E-06 1.749591384E-05 2.550531014E-06


	
	// ====================================================== //
	public static void assertEquals(String msg, List<Object> test, List<Object> results, double eps) {
		Assert.assertNotNull(msg+" test list", test);
		Assert.assertNotNull(msg+" results list", results);
		Assert.assertEquals(msg, test.size(), results.size());
		for (int i = 0; i < test.size(); i++) {
			Object testObj = test.get(i);
			Object resultsObj = results.get(i);
			if (testObj == null) {
				Assert.assertNull(msg+" null", resultsObj);
			} else {
				Assert.assertNotNull(msg+" resultsObj should not be null", resultsObj);
				Assert.assertEquals(msg+"("+i+")", testObj.getClass(), resultsObj.getClass());
				if (testObj instanceof Double) {
					Assert.assertEquals(msg+"("+i+")", (Double)testObj, (Double)resultsObj, eps);
				} else {
					Assert.assertEquals(msg+"("+i+")", testObj, resultsObj);
				}
			}
		}
	}
	
	public static void assertEqualsScalars(String msg, List<Object> test, List<CMLScalar> results, double eps) {
		Assert.assertNotNull(msg+" test list", test);
		Assert.assertNotNull(msg+" results list", results);
		Assert.assertEquals(msg, test.size(), results.size());
		for (int i = 0; i < test.size(); i++) {
			CMLScalar scalar = results.get(i);
			Class clazz = scalar.getDataTypeClass();
			Object testObj = test.get(i);
			if (testObj == null) {
				Assert.assertNull(msg+" null", scalar);
			} else {
				Assert.assertNotNull(msg+" resultsObj ", scalar);
				Assert.assertEquals(msg+"("+i+")", testObj.getClass(), clazz);
				if (testObj instanceof Double) {
					Assert.assertEquals(msg+"("+i+")", (Double)testObj, scalar.getDouble(), eps);
				} else if (testObj instanceof String){
					Assert.assertEquals(msg+"("+i+")", testObj.toString(), scalar.getXMLContent());
				} else if (testObj instanceof Integer){
					Assert.assertEquals(msg+"("+i+")", ((Integer)testObj).intValue(), scalar.getInt());
				} else if (testObj instanceof Boolean){
					Assert.assertTrue(msg+"("+i+")", ((Boolean)testObj).booleanValue() == scalar.getBoolean());
				} else if (testObj instanceof DateTime){
//					Assert.assertEquals(msg+"("+i+")", ((DateTime)testObj)., scalar.getBoolean());
				} else {
					//;
				}
			}
		}
	}
}
