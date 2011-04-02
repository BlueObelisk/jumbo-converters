package org.xmlcml.cml.converters.text;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class Debug implements MarkupApplier {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(Debug.class);
	
	public static final String TAG = "debug";

	private LineContainer lineContainer;
	private Template template;
	
	public Debug(Template template) {
		this.template = template;
	}

	public void applyMarkup(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		debug();
	}

	public void debug() {
		if (lineContainer == null) {
			LOG.debug("Debug instruction");
		} else {
			Element linesElement = lineContainer.getLinesElement();
			int childCount = linesElement.getChildCount();
			LOG.debug("linesContainer in "+template.getId()+" nodes:"+childCount);
			for (int i = 0; i < childCount; i++) {
				Node node = linesElement.getChild(i);
				System.out.println(node.getClass().getName()+": " +node.getValue());
			}
			CMLUtil.debug(linesElement, template.getId());
		}
	}

	public String getId() {
		return null;
	}
	
}
