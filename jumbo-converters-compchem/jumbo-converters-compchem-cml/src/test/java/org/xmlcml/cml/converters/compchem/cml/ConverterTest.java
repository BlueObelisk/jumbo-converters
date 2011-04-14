package org.xmlcml.cml.converters.compchem.cml;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.euclid.Util;

public class ConverterTest {
	private static Logger LOG = Logger.getLogger(ConverterTest.class);

	private static final String IN_SUFFIX = ".out";
	private static final String XML_SUFFIX = ".xml";

	private String codeType = "jaguar";
	private String fileType = "log";

	@Test
	public void dummy() {
		
	}
	
	// tests fail because of whitespace somewhere. Needs an evening's work
	@Ignore
	@Test   public void testInternuc()        {testConverter("internuc");}

    @Test
    @Ignore // need whitespace comparison
    public void jaguarOut2XML() {
		TemplateConverter converter = createConverter("org/xmlcml/cml/converters/compchem/"+codeType+"/"+fileType+"/topTemplate.xml");
        RegressionSuite.run("compchem/"+codeType+"/"+fileType, "last", "xml", converter, true);
    }
   
	private void testConverter(String name) {
		String templateXML = "org/xmlcml/cml/converters/compchem/"+codeType+"/"+fileType+"/topTemplate.xml";
		TemplateConverter converter = createConverter(templateXML);
		TestUtils.runConverterTest(converter, 
				"compchem/"+codeType+"/"+fileType+"/templates/"+name+IN_SUFFIX, 
				"compchem/"+codeType+"/"+fileType+"/templates/"+name+XML_SUFFIX,
				true);
	}
		
	private TemplateConverter createConverter(String templateXML) {
		TemplateConverter converter = null;
		try {
			InputStream templateStream = Util.getInputStreamFromResource(templateXML);
			converter = CompchemTemplateConverter.createTemplateConverter(templateStream, codeType, fileType);
		} catch (Exception e) {
			throw new RuntimeException("Cannot make template ", e);
		}
		return converter;
	}

}
