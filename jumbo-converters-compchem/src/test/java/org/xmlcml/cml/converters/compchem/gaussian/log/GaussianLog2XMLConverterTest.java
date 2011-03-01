package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.euclid.Util;

public class GaussianLog2XMLConverterTest {

	@Test
	public void test1() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(
				"org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml");
		Element templateXML = new Builder().build(templateStream).getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(
		        "compchem/gaussian/log/in/test1.log");
		GaussianLog2XMLConverter glc = new GaussianLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/gaussian/test1.xml");
		glc.convert(inputStream, out);
	}
	
	@Test
	public void testFrameMini() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(
				"org/xmlcml/cml/converters/compchem/gaussian/log/templateListFrame.xml");
		Element templateXML = new Builder().build(templateStream).getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(
		        "compchem/gaussian/log/examples/hashpmini.log");
		GaussianLog2XMLConverter glc = new GaussianLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/gaussian/hashpmini.xml");
		glc.convert(inputStream, out);
	}
	
	@Test
	public void testFrame() throws Exception {
		InputStream templateStream = Util.getInputStreamFromResource(
				"org/xmlcml/cml/converters/compchem/gaussian/log/templateListFrame.xml");
		Element templateXML = new Builder().build(templateStream).getRootElement();
		InputStream inputStream = Util.getInputStreamFromResource(
		        "compchem/gaussian/log/examples/hashp.log");
		GaussianLog2XMLConverter glc = new GaussianLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/gaussian/hashp.xml");
		glc.convert(inputStream, out);
	}
}
