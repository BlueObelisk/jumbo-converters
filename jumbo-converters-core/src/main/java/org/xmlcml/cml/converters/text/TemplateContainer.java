package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

public class TemplateContainer {
	private final static Logger LOG = Logger.getLogger(TemplateContainer.class);
	
	public static final String TAG = "templateList";
	

	private Element element;
	private List<Template> templateList = new ArrayList<Template>(); 

	public TemplateContainer(Element element) {
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
	
}
