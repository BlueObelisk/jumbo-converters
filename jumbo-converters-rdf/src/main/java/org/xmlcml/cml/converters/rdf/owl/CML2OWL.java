package org.xmlcml.cml.converters.rdf.owl;

import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.XSDVocabulary;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.rdf.cml.RDFUtils;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;



public class CML2OWL {
	
	private static final Logger LOG = Logger.getLogger(CML2OWL.class);	


	private OWLOntologyManager ontologyManager;
	private URI cmlOntologyURI;
	private OWLOntology cmlOntology;
	private OWLDataFactory ontologyFactory;

	private Element classXML;
	private OWLClass dictionaryClass;
	private OWLClass entryClass;
	private OWLClass abstractElementClass;
	private OWLClass abstractMetadataClass;

	private OWLDataType FLOAT_DATA_TYPE;
	private OWLDataType INT_DATA_TYPE;
	private OWLDataType STRING_DATA_TYPE;

	private OWLObjectProperty hasEntry;
	private OWLDataProperty hasID;
	private OWLObjectProperty hasMetadata;
	private OWLClass attributeClass;
	private OWLClass unitsClass;
	
	public CML2OWL(){
		init();
	}

	/**
	 * 
	 */
	private void init() {
        try {
			createCMLOntologyManagerAndFactory();
			INT_DATA_TYPE = ontologyFactory.getOWLDataType(XSDVocabulary.INT.getURI());
			FLOAT_DATA_TYPE = ontologyFactory.getOWLDataType(XSDVocabulary.FLOAT.getURI());
			STRING_DATA_TYPE = ontologyFactory.getOWLDataType(XSDVocabulary.STRING.getURI());

		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException("Cannot create ontology", e);
		}
	}
	

	/** main entry point
	 * assumes a cml:dictionary and cml:entry children
	 * processes by recursive descent
	 * @param cmlElement top element
	 * @return
	 * @throws OWLOntologyChangeException 
	 */
	public Element convertCMLElement(CMLElement cmlElement) {
		
		Element rdfXml = null;
		Nodes dictionaryNodes = cmlElement.query("//cml:dictionary", CMLConstants.CML_XPATH);
		if (dictionaryNodes.size() == 1) {
			try {
				CMLDictionary dictionary = (CMLDictionary) dictionaryNodes.get(0);
				// abstract classes for element and attribute
				abstractElementClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#element"));
				abstractMetadataClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#metadata"));
				// classes for dictionary and entry
				dictionaryClass = RDFUtils.createAndAddSubclass(abstractElementClass, RDFUtils.CML_URI_HASH, "Dictionary",
						ontologyFactory, cmlOntology, ontologyManager);
				entryClass = RDFUtils.createAndAddSubclass(abstractElementClass, RDFUtils.CML_URI_HASH, "Entry",
						ontologyFactory, cmlOntology, ontologyManager);
				unitsClass = ontologyFactory.getOWLClass(URI.create(RDFUtils.CML_URI_HASH + "units"));
				// dictionary can hold entries
				hasEntry = ontologyFactory.getOWLObjectProperty(new URI(RDFUtils.CML_URI_HASH + "hasEntry"));
				addObjectRestriction(dictionaryClass, hasEntry, entryClass);
				// dictionary can have ID
				addDataRestriction(dictionaryClass,"hasID",STRING_DATA_TYPE);
				// entry can have ID
				
				addDataRestriction(entryClass,"hasID",STRING_DATA_TYPE);
//				addDataRestriction(entryClass,"hasTerm",STRING_DATA_TYPE);
				
//				hasMetadata = ontologyFactory.getOWLObjectProperty(new URI(RDFUtils.CML_URI_HASH + "hasMetadata"));
//				// each entry can have several metadata members
//				addObjectRestriction(entryClass, hasMetadata, abstractMetadataClass);
				

				// make single dictionary individual
				String id = "dictionary1";
				OWLIndividual dictionaryIndividual = makeAndAddInstance(dictionaryClass, id);
				// add attributes on dictionary
				addCMLAttributesAsMetadata(dictionaryIndividual, dictionary);
				// add individual for entryNodes, adding attribute classes as they occur
				addEntryNodes(dictionary, dictionaryIndividual);
				rdfXml = RDFUtils.getRDFOWLAsXML(ontologyManager, cmlOntology);
			} catch (Exception e) {
				throw new RuntimeException("Ontology error: "+e.getMessage()+"/"+e.getCause(), e);
			}
		} else {
			throw new RuntimeException("Cannot find root dictionary");
		}
		return rdfXml;
    }

