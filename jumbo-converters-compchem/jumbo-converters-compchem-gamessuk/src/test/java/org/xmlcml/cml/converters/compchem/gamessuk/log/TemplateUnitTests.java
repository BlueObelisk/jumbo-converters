package org.xmlcml.cml.converters.compchem.gamessuk.log;

import nu.xom.Element;


import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateUnitTests {
	
  private static final String CODE_BASE = "gamessuk";
  private static final String FILE_TYPE = "log";
  private static final String BASE_DIR = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
  private static final String TEMPLATE_DIR = 
    BASE_DIR+"templates/";
  static String INPUT_TEMPLATE_S = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/topTemplate.xml";
  static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
  
  @Test
  public void dummy() {
    
  }
  
  @Test
  @Ignore
  public void templateTesterEXAMPLE() {
    TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
  }

   @Test public void testCoordsInitcoordOK()                  {runTemplateTest("coords.initcoord");}
   @Ignore @Test public void testcoordsStartupEXAMPLE()                     {runTemplateTest("coords.startup");}
   @Ignore @Test public void testcoordsSymzmat1EXAMPLE()                     {runTemplateTest("coords.symzmat1");}
   @Ignore @Test public void testcoordsSymzmat2EXAMPLE()                     {runTemplateTest("coords.symzmat2");}
   @Ignore @Test public void testcoordsEXAMPLE()                             {runTemplateTest("coords");}
   @Ignore @Test public void testfailchunkerEXAMPLE()                     {runTemplateTest("failchunker");}
   @Ignore @Test public void testheaderGamessukEXAMPLE()                     {runTemplateTest("header.gamessuk");}
   @Ignore @Test public void testheaderGamessuk0EXAMPLE()                     {runTemplateTest("header.gamessuk0");}
   @Ignore @Test public void testheaderHostnameEXAMPLE()                     {runTemplateTest("header.hostname");}
   @Ignore @Test public void testheaderInputechoEXAMPLE()                     {runTemplateTest("header.inputecho");}
   @Ignore @Test public void testheaderEXAMPLE()                     {runTemplateTest("header");}
   @Ignore @Test public void testmolbasisBasisnameEXAMPLE()                     {runTemplateTest("molbasis.basisname");}
   @Ignore @Test public void testmolbasisBasissetEXAMPLE()                     {runTemplateTest("molbasis.basisset");}
   @Ignore @Test public void testmolbasisContractprimEXAMPLE()                     {runTemplateTest("molbasis.contractprim");}
   @Ignore @Test public void testmolbasisGeoEXAMPLE()                     {runTemplateTest("molbasis.geo");}
   @Ignore @Test public void testmolbasisJobeffectEXAMPLE()                     {runTemplateTest("molbasis.jobeffect");}
   @Ignore @Test public void testmolbasisScfconvergeEXAMPLE()                     {runTemplateTest("molbasis.scfconverge");}
   @Ignore @Test public void testmolbasisShellsEXAMPLE()                     {runTemplateTest("molbasis.shells");}
   @Ignore @Test public void testmolbasisSummaryEXAMPLE()                     {runTemplateTest("molbasis.summary");}
   @Ignore @Test public void testmolbasisEXAMPLE()                     {runTemplateTest("molbasis");}
   @Ignore @Test public void testmolbasisxxTempEXAMPLE()                     {runTemplateTest("molbasisxx.temp");}
   @Ignore @Test public void testmolsymEXAMPLE()                     {runTemplateTest("molsym");}
   @Ignore @Test public void testshellsEXAMPLE()                     {runTemplateTest("shells");}  
   
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
