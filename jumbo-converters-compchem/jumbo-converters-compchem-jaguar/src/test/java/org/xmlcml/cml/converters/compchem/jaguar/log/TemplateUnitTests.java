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
  @Test public void testEndprog()                             {runTemplateTest("endprog");}
  @Test public void testFooter()                              {runTemplateTest("footer");}
  @Test public void testFukui()                               {runTemplateTest("fukui");}
  @Test public void testGrid()                                {runTemplateTest("grid");}
  @Test public void testGridDetails()                         {runTemplateTest("grid.details");}
  @Test public void testGridGridset()                         {runTemplateTest("grid.gridset");}
  @Test public void testHeader()                              {runTemplateTest("header");}
  @Test public void testHfig()                                {runTemplateTest("hfig");}
  @Test public void testHfigIrrep()                           {runTemplateTest("hfig.irrep");}
  @Test public void testHfigWave()                            {runTemplateTest("hfig.wave");}
  @Test public void testInpgeom()                             {runTemplateTest("inpgeom");}
  @Test public void testJob()                                 {runTemplateTest("job");}
  @Test public void testMolwt()                               {runTemplateTest("molwt");}
  @Test public void testMomint()                              {runTemplateTest("momint");}
  @Test public void testNondefault()                          {runTemplateTest("nondefault");}
  @Test public void testNucrep()                              {runTemplateTest("nucrep");}
  @Test public void testNumbasis()                            {runTemplateTest("numbasis");}
  @Test public void testOnee()                                {runTemplateTest("onee");}
  @Test public void testPre()                                 {runTemplateTest("pre");}
  @Test public void testProbe()                               {runTemplateTest("probe");}
  @Test public void testProginfo()                            {runTemplateTest("proginfo");}
  @Test public void testRotconst()                            {runTemplateTest("rotconst");}
  @Test public void testRwr()                                 {runTemplateTest("rwr");}
  @Ignore @Test public void testScfWHITE()                    {runTemplateTest("scf");}
  @Test public void testScfEnergy()                           {runTemplateTest("scf.energy");}
  @Test public void testScfEners()                            {runTemplateTest("scf.eners");}
  @Test public void testScfHomolumo()                         {runTemplateTest("scf.homolumo");}
  @Test public void testScfIudig()                            {runTemplateTest("scf.iudig");}
  @Test public void testScfNumbers()                          {runTemplateTest("scf.numbers");}
  @Test public void testScfOrbeners()                         {runTemplateTest("scf.orbeners");}
  @Test public void testScfType()                             {runTemplateTest("scf.type");}
  @Test public void testScfType1()                            {runTemplateTest("scftype");}
  @Test public void testStoich()                              {runTemplateTest("stoich");}
  @Test public void testTimings()                             {runTemplateTest("timings");}
  
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }


}
