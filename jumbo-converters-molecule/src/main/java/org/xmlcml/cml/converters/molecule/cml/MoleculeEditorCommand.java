package org.xmlcml.cml.converters.molecule.cml;

import org.apache.log4j.Logger;

/** a command to edit a CMLObject
 * manages the commnds, not the actual operations
 * @author pm286
 *
 */
public class MoleculeEditorCommand {

	private static final Logger LOG = 
		Logger.getLogger(MoleculeEditorCommand.class);

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
		LOG.debug("set calculate bonds "+calculateBonds+" "+this);
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
		
	public boolean isAddMorgan() {
		return addMorgan;
	}

	public void setAddMorgan(boolean addMorgan) {
		LOG.debug("setMorgan: " + addMorgan);
		this.addMorgan = addMorgan;
	}
	
}
