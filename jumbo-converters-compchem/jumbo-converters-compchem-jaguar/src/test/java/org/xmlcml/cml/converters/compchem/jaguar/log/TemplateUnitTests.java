package org.xmlcml.cml.converters.compchem.jaguar.log;

import nu.xom.Element;



import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateUnitTests {
	
  private static final String CODE_BASE = "jaguar";
  private static final String FILE_TYPE = "log";
  private static final String BASE_DIR = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
  private static final String TEMPLATE_DIR = 
    BASE_DIR+"templates/";
  static String INPUT_TEMPLATE_S = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/topTemplate.xml";
//  static String BASE_URI = "classpath:/"+BASE_DIR;
  static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
  
  @Test
  public void dummy() {
    
  }
  
  @Test
  @Ignore
  public void templateTesterEXAMPLE() {
    TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
  }


  @Test public void testBasis()                               {runTemplateTest("basis");}
  @Test public void testCh()                                  {runTemplateTest("ch");}
  @Test public void testDynmem()                              {runTemplateTest("dynmem");}
  @Test public void testFooter()                              {runTemplateTest("footer");}
  @Test public void testFukui()                               {runTemplateTest("fukui");}
  @Test public void testGrid()                                {runTemplateTest("grid");}
  @Test public void testInpgeom()                             {runTemplateTest("inpgeom");}
  
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
