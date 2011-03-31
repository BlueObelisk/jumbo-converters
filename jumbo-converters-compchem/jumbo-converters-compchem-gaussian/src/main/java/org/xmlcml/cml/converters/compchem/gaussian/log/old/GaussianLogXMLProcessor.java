package org.xmlcml.cml.converters.compchem.gaussian.log.old;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

public class GaussianLogXMLProcessor extends RawXML2CMLProcessor {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GaussianLogXMLProcessor.class);

	public GaussianLogXMLProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
