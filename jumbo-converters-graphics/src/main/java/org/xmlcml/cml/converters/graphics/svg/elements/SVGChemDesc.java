package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.graphics.svg.SVGDesc;

public class SVGChemDesc extends SVGDesc  implements SVGChemElement {

	public SVGChemDesc(SVGDesc desc) {
		SVGChem.deepCopy(desc, this);
	}

}
