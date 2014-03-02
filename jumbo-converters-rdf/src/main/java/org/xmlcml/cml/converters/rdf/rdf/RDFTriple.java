package org.xmlcml.cml.converters.rdf.rdf;

import static org.xmlcml.cml.converters.rdf.rdf.RDFConstants.RDF_DATATYPE;
import static org.xmlcml.cml.converters.rdf.rdf.RDFConstants.RDF_RESOURCE;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

/**
 * Triples can have the following structures:
 * 
 * <p:foo>value</p:foo>
 * <foo rdf:datatype="uri">value</foo>
 * <foo rdf:resource="uri#id"/>
 * 
 * Currently rdf:lang is unsupported
 * 
 * No reliance is put on the form of the namespace prefix
 * 
 * The triple is interpreted and has either a resourceId or a content
 * These and the localName and namespace can be retrieved
 * 
 * A root RDFRdf element is required to resolve the triple pointed to by resourceId
 * 
 * @author pm286
 *
 */
public class RDFTriple {
	
	/**
	    <_3:hasName>HCl</_3:hasName>
	    <_3:hasRole>reagent</_3:hasRole>
	    <_3:hasAmount rdf:resource="http://www.polymerinformatics.com/RecipeRepository.owl#rrpPeTyd310"/>
	    <_3:hasUnit>hours</_3:hasUnit>
	    <_3:hasValue rdf:datatype="http://www.w3.org/2001/XMLSchema#float">32.0</_3:hasValue>
	 */
	private static final Logger LOG = Logger.getLogger(RDFTriple.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private Element rdfxmlElement;
	private String datatype;
	private String resourceId;
	private String localName;
	private String content;
	@SuppressWarnings("unused")
	private RDFRdf root;
	private String namespaceURI;
	
	public RDFTriple(Element element, RDFRdf root) {
		this.rdfxmlElement = element;
		this.root = root;
		processAttributes();
		processChildren();
		processName();
	}

	/** convenience method to extract content.
	 * 
	 * @param element
	 * @return
	 */
	public static String extractContent(Element element) {
		String s = null;
		if (element.getChildCount() == 1) {
			Node node = element.getChild(0);
			if (node instanceof Text) {
				s = node.getValue();
			}
		}
		return s;
	}
	
	private void processChildren() {
		if (rdfxmlElement.getChildCount() > 0) {
			Node child = rdfxmlElement.getChild(0);
			if (child instanceof Text) {
				content = child.getValue();
				LOG.debug("..."+content);
			} else {
				LOG.warn("UNKNOWN CHILD "+child);
			}
		}
	}

	private void processAttributes() {
		for (int i = 0; i < rdfxmlElement.getAttributeCount(); i++) {
			Attribute attribute = rdfxmlElement.getAttribute(i);
			String localName = attribute.getLocalName();
			if (localName.equals(RDF_DATATYPE)) {
				datatype = rdfxmlElement.getAttributeValue(RDF_DATATYPE, RDFConstants.RDF_NS);
				if (datatype.startsWith(RDFConstants.RDFXSD_URI)) {
					datatype = datatype.substring(RDFConstants.RDFXSD_URI.length());
					LOG.debug("DATATYPE "+datatype);
				}
			} else if (localName.equals(RDF_RESOURCE)) {
				resourceId = RDFRdf.getResourceId(rdfxmlElement, RDFConstants.RDF_RESOURCE);
//				Set<RDFTriple> references = root.getReferenceMap().get(resourceId);
//				if (references == null) {
//					references = new HashSet<RDFTriple>();
//					root.getReferenceMap().put(resourceId, references);
//				}
//				references.add(this);
			} else {
				LOG.warn("UNKNOWN ATTRIBUTE "+attribute.getQualifiedName());
			}
		}
	}
	
	private void processName() {
		localName = rdfxmlElement.getLocalName();
		namespaceURI = rdfxmlElement.getNamespaceURI();
	}

	public String getDatatype() {
		return datatype;
	}

	public String getResourceId() {
		return resourceId;
	}

	public RDFDescription getResourceDescription(RDFRdf rdf) {
		return rdf.getAboutMap().get(resourceId);
	}

	public String getLocalName() {
		return localName;
	}

	public String getContent() {
		return content;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}
	
	public String toString() {
		String s = "";
		s += "datatype="+datatype +CMLConstants.S_NEWLINE;
		s += "resourceId="+resourceId +CMLConstants.S_NEWLINE;
		s += "localName="+localName +CMLConstants.S_NEWLINE;
		s += "content="+content +CMLConstants.S_NEWLINE;
		s += "content="+content +CMLConstants.S_NEWLINE;
		s += "namespaceURI="+namespaceURI +CMLConstants.S_NEWLINE;
		return s;
	}
}
