package org.xmlcml.cml.converters.compchem.qespresso.log;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.qespresso.QespressoCommon;

public class QuantumEspressoLogXMLProcessor extends RawXML2CMLProcessor {

	public QuantumEspressoLogXMLProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new QespressoCommon();
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
