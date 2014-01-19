package org.xmlcml.cml.converters.molecule.fragments;

import java.util.List;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.Type;

public class ChemistryUtils {
	
	public static enum CompoundClass {
		ORGANIC("organic"),
		INORGANIC("inorganic"),
		ORGANOMETALLIC("organometallic");

		private CompoundClass(String name) {
			this.name = name;
		}

		private final String name;

		public String toString() {
			return name;
		}
	}
	
	public static boolean isBoringMolecule(CMLMolecule molecule) {
		// skip boring moieties
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		String formulaS = formula.getConcise();
		formulaS = CMLFormula.removeChargeFromConcise(formulaS);
		if (formulaS.equals("H 2 O 1") || 
				formulaS.equals("H 3 O 1") ||
				formulaS.equals("H 4 O 1") ||
				molecule.getAtomCount() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static CompoundClass getCompoundClass(CMLMolecule molecule) {
		boolean hasMetal = false;
		boolean hasC = false;
		boolean hasH = false;
		for (CMLAtom atom : molecule.getAtoms()) {
			if (isMetal(atom)) {
				hasMetal = true;
			}
			String elType = atom.getElementType();
			if ("H".equals(elType)) {
				hasH = true;
			} else if ("C".equals(elType)) {
				hasC = true;
			}
		}
		if (!hasMetal) {
			return CompoundClass.ORGANIC;
		} else if (hasMetal) {
			if (hasH && hasC) {
				return CompoundClass.ORGANOMETALLIC;
			} else {
				return CompoundClass.INORGANIC;
			}
		} else {
			throw new RuntimeException("BUG: molecules should always be assigned one of the above classes.");
		}
	}

	public static boolean isMetal(CMLAtom atom) {
		return atom.getChemicalElement().isChemicalElementType(Type.METAL);
	}

	public static boolean containsMetal(CMLMolecule molecule) {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (ChemistryUtils.isMetal(atom)) {
				return true;
			}
		}
		return false;
	}

}
