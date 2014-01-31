package org.xmlcml.cml.converters.graphics.svg.elements;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGEllipse;
import org.xmlcml.graphics.svg.StyleBundle;

public class SVGChemEllipse extends SVGEllipse implements SVGChemElement {

	public static StyleBundle DEFAULT_STYLE = new StyleBundle(
			null,
			"#0000ff", // fill
			"#0000ff", // stroke
			1.0, // strokeWidth
			"",
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
