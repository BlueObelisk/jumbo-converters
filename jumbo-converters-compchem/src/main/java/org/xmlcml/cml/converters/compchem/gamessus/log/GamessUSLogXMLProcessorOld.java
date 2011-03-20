package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.element.CMLModule;

public class GamessUSLogXMLProcessorOld extends RawXML2CMLProcessor {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GamessUSLogXMLProcessorOld.class);
	private CMLModule job;
	
	public GamessUSLogXMLProcessorOld() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}
	
	protected void processXML() {
		xmlInput = CMLBuilder.ensureCML(xmlInput);
		job = new CMLModule();
		job.setRole("compchem:job");
//		checkChildNodes();
//		createInit();
//		createFinal();
//		createInter();
		wrapWithProperty(".//*[local-name()='scalar']");
		xmlInput = job;
	}
	


}
