package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class TemplateContainer {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(TemplateContainer.class);
	
	public static final String TAG = "templateList";
	

	private Element element;
	private List<Template> templateList = new ArrayList<Template>(); 

	public TemplateContainer(Element element) {
		this.element = element;
		processChildElements();
	}

	private void processChildElements() {
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

	public void apply(LineContainer lineContainer) {
		for (Template template : templateList) {
			template.debug();
			List<Element> elements = template.applyChunkers(lineContainer);
			LOG.trace("found child elements after wrap: "+elements.size());
			for (Element element : elements) {
//				CMLUtil.debug(element, "**childEl");
				LineContainer childLineContainer = new LineContainer(element);
//				childLineContainer.debug("child");
				template.applyMarkup(childLineContainer);
				
			}
		}
	}
	
}
