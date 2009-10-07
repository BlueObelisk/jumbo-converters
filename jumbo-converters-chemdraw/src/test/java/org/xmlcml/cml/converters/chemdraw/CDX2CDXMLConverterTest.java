package org.xmlcml.cml.converters.chemdraw;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.testutil.TestUtils;


public class CDX2CDXMLConverterTest {
	
	public final String getLocalDirName() {
		return "cdx/cdx";
	}
	public final String getInputSuffix() {
		return "cdx";
	}
	public final String getOutputSuffix() {
		return "cdx.xml";
	}
	
	@Test
	@Ignore // why?
	public void testConvertToXMLElement() throws IOException {
		
		CDX2CDXMLConverter c = new CDX2CDXMLConverter();
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("cdx/r19.cdx");
		File out = File.createTempFile("r19", ".cdx.xml");
		c.convert(in, out);
		InputStream expectedFile = getClass().getClassLoader().getResourceAsStream("cdxml/r19Ref.cdx.xml");
		//Assert.assertTrue("file contents", FileUtils.contentEquals(out, expectedFile));
		
		Element cmlRef = null;
		Element cml = null;
		try {
			cmlRef = new CMLBuilder().build(expectedFile).getRootElement();
			cml = new CMLBuilder().build(new FileInputStream(out)).getRootElement();
		} catch (ValidityException e) {
			throw new RuntimeException("BUG: "+e);
		} catch (ParsingException e) {
			throw new RuntimeException("BUG: "+e);
		}
		TestUtils.assertEqualsCanonically("cml file", cmlRef, cml, true);
	}
}