	/** add triple where predicate is a restriction
	 * 
	 * @param owlSubjectClass
	 * @param predicateProperty
	 * @param owlObjectClass
	 * @throws OWLOntologyChangeException
	 */
	private void addObjectRestriction(OWLClass owlSubjectClass, OWLObjectProperty predicateProperty, OWLClass owlObjectClass ) throws OWLOntologyChangeException {
		OWLObjectSomeRestriction hasObjectRestriction = ontologyFactory.getOWLObjectSomeRestriction(predicateProperty, owlObjectClass);
		OWLSubClassAxiom hasObjectAxiom = ontologyFactory.getOWLSubClassAxiom(owlSubjectClass, hasObjectRestriction);
		ontologyManager.applyChange(new AddAxiom(cmlOntology, hasObjectAxiom));
	}

	/** add type restriction to data
	 * 
	 * @param owlClass
	 * @param dataPropertyName
	 * @param dataType
	 * @throws OWLOntologyChangeException
	 * @throws URISyntaxException
	 */
	private void addDataRestriction(OWLClass owlClass, String dataPropertyName, OWLDataType dataType) throws OWLOntologyChangeException, URISyntaxException {
		OWLDataProperty dataProperty = ontologyFactory.getOWLDataProperty(
				new URI(RDFUtils.CML_URI_HASH + dataPropertyName));

		OWLDataSomeRestriction hasDataRestriction = ontologyFactory.getOWLDataSomeRestriction(
				dataProperty, dataType);
		OWLSubClassAxiom hasDataAxiom = ontologyFactory.getOWLSubClassAxiom(owlClass, hasDataRestriction);
		ontologyManager.applyChange(new AddAxiom(cmlOntology, hasDataAxiom));
	}

	/**
	 * iterate through all entry nodes in dictionary
	 * generate a subclass for each
	 * 
	 * @param dictionary
	 * @param dictionaryIndividual
	 * @throws URISyntaxException
	 * @throws OWLOntologyChangeException
	 */
	private void addEntryNodes(CMLDictionary dictionary,
			OWLIndividual dictionaryIndividual) throws URISyntaxException,
			OWLOntologyChangeException {
		String id;
		Nodes entryNodes = dictionary.query("./cml:entry", CMLConstants.CML_XPATH);
		for (int i = 0; i < entryNodes.size(); i++) {
			CMLEntry entry = (CMLEntry) entryNodes.get(i);
			id = RDFUtils.tidyId(CMLUtil.capitalize(entry.getId()));
			OWLClass owlClass = getOWLClass(entry);
//			addCMLAttributesAsMetadata(entryIndividual, entry);
		}
	}
	
