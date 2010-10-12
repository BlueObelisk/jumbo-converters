package org.xmlcml.cml.converters.marker.regex;

public class SVGText extends SVGElement {
	public SVGText() {
		super("text");
	}
	
	public void setText(String s) {
		this.appendChild(s);
	}
	
	public void setCoords(double x, double y) {
		 
	}
}
