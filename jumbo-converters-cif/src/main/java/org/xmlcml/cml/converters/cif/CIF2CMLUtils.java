package org.xmlcml.cml.converters.cif;

import nu.xom.Nodes;

import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.Type;

public class CIF2CMLUtils implements CMLConstants {
	
	public static boolean isGlobalBlock(CIFDataBlock block) {
		Nodes crystNds = block.query(".//item[@name='_cell_length_a']");
		Nodes molNds = block.query(".//loop[contains(concat(' ',@names,' '),' _atom_site_label ')]");
		Nodes symNds = block.query(".//loop[contains(concat(' ',@names,' '),' _symmetry_equiv_pos_as_xyz ')]");
		if (crystNds.size() == 0 && molNds.size() == 0 && symNds.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public final static int MAX_RINGS = 15;

	public static enum DisorderType {
		UNPROCESSED,
		PROCESSED,
		NONE;
	}

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

	public static CompoundClass getCompoundClass(CMLMolecule molecule) {
		boolean hasMetal = false;
		boolean hasC = false;
		boolean hasH = false;
		for (CMLAtom atom : molecule.getAtoms()) {
			if (atom.getChemicalElement().isChemicalElementType(Type.METAL)) {
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
		} else{
			if (hasH && hasC) {
				return CompoundClass.ORGANOMETALLIC;
			} else {
				return CompoundClass.INORGANIC;
			}
		}
	}

}
