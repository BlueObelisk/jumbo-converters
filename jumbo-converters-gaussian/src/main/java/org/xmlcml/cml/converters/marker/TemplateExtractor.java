package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

public class TemplateExtractor extends ParseExtractor {
	private static Logger LOG = Logger.getLogger(RegexExtractor.class);
	private Template template;
	
	public TemplateExtractor(Marker marker) {
		super(marker);
		template = (Template) marker;
	}
	
	@Override
	protected List<CMLElement> processExtractedElements(
			List<CMLElement> extractedElements) {
		return extractedElements;
	}
	
	@Override
	protected List<CMLElement> getExtractedElementList(CMLElement legacyElement) {
		List<CMLElement> elementList = new ArrayList<CMLElement>();
		return elementList;
	}

}