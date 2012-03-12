package org.xmlcml.cml.converters.graphics.svg.elements;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGRect;

/** analyzes line as possible bond
 * 
 * @author pm286
 *
 */
public class SVGChemRect extends SVGRect implements SVGChemElement {

	public SVGChemRect() {
		init();
	}
	
	protected void init() {
		SVGRect.setDefaultStyle(this);
	}
	
	public SVGChemRect(SVGElement rect) {
		this();
		SVGChem.deepCopy(rect, this);
	}

	/**
	 * 
	 * @param element
	 * @return rects
	 */
	public static List<SVGChemRect> getLineList(SVGChemElement element) {
		Nodes nodes = ((Element) element).query(".//svg:rect", SVG_XPATH);
		List<SVGChemRect> rectList = new ArrayList<SVGChemRect>();
		for (int i = 0; i < nodes.size(); i++) {
			rectList.add((SVGChemRect) nodes.get(i));
		}
		return rectList;
	}
	
}
