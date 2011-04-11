package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;

public class TemplateListElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TemplateListElement.class);
	
	public static final String TAG = "templateList";
	

	private Element element;
	private List<Template> templateList = new ArrayList<Template>(); 

	public TemplateListElement(Element element) {
		this.element = element;
		readChildrenAndCreateTemplates();
	}

	private void readChildrenAndCreateTemplates() {
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String childName = childElement.getLocalName();
			LOG.trace(TAG+" CHILD: "+childName);
			if (Template.TAG.equals(childName)) {
				Template template = new Template(childElement);
				templateList.add(template);
			} else {
				throw new RuntimeException("Unexpected child of "+TAG+": "+childName);
			}
		}
	}
	
	public List<Template> getTemplateList() {
		return templateList;
	}
	
	public void applyMarkup(LineContainer lineContainer) {
		for (Template childTemplate : this.getTemplateList()) {
			List<Element> elements = childTemplate.resetNodeIndexAndApplyChunkers(lineContainer);
			LOG.trace("template start+end wrapped "+elements.size()+" child elements");
			for (Element element : elements) {
//				CMLUtil.debug(element, "WRAPPED elements");
			}
			for (Element element : elements) {
				CMLElement.addCMLXAttribute(element, Template.TEMPLATE_REF, childTemplate.getId());
				LineContainer childLineContainer = new LineContainer(element, childTemplate);
				childTemplate.applyMarkup(childLineContainer);
			}
		}
		
	}
	
	public String getId() {
		return element.getAttributeValue("id");
	}
	
	public void debug() {
		LOG.debug("DEBUG TemplateList: "+templateList.size());
		for (Template template : templateList) {
			template.debug();
		}
	}
}
