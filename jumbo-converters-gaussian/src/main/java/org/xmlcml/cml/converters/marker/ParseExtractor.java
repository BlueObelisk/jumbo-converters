package org.xmlcml.cml.converters.marker;

import java.util.List;

import org.apache.log4j.Level;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;

public abstract class ParseExtractor {

	public ParseExtractor(Marker marker) {
		this.marker = marker;
	}

	protected Marker marker;

	protected void extractModulesForEachMatch(LegacyStore legacyStore, int moduleStart,
			int moduleEnd, CMLModule wrapperModule) {
		for (int j = moduleEnd; j >=  moduleStart; j--) {
			CMLElement legacyElement = legacyStore.getCMLElement(j);
			if (legacyElement == null) {
				throw new RuntimeException("accessed legacy outside range: "+j);
			}
			List<CMLElement> extractedElements = getExtractedElementList(legacyElement);
			CMLModule matchModule = new CMLModule();
			matchModule.setRole(TopTemplateContainer.CAPTURE_GROUPS);
			matchModule.setDictRef(marker.createDictRef());
			extractedElements = processExtractedElements(extractedElements);
			for (CMLElement element : extractedElements) {
				matchModule.appendChild(element);
			}
			wrapperModule.insertChild(matchModule, 0);
			// remove old elements
			if (RegexSequence.LOG.getLevel().equals(Level.TRACE)) {
				int pos = legacyElement.getParent().indexOf(legacyElement);
				legacyElement.debug("DETACHING "+pos);
			}
			legacyElement.detach();
		}
	}
	
	protected abstract List<CMLElement> processExtractedElements(List<CMLElement> extractedElements);
	
	protected abstract List<CMLElement> getExtractedElementList(CMLElement legacyElement);
}
