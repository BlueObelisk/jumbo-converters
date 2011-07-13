package org.xmlcml.cml.converters.compchem.input;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

public class MethodBasis {

	private static final String ELEMENT = "element";
	private static final String BASIS = "basis";
	private static final String FUNCTIONAL = "functional";
	private static final String METHOD = "method";
	
	private List<AtomBasis> atomBasisList = new ArrayList<AtomBasis>();
	private String method;
	private String functional;
	private Element element;

	public MethodBasis(List<Parameter> parameterList) {
		for (Parameter parameter : parameterList) {
			if (parameter instanceof Method) {
				method = parameter.getValue();
			}
			if (parameter instanceof BasisSet) {
				addAtomBasis(((BasisSet)parameter).getAtomBasis());
			}
		}
	}
	
	public MethodBasis(InputStream inputStream) {
		try {
			this.element = new Builder().build(inputStream).getRootElement();
			method = element.getAttributeValue(METHOD);
			functional = element.getAttributeValue(FUNCTIONAL);
			Elements basisElements = element.getChildElements(BASIS);
			for (int i = 0; i < basisElements.size(); i++) {
				this.addAtomBasis(new AtomBasis(basisElements.get(i).getAttributeValue(ELEMENT), basisElements.get(i).getValue()));
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse methodBasis", e);
		}
	}
	public List<AtomBasis> getAtomBasisList() {
		return atomBasisList;
	}
	
	public void addAtomBasis(AtomBasis atomBasis) {
		this.atomBasisList.add(atomBasis);
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getFunctional() {
		return functional;
	}
	public void setFunctional(String functional) {
		this.functional = functional;
	}
}
