package org.xmlcml.cml.converters.cml;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.cml.tools.SMILESTool;

public class EditorTest {

/*
	private String  removeNodesXPath;
	private boolean transformFractionalToCartesian;
	private boolean addHydrogens;
	private boolean calculateBonds;
	private boolean addBondOrders;
	private boolean adjustBondOrdersToValency;
	private boolean add2DCoordinates;
	private boolean add3DCoordinates;
	private boolean addAtomParityFromCoordinates;
	private boolean addBondStereoFromCoordinates;
	private boolean addFormulaFromAtoms;
	private boolean addSMILESFromFormula;
	private boolean addMorgan;
	private String xpath;
 */
	@Test
	public void addHydrogens() {
		CMLMolecule molIn = SMILESTool.createMolecule("[C][O][N]");
		CMLEditorConverter cmlEditorConverter = new CMLEditorConverter();
		cmlEditorConverter.getCmlEditor().setAddHydrogens(true);
		CMLElement cmlOut =  (CMLElement) cmlEditorConverter.convertToXML(molIn);
		JumboTestUtils.assertEqualsIncludingFloat("add hydrogens", JumboTestUtils.parseValidFile("editor/addHydrogens.xml"), cmlOut, true, 0.00001);
	}
	
	@Test
	public void addCoordinates2D() {
		CMLMolecule molIn = SMILESTool.createMolecule("CON");
		CMLEditorConverter cmlEditorConverter = new CMLEditorConverter();
		try {
			cmlEditorConverter.getCmlEditor().setAdd2DCoordinates(true);
			CMLElement cmlOut =  (CMLElement) cmlEditorConverter.convertToXML(molIn);
		JumboTestUtils.assertEqualsIncludingFloat("add hydrogens", JumboTestUtils.parseValidFile("editor/addHydrogens.xml"), cmlOut, true, 0.00001);
			Assert.fail("expected NYI");
		} catch (RuntimeException e) {
			
		}
	}
}
