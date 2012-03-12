package org.xmlcml.cml.converters.text;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.LegacyProcessor;


public class TemplateProcessor /*extends LegacyProcessor*/ {

	private Template template;
	
	public TemplateProcessor(Template template) {
		super();
		this.template = template;
	}
		
	public Element applyMarkup(String s) {
		template.applyMarkup(s);
		return createLinesElement();
	}

	private Element createLinesElement() {
		Element linesElement = template.getLineContainer().getLinesElement();
		return linesElement;
	}

	public Element applyMarkup(List<String> lines) {
		template.applyMarkup(new LineContainer(lines, template));
		return createLinesElement();
	}

	/** interlude to deal with pre-formed XML
	 * 
	 * @param element
	 * @return element
	 */
	public Element applyMarkup(Element element) {
		return element;
	}

}
