package org.xmlcml.cml.converters.molecule.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.molecule.filter.AtomicNumberFilter;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.euclid.IntRange;


public class AtomicNumberFilterTest {
	@Test
	public void testRange1() {
		AtomicNumberFilter anf = new AtomicNumberFilter(new IntRange(1, 10));
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCO");
		boolean accept = anf.accept(molecule);
		Assert.assertTrue(accept);
	}
	
	@Test
	public void testRange2() {
		AtomicNumberFilter anf = new AtomicNumberFilter(new IntRange(3, 10));
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCO");
		boolean accept = anf.accept(molecule);
		Assert.assertFalse(accept);
	}

	@Test
	public void testRange3() {
		AtomicNumberFilter anf = new AtomicNumberFilter(new IntRange(1, 7));
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCO");
		boolean accept = anf.accept(molecule);
		Assert.assertFalse(accept);
	}

}
