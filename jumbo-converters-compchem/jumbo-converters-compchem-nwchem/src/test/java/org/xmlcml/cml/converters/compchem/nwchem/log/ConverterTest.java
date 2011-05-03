package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.compchem.CompchemTemplateConverter;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.euclid.Util;

public class ConverterTest {
	private static Logger LOG = Logger.getLogger(ConverterTest.class);

	private static final String IN_SUFFIX = ".in";
	private static final String XML_SUFFIX = ".xml";

	private String codeType = "nwchem";
	private String fileType = "log";

	@Test
	public void dummy() {
		
	}
	
	// tests fail because of whitespace somewhere. Needs an evening's work
	@Ignore
	@Test   public void testInternuc()        {testConverter("internuc");}
	@Ignore
	@Test   public void testInternucang()     {testConverter("internucang");}
	@Ignore
	@Test   public void testMomint()          {testConverter("momint");}
	@Ignore // fails
	@Test   public void testNonvar()          {testConverter("nonvar");}

    @Test
    @Ignore // need whitespace comparison
    public void nwchemOut2XML() {
		TemplateConverter converter = createConverter("org/xmlcml/cml/converters/compchem/nwchem/log/templateList.xml");
        RegressionSuite.run("compchem/nwchem/log", "out", "xml", converter, true);
    }
    
    @Test
    @Ignore
    public void sea36FailingTestTODO() throws IOException {
	    File file = new File("src/test/resources/compchem/nwchem/log/in/test2.out");
	
	    String templateXML = "org/xmlcml/cml/converters/compchem/nwchem/log/topTemplate.xml";
	    InputStream templateStream = Util.getInputStreamFromResource(templateXML);
	    // this creates CMLElements as children
	    TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(templateStream, "nwchem", "log");
	
	    CMLModule module = (CMLModule) tc.convertToXML(file);
	
	    Elements modules = module.getChildCMLElements("module");
	    for (int i = 0; i < modules.size(); i++) {
	    	Assert.assertEquals("class", CMLElement.class, modules.get(i).getClass());
	    }
    }
    
    @Test
    @Ignore
    public void sea36FailingTest1TODO() throws IOException {
	    File file = new File("src/test/resources/compchem/nwchem/log/in/test2.out");
	
	    String templateXML = "org/xmlcml/cml/converters/compchem/nwchem/log/topTemplate.xml";
	    InputStream templateStream = Util.getInputStreamFromResource(templateXML);
	    // this creates CMLElements as children
	    TemplateConverter tc = CompchemTemplateConverter.createTemplateConverter(templateStream, "nwchem", "log");
	
	    CMLModule module0 = (CMLModule) tc.convertToXML(file);
	    CMLModule module = (CMLModule) CMLUtil.parseCML(module0.toXML());
	
	    Elements modules = module.getChildCMLElements("module");
	    Assert.assertEquals("child", 4, modules.size());
	    for (int i = 0; i < modules.size(); i++) {
	    	Assert.assertEquals("class", CMLModule.class, modules.get(i).getClass());
	    }
    }
    // ==============================================================
    
	private void testConverter(String name) {
		String templateXML = "org/xmlcml/cml/converters/compchem/nwchem/log/templateListAll.xml";
		TemplateConverter converter = createConverter(templateXML);
		TestUtils.runConverterTest(converter, 
				"compchem/nwchem/log/templates/"+name+IN_SUFFIX, 
				"compchem/nwchem/log/templates/"+name+XML_SUFFIX,
				true);
	}
		
	private TemplateConverter createConverter(String templateXML) {
		TemplateConverter converter = null;
		try {
			InputStream templateStream = Util.getInputStreamFromResource(templateXML);
			converter = CompchemTemplateConverter.createTemplateConverter(templateStream, "nwchem", "log");
		} catch (Exception e) {
			throw new RuntimeException("Cannot make template ", e);
		}
		return converter;
	}

}
