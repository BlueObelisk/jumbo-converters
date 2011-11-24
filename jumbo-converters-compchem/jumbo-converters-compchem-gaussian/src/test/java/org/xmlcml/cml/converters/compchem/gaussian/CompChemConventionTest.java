package org.xmlcml.cml.converters.compchem.gaussian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.XML2XMLConverter;
import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.gaussian.log.GaussianLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.log.GaussianLogXML2CompchemConverter;
import org.xmlcml.euclid.Util;

public class CompChemConventionTest {

	private static Document doc;
	
	@BeforeClass
	public static void runConverters() throws Exception {
		InputStream in = CompChemConventionTest.class.
			getResourceAsStream("/compchem/gaussian/log/anna/1/output.log");
		
		CompchemText2XMLTemplateConverter converter1 = new GaussianLog2XMLConverter();
		Element e1 = converter1.convertToXML(in);
		
		InputStream transformStream = Util.getResourceUsingContextClassLoader(
				"org/xmlcml/cml/converters/compchem/gaussian/log/gaussian2compchem.xml", GaussianLogXML2CompchemConverter.class);
		XML2XMLConverter converter2 = new GaussianLogXML2CompchemConverter(transformStream);
		
		Element e2 = converter2.convertToXML(e1);
		
		doc = CMLUtil.ensureDocument(e2);
	}

	@AfterClass
	public static void cleanUp() {
		doc = null;
	}
	
	@Test
	public void testConversionRuns() {
		assertNotNull(doc);
	}
	
	@Test
	public void testRootModuleHasCompChempConvention() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']", CMLConstants.CML_XPATH);
		assertEquals("Root element", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/convention/", e.getNamespaceURI("convention"));
	}
	
	@Test
	public void testFindJobList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']", CMLConstants.CML_XPATH);
		assertEquals("Job list", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
		assertNotNull(e.getAttribute("id"));
	}
	
	@Test
	public void testFindJob() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']", CMLConstants.CML_XPATH);
		assertEquals("Job", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
		assertNotNull(e.getAttribute("id"));
	}
	
	@Test
	public void testFindEnvironmentModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:environment']", CMLConstants.CML_XPATH);
		assertEquals("Environment", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
	}
	
	@Test
	public void testFindEnvironmentParameterList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:environment']/cml:parameterList", CMLConstants.CML_XPATH);
		assertEquals("Environment parameter list", 1, nodes.size());
	}

	@Test
	public void testFindEnvironmentParameters() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:environment']/cml:parameterList/*", CMLConstants.CML_XPATH);
		assertFalse("Environment has parameters", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("parameter", e.getLocalName());
		}
	}
	
	@Test
	public void testEnvironmentHostName() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:environment']/cml:parameterList/cml:parameter[@dictRef='cc:hostname']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("GINC-DEEPTHOUGHT", nodes.get(0).getValue());
	}
	
	@Test
	public void testEnvironmentVersion() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:environment']/cml:parameterList/cml:parameter[@dictRef='cc:version']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("x86-Linux-G03RevB.04", nodes.get(0).getValue());
	}

	
	
	@Test
	public void testFindInitializationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']", CMLConstants.CML_XPATH);
		assertEquals("Initialization", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
	}
	
	@Test
	public void testFindInitializationParameterList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']/cml:parameterList", CMLConstants.CML_XPATH);
		assertEquals("Initialization parameter list", 1, nodes.size());
	}

	@Test
	public void testFindInitializationParameters() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']/cml:parameterList/*", CMLConstants.CML_XPATH);
		assertFalse("Initialization has parameters", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("parameter", e.getLocalName());
		}
	}
	
	@Test
	public void testInitializationMethod() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']/cml:parameterList/cml:parameter[@dictRef='cc:method']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("RB3LYP", nodes.get(0).getValue());
	}
	
	@Test
	public void testInitializationBasis() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']/cml:parameterList/cml:parameter[@dictRef='cc:basis']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("6-31G(d)", nodes.get(0).getValue());
	}
	
	@Test
	public void testInitializationMolecule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:initialization']/cml:molecule", CMLConstants.CML_XPATH);
		assertEquals(1, nodes.size());
		
		Element molecule = (Element) nodes.get(0);
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:atomArray", CMLConstants.CML_XPATH).size());
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:bondArray", CMLConstants.CML_XPATH).size());
	}

	
	
	@Test
	public void testFindCalculationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:calculation']", CMLConstants.CML_XPATH);
		assertEquals("Calculation", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
	}
	
	@Test
	public void testFindFinalizationModule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:finalization']", CMLConstants.CML_XPATH);
		assertEquals("Environment", 1, nodes.size());
		Element e = (Element) nodes.get(0);
		assertEquals("http://www.xml-cml.org/dictionary/compchem/", e.getNamespaceURI("cc"));
	}

	@Test
	public void testFindFinalizationPropertyList() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:finalization']/cml:propertyList", CMLConstants.CML_XPATH);
		assertEquals("Finalization property list", 1, nodes.size());
	}

	@Test
	public void testFindFinalizationProperties() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:finalization']/cml:propertyList/*", CMLConstants.CML_XPATH);
		assertFalse("Finalization has properties", nodes.isEmpty());
		for (Node node : nodes) {
			Element e = (Element) node;
			assertEquals("property", e.getLocalName());
		}
	}
	
	@Test
	public void testFinalizationHFEnergy() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:finalization']/cml:propertyList/cml:property[@dictRef='cc:hfenergy']/cml:scalar/text()", CMLConstants.CML_XPATH);
		assertFalse(nodes.isEmpty());
		assertEquals("-40.5183892", nodes.get(0).getValue());
	}

	@Test
	public void testFinalizationMolecule() {
		List<Node> nodes = CMLUtil.getQueryNodes(doc, "/cml:*[@convention='convention:compchem']/cml:module[@dictRef='cc:jobList']/cml:module[@dictRef='cc:job']/cml:module[@dictRef='cc:finalization']/cml:molecule", CMLConstants.CML_XPATH);
		assertEquals(1, nodes.size());
		
		Element molecule = (Element) nodes.get(0);
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:atomArray", CMLConstants.CML_XPATH).size());
		assertEquals(1, CMLUtil.getQueryNodes(molecule, "./cml:bondArray", CMLConstants.CML_XPATH).size());
	}

}
