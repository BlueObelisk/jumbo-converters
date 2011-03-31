package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;

public class TemplateExtractor extends ParseExtractor {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RegexExtractor.class);
	@SuppressWarnings("unused")
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