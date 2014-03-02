package org.xmlcml.cml.converters.spectrum.svg.cml2svg;

import nu.xom.Element;

import org.xmlcml.graphics.svg.SVGG;



public class CMLSpect2SVGHelper {
	
	protected double width;
	protected double height;
	protected double xMin;
	protected double xMax;
	protected double yMin;
	protected double yMax;
	
	public CMLSpect2SVGHelper() {
		setDefaults();
	}

	protected void setDefaults() {
		width = 500.;
		height = 400;;
		xMin = 0.0;
		xMax = 10.0;
		yMin = 0.0;
		yMax = 10.0;
	}
	
	public SVGG parse(Element xml) {
		SVGG svgg = new SVGG();
		return svgg;
	}
	
}
