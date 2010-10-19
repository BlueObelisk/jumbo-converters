package org.xmlcml.cml.converters.rdf.owl;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

/** probably obsolete
 * 
 * @author pm286
 *
 */
public class OWL2RDFConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(OWL2RDFConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public static String MOL_PREFIX = "http://www.polymerinformatics.com/ChemistryOntology#MolecularEntity";
    public static String TEST_CHEMFILE = "owlFiles/cml2rdf27589.owl";
    public static String TEST_PROPFILE = "owlFiles/cml2rdf27589.owl";
    public static String TEST_OWLFILE = "owlFiles/cml2rdf27589.owl";
    public static String TEST_RDFOUTPUT = "src/test/resources/rdfFiles/";
    public static String TEST_RDFFOLDER = "rdfFiles/";
	public static String ONT_BASE = "http://www.polymerinformatics.com/MolecularRepository.owl#";
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/owl.out",
		"-odir", "../rdf.out",
		"-is", "cml.owl",
		"-os", "cml.rdf",
		"-converter", "org.xmlcml.cml.converters.cml.owl.OWL2RDFConverter"
	};
	
	public Type getInputType() {
		return Type.OWL;
	}

	public Type getOutputType() {
		return Type.RDFXML;
	}

	/**
	 * 
	 */
	@Override
	public Element convertToXML(Element xml) {
		Element outXML = null;
		/** obsolete
		OWL2RDF owl2Rdf = new OWL2RDF();
//		CMLUtil.debug(xml, "OWL0");
		removeImportNodes(xml);
//		CMLUtil.debug(xml, "OWL");
		owl2Rdf.loadModel(xml);
		Set<OWLClassAssertionAxiom> molIndividuals = createIndividuals(xml);
		owl2Rdf.setMolIndividuals(molIndividuals);
		try {
			outXML = owl2Rdf.convertToRDF();
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse OWL 2 RDF", e);
		}
		*/
		if (true) throw new RuntimeException("NYI");
		return outXML;
	}

	/** removes unnecessary "owl:imports".
	 * 
	 * @param xml
	 */
	@SuppressWarnings("unused")
	private void removeImportNodes(Element xml) {
		Nodes importNodes = xml.query(".//*[local-name()='imports']");
		for (int i = 0; i < importNodes.size(); i++) {
			Node node = importNodes.get(i);
			node.detach();
		}
	}
	


}
