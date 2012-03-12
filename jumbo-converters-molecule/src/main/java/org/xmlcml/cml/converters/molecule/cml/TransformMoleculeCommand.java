package org.xmlcml.cml.converters.molecule.cml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public enum TransformMoleculeCommand {
	fractionalToCartesian("fract2cart"),
	addHydrogens("addh"),
	addHydrogen2D("addh2d"),
	addHydrogen3D("addh3d"),
	calculateBonds("bonds"),
	addBondOrders("orders"),
	adjustBondOrdersToValency("valency"),
	add2DCoordinates("2dcoord"),
	add3DCoordinates("3dcoord"),
	addAtomParityFromCoordinates("parity"),
	addBondStereoFromCoordinates("bondStereo"),
	addFormulaFromAtoms("formula"),
	addSMILESFromFormula("smiles"),
	addMorgan("morgan"), 
	;
	private static Map<String, TransformMoleculeCommand> map;
	private TransformMoleculeCommand(String abbrev) {
		init(abbrev, this);
	}
	private static void init(String abbrev, TransformMoleculeCommand tmc) {
		ensureMap();
		map.put(abbrev, tmc);
	}
	
	private static void ensureMap() {
		if (map == null) {
			map = new HashMap<String, TransformMoleculeCommand>();
		}
	}
	public static TransformMoleculeCommand getCommand(String abbrev) {
		return map.get(abbrev);
	}
	public static Set<String> getAbbreviations() {
		return map.keySet();
	}
}
