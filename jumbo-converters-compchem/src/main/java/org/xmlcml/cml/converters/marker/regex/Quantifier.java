package org.xmlcml.cml.converters.marker.regex;

import nu.xom.Element;

public class Quantifier extends AbstractRegexElement {
	public final static String QUANTIFIER_TAG = "quantifier";

	public Quantifier() {
		super(QUANTIFIER_TAG);
	}
}
