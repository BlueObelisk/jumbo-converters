package org.xmlcml.cml.converters.dicts;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLConstants;

public class UnitTypeType extends ChemicalType implements CMLConstants {
	
	public UnitTypeType(Element element, ChemicalTypeMap map) {
		super(element);
		addNamesToMap(element, map);
	}


}
