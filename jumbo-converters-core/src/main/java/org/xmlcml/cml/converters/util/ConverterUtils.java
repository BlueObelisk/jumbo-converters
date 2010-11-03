package org.xmlcml.cml.converters.util;

import java.util.List;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.interfacex.HasDictRef;

public class ConverterUtils {

	public static void addPrefixedDictRef(HasDictRef element, String prefix, String value) {
		element.setDictRef(DictRefAttribute.createValue(prefix, value));
	}

	/** treats the 3D atomCoordinates as atom children of another molecule
	 * the coordinates in fromAtom are wrapped in a CMLVector3 and added as children of corresponding
	 * atoms in toMolecule. The Vector is given a dictRef of prefix:localDictRef
	 * atom correspondence is done by ids. Non-existent atoms in toMolecule are ignored
	 * no atoms or molecules are deleted 
	 * @param fromMolecule
	 * @param toMolecule
	 * @param prefix for dictRef
	 * @param localDictRef
	 */
	public static void copyCoordinateVectorAsAtomChildren(
			CMLMolecule fromMolecule, CMLMolecule toMolecule, String prefix, String localDictRef) {
		if (toMolecule == null) {
			throw new RuntimeException("toMolecule must not be null");
		}
		for (int j = 0; j < fromMolecule.getAtomCount(); j++) {
			CMLAtom fromAtom = fromMolecule.getAtom(j);
			CMLVector3 vector3 = new CMLVector3(fromAtom.getXYZ3().getArray());
			vector3.setDictRef(DictRefAttribute.createValue(prefix, localDictRef));
			String id = fromAtom.getId();
			if (id == null) {
				throw new RuntimeException("Atoms must have IDs"+fromAtom.toXML());
			}
			CMLAtom toAtom = toMolecule.getAtomById(id);
			if (toAtom != null) {
				toAtom.appendChild(vector3);
			}
		}
	}

}
