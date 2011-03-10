package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class GaussianLog2XMLConverterTest {

	@Ignore
	@Test
	public void test1() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(
				"org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml");
		Element templateXML = new Builder().build(templateStream).getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(
		        "compchem/gaussian/log/in/test1.log");
		TemplateConverter glc = new GaussianLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/gaussian/test1.xml");
		glc.convert(inputStream, out);
	}
	
	@Test
	@Ignore // needs base URI
	public void testFrame() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(
				"org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml");
		Element templateXML = new Builder().build(templateStream).getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(
            "compchem/gaussian/log/misc/hashp.log");
		InputStream refStream = Util.getInputStreamFromResource(
            "compchem/gaussian/log/misc/hashp.xml");
		TemplateConverter glc = new GaussianLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/gaussian/hashp.xml");
		glc.convert(inputStream, out);
		JumboTestUtils.assertEqualsCanonically("hashp", 
				new Builder().build(refStream).getRootElement(), 
				new Builder().build(out).getRootElement(),
				true);
	}
}
