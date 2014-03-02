package org.xmlcml.cml.converters.molecule.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.molecule.filter.MoleculeMassFilter;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.euclid.RealRange;


public class MoleculeMassFilterTest {
	@Test
	public void testNull() {
		try {
			MoleculeMassFilter mmf = new MoleculeMassFilter(null);
			Assert.fail("should trap null");
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void testMolecule1() {
		MoleculeMassFilter mmf = new MoleculeMassFilter(new RealRange(100., 200.));
		CMLMolecule molecule = SMILESTool.createMolecule("CC");
		Assert.assertFalse(mmf.accept(molecule));
	}

	@Test
	public void testMolecule2() {
		MoleculeMassFilter mmf = new MoleculeMassFilter(new RealRange(10., 200.));
		CMLMolecule molecule = SMILESTool.createMolecule("CC");
		Assert.assertTrue(mmf.accept(molecule));
	}

	@Test
	public void testMolecule3() {
		MoleculeMassFilter mmf = new MoleculeMassFilter(new RealRange(10., 100.));
		CMLMolecule molecule = SMILESTool.createMolecule("CCCCCCCCCCCC");
		Assert.assertFalse(mmf.accept(molecule));
	}

}
