package org.xmlcml.cml.converters.marker;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * not yet sure whether this is needed
 * @author pm286
 *
 */
public class TemplateCounter extends MarkerCounter {
	private static Logger LOG = Logger.getLogger(TemplateCounter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public TemplateCounter(Marker template) {
		super(template);
//		this.marker = template;
	}
	
	@Override
	protected void matchGreedilyAndPointToNextUnmatchedLegacyElement(
			LegacyStore legacyStore) {
		LOG.debug("matchGreedilyAndPointToNextUnmatchedLegacyElement...");
		marker.childMarkerSequence.match(legacyStore);
		this.markerMatched = true;
	}

}
