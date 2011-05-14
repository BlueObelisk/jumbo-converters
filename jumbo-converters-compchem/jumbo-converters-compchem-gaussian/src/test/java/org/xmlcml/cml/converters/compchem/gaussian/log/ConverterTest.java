package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.euclid.Util;

public class ConverterTest {

/*
	private static Logger LOG = Logger.getLogger(ConverterTest.class);

	private static final String IN_SUFFIX = ".in";
	private static final String XML_SUFFIX = ".xml";

	private String codeType = "gaussian";
	private String fileType = "log";
	
//	@Test   public void test101zmat()          {testConverter("l101","zmat");}
	@Ignore
	@Test   public void testAnisospin()        {testConverter("anisospin");}
	@Ignore
	@Test   public void testAtomicCharges()    {testConverter("atomiccharges");}
	@Test   public void testCoordinates()      {testConverter("coord");}
	@Ignore
	@Test   public void testMulliken()         {testConverter("mulliken");}
	@Ignore // still some bugs
	@Test   public void testl716()             {testConverter("l716");}

    @Test
	@Ignore
    public void gaussianOut2XML() {
		TemplateConverter converter = createConverter("org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml");
        RegressionSuite.run("compchem/gaussian/log", "log", "xml", converter);
    }
*/
   
//	private void testConverter(String link, String name) {
//		TemplateConverter converter = createConverter("templateListAll.xml");
//		LOG.debug("created converter");
////		TestUtils.runConverterTest(converter, link+"/"+name);
//		// later turn this to subdirectory after we separate packages
//		TestUtils.runConverterTest(converter, link+"."+name, null/*FIXME*/);
//	}

/*		
	private void testConverter(String name) {
		String templateXML = "org/xmlcml/cml/converters/compchem/gaussian/log/templateListAll.xml";
		TemplateConverter converter = createConverter(templateXML);
		TestUtils.runConverterTest(converter, 
				"compchem/gaussian/log/templates/"+name+IN_SUFFIX, 
				"compchem/gaussian/log/templates/"+name+XML_SUFFIX);
	}
		
	private TemplateConverter createConverter(String templateXML) {
		TemplateConverter converter = null;
		try {
			InputStream templateStream = Util.getInputStreamFromResource(templateXML);
			converter = CompchemTemplateConverter.createTemplateConverter(templateStream, "gaussian", "log");
		} catch (Exception e) {
			throw new RuntimeException("Cannot make template ", e);
		}
		return converter;
	}
*/

}
