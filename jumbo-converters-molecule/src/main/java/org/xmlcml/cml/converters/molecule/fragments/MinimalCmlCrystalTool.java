package org.xmlcml.cml.converters.molecule.fragments;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.util.List;

import nu.xom.Document;
import nu.xom.Nodes;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLMolecule;


/**
 * <p>
 * Takes a CML Molecule and strips away all parts that are not
 * necessary to describe a crystals unit cell and contained
 * connection tables.
 * </p>
 * 
 * @author Nick Day
 * @version 0.1
 *
 */
public class MinimalCmlCrystalTool {

	private CMLMolecule oldMol;
	private CMLMolecule minimalMol;
	
	private static int NUM_DECIMAL_PLACES = 3;

	// hide the default constructor
	private MinimalCmlCrystalTool() {
		;
	}

	public MinimalCmlCrystalTool(CMLMolecule mol) {
		this.oldMol = (CMLMolecule)mol.copy();
	}

	/**
	 * <p>
	 * Gets the minimal CML Molecule.
	 * </p>
	 * 
	 * @return the new CML Molecule as an XML Document
	 * 
	 */
	public Document getMolAsDocument() {
		return new Document(getMinimalMol());
	}
	
	public CMLMolecule getMinimalMol() {
		minimalMol = new CMLMolecule();
		copyCrystal();
		copyMolecules();
		return minimalMol;
	}

	private void copyMolecules() {
		List<CMLMolecule> molList = oldMol.getDescendantsOrMolecule();
		if (molList.size() == 1) {
			// there is only one moiety
			copyAtomsAndBonds(oldMol, minimalMol);
		} else {
			// there is more than one moiety
			for (CMLMolecule mol : molList) {
				CMLMolecule moiety = new CMLMolecule();
				minimalMol.addMolecule(moiety);
				copyAtomsAndBonds(mol, moiety);
			}
		}
	}

	private void copyAtomsAndBonds(CMLMolecule fromMol, CMLMolecule toMol) {
		for (CMLAtom atom : fromMol.getAtoms()) {
			CMLAtom newAtom = new CMLAtom();
			newAtom.setId(atom.getId());
			newAtom.setElementType(atom.getElementType());
			newAtom.setFormalCharge(atom.getFormalCharge());
			double x3 = Utils.round(atom.getX3(), NUM_DECIMAL_PLACES);
			newAtom.setX3(x3);
			double y3 = Utils.round(atom.getY3(), NUM_DECIMAL_PLACES);
			newAtom.setY3(y3);
			double z3 = Utils.round(atom.getZ3(), NUM_DECIMAL_PLACES);
			newAtom.setZ3(z3);
			toMol.addAtom(newAtom);
		}
		for (CMLBond bond : fromMol.getBonds()) {
			CMLBond newBond = new CMLBond();
			newBond.setAtomRefs2(bond.getAtomRefs2());
			newBond.setOrder(bond.getOrder());
			toMol.addBond(newBond);
		}
	}

	private void copyCrystal() {
		CMLCrystal crystal = (CMLCrystal)oldMol.getFirstCMLChild(CMLCrystal.TAG);
		if (crystal == null) {
			return;
		}
		crystal.detach();
		Nodes nds = crystal.query(".//cml:symmetry/child::cml:*", CML_XPATH);
		for (int i = 0; i < nds.size(); i++) {
			nds.get(i).detach();
		}
		minimalMol.appendChild(crystal);
	} 

}
