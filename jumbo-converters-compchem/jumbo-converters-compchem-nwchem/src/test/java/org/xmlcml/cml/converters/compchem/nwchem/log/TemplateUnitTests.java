package org.xmlcml.cml.converters.compchem.nwchem.log;

import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.TemplateTestUtils;

public class TemplateUnitTests {
	
  private static final String CODE_BASE = "nwchem";
  private static final String FILE_TYPE = "log";
  private static final String BASE_DIR = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/";
  private static final String TEMPLATE_DIR = 
    BASE_DIR+"templates/";
  static String INPUT_TEMPLATE_S = 
    "org/xmlcml/cml/converters/compchem/"+CODE_BASE+"/"+FILE_TYPE+"/templates/topTemplate.xml";
  static String BASE_URI = "classpath:/"+TEMPLATE_DIR;
  
  @Test
  public void dummy() {
    
  }
  
  @Test
  @Ignore
  public void templateTester() {
    TemplateTestUtils.runCommentExamples(INPUT_TEMPLATE_S, BASE_URI);
  }


   @Test public void testAcknowOK()                              {runTemplateTest("acknow");}
   @Test public void testAlphabetaOK()                           {runTemplateTest("alphabeta");}
   @Test public void testAlphabeta1OK()                          {runTemplateTest("alphabeta1");}
   @Test public void testAlphaeigsOK()                           {runTemplateTest("alphaeigs");}
   @Test public void testAlphafoccsOK()                          {runTemplateTest("alphafoccs");}
   @Test public void testArgumentOK()                            {runTemplateTest("argument");}
   @Test public void testAtmassOK()                              {runTemplateTest("atmass");}
   @Test public void testAtombasisOK()                           {runTemplateTest("atombasis");}
   @Test public void testAtombasisend1OK()                       {runTemplateTest("atombasisend1");}
   @Test public void testAtomicshellscreenOK()                   {runTemplateTest("atomicshellscreen");}
   @Test public void testAuthors()                               {runTemplateTest("authors");}
   @Test public void testAutosymOK()                             {runTemplateTest("autosym");}
   @Test public void testBasisShellOK()                          {runTemplateTest("basis.shell");}
   @Ignore @Test public void testBasisFIXME()                               {runTemplateTest("basis");}
   @Test public void testBasisfuncOK()                           {runTemplateTest("basisfunc");}
   @Test public void testBeforeaftercallOK()                     {runTemplateTest("beforeaftercall");}
   @Test public void testBrillouinzpOK()                         {runTemplateTest("brillouinzp");}
   @Test public void testCached1SKIP()                           {runTemplateTest("cached1");}
   @Test public void testCachingOK()                             {runTemplateTest("caching");}
   @Test public void testCcsdOKTODO()                            {runTemplateTest("ccsd.temp");}
   @Test public void testCcsdparamSKIP()                         {runTemplateTest("ccsdparam");}
   @Test public void testCcsdtOK()                               {runTemplateTest("ccsdt");}
   @Test public void testCenter23TempOK()                        {runTemplateTest("center23.temp");}
   @Test public void testCentermassOK()                          {runTemplateTest("centermass");}
   @Test public void testCenterofchargeOK()                      {runTemplateTest("centerofcharge");}
   @Test public void testCitationOK()                            {runTemplateTest("citation");}
   @Test public void testCleanupOK()                             {runTemplateTest("cleanup");}
   @Test public void testCondfukuiOK()                           {runTemplateTest("condfukui");}
   @Test public void testCondfukuimuOK()                         {runTemplateTest("condfukuimu");}
   @Test public void testConverged()                             {runTemplateTest("converged");}
   @Test public void testConvergeinfoOK()                        {runTemplateTest("convergeinfo");}
   @Test public void testConvergeiterOK()                        {runTemplateTest("convergeiter");}
   @Test public void testCphfOK()                                {runTemplateTest("cphf");}
   @Test public void testCrystaldipoleOK()                       {runTemplateTest("crystaldipole");}
   @Test public void testDamping()                               {runTemplateTest("damping");}
   @Test public void testDftOK()                                 {runTemplateTest("dft");}
   @Test public void testDftenergradOK()                         {runTemplateTest("dftenergrad");}
   @Test public void testDftfinalmolorbOK()                      {runTemplateTest("dftfinalmolorb");}
   @Test public void testDftgradient()                           {runTemplateTest("dftgradient");}
   @Ignore @Test public void testDirectmp2FIX()                {runTemplateTest("directmp2");}
   @Test public void testDirinfoOK()                             {runTemplateTest("dirinfo");}
   @Test public void testDisclaim()                              {runTemplateTest("disclaim");}
   @Test public void testEaffileOK()                             {runTemplateTest("eaffile");}
   @Test public void testEchoallOK()                             {runTemplateTest("echoall");}
   @Test public void testEffnucrepOK()                           {runTemplateTest("effnucrep");}
   @Test public void testEigqrotOK()                             {runTemplateTest("eigqrot");}
   @Ignore @Test public void testEnergycalcFIX()               {runTemplateTest("energycalc");}
   @Ignore @Test public void testEnergyminFIX()                {runTemplateTest("energymin");}
   @Test public void testErrorcurrent()                          {runTemplateTest("error.current");}
   @Test public void testErrormult()                             {runTemplateTest("error.mult");}
   @Test public void testErrornocat()                            {runTemplateTest("error.nocat");}
   @Test public void testErrorfile()                             {runTemplateTest("error.file");}
   @Test public void testErrorLastSys()                          {runTemplateTest("error.lastsys");}
   @Test public void testEtnew()                                 {runTemplateTest("etnew");}
   @Test public void testFilebalanceOK()                         {runTemplateTest("filebalance");}
   @Test public void testFinaleigenOK()                          {runTemplateTest("finaleigen");}
   @Test public void testFinalrhf()                              {runTemplateTest("finalrhf");}
   @Test public void testFockbuild()                             {runTemplateTest("fockbuild");}
   @Test public void testFourindexOK()                           {runTemplateTest("fourindex");}
   @Test public void testFukuiOK()                               {runTemplateTest("fukui");}
   @Test public void testFukuiLite()                             {runTemplateTest("fukuilite");}
   @Test public void testGama()                                  {runTemplateTest("gama");}
   @Test public void testGastatsOK()                             {runTemplateTest("gastats");}
   @Test public void testGeninfoOK()                             {runTemplateTest("geninfo");}
   @Test public void testGeomOK()                                {runTemplateTest("geometry");}
   @Test public void testGeomopt()                               {runTemplateTest("geomopt");}
   @Test public void testGlobalarray()                           {runTemplateTest("globalarray");}
   @Test public void testGradmoduleOK()               {runTemplateTest("gradmodule");}
   @Test public void testGradmodulestepOK()                      {runTemplateTest("gradmodulestep");}
   @Test public void testGridinfoOK()                            {runTemplateTest("gridinfo");}
   @Test public void testGridpts()                               {runTemplateTest("gridpts");}
   @Test public void testHdbmWHITE()                     {runTemplateTest("hdbm");}
   @Test public void testHomolumoOK()                            {runTemplateTest("homolumo");}
   @Test public void testInputBasisOK()                          {runTemplateTest("input.basis");}
   @Test public void testInputDftOK()                            {runTemplateTest("input.dft");}
   @Test public void testInputGeomOK()                {runTemplateTest("input.geom");}
   @Test public void testInputPropertyOK()                       {runTemplateTest("input.property");}
   @Ignore @Test public void testInputWHITEFIX()                    {runTemplateTest("input");}
   @Test public void testIntegdensOK()                           {runTemplateTest("integdens");}
   @Test public void testIntegralfileOK()                        {runTemplateTest("integralfile");}
   @Test public void testIntegralscreenOK()                      {runTemplateTest("integralscreen");}
   @Test public void testInternucOK()                            {runTemplateTest("internuc");}
   @Test public void testInternucangOK()                         {runTemplateTest("internucang");}
   @Test public void testIsotropOK()                             {runTemplateTest("isotrop");}
   @Test public void testIterOK()                                {runTemplateTest("iter");}
   @Test public void testIternsubOK()                            {runTemplateTest("iternsub");}
   @Test public void testItersolOK()                             {runTemplateTest("itersol");}
   @Test public void testItol2eOK()                              {runTemplateTest("itol2e");}
   @Test public void testJobOK()                                 {runTemplateTest("job");}
   @Test public void testKineticenergyOK()                       {runTemplateTest("kineticenergy");}
   @Ignore @Test public void testLatticeOKFIX()                          {runTemplateTest("lattice");}
   @Test public void testLcubeOK()                               {runTemplateTest("lcube");}
   @Test public void testLibraryfileOK()                         {runTemplateTest("libraryfile");}
   @Test public void testLibrarynameOK()                         {runTemplateTest("libraryname");}
   @Test public void testLimitedOK()                             {runTemplateTest("limited");}
   @Test public void testLindeptolOK()                           {runTemplateTest("lindeptol");}
   @Test public void testLinesearch()                            {runTemplateTest("linesearch");}
   @Test public void testLinestoskipOK()                         {runTemplateTest("linestoskip");}
   @Test public void testLoading()                               {runTemplateTest("loading");}
   @Test public void testLocalmemOK()                            {runTemplateTest("localmem");}
   @Test public void testMagneticatomBAD()                       {runTemplateTest("magneticatom");}
   @Test public void testMullikenOK()                            {runTemplateTest("mulliken.temp");}
   @Test public void testMemoryWHITE()                           {runTemplateTest("memory");}
   @Test public void testMemoryUtilOK()                          {runTemplateTest("memoryUtil");}
   @Test public void testMoleculardipoleOK()                     {runTemplateTest("moleculardipole");}
   @Test public void testMomintOK()                              {runTemplateTest("momint");}
   @Test public void testMp2energyOK()                           {runTemplateTest("mp2energy");}
   @Test public void testMullikenTempOK()                        {runTemplateTest("mulliken.temp");}
   @Test public void testMultipoleOK()                           {runTemplateTest("multipole");}
   @Test public void testMultipole1OK()                          {runTemplateTest("multipole1");}
   @Test public void testNamedtensorOK()                         {runTemplateTest("namedtensor");}
   @Test public void testNccpOK()                                {runTemplateTest("nccp");}
   @Test public void testNonvariterOK()                          {runTemplateTest("nonvariter");}
   @Test public void testNucdipoleOK()                           {runTemplateTest("nucdipole");}
   @Test public void testNumbirrepOK()                           {runTemplateTest("numbirrep");}
   @Test public void testNwcheminpOK()                           {runTemplateTest("nwcheminp");}
   @Test public void testNwcheminpnwpwOK()                       {runTemplateTest("nwcheminpnwpw");}
   @Test public void testNwcheminptitleOK()                      {runTemplateTest("nwcheminptitle");}
   @Test public void testNwchemmp2OK()                           {runTemplateTest("nwchemmp2");}
   @Test public void testNwpwAtomcomposOK()                      {runTemplateTest("nwpw.atomcompos");}
   @Test public void testNwpwBadbrillouinOK()                    {runTemplateTest("nwpw.badbrillouin");}
   @Test public void testNwpwBannerOK()                          {runTemplateTest("nwpw.banner");}
   @Test public void testNwpwBrillouinOK()                       {runTemplateTest("nwpw.brillouin");}
   @Test public void testNwpwCompOK()                            {runTemplateTest("nwpw.comp");}
   @Test public void testNwpwCompgridsOK()                       {runTemplateTest("nwpw.compgrids");}
   @Test public void testNwpwConvertingOK()                      {runTemplateTest("nwpw.converting");}
   @Test public void testNwpwDensitycutOK()                      {runTemplateTest("nwpw.densitycut");}
   @Test public void testNwpwElemsAtomOK()                       {runTemplateTest("nwpw.elems.atom");}
   @Test public void testNwpwElemsOK()                           {runTemplateTest("nwpw.elems");}
   @Test public void testNwpwElems1OK()                          {runTemplateTest("nwpw.elems1");}
   @Test public void testNwpwEwaldOK()                           {runTemplateTest("nwpw.ewald");}
   @Test public void testNwpwGeneratedOK()                       {runTemplateTest("nwpw.generated");}
   @Test public void testNwpwGramschmidtOK()                     {runTemplateTest("nwpw.gramschmidt");}
   @Test public void testNwpwInputdataOK()                       {runTemplateTest("nwpw.inputdata");}
   @Test public void testNwpwJobOK()                             {runTemplateTest("nwpw.job");}
   @Test public void testNwpwLatticeOK()                         {runTemplateTest("nwpw.lattice");}
   @Test public void testNwpwLibraryOK()                         {runTemplateTest("nwpw.library");}
   @Test public void testNwpwNelectronOK()                       {runTemplateTest("nwpw.nelectron");}
   @Test public void testNwpwOptionsOK()                         {runTemplateTest("nwpw.options");}
   @Test public void testNwpwPsifileOK()                         {runTemplateTest("nwpw.psifile");}
   @Test public void testNwpwRandomOK()                          {runTemplateTest("nwpw.random");}
   @Test public void testNwpwSupercellOK()                       {runTemplateTest("nwpw.supercell");}
   @Test public void testNwpwSupercell1OK()                      {runTemplateTest("nwpw.supercell1");}
   @Test public void testNwpwTechnicalOK()                       {runTemplateTest("nwpw.technical");}
   @Test public void testNwpwTotalchargeOK()                     {runTemplateTest("nwpw.totalcharge");}
   @Test public void testNwpwWarningOK()                         {runTemplateTest("nwpw.warning");}
   @Ignore @Test public void testNwpwWHITEFIX()                     {runTemplateTest("nwpw");}
   @Test public void testOperationtimeOK()                       {runTemplateTest("operationtime");}
   @Test public void testOrbitalenerOK()                         {runTemplateTest("orbitalener");}
   @Test public void testOrbitalsymOK()                          {runTemplateTest("orbitalsym");}
   @Test public void testParallelOK()                            {runTemplateTest("parallel");}
   @Test public void testPrincipalcomponentsOK()                 {runTemplateTest("principalcomponents");}
   @Test public void testPropertyOK()                            {runTemplateTest("property");}
   @Test public void testQuadrohfOK()                            {runTemplateTest("quadrohf");}
   @Test public void testQuartetsOK()                            {runTemplateTest("quartets");}
   @Test public void testReadmolorbOK()                          {runTemplateTest("readmolorb");}
   @Test public void testRecordsizeOK()                          {runTemplateTest("recordsize");}
   @Test public void testReferencesOK()                          {runTemplateTest("references");}
   @Test public void testRohffinalTempOK()                       {runTemplateTest("rohffinal.temp");}
   @Test public void testRtdbOK()                                {runTemplateTest("rtdb");}
   @Test public void testScalingOK()                             {runTemplateTest("scaling");}
   @Test public void testScfTempOK()                             {runTemplateTest("scf.temp");}
   @Test public void testScfenerOK()                             {runTemplateTest("scfener");}
   @Test public void testSchwarzOK()                             {runTemplateTest("schwarz");}
   @Test public void testScreentolOK()                           {runTemplateTest("screentol");}
   @Test public void testSegmentedOK()                           {runTemplateTest("segmented");}
   @Test public void testSemidirectOK()                          {runTemplateTest("semidirect");}
   @Ignore @Test public void testShieldingFIX()                      {runTemplateTest("shielding");}
   @Test public void testSpincontamOK()                          {runTemplateTest("spincontam");}
   @Ignore @Test public void testStepTEXT()                                  {runTemplateTest("step");}
   @Test public void testStepdataOK()                            {runTemplateTest("stepdata");}
   @Ignore @Test public void testStepLastTEXT()                              {runTemplateTest("steplast");}
   @Test public void testSummaryallocOK()                        {runTemplateTest("summaryalloc");}
   @Test public void testSummarybasisOK()                        {runTemplateTest("summarybasis");}
   @Ignore @Test public void testSummaryresultsFIX()            {runTemplateTest("summaryresults");}
   @Test public void testSummglobarraysOK()                      {runTemplateTest("summglobarrays");}
   @Test public void testSuperposatomguessOK()                   {runTemplateTest("superposatomguess");}
   @Test public void testSymbasOK()                              {runTemplateTest("symbas");}
   @Ignore @Test public void testSymminfoTempFIX()             {runTemplateTest("symminfo.temp");}
   @Test public void testSymmolorbOK()                           {runTemplateTest("symmolorb");}
   @Test public void testThreetwoOK()                            {runTemplateTest("threetwo");}
   @Test public void testTidyHeadingOK()                         {runTemplateTest("tidyheading");}
   @Test public void testTimeafterOK()                           {runTemplateTest("timeafter");}
   @Test public void testTimeboxOK()                             {runTemplateTest("timebox");}
   @Test public void testTimePriorOK()                           {runTemplateTest("timeprior");}
   @Test public void testTimesOK()                               {runTemplateTest("times");}
   @Ignore @Test public void testTimingNEEDSWORK()                   {runTemplateTest("timing");}
   @Test public void testTitleOK()                               {runTemplateTest("title");}
   @Test public void testTotalbandenerOK()                       {runTemplateTest("totalbandener");}
   @Test public void testTotaldftOK()                            {runTemplateTest("totaldft");}
   @Test public void testTriplesOK()                             {runTemplateTest("triples");}
   @Test public void testWarningDensity()                        {runTemplateTest("warning.density");}
   @Test public void testWrotemolorbOK()                         {runTemplateTest("wrotemolorb");}
   @Test public void testXcinfoOK()                              {runTemplateTest("xcinfo");}
   @Test public void testXcquadOK()                              {runTemplateTest("xcquad");}
   @Test public void testXyzOK()                                 {runTemplateTest("xyz");}
   @Test public void testZmatOK()                                {runTemplateTest("zmat");}
   @Test public void testZmatautozMISSES()                       {runTemplateTest("zmatautoz");}
   @Ignore @Test public void testZmatgradFIX()                 {runTemplateTest("zmatgrad");}
   @Ignore@Test public void testZmatuserFIX()                 {runTemplateTest("zmatuser");}
  
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
