package org.xmlcml.cml.converters.graphics.svg.elements;

import java.util.ArrayList;

import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;

public class SVGChemCircle extends SVGCircle implements SVGChemElement {

//	public static StyleBundle DEFAULT_STYLE = new StyleBundle(
//			"#0000ff", // fill
//			"#0000ff", // stroke
//			1.0, // strokeWidth
//			"",
//			1.0, // fontSize
//			"",
//			1.0
//			);
	public SVGChemCircle() {
		init();
	}
	
	protected void init() {
		SVGCircle.setDefaultStyle(this);
	}
	
	public SVGChemCircle(SVGElement circle) {
		SVGChem.deepCopy(circle, this);
	}

	/**
	 * 
	 * @param element
	 * @return circles
	 */
	public static List<SVGChemCircle> getCircleList(SVGChemElement element) {
		Nodes nodes = ((Element) element).query(".//svg:circle", SVG_XPATH);
		List<SVGChemCircle> circleList = new ArrayList<SVGChemCircle>();
		for (int i = 0; i < nodes.size(); i++) {
			circleList.add((SVGChemCircle) nodes.get(i));
		}
		return circleList;
	}

}
