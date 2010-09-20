package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;


public class NotFilter implements AbstractCMLFilter {
	private AbstractCMLFilter filter1;
	
	public NotFilter(AbstractCMLFilter filter1) {
		if (filter1 == null) {
			throw new RuntimeException("NotFilter has null filters");
		}
		this.filter1 = filter1;

	}
	/** 
	 * @param element either CMLFormula or molecule
	 */
	public boolean accept(CMLElement element) {
		return !filter1.accept(element);
	}
}
