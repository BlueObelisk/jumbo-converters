package org.xmlcml.cml.converters.format;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.text.Template;

public abstract class IfReader extends LineReader {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(IfReader.class);

	public final static String IF_READER = "if";
	public IfReader(Element childElement, Template template) {
		super(IF_READER, childElement, template);
		init();
	}

	protected void init() {
	}

}
