package org.xmlcml.cml.converters.text;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLZMatrix;

public class ZMatrixElement {

	private final static Logger LOG = Logger.getLogger(ZMatrixElement.class);

	private static final String CC_ELEMENT_TYPE = "cc:elementType";
	private static final String CC_SERIAL = "cc:serial";
	private static final String CC_NAME = "cc:name";
	private static final String CC_VALUE = "cc:value";

	private static final String A1 = "a1";
	private static final String A2 = "a2";
	private static final String A3 = "a3";
	private static final String IMPLICIT321 = "implicit321";

	private Element element;
	private Nodes atom1Nodes;
	private Nodes atom2Nodes;
	private Nodes atom3Nodes;
	private Nodes atom4Nodes;
	private Element atom1;
	private Element atom2;
	private Element atom3;
	private Element atom4;
	private Nodes serialNodes;
	private Nodes nameNodes;
	private Nodes valueNodes;
	
	private CMLMolecule molecule;

	private CMLZMatrix zmatrix;

	private String type;
	
	public ZMatrixElement(Element element, String type) {
		this.element = element;
		this.type = type;
		createElement();
	}
	
	private void createElement() {
		zmatrix = new CMLZMatrix();
		molecule = new CMLMolecule();
		molecule.appendChild(zmatrix);
//		CMLUtil.debug(element, "ELEM");
		atom1Nodes = TransformElement.queryUsingNamespaces(element, "./cml:list[@cmlx:templateRef='atom1' and cml:scalar]");
		atom2Nodes = TransformElement.queryUsingNamespaces(element, "./cml:list[@cmlx:templateRef='atom2' and cml:scalar]");
		atom3Nodes = TransformElement.queryUsingNamespaces(element, "./cml:list[@cmlx:templateRef='atom3' and cml:scalar]");
		atom4Nodes = TransformElement.queryUsingNamespaces(element, "./cml:list[@cmlx:templateRef='atom4' and cml:scalar]");
		atom1 = (atom1Nodes.size() == 0) ? null : (Element) atom1Nodes.get(0);
		atom2 = (atom2Nodes.size() == 0) ? null : (Element) atom2Nodes.get(0);
		atom3 = (atom3Nodes.size() == 0) ? null : (Element) atom3Nodes.get(0);
		try {
			checkZMatrix();
		} catch (Exception e) {
			LOG.warn("Cannot create zmatrix");
			return;
		}
		addAtom1(atom1, A1);
		addAtom2(atom2, A2);
		addAtom3(atom3, A3);
		for (int i = 0; i < atom4Nodes.size(); i++) {
			atom4 = (Element)atom4Nodes.get(i);
			int serial = i+4;
			addAtom4(atom4, "a"+serial);
		}
	}

	private void checkZMatrix() {
		if (atom1Nodes.size() > 1) {
			throw new RuntimeException("too many atom1s in zmatrix");
		}
		if (atom2Nodes.size() > 1) {
			throw new RuntimeException("too many atom2s in zmatrix");
		}
		if (atom3Nodes.size() > 1) {
			throw new RuntimeException("too many atom3s in zmatrix");
		}
		if (atom1 == null) {
			throw new RuntimeException("no atoms in zmatrix");
		}
		if (atom2 == null && (atom3 != null || atom4Nodes.size() >0)) {
			throw new RuntimeException("missing atom2 in zmatrix");
		}
		if (atom3 == null && atom4Nodes.size() >0) {
			throw new RuntimeException("missing atom3 in zmatrix");
		}
	}

	private void addAtom1(Element atomElement, String id) {
		createSerialNameValueNodes(atomElement, 0);
		addAtom(id, atomElement);
	}

	private void addAtom2(Element atomElement, String id) {
		createSerialNameValueNodes(atomElement, 1);
		addAtom(id, atomElement);
		addLength(id, serialNodes, valueNodes, nameNodes);
	}

	private void addAtom3(Element atomElement, String id) {
		createSerialNameValueNodes(atomElement, 2);
		addAtom(id, atomElement);
		addLength(id, serialNodes, valueNodes, nameNodes);
		addAngle(id, serialNodes, valueNodes, nameNodes);
	}

