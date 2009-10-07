package org.xmlcml.cml.converters.marker;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.element.CMLModule;


/**
 * The marker contains:
 * - the XML element which gives this template its info and children
 * - a counter which records the number of matches and whether this is within
 *   the limits in min/max in THIS marker
 * - a list (markerSequence) of child markers as specifed in the template.
 *   be careful not to confuse the serial numbers of the children with the match
 *   counts on this marker.
 *   
 * @author pm286
 *
 */
public abstract class Marker {
	private static Logger LOG = Logger.getLogger(Marker.class);

	/**
	 * The sequence of children. Mandatory for templates, forbidden for regex
	 */
	protected MarkerSequence childMarkerSequence;
	/**
	 * The XML delegate
	 */
	protected Element xmlElement;
	/**
	 * A count of how many times THIS object has been matched
	 */
	protected MarkerCounter counter;
	/**
	 * holds the XML attributes of this Marker
	 */
	protected LegacyStore legacyStore;
	protected String dictRefLocalName;
	protected String dictRefPrefix;
	protected Marker parentMarker;
	protected TopTemplateContainer topTemplateContainer;
	protected ParseExtractor parseExtractor;

	public static final String NO = "no";
	public static final String YES = "yes";
	public static final String NAMES = "names";
	public static final String SKIP = "skip";
	public static final String STAR = "*";
	public static final String TRANSLATE = "translate";

	protected Marker(Element xmlElement, Marker parentMarker) {
		this.xmlElement = xmlElement;
		this.parentMarker = parentMarker;
		interpretAttributes();
	}
	
	private void interpretAttributes() {
		interpretDictRefAttribute();
	}

	private void interpretDictRefAttribute() {
		dictRefPrefix = getDictRefPrefix();
		dictRefLocalName = getDictRefLocalName();
	}

	public Marker getParentMarker() {
		return parentMarker;
	}
	
	public Template getTopTemplate() {
		Marker currentMarker = this;
		while (currentMarker != null) {
			Marker parentTemplate = currentMarker.parentMarker;
			if (parentTemplate == null) {
				break;
			}
			currentMarker = parentTemplate;
		}
		return (Template) currentMarker;
	}

	public Element getXMLElement() {
		return xmlElement;
	}

	public MarkerSequence getMarkerSequence() {
		return childMarkerSequence;
	}

	public MarkerCounter getMarkerCounter() {
		return counter;
	}

	public boolean isSkip() {
		return counter.isSkip();
	}

	public String getAttributeValue(String attName) {
		return xmlElement.getAttributeValue(attName);
	}

	public void addAttribute(Attribute attribute) {
		xmlElement.addAttribute(attribute);
	}

	public String getDictRefLocalName() {
		return DictRefAttribute.getLocalName(xmlElement.getAttributeValue(TopTemplateContainer.DICT_REF_ATT));
	}

	public String getDictRefPrefix() {
		return DictRefAttribute.getPrefix(xmlElement.getAttributeValue(TopTemplateContainer.DICT_REF_ATT));
	}

	public LegacyStore getLegacyStore() {
		return legacyStore;
	}
	
	protected int getLevel() {
		Node parent = xmlElement;
		while (true) {
			Node parent1 = parent.getParent();
			if (parent1 == null) {
				break;
			}
			parent = parent1;
		}
		Nodes ancestors = xmlElement.query("ancestor::*");
		return ancestors.size();
	}
	
	protected boolean match(LegacyStore legacyStore) {
		LOG.debug("MARKER match "+this.getId());
		this.legacyStore = legacyStore;
		counter.setMatched(false);
		LOG.debug("legacyPointer "+legacyStore.getPointer());
		counter.setModuleStart(legacyStore.getPointer());
		counter.setModuleEnd(legacyStore.getPointer());
		boolean childrenMatched = false;
		while (counter.canIncrementCount() && legacyStore.hasCurrentLegacyElement()) {
			// a marker is only matched if all its children are matched
			childrenMatched = this.matchChildren(legacyStore);
			if (!childrenMatched) {
				break;
			}
			counter.setModuleEnd(legacyStore.getPointer());
			if (!counter.canIncrementCount()) {
				counter.forceIncrementCount();
				LOG.debug("ran out of marker count");
				break;
			}
			// get next marker
			counter.forceIncrementCount();
		}
		if (childrenMatched) {
			counter.setMatched(true);
			LOG.debug("MARKER ("+getName()+") tidied after matching: legacyStore = "+this.getLegacyStore());
			LOG.debug("MarkerList "+counter.getModuleStart()+"..."+counter.getModuleEnd());
			tidyModulesAfterMatching(legacyStore);
		}
		return counter.isMatched();
	}

	protected abstract void tidyModulesAfterMatching(LegacyStore legacyStore);

	private boolean matchChildren(LegacyStore legacyStore) {
		LOG.debug("Matching children of: "+this.getId());
		boolean matched = false;
		for (Marker childMarker : childMarkerSequence.markerList) {
			matched = childMarker.match(legacyStore);
			if (!matched) {
				break;
			}
		}
		return matched;
	}

	protected String getIndentForFormatting() {
		int l = getLevel();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < l; i++) {
			sb.append("    ....");
		}
		return sb.toString();
	}
	
	public String getName() {
		return this.xmlElement.getLocalName().toUpperCase();
	}
	
	public String getId() {
		return xmlElement.getAttributeValue("id");
	}
	
	public String toString() {
		String sp = getIndentForFormatting();
		String s = sp+this.xmlElement.getLocalName().toUpperCase()+" ("+this.hashCode()+") "+this.getId()+"   ";
		s += sp+"Legacy: "+legacyStore+"  ";
		s += "dictRef:"+getDictRefLocalName()+"   ";
		s += "dictPref: "+getDictRefPrefix()+"\n";
		if (childMarkerSequence != null) {
			s += sp+childMarkerSequence+"\n";
		}
		s += sp+"CMC "+counter+"\n";
		return s;

	}

	protected void addIdsAndTLTRecursivelyAndCreateIndex(String id, TopTemplateContainer topTemplateContainer) {
		this.xmlElement.addAttribute(new Attribute("id", id));
		this.topTemplateContainer = topTemplateContainer;
		int i = 0;
		MarkerSequence markerSequence = this.getMarkerSequence();
		if (markerSequence != null) {
			List<Marker> markerList = markerSequence.markerList;
			if (markerList != null) {
				for (Marker child : markerList) {
					String idd = child.createId(id, ++i);
					child.addIdsAndTLTRecursivelyAndCreateIndex(idd, topTemplateContainer);
				}
			}
		}
	}

	protected String createId(String id, int i) {
		return id+"."+(i);
	}
	
	protected void addTemplateInformationToModule(CMLModule module) {
	}

	public String createDictRef() {
		String dictRef = DictRefAttribute.createValue(dictRefPrefix, dictRefLocalName);
		return dictRef;
	}

	public ParseExtractor getParseExtractor() {
		return parseExtractor;
	}
	
}
