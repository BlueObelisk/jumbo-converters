package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;


public class ValueFilter implements AbstractCMLFilter {
	
	private Object value;
	public ValueFilter(Object value) {
		this.value = value;
	}
	/** 
	 * @param element either CMLFormula or molecule
	 */
	public boolean accept(CMLElement element) {
		String value1 = element.getValue();
		return (value.equals(value1));
	}
}
