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
	private Units units;

	/**
	 * 
	 * @param valueElement
	 * @param unitsElement may be null if no &lt;units> follows the &lt;value>
	 */
	public Value(Element valueElement, Element unitsElement) {
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
		if (point == null) {
			if (min == null || max == null) {
				throw new RuntimeException("peak must have point or min and max");
			}
		}
		if (unitsElement != null) {
			units = new Units(unitsElement);
		}
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

	public String getPoint() {
		return point;
	}

	public Units getUnits() {
		return units;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}
}