	private OWLClass getOWLClass(CMLEntry entry) throws OWLOntologyChangeException, URISyntaxException {
		OWLClass subEntryClass = null;
		// only works for CIF - have to change this later
		Attribute idAttribute = null;
		Attribute superclassAttribute = null;
		Attribute definitionAttribute = null;
		Attribute descriptionAttribute = null;
		Attribute nameAttribute = null;
		Attribute typeAttribute = null;
		Attribute unitsAttribute = null;
		Attribute unitsDescriptionAttribute = null;
		for (int i = 0; i < entry.getAttributeCount(); i++) {
			Attribute attribute = entry.getAttribute(i);
			String localName = attribute.getLocalName();
//			System.out.println(localName);
			if (localName.equals("name")) {
				nameAttribute = attribute;
			} else if (localName.equals("superclass")) {
				superclassAttribute = attribute;
			} else if (localName.equals("id")) {
				idAttribute = attribute;
			} else if (localName.equals("definition")) {
				definitionAttribute = attribute;
			} else if (localName.equals("description")) {
				descriptionAttribute = attribute;
			} else if (localName.equals("type")) {
				typeAttribute = attribute;
			} else if (localName.equals("unitsName")) {
				unitsAttribute = attribute;
			} else if (localName.equals("unitsDescription")) {
				unitsDescriptionAttribute = attribute;
			}
		}
		OWLClass superclassClass = null;
		if (idAttribute == null) {
			throw new RuntimeException("missing id attribute");
		}
		if (nameAttribute == null) {
			LOG.warn("missing name attribute: "+idAttribute);
		
		} else {	
			String attributeValue = nameAttribute.getValue();
			if (superclassAttribute != null) {
				superclassClass = RDFUtils.createAndAddSubclass(entryClass,  RDFUtils.CMLX_URI_HASH, RDFUtils.removeNonURLCharacters(superclassAttribute.getValue()),
						ontologyFactory, cmlOntology, ontologyManager);
				subEntryClass = RDFUtils.createAndAddSubclass(superclassClass,  RDFUtils.CMLX_URI_HASH, RDFUtils.removeNonURLCharacters(attributeValue),
						ontologyFactory, cmlOntology, ontologyManager);
			} else {
				subEntryClass = RDFUtils.createAndAddSubclass(entryClass,  RDFUtils.CMLX_URI_HASH, RDFUtils.removeNonURLCharacters(attributeValue),
						ontologyFactory, cmlOntology, ontologyManager);
			}
			OWLAnnotation idAnnotation = ontologyFactory.getOWLLabelAnnotation(attributeValue);
			OWLAxiom axiom = ontologyFactory.getOWLEntityAnnotationAxiom(subEntryClass, idAnnotation);
			ontologyManager.applyChange(new AddAxiom(cmlOntology, axiom));
			if (subEntryClass != null) {
				if (typeAttribute != null) {
					attributeValue = typeAttribute.getValue();
					if (attributeValue.equals(CMLConstants.XSD_FLOAT)) {
						addDataRestriction(subEntryClass, RDFUtils.HAS_VALUE, FLOAT_DATA_TYPE);
					} else if (attributeValue.equals(CMLConstants.XSD_STRING)) {
						addDataRestriction(subEntryClass, RDFUtils.HAS_VALUE, STRING_DATA_TYPE);
					} else if (attributeValue.equals("null")) {
						// skip this
					}
				}
				if (definitionAttribute != null) {
					 OWLAnnotation definitionAnnotation = ontologyFactory.getCommentAnnotation("def: "+definitionAttribute.getValue(), "en");
					 axiom = ontologyFactory.getOWLEntityAnnotationAxiom(subEntryClass, definitionAnnotation);
					 ontologyManager.applyChange(new AddAxiom(cmlOntology, axiom));
				}
				if (descriptionAttribute != null) {
					 OWLAnnotation descriptionAnnotation = ontologyFactory.getCommentAnnotation("desc: "+descriptionAttribute.getValue(), "en");
					 axiom = ontologyFactory.getOWLEntityAnnotationAxiom(subEntryClass, descriptionAnnotation);
					 ontologyManager.applyChange(new AddAxiom(cmlOntology, axiom));
				}
				if (unitsAttribute != null) {
					OWLClass subUnitsClass = RDFUtils.createAndAddSubclass(unitsClass,  RDFUtils.CML_URI_HASH, RDFUtils.removeNonURLCharacters(unitsAttribute.getValue()),
						ontologyFactory, cmlOntology, ontologyManager);
					String unitsDetail = (unitsDescriptionAttribute != null) ? unitsDescriptionAttribute.getValue() : unitsAttribute.getValue();
					OWLAnnotation unitsAnnotation = ontologyFactory.getOWLLabelAnnotation(unitsDetail);
					axiom = ontologyFactory.getOWLEntityAnnotationAxiom(subUnitsClass, unitsAnnotation);
					ontologyManager.applyChange(new AddAxiom(cmlOntology, axiom));
					OWLObjectProperty hasUnits = ontologyFactory.getOWLObjectProperty(new URI(RDFUtils.CML_URI_HASH + "hasUnits"));
					addObjectRestriction(subEntryClass, hasUnits, subUnitsClass);
				}
			}
	    }	
		return subEntryClass;
	}
	
	
	private OWLIndividual makeAndAddInstance(OWLClass owlClass, String id) {
		OWLIndividual individual = null;
		try {
			individual = ontologyFactory.getOWLIndividual(new URI(RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI, id)));
			OWLClassAssertionAxiom ax = ontologyFactory.getOWLClassAssertionAxiom(individual, owlClass);
			AddAxiom addAx = new AddAxiom(cmlOntology, ax);
			ontologyManager.applyChange(addAx);
		} catch (Exception e) {
			throw new RuntimeException("failed individual", e);
		}
		return individual;
	}

	
	private void addCMLAttributesAsMetadata(OWLIndividual elementIndividual, CMLElement element) {
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute attribute = element.getAttribute(i);
			if (!"id".equals(attribute.getLocalName()) && 
				!"namespace".equals(attribute.getLocalName())) {
				try {
					addAttributeClassAndIndividualAndValue(elementIndividual, element, attribute);
				} catch (Exception e) {
					System.err.println(elementIndividual + "/" + element + "/" + attribute);
					throw new RuntimeException("some horrible ontology exception - good luck", e);
				}
			}
		}
	}
	
	private void addAttributeClassAndIndividualAndValue(OWLIndividual elementIndividual, CMLElement element, Attribute attribute) throws OWLOntologyChangeException, URISyntaxException {
		String name = RDFUtils.removeNonURLCharacters(attribute.getLocalName());
		@SuppressWarnings("unused")
		String uriS = RDFUtils.CML_URI_HASH + name;
		// make attribute subclass; don't worry about duplicates
		OWLClass namedAttributeClass = RDFUtils.createAndAddSubclass(abstractMetadataClass, RDFUtils.CML_URI_HASH, name,
				ontologyFactory, cmlOntology, ontologyManager);
		
		OWLIndividual attributeIndividual = makeAndAddInstance(namedAttributeClass, element.getId()+name);
		//OWLObjectProperty hasAttribute = ontologyFactory.getOWLObjectProperty(new URI(RDFUtils.CML_URI_HASH + "hasAttribute"));
		RDFUtils.makeObjectPropertyAssert(elementIndividual, hasMetadata, attributeIndividual, 
				ontologyFactory, cmlOntology, ontologyManager);
		// add value
		String value = attribute.getValue();
		OWLDataPropertyExpression hasValue = ontologyFactory.getOWLDataProperty(new URI(RDFUtils.CML_URI_HASH + "hasValue"));
		OWLTypedConstant owlValue = ontologyFactory.getOWLTypedConstant(value);
		OWLDataPropertyAssertionAxiom ax = ontologyFactory.
		    getOWLDataPropertyAssertionAxiom(attributeIndividual, hasValue, owlValue);
		AddAxiom addax = new AddAxiom(cmlOntology, ax);
		ontologyManager.applyChange(addax);
	}

