package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemCircle;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemDesc;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemElement;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemEllipse;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPath;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPolygon;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPolyline;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemRect;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemSVG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemText;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemTitle;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGDesc;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGEllipse;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGTitle;

public class SVGChem {

	public static void deepCopy(SVGElement from, SVGChemElement to) {
		((SVGElement)to).copyAttributesFrom(from);
		for (int i = 0; i < from.getChildCount(); i++) {
			Node node = from.getChild(i);
			Node newNode;
			if (node instanceof Text) {
				newNode = new Text(node.getValue());
			} else if (node instanceof SVGCircle) {
				newNode = new SVGChemCircle((SVGElement) node);
			} else if (node instanceof SVGDesc) {
				newNode = new SVGChemDesc((SVGDesc) node);
			} else if (node instanceof SVGEllipse) {
				newNode = new SVGChemEllipse((SVGElement) node);
			} else if (node instanceof SVGG) {
				newNode = new SVGChemG((SVGElement) node);
			} else if (node instanceof SVGLine) {
				newNode = new SVGChemLine((SVGLine) node);
			} else if (node instanceof SVGPath) {
				newNode = new SVGChemPath((SVGPath) node);
			} else if (node instanceof SVGPolygon) {
				newNode = new SVGChemPolygon((SVGPolygon) node);
			} else if (node instanceof SVGPolyline) {
				newNode = new SVGChemPolyline((SVGPolyline) node);
			} else if (node instanceof SVGRect) {
				newNode = new SVGChemRect((SVGElement) node);
			} else if (node instanceof SVGSVG) {
				newNode = new SVGChemSVG((SVGSVG) node);
			} else if (node instanceof SVGTitle) {
				newNode = new SVGChemTitle((SVGTitle) node);
			} else if (node instanceof SVGText) {
				newNode = new SVGChemText((SVGText) node);
			} else if (node instanceof Comment) {
				newNode = new Comment((Comment) node);
			} else if (node instanceof ProcessingInstruction) {
				newNode = new ProcessingInstruction((ProcessingInstruction) node);
			} else {
				throw new RuntimeException("bad copy "+node);
			}
			((Element)to).appendChild(newNode);
		}
	};
	
}
