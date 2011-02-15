package org.xmlcml.cml.converters.filter;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealRange;

public class XPathFilterTest {
	@Test
	public void nodeCountTest1() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:double'>1.0</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "//*", 1);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest2() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", 2);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest2a() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", 3);
		Assert.assertFalse(filter.accept(element));
	}


	@Test
	public void nodeCountTest3() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", new IntRange(1, 3));
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest3a() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", new IntRange(0, 1));
		Assert.assertFalse(filter.accept(element));
	}


	@Test
	public void nodeCountTest4() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", 2.0);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest4aa() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", 2.0001);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest4a() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", new RealRange(1.9, 2.1));
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void nodeCountTest4b() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema'><molecule/><molecule/></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.NODE_COUNT, "/*/*", new RealRange(2.1, 2.3));
		Assert.assertFalse(filter.accept(element));
	}
	
	// ===================================================

	@Test
	public void singleNodeValueTest1() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>1</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "//*/text()", 1);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest1a() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'><scalar>1</scalar></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "//*/text()", 1);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest1b() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'></cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "//*/text()", 1);
		Assert.assertFalse(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest1c() {
		String test = "<cml xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>" +
				"<scalar>1</scalar><scalar>1</scalar>" +
				"</cml>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "//*/text()", 1);
		Assert.assertFalse(filter.accept(element));
	}


	@Test
	public void singleNodeValueTest2() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:double'>2.1</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", 2.1);
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest2a() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:double'>2.1</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", 2.2);
		Assert.assertFalse(filter.accept(element));
	}


	@Test
	public void singleNodeValueTest3() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>2</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", new IntRange(1, 3));
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest3a() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>2</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", new IntRange(0, 1));
		Assert.assertFalse(filter.accept(element));
	}


	@Test
	public void singleNodeValueTest4a() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>2</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", new RealRange(1.9, 2.1));
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest4b() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:integer'>2</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", new RealRange(2.1, 2.3));
		Assert.assertFalse(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest5() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:string'>YES</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", "YES");
		Assert.assertTrue(filter.accept(element));
	}

	@Test
	public void singleNodeValueTest5a() {
		String test = "<scalar xmlns='http://www.xml-cml.org/schema' dataType='xsd:string'>NO</scalar>";
		CMLElement element = CMLUtil.parseQuietlyIntoCML(new ByteArrayInputStream(test.getBytes()));
		XPathFilter filter = new XPathFilter(XPathFilter.SINGLE_NODE_VALUE, "/*/text()", "YES");
		Assert.assertFalse(filter.accept(element));
	}


}
