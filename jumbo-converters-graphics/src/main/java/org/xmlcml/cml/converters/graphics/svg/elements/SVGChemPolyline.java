package org.xmlcml.cml.converters.graphics.svg.elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolyline;

/** analyzes line as possible bond
 * 
 * @author pm286
 *
 */
public class SVGChemPolyline extends SVGPolyline implements SVGChemElement {
	private static Logger LOG = Logger.getLogger(SVGChemPolyline.class);

	public SVGChemPolyline() {
		init();
	}
	
	protected void init() {
		SVGPolyline.setDefaultStyle(this);
	}
	
	public static SVGChemPolyline createFromLine(SVGLine line) {
		Real2Array xy = new Real2Array();
		xy.add(line.getXY(0));
		xy.add(line.getXY(1));
		return new SVGChemPolyline(xy);
	}
	
	public SVGChemPolyline(SVGPolyline line) {
		this();
		SVGChem.deepCopy(line, this);
	}
	
	public SVGChemPolyline(Real2Array r2a) {
		this(new SVGPolyline(r2a));
	}
	
	
	/**
	 * 
	 * @param line0
	 * @param line1
	 * @param eps
	 * @return polyline
	 */
	
	public static SVGPolyline createMergedPolyline(SVGElement line0, SVGElement line1, double eps) {
		SVGPolyline polyline0 = passPolylineOrCreatePolylineFromLine(line0);
		SVGPolyline polyline1 = passPolylineOrCreatePolylineFromLine(line1);
		return SVGChemPolyline.createMergedPolyline1(polyline0, polyline1, eps);
	}

	
	public static SVGPolyline createMergedPolyline1(SVGPolyline polyline0, SVGPolyline polyline1, double eps) {
		LOG.trace("createMergedPolyline1");
		if (polyline0 == null || polyline1 == null) {
			throw new RuntimeException("Cannot create SVGChemPolyline from null arguments");
		}
		Real2Array ra0 = polyline0.getReal2Array();
		Real2Array ra1 = polyline1.getReal2Array();
//		LOG.debug("polylines"+ra0+"/"+ra1);
		Real2 ra0Last = (ra0.size() > 0) ? ra0.get(ra0.size()-1) : null;
		Real2 ra1First = (ra1.size() > 0) ? ra1.get(0) : null;
		SVGPolyline polyline = null;
		if (ra0Last != null && ra1First != null) {
//			LOG.debug("comparing "+ra0Last+"/"+ra1First);
			if (ra0Last.isEqualTo(ra1First, eps)) {
				ra1.deleteElement(0);
				ra0.add(ra1);
				polyline0.setReal2Array(ra0);
				LOG.trace("merging... "+ra0.size());
				polyline = polyline0;
			}
		} else {
			//return null;
		}
		return polyline;
	}
	
	/**
	 * @param line0
	 * @throws RuntimeException
	 */
	private static SVGPolyline passPolylineOrCreatePolylineFromLine(SVGElement line)
			throws RuntimeException {
		SVGPolyline polyline = null;
		if (line == null) {
			throw new RuntimeException("null line argument");
		} else if (line instanceof SVGLine) {
			LOG.trace("trying convert line to polyline");
			polyline = new SVGChemPolyline(new SVGPolyline((SVGLine)line));
		} else if (line instanceof SVGPolyline) {
			polyline = (SVGPolyline) line;
		} else {
			throw new RuntimeException("wrong class for polyline: "+line.getClass());
		}
		return polyline;
	}
}
