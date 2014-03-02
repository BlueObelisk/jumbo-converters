package org.xmlcml.cml.converters.rdf.cml;



import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

/** converts to RDF
 * not yet stable
 * @author pm286
 *
 */
public class CML2RDFConverter extends AbstractConverter {
	private static Logger LOG = Logger.getLogger(CML2RDFConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "mol",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter"
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
		CMLElement cml = CMLBuilder.ensureCML(xml);
//		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendantOrSelf(true);
		CML2RDF cml2rdf = new CML2RDF();
		Element rdf = cml2rdf.convertCMLElement(cml);
		return rdf;
	}

}
