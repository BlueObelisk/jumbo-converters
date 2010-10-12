package org.xmlcml.cml.converters.marker;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLScalar;

public class RegexCounter extends MarkerCounter {
	private final static Logger LOG = Logger.getLogger(RegexCounter.class);

	public RegexCounter(Marker regex) {
		super(regex);
	}

//	public boolean matches(CMLElement element) {
//		markerMatched = false;
//		if (element instanceof CMLScalar) {
//			String line = ((CMLScalar)element).getValue();
//			markerMatched = ((Regex)marker).getParserRegexPattern().matcher(line).matches();
//		}
//		return markerMatched;
//	}
	
	@Override
	protected void matchGreedilyAndPointToNextUnmatchedLegacyElement(LegacyStore legacyStore) {
//		Regex regex = ((Regex)marker);
//		CMLElement currentElement = null;
//		// eat elements against current regex until limit reached
//		while ((currentElement = legacyStore.getCurrentLegacyElement()) != null) {
//			// fail if no match and no skip set
//			markerMatched = matches(currentElement);
//			if (markerMatched == markerSkip) {
//				LOG.debug("match aborted matched=skip="+markerMatched+"; "+legacyStore.getPointer() +
//						" with regex ~"+regex.getRegexInterpreter().getParserRegexPattern().pattern()+
//						"~ ... ~"+currentElement.getValue()+"~");
//				this.setMatched(this.isWithinCount());
//				break;
//			}
//			markerMatched = true;
//			this.forceIncrementCount();
//			LOG.debug(((markerSkip) ? "skipped" : "matched")+ " module "+legacyStore.getPointer()+" with regex ~"+regex.getRegexString()+
//					"~ / "+this.markerCount+ "... ~"+currentElement.getValue()+"~ pointer = "+markerCount);
//			setModuleEnd(legacyStore.getPointer());  // record for later extraction
//			if (!legacyStore.canIncrementPointer()) {		// more elements to match?
//				legacyStore.forceIncrementPointer();
//				LOG.debug("no more legacy");
//				break;
//			}
//			currentElement = legacyStore.incrementLegacyElement();  // get next legacyElement line
//			if (!this.canIncrementCount()) {
//				LOG.debug("Cannot increase this regex count; getting next regex");
//				break;  // go on to next regex
//			}
//			LOG.debug("getting next legacy");
//		}
//		LOG.debug("end of greedy regex");
	}

}
