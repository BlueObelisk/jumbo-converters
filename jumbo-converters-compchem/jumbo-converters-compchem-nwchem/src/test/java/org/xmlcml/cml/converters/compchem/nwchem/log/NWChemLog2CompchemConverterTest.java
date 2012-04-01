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
        //compchem:wavefunction_type
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunction_type']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("RHF", nodes.get(0).getValue());
        // task for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initial_guess", nodes.get(0).getValue());
        // 1e_energy for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:1e_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd SCF iteration total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962154715", nodes.get(0).getValue());
        //Final total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962985614357", nodes.get(0).getValue());
        // scf walltime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:walltime']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("0.0", nodes.get(0).getValue());
    }
    
    @Ignore @Test
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
        //compchem:wavefunction_type
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunction_type']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("UHF", nodes.get(0).getValue());
        // task for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initial_guess", nodes.get(0).getValue());
        // 1e_energy for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:1e_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd SCF iteration total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.9625138416", nodes.get(0).getValue());
        //Final total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962985614357", nodes.get(0).getValue());
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
        //compchem:dft_functional_title
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:dft_functional_title']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("B3LYP", nodes.get(0).getValue());
        //compchem:wavefunction_type
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunction_type']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("closed shell", nodes.get(0).getValue());
        // task for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initial_guess", nodes.get(0).getValue());
        // 1e_energy for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:1e_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd DFT iteration energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='iteration'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.3124327277", nodes.get(0).getValue()); 
        // Final total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.312451071835", nodes.get(0).getValue());
        // dft walltime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:walltime']/cml:scalar/text()", CMLConstants.CML_XPATH);
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
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.96590119", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.15025565", nodes.get(0).getValue());
        // optimisation walltime
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:walltime']/cml:scalar/text()", CMLConstants.CML_XPATH);
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
        //compchem:dft_functional_title
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:dft_functional_title']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("B3LYP", nodes.get(0).getValue());
        // Final Energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.3227749", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.17931911", nodes.get(0).getValue());
    }
    
    @Test
    public void testH2oSto3gMp2() throws Exception {
        Document doc = convertFile( FILE_DIRECTORY+"h2o_sto3g_mp2.nwo" );
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
        //compchem:wavefunction_type
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:wavefunction_type']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("RHF", nodes.get(0).getValue());
        // task for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:task']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("initial_guess", nodes.get(0).getValue());
        // 1e_energy for initial_guess
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation' and @id='initial_guess']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:1e_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-121.780484", nodes.get(0).getValue());
        // 2nd SCF iteration total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']//cml:module[@dictRef='compchem:calculation'][position()=3]/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.962154715", nodes.get(0).getValue());
        //Final total_energy
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-74.998509624188", nodes.get(0).getValue());
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
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
        assertFalse(nodes.isEmpty());
        assertEquals("-75.00613631", nodes.get(0).getValue());
        // Get z coordinate of first atom to test we have the correct molecule
        nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule/cml:atomArray/cml:atom[position()=1]/@z3", CMLConstants.CML_XPATH);
        assertEquals(1, nodes.size());
        assertEquals("0.17285474", nodes.get(0).getValue());
    }
    

}
