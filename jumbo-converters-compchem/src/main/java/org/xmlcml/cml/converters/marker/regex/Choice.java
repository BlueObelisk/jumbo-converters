package org.xmlcml.cml.converters.marker.regex;

import org.apache.log4j.Logger;

public class Choice extends AbstractRegexElement {
	private final static Logger LOG = Logger.getLogger(Choice.class);
	
	public static final String CHOICE_TAG = "choice";

	public Choice() {
		super(CHOICE_TAG);
	}

}
