package org.xmlcml.cml.converters.molecule.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.molecule.filter.AtomCountFilter;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.euclid.RealRange;


public class AtomCountFilterTest {
	@Test
	public void testExactAtomTypes1() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 5 H 12 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCO");
		boolean accept = acf.accept(molecule);
		Assert.assertTrue(accept);
	}

	@Test
	public void testExactAtomTypes2() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 5 H 12 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCO");
		Assert.assertFalse(acf.accept(molecule));
	}

	@Test
	public void testExactAtomTypes3() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 3 H 12 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCCO");
		Assert.assertFalse(acf.accept(molecule));
	}

	@Test
	public void testImplicitAtomTypes() {
		// should create H count of 0
		CMLFormula lowFormula = CMLFormula.createFormula("C 1 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 3 H 8 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCCO");
		Assert.assertTrue(acf.accept(molecule));
	}

	@Test
	public void testExactAtomTypesHydrogen() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 4 H 6 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCCCO");
		Assert.assertFalse(acf.accept(molecule));
	}

	@Test
	public void testMissingAtomTypes() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 5 H 12 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("CCC");
		Assert.assertFalse(acf.accept(molecule));
	}

	@Test
	public void testExtraneousElement() {
		CMLFormula lowFormula = CMLFormula.createFormula("C 2 H 4 O 1");
		CMLFormula highFormula = CMLFormula.createFormula("C 5 H 12 O 3");
		AtomCountFilter acf = new AtomCountFilter(lowFormula, highFormula, false);
		CMLMolecule molecule = SMILESTool.createMolecule("OCSCO");
		Assert.assertFalse(acf.accept(molecule));
	}

}
