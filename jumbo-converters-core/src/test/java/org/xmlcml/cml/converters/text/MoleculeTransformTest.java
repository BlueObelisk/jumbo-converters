package org.xmlcml.cml.converters.text;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;

public class MoleculeTransformTest {
	
	@Test 
	public void testTransformMolecule() {
		TransformTest.runTest("convolute", 
			"<moleculeTransform process='convoluteProperty'" +
			"   xpath='.//cml:molecule' valueXpath='.//cml:scalar' args='npasses=1 damping=1.0'/>",
			CMLUtil.readElementFromResource("org/xmlcml/cml/converters/text/transformMolecule.xml"),
		    CMLUtil.readElementFromResource("org/xmlcml/cml/converters/text/transformMoleculeRef.xml")
			);
	}

	
}
