package org.xmlcml.cml.converters.molecule.fragments;

import net.sf.jniinchi.*;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Tool for the creation of InChIs from CML molecules.
 * </p>
 * 
 * @author Nick Day
 * @author Sam Adams
 * @version 0.2
 */
public class InchiTool {

	private static final Logger LOG = Logger.getLogger(InchiTool.class);
	
	/**
	 * <p>
	 * Generate the InChI for the objects molecule using the InChI program
	 * options provided as a parameter.
	 * </p>
	 *
	 * @param options - the InChI program options to be used for generation.
	 *
	 * @return the InChI string for the objects molecule. Returns null if there
	 * was a problem during generation.
	 */
	public static String generateInchi(CMLMolecule molecule, String options) {
        if (molecule == null) {
            throw new IllegalArgumentException("Null argument: molecule");
        }
        try {
            JniInchiInput input = getInchiInput(molecule, options);
            JniInchiOutput output = JniInchiWrapper.getInchi(input);
            return output.getInchi();
        } catch (JniInchiException e) {
            throw new RuntimeException(e);
        }
	}

    public static String generateInchiKey(String inchi) {
        if (inchi == null) {
            throw new IllegalArgumentException("Null argument: inchi");
        }
        try {
            JniInchiOutputKey output = JniInchiWrapper.getInchiKey(inchi);
            return output.getKey();
        } catch (JniInchiException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * <p>
	 * Creates a complete <code>JniInchiInput</code> object representing
	 * the structure in the object CML molecule.  NOTE that this method
	 * assumes that the provided CMLMolecule has 3D coordinates for all
	 * atoms, and allows the InChI tool to calculate the stereochemical
	 * flags instead of having to input them explicitly.
	 * </p>
	 *
	 * @param options - the InChI program options to be used in creating
	 * the InChI input.
	 *
	 * @return a complete <code>JniInchiInput</code> object representing
	 * the structure in the object CML molecule.
	 *
	 * @throws JniInchiException if there is an error is generating the
	 * <code>JniInchiInput</code>.
	 */
	private static JniInchiInput getInchiInput(CMLMolecule molecule, String options) throws JniInchiException {
		JniInchiInput input = new JniInchiInput(options);
		Map<String, JniInchiAtom> jniAtomList = new HashMap<String, JniInchiAtom>(molecule.getAtomCount());
		for (CMLAtom atom : molecule.getAtoms()) {
			JniInchiAtom a1 = input.addAtom(new JniInchiAtom(atom.getX3(), atom.getY3(),
					atom.getZ3(), atom.getElementType()));
			jniAtomList.put(atom.getId(), a1);
		}
		for (CMLBond bond : molecule.getBonds()) {
			String[] atomRefs = bond.getAtomRefs2();
			JniInchiAtom a0 = jniAtomList.get(atomRefs[0]);
			JniInchiAtom a1 = jniAtomList.get(atomRefs[1]);
			input.addBond(new JniInchiBond(a0, a1, getBondType(bond)));
		}
		return input;
	}

	/**
	 * <p>
	 * Gets the bond type for the provided CML bond.
	 * </p>
	 *
	 * @param bond whose type you want to returned.
	 *
	 * @return bond type of the provided bond.
	 */
	private static INCHI_BOND_TYPE getBondType(CMLBond bond) {
		String order = bond.getOrder();
		if ("1".equals(order)) {
			return INCHI_BOND_TYPE.SINGLE;
		} else if ("2".equals(order)) {
			return INCHI_BOND_TYPE.DOUBLE;
		} else if ("3".equals(order)) {
			return INCHI_BOND_TYPE.TRIPLE;
		} else {
			return INCHI_BOND_TYPE.ALTERN;
		}
	}

}
