package org.xmlcml.cml.converters.marker;



import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * a regex sequence manages a list of regexes including their creation and
 * current pointers.
 * @author pm286
 *
 */
public class RegexSequence extends MarkerSequence {
	static Logger LOG = Logger.getLogger(RegexSequence.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	String prefix;

	public RegexSequence(Template parentTemplate) {
		super(parentTemplate);
		createAndProcessSequenceMarkers(TopTemplateContainer.REGEX_NAME);
	}

	public String getPrefix() {
		return prefix;
	}

	protected Marker createNewMarker(Element element) {
		Regex regex = new Regex(element, parentTemplate);
		regex.setTemplateDictRefLocalName(parentTemplate.getDictRefLocalName());
		regex.setTemplateDictRefPrefix(parentTemplate.getDictRefPrefix());
		return regex;
	}
	
	protected void tidyAfterCreatingSequence() {
		createRegexStringsForSkipRegexes();
		if (LOG.getLevel().equals(Level.DEBUG)) {
			for (Marker regex : markerList) {
				LOG.trace("... "+regex.toString());
			}
		}
	}
	
	protected void tidyAfterAllMatching() {
		LOG.debug("regex.TidyAfterMatching");
		TableAndArrayCreator tableAndArrayCreator = new TableAndArrayCreator(parentTemplate);
		tableAndArrayCreator.processArraysAndTables(legacyStore);
	}
	private void createRegexStringsForSkipRegexes() {
		for (int i = 0; i < this.size(); i++) {
			Regex regex = (Regex) this.getMarkerList().get(i);
//			Regex regex = (Regex) this.getMarkerForSequencePointer(i);
			if (regex.isSkip()) {
				skipRegex(i, regex);
			}
		}
	}

	private void skipRegex(int i, Regex regex) {
		String regexString = regex.getRegexString();
		if (regexString.trim().length() == 0) {
			if (i == markerList.size() -1) {
				throw new RuntimeException("regex with skip cannot be last in a list");
			}
			Regex nextRegex = (Regex) markerList.get(i+1);
			if (nextRegex.isSkip() && nextRegex.getRegexString().trim().length() == 0) {
				throw new RuntimeException("Cannot have two empty skip regexes in sequence");
			}
			String nextRegexString = nextRegex.getRegexString();
			LOG.debug("set skipped regex to younger sibling: "+nextRegexString);
			regex.setRegexString(nextRegexString);
		}
	}
		
//	private void processParsedModules(LegacyStore legacyStore, Regex regex,
//			int moduleStart, int moduleEnd, CMLModule wrapperModule) {
//		parseExtractor = regex.getParseExtractor();
//		parseExtractor.extractModulesForEachMatch(legacyStore,
//				moduleStart, moduleEnd,	wrapperModule);
//	}

}
