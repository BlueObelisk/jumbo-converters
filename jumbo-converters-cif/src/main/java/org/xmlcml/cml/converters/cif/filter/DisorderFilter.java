package org.xmlcml.cml.converters.cif.filter;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.filter.AbstractCMLFilter;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.DisorderTool;

// i/mport org.xmlcml.cml.converters.filter.AbstractCMLFilter;

public class DisorderFilter implements AbstractCMLFilter {

	public DisorderFilter() {
		
	}
	public boolean accept(CMLElement element) {
		boolean accept = false;
		if (element != null && element instanceof CMLMolecule) {
			CMLMolecule molecule = (CMLMolecule) element;
			accept = DisorderTool.isDisordered(molecule);
		}
		return accept;
	}

	
}
