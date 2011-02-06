package org.xmlcml.cml.converters.format;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Template;

public abstract class IfReader extends LineReader {
	private final static Logger LOG = Logger.getLogger(IfReader.class);

	public final static String IF_READER = "if";
	public IfReader(Element childElement, Template template) {
		super(IF_READER, childElement, template);
		init();
	}

	protected void init() {
	}

}
