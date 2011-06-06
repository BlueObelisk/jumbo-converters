package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class TransformListElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TransformListElement.class);
	public static final String TAG = "transformList";
	
	private Element element;
	private Template template;
	private List<MarkupApplier> markerList;

	public TransformListElement(Element element) {
		if (!(element.getLocalName().equals(TransformListElement.TAG))) {
			throw new RuntimeException("cannot make transformList from: "+element.getLocalName());
		}
		this.element = element;
		expandIncludes(this.element);
		processChildElementsAndAttributes();
	}

	public TransformListElement(Element element, Template template) {
		this(element);
		this.template = template;
	}

	private void expandIncludes(Element element) {
        try {
        	CMLUtil.ensureDocument(element);
            ClassPathXIncludeResolver.resolveIncludes(element.getDocument());
		} catch (Exception e) {
			throw new RuntimeException("Bad XInclude", e);
		}
	}

	private void processChildElementsAndAttributes() {
		processAttributes();
		createSubclassedElementsFromChildElements();
	}

	private void createSubclassedElementsFromChildElements() {
		Elements childElements = element.getChildElements();
		markerList = new ArrayList<MarkupApplier>();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String name = childElement.getLocalName();
			if (TransformElement.TAG.equals(name)) {
				TransformElement transform = new TransformElement(childElement, template);
				markerList.add(transform);
//			} else if (TransformElement.TAG.equals(name)) {
//				TransformListElement transformList = new TransformListElement(childElement, template);
//				markerList.add(transformList);
			} else if (TransformListElement.TAG.equals(name)) {
				TransformListElement transformList = new TransformListElement(childElement, template);
				transformList.processChildElementsAndAttributes();
				List<MarkupApplier> childMarkerList = transformList.getMarkerList();
				for (MarkupApplier markup : childMarkerList) {
					markerList.add(markup);
				}
			} else {
				CMLUtil.debug(childElement, "UNKNOWN CHILD");
				throw new RuntimeException("unknown child: "+name);
			}
		}
	}

	public List<MarkupApplier> getMarkerList() {
		return markerList;
	}

	private void processAttributes() {
		// no-op
	}

	public void applyMarkup(Element element) {
		for (MarkupApplier marker : markerList) {
			LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
			marker.applyMarkup(element);
		}
	}

	public void applyMarkup(LineContainer lineContainer) {
		for (MarkupApplier marker : markerList) {
			LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
			marker.applyMarkup(lineContainer);
		}
	}

	public void debug() {
		// TODO Auto-generated method stub

	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
