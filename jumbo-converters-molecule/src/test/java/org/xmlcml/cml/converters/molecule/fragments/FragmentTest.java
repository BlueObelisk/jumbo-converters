package org.xmlcml.cml.converters.molecule.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MorganMD5;

public class FragmentTest {

	private final static Logger LOG = Logger.getLogger(FragmentTest.class);
	
	@Test
	public void testFragmentsOrganic() {
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		File outputDir = new File("target/2234529");
		fragmentGenerator.setOutputDir(outputDir);
		fragmentGenerator.readMolecule(new File("src/test/resources/molecule/fragments/2234529.cml"));
		fragmentGenerator.createMoietiesAndFragments();
		Assert.assertTrue("output", outputDir.exists());
		Assert.assertTrue("output", outputDir.isDirectory());
		Assert.assertEquals("child", 1, outputDir.listFiles().length); 
		File molDir = new File(outputDir, "mol");
		Assert.assertTrue("mol", molDir.exists());
		Assert.assertTrue("mol", molDir.isDirectory());
		Assert.assertEquals("child", 4, molDir.listFiles().length); 
		File totalCml = new File(molDir, "total.cml");
		Assert.assertTrue("cml", totalCml.exists());
		Assert.assertTrue("cml", !totalCml.isDirectory());
		Assert.assertTrue("cml", FileUtils.sizeOf(totalCml) > 0);
		
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
//	@Ignore
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
	
	@Test
	public void testMoietyMorgan() {
		CML2FragmentConverter converter = new CML2FragmentConverter();
    	String[] args = {
    			"src/test/resources/molecule/fragments/metal.cml", 
    			"src/test/resources/molecule/fragments/mbok1z.cml",
    			"src/test/resources/molecule/fragments/1008945.cml",
    			"-o", "target/morgan"};
    	converter.runArgs(args);
    	File morgan = new File("target/morgan");
    	List<CMLMolecule> moleculeList = extractMolecules(morgan, "total.cml");
    	Assert.assertEquals("mols", 3, moleculeList.size());
    	Assert.assertEquals("mol0", "a67a4ec271e98374971652ba7c74c2e7", MorganMD5.createMorganMD5(moleculeList.get(0)));
    	Assert.assertEquals("mol1", 
    			"*1*1eec23a7159633e318b1467d28da7e*1*497e7f85d8fdf5e62868ffbfb6753b7*1*a736d5e0a0b1e5ea3cc8ed6b4b84b9c2*1*ab92bfc57c84c37b71a1b77712663b9", 
    			MorganMD5.createMorganMD5(moleculeList.get(1)));
    	Assert.assertEquals("mol2", 
    			"*1*71e8e86626ce429f9db3119826dcaeb7*1*77ca9cc6f5dcad7db66a5218c35273ca*1*cb3ecfc166e7f3010a0f185a83cc9da", 
    			MorganMD5.createMorganMD5(moleculeList.get(2)));
	}
	
	@Test
	public void testMoietyMorganMoiety() {
    	File morgan = new File("target/morgan");
    	if (!morgan.exists()) throw new RuntimeException("need morgan directory");
    	List<String> morganList = createMorganList(morgan, "molecule.cml");
    	Assert.assertEquals("morgan", 5, morganList.size());
	}

	@Test
	public void testMoietyMorganFragment() {
    	File morgan = new File("target/morgan");
    	if (!morgan.exists()) throw new RuntimeException("need morgan directory");
    	List<String> morganList = createMorganList(morgan, "fragment.cml");
    	Assert.assertEquals("morgan", 36, morganList.size());
    	Collections.sort(morganList);
    	for (String s : morganList) {
    		System.out.println(s);
    	}
	}

	// ===================================================

	private List<String> createMorganList(File morgan, String filename) {
		List<CMLMolecule> moleculeList = extractMolecules(morgan, filename);
		List<String> morganList = new ArrayList<String>();
    	for (CMLMolecule molecule : moleculeList) {
    		morganList.add(MorganMD5.createMorganMD5(molecule));
    	}
    	return morganList;
	}

	private List<CMLMolecule> extractMolecules(File morgan, String filename) {
		List<File> files = extractFiles(morgan, filename);
    	List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
    	for (File file : files) {
    		CMLMolecule molecule = (CMLMolecule) CMLUtil.getQueryElements(CMLUtil.parseQuietlyIntoCML(file),
    				"//cml:molecule", CMLConstants.CML_XPATH).get(0);
    		moleculeList.add(molecule);
    	}
    	return moleculeList;
	}

	private List<File> extractFiles(File top, String filename) {
		final String f = filename;
		List<File> files = new ArrayList<File>(FileUtils.listFiles(top, new IOFileFilter() {
    		public boolean accept(File file) {
    			return file.getName().equals(f);
    		}
			public boolean accept(File dir, String name) {
				return name.equals(f);
			}
    	},
    	TrueFileFilter.INSTANCE));
		return files;
	}
}
