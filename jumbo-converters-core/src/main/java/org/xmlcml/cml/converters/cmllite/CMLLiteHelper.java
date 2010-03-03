package org.xmlcml.cml.converters.cmllite;

import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLScalar;

public class CMLLiteHelper {

	private static final Logger LOG = Logger.getLogger(CMLLiteHelper.class);
	private static Set<Class> cmlLiteClassSet = new HashSet<Class>(); 
	static {
		cmlLiteClassSet = new HashSet<Class>(); 
		cmlLiteClassSet.add(CMLAtom.class);
		cmlLiteClassSet.add(CMLAtomArray.class);
		cmlLiteClassSet.add(CMLAtomParity.class);
		cmlLiteClassSet.add(CMLBond.class);
		cmlLiteClassSet.add(CMLBondArray.class);
		cmlLiteClassSet.add(CMLBondStereo.class);
		cmlLiteClassSet.add(CMLCml.class);
		cmlLiteClassSet.add(CMLFormula.class);
		cmlLiteClassSet.add(CMLLabel.class);
		cmlLiteClassSet.add(CMLMolecule.class);
		cmlLiteClassSet.add(CMLMoleculeList.class);
		cmlLiteClassSet.add(CMLName.class);
		cmlLiteClassSet.add(CMLScalar.class);
	}
	private CMLElement cml;

	public CMLLiteHelper(CMLElement cmlIn) {
		this.cml = (CMLElement) cmlIn.copy();
		process();
	}

	private void process() {
//		LOG.debug("CMLLite");
		cml.addNamespaceDeclaration("cmlDict", "http://www.xml-cml.org/dictionary/cml/");
		Nodes nodes = cml.query("//*");
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element) nodes.get(i);
			Class clazz = element.getClass();
			if (element instanceof CMLElement) {
//				LOG.debug("class "+clazz);
				if (cmlLiteClassSet.contains(clazz)) {
					if (element instanceof CMLBondStereo) {
						processBondStereo((CMLBondStereo) element);
					} else if (element instanceof CMLLabel) {
						processLabel((CMLLabel) element);
					} else if (element instanceof CMLMoleculeList) {
						processMoleculeList((CMLMoleculeList) element);
					} else if (element instanceof CMLScalar) {
						processScalar((CMLScalar) element);
					}
				} else {
//					LOG.warn("Removing CML Element "+clazz);
					element.detach();
				}
			} else {
				LOG.warn("Non-CML Element "+clazz);
			}
		}
	}

	private void processScalar(CMLScalar scalar) {
		if (scalar.getXMLContent() == null) {
			scalar.setXMLContent("content");
		}
	}

	private void processMoleculeList(CMLMoleculeList moleculeList) {
		ParentNode parent = moleculeList.getParent();
		int index = parent.indexOf(moleculeList);
		for (CMLMolecule molecule : moleculeList.getMoleculeElements()) {
			processMolecule(molecule);
			molecule.detach();
			parent.insertChild(molecule, index);
		}
		moleculeList.detach();
	}

	private void processMolecule(CMLMolecule molecule) {
		// currently no-op
	}

	private void processLabel(CMLLabel label) {
		if (removeCMLLabel(label)) {
//			LOG.debug("removed CMLLabel");
		} else {
			makeMoleculeLabelsBold(label);
		}
	}
			
	private boolean removeCMLLabel(CMLLabel label) {
		ParentNode parent = label.getParent();
		if (parent instanceof CMLCml) {
			label.detach();
			return true;
		}
		return false;
	}

	private void makeMoleculeLabelsBold(CMLLabel label) {
		ParentNode parent = label.getParent();
		if (parent instanceof CMLMolecule) {
			label.setDictRef("cmlDict:moleculeBoldNo");
		}
	}

	private void processBondStereo(CMLBondStereo bondStereo) {
		Attribute convention = bondStereo.getConventionAttribute();
		if (convention == null) {
			bondStereo.setConvention("cmlDict:wedgehatch");
		}
//		bondStereo.debug("BS");
		
		// this is because of a bug in chem4word
		bondStereo.detach();
	}

	public CMLCml getCML() {
		if (!(cml instanceof CMLCml)) {
			CMLCml wrapperCml = new CMLCml();
			cml.detach();
			wrapperCml.appendChild(cml);
			cml = wrapperCml;
		}
		return (CMLCml) cml;
	}

}
