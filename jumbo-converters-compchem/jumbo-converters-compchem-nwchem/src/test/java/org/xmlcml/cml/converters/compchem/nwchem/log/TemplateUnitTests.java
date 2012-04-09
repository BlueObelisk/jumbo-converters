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

/*
 * These templates are used and the tests work.
 */
   @Test public void testAcknow()                                {runTemplateTest("acknow");}
   @Test public void testArgument()                              {runTemplateTest("argument");}
   @Test public void testAtmass()                                {runTemplateTest("atmass");}
   @Test public void testAuthors()                               {runTemplateTest("authors");}
   @Test public void testBasis()                                 {runTemplateTest("basis");}
   @Test public void testBasisShell()                            {runTemplateTest("basis.shell");}
   @Test public void testBasisSummary()                          {runTemplateTest("basis.summary");}
   @Test public void testCaching()                               {runTemplateTest("caching");}
   @Test public void testCitation()                              {runTemplateTest("citation");}
   @Test public void testDftConvergeinfo()                       {runTemplateTest("dft.convergeinfo");}
   @Test public void testDftInfo()                               {runTemplateTest("dft.info");}
   @Test public void testDftIter()                               {runTemplateTest("dft.iter");}
   @Test public void testDftGridpts()                            {runTemplateTest("dft.gridpts");}
   @Test public void testDftFinal()                              {runTemplateTest("dft.final");}
   @Test public void testDftFinalMo()                            {runTemplateTest("dft.final.mo");}
   @Test public void testDftModule()                             {runTemplateTest("dft.module");}
   @Test public void testDftScreentol()                          {runTemplateTest("dft.screentol");}
   @Test public void testDftXcInfo()                             {runTemplateTest("dft.xcinfo");}
   @Test public void testDftWarningDensity()                     {runTemplateTest("dft.warn.density");}
   @Test public void testDirinfo()                               {runTemplateTest("dirinfo");}
   @Test public void testEffnucrep()                             {runTemplateTest("effnucrep");}
   @Test public void testEnviroment()                            {runTemplateTest("environment");}
   @Test public void testErrorcurrent()                          {runTemplateTest("error.current");}
   @Test public void testErrormult()                             {runTemplateTest("error.mult");}
   @Test public void testErrornocat()                            {runTemplateTest("error.nocat");}
   @Test public void testErrorfile()                             {runTemplateTest("error.file");}
   @Test public void testErrorLastSys()                          {runTemplateTest("error.lastsys");}
   @Test public void testGastats()                               {runTemplateTest("ga.stats");}
   @Test public void testGaSummary()                             {runTemplateTest("ga.summary");}
   @Test public void testGeomopt()                               {runTemplateTest("geomopt");}
   @Test public void testGeomoptFinal()                          {runTemplateTest("geomopt.final");}
   @Test public void testGeomoptStep()                           {runTemplateTest("geomopt.step");}
   @Test public void testGeometry()                              {runTemplateTest("geometry");}
   @Test public void testGeometryMol()                           {runTemplateTest("geometry.mol");}
   @Test public void testGeomoptStepdata()                       {runTemplateTest("geomopt.stepdata");}
   @Test public void testGradmodule()                            {runTemplateTest("gradient.module");}
   @Test public void testGradTimebox()                           {runTemplateTest("gradient.timebox");}
   @Test public void testDftGridinfo()                           {runTemplateTest("dft.gridinfo");}
   @Test public void testGuess()                                 {runTemplateTest("guess");}
   @Test public void testGuessNonvar()                           {runTemplateTest("guess.nonvar");}
   @Test public void testInputBasis()                            {runTemplateTest("input.basis");}
   @Test public void testInputDft()                              {runTemplateTest("input.dft");}
   @Test public void testInputGeom()                             {runTemplateTest("input.geom");}
   @Test public void testInputModule()                           {runTemplateTest("input.module");}
   @Test public void testInputProperty()                         {runTemplateTest("input.property");}
   @Test public void testInputUser()                             {runTemplateTest("input.user");}
   @Test public void testIntegralfile()                          {runTemplateTest("integralfile");}
   @Test public void testInternuc()                              {runTemplateTest("internuc");}
   @Test public void testInternucang()                           {runTemplateTest("internucang");}
   @Test public void testJob()                                   {runTemplateTest("job.info");}
   @Test public void testMcscfFinal()                            {runTemplateTest("mcscf.final");}
   @Test public void testMcscfModule()                           {runTemplateTest("mcscf.module");}
   @Test public void testMemory()                                {runTemplateTest("memory");}
   @Test public void testMp2Final()                              {runTemplateTest("mp2.final");}
   @Test public void testMp2Module()                             {runTemplateTest("mp2.module");}
   @Test public void testMp2DirectModule()                       {runTemplateTest("mp2.direct.module");}
   @Test public void testNccp()                                  {runTemplateTest("nccp");}
   @Test public void testNucdipole()                             {runTemplateTest("nucdipole");}
   @Test public void testQuartets()                              {runTemplateTest("quartets");}
   @Test public void testRimp2()                                 {runTemplateTest("rimp2.module");}
   @Test public void testScfFinal()                              {runTemplateTest("scf.final");}
   @Test public void testScfIter()                               {runTemplateTest("scf.iter");}
   @Test public void testScfModule()                             {runTemplateTest("scf.module");}
   @Test public void testSuperposatomguess()                     {runTemplateTest("superposatomguess");}
   @Test public void testSymbas()                                {runTemplateTest("symbas");}
   @Test public void testSymminfo()                              {runTemplateTest("symminfo");}
   @Test public void testSymmolorb()                             {runTemplateTest("symmolorb");}   
   @Test public void testTimes()                                 {runTemplateTest("times");}
   @Test public void testXyz()                                   {runTemplateTest("xyz");}
   @Test public void testZmatNew()                               {runTemplateTest("zmat.new");}

   /*
    * These templates are used, but for various reasons cannot be tested and should be fixed.
    */

   
   /*
    * These are the old templates which need to be integrated back into the new, more modular
    * parsing approach.
    */
   @Ignore @Test public void testAlphabetaOK()                           {runTemplateTest("alphabeta");}
   @Ignore @Test public void testAlphabeta1OK()                          {runTemplateTest("alphabeta1");}
   @Ignore @Test public void testAlphaeigsOK()                           {runTemplateTest("alphaeigs");}
   @Ignore @Test public void testAlphafoccsOK()                          {runTemplateTest("alphafoccs");}
   @Ignore @Test public void testAtombasisOK()                           {runTemplateTest("atombasis");}
   @Ignore @Test public void testAtombasisend1OK()                       {runTemplateTest("atombasisend1");}
   @Ignore @Test public void testAtomicshellscreenOK()                   {runTemplateTest("atomicshellscreen");}
   @Ignore @Test public void testBasisfuncOK()                           {runTemplateTest("basisfunc");}
   @Ignore @Test public void testBeforeaftercallOK()                     {runTemplateTest("beforeaftercall");}
   @Ignore @Test public void testBrillouinzpOK()                         {runTemplateTest("brillouinzp");}
   @Ignore @Test public void testCached1SKIP()                           {runTemplateTest("cached1");}
   @Ignore @Test public void testCcsdOKTODO()                            {runTemplateTest("ccsd.temp");}
   @Ignore @Test public void testCcsdparamSKIP()                         {runTemplateTest("ccsdparam");}
   @Ignore @Test public void testCcsdtOK()                               {runTemplateTest("ccsdt");}
   @Ignore @Test public void testCenter23TempOK()                        {runTemplateTest("center23.temp");}
   @Ignore @Test public void testCentermassOK()                          {runTemplateTest("centermass");}
   @Ignore @Test public void testCenterofchargeOK()                      {runTemplateTest("centerofcharge");}
   @Ignore @Test public void testCleanupOK()                             {runTemplateTest("cleanup");}
   @Ignore @Test public void testCondfukuiOK()                           {runTemplateTest("condfukui");}
   @Ignore @Test public void testCondfukuimuOK()                         {runTemplateTest("condfukuimu");}
   @Ignore @Test public void testConverged()                             {runTemplateTest("converged");}
   @Ignore @Test public void testCphfOK()                                {runTemplateTest("cphf");}
   @Ignore @Test public void testCrystaldipoleOK()                       {runTemplateTest("crystaldipole");}
   @Ignore @Test public void testDamping()                               {runTemplateTest("damping");}
   @Ignore @Test public void testDftenergradOK()                         {runTemplateTest("dftenergrad");}
   @Ignore @Test public void testDftgradient()                           {runTemplateTest("dftgradient");}
   @Ignore @Test public void testDftXcquadOK()                           {runTemplateTest("dft.xcquad");}
   @Ignore @Test public void testDisclaim()                              {runTemplateTest("disclaim");}
   @Ignore @Test public void testEaffileOK()                             {runTemplateTest("eaffile");}
   @Ignore @Test public void testEchoallOK()                             {runTemplateTest("echoall");}
   @Ignore @Test public void testEigqrotOK()                             {runTemplateTest("eigqrot");}
   @Ignore @Test public void testEnergycalcFIX()               {runTemplateTest("energycalc");}
   @Ignore @Test public void testEnergyminFIX()                {runTemplateTest("energymin");}
   @Ignore @Test public void testEtnew()                                 {runTemplateTest("etnew");}
   @Ignore @Test public void testFilebalanceOK()                         {runTemplateTest("filebalance");}
   @Ignore @Test public void testFinaleigenOK()                          {runTemplateTest("finaleigen");}
   @Ignore @Test public void testFockbuild()                             {runTemplateTest("fockbuild");}
   @Ignore @Test public void testFourindexOK()                           {runTemplateTest("fourindex");}
   @Ignore @Test public void testFukuiOK()                               {runTemplateTest("fukui");}
   @Ignore @Test public void testFukuiLite()                             {runTemplateTest("fukuilite");}
   @Ignore @Test public void testGama()                                  {runTemplateTest("gama");}
   @Ignore @Test public void testGlobalarray()                           {runTemplateTest("globalarray");}
   @Ignore @Test public void testHdbmWHITE()                             {runTemplateTest("hdbm");}
   @Ignore @Test public void testHomolumoOK()                            {runTemplateTest("homolumo");}
   @Ignore @Test public void testIntegdensOK()                           {runTemplateTest("integdens");}
   @Ignore @Test public void testIntegralscreenOK()                      {runTemplateTest("integralscreen");}
   @Ignore @Test public void testIsotropOK()                             {runTemplateTest("isotrop");}
   @Ignore @Test public void testIternsubOK()                            {runTemplateTest("iternsub");}
   @Ignore @Test public void testItersolOK()                             {runTemplateTest("itersol");}
   @Ignore @Test public void testItol2eOK()                              {runTemplateTest("itol2e");}
   @Ignore @Test public void testKineticenergyOK()                       {runTemplateTest("kineticenergy");}
   @Ignore @Test public void testLatticeOKFIX()                          {runTemplateTest("lattice");}
   @Ignore @Test public void testLcubeOK()                               {runTemplateTest("lcube");}
   @Ignore @Test public void testLibraryfileOK()                         {runTemplateTest("libraryfile");}
   @Ignore @Test public void testLibrarynameOK()                         {runTemplateTest("libraryname");}
   @Ignore @Test public void testLimitedOK()                             {runTemplateTest("limited");}
   @Ignore @Test public void testLindeptolOK()                           {runTemplateTest("lindeptol");}
   @Ignore @Test public void testLinesearch()                            {runTemplateTest("linesearch");}
   @Ignore @Test public void testLinestoskipOK()                         {runTemplateTest("linestoskip");}
   @Ignore @Test public void testLoading()                               {runTemplateTest("loading");}
   @Ignore @Test public void testLocalmemOK()                            {runTemplateTest("localmem");}
   @Ignore @Test public void testMagneticatomBAD()                       {runTemplateTest("magneticatom");}
   @Ignore @Test public void testMullikenOK()                            {runTemplateTest("mulliken.temp");}
   @Ignore @Test public void testMemoryUtilOK()                          {runTemplateTest("memoryUtil");}
   @Ignore @Test public void testMoleculardipoleOK()                     {runTemplateTest("moleculardipole");}
   @Ignore @Test public void testMomintOK()                              {runTemplateTest("momint");}
   @Ignore @Test public void testMullikenTempOK()                        {runTemplateTest("mulliken.temp");}
   @Ignore @Test public void testMultipoleOK()                           {runTemplateTest("multipole");}
   @Ignore @Test public void testMultipole1OK()                          {runTemplateTest("multipole1");}
   @Ignore @Test public void testNamedtensorOK()                         {runTemplateTest("namedtensor");}
   @Ignore @Test public void testNwcheminpOK()                           {runTemplateTest("nwcheminp");}
   @Ignore @Test public void testNwcheminpnwpwOK()                       {runTemplateTest("nwcheminpnwpw");}
   @Ignore @Test public void testNwcheminptitleOK()                      {runTemplateTest("nwcheminptitle");}
   @Ignore @Test public void testNwchemmp2OK()                           {runTemplateTest("nwchemmp2");}
   @Ignore @Test public void testNwpwAtomcomposOK()                      {runTemplateTest("nwpw.atomcompos");}
   @Ignore @Test public void testNwpwBadbrillouinOK()                    {runTemplateTest("nwpw.badbrillouin");}
   @Ignore @Test public void testNwpwBannerOK()                          {runTemplateTest("nwpw.banner");}
   @Ignore @Test public void testNwpwBrillouinOK()                       {runTemplateTest("nwpw.brillouin");}
   @Ignore @Test public void testNwpwCompOK()                            {runTemplateTest("nwpw.comp");}
   @Ignore @Test public void testNwpwCompgridsOK()                       {runTemplateTest("nwpw.compgrids");}
   @Ignore @Test public void testNwpwConvertingOK()                      {runTemplateTest("nwpw.converting");}
   @Ignore @Test public void testNwpwDensitycutOK()                      {runTemplateTest("nwpw.densitycut");}
   @Ignore @Test public void testNwpwElemsAtomOK()                       {runTemplateTest("nwpw.elems.atom");}
   @Ignore @Test public void testNwpwElemsOK()                           {runTemplateTest("nwpw.elems");}
   @Ignore @Test public void testNwpwElems1OK()                          {runTemplateTest("nwpw.elems1");}
   @Ignore @Test public void testNwpwEwaldOK()                           {runTemplateTest("nwpw.ewald");}
   @Ignore @Test public void testNwpwGeneratedOK()                       {runTemplateTest("nwpw.generated");}
   @Ignore @Test public void testNwpwGramschmidtOK()                     {runTemplateTest("nwpw.gramschmidt");}
   @Ignore @Test public void testNwpwInputdataOK()                       {runTemplateTest("nwpw.inputdata");}
   @Ignore @Test public void testNwpwJobOK()                             {runTemplateTest("nwpw.job");}
   @Ignore @Test public void testNwpwLatticeOK()                         {runTemplateTest("nwpw.lattice");}
   @Ignore @Test public void testNwpwLibraryOK()                         {runTemplateTest("nwpw.library");}
   @Ignore @Test public void testNwpwNelectronOK()                       {runTemplateTest("nwpw.nelectron");}
   @Ignore @Test public void testNwpwOptionsOK()                         {runTemplateTest("nwpw.options");}
   @Ignore @Test public void testNwpwPsifileOK()                         {runTemplateTest("nwpw.psifile");}
   @Ignore @Test public void testNwpwRandomOK()                          {runTemplateTest("nwpw.random");}
   @Ignore @Test public void testNwpwSupercellOK()                       {runTemplateTest("nwpw.supercell");}
   @Ignore @Test public void testNwpwSupercell1OK()                      {runTemplateTest("nwpw.supercell1");}
   @Ignore @Test public void testNwpwTechnicalOK()                       {runTemplateTest("nwpw.technical");}
   @Ignore @Test public void testNwpwTotalchargeOK()                     {runTemplateTest("nwpw.totalcharge");}
   @Ignore @Test public void testNwpwWarningOK()                         {runTemplateTest("nwpw.warning");}
   @Ignore @Test public void testNwpwWHITEFIX()                     {runTemplateTest("nwpw");}
   @Ignore @Test public void testOperationtimeOK()                       {runTemplateTest("operationtime");}
   @Ignore @Test public void testOrbitalenerOK()                         {runTemplateTest("orbitalener");}
   @Ignore @Test public void testOrbitalsymOK()                          {runTemplateTest("orbitalsym");}
   @Ignore @Test public void testParallelOK()                            {runTemplateTest("parallel");}
   @Ignore @Test public void testPrincipalcomponentsOK()                 {runTemplateTest("principalcomponents");}
   @Ignore @Test public void testPropertyOK()                            {runTemplateTest("property");}
   @Ignore @Test public void testQuadrohfOK()                            {runTemplateTest("quadrohf");}
   @Ignore @Test public void testReadmolorbOK()                          {runTemplateTest("readmolorb");}
   @Ignore @Test public void testRecordsizeOK()                          {runTemplateTest("recordsize");}
   @Ignore @Test public void testReferencesOK()                          {runTemplateTest("references");}
   @Ignore @Test public void testRohffinalTempOK()                       {runTemplateTest("rohffinal.temp");}
   @Ignore @Test public void testRtdbOK()                                {runTemplateTest("rtdb");}
   @Ignore @Test public void testScalingOK()                             {runTemplateTest("scaling");}
   @Ignore @Test public void testScfenerOK()                             {runTemplateTest("scfener");}
   @Ignore @Test public void testSchwarzOK()                             {runTemplateTest("schwarz");}
   @Ignore @Test public void testSegmentedOK()                           {runTemplateTest("segmented");}
   @Ignore @Test public void testSemidirectOK()                          {runTemplateTest("semidirect");}
   @Ignore @Test public void testShieldingFIX()                      {runTemplateTest("shielding");}
   @Ignore @Test public void testSpincontamOK()                          {runTemplateTest("spincontam");}
   @Ignore @Test public void testStepTEXT()                                  {runTemplateTest("step");}
   @Ignore @Test public void testStepLastTEXT()                              {runTemplateTest("steplast");}
   @Ignore @Test public void testSummaryresultsFIX()            {runTemplateTest("summaryresults");}
   @Ignore @Test public void testThreetwoOK()                            {runTemplateTest("threetwo");}
   @Ignore @Test public void testTidyHeadingOK()                         {runTemplateTest("tidyheading");}
   @Ignore @Test public void testTimeafterOK()                           {runTemplateTest("timeafter");}
   @Ignore @Test public void testTimePriorOK()                           {runTemplateTest("timeprior");}
   @Ignore @Test public void testTimingNEEDSWORK()                   {runTemplateTest("timing");}
   @Ignore @Test public void testTotalbandenerOK()                       {runTemplateTest("totalbandener");}
   @Ignore @Test public void testTotaldftOK()                            {runTemplateTest("totaldft");}
   @Ignore @Test public void testTriplesOK()                             {runTemplateTest("triples");}
   @Ignore @Test public void testWrotemolorbOK()                         {runTemplateTest("wrotemolorb");}
   @Ignore @Test public void testZmatautozMISSES()                       {runTemplateTest("zmatautoz");}
   @Ignore @Test public void testZmatgradFIX()                 {runTemplateTest("zmatgrad");}
   @Ignore@Test public void testZmatuserFIX()                 {runTemplateTest("zmatuser");}
   
  private void runTemplateTest(String templateName) {
    Element template = TemplateTestUtils.getTemplate(TEMPLATE_DIR+templateName+".xml", BASE_URI);
    TemplateTestUtils.runCommentExamples(template);
  }

}
