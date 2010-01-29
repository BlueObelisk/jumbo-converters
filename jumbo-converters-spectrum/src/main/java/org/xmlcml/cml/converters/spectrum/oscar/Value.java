package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;
import nu.xom.Elements;

public class Value {
	/*
	<quantity type="shift">
		<value>
			<point>9.73</point>
		</value>
	</quantity>
	 */
	String min;
	String max;
	String point;
	private Elements childElements;

	public Value(Element valueElement) {
		Elements minElements = valueElement.getChildElements("min");
		Elements maxElements = valueElement.getChildElements("max");
		Elements pointElements = valueElement.getChildElements("point");
		childElements = valueElement.getChildElements();
		if (childElements.size() != minElements.size() + maxElements.size() + pointElements.size()) {
			throw new RuntimeException("spectrum: unknown children of value");
		}
		min = getValue(minElements, "min");
		max = getValue(maxElements, "max");
		point = getValue(pointElements, "point");
	}
	
	private String getValue(Elements elements, String type) {
		String content = null;
		if (elements.size() > 1) {
			throw new RuntimeException("spectrum: too many children: "+type);
		} else if (elements.size() == 1) {
			content = elements.get(0).getValue();
		}
		return content;
	}
}
