package org.xmlcml.cml.converters.dicts;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLEntry;

public class EntryType extends ChemicalType implements CMLConstants {
	
	public EntryType(CMLEntry entry, ChemicalTypeMap map) {
		super(entry);
		addNamesToMap(entry, map);
	}

}
