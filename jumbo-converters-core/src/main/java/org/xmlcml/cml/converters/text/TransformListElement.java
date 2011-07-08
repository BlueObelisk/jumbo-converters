package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;

public class TransformListElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TransformListElement.class);
	public static final String TAG = "transformList";

	// attributes
	private static final String ID                = "id";
	private static final String UNTIL             = "until";
	private static final String WHILE             = "while";

	public static String[] OPTIONS = 
		new String[]{
		ID,
		UNTIL,
		WHILE,
	};
	
	
	private Element transformListElement;
	private Template template;
	private OutputLevel outputLevel;
	private List<MarkupApplier> markerList;

	// attributes
	private String until;
	private String whilex;

	public TransformListElement(Element element) {
		if (!(element.getLocalName().equals(TransformListElement.TAG))) {
			throw new RuntimeException("cannot make transformList from: "+element.getLocalName());
		}
		this.transformListElement = element;
		expandIncludes(this.transformListElement);
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
		Elements childElements = transformListElement.getChildElements();
		markerList = new ArrayList<MarkupApplier>();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String name = childElement.getLocalName();
			if (TransformElement.TAG.equals(name)) {
				MarkupApplier transform = new TransformElement(childElement, template);
				markerList.add(transform);
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
		Template.checkIfAttributeNamesAreAllowed(transformListElement, OPTIONS);
		
		until                    = addAndIndexAttribute(UNTIL);
		whilex                   = addAndIndexAttribute(WHILE);
		
		TransformElement.manageOutputLevel(outputLevel, transformListElement);
	}
	
	private String addAndIndexAttribute(String attName) {
		String attVal = transformListElement.getAttributeValue(attName);
		return attVal;
	}

// ==================================================================
	

	public void applyMarkup(Element element) {
		// we'll change this to "while" when we are brave enough
		if (testIsTrue(element)) {
			for (MarkupApplier marker : markerList) {
				LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
				marker.applyMarkup(element);
			}
		}
	}

	public void applyMarkup(LineContainer lineContainer) {
		Element element = lineContainer.getLinesElement();
		// we'll change this to "while" when we are brave enough
		if (testIsTrue(element)) {
			for (MarkupApplier marker : markerList) {
				LOG.trace("Applying: "+marker.getClass().getSimpleName()+" "+marker.getId());
				marker.applyMarkup(lineContainer);
			}
		}
	}

	private boolean testIsTrue(Element element) {
		// true if no attributes
		boolean testIsTrue = true;
		if (whilex != null) {
			if (until != null) {
				throw new RuntimeException("Cannot have both while and until");
			}
			Nodes testNodes = TransformElement.queryUsingNamespaces(element, whilex);
			testIsTrue = (testNodes.size() > 0);
		} else if (until != null) {
			Nodes testNodes = TransformElement.queryUsingNamespaces(element, until);
			testIsTrue = (testNodes.size() == 0);
		}
		return testIsTrue;
	}

	public void debug() {
		// TODO Auto-generated method stub

	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
