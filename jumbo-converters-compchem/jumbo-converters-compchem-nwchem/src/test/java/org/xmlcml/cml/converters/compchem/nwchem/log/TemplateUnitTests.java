package org.xmlcml.cml.converters.compchem.nwchem.log;

import nu.xom.Element;


import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.text.TemplateTestUtils;

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
   @Test public void testAtombasisOK()                  {runTemplateTest("atombasis");}
   @Test public void testAtombasisendOK()               {runTemplateTest("atombasisend");}
   @Test public void testAtombasisend1OK()               {runTemplateTest("atombasisend1");}
   @Test public void testAtomicshellscreenOK()                   {runTemplateTest("atomicshellscreen");}
   @Test public void testAuthors()                               {runTemplateTest("authors");}
   @Test public void testAutosymOK()                             {runTemplateTest("autosym");}
   @Test public void testBasisShellOK()                          {runTemplateTest("basis.shell");}
   @Test public void testBasisFIXME()                               {runTemplateTest("basis");}
   @Test public void testBasisfuncOK()                           {runTemplateTest("basisfunc");}
   @Test public void testBeforeaftercallOK()                     {runTemplateTest("beforeaftercall");}
   @Test public void testBrillouinzpOK()                         {runTemplateTest("brillouinzp");}
   @Test public void testCached1SKIP()                           {runTemplateTest("cached1");}
   @Test public void testCachingOK()                             {runTemplateTest("caching");}
   @Ignore @Test public void testCcsdtTBDWHITE()                 {runTemplateTest("ccsdt");}
