package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;

public class SVGChemPoint extends SVGChemG implements SVGChemElement {

	private SVGElement point;
	private double rad = 1;
	
	public SVGChemPoint(Real2 xy1) {
		point = new SVGCircle(xy1, rad);
		this.appendChild(point);
	}
	

}
