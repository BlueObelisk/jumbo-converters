package org.xmlcml.cml.converters.molecule.fragments;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class FragmentTest {

	@Test
	public void testFragmentsOrganic() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.setOutputDir(new File("target/2234529"));
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/2234529.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsInorganic() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.setOutputDir(new File("target/1008945"));
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/1008945.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsMetal() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.setOutputDir(new File("target/metal"));
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/metal.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	public void testFragmentsMetal1() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.setOutputDir(new File("target/mbok1z"));
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/mbok1z.cml"));
		fragmentGenerator.createMoietiesAndFragments();
	}
	
	@Test
	@Ignore
	public void testConverter() {
		CML2FragmentConverter converter = new CML2FragmentConverter();
		converter.convert(new File("src/test/resources/molecule/fragments/mbok1z.cml"), new File("target/codcy/"));
	}
	
	@Test
	@Ignore
	public void testCODOrganic() {
		CML2FragmentConverter converter = new CML2FragmentConverter();
    	String[] args = {"-i", "../../cifs/codOrganic20140120/cml/", "-o", "../../cifs/codOrganic20140120/codcy/"};
    	converter.runArgs(args);
	}

	@Test
	@Ignore
	public void testCODMetalOrganic() {
		CML2FragmentConverter converter = new CML2FragmentConverter();
    	String[] args = {"-i", "../../cifs/codMetalOrganic20140120/cml/", "-o", "../../cifs/codMetalOrganic20140120/codcy/"};
    	converter.runArgs(args);
	}
	
	@Test
	public void testMultipleFileInput() {
		CML2FragmentConverter converter = new CML2FragmentConverter();
    	String[] args = {
    			"src/test/resources/molecule/fragments/metal.cml", 
    			"src/test/resources/molecule/fragments/mbok1z.cml",
    			"src/test/resources/molecule/fragments/1008945.cml",
    			"-o", "target/multiple"};
    	converter.runArgs(args);
	}
}
