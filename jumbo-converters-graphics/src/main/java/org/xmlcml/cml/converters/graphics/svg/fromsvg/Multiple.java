package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemSVG;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.euclid.Real2;

public class Multiple extends ArrayList<SVGChemLine> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8274932393834574004L;
//	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Multiple.class);
	
	private double averageLength;
	private SVGChemSVG svgChem;
	@SuppressWarnings("unused")
	private GraphicsConverterTool converter;
	
	public Multiple(SVG2CMLTool converter) {
		this.averageLength = converter.getAverageLength();
		this.converter = converter;
		svgChem = converter.getSvgChem();
	}
	
	public SVGChemLine getMultipleBond() {
		SVGChemLine multipleBond = null;
		if (size() > 3) {
			throw new RuntimeException("quadruple or higher bonds not allowed: "+size());
		} else if (size() == 1) {
			throw new RuntimeException("single bonds not allowed in multiple");
		} else if (size() == 2) {
			multipleBond = makeDouble();
		} else if (size() == 3) {
			multipleBond = makeTriple();
		}
		return multipleBond;
	}

	private SVGChemLine makeDouble() {
		SVGChemLine multipleBond = null;
		this.get(0).detachPath(svgChem);
		this.get(1).detachPath(svgChem);
		double delta = this.get(0).getLength() - this.get(1).getLength();
		if (Math.abs(delta) > SVG2CMLTool.DOUBLE_BOND_DELTA_RATIO*averageLength) {
			// choose which line to keep
			int ibond = (delta > 0) ? 0 : 1;
			multipleBond = this.get(ibond);
			multipleBond.setOrder(CMLBond.DOUBLE_D);
			this.get(1 - ibond).detach();
		} else {
			// average bond
			Real2 xy0 = this.get(0).getXY(0).getMidPoint(this.get(1).getXY(0));
			Real2 xy1 = this.get(0).getXY(1).getMidPoint(this.get(1).getXY(1));
			if (xy0.getDistance(xy1) < 1) {
				LOG.warn("COINCIDENT POINTS "+xy0+"/"+xy1);
			} else {
				SVGLine linex = null;
				linex = new SVGLine(xy0, xy1);
				multipleBond = new SVGChemLine(linex);
				multipleBond.setStrokeWidth(2.0);
				multipleBond.setOrder(CMLBond.DOUBLE_D);
				this.get(0).detach();
				this.get(1).detach();
			}
		}
		return multipleBond;
	}

	private SVGChemLine makeTriple() {
		SVGChemLine multipleBond;
		this.get(0).detachPath(svgChem);
		this.get(1).detachPath(svgChem);
		this.get(2).detachPath(svgChem);
		this.get(0).detach();
		this.get(2).detach();
		multipleBond = this.get(1);
		multipleBond.setOrder(CMLBond.TRIPLE_T);
		multipleBond.setOpacity(0.3);
		return multipleBond;
	}
}