	private void addAtom4(Element atomElement, String id) {
		createSerialNameValueNodes(atomElement, 3);
		addAtom(id, atomElement);
		addLength(id, serialNodes, valueNodes, nameNodes);
		addAngle(id, serialNodes, valueNodes, nameNodes);
		addTorsion(id, serialNodes, valueNodes, nameNodes);
	}

	private void createSerialNameValueNodes(Element atomElement, int size) {
		serialNodes = TransformElement.queryUsingNamespaces(atomElement, "cml:scalar[@dictRef='"+CC_SERIAL+"']");
		nameNodes = TransformElement.queryUsingNamespaces(atomElement, "cml:scalar[@dictRef='"+CC_NAME+"']");
		valueNodes = TransformElement.queryUsingNamespaces(atomElement, "cml:scalar[@dictRef='"+CC_VALUE+"']");
		if (serialNodes.size() > 0 && serialNodes.size() != size) {
			throw new RuntimeException("wrong serial count for atom"+(size+1)+", found: "+serialNodes.size());
		}
		if (nameNodes.size()+valueNodes.size() != size) {
			throw new RuntimeException("wrong serial, name, value count for atom"+(size+1)+", found: "+serialNodes.size());
		}
		if (nameNodes.size() != 0 && valueNodes.size() != 0) {
			throw new RuntimeException("sorry, cannot mix name, value count for atom"+(size+1)+", found: "+serialNodes.size());
		}
	}

	private void addLength(String  atomId, Nodes serialNodes, Nodes valueNodes, Nodes nameNodes) {
		CMLLength length = new CMLLength();
		if (serialNodes.size() > 0) {
			length.setAtomRefs2(new String[]{atomId, serialNodes.get(0).getValue()});
		} else if (IMPLICIT321.equals(type)) {
			length.setAtomRefs2(new String[]{A1, atomId});
		}
		if (valueNodes.size() >= 1) {
			length.setXMLContent(valueNodes.get(0).getValue());
		} else if (nameNodes.size() >= 1) {
			length.setTitle(nameNodes.get(0).getValue());
		}
		zmatrix.addLength(length);
	}

	private void addAngle(String  atomId, Nodes serialNodes, Nodes valueNodes, Nodes nameNodes) {
		CMLAngle angle = new CMLAngle();
		if (serialNodes.size() > 0) {
			angle.setAtomRefs3(new String[]{atomId, serialNodes.get(0).getValue(), serialNodes.get(1).getValue()});
		} else if (IMPLICIT321.equals(type)) {
			angle.setAtomRefs3(new String[]{A2, A1, atomId,});
		}
		if (valueNodes.size() >= 2) {
			angle.setXMLContent(valueNodes.get(1).getValue());
		} else if (nameNodes.size() >= 2) {
			angle.setTitle(nameNodes.get(1).getValue());
		}
		zmatrix.addAngle(angle);
	}

	private void addTorsion(String  atomId, Nodes serialNodes, Nodes valueNodes, Nodes nameNodes) {
		CMLTorsion torsion = new CMLTorsion();
		if (serialNodes.size() > 0) {
			torsion.setAtomRefs4(new String[]{atomId, 
				serialNodes.get(0).getValue(), serialNodes.get(1).getValue(), serialNodes.get(2).getValue()});
		} else if (IMPLICIT321.equals(type)) {
			torsion.setAtomRefs4(new String[]{A3, A2, A1, atomId,});
		}
		if (valueNodes.size() >= 3) {
			torsion.setXMLContent(valueNodes.get(2).getValue());
		} else if (nameNodes.size() >= 3) {
			torsion.setTitle(nameNodes.get(2).getValue());
		}
		zmatrix.addTorsion(torsion);
	}

	private void addAtom(String id, Element atomElement) {
		CMLAtom atom = new CMLAtom(id);
//		CMLUtil.debug(atomElement,"X");
		Nodes elementTypeNodes = TransformElement.queryUsingNamespaces(atomElement, "cml:scalar[@dictRef='"+CC_ELEMENT_TYPE+"']");
		String elementType = (elementTypeNodes.size() == 0) ? null : elementTypeNodes.get(0).getValue();
		if (elementType != null) {
			atom.setElementType(elementType);
		}
		molecule.addAtom(atom);
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}


}
