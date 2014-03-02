package org.xmlcml.cml.converters.reaction.properties;


import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.rdf.rdf.RDFDescription;
import org.xmlcml.cml.converters.rdf.rdf.RDFRdf;
import org.xmlcml.cml.converters.rdf.rdf.RDFTriple;
import org.xmlcml.cml.converters.reaction.properties.MMReConstants.Predicate;
import org.xmlcml.cml.converters.reaction.properties.MMReConstants.Unit;
import org.xmlcml.cml.element.CMLAction;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSubstance;

public class CMLRDFObject {
	private static Logger LOG = Logger.getLogger(CMLRDFObject.class);
	
	private RDFRdf rdf;
	
	public CMLRDFObject(RDFRdf rdf) {
		this.rdf = rdf;
	}
	
/**
		HAS_MASS("hasGram"),
		HAS_VOL("hasVol"),
		HAS_MOLAR_AMOUNT("hasMol"),
		HAS_PERCENT("hasPercent"),
		
		HAS_AMOUNT("hasAmount"),
		HAS_COLOR("hasColor"),
		HAS_CHROMATOGRAPHY("hasChromatography"),
		IS_EXTRACTED_BY("isExtractedBy"),
		HAS_FILTER_PHRASE("hasFilter-Phrase"),
		IS_FILTERED_BY("isFilteredBy"),
		HAS_NAME("hasName"),
		HAS_NUMBER("hasNumber"),
		HAS_PREPARATION("hasPreparation"),
		HAS_PRODUCT("hasProduct"),
		IS_CONCENTRATED_BY("isConcentrateedBy"),
		IS_PURIFIED_BY("isPurifiedBy"),
		IS_WASHED_BY("isWashedBy"),
		HAS_REACTANT("hasReactant"),
		HAS_ROLE("hasRole"),
		HAS_STATE("hasState"),
		HAS_SUBSTANCE("hasSubstance"),
		HAS_PRESSURE("hasPressure"),
		HAS_TEMP("hasTemp"),
		HAS_TIME("hasTime"),
		HAS_UNIT("hasUnit"),
		HAS_VALUE("hasValue"),
 */	
	
	public void expandDescription(RDFDescription description, CMLElement element) {
		if (element == null) {
			throw new RuntimeException("NULL");
		}
		element.setId(description.getId());
		for (RDFTriple triple : description.getTripleList()) {
			expandTriple(triple, element);
		}
	}

	/**
	 * triple can either have resource pointer or content
	 * @param element
	 * @param triple
	 */
	public void expandTriple(RDFTriple triple, CMLElement element) {
		RDFDescription resourceDescription = triple.getResourceDescription(rdf);
		CMLElement newElement = createCMLElement(triple, element);
		if (newElement == null) {
			LOG.trace("null element "+triple.getLocalName());
		} else {
			element.appendChild(newElement);
			String id = triple.getResourceId();
			if (id != null) {
				newElement.addAttribute(new Attribute("ref", id));
			}
			String content = triple.getContent();
			if (resourceDescription != null) {
				expandDescription(resourceDescription, newElement);
			} else if (content != null) {
				// leaf node
				newElement.appendChild(content);
				String dataType = triple.getDatatype();
				if (dataType != null) {
					newElement.addAttribute(new Attribute("dataType", dataType));
				}
				removeTemporaryRefAttribute(newElement);
			} else if (id != null) {
				// leave an empty pointer
			} else {
				// leave empty element
				LOG.warn("No resource pointer/Id or content "+triple);
			}
			// remove ref from parent
		}
		removeTemporaryRefAttribute(element);
	}

	private void removeTemporaryRefAttribute(CMLElement element) {
		String ref = element.getAttributeValue("ref");
		if (ref != null) {
			element.setId(ref);
			element.removeAttribute("ref");
		}
	}
	
