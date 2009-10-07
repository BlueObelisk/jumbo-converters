package org.xmlcml.cml.converters.rdf.cml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import nu.xom.Builder;
import nu.xom.Element;

import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.io.StreamOutputTarget;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.xmlcml.cml.base.CMLConstants;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFUtils {

	public static String HAS_VALUE= "hasValue";
	public static String RDF_TYPE ="http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	public static String CML_PREFIX = "cml";
	public static String CML_RDF_PREFIX = "cmlRdf";
	public static String CML_URI = "http://www.xml-cml.org/schema";
	public static String CML_URI_HASH = CML_URI+CMLConstants.S_HASH;
	
	public static String XSD_PREFIX = "xsd";
	public static String XSD_URI = "http://www.w3.org/2001/XMLSchema";
	public static String XSD_URI_HASH = XSD_URI+CMLConstants.S_HASH;
	
	public static String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	
	public static String CMLX_URI = "http://www.xml-cml.org/schema/cmlx";
	public static String CMLX_URI_HASH = CMLX_URI+CMLConstants.S_HASH;
	public static String CMLX_PREFIX = "cmlx";
	public static String CMLX_XMLNS_PREFIX = CMLConstants.XMLNS + CMLConstants.S_COLON + CMLX_PREFIX + 
	    CMLConstants.S_EQUALS + CMLConstants.S_APOS + CMLX_URI + CMLConstants.S_APOS;
	
	/**
	 * @return
	 */
	public static Resource createBNode(Model rdfModel) {
		return rdfModel.createResource(RDFUtils.createUUIDURN());
	}

	public static String createUUIDURN() {
		return new StringBuilder("urn:uuid:").append(createUUID()).toString();
	}

	public static String createUUID() {
		return UUID.randomUUID().toString();
	}

	public static URI createUUIDURI() {
		URI uri = null;
		try {
			uri = new URI(createUUIDURN());
		} catch (URISyntaxException e) {
			throw new RuntimeException("Bad URI");
		}
		return uri;
	}

	public static String removeNonURLCharacters(String s) {
		String ss = null;
		if (s != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
					sb.append(c);
				}
			}
			ss = sb.toString();
		} return ss;
	}

	public static String removeNonQNameCharacters(String s) {
		String ss = null;
		if (s != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '.' || c == '_') {
					sb.append(c);
				}
			}
			ss = sb.toString();
		} return ss;
	}

	public static String cleanAndMakeURI(String namespaceURI, String localName) {
		if (namespaceURI == null || namespaceURI == CMLConstants.S_EMPTY) {
			namespaceURI = RDFUtils.CML_URI_HASH;
		}
		if (!namespaceURI.endsWith(CMLConstants.S_HASH)) {
			namespaceURI += CMLConstants.S_HASH;
		}
		localName = removeNonURLCharacters(localName);
		String uri = namespaceURI + localName;
		return uri;
	}

	public static String tidyId(String id) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	static void add(Model rdfModel, Resource resource, Property property, Resource newResource) {
		if (rdfModel == null) {
		} else if (resource == null) {
		} else if (property == null) {
		} else if (newResource == null) {
		} else {
			rdfModel.add(resource, property, newResource);
		}
	
	}

	static void add(Model rdfModel, Resource resource, Property property, String value) {
		if (rdfModel == null) {
		} else if (resource == null) {
		} else if (property == null) {
		} else if (value == null) {
		} else {
			rdfModel.add(resource, property, value);
		}
	}

	static void add(Model rdfModel, Resource resource, Property property, Literal value) {
		if (rdfModel == null) {
		} else if (resource == null) {
		} else if (property == null) {
		} else if (value == null) {
		} else {
			rdfModel.add(resource, property, value);
		}
	}

	public static Element convertOntologyToXML(OWLOntologyManager manager, OWLOntology ontology){
		Element xml = null;
		try {
			File tempFile = File.createTempFile("convertedCML", ".owl");
			manager.saveOntology(ontology, tempFile.toURI());
			xml = new Builder().build(tempFile).getRootElement();
			tempFile.delete();
		} catch (Exception e) {
			throw new RuntimeException("Cannot write ontology ", e);
		}
//		CMLUtil.debug(xml, "ONTOLOGY");
		return xml;
	}

	public static void makeObjectPropertyAssert(OWLIndividual subject, OWLObjectProperty predicate, OWLIndividual object,
			OWLDataFactory ontologyFactory, OWLOntology cmlOntology, OWLOntologyManager ontologyManager) throws OWLOntologyChangeException {
		OWLObjectPropertyAssertionAxiom ax = ontologyFactory.getOWLObjectPropertyAssertionAxiom(subject, predicate, object);
		AddAxiom addax = new AddAxiom(cmlOntology, ax);
		ontologyManager.applyChange(addax);
	}

	/**
	 * gets XML element from "ontology"
	 * @return
	 */
	public static Element getRDFOWLAsXML(OWLOntologyManager ontologyManager, OWLOntology cmlOntology) {
	    Element xml = null;
	    try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        RDFUtils.writeRDFOWLAsOutputStream(ontologyManager, cmlOntology, baos);
	        String xmlString = new String(baos.toByteArray());
	        // reparse to get rid of DTD horror
	        xml = new Builder().build(new StringReader(xmlString)).getRootElement();
	        Element newElement = new Builder().build(new StringReader(xml.toXML())).getRootElement();
	        // set CML namespace to be explicit
	        for (int i = 0; i < xml.getNamespaceDeclarationCount(); i++) {
	        	String prefix = xml.getNamespacePrefix(i);
	        	String ns = xml.getNamespaceURI(prefix);
	        	if (ns.equals(CML_URI_HASH)) {
	        		xml.removeNamespaceDeclaration(prefix);
	        		xml.addNamespaceDeclaration(CML_PREFIX, CML_URI_HASH);
	        	}
	        }
	        xml = newElement;
	    } catch (Exception e) {
	        throw new RuntimeException("Owl/Parse failure", e);
	    }
	    return xml;
	}

	public static void writeRDFOWLAsOutputStream(OWLOntologyManager ontologyManager, OWLOntology cmlOntology, OutputStream os) {
	    StreamOutputTarget sot = new StreamOutputTarget(os);
	    try {
	        ontologyManager.saveOntology(cmlOntology,  new RDFXMLOntologyFormat(), sot);
	    } catch (Exception e) {
	        throw new RuntimeException("OWL Storage exception ", e);
	    }
	}

	/**
	 * @param superClass
	 * @param subClassName
	 * @throws OWLOntologyChangeException
	 */
	public static OWLClass createAndAddSubclass(OWLClass superClass, String uriNameHash, String subClassName,
			OWLDataFactory ontologyFactory, OWLOntology cmlOntology, OWLOntologyManager ontologyManager)
			throws OWLOntologyChangeException {
		OWLClass subClass = ontologyFactory.getOWLClass(URI.create(uriNameHash + subClassName));
		OWLAxiom axiom = ontologyFactory.getOWLSubClassAxiom(subClass, superClass);
		AddAxiom addAxiom = new AddAxiom(cmlOntology, axiom);
		ontologyManager.applyChange(addAxiom);
		return subClass;
	}

}
