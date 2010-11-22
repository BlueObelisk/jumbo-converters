package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.util.ConverterUtils;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.element.CMLZMatrix;

public class GamessUSLogXMLProcessor extends RawXML2CMLProcessor {
	private static Logger LOG = Logger.getLogger(GamessUSLogXMLProcessor.class);
	private CMLModule job;
	
	public GamessUSLogXMLProcessor() {
		
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
