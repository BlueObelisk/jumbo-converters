package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;

public class MoleculeLineReader extends LineReader {
	private static final Logger LOG = Logger.getLogger(MoleculeLineReader.class);

	public static final String MOLECULE_LINE_READER = "moleculeLineReader";

	private static final String ATOM_ID_PREF = "a";
	private static final String Z3 = "z3";
	private static final String Y3 = "y3";
	private static final String X3 = "x3";
	
	public MoleculeLineReader(Element childElement) {
		super(MOLECULE_LINE_READER, childElement);
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		CMLArrayList arrayList = jumboReader.readTableColumnsAsArrayList(this);
		CMLMolecule molecule = makeMolecule(arrayList);
		jumboReader.addElementWithDictRef(molecule, localDictRef);
		return molecule;
	}

	private CMLMolecule makeMolecule(CMLArrayList arrayList) {
		CMLMolecule molecule = new CMLMolecule();
		
		CMLAtom atom = null;
		List<CMLAtom> atomList = new ArrayList<CMLAtom>();
		for (int j = 0; j < arrayList.getArrays().get(0).getArraySize(); j++) {
			atom = new CMLAtom(ATOM_ID_PREF+(j+1));
			molecule.addAtom(atom);
			atomList.add(atom);
		}
		
		for (int i = 0; i < arrayList.getArraysCount(); i++) {
			CMLArray array = (CMLArray) arrayList.getArrays().get(i);
			String dictRef = array.getDictRef();
			String localDictRef = DictRefAttribute.getLocalName(dictRef);
			String dataType = array.getDataType();
			for (int j = 0; j < array.getSize(); j++) {
				atom = atomList.get(j);
				CMLScalar scalar = null;
				if (localDictRef.equals(ELEMENT_TYPE)) {
					atom.setElementType(array.getStrings()[j]);
				} else if (localDictRef.equals(X3)) {
					atom.setX3(array.getDoubles()[j]);
				} else if (localDictRef.equals(Y3)) {
					atom.setY3(array.getDoubles()[j]);
				} else if (localDictRef.equals(Z3)) {
					atom.setZ3(array.getDoubles()[j]);
				} else if (dataType.equals(CMLConstants.XSD_STRING)) {
					scalar = new CMLScalar(array.getStrings()[j]);
				} else if (dataType.equals(CMLConstants.XSD_INTEGER)) {
					scalar = new CMLScalar(array.getInts()[j]);
				} else if (dataType.equals(CMLConstants.XSD_DOUBLE)) {
					scalar = new CMLScalar(array.getDoubles()[j]);
				} else {
					throw new RuntimeException("Cannot process array "+dataType);
				}
				if (scalar != null) {
					scalar.setDictRef(dictRef);
					atom.appendChild(scalar);
				}
			}
		}
		return molecule;
	}
}
