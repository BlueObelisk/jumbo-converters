package org.xmlcml.cml.converters.rdf.cml;



import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Type;

/** converts to RDF
 * not yet stable
 * @author pm286
 *
 */
public class CML2OWLRDFConverter extends AbstractConverter {
	private static Logger LOG = Logger.getLogger(CML2RDFConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "mol",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2OWLRDFConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.RDFXML;
	}

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
		Element rdf = cml2owlrdf.convertCMLElement(cml);
		return rdf;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
