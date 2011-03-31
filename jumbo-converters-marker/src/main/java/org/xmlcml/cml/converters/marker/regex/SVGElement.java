package org.xmlcml.cml.converters.marker.regex;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public abstract class SVGElement extends Element {
	public static final double WIDTH = 100.0;
	public static final double HEIGHT = 50.0;
	private double x;
	private double y;

	public SVGElement(String tag) {
		super(tag);
	}
	
	protected void setAttribute(String name, String value) {
		if (value != null) {
			this.addAttribute(new Attribute(name, value));
		}
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		this.setAttribute("x", ""+x);
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		this.setAttribute("y", ""+y);
	}
	
	public double getWidth() {
		double width = 0.0;
		Elements childElements = this.getChildElements();
		if (childElements.size() == 0) {
			width = WIDTH;
		} else {
			for (int i = 0; i < childElements.size(); i++) {
				width += ((SVGElement)childElements.get(i)).getWidth();
			}
		}
		return width;
	}
	
	public double getHeight() {
		double height = 0.0;
		Elements childElements = this.getChildElements();
		if (childElements.size() == 0) {
			height = HEIGHT;
		} else {
			for (int i = 0; i < childElements.size(); i++) {
				height = ((SVGElement)childElements.get(i)).getHeight()+HEIGHT;
			}
		}
		return height;
	}
}
