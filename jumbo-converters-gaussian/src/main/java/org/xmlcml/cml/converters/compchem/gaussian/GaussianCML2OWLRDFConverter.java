package org.xmlcml.cml.converters.compchem.gaussian;

import java.io.File;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.AbstractCompchemCML2XHTMLConverter;
import org.xmlcml.cml.converters.rdf.cml.CML2OWLRDF;
import org.xmlcml.cml.element.CMLCml;


public class GaussianCML2OWLRDFConverter extends AbstractCompchemCML2XHTMLConverter {
	
	private static final Logger LOG = Logger.getLogger(GaussianCML2OWLRDFConverter.class);
	private String AUX_FILENAME = "org/xmlcml/cml/converters/rdf/cml/ontologies/gaussianArchiveDict.owl";
	
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public GaussianCML2OWLRDFConverter() {
		super();
		getCommand().setAuxfileName(AUX_FILENAME);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.RDFXML;
	}

	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "D:/projects/cost/gaussian",
		"-odir", "../gaussian.cml",
		// kludge
		"-is", "gau.cml",
		"-os", "html",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "org/xmlcml/cml/converters/rdf/cml/ontologies/gaussianArchiveDict.owl",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianCML2OWLRDFConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/gaussian",
		"-odir", "../gaussian.cml",
		// kludge
		"-is", "gau.cml",
		"-os", "html",
		// dictionary file; should not be necessary to have full file name
		// eclipse picks this up from resources but any does not
		"-aux", "src/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml",
		"-converter", "org.xmlcml.cml.converters.compchem.gaussian.GaussianCML2OWLRDFConverter"
	};

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in
	 *            input stream
	 */
	public Element convertToXML(Element xml) {
		CMLElement cml = ensureCML(xml);
		String relativeURINameForOntology = getCommand().getAuxfileName();
		if (relativeURINameForOntology == null) {
			throw new RuntimeException("Ontology must be specified in auxFile");
		}
		CML2OWLRDF cml2owlrdf = new CML2OWLRDF(relativeURINameForOntology);
		// only use one molecule
		if (cml instanceof CMLCml) {
			Nodes cmlChildNodes = cml.query("./cml:cml", CMLConstants.CML_XPATH);
			// get last molecule
			if (cmlChildNodes.size() > 0) {
				cml = ((CMLCml) cmlChildNodes.get(cmlChildNodes.size()-1));
			}
		}
		Element rdf = cml2owlrdf.convertCMLElement(cml);
		return rdf;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	

}
