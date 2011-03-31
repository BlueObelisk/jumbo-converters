package org.xmlcml.cml.converters.marker;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLModule;

/**
 * Template models the <template> element.
 * It must contain a matcherSequence (either all regexes or all templates)
 * It also extends MarkerMatcher so it can be used as a matcher and in
 * matcherSequences
 * 
 * @author pm286
 *
 */
public class Template extends Marker {
	public static final String ID_PREFIX = "t";

	static Logger LOG = Logger.getLogger(Template.class);
	
	boolean tidyEmptyElements;
	TemplateInterpreter templateInterpreter;
	
	public Template(Element element, Template parentTemplate) {
		super(element, parentTemplate);
		makeComponents();
	}
	
	private void makeComponents() {
		counter = new TemplateCounter(this);
		templateInterpreter = new TemplateInterpreter(this);
		childMarkerSequence = createChildMarkerSequence();
		parseExtractor = new TemplateExtractor(this);
	}
	
	private MarkerSequence createChildMarkerSequence() {
		
		Nodes regexNodes = this.xmlElement.query(TopTemplateContainer.REGEX_NAME);
		Nodes templateNodes = this.xmlElement.query(TopTemplateContainer.TEMPLATE_NAME);
		if (regexNodes.size() == 0 && templateNodes.size() == 0) {
			throw new RuntimeException("Children must be given for template");
		}
		if (regexNodes.size() != 0 && templateNodes.size() != 0) {
			throw new RuntimeException("Regex and template children must not both be given for template");
		}
		if (regexNodes.size() != 0) {
			childMarkerSequence = new RegexSequence(this);
		} else {
			childMarkerSequence = createAndProcessSequenceMarkers(TopTemplateContainer.TEMPLATE_NAME);
		}
		return childMarkerSequence;
	}
	
	private MarkerSequence createAndProcessSequenceMarkers(String name) {
		MarkerSequence markerSequence = new TemplateSequence(this);
		markerSequence.createAndProcessSequenceMarkers(TopTemplateContainer.TEMPLATE_NAME);
		return markerSequence;
	}

	public boolean isCompletelyMatched() {
		boolean isCompletelyMatched = counter.isWithinCount();
		if (isCompletelyMatched) {
			isCompletelyMatched = this.childMarkerSequence.areAllMarkersMatched();
		}
		return isCompletelyMatched;
	}
	
	protected void tidyModulesAfterMatching(LegacyStore legacyStore) {
		for (int count = 0; count < counter.getCount(); count++) {
			this.childMarkerSequence.tidyAfterMatching(legacyStore);
		}
	}


	public String toString() {
		return super.toString();
	}

	public void wrapInModule() {
		LOG.debug("wrapInModule NYI "+this.getId()+" moduleStart "+counter.getModuleStart()+" moduleStart "+counter.getModuleEnd());
		CMLUtil.debug(legacyStore.getModule(), "MOD");
	}

	protected void addTemplateInformationToModule(CMLModule module) {
		String dictRef = DictRefAttribute.createValue(this.getDictRefPrefix(), this.getDictRefLocalName());
		module.setDictRef(dictRef);
		module.setRole(TopTemplateContainer.TEMPLATE_MATCHES);
	}
}
