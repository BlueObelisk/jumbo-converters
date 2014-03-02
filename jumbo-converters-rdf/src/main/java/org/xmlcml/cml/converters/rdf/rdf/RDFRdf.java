package org.xmlcml.cml.converters.rdf.rdf;

import static org.xmlcml.cml.converters.rdf.rdf.RDFConstants.RDF_DESCRIPTION;
import static org.xmlcml.cml.converters.rdf.rdf.RDFConstants.RDF_RESOURCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
public class RDFRdf {
	
	/**
	<?xml version="1.0" encoding="UTF-8"?>
	<rdf:RDF
	   xmlns:_3="http://www.polymerinformatics.com/RecipeRepository.owl#"
	   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	>
	  <rdf:Description rdf:about="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd309">
	    <_3:hasName>HCl</_3:hasName>
	    <_3:hasRole>reagent</_3:hasRole>
	    <_3:hasAmount rdf:resource="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd310"/>
	  </rdf:Description>
	  */
	/**
	  <rdf:Description rdf:about="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd311">
	    <_3:hasUnit>hours</_3:hasUnit>
	    <_3:hasValue rdf:datatype="http://www.w3.org/2001/XMLSchema#float">32.0</_3:hasValue>
	  </rdf:Description>	 
	  */
	
	private static final Logger LOG = Logger.getLogger(RDFRdf.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Element rdf;
	// all 
	private Map<RDFDescription, Set<RDFTriple>> triple2TripleMap;
	private Map<String, RDFDescription> aboutMap;
	private List<RDFDescription> descriptionList;
	private List<String> namespaceURIList;
	private List<RDFDescription> topNodeList;
	
	public RDFRdf(Element rdf) {
		this.rdf = rdf;
		getNamespaceURIs();
		triple2TripleMap = new HashMap<RDFDescription, Set<RDFTriple>>();
		aboutMap = new HashMap<String, RDFDescription>();
		makeDescriptionList();
		makeTriplePointers();
		for (RDFDescription description : getTopNodes()) {
			LOG.trace("TOPNODE "+description.getId());
		}
	}
	
	private void getNamespaceURIs() {
		namespaceURIList = new ArrayList<String>();
		for (int i = 0; i < rdf.getNamespaceDeclarationCount(); i++) {
			String namespacePrefix = rdf.getNamespacePrefix(i);
			String namespaceURI = rdf.getNamespaceURI(namespacePrefix);
			if (namespaceURI != rdf.getNamespaceURI()) {
				namespaceURIList.add(namespaceURI);
				LOG.trace("NAMESPACE "+namespaceURI);
			}
		}
	}
	
	public boolean containsNamespaceURI(String ns) {
		return (namespaceURIList.contains(ns));
	}

	public void makeDescriptionList() {
		descriptionList = new ArrayList<RDFDescription>();
		Nodes nodes = rdf.query(RDF_DESCRIPTION, RDFConstants.RDF_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			descriptionList.add(new RDFDescription((Element) nodes.get(i), this));
		}
	}
	
	public void makeTriplePointers() {
		for (RDFDescription description : descriptionList) {
			List<RDFTriple> tripleList = description.getTripleList();
			for (RDFTriple triple : tripleList) {
				String resourceId = triple.getResourceId();
				RDFDescription description1 = aboutMap.get(resourceId);
				if (description1 != null) {
					Set<RDFTriple> tripleSet = triple2TripleMap.get(description1);
					if (tripleSet == null) {
						tripleSet = new HashSet<RDFTriple>();
						triple2TripleMap.put(description1, tripleSet);
					}
					tripleSet.add(triple);
				}
			}
		}
	}
	
	public List<RDFDescription> getTopNodes() {
		if (topNodeList == null) {
			topNodeList = new ArrayList<RDFDescription>();
			for (RDFDescription description : descriptionList) {
				Set<RDFTriple> tripleSet = triple2TripleMap.get(description);
				if (tripleSet == null) {
					topNodeList.add(description);
				} else {
				}
			}
		}
		return topNodeList;
	}
	
	public Map<RDFDescription, Set<RDFTriple>> getReferenceMap() {
		return triple2TripleMap;
	}

	public Map<String, RDFDescription> getAboutMap() {
		return aboutMap;
	}

	public void setAboutMap(Map<String, RDFDescription> aboutMap) {
		this.aboutMap = aboutMap;
	}

	public Element getRdf() {
		return rdf;
	}

	public static String getResourceId(Element element, String attName) {
		String value = element.getAttributeValue(attName, RDFConstants.RDF_NS);
		if (value != null) {
			int idx = value.lastIndexOf(CMLConstants.S_HASH);
			value = value.substring(idx+1);
		}
		return value;
	}

	public static String getAboutNamespace(Element element) {
		String value = element.getAttributeValue(RDFConstants.RDF_ABOUT, RDFConstants.RDF_NS);
		if (value != null) {
			int idx = value.lastIndexOf(CMLConstants.S_HASH);
			value = value.substring(0, idx+1);
		}
		return value;
	}

	public RDFDescription getResource(Element element) {
		String resourceId = RDFRdf.getResourceId(element, RDF_RESOURCE);
		return aboutMap.get(resourceId);
	}

	public List<String> getNamespaceURIList() {
		return namespaceURIList;
	}
	
	public static String translateChars(String s) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < s.length(); j++) {
			int ch = (int)s.charAt(j);
			if (ch < 256) {
				sb.append((char)ch);
			} else {
				sb.append(CMLConstants.S_AMP+CMLConstants.S_HASH+ch+CMLConstants.S_SEMICOLON);
			}
		}
		return sb.toString();
	}

}