// methods for simple CML schema	
    public Element convert2OWL(Element classXML) {
    	this.classXML = classXML;
    	Element xml = null;
        try {
            createOntologyFromCMLClasses();
            xml = RDFUtils.getRDFOWLAsXML(ontologyManager, cmlOntology);
        }
        catch (OWLException e) {
            e.printStackTrace();
        }
        return xml;
    }

	/**
	 * @throws OWLOntologyChangeException
	 */
	private void createOntologyFromCMLClasses() throws OWLOntologyChangeException {
		addElements();
		addAttributes();
		addChildren();
	}

	/**
	 * @throws OWLOntologyCreationException
	 */
	private void createCMLOntologyManagerAndFactory()
			throws OWLOntologyCreationException {
		ontologyManager = OWLManager.createOWLOntologyManager();
		cmlOntologyURI = URI.create("http://www.xml-cml.org/schema");
		cmlOntology = ontologyManager.createOntology(cmlOntologyURI);
		ontologyFactory = ontologyManager.getOWLDataFactory();
	}

	/**
	 * @param manager
	 * @param cmlOntologyURI
	 * @param cmlOntology
	 * @param factory
	 * @param clsA
	 * @param elements
	 * @throws OWLOntologyChangeException
	 */
	private void addElements() throws OWLOntologyChangeException {
		OWLClass abstractElementClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#element"));
        Nodes elements = classXML.query("./element");
        
		for (int i = 0; i < elements.size(); i++) {
			addElement(abstractElementClass, (Element) elements.get(i));
		}
	}

	/**
	 * @param abstractTlementClass
	 * @param elements
	 * @param i
	 * @throws OWLOntologyChangeException
	 */
	private void addElement(OWLClass abstractElementClass, Element element)
			throws OWLOntologyChangeException {
		String className = element.getAttributeValue("name");
		RDFUtils.createAndAddSubclass(abstractElementClass, RDFUtils.CML_URI_HASH,  className,
				ontologyFactory, cmlOntology, ontologyManager);
	}

	/**
	 * @param manager
	 * @param cmlOntologyURI
	 * @param cmlOntology
	 * @param factory
	 * @param clsA
	 * @param elements
	 * @throws OWLOntologyChangeException
	 */
	private void addAttributes() throws OWLOntologyChangeException {
        Nodes elements = classXML.query("./element");
		for (int i = 0; i < elements.size(); i++) {
			Element element = (Element) elements.get(i);
			Nodes attributes = element.query("./attribute");
			for (int j = 0; j < attributes.size(); j++) {
				Element attribute = (Element) attributes.get(j);
				addAttribute(element, attribute);
			}
		}
	}
	
	private void addAttribute(Element element, Element attribute) {
		String elementName = element.getAttributeValue("name");
		String attributeName = attribute.getAttributeValue("name");
		String hasAttributeName = "has"+CMLUtil.capitalize(attributeName);
		// add attribute as property of element
		OWLClass elementClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#"+elementName));
		OWLClass attributeClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#"+attributeName));

		OWLObjectProperty hasAttributeProperty = ontologyFactory.getOWLObjectProperty(URI.create(cmlOntologyURI + "#"+hasAttributeName));
		OWLDescription hasPartSomeAttribute = ontologyFactory.getOWLObjectSomeRestriction(hasAttributeProperty, attributeClass );

		
		OWLSubClassAxiom axiom = ontologyFactory.getOWLSubClassAxiom(elementClass, hasPartSomeAttribute);
		AddAxiom addAxiom = new AddAxiom(cmlOntology, axiom);
		try {
			ontologyManager.applyChange(addAxiom);
		} catch (OWLOntologyChangeException e) {
			throw new RuntimeException("cannot create axiom", e);
		}
		
	}
    
	/**
	 * @param manager
	 * @param cmlOntologyURI
	 * @param cmlOntology
	 * @param factory
	 * @param clsA
	 * @param elements
	 * @throws OWLOntologyChangeException
	 */
	private void addChildren() throws OWLOntologyChangeException {
        Nodes elements = classXML.query("./element");
        Nodes attributes = classXML.query("./attribute");
		for (int i = 0; i < elements.size(); i++) {
			Element element = (Element) elements.get(i);
			Nodes childs = element.query("./child");
			for (int j = 0; j < childs.size(); j++) {
				Element child = (Element) childs.get(j);
				addChild(element, child);
			}
		}
	}
	
	private void addChild(Element element, Element child) {
		String elementName = element.getAttributeValue("name");
		String childName = child.getAttributeValue("name");
		// add child as property of element
		if (!childName.startsWith("#")) {
			OWLClass elementClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#"+elementName));
			OWLClass childClass = ontologyFactory.getOWLClass(URI.create(cmlOntologyURI + "#"+childName));
			OWLObjectProperty hasPart = ontologyFactory.getOWLObjectProperty(URI.create(cmlOntologyURI + "#"+"hasChild"));
			OWLDescription hasPartSomeChild = ontologyFactory.getOWLObjectSomeRestriction(hasPart, childClass);
			OWLSubClassAxiom axiom = ontologyFactory.getOWLSubClassAxiom(elementClass, hasPartSomeChild);
			AddAxiom addAxiom = new AddAxiom(cmlOntology, axiom);
			try {
				ontologyManager.applyChange(addAxiom);
			} catch (OWLOntologyChangeException e) {
				throw new RuntimeException("cannot create axiom", e);
			}
		}
	}
}
