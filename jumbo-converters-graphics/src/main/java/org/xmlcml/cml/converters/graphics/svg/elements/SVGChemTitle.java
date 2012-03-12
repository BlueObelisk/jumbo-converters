package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGTitle;

public class SVGChemTitle extends SVGTitle  implements SVGChemElement {

	public SVGChemTitle(SVGTitle title) {
		SVGChem.deepCopy(title, this);
	}

}
