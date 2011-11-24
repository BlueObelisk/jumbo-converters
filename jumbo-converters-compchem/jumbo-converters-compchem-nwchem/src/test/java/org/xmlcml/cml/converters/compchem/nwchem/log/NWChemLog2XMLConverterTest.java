package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.text.ClassPathXIncludeResolver;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.euclid.Util;

public class NWChemLog2XMLConverterTest {

	private static String URI_BASE = ClassPathXIncludeResolver.createClasspath(NWChemLog2XMLConverterTest.class);

	@Test
	public void test1() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(URI_BASE+"templates/topTemplate.xml");
		Element templateXML = new Builder().build(templateStream, "classpath:"+URI_BASE+"templates/").getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(URI_BASE+"in/ch3f.log");
		Text2XMLTemplateConverter glc = new NWChemLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/nwchem/test1.xml");
		glc.convert(inputStream, out);
		Assert.assertTrue(out.exists());
	}
	
//	@Test
//	@Ignore // needs base URI
//	public void testFrame() throws Exception {
//		InputStream templateStream = Util.getInputStreamFromResource(
//				"org/xmlcml/cml/converters/compchem/nwchem/log/templateList.xml");
//		Element templateXML = new Builder().build(templateStream).getRootElement();
//		InputStream inputStream = Util.getInputStreamFromResource(
//            "compchem/nwchem/log/misc/hashp.log");
//		InputStream refStream = Util.getInputStreamFromResource(
//            "compchem/nwchem/log/misc/hashp.xml");
//		CompchemConverter glc = new NWChemLog2XMLConverter(templateXML);
//        File out = new File(new File("."), "target/test/compchem/nwchem/hashp.xml");
//		glc.convert(inputStream, out);
//		JumboTestUtils.assertEqualsCanonically("hashp", 
//				new Builder().build(refStream).getRootElement(), 
//				new Builder().build(out).getRootElement(),
//				true);
//	}
}
