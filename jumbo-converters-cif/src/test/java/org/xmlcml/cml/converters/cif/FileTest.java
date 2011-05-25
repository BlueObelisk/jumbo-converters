package org.xmlcml.cml.converters.cif;

import java.io.File;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;

public class FileTest {

	@Test
	public void testCIFMolecules() throws Exception {
		File file = new File("src/test/resources/cif/cif/in/cv5056.cif");
		CIF cif = (CIF) new CIFParser().parse(file).getRootElement();
	
		Element rawCml = new CIFXML2CMLConverter().convertToXML(cif);
		Element completeCml = new RawCML2CompleteCMLConverter().convertToXML(rawCml);
	
		CMLMolecule molecule = (CMLMolecule) CMLUtil.getQueryNodes(completeCml, "//cml:molecule", CMLConstants.CML_XPATH).get(0);
		Assert.assertEquals("single molecule", 0, molecule.getMoleculeCount());
		Assert.assertEquals("single molecule size", 32, molecule.getAtomCount());
	}

	@Test
	public void testCIFMolecules1() throws Exception {
		File file = new File("src/test/resources/cif/cif/in/o1060.cif");
		CIF cif = (CIF) new CIFParser().parse(file).getRootElement();
	
		Element rawCml = new CIFXML2CMLConverter().convertToXML(cif);
		Element completeCml = new RawCML2CompleteCMLConverter().convertToXML(rawCml);

		CMLMolecule molecule = (CMLMolecule) CMLUtil.getQueryNodes(completeCml, "//cml:molecule", CMLConstants.CML_XPATH).get(0);
		Assert.assertEquals("multiple molecules", 2, molecule.getChildElements().size());
		CMLMolecule molecule0 = (CMLMolecule)molecule.getChildElements().get(0);
		Assert.assertEquals("molecule size 0", 20, molecule0.getAtomCount());
		CMLMolecule molecule1 = (CMLMolecule)molecule.getChildElements().get(1);
		Assert.assertEquals("molecule size 1", 5, molecule1.getAtomCount());
	}

}
