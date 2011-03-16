package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class TransformElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TransformElement.class);
	
	public static final String TAG = "transform";
	
	private Element element;

	private Elements transformChildElements;

	public TransformElement(Element element) {
		this.element = element;
		transformChildElements = this.element.getChildElements();
	}

	
	public void applyMarkup(LineContainer lineContainer) {
		for (int i = 0; i < transformChildElements.size(); i++) {
			Element transformer = transformChildElements.get(i);
			System.out.println("Processing "+transformer.getLocalName());
		}
	}
	
	public String getId() {
		return element.getAttributeValue("id");
	}
	
	public void debug() {
		LOG.debug("DEBUG Transform childElements: "+transformChildElements.size());
	}
}
