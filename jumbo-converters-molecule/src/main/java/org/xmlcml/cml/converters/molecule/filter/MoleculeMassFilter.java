package org.xmlcml.cml.converters.molecule.filter;

import org.xmlcml.euclid.RealRange;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.filter.AbstractCMLFilter;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;


public class MoleculeMassFilter implements AbstractCMLFilter {
	private RealRange range;
	public MoleculeMassFilter(RealRange range) {
		this.range = range;
		if (range == null) {
			throw new RuntimeException("MoleculeMassFilter Null range");
		}
	}
	
	/*
	 * @param element either CMLFormula or CMLMolecule else return false
	 */
	public boolean accept(CMLElement element) {
		boolean accept = false;
		if (element instanceof CMLMolecule) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool((CMLMolecule)element);
			accept = range.contains(moleculeTool.getCalculatedMolecularMass());
		} else if (element instanceof CMLFormula) {
			CMLFormula formula = (CMLFormula) element;
			accept = range.contains(formula.getCalculatedMolecularMass());
		}
		return accept;
	}
	
}
