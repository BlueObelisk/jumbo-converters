package org.xmlcml.cml.converters.compchem.nwchem.log;

import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

public class TemplateTest {
  private static final String CODE_BASE = "nwchem";
  private static final String FILE_TYPE = "log";
  private static final String BASE_DIR = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
  private static final String TEMPLATE_DIR = 
    BASE_DIR+"templates/";
  static String INPUT_TEMPLATE_S = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/topTemplate.xml";
  static String BASE_URI = "classpath:/"+BASE_DIR;
  
  @Test
  public void dummy() {
    
  }
  
  @Test
  @Ignore
  public void templateTesterEXAMPLE() {
    TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
  }

  
  @Test   public void testAcknow()                             {runTemplateTest("acknow");}
  @Test  public void testAlphabeta()                           {runTemplateTest("alphabeta");}
//  @Test  public void testAlphabeta1()                          {runTemplateTest("alphabeta1");}
  @Test  public void testAlphaeigs()                           {runTemplateTest("alphaeigs");}
  @Test  public void testAlphafoccs()                           {runTemplateTest("alphafoccs");}
  @Test  public void testArgument()                           {runTemplateTest("argument");}
  @Test  public void testAtmass()                             {runTemplateTest("atmass");}
  @Test   @Ignore public void testAtombasisINCLUDE()                          {runTemplateTest("atombasis");}
  @Test   @Ignore public void testAuthorsEXAMPLE()                            {runTemplateTest("authors");}
  @Test   @Ignore public void testBasisINCLUDE()                              {runTemplateTest("basis");}
  @Test  public void testBrillouinzp()                        {runTemplateTest("brillouinzp");}
  @Test   @Ignore public void testCcsdtTBD()                              {runTemplateTest("ccsdt");}
//  @Test   public void testCenter23()                           {runTemplateTest("center23");}
  @Test  @Ignore public void testCenterOfChargeEXAMPLE()              {runTemplateTest("centerofcharge");}
  @Test   @Ignore public void testCentermassEXAMPLE()                         {runTemplateTest("centermass");}
  @Test   @Ignore public void testCitationEXAMPLE()                           {runTemplateTest("citation");}
//  @Test   public void testConverge()                           {runTemplateTest("converge");}
  @Test   @Ignore public void testCphfEXAMPLE()                               {runTemplateTest("cphf");}
  @Test   @Ignore public void testDirectmp2EXAMPLE()                          {runTemplateTest("directmp2");}
  @Test   @Ignore public void testDirinfoEXAMPLE()                            {runTemplateTest("dirinfo");}
  @Test   @Ignore public void testDftEXAMPLE()                                {runTemplateTest("dft");}
//  @Test   public void testDftvector()                          {runTemplateTest("dftvector");}
//  @Test   public void testEigen()                              {runTemplateTest("eigen");}
  @Test   @Ignore public void testFinalrhfEXAMPLE()                           {runTemplateTest("finalrhf");}
  @Test   @Ignore public void testFourindexEXAMPLE()                          {runTemplateTest("fourindex");}
  @Test   @Ignore public void testGastatsEXAMPLE()                            {runTemplateTest("gastats");}
  @Test   @Ignore public void testGeninfoEXAMPLE()                            {runTemplateTest("geninfo");}
  @Test   @Ignore public void testGeomEXAMPLE()                               {runTemplateTest("geom");}
//  @Test   public void testGrid()                               {runTemplateTest("grid");}
//    @Test   public void testIgnore()                             {runTemplateTest("ignore");}
    @Test   @Ignore public void testJobEXAMPLE()                                {runTemplateTest("job");}
  @Test   @Ignore public void testMemoryEXAMPLE()                             {runTemplateTest("memory");}
  @Test   @Ignore public void testMomintEXAMPLE()                             {runTemplateTest("momint");}
  @Test   @Ignore public void testMp2energyEXAMPLE()                          {runTemplateTest("mp2energy");}
//  @Test   public void testMulliken()                           {runTemplateTest("mulliken");}
  @Test   @Ignore public void testMultipoleEXAMPLE()                          {runTemplateTest("multipole");}
  @Test   @Ignore public void testNccpEXAMPLE()                               {runTemplateTest("nccp");}
//  @Test   public void testNonvar()                             {runTemplateTest("nonvar");}
  @Test   @Ignore public void testNucdipoleEXAMPLE()                          {runTemplateTest("nucdipole");}
  @Test   @Ignore public void testNwcheminpEXAMPLE()                          {runTemplateTest("nwcheminp");}
  @Test   @Ignore public void testNwchemmp2EXAMPLE()                          {runTemplateTest("nwchemmp2");}
  @Test   @Ignore public void testPropertyEXAMPLE()                           {runTemplateTest("property");}
//  @Test   public void testRohffinal()                          {runTemplateTest("rohffinal");}
//  @Test   public void testScf()                                {runTemplateTest("scf");}
//  @Test   public void testScreen()                             {runTemplateTest("screen");}
//  @Test   public void testShielding()                          {runTemplateTest("shielding");}
//  @Test   public void testSummary()                            {runTemplateTest("summary");}
//  @Test   public void testSuperpos()                           {runTemplateTest("superpos");}
  @Test   @Ignore public void testSymbasEXAMPLE()                             {runTemplateTest("symbas");}
//  @Test   public void testSymminfo()                           {runTemplateTest("symminfo");}
  @Test   @Ignore public void testSymmolorbEXAMPLE()                          {runTemplateTest("symmolorb");}
//  @Test   public void testTensor()                             {runTemplateTest("tensor");}
//  @Test   public void testXc()                                 {runTemplateTest("xc");}
  @Test   @Ignore public void testXyzEXAMPLE()                                {runTemplateTest("xyz");}
  @Test   @Ignore public void testZmatEXAMPLE()                               {runTemplateTest("zmat");}

  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
