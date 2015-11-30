package org.xmlcml.cml.converters.compchem.nwchem.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.compchem.nwchem.CompChemConventionTest;
import org.xmlcml.cml.converters.text.ClassPathXIncludeResolver;

public class NWChemLog2CompchemConverterTest {

    private static String URI_BASE = ClassPathXIncludeResolver
            .createClasspath(NWChemLog2CompchemConverterTest.class);
    
    private static String FILE_DIRECTORY="/compchem/nwchem/log/jens/";

    public static Document convertFile(String filePath) throws Exception {
        InputStream in = CompChemConventionTest.class
                .getResourceAsStream(filePath);
        NWChemLog2XMLConverter converter1 = new NWChemLog2XMLConverter();
        Element e1 = converter1.convertToXML(in);
        NWChemLogXML2CompchemConverter converter2 = new NWChemLogXML2CompchemConverter();
        Element e2 = converter2.convertToXML(e1);
        //CMLUtil.debug(e2, new FileOutputStream("debug.xml"), 1);
        Document doc = CMLUtil.ensureDocument(e2);
        return doc;
    }
    
    @Test
    public void testH2oSto3gNoTitle() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_notitle.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("scf", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("energy", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        //compchem:pointGroup
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:pointGroup']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("C2v", nodes.get(0).getValue());
        //compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("sto-3g", nodes.get(0).getValue());
        //compchem:wavefunctionType
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunctionType']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("RHF", nodes.get(0).getValue());
        // task for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initialGuess", nodes.get(0).getValue());
        // Energy_1e for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_1e']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd SCF iteration Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962154715", nodes.get(0).getValue());
        //Final Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962985614357", nodes.get(0).getValue());
        // scf wallTime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:wallTime']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
    }
    
    @Test
    public void testH2oSto3gUhf() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_uhf.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("scf", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("energy", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        //compchem:pointGroup
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:pointGroup']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("C2v", nodes.get(0).getValue());
        //compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("sto-3g", nodes.get(0).getValue());
        //compchem:wavefunctionType
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunctionType']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("UHF", nodes.get(0).getValue());
        //compchem:numAlphaElectrons
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:numAlphaElectrons']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("5", nodes.get(0).getValue());
        //compchem:numBetaElectrons - FIXME!
//        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:numBetaElectrons']/cml:scalar/text()", CMLConstants.CML_XPATH);
//        assertFalse(nodes.isEmpty());
//        assertEquals("5", nodes.get(0).getValue());
        // task for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initialGuess", nodes.get(0).getValue());
        // Energy_1e for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_1e']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd SCF iteration Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.9625138416", nodes.get(0).getValue());
        //Final Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962985614676", nodes.get(0).getValue());
        // scf wallTime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:wallTime']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
    }
    

    
    @Test
    public void testH2oSto3gDftB3lyp() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_dft_b3lyp.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("dft", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("energy", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        //compchem:dftFunctionalLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:dftFunctionalLabel']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("B3LYP", nodes.get(0).getValue());
        //compchem:wavefunctionType
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunctionType']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("closed shell", nodes.get(0).getValue());
        // task for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initialGuess", nodes.get(0).getValue());
        // Energy_1e for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_1e']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd DFT iteration energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='iteration'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.3124327277", nodes.get(0).getValue()); 
        // Final Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.312451071835", nodes.get(0).getValue());
        // dft wallTime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:wallTime']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.1", nodes.get(0).getValue());

    }
    
    @Test
    public void testH2oSto3gOpt() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_opt.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("scf", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("geometry_optimization", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        // Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.96590119", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.15025565", nodes.get(0).getValue());
        // optimisation wallTime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:wallTime']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.7", nodes.get(0).getValue());
    }


    @Test
    public void testH2oSto3gDftB3lypOpt() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_dft_b3lyp_opt.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("dft", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("geometry_optimization", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        //compchem:dftFunctionalLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:dftFunctionalLabel']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("B3LYP", nodes.get(0).getValue());
        // Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.3227749", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.17931911", nodes.get(0).getValue());
    }
    
    @Test
    public void testBenzne321gMp2() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"benzene_321g_mp2.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mp2", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("energy", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        //compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("3-21g", nodes.get(0).getValue());
        //compchem:pointGroup
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:pointGroup']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("D6h", nodes.get(0).getValue());
        //compchem:wavefunctionType
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunctionType']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("RHF", nodes.get(0).getValue());
        // task for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initialGuess", nodes.get(0).getValue());
        // Energy_1e for initialGuess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initialGuess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_1e']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-709.201704", nodes.get(0).getValue());
        // 2nd SCF iteration Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-229.4183271268", nodes.get(0).getValue());
        //Final Energy_total
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-229.94606251021", nodes.get(0).getValue());
    }
    
    @Test
    public void testH2oSto3gMp2Opt() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_mp2_opt.nwo" );
        //compchem:method
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mp2", nodes.get(0).getValue());
        //compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("geometry_optimization", nodes.get(0).getValue());
        //compchem:charge
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:charge']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
        // Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.00613631", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.17285474", nodes.get(0).getValue());
    }
    
    @Test
    public void testArScfMp2Multi() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"Ar_scf-mp2_cc-vqz.nwo" );
        
        // Check we have three jobs
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals(3, nodes.size());
        
        //1st job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mp2", nodes.get(0).getValue());
        
        //1st job: compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("cc-pvqz", nodes.get(0).getValue());
        
        // 1st Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-527.110448613487", nodes.get(0).getValue());
        
        //2nd job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mp2", nodes.get(0).getValue());
        
        //2nd job: compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("cc-pvqz", nodes.get(0).getValue());
        
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:initialization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.0", nodes.get(0).getValue());
        
        // 2nd Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-527.110448613487", nodes.get(0).getValue());
        
        //3rd job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mp2", nodes.get(0).getValue());
        
        //3rd job: compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("user specified", nodes.get(0).getValue());
        
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:initialization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.0", nodes.get(0).getValue());
        
        // 3rd Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-527.110443784669", nodes.get(0).getValue());
        
    }
    
    @Test
    public void testMCSCF() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"mcscf_ch2.nwo" );
        
        // Check we have three jobs
        List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals(3, nodes.size());
        
        //1st job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("scf", nodes.get(0).getValue());
        
        //1st job: compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("energy", nodes.get(0).getValue());
        
        //1st job: compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("6-31g**", nodes.get(0).getValue());
        
        // 1st Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=1]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-38.857160053103", nodes.get(0).getValue());
        
        //2nd job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mcscf", nodes.get(0).getValue());
        
        //2nd job: compchem:task
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("geometry_optimization", nodes.get(0).getValue());
        
        //2nd job: compchem:basisSetLabel
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basisSet']/cml:scalar[@dictRef='compchem:basisSetLabel']/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("6-31g**", nodes.get(0).getValue());
        
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:initialization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.205", nodes.get(0).getValue());
        
        // 2nd Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-38.95876065", nodes.get(0).getValue());
        
        
        // 2nd Job final molecule - Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=2]/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.23336589", nodes.get(0).getValue());
        
        //3rd job: compchem:method
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("mcscf", nodes.get(0).getValue());
        
        
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:initialization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.23336589", nodes.get(0).getValue());
        
        // 3rd Job - Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job' and position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:Energy_total']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-38.916059261199", nodes.get(0).getValue());
        
    }
}
