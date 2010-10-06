package org.xmlcml.cml.converters.compchem.gaussian;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.euclid.Util;

public class GaussianArchive2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianArchive2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	// really awful, but ant cannot pick up classpath
	public final static String DICT_FILE = 
		"src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";
	
	public static final String GAUSS_PREFIX = "gauss";
	public static final String GAUSS_URI = "http://wwmm.ch.cam.ac.uk/dict/gauss";
	
	public final static String[] typicalArgsForConverterCommand = {
//		"-sd", "src/test/resources/gaussian",
		"-sd", "D:/projects/cost/gaussian/files",
		"-odir", "../gaussian.cml",
		"-is", "log",
		"-os", "gau.cml",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "D:/workspace/jumbo-converters/src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianArchive2CMLConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/gaussian",
		"-odir", "../gaussian.cml",
		"-is", "gau",
		"-os", "gau.cml",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianArchive2CMLConverter"
	};
	
	public Type getInputType() {
		return Type.GAU_ARC;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml topCml = new CMLCml();
		CMLDictionary dictionary = findDictionary();
		GaussianArchiveProcessor converter = new GaussianArchiveProcessor(dictionary, this.getCommand());
		converter.setConverterLog(converterLog);
		List<CMLElement> cmlElementList = converter.readArchives(lines);
		for (CMLElement cmlElement : cmlElementList) {
			topCml.appendChild(cmlElement);
		}
		try {
			ensureId(topCml);
		} catch (RuntimeException e) {
			// no id
		}
		topCml = processParamsTopMetadataNamespaces(topCml);
		return topCml;
	}

	public void addNamespaces(CMLCml topCml) {
		addCommonNamespaces(topCml);
		topCml.addNamespaceDeclaration(GAUSS_PREFIX, GAUSS_URI);
	}
	
	private CMLDictionary findDictionary() {
		CMLDictionary dictionary = null;
		String resourceS = "org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";
		try {
			InputStream inputStream = Util.getInputStreamFromResource(resourceS);
			CMLCml cml = (CMLCml) new CMLBuilder().build(inputStream).getRootElement();
			dictionary = (CMLDictionary)cml.getFirstCMLChild(CMLDictionary.TAG);
			if (dictionary == null) {
				throw new IllegalStateException("Failed to find dictionary element in "+resourceS);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read dictionary", e);
		}
		return dictionary;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
