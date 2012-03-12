package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;

public class SVGChemG extends SVGG implements SVGChemElement {

	public SVGChemG() {
	}
	
	/** copy constructor
	 * @param svgChemSVG
	 */
	public SVGChemG(SVGChemSVG svgChemSVG) {
//		CMLUtil.debug(svgChemSVG, "svgChem");
//		SVGElement g = (SVGElement) svgChemSVG.getChildElements().get(0);
//		SVGElement g = (SVGElement) svgChemSVG.getChildElements().get(0);
		SVGChem.deepCopy(svgChemSVG, this);
	}

	public SVGChemG(SVGElement g) {
		SVGChem.deepCopy(g, this);
	}


}
