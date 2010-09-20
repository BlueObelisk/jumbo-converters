package org.xmlcml.cml.converters.molecule.filter;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.filter.AbstractCMLFilter;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.molutil.ChemicalElement;

public class AtomicNumberFilter implements AbstractCMLFilter {
	private IntRange range;
	public AtomicNumberFilter(IntRange range) {
		if (range == null) {
			throw new RuntimeException("AtomicNumberFilter null range");
		}
		this.range = range;
	}
	
	public AtomicNumberFilter(String lowElementType, String highElementType) {
		if (lowElementType == null || highElementType == null) {
			throw new RuntimeException("AtomicNumberFilter null elementType");
		}
		ChemicalElement elLow = ChemicalElement.getChemicalElement(lowElementType);
		ChemicalElement elHigh = ChemicalElement.getChemicalElement(highElementType);
		if (elLow == null || elHigh == null) {
			throw new RuntimeException("AtomicNumberFilter unknown elementTypes");
		}
		range = new IntRange(elLow.getAtomicNumber(), elHigh.getAtomicNumber());
	}
	
	public boolean accept(CMLElement element) {
		boolean accept = true;
		if (element != null && element instanceof CMLFormula) {
			CMLFormula formula = (CMLFormula) element;
			String[] testElementTypes = formula.getElementTypes();
			for (String elementType : testElementTypes) {
				ChemicalElement el = ChemicalElement.getChemicalElement(elementType);
				if (el == null) {
					accept = false;
				} else {
					int atNum = el.getAtomicNumber();
					accept = range.contains(atNum);
				}
				if (!accept) {
					break;
				}
			}
		} else if(element instanceof CMLMolecule) {
			CMLFormula formula = CMLFormula.createFormula((CMLMolecule) element);
			accept = accept(formula);
		}
		return accept;
	}
}
