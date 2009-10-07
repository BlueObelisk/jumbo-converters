package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cif.CIF;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.euclid.Util;

public class CML2CIFConverter extends AbstractConverter {

	public int getConverterVersion() {
		return 0;
	}

	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CIF;
	}
	
	@Override
	public List<String> convertToText(Element element) {
		List<String> lines = null;
		CMLElement rootElement = null;
		if (element instanceof CMLElement) {
			rootElement = (CMLElement) element;
			CIF cif = new CIF();
			lines = new ArrayList<String>();
			convertAndAddAtoms(cif, rootElement, lines);
			convertAndAddBonds(cif, rootElement, lines);
		}
		return lines;
	}
	
	private void convertAndAddAtoms(CIF cif, CMLElement element, List<String> lines) {
/*
 # chemical_conn_atom_[]

    * chemical_conn_atom_charge
    * chemical_conn_atom_display_
    * chemical_conn_atom_NCA
    * chemical_conn_atom_NH
    * chemical_conn_atom_number
    * chemical_conn_atom_type_symbol 
*/
		lines.add("_chemical_conn_atom_number");
		lines.add("_chemical_conn_atom_type_symbol"); 
		lines.add("_chemical_conn_atom_charge");
		lines.add("_chemical_conn_atom_display_x");
		lines.add("_chemical_conn_atom_display_y");
		lines.add("_chemical_conn_atom_NCA");
		lines.add("_chemical_conn_atom_NH");
		Nodes atoms = element.query("//cml:atom", CML_XPATH);
		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom atom = (CMLAtom) atoms.get(i);
			lines.add(
					atom.getId().substring(1) + S_SPACE +
					atom.getElementType() + S_SPACE +
					atom.getFormalCharge() + S_SPACE +
					Util.trimFloat(atom.getX2(), 3) + S_SPACE +
					Util.trimFloat(atom.getY2(), 3) + S_SPACE +
					(atom.getLigandAtoms().size() - atom.getLigandHydrogenAtoms().size()) + S_SPACE +
			atom.getLigandHydrogenAtoms().size()
			);
		}
	}

	private void convertAndAddBonds(CIF cif, CMLElement element, List<String> lines) {
/*# chemical_conn_bond_[]

    * chemical_conn_bond_atom_
    * chemical_conn_bond_type  */
		
		lines.add("_chemical_conn_bond_atom_1");
		lines.add("_chemical_conn_bond_atom_2");
		lines.add("_chemical_conn_bond_type");
		Nodes bonds = element.query("//cml:bond", CML_XPATH);
		for (int i = 0; i < bonds.size(); i++) {
			CMLBond bond = (CMLBond) bonds.get(i);
			lines.add(
					bond.getAtom(0).getId().substring(1) + S_SPACE +
					bond.getAtom(1).getId().substring(1) + S_SPACE +
					bond.getOrder()
					);
		}
	}
}
