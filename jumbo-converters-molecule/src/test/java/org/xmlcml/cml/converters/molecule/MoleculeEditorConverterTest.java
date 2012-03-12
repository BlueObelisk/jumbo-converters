package org.xmlcml.cml.converters.molecule;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.cml.CMLEditorList;
import org.xmlcml.cml.converters.molecule.cml.MoleculeEditor;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.cml.tools.SMILESTool;

public class MoleculeEditorConverterTest {

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
		CMLEditorList cmlEditorConverter = new CMLEditorList();
		MoleculeEditor moleculeEditor = new MoleculeEditor();
		moleculeEditor.setAddHydrogens(true);
		cmlEditorConverter.add(moleculeEditor);
		CMLElement cmlOut =  (CMLElement) cmlEditorConverter.convertToXML(molIn);
		JumboTestUtils.assertEqualsIncludingFloat("add hydrogens", JumboTestUtils.parseValidFile("editor/addHydrogens.xml"), cmlOut, true, 0.00001);
	}
	
	@Test
	public void addCoordinates2D1() {
		CMLMolecule molIn = SMILESTool.createMolecule("[C][O][N]");
		CMLEditorList cmlEditorConverter = new CMLEditorList();
		MoleculeEditor moleculeEditor = new MoleculeEditor();
		moleculeEditor.setAdd2DCoordinates(true);
		cmlEditorConverter.add(moleculeEditor);
		CMLElement cmlOut =  (CMLElement) cmlEditorConverter.convertToXML(molIn);
//		cmlOut.debug("out");
		JumboTestUtils.assertEqualsIncludingFloat("add coordinates", JumboTestUtils.parseValidFile("editor/addCoordinates2D-no-hydrogen.xml"), cmlOut, true, 0.00001);
	}
	
	@Test
	@Ignore ("bug in groups")
	public void addCoordinates2D() {
		CMLMolecule molIn = SMILESTool.createMolecule("CON");
		CMLEditorList cmlEditorConverter = new CMLEditorList();
		MoleculeEditor moleculeEditor = new MoleculeEditor();
		moleculeEditor.setAdd2DCoordinates(true);
		cmlEditorConverter.add(moleculeEditor);
		CMLElement cmlOut =  (CMLElement) cmlEditorConverter.convertToXML(molIn);
		cmlOut.debug("out");
		JumboTestUtils.assertEqualsIncludingFloat("add coordinates2D", 
					JumboTestUtils.parseValidFile("editor/addCoordinates2D.xml"), cmlOut, true, 0.00001);
	}
}
