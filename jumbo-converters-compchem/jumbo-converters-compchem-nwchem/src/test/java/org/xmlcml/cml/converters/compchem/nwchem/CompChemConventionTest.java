package org.xmlcml.cml.converters.compchem.nwchem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.compchem.nwchem.log.NWChemLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.nwchem.log.NWChemLogXML2CompchemConverter;

public class CompChemConventionTest {

	private static Document doc;
	
	@BeforeClass
	public static void runConverters() throws Exception {
		InputStream in = CompChemConventionTest.class.
			getResourceAsStream("/compchem/nwchem/log/jens/h2o_sto3g.nwo");
		
		NWChemLog2XMLConverter converter1 = new NWChemLog2XMLConverter();
		Element e1 = converter1.convertToXML(in);
		
		NWChemLogXML2CompchemConverter converter2 = new NWChemLogXML2CompchemConverter();
		
		Element e2 = converter2.convertToXML(e1);
		doc = CMLUtil.ensureDocument(e2);
		CMLUtil.debug(e2, new FileOutputStream("test/h2o_sto3g.xml"), 1);
	}

	@AfterClass
	public static void cleanUp() {
		doc = null;
	}
	
	@Test
	public void testConversionRuns() {
		assertNotNull(doc);
	}

	/**
<module cmlx:templateRef="nwchem.log" 
convention="convention:compchem" 
xmlns="http://www.xml-cml.org/schema" 
xmlns:cmlx="http://www.xml-cml.org/schema/cmlx" 
xmlns:conventions="http://www.xml-cml.org/convention/" 
xmlns:compchem="http://www.xml-cml.org/dictionary/compchem/" 
xmlns:n="http://www.xml-cml.org/dictionary/nwchem/" 
xmlns:x="http://www.xml-cml.org/dictionary/cmlx/" 
xmlns:h="http://www.w3.org/1999/xhtml" 
xmlns:cml="http://www.xml-cml.org/schema" 
xmlns:convention="http://www.xml-cml.org/convention/" 
xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
xmlns:nonsi="http://www.xml-cml.org/unit/nonSi/">
 <module cmlx:lineCount="1" cmlx:templateRef="argument">
	 */
	@Test
	public void testRootModuleHasCompChempConvention() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']",
			CMLConstants.CML_XPATH);
		assertEquals("Root element", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/convention/", e.getNamespaceURI("convention"));
	}

	/**
 <module id="jobList1" dictRef="compchem:jobList">
  <module id="job1" dictRef="compchem:job">
   <module id="environment" dictRef="compchem:environment">
    <parameterList/>
   </module>
  </module>
 </module>
	 */
	@Test
	public void testFindJobList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']", CMLConstants.CML_XPATH);
		assertEquals("Job list", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
		assertNotNull(e.getAttribute("id"));
	}
	
	@Test
	public void testFindJob() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']", CMLConstants.CML_XPATH);
		assertEquals("Job", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
		assertNotNull(e.getAttribute("id"));
	}
	
	@Test
	public void testFindEnvironmentModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:environment']", CMLConstants.CML_XPATH);
		assertEquals("Environment", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
	}
	
	@Test
	public void testFindEnvironmentParameterList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:environment']/cml:parameterList", CMLConstants.CML_XPATH);
		assertEquals("Environment parameter list", 1, nodes.size());
	}

	@Test
	public void testFindEnvironmentParameters() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:environment']/cml:parameterList/*", CMLConstants.CML_XPATH);
		assertFalse("Environment has parameters", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("parameter", e.getLocalName());
		}
	}
	/**
    <parameterList>
     <parameter dictRef="compchem:hostname">
      <scalar dataType="xsd:string" cmlx:templateRef="host">jmhts-MacBook-Air.local</scalar>
     </parameter>
     <parameter dictRef="compchem:executable">
      <scalar dataType="xsd:string" cmlx:templateRef="prog">nwchem</scalar>
     </parameter>
     <parameter dictRef="compchem:date">
      <scalar dataType="xsd:string" cmlx:templateRef="date">Wed Mar 14 11:38:24 2012</scalar>
     </parameter>
     <parameter dictRef="compchem:compile_date">
      <scalar dataType="xsd:string" cmlx:templateRef="compiled">Sat_Mar_03_17:07:28_2012</scalar>
     </parameter>
     <parameter dictRef="compchem:version">
      <scalar dataType="xsd:string" cmlx:templateRef="version">Development</scalar>
     </parameter>
     <parameter dictRef="compchem:nproc">
      <scalar dataType="xsd:string" cmlx:templateRef="nproc">1</scalar>
     </parameter>
    </parameterList>

	 */
	
	@Test
	public void testEnvironmentHostName() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:environment']/cml:parameterList/cml:parameter[@dictRef='compchem:hostname']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("jmhts-MacBook-Air.local", nodes.get(0).getValue());
	}
	
	@Test
	public void testEnvironmentVersion() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:environment']/cml:parameterList/cml:parameter[@dictRef='compchem:version']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("Development", nodes.get(0).getValue());
	}

	
	
	@Test
	public void testFindInitializationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']", CMLConstants.CML_XPATH);
		assertEquals("Initialization", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
	}
	
	@Test
	public void testFindInitializationParameterList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList", CMLConstants.CML_XPATH);
		assertEquals("Initialization parameter list", 1, nodes.size());
	}

	@Test
	public void testFindInitializationParameters() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/*", CMLConstants.CML_XPATH);
		assertFalse("Initialization has parameters", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("parameter", e.getLocalName());
		}
	}
	
	@Test
	public void testInitializationSCFType() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:parameterList/cml:parameter[@dictRef='compchem:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("scf", nodes.get(0).getValue());
	}
	
	@Test
	public void testInitializationBasis() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:list[@dictRef='compchem:basis_set']/cml:scalar[@dictRef='compchem:basis_set_title']/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("sto-3g", nodes.get(0).getValue());
	}
	
	@Test
	public void testInitializationMolecule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:initialization']/cml:molecule", CMLConstants.CML_XPATH);
		assertEquals(1, nodes.size());
		
		Element molecule = (Element) nodes.get(0);
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:atomArray", CMLConstants.CML_XPATH).size());
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:bondArray", CMLConstants.CML_XPATH).size());
	}

	
	
	@Test
	public void testFindCalculationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:calculation']", CMLConstants.CML_XPATH);
		assertEquals("Calculation", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
	}
	
	@Test
	public void testFindFinalizationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']", CMLConstants.CML_XPATH);
		assertEquals("Environment", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("compchem"));
	}

	@Test
	public void testFindFinalizationPropertyList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList", CMLConstants.CML_XPATH);
		assertEquals("Finalization property list", 1, nodes.size());
	}

	@Test
	public void testFindFinalizationProperties() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/*", CMLConstants.CML_XPATH);
		assertFalse("Finalization has properties", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("property", e.getLocalName());
		}
	}

	@Test
	public void testFinalizationTotalEnergy() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:propertyList/cml:property[@dictRef='compchem:total_energy']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("-74.962985614357", nodes.get(0).getValue());
	}

	@Ignore
	@Test
	public void testFinalizationMolecule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='compchem:jobList']/cml:module[@dictRef='compchem:job']/cml:module[@dictRef='compchem:finalization']/cml:molecule", CMLConstants.CML_XPATH);
		assertEquals(1, nodes.size());
		
		Element molecule = (Element) nodes.get(0);
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:atomArray", CMLConstants.CML_XPATH).size());
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:bondArray", CMLConstants.CML_XPATH).size());
	}

}
