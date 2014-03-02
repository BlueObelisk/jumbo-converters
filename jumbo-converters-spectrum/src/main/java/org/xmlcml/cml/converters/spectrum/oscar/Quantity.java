package org.xmlcml.cml.converters.spectrum.oscar;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLPeak;

public class Quantity {
	public Nodes getTexts() {
		return texts;
	}

	public Elements getValueElements() {
		return valueElements;
	}

	public Elements getUnitsElements() {
		return unitsElements;
	}

	public String getPeakType() {
		return peakType;
	}

	public Value getCoupling() {
		return coupling;
	}

	private final static Logger LOG = Logger.getLogger(Quantity.class);
	
	private String type;
	private Nodes texts;
	private Elements valueElements;
	private Elements unitsElements;
	private Elements childElements;
	private String peakType;
	private Value coupling;
	private String comment;
	private List<Value> valueList;

	public List<Value> getValueList() {
		return valueList;
	}

	public Quantity(Element element) {
		getType(element);
		texts = element.query("text()");
		valueElements = element.getChildElements("value");
		unitsElements = element.getChildElements("units");
		childElements = element.getChildElements();
		if (childElements.size() == 0 && texts.size() == 1) {
			processText(texts.get(0));
		} else if (valueElements.size() + unitsElements.size() == childElements.size()) {
			processValues();
//			processUnits();
		}
	}


//	private void processUnits() {
//		if (unitsElements.size() > 1) {
//			throw new RuntimeException("only one units allowed");
//		} else if (unitsElements.size() == 1) {
//			units = new Units(unitsElements.get(0));
//		}
//	}

	private void processValues() {
		valueList = new ArrayList<Value>();
		for (int i = 0; i < valueElements.size(); i++) {
			Element valueElement = valueElements.get(i);
			Element unitsElement = null;
			Element parentElement = (Element) valueElement.getParent();
			if (parentElement != null) {
				for (int j = parentElement.indexOf(valueElement)+1; j < parentElement.getChildCount(); j++) {
					if (parentElement.getChild(j) instanceof Element) {
						Element nextElement = (Element) parentElement.getChild(j);
						if ("units".equals(nextElement.getLocalName())) {
							unitsElement = nextElement;
						}
						break;
					}
				}
			}
			Value value = new Value(valueElement, unitsElement);
			valueList.add(value);
		}
	}

	private void processText(Node node) {
		if ("peaktype".equals(getType())) {
			peakType = node.getValue();
		} else if ("comment".equals(getType())) {
			comment = node.getValue();
		} else {
			throw new RuntimeException("quantity: unknown text type "+getType());
		}
	}

	private void getType(Element element) {
		type = element.getAttributeValue("type");
		if (type == null) {
			CMLUtil.debug(element, "QUANTITY");
			throw new RuntimeException("quantity has no type");
		}
	}

	public void addToPeak(CMLPeak cmlPeak) {
		if ("shift".equals(getType())) {
		} else if ("peaktype".equals(getType())) {
		} else if ("integral".equals(getType())) {
		} else if ("coupling".equals(getType())) {
		} else if ("comment".equals(getType())) {
		} else {
			throw new RuntimeException("unprocessed type "+getType());
		}
	}

	public String getType() {
		return type;
	}

	public String getTextValue() {
		return (texts != null && texts.size() == 1) ? texts.get(0).getValue() : null;
	}
}
