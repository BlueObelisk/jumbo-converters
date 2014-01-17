package org.xmlcml.cml.converters.molecule.fragments;

import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.StereochemistryTool;


public class CMLUtils {
	
	public static CMLMolecule getFirstParentMolecule(CMLElement cml) {
		Nodes moleculeNodes = cml.query(CMLMolecule.NS, CML_XPATH);
		if (moleculeNodes.size() != 1) {
			return null;
		}
		return (CMLMolecule) moleculeNodes.get(0);
	}
	
	public static boolean hasBondOrdersAndCharges(CMLMolecule molecule) {
		boolean hasBOAC = true;
		Nodes flagNodes = molecule.query(".//"+CMLMetadata.NS+"[@dictRef='"+CrystalEyeConstants.NO_BONDS_OR_CHARGES_FLAG_DICTREF+"']", CML_XPATH);
		if (flagNodes.size() > 0) {
			hasBOAC = false;
		}
		return hasBOAC;
	}
	
	/**
	 * <p>
	 * Calculates the InChI for the provided CMLMolecule and appends
	 * it as a CMLIdentifier. 
	 * </p>
	 * 
	 * @param molecule
	 */
	public static void calculateAndAddInchi(CMLMolecule molecule) {
		String inchi = InchiTool.generateInchi(molecule, "");
		CMLIdentifier identifier = new CMLIdentifier();
		identifier.setConvention("iupac:inchi");
		identifier.appendChild(new Text(inchi));
		molecule.appendChild(identifier);
	}

	/**
	 * <p>
	 * Calculates the SMILES for the provided CMLMolecule and appends
	 * it as a CMLIdentifier. 
	 * </p>
	 * 
	 * @param mol
	 */
	public static void calculateAndAddSmiles(CMLMolecule mol) {
		String smiles = SmilesTool.generateSmiles(mol);
		if (smiles != null) {
			Element scalar = new Element("identifier", CML_NS);
			scalar.addAttribute(new Attribute("convention", "daylight:smiles"));
			scalar.appendChild(new Text(smiles));
			mol.appendChild(scalar);
		}
	}
	
	/**
	 * <p>
	 * Sets the order of all bonds in the provided CMLMolecule.
	 * </p>
	 * 
	 * @param molecule
	 * @param order
	 */
	public static void setAllBondOrders(CMLMolecule molecule, String order) {
		for (CMLBond bond : molecule.getBonds()) {
			bond.setOrder(order);
		}
	}
	
	/**
	 * <p>
	 * Rearranges the ordering of the atom IDs for all bonds in the
	 * provided CMLMolecule so that if there is a chiral atom ID, it 
	 * will come first.  (Can't remember why this is important =( ned24).
	 * </p>
	 * 
	 * @param molecule
	 */
	public static void rearrangeChiralAtomsInCMLBondIds(CMLMolecule molecule) {
		for (CMLMolecule subMol : molecule.getDescendantsOrMolecule()) {
			StereochemistryTool st = new StereochemistryTool(subMol);
			List<CMLAtom> chiralAtoms = st.getChiralAtoms();
			List<CMLBond> toRemove = new ArrayList<CMLBond>();
			List<CMLBond> toAdd = new ArrayList<CMLBond>();
			for (CMLBond bond : subMol.getBonds()) {
				CMLAtom secondAtom = bond.getAtom(1);
				if (chiralAtoms.contains(secondAtom)) {
					CMLBond newBond = new CMLBond(bond);
					newBond.setAtomRefs2(bond.getAtom(1).getId()+" "+bond.getAtom(0).getId());
					newBond.resetId(bond.getAtom(1).getId()+"_"+bond.getAtom(0).getId());
					toAdd.add(newBond);
					toRemove.add(bond);
				}
			}
			for (CMLBond bond : toRemove) {
				bond.detach();
			}
			for (CMLBond bond : toAdd) {
				subMol.addBond(bond);
			}
		}
	}	

}
