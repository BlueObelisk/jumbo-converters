package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

public class TemplateContainer {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(TemplateContainer.class);
	
	public static final String TAG = "templateList";
	

	private Element element;
	private List<Template> templateList = new ArrayList<Template>(); 

	public TemplateContainer(Element element) {
		this.element = element;
		readChildrenAndCreateTemplates();
	}

	private void readChildrenAndCreateTemplates() {
		Elements elements = element.getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String name = element.getLocalName();
			if (Template.TAG.equals(name)) {
				Template template = new Template(element);
				templateList.add(template);
			}
		}
	}
	
	public List<Template> getTemplateList() {
		return templateList;
	}
	
}