	public CMLElement createCMLElement(RDFTriple triple, CMLElement element) {
		CMLElement newElement = null;
		String localName = triple.getLocalName();
		String content = triple.getContent();
		Predicate predicate = Predicate.getPredicate(localName);
		if (predicate == null) {
			throw new RuntimeException("Unknown predicate: "+localName);

		} else if (
			predicate.equals(Predicate.HAS_MASS) ||
			predicate.equals(Predicate.HAS_VOL) ||
			predicate.equals(Predicate.HAS_MOLAR_AMOUNT) ||
			predicate.equals(Predicate.HAS_PERCENT)) {
			newElement = new CMLScalar();
			if (predicate.dictRef != null) {
				newElement.addAttribute(new Attribute("dictRef", predicate.dictRef));
			}
			newElement.addAttribute(new Attribute("dataType", CMLConstants.XSD_DOUBLE));
			
		} else if (predicate.equals(Predicate.HAS_AMOUNT)) {
			newElement = new CMLAmount();
			
		} else if (
			predicate.equals(Predicate.HAS_COLOR) ||
			predicate.equals(Predicate.HAS_STATE)) {
			newElement = new CMLProperty();
			if (predicate.dictRef != null) {
				newElement.addAttribute(new Attribute("dictRef", predicate.dictRef));
			}
		} else if (
				predicate.equals(Predicate.IS_CONCENTRATED_BY) ||
				predicate.equals(Predicate.IS_PURIFIED_BY) ||
				predicate.equals(Predicate.IS_WASHED_BY) ||
				predicate.equals(Predicate.HAS_CHROMATOGRAPHY) ||
				predicate.equals(Predicate.IS_EXTRACTED_BY) ||
				predicate.equals(Predicate.HAS_FILTER_PHRASE) ||
				predicate.equals(Predicate.IS_FILTERED_BY)) {
			newElement = new CMLAction();
			if (predicate.dictRef != null) {
				newElement.addAttribute(new Attribute("role", predicate.dictRef));
			}
		} else if (predicate.equals(Predicate.HAS_NAME)) {
			newElement = new CMLName();
		} else if (predicate.equals(Predicate.HAS_NUMBER)) {
			newElement = new CMLLabel();
			if (predicate.dictRef != null) {
				newElement.addAttribute(new Attribute("role", predicate.dictRef));
			}
		} else if (predicate.equals(Predicate.HAS_PREPARATION)) {
			newElement = new CMLReaction();
		} else if (predicate.equals(Predicate.HAS_PRODUCT)) {
			newElement = new CMLProduct();
		} else if (predicate.equals(Predicate.HAS_REACTANT)) {
			newElement = new CMLReactant();
		} else if (predicate.equals(Predicate.HAS_ROLE)) {
			element.addAttribute(new Attribute("role", content));
		} else if (predicate.equals(Predicate.HAS_SUBSTANCE)) {
			newElement = new CMLSubstance();
		} else if (
				predicate.equals(Predicate.HAS_PRESSURE) ||
				predicate.equals(Predicate.HAS_TIME) ||
				predicate.equals(Predicate.HAS_TEMP)
				) {
			newElement = new CMLParameter();
			if (predicate.dictRef != null) {
				newElement.addAttribute(new Attribute("dictRef", predicate.dictRef));
			}
			newElement.addAttribute(new Attribute("dataType", CMLConstants.XSD_DOUBLE));

		} else if (predicate.equals(Predicate.HAS_UNIT)) {
			content = RDFRdf.translateChars(content);
			Unit unit = MMReConstants.UNIT_MAP.get(content);
			if (unit == null) {
				throw new RuntimeException("unknown unit: "+content);
			}
			element.addAttribute(new Attribute(MMReConstants.UNIT_ATTNAME, unit.value));

		} else if (predicate.equals(Predicate.HAS_VALUE)) {
			element.addAttribute(new Attribute("value", content));
		} else {
			throw new RuntimeException("unknown predicate "+predicate);
		}
		return newElement;
	}
}
