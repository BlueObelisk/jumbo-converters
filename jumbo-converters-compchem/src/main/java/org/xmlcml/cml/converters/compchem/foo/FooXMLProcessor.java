package org.xmlcml.cml.converters.compchem.foo;

import gigadot.semsci.converters.chem.CompChemCommon;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class FooXMLProcessor extends RawXML2CMLProcessor {

	public FooXMLProcessor() {
		
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new FooCommon();
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
