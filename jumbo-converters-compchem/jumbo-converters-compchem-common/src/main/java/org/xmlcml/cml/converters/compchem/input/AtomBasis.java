package org.xmlcml.cml.converters.compchem.input;

public class AtomBasis {
	
	public final static String WILDCARD = "*";
	private String element;
	private String basis;
	
	public AtomBasis(String element, String basis) {
		this.element = element;
		this.basis = basis;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getBasis() {
		return basis;
	}
	public void setBasis(String basis) {
		this.basis = basis;
	}

}
