package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.ArrayList;
import java.util.List;

import nu.xom.ParentNode;

import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPath;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Range;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;

/** 
 * manages lists of short bonds
 * could be hatches, dashes or short multiple bonds
 * @author pm286
 *
 */
public class ShortLineList extends ArrayList<SVGChemLine> {

	public enum Type {
		HATCH,
		DASH,
		UNKNOWN
	}
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6947096159600273194L;
	public final static double MIN_WEDGE_FACTOR = 0.1;
	private double avLength;
	private Real2 centroid;
	private double wedgeFactor;
	private Real2 dashVector;
	private Type type;

	public ShortLineList() {
		super();
	}
	
	private void analyze() {
		avLength = 0;
		for (SVGChemLine line : this) {
			avLength += line.getLength();
		}
		avLength /= this.size();
		// overall vector
		Vector2 vector = new Vector2(this.get(size()-1).getMidPoint().subtract(
				this.get(0).getMidPoint()));
		type = null;
		for (int i = 0; i < size()-1; i++) {
			Vector2 interShortLine = new Vector2(this.get(i+1).getMidPoint().subtract(
					this.get(i).getMidPoint()));
			Angle angle = vector.getAngleMadeWith(interShortLine);
			angle.setRange(Range.SIGNED);
			if (angle.lessThan(0)) {
				angle.multiplyBy(-1.0);
			}
			if (angle.greaterThan(Math.PI * 0.25)) {
				if (type == null) {
					type = Type.HATCH;
				} else if (!type.equals(Type.HATCH)) {
					type = Type.UNKNOWN;
				}
			} else {
				if (type == null) {
					type = Type.DASH;
				} else if (!type.equals(Type.DASH)) {
					type = Type.UNKNOWN;
				}
			}
			// assume all are drawn in same direction
			if (vector.isAntiParallelTo(interShortLine, 0.1)) {
				throw new RuntimeException("bad hatch");
			}
		}
		wedgeFactor = 0.0;
		for (int i = 0; i < size(); i++) {
			double delta = this.get(i).getLength() - avLength;
			wedgeFactor += i * delta;
		}
		wedgeFactor /= (double) size();
		if (Math.abs(wedgeFactor) < MIN_WEDGE_FACTOR) {
			wedgeFactor = 0.0;
		}
		
		centroid = this.get(0).getMidPoint();
		for (int i = 1; i < size(); i++) {
			centroid = centroid.plus(this.get(i).getMidPoint());
		}
		centroid = centroid.multiplyBy(1./ (double) size());
		
		dashVector = this.get(0).getEuclidLine().getVector();
		for (int i = 1; i < size(); i++) {
			dashVector = dashVector.plus(this.get(i).getEuclidLine().getVector());
		}
		dashVector = dashVector.multiplyBy(1.0/(double) size());
		
	}
	
	public SVGChemLine getHatchLine() {
		SVGLine line = new SVGLine(this.get(0).getMidPoint(), this.get(size()-1).getMidPoint());
		SVGChemLine hatchLine = null;
		ParentNode parent = this.get(0).getParent();
		try {
			hatchLine = new SVGChemLine(line);
//			hatchLine.setStrokeWidth(1.5);
//			hatchLine.setStroke("#00ff00");
//			parent.appendChild(hatchLine);
//			hatchLine.setOrder(CMLBond.SINGLE_S);
		} catch (RuntimeException e) {
			System.err.println("bad hatch line");
		}
		
		SVGChemPath path = new SVGChemPath();
		StringBuilder sb = new StringBuilder("M ");
		if (Math.abs(wedgeFactor) < MIN_WEDGE_FACTOR) {
			Line2 line21 = this.get(0).getEuclidLine();
			SVGChemPath.appendPointToD(sb, line21.getFrom());
			sb.append(" L ");
			SVGChemPath.appendPointToD(sb, line21.getTo());
			Line2 line22 = this.get(size()-1).getEuclidLine();
			SVGChemPath.appendPointToD(sb, line22.getTo());
			SVGChemPath.appendPointToD(sb, line22.getFrom());
			SVGChemPath.appendPointToD(sb, line21.getFrom());
			hatchLine.setSVGClassName("parallel");
		} else {
			hatchLine.setSVGClassName("pointed");
			int line1 = 0;
			int line2 = size()-1;
			
			if (wedgeFactor < -MIN_WEDGE_FACTOR) {
				hatchLine.setSVGClassName("antipointed");
				line2 = 0;
				line1 = size()-1;
			}
			Line2 line21 = this.get(line1).getEuclidLine();
			SVGChemPath.appendPointToD(sb, line21.getMidPoint());
			sb.append(" L ");
			Line2 line22 = this.get(line2).getEuclidLine();
			SVGChemPath.appendPointToD(sb, line22.getTo());
			SVGChemPath.appendPointToD(sb, line22.getFrom());
			SVGChemPath.appendPointToD(sb, line21.getMidPoint());
		}
		path.setDString(sb.toString());
		path.setFill("#aaaaff");
		path.setStroke("#000000");
		path.setOpacity(0.2);
		path.createEdges();
		path.getVertexR2Array();
		parent.appendChild(path);
		
		for (SVGChemLine linex : this) {
			linex.detach();
		}
		
		return hatchLine;
	}

	public static void makeHatches(SVG2CMLTool svg2CmlTool) {
		svg2CmlTool.binLines();
		List<SVGChemLine> shortLines = svg2CmlTool.getShortLines();
		if (shortLines.size() > 0) {
			List<ShortLineList> hatchList = new ArrayList<ShortLineList>();
			// assume lines are ordered...
			ShortLineList hatch = new ShortLineList();
			hatchList.add(hatch);
			SVGChemLine currentLine = shortLines.get(0);
			SVGElement parent = (SVGElement) currentLine.getParent();
			hatch.add(currentLine);
			// at present simply assume sets of short bonds close to each other
			for (int i = 1; i < shortLines.size(); i++) {
				SVGChemLine line = shortLines.get(i);
				double interShortBondLength = currentLine.getMidPoint().getDistance(line.getMidPoint());
				if (interShortBondLength > SVG2CMLTool.INTER_WEDGE_DIST_RATIO*svg2CmlTool.getAverageLength()) {
					hatch = new ShortLineList();
					hatchList.add(hatch);
				}
				hatch.add(line);
				currentLine = line;
			}
			for (ShortLineList hatchx : hatchList) {
				if (hatchx.size() > 2) {
					hatchx.analyze();
					SVGChemLine hatchLine = hatchx.getHatchLine();
//					LOG.debug("HHHHHHHHHH  "+hatchx.size());
					hatchLine.setOrder(CMLBond.SINGLE_S);
					hatchLine.setOpacity(SVG2CMLTool.HATCH_BOND_OPACITY);
					hatchLine.detach(); // in case has parent
					parent.appendChild(hatchLine);
					for (SVGChemLine line : hatchx) {
						line.detachPath(svg2CmlTool.getSvgChem());
						line.detach();
					}
//					hatchLine.setStrokeWidth(5.);
				} else if (hatchx.size() == 2) {
					for (SVGChemLine linex : hatchx) {
						linex.setStrokeWidth(5.0);
					}
				} else {
					SVGChemLine linex = hatchx.get(0);
					linex.setStrokeWidth(8.0);
				}
			}
			svg2CmlTool.calculateAverageLength(SVGChemLine.getLineList(svg2CmlTool.getSvgChem()));
		}
	}
}
