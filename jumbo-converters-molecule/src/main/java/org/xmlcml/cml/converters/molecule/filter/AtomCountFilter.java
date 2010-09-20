package org.xmlcml.cml.converters.molecule.filter;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.filter.AbstractCMLFilter;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;

public class AtomCountFilter implements AbstractCMLFilter {
	private static Double EPS = 0.000000000000000000000000001;
	private CMLFormula low;
	private CMLFormula high;
	private boolean allowOtherElements;
	private String[] lowElementTypes;
	private double[] lowCounts;
	private double[] highCounts;

	public AtomCountFilter(CMLFormula low, CMLFormula high, boolean allowOtherElements) {
		if (high == null || low == null) {
			throw new RuntimeException("Must give high or low formula");
		}
		init(low, high, allowOtherElements);
	}

	private void init(CMLFormula low, CMLFormula high,
			boolean allowOtherElements) {
		this.high = high;
		this.low = low;
		createAlignedFormulaWithZeroElementCounts(high);
		this.allowOtherElements = allowOtherElements;
		lowElementTypes = low.getElementTypes();
		lowCounts = low.getCounts();
		highCounts = createAlignedCounts(lowElementTypes, lowCounts);
	}
	
	public AtomCountFilter(String low, String high, boolean allowOtherElements) {
		if (high == null || low == null) {
			throw new RuntimeException("Must give high or low formula");
		}
		init(CMLFormula.createFormula(low), CMLFormula.createFormula(high), allowOtherElements);
	}
	
	private void createAlignedFormulaWithZeroElementCounts(CMLFormula high) {
		String[] highElems = high.getElementTypes();
		for (int i = 0; i < highElems.length; i++) {
			Double lowCount = low.getElementCount(highElems[i]);
			if (lowCount == null) {
				this.low.add(highElems[i], 0.0);
			}
		}
	}
	
	/** 
	 * @param element either CMLFormula or molecule
	 */
	public boolean accept(CMLElement element) {
		boolean accept = true;
		if (element != null && element instanceof CMLFormula) {
			CMLFormula formula = (CMLFormula) element;
			String[] testElementTypes = formula.getElementTypes();
			accept = checkCountInTestForElementTypesInLow(formula);
			if (!allowOtherElements && testElementTypes.length > lowElementTypes.length) {
				accept = false;
			}
		} else if (element instanceof CMLMolecule) {
			CMLFormula formula = CMLFormula.createFormula((CMLMolecule) element);
			accept = accept(formula);
		} else {
			accept = false;
		}
		return accept;
	}
	private boolean checkCountInTestForElementTypesInLow(CMLFormula formula) {
		boolean accept = true;
		for (int i = 0; i < lowElementTypes.length; i++) {
			Double tc = formula.getElementCount(lowElementTypes[i]);
			if (tc == null) {
				if (lowCounts[i] > EPS) {
					accept = false;
				}
			} else if (tc < lowCounts[i] || tc > highCounts[i]) {
				accept = false;
			}
			if (!accept) {
				break;
			}
		}
		return accept;
	}
	private double[] createAlignedCounts(String[] elementTypes,
			double[] lowCount) {
		double[] hiCount = new double[lowCount.length];
		for (int i = 0; i < elementTypes.length; i++) {
			hiCount[i] = high.getElementCount(elementTypes[i]);
		}
		return hiCount;
	}
	
}
