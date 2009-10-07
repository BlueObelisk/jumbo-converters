package org.xmlcml.cml.converters.dicts;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLReaction;

public class ReactionType extends ChemicalType implements CMLConstants {
	
	public ReactionType(CMLReaction reaction, ChemicalTypeMap map) {
		super(reaction);
		addNamesToMap(reaction, map);
	}
}
