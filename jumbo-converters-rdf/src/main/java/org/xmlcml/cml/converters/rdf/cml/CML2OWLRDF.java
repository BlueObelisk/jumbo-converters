package org.xmlcml.cml.converters.rdf.cml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
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
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;



public class CML2OWLRDF {
	
	private static final Logger LOG = Logger.getLogger(CML2OWLRDF.class);	

	private URI ontologyURI;
	private OWLOntology cmlOntology;
	private OWLOntologyManager ontologyManager;
	private OWLDataFactory ontologyDataFactory;

	private OWLIndividual cmlRdfInstance;
	private Map<String, OWLClass> owlClassMap;
	private Map<String, Integer> idMap;

	private OWLClass cmlElementClass;

	private OWLObjectProperty hasCmlChildProperty;
	private String relativeURINameforOntology;
	
	public CML2OWLRDF(String relativeURINameforOntology){
		this.relativeURINameforOntology = relativeURINameforOntology;
		init();
	}

	/**
	 * 
	 */
	private void init() {
		createOntologyFactoryAndManager(relativeURINameforOntology);
		idMap = new HashMap<String, Integer>();
	}
	
	private void createOntologyFactoryAndManager(String ontologyResourceName) {
		try {
			ontologyURI = CML2OWLRDF.class.getClassLoader().getResource(ontologyResourceName).toURI();
			ontologyManager = OWLManager.createOWLOntologyManager();
			cmlOntology = ontologyManager.loadOntologyFromPhysicalURI(ontologyURI);
			ontologyDataFactory = ontologyManager.getOWLDataFactory();
		} catch (Exception e) {
			throw new RuntimeException("Cannot load ontology: "+ontologyResourceName, e);
		}
		try {
			hasCmlChildProperty = ontologyDataFactory.
				getOWLObjectProperty(new URI(RDFUtils.CML_URI_HASH+"hasCmlChild"));
		} catch (URISyntaxException e) {
			throw new RuntimeException("Bad URI", e);
		}
		indexOntology();
	}
	
	private void indexOntology() {
		cmlElementClass = null;
		owlClassMap = new HashMap<String, OWLClass>();
		for (OWLClass  owlClass : cmlOntology.getReferencedClasses()) {
			for (OWLAnnotation annot : owlClass.getAnnotations(cmlOntology)) {
				if (annot.getAnnotationURI().toString().equals(RDFUtils.RDFS_LABEL)){
					String annotValue = annot.getAnnotationValue().toString();
//					System.out.println("... "+annotValue);
					owlClassMap.put(annotValue, owlClass);
				}
			}
			if (owlClass.toString().contains("element")) {
				cmlElementClass = owlClass;
			}
		}
	}
	
	private void makeIndividual() {
		cmlRdfInstance = ontologyDataFactory.getOWLIndividual(ontologyURI);
	}

	/** main entry point
	 * processes by recursive descent
	 * @param cmlElement top element
	 * @return
	 */
	public Element convertCMLElement(CMLElement cmlElement) {
		makeIndividual();
		processElement(cmlRdfInstance, cmlElement, 1000);
			
		Element rdfXml = null;
		try {
			removeClassesFromOntology();
	        rdfXml = RDFUtils.convertOntologyToXML(ontologyManager, cmlOntology);
		} catch (Exception e) {
			throw new RuntimeException("CML exception ", e);
		}
		return rdfXml;
      }
	
