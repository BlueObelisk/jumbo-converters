package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGEllipse;
import org.xmlcml.cml.graphics.StyleBundle;

public class SVGChemEllipse extends SVGEllipse implements SVGChemElement {

	public static StyleBundle DEFAULT_STYLE = new StyleBundle(
			"#0000ff", // fill
			"#0000ff", // stroke
			1.0, // strokeWidth
			"",
			1.0, // fontSize
			"",
			1.0
			);
	
	public SVGChemEllipse() {
		init();
	}
	protected void init() {
		SVGEllipse.setDefaultStyle(this);
	}
	public SVGChemEllipse(SVGElement ellipse) {
		this();
		SVGChem.deepCopy(ellipse, this);
	}

}
