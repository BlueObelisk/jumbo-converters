package org.xmlcml.cml.converters.graphics.svg.elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPolygon;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;

/** analyzes line as possible bond
 * 
 * @author pm286
 *
 */
public class SVGChemPolygon extends SVGPolygon implements SVGChemElement {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(SVGChemPolygon.class);

	public SVGChemPolygon() {
		init();
	}
	
	protected void init() {
		SVGPolygon.setDefaultStyle(this);
	}
	
	public static SVGChemPolygon createFromLine(SVGLine line) {
		Real2Array xy = new Real2Array();
		xy.add(line.getXY(0));
		xy.add(line.getXY(1));
		return new SVGChemPolygon(xy);
	}
	
	public SVGChemPolygon(SVGPolygon line) {
		this();
		SVGChem.deepCopy(line, this);
	}
	
	public SVGChemPolygon(Real2Array r2a) {
		this(new SVGPolygon(r2a));
	}
	
	/**
	 * 
	 * @return line
	 */
	public Real2Array getReal2Array() {
		if (real2Array == null) {
			real2Array = Real2Array.createFromPairs(getAttributeValue("points"),
					S_COMMA+S_PIPE+CMLConstants.S_SPACE);
		}
		return real2Array;
	}
	
	/** set line and add coordinates to super.
	 * 
	 * @param real2Array
	 */
	public void setReal2Array(Real2Array real2Array) {
		this.real2Array = real2Array;
	}
	
	
	/**
	 * 
	 * @param line0
	 * @param line1
	 * @param eps
	 * @return polygon
	 */
	public static SVGChemPolygon createMergedPolygon(SVGElement line0, SVGElement line1, double eps) {
		SVGPolygon polygon0 = createPolygon(line0);
		SVGPolygon polygon1 = createPolygon(line1);
		return SVGChemPolygon.createMergedPolygon1(polygon0, polygon1, eps);
	}

	
	public static SVGChemPolygon createMergedPolygon1(SVGPolygon polygon0, SVGPolygon polygon1, double eps) {
		if (polygon0 == null || polygon1 == null) {
			throw new RuntimeException("Cannot create SVGChemPolygon from null arguments");
		}
		Real2Array ra0 = new Real2Array(polygon0.getReal2Array());
		Real2Array ra1 = new Real2Array(polygon1.getReal2Array());
		Real2 ra0Last = (ra0.size() > 0) ? ra0.get(ra0.size()-1) : null;
		Real2 ra1First = (ra1.size() > 0) ? ra1.get(0) : null;
		if (ra0Last != null && ra1First != null) {
			if (ra0Last.isEqualTo(ra1First, eps)) {
				ra1.deleteElement(0);
				ra0.add(ra1);
			}
		}
		return new SVGChemPolygon(ra0);
	}
	
	/**
	 * @param line
	 * @return polygon
	 * @throws RuntimeException
	 */
	private static SVGPolygon createPolygon(SVGElement line)
			throws RuntimeException {
		SVGPolygon polygon = null;
		if (line == null) {
			throw new RuntimeException("null line argument");
		} else if (line instanceof SVGLine) {
			polygon = new SVGPolygon((SVGLine)line);
		} else if (line instanceof SVGPolygon) {
			polygon = (SVGPolygon) line;
		} else {
			throw new RuntimeException("wrong class for polygon: "+line.getClass());
		}
		return polygon;
	}
}