	private void processElement(OWLIndividual individual, CMLElement cmlElement, int count) {
		if(--count <0 ) {
			throw new RuntimeException("uncontrolled recursion stopped at "+ cmlElement);
		}
		if (false) {
		} else if (cmlElement instanceof CMLAtomArray) {
			// currently give up at this stage
		} else if (cmlElement instanceof CMLBondArray) {
			// currently give up at this stage
		} else if (cmlElement instanceof CMLCml) {
			processCml(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLDictionary) {
			processDictionary(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLEntry) {
			processEntry(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLFormula) {
			processFormula(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLIdentifier) {
			processIdentifier(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLLabel) {
			processLabel(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processMetadataList(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processMetadata(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMetadataList) {
			processChildren(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLMolecule) {
			processMolecule(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLName) {
			processName(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLParameter) {
			processParameter(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLParameterList) {
			processParameterList(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLProperty) {
			processProperty(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLPropertyList) {
			processPropertyList(individual, cmlElement, count - 1);
		} else if (cmlElement instanceof CMLScalar) {
			processScalar(individual, cmlElement, count - 1);
		}
	}

	
	private void createAndAddTripleFromId(OWLIndividual individual, CMLElement cmlElement) {
		try {
			String id = cmlElement.getId();
			if (id != null) {
				addOWLDataProperty(individual, "hasId", id);
			}
		} catch (Exception e) {
			
		}
	}

	private void createAndAddTripleFromAttribute(OWLIndividual individual, Attribute attribute) {
		if (attribute != null) {
			String attName = attribute.getLocalName();
			String attValue = attribute.getValue();
//			LOG.debug("......................."+attName+"/"+attValue);
			addOWLDataProperty(individual, "has"+Util.capitalise(attName)+"_att", attValue);
		}
	}
	
	private void processChildren(OWLIndividual individual, CMLElement element, int count) {
		List<CMLElement> childElements = element.getChildCMLElements();
		if (childElements.size() > 0) {
			for (CMLElement child : childElements) {
				processElement(individual, child, count - 1);
			}
		} else {
			String content = element.getValue();
			if (content != null && !content.trim().equals(CMLConstants.S_EMPTY)) {
				DictRefManager dictRefManager = new DictRefManager(element, owlClassMap, cmlOntology);
				OWLClass dictRefClass = dictRefManager.getOWLClass();
				String dataType = CMLConstants.XSD_STRING;
				if (dictRefClass != null) {
					List<CMLAxiom> axioms = dictRefManager.getCMLAxioms();
					String hasValue = dictRefManager.getHasValue();
					if ("float".equals(hasValue)) {
						dataType = CMLConstants.XSD_FLOAT;
					}
				}
				if (CMLConstants.XSD_FLOAT.equals(dataType)) {
					try {
						addOWLDataProperty(individual, RDFUtils.HAS_VALUE, new Float(content).floatValue());
					} catch (Exception e) {
						addOWLDataProperty(individual, RDFUtils.HAS_VALUE, content);
						LOG.warn("Cannot parse as float... "+ e);
					}
				} else {
					addOWLDataProperty(individual, RDFUtils.HAS_VALUE, content);
				}
			}
		}
		
	}

	/**
	 * @param individual
	 * @param content
	 * @throws URISyntaxException
	 * @throws OWLOntologyChangeException
	 */
	private void addOWLDataProperty(OWLIndividual individual, String propertyName, String content) {
		try {
			OWLDataProperty property = ontologyDataFactory.getOWLDataProperty(new URI(RDFUtils.CML_URI_HASH+propertyName));
			OWLAxiom axiom = ontologyDataFactory.getOWLDataPropertyAssertionAxiom(individual, property, content);
			ontologyManager.addAxiom(cmlOntology, axiom);
		} catch (Exception e) {
			throw new RuntimeException("cannot write data property", e);
		}
	}
	
	/**
	 * @param individual
	 * @param content
	 * @throws URISyntaxException
	 * @throws OWLOntologyChangeException
	 */
	@SuppressWarnings("unused")
	private void addOWLDataProperty(OWLIndividual individual, String propertyName, double content) {
		try {
			OWLDataProperty property = ontologyDataFactory.getOWLDataProperty(new URI(RDFUtils.CML_URI_HASH+propertyName));
			OWLAxiom axiom = ontologyDataFactory.getOWLDataPropertyAssertionAxiom(individual, property, content);
			ontologyManager.addAxiom(cmlOntology, axiom);
		} catch (Exception e) {
			throw new RuntimeException("cannot write data property", e);
		}
	}
	
	/**
	 * @param individual
	 * @param content
	 * @throws URISyntaxException
	 * @throws OWLOntologyChangeException
	 */
	private void addOWLDataProperty(OWLIndividual individual, String propertyName, float content) {
		try {
			OWLDataProperty property = ontologyDataFactory.getOWLDataProperty(new URI(RDFUtils.CML_URI_HASH+propertyName));
			OWLAxiom axiom = ontologyDataFactory.getOWLDataPropertyAssertionAxiom(individual, property, content);
			ontologyManager.addAxiom(cmlOntology, axiom);
		} catch (Exception e) {
			throw new RuntimeException("cannot write data property", e);
		}
	}
	
	/**
	 * @param cmlElement
	 */
	private void processCml(OWLIndividual individual, CMLElement cmlElement, int count) {
		OWLIndividual newIndividual = createIndividualAndAddToSubject(individual, cmlElement);
		addIDAttributesAndChildren(newIndividual, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processDictionary(OWLIndividual individual, CMLElement cmlElement, int count) {
		individual= createIndividualAndAddToSubject(individual, cmlElement);
		addIDAttributesAndChildren(individual, cmlElement, count - 1);
		createAndAddTripleFromAttribute(individual, cmlElement.getAttribute("namespace"));
	}

	/**
	 * @param individual
	 * @param cmlElement
	 * @return null if id = null
	 */
	private OWLIndividual createIndividualAndAddToSubject(OWLIndividual subject, CMLElement cmlElement) {
		String id = RDFUtils.removeNonURLCharacters(cmlElement.getId());
		String uri = RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI, id);
		// no id, make one
		if (id == null) {
			uri = createUniqueURI(cmlElement);
		}
		// make class from CML name
		OWLIndividual object = null;
		try {
			OWLClass cmlClass = ontologyDataFactory.getOWLClass(new URI(RDFUtils.CML_URI_HASH+Util.capitalise(cmlElement.getLocalName())));
			OWLAxiom subClassAxiom = ontologyDataFactory.getOWLSubClassAxiom(cmlClass, cmlElementClass);
			ontologyManager.addAxiom(cmlOntology, subClassAxiom);
			// individual is instance of cmlElement
			object = ontologyDataFactory.getOWLIndividual(new URI(RDFUtils.removeNonQNameCharacters(uri)));
			OWLAxiom axiom = ontologyDataFactory.getOWLClassAssertionAxiom(object, cmlClass);
			ontologyManager.addAxiom(cmlOntology, axiom);
			// individual is cmlChild of subject
			
			RDFUtils.makeObjectPropertyAssert(subject, hasCmlChildProperty, object, 
					ontologyDataFactory, cmlOntology, ontologyManager);
		} catch (Exception e) {
			throw new RuntimeException("URI/OWL problem", e);
		}
		
		return object;
	}

	String createUniqueURI(CMLElement cmlElement) {
		String uriString = null;
		String id = cmlElement.getId();
		if (id == null) {
			id = cmlElement.getAttributeValue("dictRef");
		}
		if (id == null) {
			id = cmlElement.getLocalName();
		}
		uriString = id+"_"+RDFUtils.createUUID();
		return uriString;
	}
	/**
	 * @param cmlElement
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getCMLType(CMLElement cmlElement) {
		String propertyName = RDFUtils.CML_URI_HASH+cmlElement.getLocalName();
		return propertyName;
	}
	
	/**
	 * @param cmlElement
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getCMLProperty(CMLElement cmlElement) {
		String propertyName = RDFUtils.CML_URI_HASH+"has"+Util.capitalise(cmlElement.getLocalName());
		return propertyName;
	}
	
	/**
	 * @param cmlElement
	 */
	private void processEntry(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * @param cmlElement
	 */
	private void processFormula(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * @param cmlElement
	 */
	private void processIdentifier(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * @param cmlElement
	 */
	private void processLabel(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * skip through to children recursively
	 * @param cmlElement
	 */
	private void processMetadataList(OWLIndividual individual, CMLElement cmlElement, int count) {
		processChildren(individual, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processMetadata(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}
	
	/**
	 * @param cmlElement
	 */
	private void processMolecule(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}
	/**
	 * @param cmlElement
	 */
	private void processName(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * @param cmlElement
	 */
	private void processParameterList(OWLIndividual individual, CMLElement cmlElement, int count) {
		processChildren(individual, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processParameter(OWLIndividual individual, CMLElement cmlElement, int count) {
		CMLParameter parameter = (CMLParameter) cmlElement;
		Nodes scalarNodes = cmlElement.query("./cml:scalar", CMLConstants.CML_XPATH);
		if (scalarNodes.size() == 1) {
			CMLScalar scalar = new CMLScalar((CMLScalar) scalarNodes.get(0));
			scalar.setDictRef(parameter.getDictRef());
			processScalar(individual, scalar, count);
		} else {
			processGenericCMLElement(individual, cmlElement, count);
		}
	}

	/**
	 * @param cmlElement
	 */
	private void processPropertyList(OWLIndividual individual, CMLElement cmlElement, int count) {
		processChildren(individual, cmlElement, count - 1);
	}

	/**
	 * @param cmlElement
	 */
	private void processProperty(OWLIndividual individual, CMLElement cmlElement, int count) {
		CMLProperty property = (CMLProperty) cmlElement;
		Nodes scalarNodes = cmlElement.query("./cml:scalar", CMLConstants.CML_XPATH);
		if (scalarNodes.size() == 1) {
			CMLScalar scalar = new CMLScalar((CMLScalar) scalarNodes.get(0));
			scalar.setDictRef(property.getDictRef());
			processScalar(individual, scalar, count);
		} else {
			processGenericCMLElement(individual, cmlElement, count);
		}
	}

	/**
	 * @param cmlElement
	 */
	private void processScalar(OWLIndividual individual, CMLElement cmlElement, int count) {
		processGenericCMLElement(individual, cmlElement, count);
	}

	/**
	 * @param cmlElement
	 */
	private void addIDAttributesAndChildren(OWLIndividual individual, CMLElement cmlElement, int count) {
		createAndAddTripleFromId(individual, cmlElement);
		try {
			addAttributesAndChildren(individual, cmlElement, count - 1);
		} catch (Exception e) {
			throw new RuntimeException("cannot add atribute or child", e);
		}
	}
	
	/**
	 * @param individual
	 * @param cmlElement
	 * @param count
	 */
	private void processGenericCMLElement(OWLIndividual individual,
			CMLElement cmlElement, int count) {
		OWLIndividual newIndividual = createIndividualAndAddToSubject(individual, cmlElement);
		addIDAttributesAndChildren(newIndividual, cmlElement, count - 1);
	}
	
//	/** add triple where predicate is a restriction
//	 * 
//	 * @param owlSubjectClass
//	 * @param predicateProperty
//	 * @param owlObjectClass
//	 * @throws OWLOntologyChangeException
//	 */
//	private void addDataRestriction(OWLIndividual owlSubjectClass, OWLDataProperty predicateProperty, OWLIndividual owlObjectClass ) throws OWLOntologyChangeException {
//		OWLObjectSomeRestriction hasObjectRestriction = ontologyDataFactory.getOWLDataSomeRestriction(predicateProperty, owlObjectClass);
//		OWLSubClassAxiom hasObjectAxiom = ontologyDataFactory.getOWLSubClassAxiom(owlSubjectClass, hasObjectRestriction);
//		ontologyManager.applyChange(new AddAxiom(cmlOntology, hasObjectAxiom));
//	}

	/**
	 * @param cmlElement
	 */
	private void addAttributesAndChildren(OWLIndividual individual, CMLElement cmlElement, int count) throws Exception {
		processAnyDictRefAttribute(individual, cmlElement);
		// process other attributes, omitting special ones
		for (int i = 0; i < cmlElement.getAttributeCount(); i++) {
			Attribute attribute = cmlElement.getAttribute(i);
			String attName = attribute.getLocalName();
			if (attName.equals("id")) {
			} else if (attName.equals("dataType")) {
				// provided by the ontology?
//			} else if (attName.equals("dictRef")) {
				// provided by the ontology
			} else {
				createAndAddTripleFromAttribute(individual, attribute);
			}
		}
		processChildren(individual, cmlElement, count - 1);
	}

	/**
	 * @param individual
	 * @param cmlElement
	 * @throws URISyntaxException
	 * @throws OWLOntologyChangeException
	 */
	private void processAnyDictRefAttribute(OWLIndividual individual,
			CMLElement cmlElement) throws URISyntaxException,
			OWLOntologyChangeException {
		DictRefManager dictRefManager = new DictRefManager(cmlElement, owlClassMap, cmlOntology);
		OWLClass dictRefClass = dictRefManager.getOWLClass();
		if (dictRefClass != null) {
			OWLClassAssertionAxiom dictRefAxiom = ontologyDataFactory.getOWLClassAssertionAxiom(
					individual, dictRefClass);
			ontologyManager.addAxiom(cmlOntology, dictRefAxiom);
			List<CMLAxiom> axioms = dictRefManager.getCMLAxioms();
//			String dataType = dictRefManager.getHasValue();
			
			CMLAxiom hasUnitsAxiom = dictRefManager.getCMLAxiom("hasUnits");
			if (hasUnitsAxiom != null) {
				OWLDataProperty unitsProperty = ontologyDataFactory.getOWLDataProperty(
						new URI(RDFUtils.CML_URI_HASH+"hasUnits"));
				OWLAxiom axiom = ontologyDataFactory.getOWLDataPropertyAssertionAxiom(
						individual, unitsProperty, hasUnitsAxiom.object.toString());
				ontologyManager.addAxiom(cmlOntology, axiom);
			}
		}
	}
	
	private void removeClassesFromOntology() {
	    // Create the entity remover - in this case we just want to remove the individuals from
		   OWLEntityRemover remover = new OWLEntityRemover(ontologyManager, Collections.singleton(cmlOntology));
		   Set<OWLClass> objectRestrictionSet = getClassesInObjectRestrictions();
		   for(OWLClass owlClass : cmlOntology.getReferencedClasses()) {
			   Set<OWLIndividual> individuals = owlClass.getIndividuals(cmlOntology);
			   Set<OWLDescription> subClasses = owlClass.getSubClasses(cmlOntology);
				@SuppressWarnings("unused")
			   Set<OWLDescription> superClasses = owlClass.getSuperClasses(cmlOntology);
			   if (individuals.size() == 0 &&
					   subClasses.size() == 0 &&
					   !objectRestrictionSet.contains(owlClass)) {
//				   LOG.debug("removed "+owlClass+ " super "+superClasses.size());
				   if (false) {
				   } else {
					   owlClass.accept(remover);
				   }
			   } else {
//				   LOG.debug("accepted "+owlClass);
			   }
		   }
		   try {
			ontologyManager.applyChanges(remover.getChanges());
		} catch (OWLOntologyChangeException e) {
			throw new RuntimeException("cannot remove ", e);
		}
	}

	/**
	 * 
	 */
	private Set<OWLClass> getClassesInObjectRestrictions() {
		Set<OWLClass> classesInRestrictions = new HashSet<OWLClass>();
	   for(OWLClass owlClass : cmlOntology.getReferencedClasses()) {
		   Set<OWLDescription> superClasses = owlClass.getSuperClasses(cmlOntology);
		   for (OWLDescription superClass : superClasses) {
			   if (superClass instanceof OWLObjectSomeRestriction) {
				   OWLObjectSomeRestriction oosr = (OWLObjectSomeRestriction) superClass;
				   OWLDescription desc = oosr.getFiller();
				   classesInRestrictions.add((OWLClass)desc);
			   }
		   }
	   }
	   return classesInRestrictions;
	}

//	private OWLIndividual makeAndAddInstance(OWLClass owlClass, String id) {
//		OWLIndividual individual = null;
//		try {
//			individual = ontologyDataFactory.getOWLIndividual(new URI(RDFUtils.cleanAndMakeURI(RDFUtils.CML_URI, id)));
//			OWLClassAssertionAxiom ax = ontologyDataFactory.getOWLClassAssertionAxiom(individual, owlClass);
//			ontologyManager.addAxiom(cmlOntology, ax);
//		} catch (Exception e) {
//			throw new RuntimeException("failed individual", e);
//		}
//		return individual;
//	}
}

class DictRefManager {
	
	private static Logger LOG = Logger.getLogger(DictRefManager.class);
	private OWLClass owlClass = null;
	private OWLOntology cmlOntology = null;
	private List<CMLAxiom> axiomList;
	
	public DictRefManager(CMLElement cmlElement, Map<String, OWLClass> owlClassMap, OWLOntology cmlOntology) {
		this.cmlOntology = cmlOntology;
		Attribute attribute = cmlElement.getAttribute("dictRef");
		if (attribute != null) {
			String entryName = attribute.getValue();
//			String prefix = DictRefAttribute.getPrefix(entryName);
			String entryId = DictRefAttribute.getLocalName(entryName);
			entryId = RDFUtils.removeNonQNameCharacters(entryId);
			owlClass = owlClassMap.get(entryId);
		}
	}
	
	public OWLClass getOWLClass() {
		return owlClass;
	}

	/**
	 * process the axioms so we can link to ontology.
	 * This is NOT RIGHT I am sure
	 * @param owlClass
	 */
	
	public List<CMLAxiom> getCMLAxioms() {
		Set<OWLClassAxiom> axioms = cmlOntology.getAxioms(owlClass);
		axiomList = new ArrayList<CMLAxiom>();
		for (OWLClassAxiom axiom : axioms) {
			OWLDescription superClassDescription = ((OWLSubClassAxiom)axiom).getSuperClass();
			OWLDescription subClassDescription = ((OWLSubClassAxiom)axiom).getSubClass();
			CMLAxiom cmlAxiom = null;
			if (superClassDescription instanceof OWLDataSomeRestriction) {
				OWLDataSomeRestriction odsr = (OWLDataSomeRestriction) superClassDescription;
				OWLDataPropertyExpression predicate = odsr.getProperty();
				OWLDataRange object  = odsr.getFiller();
				cmlAxiom = new CMLAxiom(subClassDescription, predicate, object);
			} else if (superClassDescription instanceof OWLObjectSomeRestriction) {
				OWLObjectSomeRestriction oosr = (OWLObjectSomeRestriction) superClassDescription;
				OWLObjectPropertyExpression predicate = oosr.getProperty();
				OWLDescription object  = oosr.getFiller();
				cmlAxiom = new CMLAxiom(subClassDescription, predicate, object);
			} else if (superClassDescription instanceof OWLClass) {
				cmlAxiom = new CMLAxiom(subClassDescription, "isSubClassOf", superClassDescription);
			} else {
				throw new RuntimeException("Cannot understand axiom: "+axiom);
			}
			axiomList.add(cmlAxiom);
		}
		return axiomList;
	}
	
	CMLAxiom getCMLAxiom(String predicateS) {
		CMLAxiom axiom = null;
		if (axiomList != null) {
			for (CMLAxiom ax : axiomList) {
				if (ax.hasPredicate(predicateS)) {
					axiom = ax;
					break;
				}
			}
		}
		return axiom;
	}
	
	String getHasValue() {
		CMLAxiom hasValueAxiom = getCMLAxiom(RDFUtils.HAS_VALUE);
		String value = null;
		if (hasValueAxiom != null) {
			String[] ss = hasValueAxiom.toString().trim().split(" ");
			value = ss[ss.length - 1];
		}
		return value;
	}
	
	OWLClass getUnits() {
		CMLAxiom hasUnits = getCMLAxiom("hasUnits");
		return (hasUnits == null) ? null : (OWLClass) hasUnits;
	}
}

class CMLAxiom {
	Object subject;
	Object predicate;
	Object object;
	public CMLAxiom(Object subject, Object predicate, Object object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	boolean hasPredicate(String predicateS) {
		return predicate.toString().equals(predicateS);
	}
	
	public String toString() {
		return subject + " .. " + predicate + " .. " + object;
	}
}
