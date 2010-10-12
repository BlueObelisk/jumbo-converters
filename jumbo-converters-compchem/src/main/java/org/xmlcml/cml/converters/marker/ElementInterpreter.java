package org.xmlcml.cml.converters.marker;

import nu.xom.Element;

public abstract class ElementInterpreter {

	protected Element interpretedElement;
	protected Marker marker;
	
	protected ElementInterpreter(Marker marker) {
	}
}
