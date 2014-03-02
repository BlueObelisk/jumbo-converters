package org.xmlcml.cml.converters.dicts;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLSubstance;

public class SubstanceType extends ChemicalType implements CMLConstants {
	
	public SubstanceType(CMLSubstance substance, ChemicalTypeMap map) {
		super(substance);
		addNamesToMap(substance, map);
	}
}
