package org.xmlcml.cml.converters.molecule.fragments;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Point3;
import org.xmlcml.molutil.ChemicalElement.AS;

public class MiscTest {

	private final static Logger LOG = Logger.getLogger(MiscTest.class);
	
	@Test
	public void testInchi() {
		CMLMolecule molecule = createCO();
		String inchi = InchiTool.generateInchi(molecule, "");
		LOG.debug("INCHI: "+inchi);
	}
	
//	@Test
//	public void testInchiI() {
//		CMLMolecule molecule = createCO();
//		JumboInchi
//		String inchi = InchiTool.generateInchi(molecule, "");
//		LOG.debug("INCHI: "+inchi);
//	}

	private CMLMolecule createCO() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom a1 = new CMLAtom("a1", AS.C);
		a1.setXYZ3(new Point3(0.0, 0.0, 0.0));
		CMLAtom a2 = new CMLAtom("a2", AS.O);
		a1.setXYZ3(new Point3(1.0, 0.0, 0.0));
		molecule.addAtom(a1);
		molecule.addAtom(a2);
		CMLBond b12 = new CMLBond(a1, a2);
		b12.setOrder(CMLBond.DOUBLE_D);
		molecule.addBond(b12);
		molecule.debug("mol");
		return molecule;
	}
}
