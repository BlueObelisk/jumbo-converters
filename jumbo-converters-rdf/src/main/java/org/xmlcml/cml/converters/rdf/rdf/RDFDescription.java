package org.xmlcml.cml.converters.rdf.rdf;

import static org.xmlcml.cml.converters.rdf.rdf.RDFConstants.RDF_ABOUT;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RDFDescription {
	
	private static final Logger LOG = Logger.getLogger(RDFDescription.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	/**
	  <rdf:Description rdf:about="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd309">
	    <_3:hasName>HCl</_3:hasName>
	    <_3:hasRole>reagent</_3:hasRole>
	    <_3:hasAmount rdf:resource="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd310"/>
	  </rdf:Description>
	  <rdf:Description rdf:about="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd311">
	    <_3:hasUnit>hours</_3:hasUnit>
	    <_3:hasValue rdf:datatype="http://www.w3.org/2001/XMLSchema#float">32.0</_3:hasValue>
	  </rdf:Description>	 
	  */

	private Element description;
	private String about;
	private String id;
	private List<RDFTriple> tripleList;
	private RDFRdf root;
	private String aboutNamespace;
	
	public RDFDescription(Element desc, RDFRdf root) {
		this.description = desc;
		this.root = root;
		tripleList = new ArrayList<RDFTriple>();
		about = description.getAttributeValue(RDF_ABOUT, RDFConstants.RDF_NS);
		id = RDFRdf.getResourceId(description, RDF_ABOUT);
		aboutNamespace = RDFRdf.getAboutNamespace(description);
		root.getAboutMap().put(id, this);
		
		processChildren();
	}

	private void processChildren() {
		Elements elements = description.getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			Element element = (Element) elements.get(i);
			if (root.containsNamespaceURI(element.getNamespaceURI())) {
				RDFTriple triple = new RDFTriple(element, root);
				tripleList.add(triple);
			} else {
				LOG.warn("UNKNOWN CHILD "+element.getQualifiedName());
			}
		}
	}
	
	public Element getDescription() {
		return description;
	}

	public List<RDFTriple> getTripleList() {
		return tripleList;
	}

	public String getAbout() {
		return about;
	}

	public String getId() {
		return id;
	}

	public String getAboutNamespace() {
		return aboutNamespace;
	}
}
