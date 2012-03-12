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
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGDesc;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGEllipse;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.cml.graphics.SVGPolygon;
import org.xmlcml.cml.graphics.SVGPolyline;
import org.xmlcml.cml.graphics.SVGRect;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.cml.graphics.SVGTitle;

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
