package org.xmlcml.cml.converters.molecule.fragments;

import java.io.File;

import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.molecule.fragments.ChemistryUtils.CompoundClass;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;

public class FragmentTest {

	@Test
	public void testFragmentsOrganic() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/2234529.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsInorganic() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/1008945.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsMetal() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/metal.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsMetal1() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/mbok1z.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	
}
