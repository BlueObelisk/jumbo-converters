package org.xmlcml.cml.converters.compchem.gamessuk;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;

public class GamessUKPunchXMLProcessor {

	private Element xml;
	private CMLElement cmlElement;
	private Element bondArray;

	public void process(Element punchXML) {
		this.xml = punchXML;
		CMLUtil.removeWhitespaceNodes(xml);
		transformAtomArraysIntoMolecules();
		transformAtomArraysIntoFrequencies();
		createProperties();
		createModules();
		cmlElement = CMLBuilder.ensureCML(xml);
	}

	private void createModules() {
		Nodes moduleNodes = xml.query("./*[local-name()='module']");
		for (int i = 0; i < moduleNodes.size(); i++) {
			Element module = (Element) moduleNodes.get(i);
			Element nextModule = (i == moduleNodes.size()-1) ? null : (Element) moduleNodes.get(i+1);
			wrapFollowingSiblings(module, nextModule);
		}
	}

	private void wrapFollowingSiblings(Element module, Element nextModule) {
		CMLUtil.debug(xml, "XXXXX");
		int index = xml.indexOf(module);
		int nextIndex = (nextModule == null) ? xml.getChildCount() : xml.indexOf(nextModule);
		Elements childElements = xml.getChildElements();
		List<Element> siblingList = new ArrayList<Element>();
		for (int i = index+1; i < nextIndex; i++) {
			siblingList.add(childElements.get(i));
		}
		for (int i = 0; i < siblingList.size(); i++) {
			Element sibling = siblingList.get(i);
			sibling.detach();
			module.appendChild(sibling);
		}
	}

	private void transformAtomArraysIntoFrequencies() {
		Nodes atomArrayNodes = xml.query("./*[local-name()='atomArray' and @dictRef='gamessuk:normal_coordinates']");
		for (int i = 0; i < atomArrayNodes.size(); i++) {
			Element atomArray = (Element) atomArrayNodes.get(i);
			CMLArray array = new CMLArray();
			CMLUtil.copyAttributes(atomArray, array);
			Elements childElements = atomArray.getChildElements();
			for (int j = 0; j < childElements.size(); j++) {
				Element atom = childElements.get(j);
				atom.detach();
				array.appendChild(atom);
			}
			xml.replaceChild(atomArray, array);
		}
	}

	private void createProperties() {
		Nodes nodes = xml.query("./*[(local-name()='scalar' or local-name()='array' or local-name()='matrix') and @dictRef]");
		for (int i = 0; i < nodes.size(); i++) {
			Element el = (Element) nodes.get(i);
			CMLProperty property = new CMLProperty();
			Attribute dictRef = el.getAttribute("dictRef");
			dictRef.detach();
			property.addAttribute(dictRef);
			xml.replaceChild(el, property);
			Attribute index = el.getAttribute("index", GamessUKPunch2XMLConverter.GAMESSUK_URI);
			if (index != null) {
				index.detach();
				property.addAttribute(index);
			}
			property.appendChild(el);
		}
	}

	private void transformAtomArraysIntoMolecules() {
		Nodes bondArrayNodes = xml.query("./*[local-name()='bondArray' and @dictRef='gamessuk:connectivity']");
		if (bondArrayNodes.size() > 1) {
			throw new RuntimeException("Cannot manage multiple connectivities");
		}
		bondArray = (bondArrayNodes.size() == 0) ? null : (Element) bondArrayNodes.get(0);
		Nodes atomArrayNodes = xml.query("./*[local-name()='atomArray' and not(@dictRef='gamessuk:normal_coordinates')]");
		for (int i = 0; i < atomArrayNodes.size(); i++) {
			Element atomArray = (Element) atomArrayNodes.get(i);
			CMLMolecule molecule = replaceAtomArrayByMolecule(atomArray);
			addConnectivity(molecule);
		}
		if (bondArray != null) {
			bondArray.detach();
		}
	}

	private void addConnectivity(CMLMolecule molecule) {
		if (bondArray != null) {
			for (int i = 0; i < bondArray.getChildElements().size(); i++) {
				Element oldBond = bondArray.getChildElements().get(i);
				CMLBond bond = new CMLBond();
				CMLUtil.copyAttributes(oldBond, bond);
				molecule.addBond(bond);
			}
		}
	}

	private CMLMolecule replaceAtomArrayByMolecule(Element atomArray) {
		CMLMolecule molecule = new CMLMolecule();
		for (int i = 0; i < atomArray.getChildElements().size(); i++) {
			Element oldAtom = atomArray.getChildElements().get(i);
			CMLAtom atom = new CMLAtom();
			CMLUtil.copyAttributes(oldAtom, atom);
			molecule.addAtom(atom);
		}
		CMLUtil.copyAttributes(atomArray, molecule);
		xml.replaceChild(atomArray, molecule);
		return molecule;
	}

	public CMLElement getCML() {
		return cmlElement;
	}

}