//   @Test public void testCenter23INCLUDE()                     {runTemplateTest("center23");}
   @Test public void testCcsdOKTODO()                            {runTemplateTest("ccsd.temp");}
   @Test public void testCcsdparamSKIP()                         {runTemplateTest("ccsdparam");}
   @Ignore @Test public void testCcsdtWHITE()                            {runTemplateTest("ccsdt");}
   @Test public void testCenter23TempOK()                        {runTemplateTest("center23.temp");}
   @Test public void testCentermassOK()                          {runTemplateTest("centermass");}
   @Ignore @Test public void testCenterofchargeFIXME()                      {runTemplateTest("centerofcharge");}
   @Test public void testCitationOK()                            {runTemplateTest("citation");}
   @Ignore @Test public void testCleanup()                       {runTemplateTest("cleanup");}
   @Test public void testCondfukuiOK()                           {runTemplateTest("condfukui");}
   @Test public void testCondfukuimuOK()                         {runTemplateTest("condfukuimu");}
   @Test public void testConvergeinfoOK()                        {runTemplateTest("convergeinfo");}
   @Test public void testConvergeiterOK()                        {runTemplateTest("convergeiter");}
   @Test public void testCphfOK()                                {runTemplateTest("cphf");}
   @Test public void testCrystaldipoleOK()                       {runTemplateTest("crystaldipole");}
   @Test public void testDamping()                               {runTemplateTest("damping");}
   @Test public void testDftOK()                                 {runTemplateTest("dft");}
   @Ignore @Test public void testDftfinalmolorbWHITE()           {runTemplateTest("dftfinalmolorb");}
   @Ignore @Test public void testDirectmp2WHITE()                {runTemplateTest("directmp2");}
   @Ignore @Test public void testDirinfoWHITE()                  {runTemplateTest("dirinfo");}
   @Test public void testDisclaim()                              {runTemplateTest("disclaim");}
   @Test public void testEaffileOK()                             {runTemplateTest("eaffile");}
   @Ignore @Test public void testEchoall()                       {runTemplateTest("echoall");}
   @Test public void testEffnucrep()                             {runTemplateTest("effnucrep");}
   @Test public void testEigqrotOK()                             {runTemplateTest("eigqrot");}
   @Ignore @Test public void testEnergycalcWHITE()               {runTemplateTest("energycalc");}
   @Ignore @Test public void testEnergyminWHITE()                {runTemplateTest("energymin");}
   @Test public void testEtnew()                                 {runTemplateTest("etnew");}
   @Test public void testFilebalanceOK()                         {runTemplateTest("filebalance");}
   @Test public void testFinaleigenOK()                          {runTemplateTest("finaleigen");}
   @Test public void testFinalrhf()                              {runTemplateTest("finalrhf");}
   @Test public void testFockbuild()                             {runTemplateTest("fockbuild");}
   @Ignore @Test public void testFourindexWHITE()                {runTemplateTest("fourindex");}
   @Ignore @Test public void testFukuiWHITE()                    {runTemplateTest("fukui");}
   @Test public void testFukuiLite()                             {runTemplateTest("fukuilite");}
   @Ignore @Test public void testGama()                          {runTemplateTest("gama");}
   @Ignore @Test public void testGastatsWHITE()                  {runTemplateTest("gastats");}
   @Test public void testGeninfoOK()                               {runTemplateTest("geninfo");}
   @Test public void testGeomOK()                                {runTemplateTest("geometry");}
   @Test public void testGeomopt()                               {runTemplateTest("geomopt");}
   @Test public void testGlobalarray()                           {runTemplateTest("globalarray");}
   @Ignore @Test public void testGradmoduleWHITE()               {runTemplateTest("gradmodule");}
   @Ignore @Test public void testGradmodulestepWHITE()           {runTemplateTest("gradmodulestep");}
   @Test public void testGridinfoOK()                            {runTemplateTest("gridinfo");}
   @Test public void testGridpts()                               {runTemplateTest("gridpts");}
   @Ignore @Test public void testHdbmWHITE()                     {runTemplateTest("hdbm");}
   @Test public void testHomolumoOK()                            {runTemplateTest("homolumo");}
   @Test public void testInputBasisOK()                          {runTemplateTest("input.basis");}
   @Test public void testInputDftOK()                            {runTemplateTest("input.dft");}
   @Ignore @Test public void testInputGeomWHITE()                {runTemplateTest("input.geom");}
   @Test public void testInputPropertyOK()                       {runTemplateTest("input.property");}
   @Ignore @Test public void testInputWHITE()                    {runTemplateTest("input");}
   @Test public void testIntegdensOK()                           {runTemplateTest("integdens");}
   @Test public void testIntegralfileOK()                        {runTemplateTest("integralfile");}
   @Ignore @Test public void testIntegralscreenWHITE()           {runTemplateTest("integralscreen");}
   @Test public void testInternucOK()                            {runTemplateTest("internuc");}
   @Test public void testInternucangOK()                         {runTemplateTest("internucang");}
   @Test public void testIsotropOK()                             {runTemplateTest("isotrop");}
   @Ignore @Test public void testIterMiscWHITE()                 {runTemplateTest("iter.misc");}
   @Test public void testIterOK()                                {runTemplateTest("iter");}
   @Test public void testIternsubOK()                            {runTemplateTest("iternsub");}
   @Test public void testItersolOK()                             {runTemplateTest("itersol");}
   @Test public void testItol2eOK()                              {runTemplateTest("itol2e");}
   @Test public void testJobOK()                                 {runTemplateTest("job");}
   @Test public void testKineticenergyOK()                       {runTemplateTest("kineticenergy");}
   @Ignore @Test public void testLatticeOKFIX()                             {runTemplateTest("lattice");}
   @Test public void testLcubeOK()                               {runTemplateTest("lcube");}
   @Test public void testLibraryfileOK()                         {runTemplateTest("libraryfile");}
   @Test public void testLibrarynameOK()                         {runTemplateTest("libraryname");}
   @Test public void testLimitedOK()                             {runTemplateTest("limited");}
   @Test public void testLindeptolOK()                           {runTemplateTest("lindeptol");}
   @Ignore @Test public void testLinestoskipWHITE()              {runTemplateTest("linestoskip");}
   @Test public void testLocalmemOK()                            {runTemplateTest("localmem");}
   @Test public void testMagneticatomBAD()                       {runTemplateTest("magneticatom");}
   @Ignore @Test public void testMullikenWHITE()                 {runTemplateTest("mulliken.temp");}
   @Ignore @Test public void testMemoryWHITE()                   {runTemplateTest("memory");}
   @Test public void testMemoryUtilOK()                          {runTemplateTest("memoryUtil");}
   @Ignore @Test public void testMoleculardipoleWHITE()          {runTemplateTest("moleculardipole");}
   @Ignore @Test public void testMomintOKFIX()                              {runTemplateTest("momint");}
   @Test public void testMp2energyOK()                           {runTemplateTest("mp2energy");}
   @Test public void testMullikenTempOK()                        {runTemplateTest("mulliken.temp");}
   @Test public void testMultipoleOK()                           {runTemplateTest("multipole");}
   @Test public void testMultipole1NAMESPACE()                   {runTemplateTest("multipole1");}
   @Test public void testNamedtensorOK()                         {runTemplateTest("namedtensor");}
   @Test public void testNccpEXAMPLE()                           {runTemplateTest("nccp");}
   @Test public void testNonvariterEXAMPLE()                     {runTemplateTest("nonvariter");}
   @Test public void testNucdipoleEXAMPLE()                      {runTemplateTest("nucdipole");}
   @Test public void testNumbirrepEXAMPLE()                      {runTemplateTest("numbirrep");}
   @Test public void testNwcheminpEXAMPLE()                      {runTemplateTest("nwcheminp");}
   @Ignore @Test public void testNwcheminpnwpwNAMESPACE()        {runTemplateTest("nwcheminpnwpw");}
   @Test public void testNwcheminptitleEXAMPLE()                 {runTemplateTest("nwcheminptitle");}
   @Test public void testNwchemmp2EXAMPLE()                      {runTemplateTest("nwchemmp2");}
   @Ignore @Test public void testNwpwAtomcomposWHITESPACE()      {runTemplateTest("nwpw.atomcompos");}
   @Test public void testNwpwBadbrillouinEXAMPLE()               {runTemplateTest("nwpw.badbrillouin");}
   @Test public void testNwpwBannerOK()                          {runTemplateTest("nwpw.banner");}
   @Test public void testNwpwBrillouinOK()                       {runTemplateTest("nwpw.brillouin");}
   @Ignore @Test public void testNwpwCompWHITE()                 {runTemplateTest("nwpw.comp");}
   @Test public void testNwpwCompgridsOK()                       {runTemplateTest("nwpw.compgrids");}
   @Test public void testNwpwConvertingOK()                      {runTemplateTest("nwpw.converting");}
   @Test public void testNwpwDensitycutOK()                      {runTemplateTest("nwpw.densitycut");}
   @Test public void testNwpwElemsAtomOK()                       {runTemplateTest("nwpw.elems.atom");}
   @Ignore @Test public void testNwpwElemsNAMESPACE()            {runTemplateTest("nwpw.elems");}
   @Ignore @Test public void testNwpwElems1NAMESPACE()           {runTemplateTest("nwpw.elems1");}
   @Test public void testNwpwEwaldOK()                           {runTemplateTest("nwpw.ewald");}
   @Test public void testNwpwGeneratedOK()                       {runTemplateTest("nwpw.generated");}
   @Test public void testNwpwGramschmidtOK()                     {runTemplateTest("nwpw.gramschmidt");}
   @Test public void testNwpwInputdataOK()                       {runTemplateTest("nwpw.inputdata");}
   @Ignore @Test public void testNwpwJobWHITE()                  {runTemplateTest("nwpw.job");}
   @Ignore @Test public void testNwpwLatticeWHITE()              {runTemplateTest("nwpw.lattice");}
   @Test public void testNwpwLibraryOK()                         {runTemplateTest("nwpw.library");}
   @Test public void testNwpwNelectronOK()                       {runTemplateTest("nwpw.nelectron");}
   @Ignore @Test public void testNwpwOptionsWHITE()              {runTemplateTest("nwpw.options");}
   @Test public void testNwpwPsifileOK()                         {runTemplateTest("nwpw.psifile");}
   @Test public void testNwpwRandomOK()                          {runTemplateTest("nwpw.random");}
   @Test public void testNwpwSupercellOK()                       {runTemplateTest("nwpw.supercell");}
   @Test public void testNwpwSupercell1OK()                      {runTemplateTest("nwpw.supercell1");}
   @Test public void testNwpwTechnicalOK()                       {runTemplateTest("nwpw.technical");}
   @Test public void testNwpwTotalchargeOK()                     {runTemplateTest("nwpw.totalcharge");}
   @Test public void testNwpwWarningOK()                         {runTemplateTest("nwpw.warning");}
   @Ignore @Test public void testNwpwWHITE()                     {runTemplateTest("nwpw");}
   @Ignore @Test public void testOperationtimeWHITE()            {runTemplateTest("operationtime");}
   @Test public void testOrbitalenerEXAMPLE()                    {runTemplateTest("orbitalener");}
   @Ignore @Test public void testOrbitalsymWHITE()               {runTemplateTest("orbitalsym");}
   @Test public void testParallelOK()                            {runTemplateTest("parallel");}
   @Test public void testPrincipalcomponentsOK()                 {runTemplateTest("principalcomponents");}
   @Test public void testPropertyEXAMPLE()                       {runTemplateTest("property");}
   @Test public void testQuadrohfEXAMPLE()                       {runTemplateTest("quadrohf");}
   @Ignore @Test public void testQuartetsEXAMPLEFIX()                       {runTemplateTest("quartets");}
   @Test public void testReadmolorbEXAMPLE()                     {runTemplateTest("readmolorb");}
   @Test public void testRecordsizeEXAMPLE()                     {runTemplateTest("recordsize");}
   @Test public void testReferencesEXAMPLE()                     {runTemplateTest("references");}
   @Ignore @Test public void testRohffinalTempWHITE()            {runTemplateTest("rohffinal.temp");}
   @Test public void testRtdbEXAMPLEEXAMPLE()                    {runTemplateTest("rtdb");}
   @Test public void testScalingOK()                             {runTemplateTest("scaling");}
   @Test public void testScfTempEXAMPLE()                        {runTemplateTest("scf.temp");}
   @Test public void testScfenerEXAMPLE()                        {runTemplateTest("scfener");}
   @Test public void testSchwarzEXAMPLE()                        {runTemplateTest("schwarz");}
   @Test public void testScreentolEXAMPLE()                      {runTemplateTest("screentol");}
   @Ignore @Test public void testSegmentedWHITE()                {runTemplateTest("segmented");}
   @Test public void testSemidirectEXAMPLE()                     {runTemplateTest("semidirect");}
   @Ignore @Test public void testShieldingNAME()                 {runTemplateTest("shielding");}
   @Test public void testSpincontamEXAMPLE()                     {runTemplateTest("spincontam");}
   @Test public void testSummaryallocEXAMPLE()                   {runTemplateTest("summaryalloc");}
   @Ignore @Test public void testSummarybasisEXAMPLE()                   {runTemplateTest("summarybasis");}
   @Ignore @Test public void testSummaryresultsNAME()            {runTemplateTest("summaryresults");}
   @Test public void testSummglobarraysEXAMPLE()                 {runTemplateTest("summglobarrays");}
   @Test public void testSuperposatomguessEXAMPLE()              {runTemplateTest("superposatomguess");}
   @Ignore @Test public void testSymbasWHITE()                   {runTemplateTest("symbas");}
   @Ignore @Test public void testSymminfoTempWHITE()             {runTemplateTest("symminfo.temp");}
   @Test public void testSymmolorbEXAMPLE()                      {runTemplateTest("symmolorb");}
   @Ignore @Test public void testThreetwoWHITE()                 {runTemplateTest("threetwo");}
   @Test public void testTidyHeading()                           {runTemplateTest("tidyheading");}
   @Test public void testTimeafterEXAMPLE()                      {runTemplateTest("timeafter");}
   @Ignore @Test public void testTimesWHITE()                    {runTemplateTest("times");}
   @Ignore @Test public void testTimingWHITE()                   {runTemplateTest("timing");}
   @Test public void testTitleEXAMPLE()                          {runTemplateTest("title");}
   @Test public void testTotalbandenerEXAMPLE()                  {runTemplateTest("totalbandener");}
   @Test public void testTotaldftEXAMPLE()                       {runTemplateTest("totaldft");}
   @Test public void testTriplesEXAMPLE()                        {runTemplateTest("triples");}
   @Test public void testWrotemolorbEXAMPLE()                    {runTemplateTest("wrotemolorb");}
   @Ignore @Test public void testXcinfoWHITE()                   {runTemplateTest("xcinfo");}
   @Test public void testXcquadEXAMPLE()                         {runTemplateTest("xcquad");}
   @Ignore @Test public void testXyzWHITE()                      {runTemplateTest("xyz");}
   @Test public void testZmatOK()                                {runTemplateTest("zmat");}
   @Ignore @Test public void testZmatautozWHITE()                {runTemplateTest("zmatautoz");}
   @Ignore @Test public void testZmatgradWHITE()                 {runTemplateTest("zmatgrad");}
   @Ignore @Test public void testZmatuserWHITE()                 {runTemplateTest("zmatuser");}
  
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
