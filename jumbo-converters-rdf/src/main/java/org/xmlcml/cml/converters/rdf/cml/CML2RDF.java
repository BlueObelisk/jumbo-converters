package org.xmlcml.cml.converters.rdf.cml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
//import org.xmlcml.cif.CIFConstants;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;



public class CML2RDF {
	
	private static final Logger LOG = Logger.getLogger(CML2RDF.class);	

	private Model rdfModel;
	
	private HashMap<String, String> PREFIX_MAP;

	private URI ontologyURI;

	private OWLOntology ontology;

	private OWLOntologyManager manager;
	
	public CML2RDF(){
		init();
	}

	/**
	 * 
	 */
	private void init() {
		
		PREFIX_MAP =  new HashMap<String,String>();
		PREFIX_MAP.put(RDFUtils.CML_RDF_PREFIX, RDFUtils.CML_URI_HASH);
//		PREFIX_MAP.put(CIFConstants.IUCR_PREFIX, CIFConstants.IUCR_URI_HASH);
		rdfModel = ModelFactory.createDefaultModel();
		rdfModel.setNsPrefixes(PREFIX_MAP);
		
		loadOntology("org/xmlcml/cml/converters/rdf/cml/ontologies/cifCore.owl");

	}
	
	private void loadOntology(String ontologyResourceName) {
		if (ontology == null) {
			try {
				ontologyURI = getClass().getClassLoader().getResource(ontologyResourceName).toURI();
				manager = OWLManager.createOWLOntologyManager();
				ontology = manager.loadOntologyFromPhysicalURI(ontologyURI);
			} catch (Exception e) {
				throw new RuntimeException("Cannot load ontology: "+ontologyResourceName, e);
			}
//			for(OWLClass cls : ontology.getReferencedClasses()) {
//				System.out.println(cls);
//			}
		}
	}

	/** main entry point
	 * processes by recursive descent
	 * @param cmlElement top element
	 * @return
	 */
	public Element convertCMLElement(CMLElement cmlElement) {
		
		processElement(null, cmlElement, 1000);
			
		Element rdfXml = null;
		try {
//	        LOG.debug("Writing RDF Model.");
	        rdfXml = convertModelToXML(rdfModel);
		} catch (Exception e) {
			throw new RuntimeException("CML exception ", e);
		}
//		CMLUtil.debug(rdfXml, "RDF");
		return rdfXml;
      }
	
