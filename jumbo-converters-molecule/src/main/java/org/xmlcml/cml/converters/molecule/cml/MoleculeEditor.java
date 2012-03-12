package org.xmlcml.cml.converters.molecule.cml;


import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.cml.CMLEditor;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.AbstractSVGTool;
import org.xmlcml.cml.tools.MoleculeLayout;
import org.xmlcml.cml.tools.MoleculeTool;

/** allows editing of CMLObjects.
 * Supports, bonds, formula, Inchi, etc.
 * Simple example here:
 * <pre>
 * usage:
 *   CMLEditor editor = new CMLEditor();
 *   editor.setAddHydrogens(true);
 *   editor.executeCommands(cmlObject);
 * </pre>
 * More examples in Tests
 * @author pm286
 *
 */
public class MoleculeEditor implements CMLEditor {

	private static final Logger LOG = Logger.getLogger(MoleculeEditor.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
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
	private Double scale2D;
	private Double average2DBondlength;
	
	public MoleculeEditor() {
	}
	
	public String getRemoveNodesXPath() {
		return removeNodesXPath;
	}

	public void setRemoveNodesXPath(String removeNodesXPath) {
		this.removeNodesXPath = removeNodesXPath;
	}

	public boolean isTransformFractionalToCartesian() {
		return transformFractionalToCartesian;
	}

	public void setTransformFractionalToCartesian(
			boolean transformFractionalToCartesian) {
		this.transformFractionalToCartesian = transformFractionalToCartesian;
	}


	public boolean isAddHydrogens() {
		return addHydrogens;
	}


	public void setAddHydrogens(boolean addHydrogens) {
		this.addHydrogens = addHydrogens;
	}


	public boolean isCalculateBonds() {
		return calculateBonds;
	}


	public void setCalculateBonds(boolean calculateBonds) {
		this.calculateBonds = calculateBonds;
	}


	public boolean isAddBondOrders() {
		return addBondOrders;
	}


	public void setAddBondOrders(boolean addBondOrders) {
		this.addBondOrders = addBondOrders;
	}


	public boolean isAdjustBondOrdersToValency() {
		return adjustBondOrdersToValency;
	}


	public void setAdjustBondOrdersToValency(boolean adjustBondOrdersToValency) {
		this.adjustBondOrdersToValency = adjustBondOrdersToValency;
	}


	public boolean isAdd2DCoordinates() {
		return add2DCoordinates;
	}


	public void setAdd2DCoordinates(boolean add2DCoordinates) {
		this.add2DCoordinates = add2DCoordinates;
	}


	public boolean isAdd3DCoordinates() {
		return add3DCoordinates;
	}


	public void setAdd3DCoordinates(boolean add3DCoordinates) {
		this.add3DCoordinates = add3DCoordinates;
	}


	public boolean isAddAtomParityFromCoordinates() {
		return addAtomParityFromCoordinates;
	}


	public void setAddAtomParityFromCoordinates(boolean addAtomParityFromCoordinates) {
		this.addAtomParityFromCoordinates = addAtomParityFromCoordinates;
	}


	public boolean isAddBondStereoFromCoordinates() {
		return addBondStereoFromCoordinates;
	}


	public void setAddBondStereoFromCoordinates(boolean addBondStereoFromCoordinates) {
		this.addBondStereoFromCoordinates = addBondStereoFromCoordinates;
	}


	public boolean isAddFormulaFromAtoms() {
		return addFormulaFromAtoms;
	}


	public void setAddFormulaFromAtoms(boolean addFormulaFromAtoms) {
		this.addFormulaFromAtoms = addFormulaFromAtoms;
	}


	public boolean isAddSMILESFromFormula() {
		return addSMILESFromFormula;
	}


	public void setAddSMILESFromFormula(boolean addSMILESFromFormula) {
		this.addSMILESFromFormula = addSMILESFromFormula;
	}
	
	/** applies previously set commands to element which is MODIFIED.
	 * 
	 * @param element
	 */
	public void executeCommand(CMLElement element) {
		if (removeNodesXPath != null) {
			Nodes nodes = element.query(removeNodesXPath, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		} else {
			List<CMLMolecule> moleculeList = getMolecules(element);
			for (CMLMolecule molecule : moleculeList) {
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				if (transformFractionalToCartesian) {
					transformFractionalToCartesian(moleculeTool);
				} else if (addHydrogens) {
					addHydrogens(moleculeTool);
				} else if (calculateBonds) {
					calculateBonds(moleculeTool);
				} else if (addBondOrders) {
					addBondOrders(moleculeTool);
				} else if (adjustBondOrdersToValency) {
					adjustBondOrdersToValency(moleculeTool);
				} else if (add2DCoordinates) {
					add2DCoordinates(moleculeTool);
				} else if (scale2D != null) {
					moleculeTool.getMolecule().multiply2DCoordsBy(scale2D);
				} else if (average2DBondlength != null) {
					moleculeTool.scaleAverage2DBondLength(average2DBondlength);
			//		moleculeTool.setAverage2DBondLength(average2DBondlength);
				} else if (add3DCoordinates) {
					add3DCoordinates(moleculeTool);
				} else if (addAtomParityFromCoordinates) {
					addAtomParityFromCoordinates(moleculeTool);
				} else if (addBondStereoFromCoordinates) {
					addBondStereoFromCoordinates(moleculeTool);
				} else if (addFormulaFromAtoms) {
					addFormulaFromAtoms(moleculeTool);
				} else if (addSMILESFromFormula) {
					addSMILESFromFormula(moleculeTool);
				} else if (addMorgan) {
					addMorgan(moleculeTool);
				} else {
					
				}
			}
		}
	}
	
	private List<CMLMolecule> getMolecules(CMLElement element) {
		List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
//		Nodes nodes = element.query(".//*[local-name()='molecule']");
//		Nodes nodes = element.query(".//cml:molecule", CML_XPATH);
		Nodes nodes = element.query(".//descendant-or-self::cml:molecule[not(ancestor::cml:molecule)]", CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			moleculeList.add((CMLMolecule) nodes.get(i));
		}
		return moleculeList;
	}
	
	private void transformFractionalToCartesian(AbstractSVGTool moleculeTool) {
		
	}
	
	private void addHydrogens(MoleculeTool moleculeTool) {
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.USE_EXPLICIT_HYDROGENS);
	}
	private void calculateBonds(MoleculeTool moleculeTool) {
		moleculeTool.calculateBondedAtoms();
	}
	private void addBondOrders(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void adjustBondOrdersToValency(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void add2DCoordinates(MoleculeTool moleculeTool) {
		MoleculeLayout moleculeLayout = 
			new MoleculeLayout(moleculeTool);
		moleculeLayout.create2DCoordinates();
	}
	private void add3DCoordinates(AbstractSVGTool moleculeTool) {
		throw new RuntimeException("ADD 3D NYI");
	}
	private void addAtomParityFromCoordinates(AbstractSVGTool moleculeTool) {
//		List<CMLAtom> atomList = moleculeTool.getMolecule().getAtoms();
//		for (CMLAtom atom : atomList) {
//			AtomTool atomTool = AtomTool.getOrCreateTool(atom);
//		}
		throw new RuntimeException("atom parity NYI");
	}
	private void addBondStereoFromCoordinates(AbstractSVGTool moleculeTool) {
//		List<CMLBond> bondList = moleculeTool.getMolecule().getBonds();
//		for (CMLBond bond : bondList) {
//			BondTool bondTool = BondTool.getOrCreateTool(bond);
//		}
//		throw new RuntimeException("bond parity NYI");
	}
	private void addFormulaFromAtoms(AbstractSVGTool moleculeTool) {
		throw new RuntimeException("addFormulaFromAtoms not yet implemented");
	}
	private void addSMILESFromFormula(AbstractSVGTool moleculeTool) {
		throw new RuntimeException("addSMILESFromFormula not yet implemented");
	}
	private void addMorgan(AbstractSVGTool moleculeTool) {
		throw new RuntimeException("addMorgan not yet implemented");
	}

	public boolean isAddMorgan() {
		return addMorgan;
	}

	public void setAddMorgan(boolean addMorgan) {
		this.addMorgan = addMorgan;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public void setScale2D(Double d) {
		this.scale2D = d;
	}

	public void set(Double average2DBondlength) {
		this.average2DBondlength = average2DBondlength;
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage();
			test();

		}
	}

	private static void test() throws Exception {
		String xmlString = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<cml xmlns='http://www.xml-cml.org/schema'>" +
				"  <molecule title='Test example'>" +
				"    <atomArray>" +
				"      <atom id='a1' elementType='C' x3='0.0' y3='0.0' z3='0.0'/>" +
				"      <atom id='a2' elementType='N' x3='1.34' y3='0.0' z3='0.0'/>" +
				"      <atom id='a3' elementType='O' x3='2.0' y3='1.1' z3='0.0'/>" +
				"    </atomArray>" +
				"  </molecule>" +
				"</cml>";
		CMLElement cmlObject = (CMLElement) new CMLBuilder().parseString(xmlString);
		MoleculeEditor editor = new MoleculeEditor();
		editor.setCalculateBonds(true);
		editor.executeCommand(cmlObject);
		cmlObject.debug("CML");
	}

	private static void usage() {
		System.out.println("usage: [zero args runs test]");
	}
}
