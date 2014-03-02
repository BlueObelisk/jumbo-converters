package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.graphics.svg.SVGTitle;

public class SVGChemTitle extends SVGTitle  implements SVGChemElement {

	public SVGChemTitle(SVGTitle title) {
		SVGChem.deepCopy(title, this);
	}

}
