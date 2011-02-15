package org.xmlcml.cml.converters.compchem.gamessus.punch;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;

public class GamessUSPunchXMLProcessor extends RawXML2CMLProcessor {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GamessUSPunchXMLProcessor.class);
	
	@SuppressWarnings("unused")
	private static final String ATOM_VIBRATION = "atom.vibration";
	@SuppressWarnings("unused")
	private static final String ATOM_GRADIENT = "atom.gradient";
//	private CMLModule job;
	
	public GamessUSPunchXMLProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}
	
	protected void processXML() {
//		xmlInput = CMLBuilder.ensureCML(xmlInput);
//		job = new CMLModule();
//		job.setRole("compchem:job");
//		checkChildNodes();
//		createInit();
//		createFinal();
//		createInter();
//		wrapWithProperty(".//*[local-name()='scalar']");
//		xmlInput = job;
	}
	

//	private void checkChildNodes() {
//		List<Node> childNodes = CMLUtil.getQueryNodes(xmlInput, "*");
//		for (Node childNode : childNodes) {
//			CMLElement childElement = (CMLElement) childNode;
//			String name = childElement.getLocalName();
//			if (!(name.equals(CMLMolecule.TAG) ||
//					name.equals(CMLZMatrix.TAG) ||
//					name.equals(CMLModule.TAG))) {
//				LOG.warn("unknown toplevel child"+childNode);
//			}
//		}
//	}

//	private void createInit() {
//		List<CMLElement> childList = getChildElements();
//		CMLModule init = new CMLModule();
//		init.setRole("compchem:init");
//		job.appendChild(init);
//		for (int i = 0; i < childList.size(); i++) {
//			Element child = childList.get(i);
//			String name = child.getLocalName();
//			if (name.equals(CMLMolecule.TAG) || name.equals(CMLZMatrix.TAG)) {
//				child.detach();
//				init.appendChild(child);
//			}
//		}
//	}


//	private List<CMLElement> getChildElements() {
//		List<CMLElement> childList = new ArrayList<CMLElement>();
//		List<Node> childNodes = CMLUtil.getQueryNodes(xmlInput, "*");
//		for (Node childNode : childNodes) {
//			childList.add((CMLElement)childNode);
//		}
//		return childList;
//	}

//	private void createFinal() {
//		List<CMLElement> childList = getChildElements();
//		CMLModule finalx = new CMLModule();
//		finalx.setRole("compchem:final");
//		job.appendChild(finalx);
//		moveChildModulesFromEndUntilIncludingResults(childList, finalx);
//		moveAtomGradients(finalx);
//		moveAtomVibrations(finalx);
//	}
	
//	private void moveChildModulesFromEndUntilIncludingResults(
//			List<CMLElement> childList, CMLModule finalx) {
//		for (int i = childList.size()-1; i >= 0; i--) {
//			CMLElement childElement = (CMLElement) childList.get(i);
//			childElement.detach();
//			finalx.insertChild(childElement, 0);
//			if (childElement instanceof CMLModule) {
//				String dictRefLocalValue = DictRefAttribute.getLocalValue(childElement);
//				if (GamessUSCommon.RESULTS.equalsIgnoreCase(dictRefLocalValue)) {
//					break;
//				}
//			}
//		}
//	}
	
//	private void createInter() {
//		List<CMLElement> childList = getChildElements();
//		CMLModule inter = new CMLModule();
//		inter.setRole("compchem:inter");
//		job.insertChild(inter, 1);
//		
//		CMLModule step = null;
//		CMLMolecule molecule = null;
//		for (int i = 0; i < childList.size(); i++) {
//			CMLElement element = childList.get(i);
//			if (element instanceof CMLModule &&
//					GamessUSCommon.NSERCH.toLowerCase().equals(DictRefAttribute.getLocalValue(element))) {
//				step = new CMLModule();
//				molecule = processNserch(step, element);
//				step.appendChild(element);
//				inter.appendChild(step);
//				element.detach();
//			} else {
//				addScalarsAndDisplacements(molecule, element);
//				element.detach();
//			}
//			if (step == null) {
//				throw new RuntimeException("no step");
//			}
//		}
//	}
//	private void moveAtomGradients(CMLModule finalx) {
//		CMLMolecule resultsMolecule = (CMLMolecule) CMLUtil.getSingleElement(finalx, "./*[local-name()='module' and contains(@dictRef, ':results')]/*[local-name()='molecule']");
//		CMLMolecule gradMolecule = (CMLMolecule) CMLUtil.getSingleElement(finalx, "./*[local-name()='module' and contains(@dictRef, ':grad')]/*[local-name()='molecule']");
//		if (resultsMolecule != null && gradMolecule != null) {
//			ConverterUtils.copyCoordinateVectorAsAtomChildren(
//					gradMolecule, resultsMolecule, abstractCommon.getPrefix(), ATOM_GRADIENT);
//			gradMolecule.detach();
//		}
//	}
	
//	private void moveAtomVibrations(CMLModule finalx) {
//		CMLMolecule resultsMolecule = (CMLMolecule) CMLUtil.getSingleElement(finalx, "./*[local-name()='module' and contains(@dictRef, ':results')]/*[local-name()='molecule']");
//		CMLArray vibs = (CMLArray) CMLUtil.getSingleElement(finalx, "./*[local-name()='module' and contains(@dictRef, ':vib')]/*[local-name()='array' and contains(@dictRef, ':vector')]");
//		if (resultsMolecule != null && vibs != null) {
//			List<CMLAtom> resultsAtoms = resultsMolecule.getAtoms();
//			double[] vibCoords = vibs.getDoubles();
//			int j = 0;
//			for (int i = 0; i < resultsAtoms.size(); i++) {
//				CMLAtom resultsAtom = resultsAtoms.get(i);
//				CMLVector3 vector = new CMLVector3(vibCoords[j++],vibCoords[j++],vibCoords[j++] );
//				vector.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), ATOM_VIBRATION));
//				resultsAtom.appendChild(vector);
//			}
//			vibs.detach();
//		}
//	}
	
//	private void addScalarsAndDisplacements(CMLMolecule molecule,
//			CMLElement element) {
//		Elements scalars = element.getChildCMLElements(CMLScalar.TAG);
//		ParentNode parent = molecule.getParent();
//		for (int j = 0; j < scalars.size(); j++) {
//			scalars.get(j).detach();
//			parent.appendChild(scalars.get(j));
//		}
//		CMLMolecule gradMolecule = (CMLMolecule) element.getFirstCMLChild(CMLMolecule.TAG);
//		if (gradMolecule != null) {
//			ConverterUtils.copyCoordinateVectorAsAtomChildren(
//					molecule, gradMolecule, abstractCommon.getPrefix(), ATOM_GRADIENT);
//		}
//		element.detach();
//	}
//	private CMLMolecule processNserch(CMLModule step, Element element) {
//		step.setRole(GamessUSCommon.STEP);
//		CMLScalar scalar = (CMLScalar) CMLUtil.getSingleElement(
//				element, "./*[local-name()='scalar' and contains(@dictRef, ':"+GamessUSCommon.NCYC+"')]");
//		if (scalar == null) {
//			throw new RuntimeException("expected scalar with serial");
//		}
//		step.setSerial(scalar.getXMLContent());
//		scalar.detach();
//		element.detach();
//		CMLMolecule molecule = (CMLMolecule) CMLUtil.getSingleElement(element, "./*[local-name()='molecule']");
//		molecule.detach();
//		step.insertChild(molecule, 0);
//		return molecule;
//	}

}
