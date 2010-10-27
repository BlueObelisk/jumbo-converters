package org.xmlcml.cml.converters.cml;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLProperty;

public abstract class RawXML2CMLProcessor {

	protected Element xmlInput;
	protected CMLElement cmlElement;
	
	public void process(Element xml) {
//		this.xmlInput = CMLBuilder.ensureCML(xml);
		this.xmlInput = xml;
		CMLUtil.removeWhitespaceNodes(xml);
		processXML();
		cmlElement = CMLBuilder.ensureCML(xml);
	}

	protected void wrapWithProperty(String xpath) {
		Nodes nodes = xmlInput.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			Element wrappedElement = (Element) nodes.get(i);
			CMLProperty property = new CMLProperty();
			Attribute dictRef = wrappedElement.getAttribute("dictRef");
			dictRef.detach();
			property.addAttribute(dictRef);
			xmlInput.replaceChild(wrappedElement, property);
			decorateProperty(wrappedElement, property);
			property.appendChild(wrappedElement);
		}
	}

	/**
	 * override this to add special features to property
	 */
	protected void decorateProperty(Element scalArrMat, CMLProperty property) {
	}

	/**
	 * fill in how XML is processed specifically
	 * must generate cmlElement
	 */
	protected abstract void processXML();

	public CMLElement getCMLElement() {
		return cmlElement;
	}
}
