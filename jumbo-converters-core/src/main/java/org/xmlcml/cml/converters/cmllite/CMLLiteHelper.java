package org.xmlcml.cml.converters.cmllite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
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
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.BondOrder;
import org.xmlcml.cml.tools.BondTool;

public class CMLLiteHelper {

	private static final Logger LOG = Logger.getLogger(CMLLiteHelper.class);
	private static Set<Class<? extends CMLElement>> cmlLiteClassSet = new HashSet<Class<? extends CMLElement>>(); 
	static {
		cmlLiteClassSet = new HashSet<Class<? extends CMLElement>>(); 
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
		processElementDescendants();
		processIDAttributes();
		processDictRefAttributes();
		removeBadAttributes();
	}

	private void processElementDescendants() {
		Nodes nodes = cml.query("//*");
//		System.out.println("NODESSSS"+nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element) nodes.get(i);
			Class<?> clazz =  element.getClass();
			if (element instanceof CMLElement) {
				if (cmlLiteClassSet.contains(clazz)) {
					if (element instanceof CMLAtom) {
						processAtom((CMLAtom) element);
					} else if (element instanceof CMLAtomArray) {
						processAtomArray((CMLAtomArray) element);
					} else if (element instanceof CMLBondStereo) {
						processBondStereo((CMLBondStereo) element);
					} else if (element instanceof CMLBond) {
						processBond((CMLBond) element);
					} else if (element instanceof CMLLabel) {
						processLabel((CMLLabel) element);
					} else if (element instanceof CMLMoleculeList) {
						processMoleculeList((CMLMoleculeList) element);
					} else if (element instanceof CMLScalar) {
						processScalar((CMLScalar) element);
					}
				} else {
//					System.out.println("CLASS "+element);
					//processNonCMLLiteCML((CMLElement) element);
				}
			} else {
				LOG.warn("Non-CML Element "+clazz);
			}
		}
	}

	private void processIDAttributes() {
		Nodes nodes = cml.query("//@id");
		for (int i = 0; i < nodes.size(); i++) {
			normalizeId((Attribute) nodes.get(i));
		}
	}
	
	private static Set<String> badAttNames;
	static {
		badAttNames = new HashSet<String>();
		badAttNames.add("userCyclic");
	}
	private void removeBadAttributes() {
		Nodes nodes = cml.query("//@*");
		for (int i = 0; i < nodes.size(); i++) {
			Attribute att = (Attribute) nodes.get(i);
			if (badAttNames.contains(att.getLocalName())) {
				att.detach();
			}
		}
	}

	private void normalizeId(Attribute idAtt) {
		String value = idAtt.getValue();
		value = value.replaceAll("[&^%$£\"!(),;'/#~=+*{}\\[\\]]", "_");
		idAtt.setValue(value);
	}
	
	private void processDictRefAttributes() {
		Nodes nodes = cml.query("//@dictRef");
		for (int i = 0; i < nodes.size(); i++) {
			normalizeDictRef((DictRefAttribute) nodes.get(i));
		}
	}
	
	private void normalizeDictRef(DictRefAttribute dictRefAtt) {
		String prefix = dictRefAtt.getPrefix();
		if (prefix == null) {
			throw new RuntimeException("no prefix");
		}
		String ns = dictRefAtt.getNamespaceURIString();
		if (ns == null) {
			Element root = (Element) dictRefAtt.getParent().query("/*").get(0);
			root.addNamespaceDeclaration(prefix, "http://www.xml-cml.org/schema/foo");
		}
		String idRef = dictRefAtt.getIdRef();
		String idRef1 = normalizeIdRef(idRef);
		if (!idRef.equals(idRef1)) {
			dictRefAtt.setIdRef(idRef1);
		}
	}
	
	
	private String normalizeIdRef(String idRef) {
		while (idRef.startsWith(CMLConstants.S_UNDER)) {
			idRef = idRef.substring(1);
		}
		return idRef;
	}

	private void processScalar(CMLScalar scalar) {
		if (scalar.getXMLContent() == null) {
			scalar.setXMLContent("content");
		}
		// all scalars must be in properties
		ParentNode parent = scalar.getParent();
		if (parent != null && !(parent instanceof CMLProperty)) {
			replaceScalarByPropertyScalar(scalar, parent);
		}
	}

	private void replaceScalarByPropertyScalar(CMLScalar scalar, ParentNode parent) {
		CMLProperty property = new CMLProperty();
		parent.replaceChild(scalar, property);
		property.appendChild(scalar);
		Attribute dictRefAtt = scalar.getDictRefAttribute();
		if (dictRefAtt != null) {
			dictRefAtt.detach();
			property.addAttribute(dictRefAtt);
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

	private void processAtom(CMLAtom atom) {
		Nodes childNodes = atom.query("*");
		for (int i = 0; i < childNodes.size(); i++) {
			Element childElement = (Element) childNodes.get(i);
			if (childElement instanceof CMLAtomParity ||
					childElement instanceof CMLLabel ||
					childElement instanceof CMLName) {
			} else if (childElement instanceof CMLScalar) {
				// only labels allowed
				//<scalar dictRef="iucr:_atom_site_label" dataType="xsd:string">C2S</scalar>
				CMLScalar scalar = (CMLScalar) childElement;
				DictRefAttribute dictRefAtt = (DictRefAttribute) scalar.getDictRefAttribute();
				String value = dictRefAtt.getValue();
				if (dictRefAtt != null && 
						(value.equals("iucr:atom_site_label") || value.equals("iucr:_atom_site_label"))) {
					CMLLabel label = new CMLLabel();
					label.setCMLValue(scalar.getValue());
					label.setDictRef(value);
					atom.replaceChild(scalar, label);
					// should add a convention at some stage
				} else {
					childElement.detach();
				}
			} else {
				childElement.detach();
			}
		}
	}
			
	private void processAtomArray(CMLAtomArray atomArray) {
		ParentNode parent = atomArray.getParent();
		if (parent != null && parent instanceof CMLFormula) {
			atomArray.detach();
		}
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

	private void processBond(CMLBond bond) {
		BondOrder.normalizeBondOrder(bond);
		BondTool.getOrCreateTool(bond).ensureId();
	}
	
	private void processBondStereo(CMLBondStereo bondStereo) {
		Attribute convention = bondStereo.getConventionAttribute();
		if (convention == null) {
			bondStereo.setConvention("cmlDict:wedgehatch");
		}
		
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

	public CMLMolecule getMolecule() {
		List<CMLMolecule> moleculeList = CMLUtil.extractTopLevelMolecules(cml);
		return (moleculeList.size() > 0) ? moleculeList.get(0) : null;
	}

}
