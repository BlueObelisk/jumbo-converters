package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;


public class OrFilter implements AbstractCMLFilter {
	private AbstractCMLFilter filter1;
	private AbstractCMLFilter filter2;
	
	public OrFilter(AbstractCMLFilter filter1, AbstractCMLFilter filter2) {
		if (filter1 == null && filter2 == null) {
			throw new RuntimeException("AndFilter has null filters");
		}
		this.filter1 = filter1;
		this.filter2 = filter2;
	}
	/** 
	 * @param element either CMLFormula or molecule
	 */
	public boolean accept(CMLElement element) {
		return filter1.accept(element) || filter2.accept(element);
	}
}
