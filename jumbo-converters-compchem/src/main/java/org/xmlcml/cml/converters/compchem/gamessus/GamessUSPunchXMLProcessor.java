package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLZMatrix;

public class GamessUSPunchXMLProcessor extends RawXML2CMLProcessor {
	private static Logger LOG = Logger.getLogger(GamessUSPunchXMLProcessor.class);
	private CMLModule job;
	
	protected void processXML() {
		xmlInput = CMLBuilder.ensureCML(xmlInput);
		job = new CMLModule();
		job.setRole("compchem:job");
		checkChildNodes();
		createInit();
		createFinal();
		createInter();
		wrapWithProperty("./*[local-name()='scalar']");
		xmlInput = job;
	}
	

	private void checkChildNodes() {
		List<Node> childNodes = CMLUtil.getQueryNodes(xmlInput, "*");
		for (Node childNode : childNodes) {
			CMLElement childElement = (CMLElement) childNode;
			String name = childElement.getLocalName();
			if (!(name.equals("molecule") || name.equals("zmatrix") || name.equals("module"))) {
				LOG.warn("unknown toplevel child"+childNode);
			}
		}
	}

	private void createInit() {
		List<CMLElement> childList = getChildElements();
		CMLModule init = new CMLModule();
		init.setRole("compchem:init");
		job.appendChild(init);
		for (int i = 0; i < childList.size(); i++) {
			Element child = childList.get(i);
			String name = child.getLocalName();
			if (name.equals(CMLMolecule.TAG) || name.equals(CMLZMatrix.TAG)) {
				child.detach();
				init.appendChild(child);
			}
		}
	}


	private List<CMLElement> getChildElements() {
		List<CMLElement> childList = new ArrayList<CMLElement>();
		List<Node> childNodes = CMLUtil.getQueryNodes(xmlInput, "*");
		for (Node childNode : childNodes) {
			childList.add((CMLElement)childNode);
		}
		return childList;
	}

	private void createFinal() {
		List<CMLElement> childList = getChildElements();
		CMLModule finalx = new CMLModule();
		finalx.setRole("compchem:final");
		job.appendChild(finalx);
		for (int i = childList.size()-1; i >= 0; i--) {
			CMLElement childElement = (CMLElement) childList.get(i);
			childElement.detach();
			finalx.insertChild(childElement, 0);
			if (childElement instanceof CMLModule &&
					"RESULTS".equals(((CMLModule)childElement).getRole())) {
				break;
			}
		}
	}
	
	private void createInter() {
		List<CMLElement> childList = getChildElements();
		CMLModule inter = new CMLModule();
		inter.setRole("compchem:inter");
		job.insertChild(inter, 1);
		
		CMLModule step = null;
		for (int i = 0; i < childList.size(); i++) {
			CMLElement element = childList.get(i);
			if (element instanceof CMLModule &&
					"NSERCH".equals(((CMLModule)element).getRole())) {
				step = new CMLModule();
				step.setRole("STEP");
				inter.appendChild(step);
			}
			element.detach();
			step.appendChild(element);
		}
	}

}
