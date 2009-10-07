package org.xmlcml.cml.converters.compchem.gaussian.input;


import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2GaussianInputConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(CML2GaussianInputConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	// really awful, but ant cannot pick up classpath
	public final static String DICT_FILE = 
		"src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";
	
	public static final String GAUSS_PREFIX = "gauss";
	public static final String GAUSS_URI = "http://wwmm.ch.cam.ac.uk/dict/gauss";
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "D:/projects/cost/gaussian/files",
		"-odir", "../gaussian.in",
		"-is", "cml",
		"-os", "in.gau",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "D:/workspace/jumbo-converters/src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianArchive2CMLConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/gaussian/input",
		"-odir", "../gaussian.in",
		"-is", "cml",
		"-os", "in.gau",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianArchive2CMLConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.GAU_IN;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public List<String> convertToText(Element xml) {
		List<String> stringList = null;
		CMLElement cml = ensureCML(xml);
		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(true);
		if (molecule != null) {
			setMolecule(molecule);
			GaussianInputProcessor gaussianInputProcessor = new GaussianInputProcessor(this);
			stringList = gaussianInputProcessor.makeInput();
		}
		return stringList;	
	}
	
	public void addNamespaces(CMLCml topCml) {
		addCommonNamespaces(topCml);
		topCml.addNamespaceDeclaration(GAUSS_PREFIX, GAUSS_URI);
	}
	
	@Override
	public int getConverterVersion() {
		return 0;
	}
	

}
