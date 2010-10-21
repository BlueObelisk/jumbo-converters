package org.xmlcml.cml.converters.compchem.foo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

public class FooBlock extends AbstractBlock {

	/*
	 * keywords in legacy input
	 */
	public static final String DIPOLE   = "dipole";
	public static final String MOLECULE = "molecule";
	
	public FooBlock(BlockContainer blockContainer) {
		super(blockContainer);
		this.abstractCommon = new FooCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (MOLECULE.equals(getBlockName())) {
			makeMolecule();
		} else if (DIPOLE.equals(getBlockName())) {
			makeDipole();
		} else {
			System.err.println("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), getBlockName());
			element.setAttribute("dictRef", dictRef);
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	/**
	 * $dipole
	 *   1.2345
	 *   (Note that the $dipole has been lexically stripped)
	 */
	private void makeDipole() {
		String field = lines.get(0).substring(0, 8).trim();
		Double value = createDouble(field);
		CMLScalar dipole= new CMLScalar(value);
		dipole.setUnits("units:debye");
		element = dipole;
	}

	/**
	 * atoms and bonds
	 *   3  2
	 *  Cl   0.0   0.0   0.0
	 *  O    1.0   0.0   0.0
	 *  H    2.0   0.0   0.0
	 *   1  2
	 *   1  3
	 * 
	 */
	public static final Pattern ATOM_BOND_COUNT = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	public static final Pattern ATOM = Pattern.compile("\\s*([A-Z][a-z]?)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)");
	public static final Pattern BOND = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	
	private void makeMolecule() {
		CMLMolecule molecule = new CMLMolecule();
		// read first line for counts
		int lineCount = 0;
		String firstLine = lines.get(lineCount++);
		Matcher countMatcher = ATOM_BOND_COUNT.matcher(firstLine);
		if (!countMatcher.matches() || countMatcher.groupCount() != 2) {
			throw new RuntimeException("Expected atom/bond count, found: "+firstLine);
		}
		int atomCount = Integer.parseInt(countMatcher.group(1));
		int bondCount = Integer.parseInt(countMatcher.group(2));
		lineCount = addAtoms(molecule, lineCount, atomCount);
		lineCount = addBonds(molecule, lineCount, bondCount);
		element = molecule;
	}

	private int addAtoms(CMLMolecule molecule, int lineCount, int atomCount) {
		for (int iatom = 0; iatom < atomCount; iatom++) {
			String line = lines.get(lineCount++);
			Matcher atomMatcher = ATOM.matcher(line);
			if (!atomMatcher.matches() || atomMatcher.groupCount() != 4) {
				throw new RuntimeException("Expected atom, found: "+line);
			}
			CMLAtom atom = new CMLAtom();
			atom.setId("a"+(lineCount-1));
			// changes "he", "HE" etc to "He"
			atom.setElementType(Util.capitalise(atomMatcher.group(1).toLowerCase()));
			try {
				atom.setX3(new Double(atomMatcher.group(2)));
				atom.setY3(new Double(atomMatcher.group(3)));
				atom.setZ3(new Double(atomMatcher.group(4)));
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("bad atom coordinate in "+line, nfe);
			}
			molecule.addAtom(atom);
		}
		return lineCount;
	}

	private int addBonds(CMLMolecule molecule, int lineCount, int bondCount) {
		for (int ibond = 0; ibond < bondCount; ibond++) {
			String line = lines.get(lineCount++);
			Matcher bondMatcher = BOND.matcher(line);
			if (!bondMatcher.matches() || bondMatcher.groupCount() != 2) {
				throw new RuntimeException("Expected bond, found: "+line);
			}
			CMLBond bond = new CMLBond();
			String atomRef1 = "a"+bondMatcher.group(1);
			String atomRef2 = "a"+bondMatcher.group(2);
			bond.setAtomRefs2(atomRef1+" "+atomRef2);
			bond.setId(atomRef1+"_"+atomRef2);	// good idea for all elements to have ids
			molecule.addBond(bond);
		}
		return lineCount;
	}
}
