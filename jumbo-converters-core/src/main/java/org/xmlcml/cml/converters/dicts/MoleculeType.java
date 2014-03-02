package org.xmlcml.cml.converters.dicts;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMolecule;

public class MoleculeType extends ChemicalType implements CMLConstants {
	
	public MoleculeType(CMLMolecule molecule, ChemicalTypeMap map) {
		super(molecule);
		addNamesToMap(molecule, map);
	}


}
