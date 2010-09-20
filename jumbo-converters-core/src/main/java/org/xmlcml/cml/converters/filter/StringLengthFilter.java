package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.euclid.IntRange;


public class StringLengthFilter implements AbstractCMLFilter {
	
	private Object target;
	
	public StringLengthFilter(Object target) {
		this.target = target;
	}
	
	/** 
	 * @param element either CMLFormula or molecule
	 */
	public boolean accept(CMLElement element) {
		boolean accept = false;
		String value = element.getValue();
		int length = value.length();
		if (target instanceof Integer) {
			accept = (Integer)target == length;
		} else if (target instanceof IntRange) {
			accept = ((IntRange)target).contains(length);
		} else {
			// nothing useful
		}
		return accept;
	}
}
