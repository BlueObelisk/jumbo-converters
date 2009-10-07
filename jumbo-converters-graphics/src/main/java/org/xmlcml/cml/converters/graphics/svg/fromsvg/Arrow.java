package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemElement;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPath;

public class Arrow extends SVGChemG {
	@SuppressWarnings("unused")
	private SVGChemLine line;
	@SuppressWarnings("unused")
	private SVGChemPath arrowHead;
	
	public Arrow(SVGChemLine line, SVGChemPath arrowHead) {
		this.line = line;
		this.arrowHead = arrowHead;
		line.detach();
		arrowHead.detach();
		this.appendChild(line);
		this.appendChild(arrowHead);
		this.setSvgClass("arrow");
	}
	
	public static List<Arrow> getArrowList(SVGChemElement svgChem) {
		List<Arrow> arrowList = new ArrayList<Arrow>();
		Nodes nodes = ((Element)svgChem).query(".//svg:g[@class='arrow']", SVG_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			arrowList.add((Arrow) nodes.get(i));
		}
		return arrowList;
	}
}
