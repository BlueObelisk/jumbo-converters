package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.euclid.Real2;

public class SVGChemPoint extends SVGChemG implements SVGChemElement {

	private SVGElement point;
	private double rad = 1;
	
	public SVGChemPoint(Real2 xy1) {
		point = new SVGCircle(xy1, rad);
		this.appendChild(point);
	}
	

}