	private void processElement(Resource resource, CMLElement cmlElement, int count) {
		if(--count <0 ) {
			throw new RuntimeException("uncontrolled recursion stopped at "+ cmlElement);
		}
		if (false) {
		} else if (cmlElement instanceof CMLAtomArray) {
		} else if (cmlElement instanceof CMLBondArray) {
		} else if (cmlElement instanceof CMLCml) {
			processCml(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLDictionary) {
			processDictionary(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLEntry) {
			processEntry(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLFormula) {
			processFormula(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLIdentifier) {
			processIdentifier(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLLabel) {
			processLabel(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processMetadataList(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processMetadata(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processChildren(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMolecule) {
			processMolecule(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLName) {
			processName(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLParameter) {
			processParameter(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLProperty) {
			processProperty(resource, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLScalar) {
			processScalar(resource, cmlElement, count - 1);
		}
	}

	private void createAndAddTripleFromId(Resource resource, CMLElement cmlElement) {
		Property property = rdfModel.createProperty(RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI_HASH, "hasId"));
		String id = cmlElement.getId();
		if (id != null) {
//			System.out.println("ID "+id);
			RDFUtils.add(rdfModel, resource, property, RDFUtils.removeNonURLCharacters(id));
		}
	}

	private void createAndAddTripleFromAttribute(Resource resource, Attribute attribute) {
		if (attribute != null) {
			String namespaceURI = attribute.getNamespaceURI();
			String localName = attribute.getLocalName();
			if (!"id".equals(localName)) {
				String propertyName = RDFUtils.cleanAndMakeURI(namespaceURI, localName);
				Property property = rdfModel.createProperty(propertyName);
				RDFUtils.add(rdfModel, resource, property, attribute.getValue());
			}
		}
	}
	
	private void processChildren(Resource resource, CMLElement element, int count) {
		List<CMLElement> childElements = element.getChildCMLElements();
		if (childElements.size() > 0) {
			for (CMLElement child : childElements) {
				processElement(resource, child, count - 1);
			}
		} else {
			String content = element.getValue();
			if (content != null && !content.trim().equals(CMLConstants.S_EMPTY)) {
		    	Property property = rdfModel.createProperty(
		    			RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI_HASH, RDFUtils.HAS_VALUE));
		    	if (property != null) {
		    		
//			    	System.out.println("CONT "+content);
					RDFUtils.add(rdfModel, resource, property, rdfModel.createTypedLiteral(content));
		    	}
			}
		}
		
	}
	
	/**
	 * @param cmlElement
	 */
	private void processCml(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processDictionary(Resource resource, CMLElement cmlElement, int count) {
		resource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(resource, cmlElement, count - 1);
		createAndAddTripleFromAttribute(resource, cmlElement.getAttribute("namespace"));
	}

	/**
	 * @param resource
	 * @param cmlElement
	 * @return null if id = null
	 */
	private Resource createResourceAndAddToSubject(Resource resource, CMLElement cmlElement) {
		Resource newResource = null;
		String id = RDFUtils.removeNonURLCharacters(cmlElement.getId());
		String uri = RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI, id);
		// no id, make one
		if (id == null) {
//			uri = CML_URI_HASH+RDFUtils.createUUIDURN();
			uri = RDFUtils.createUUIDURN();
		}
//		LOG.debug("URI"+uri);
		newResource = rdfModel.createResource(uri);
		Property type = rdfModel.createProperty(RDFUtils.RDF_TYPE);
		if (cmlElement instanceof CMLScalar || cmlElement instanceof CMLArray) {
			// do not add type for scalar, array
		} else {
			Resource cmlType = rdfModel.createResource(getCMLType(cmlElement));
			RDFUtils.add(rdfModel, newResource, type, cmlType);
		}
		if (resource != null) {
			String propertyName = getCMLProperty(cmlElement);
			Property property = rdfModel.createProperty(propertyName);
			RDFUtils.add(rdfModel, resource, property, newResource);
		}
		return newResource;
	}
	
	/**
	 * @param cmlElement
	 * @return
	 */
	private String getCMLType(CMLElement cmlElement) {
		String propertyName = RDFUtils.CML_URI_HASH+cmlElement.getLocalName();
		return propertyName;
	}
	
	/**
	 * @param cmlElement
	 * @return
	 */
	private String getCMLProperty(CMLElement cmlElement) {
		String propertyName = RDFUtils.CML_URI_HASH+"has"+Util.capitalise(cmlElement.getLocalName());
		return propertyName;
	}
	
	/**
	 * @param cmlElement
	 */
	private void processEntry(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}
	
	/**
	 * @param cmlElement
	 */
	private void processFormula(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processIdentifier(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processLabel(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processMetadataList(Resource resource, CMLElement cmlElement, int count) {
		processChildren(resource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processMetadata(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}
	
	/**
	 * @param cmlElement
	 */
	private void processMolecule(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}
	/**
	 * @param cmlElement
	 */
	private void processName(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processParameter(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processProperty(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processScalar(Resource resource, CMLElement cmlElement, int count) {
		Resource newResource = createResourceAndAddToSubject(resource, cmlElement);
		addIDAttributesAndChildren(newResource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void addIDAttributesAndChildren(Resource resource, CMLElement cmlElement, int count) {
		createAndAddTripleFromId(resource, cmlElement);
//		LOG.debug("AIAAC");
		addAttributesAndChildren(resource, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void addAttributesAndChildren(Resource resource, CMLElement cmlElement, int count) {
		for (int i = 0; i < cmlElement.getAttributeCount(); i++) {
			Attribute attribute = cmlElement.getAttribute(i);
			String attName = attribute.getLocalName();
			if (attName.equals("id")) {
			} else if (attName.equals("dataType")) {
				// skip data type
			} else if (attName.equals("dictRef")) {
				String entryName = attribute.getValue();
				String prefix = DictRefAttribute.getPrefix(entryName);
				String entryId = DictRefAttribute.getLocalName(entryName);
				entryId = RDFUtils.removeNonQNameCharacters(entryId);
				Resource entryClass = rdfModel.createResource(prefix+CMLConstants.S_COLON+entryId);
				Property typeProperty = rdfModel.createProperty(RDFUtils.RDF_TYPE);
				RDFUtils.add(rdfModel, resource, typeProperty, entryClass);
			} else {
				createAndAddTripleFromAttribute(resource, attribute);
			}
		}
		processChildren(resource, cmlElement, count - 1);
	}

	public static Element convertModelToXML(Model rdfModel){
		Element xml = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   	 
	   	rdfModel.write(baos);
	   	String s = new String(baos.toByteArray());
		try {
			xml = new Builder().build(new StringReader(s)).getRootElement();
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
}
