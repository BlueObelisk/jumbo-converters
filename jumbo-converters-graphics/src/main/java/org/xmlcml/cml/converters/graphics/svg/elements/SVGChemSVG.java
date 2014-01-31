package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;

public class SVGChemSVG extends SVGSVG implements SVGChemElement {

	public SVGChemSVG() {
	}
	/** create from subclass.
	 * 
	 * @param svg
	 */
	public SVGChemSVG(SVGSVG svg) {
		SVGChem.deepCopy(svg, this);
	}
	
//	/** copy constructor
//	 * @param svgChem
//	 */
//	public SVGChemSVG(SVGChemSVG svgChem) {
//		this(svgChem);
//	}

	public void tidy() {
		SVGChemPath.convertTwoPointPathToLines(this);
	}

	/**
	 * 
	 * @param svgParent
	 * @param atom
	 * @param color
	 * @param xy2
	 * @param elementType
	 */
	public static void plotAtom(SVGElement svgParent, CMLAtom atom, 
			String color, Real2 xy2, String elementType, double opacity) {
		if (svgParent != null) {
			atom.setXY2(xy2);
			atom.setElementType(elementType);
			SVGElement circle = new SVGCircle(xy2, 4.0);
			circle.setFill(color);
			circle.setStroke("none");
			circle.setOpacity(opacity);
			svgParent.appendChild(circle);
		}
	}
	
	public static void main(String[] args) {
		
	}

}
