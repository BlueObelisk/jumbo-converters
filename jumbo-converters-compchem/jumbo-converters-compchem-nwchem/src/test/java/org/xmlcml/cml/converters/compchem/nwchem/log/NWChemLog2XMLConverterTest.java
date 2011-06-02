package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.euclid.Util;

public class NWChemLog2XMLConverterTest {

	@Test
	public void test1() throws Exception {
//		InputStream templateStream = Util.getInputStreamFromResource(
//				"org/xmlcml/cml/converters/compchem/nwchem/log/templates/topTemplate.xml");
//		Element templateXML = new Builder().build(templateStream).getRootElement();
		NWChemLog2XMLConverter converter = new NWChemLog2XMLConverter();
		InputStream inputStream = new FileInputStream(
        "src/test/resources/compchem/nwchem/log/markjohn/fukuilite.log");
//		Text2XMLTemplateConverter glc = new NWChemLog2XMLConverter(templateXML);
        File out = new File(new File("."), "target/test/compchem/nwchem/log/markjohn/fukuilite.xml");
		converter.convert(inputStream, out);
	}
	
}
