package org.xmlcml.cml.converters.graphics.svg.elements;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.GraphicsConverterTool;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.HasStereoBondType;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVG2CMLTool;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Angle.Range;

/** analyzes line as possible bond
 * 
 * @author pm286
 *
 */
public class SVGChemLine extends SVGLine implements SVGChemElement, Comparable<SVGChemLine>, HasStereoBondType {
	private static Logger LOG = Logger.getLogger(SVGChemLine.class);

	public enum StereoBondType {
		HATCH,
		SOLID,
		WEDGE,
		WIGGLE,
	}
	private static double SHORT_BOND_LENGTH_RATIO = 0.5;
	private static double LONG_BOND_LENGTH_RATIO = 1.6;
	private static double ATOM_DOT_RADIUS = 0.8;
	private double x1 = Double.NaN;
	private double x2 = Double.NaN;
	private double y1 = Double.NaN;
	private double y2 = Double.NaN;
	private double length = Double.NaN;
	private Line2 line2;
	private Real2 midPoint;
	private String order;
	private CMLAtom[] atoms = new CMLAtom[2];
	private CMLBond bond;
	private StereoBondType bondType;
	private SVGCircle[] dots = new SVGCircle[2];

	public SVGChemLine() {
		init();
	}
	
	protected void init() {
		SVGLine.setDefaultStyle(this);
	}
	public SVGChemLine(SVGLine line) {
		this();
		SVGChem.deepCopy(line, this);
		getLength();
		getMidPoint();
	}
	
	/**
	 * @return
	 */
	public double getX1() {
		if (Double.isNaN(x1)) {
			x1 = new Double(this.getAttributeValue("x1")).doubleValue();
		}
		return x1;
	}
	
	/**
	 * @return
	 */
	public double getX2() {
		if (Double.isNaN(x2)) {
			x2 = new Double(this.getAttributeValue("x2")).doubleValue();
		}
		return x2;
	}
	
	/**
	 * @return
	 */
	public double getY1() {
		if (Double.isNaN(y1)) {
			y1 = new Double(this.getAttributeValue("y1")).doubleValue();
		}
		return y1;
	}
	
	/**
	 * @return
	 */
	public double getY2() {
		if (Double.isNaN(y2)) {
			y2 = new Double(this.getAttributeValue("y2")).doubleValue();
		}
		return y2;
	}

	/**
	 * 
	 * @return line
	 */
	public org.xmlcml.euclid.Line2 getEuclidLine() {
		if (line2 == null) {
			line2 = new Line2(getXY(0), getXY(1));
		}
		return line2;
	}
	
	/** set line and add coordinates to super.
	 * 
	 * @param line2
	 */
	public void setLine2(org.xmlcml.euclid.Line2 line2) {
		this.line2 = line2;
		this.setXY(line2.getXY(0), 0);
		this.setXY(line2.getXY(1), 1);
	}
	
	/**
	 * 
	 * @return length of line
	 */
	public double getLength() {
		if (Double.isNaN(length)) {
//			getLine2();
			getEuclidLine();
			length = line2.getLength();
		}
		return length;
	}

	public Real2 getMidPoint() {
		if (midPoint == null) {
			getLine2();
			midPoint = line2.getMidPoint();
		}
		return midPoint;
	}
	
	public void setStereoBondType(StereoBondType type) {
		this.bondType = type;
	}
	
	public StereoBondType getStereoBondType() {
		return this.bondType;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public static List<SVGChemLine> getLineList(SVGChemElement element) {
		Nodes nodes = ((Element) element).query(".//svg:line", SVG_XPATH);
		List<SVGChemLine> lineList = new ArrayList<SVGChemLine>();
		for (int i = 0; i < nodes.size(); i++) {
			lineList.add((SVGChemLine) nodes.get(i));
		}
		return lineList;
	}
	
	public int compareTo(SVGChemLine line) {
		double dd = this.getLength() - line.getLength();
		int result = 0;
		if (dd > 0) {
			result = 1;
		} else if (dd < 0) {
			result = -1;
		}
		return result;
	}
	
	public boolean isHorizontal(double eps) {
		return (Math.abs(y1 - y2) < eps);
	}
	
	public boolean isVertical(double eps) {
		return (Math.abs(x1 - x2) < eps);
	}
	
	public void colorBondByLength(double averageLength) {
		if (isLong(averageLength)) {
			setStroke(SVG2CMLTool.LONG_BOND_COLOR);
//			setStrokeWidth(10.);
		} else if (isShort(averageLength)) {
			setStroke(SVG2CMLTool.SHORT_BOND_COLOR);
//			setStrokeWidth(10.);
		}
	}
	
	public boolean isLong(double averageLength) {
		getLength();
		return !Double.isNaN(averageLength) && length > LONG_BOND_LENGTH_RATIO*averageLength;
	}
	
	public boolean isShort(double averageLength) {
		getLength();
		return !Double.isNaN(averageLength) && 
		    length < SHORT_BOND_LENGTH_RATIO*averageLength;
	}
	
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
		this.addAttribute(new Attribute("order", order));
		this.setStroke(SVG2CMLTool.BOND_PROCESSED);
		if (CMLBond.SINGLE.equals(order)) {
			this.setStrokeWidth(SVG2CMLTool.SINGLE_BOND_WIDTH);
		} else if (CMLBond.DOUBLE.equals(order)) {
			this.setStrokeWidth(SVG2CMLTool.DOUBLE_BOND_WIDTH);
		} else if (CMLBond.TRIPLE.equals(order)) {
			this.setStrokeWidth(SVG2CMLTool.TRIPLE_BOND_WIDTH);
		}

	}
	public CMLAtom[] getAtoms() {
		return atoms;
	}
	public void setAtoms(CMLAtom[] atoms) {
		this.atoms = atoms;
	}
	
	public CMLBond getOrCreateBond(List<CMLAtom> atomList, double averageLength) {
		if (order == null) {
			order = CMLBond.SINGLE;
		}
		// try to find atoms
		if (atoms[0] == null) {
			atoms[0] = findNearestAtomIfNull(line2.getXY(0), atomList, 0.15*averageLength);
		}
		if (atoms[1] == null) {
			atoms[1] = findNearestAtomIfNull(line2.getXY(1), atomList, 3.0);
		}
		if (atoms[0] != null && atoms[1] != null) {
			if (atoms[0].equals(atoms[1])) {
				LOG.debug("Two identical atoms in bond: "+atoms[0].getId()+"/"+line2.getXY(0)+"/"+line2.getXY(1));
			} else {
				bond = new CMLBond(atoms[0], atoms[1], order);
			}
		} else {
			SVGChemLine.debugLine("BOND STILL NULL ", this);
		}
		return bond;
	}
	
	private CMLAtom findNearestAtomIfNull(Real2 xy2, List<CMLAtom> atomList, double maxdist) {
		double distmin = Double.MAX_VALUE;
		CMLAtom nearest = null;
		for (CMLAtom atom1 : atomList) {
			double d = atom1.getXY2().getDistance(xy2);
			if (d < maxdist && d < distmin) {
				distmin = d;
				nearest = atom1;
			}
		}
		return nearest;
	}
	
	public void detachPath(SVGChemElement svgChem) {
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
		for (SVGChemPath path : pathList) {
			if (path.encloses(this)) {
				path.detach();
			}
		}
	}
	
	public String toBond() {
		String s = "";
		String a0 = (atoms == null || atoms[0] == null) ? null : atoms[0].getId();
		s += a0+" ";
		s += order+" ";
		String a1 = (atoms == null || atoms[1] == null) ? null : atoms[1].getId();
		s += a1;
		return s;
	}

	public String getCoordinates() {
		Line2 line2 = this.getEuclidLine();
		return line2.getXY(0)+"/"+line2.getXY(1);
	}
	
    public static double calculateAverageLength(List<SVGChemLine> lineList, double currentAverageLength) {
		double av  = 0.0;
		int count = 0;
    	for (SVGChemLine line : lineList) {
//    		double l = line.getLength();
//    		LOG.debug("LL "+l);
    		if (line.isShort(currentAverageLength)) {
//    			LOG.debug("SKIPPED SHORT: "+line);
    		} else if (line.isLong(currentAverageLength)) { 
    		} else {
				double length = line.getEuclidLine().getLength();
				av += length;
				count++;
    		}
    	}
    	av /= (double) count;
//    	LOG.debug("AVVVVVVVV "+averageLength);
    	return av;
//    	averageLength = av;
	}
    
	public String getDebug() {
		return this.getCoordinates()+" ... "+this.toBond();
	}

	public void closeShortGapsBetweenLineEndAndGroups(
			List<SVGChemText> groups, int serial, List<CMLAtom> atomList, SVG2CMLTool svg2CmlTool) {
		if (getAtoms()[serial] == null) {
			Real2 xy = getXY(serial);
			for (SVGChemText group : groups) {
				if (!joinAnchor(serial, xy, group, group.getLeftAnchorXY(), atomList, svg2CmlTool)) {
					joinAnchor(serial, xy, group, group.getRightAnchorXY(), atomList, svg2CmlTool);
				}
			}
		}
	}

	private boolean joinAnchor(int serial, Real2 xy, 
			SVGChemText group, Real2 anchorXY, List<CMLAtom> atomList, SVG2CMLTool svg2CmlTool) {
		SVGChemSVG svgChem = svg2CmlTool.getSvgChem();
		svg2CmlTool.setAverageLength(SVGChemLine.calculateAverageLength(SVGChemLine.getLineList(svgChem), svg2CmlTool.getAverageLength()));
		boolean join = false;
		if (anchorXY != null) {
			double dist = anchorXY.getDistance(xy);
			if (dist < SVG2CMLTool.INTER_LINE_JOIN_RATIO*svg2CmlTool.getAverageLength()) {
				setXY(anchorXY, serial);
				join = true;
				atoms[serial] = group.getAtom();
				this.setStroke("pink");
				this.setStrokeWidth(3.0);
				group.setFill("green");
				for (CMLAtom atom : atomList) {
					Real2 atomXY2 = atom.getXY2();
					Real2 lineXY2 = this.getXY(1-serial);
					dist = atomXY2.getDistance(lineXY2);
					if (dist < SVG2CMLTool.INTER_LINE_JOIN_RATIO*svg2CmlTool.getAverageLength()) {
						setXY(atomXY2, 1-serial);
						setFill("77ff00");
						break;
					}
				}
			}
		}
		return join;
	}

	/** moves movableEnd to create line of target length.
	 * 
	 * @param targetLength
	 * @param movableEnd
	 */
	public void extendLineToAverageLength(double targetLength, int moveableEnd) {
		Line2 line2 = this.getEuclidLine();
		Real2 moveXY = line2.getXY(moveableEnd);
		Real2 staticXY = line2.getXY(1-moveableEnd);
		Real2 vector = moveXY.subtract(staticXY);
		double length = vector.getLength();
		vector = vector.multiplyBy(targetLength / length);
		Real2 newXY = staticXY.plus(vector);
		line2.setXY(newXY, moveableEnd);
		this.setLine2(line2);
	}
	
	public void joinToCloseAnchor(int movableEnd, SVG2CMLTool svg2CmlTool) {
		double averageLength = svg2CmlTool.getAverageLength();
		Real2 xy = this.getXY(movableEnd);
		List<SVGChemText> groups = SVGChemText.getTextList(svg2CmlTool.getSvgChem());
		for (SVGChemText group : groups) {
//			CMLAtom atom = group.getAtom();
			Real2 leftAnchor = group.getLeftAnchorXY();
			double distL = xy.getDistance(leftAnchor);
			Real2 rightAnchor = group.getRightAnchorXY();
			double distR = xy.getDistance(rightAnchor);
			
			Real2 anchorXY = null;
			if (Double.isNaN(distR) || distL < distR) {
				anchorXY = leftAnchor;
			} else {
				anchorXY = rightAnchor;
			}
			if (anchorXY != null) {
				if (anchorXY.getDistance(xy) < SVG2CMLTool.EXTENDED_JOIN_RATIO*averageLength) {
					this.setXY(anchorXY, movableEnd);
					this.detachPath(svg2CmlTool.getSvgChem());
					this.getAtoms()[movableEnd] = group.getAtom();
					if (this.getOrder() == null) {
						this.setOrder(CMLBond.SINGLE);
					} else {
						// order and width already set
					}
//					this.setStrokeWidth(10.);
//					this.setStroke("yellow");
				}
			}
		}
	}
	
    public void clickToAtom(int end) {
    	CMLAtom atom = this.getAtoms()[end];
    	if (atom != null) {
    		this.setXY(atom.getXY2(), end);
    		if (dots[end] == null) {
    			dots[end] = new SVGCircle();
    			dots[end].setRad(ATOM_DOT_RADIUS);
    			dots[end].setFill("black");
    		}
    		dots[end].detach();
    		this.getParent().appendChild(dots[end]);
    		Real2 mid = this.getMidPoint();
    		Real2 xy = mid.getMidPoint(this.getXY(end));
    		dots[end].setXY(xy);
    	}
    }
    

	public Angle getAngleMadeWith(SVGChemLine line) {
		Angle angle = null;
		if (line != null) {
			Vector2 lineV = new Vector2(line.getEuclidLine().getVector().getUnitVector());
			Vector2 thisV = new Vector2(this.getEuclidLine().getVector().getUnitVector());
			angle = lineV.getAngleMadeWith(thisV);
		}
		return angle;
	}
	
	/**
	 * 
	 * @param line
	 * @param eps tolerance in radians
	 * @return
	 */
	public boolean isParallelOrAntiparallel(SVGChemLine line, double eps) {
		Angle angle = this.getAngleMadeWith(line);
		angle.setRange(Range.SIGNED);
		double angleR = Math.abs(angle.getRadian());
		return angleR < eps || Math.PI < angleR + eps;
	}
	
	public SVGChemLine makeDouble(SVGChemLine currentLine, SVGChemElement svgChem, SVG2CMLTool svg2CmlTool) {
		SVGChemLine oldLine = null;
		double averageLength = svg2CmlTool.getAverageLength();
		double delta = getLength() - currentLine.getLength();
		if (Math.abs(delta) > SVG2CMLTool.DOUBLE_BOND_DELTA_RATIO*averageLength) {
			// choose which line to keep
			if (delta > 0) {
				// keep this
				this.detachPath(svgChem);
				this.setOrder(CMLBond.DOUBLE);
				this.setStroke("#ff00ff");
				currentLine.detachPath(svgChem);
				currentLine.addAttribute(new Attribute("detach", "true"));
				oldLine = currentLine;
			} else {
				currentLine.detachPath(svgChem);
				currentLine.setOrder(CMLBond.DOUBLE);
				currentLine.setStroke("cyan");
				this.detachPath(svgChem);
				this.addAttribute(new Attribute("detach", "true"));
				oldLine = this;
			}
		} else {
			SVGChemLine doubleLine = this.createAverageLineAndAppend(currentLine);
			doubleLine.setStrokeWidth(2.0);
			doubleLine.setOrder(CMLBond.DOUBLE);
			doubleLine.setStroke("#ffff00");
			doubleLine.addAttribute(new Attribute("detach", "true"));
			currentLine.detachPath(svgChem);
//			currentLine.detach();
			this.detachPath(svgChem);
//			this.detach();
			oldLine = currentLine;
			oldLine.addAttribute(new Attribute("average", "true"));
			oldLine.addAttribute(new Attribute("detach", "true"));
		}
		return oldLine;
	}

	public void  makeTriple(SVGChemLine currentLine, SVGChemLine oldLine, GraphicsConverterTool svg2CmlTool) {
//		double oldCurrDist = currentLine.getMidPoint().getDistance(oldLine.getMidPoint());
//		double thisCurrDist = currentLine.getMidPoint().getDistance(this.getMidPoint());
//		double thisOldDist = oldLine.getMidPoint().getDistance(this.getMidPoint());
//		LOG.debug("OLD-CURR THIS-CURR THIS-OLD "+oldCurrDist+"/"+thisCurrDist+"/"+thisOldDist);
		currentLine.setOrder(CMLBond.TRIPLE);
		currentLine.setStroke("#0000ff");
		currentLine.setOpacity(0.3);
		// replace current line
		currentLine.detach();
		this.getParent().appendChild(currentLine);
		oldLine.detach();
		oldLine.detachPath(svg2CmlTool.getSvgChem());
		this.detach();
		this.detachPath(svg2CmlTool.getSvgChem());
	}

	public SVGChemLine createAverageLineAndAppend(SVGChemLine line) {
		SVGChemLine midLine = null;
		try {
			Real2 to = line.getEuclidLine().getTo().plus(getEuclidLine().getTo()).multiplyBy(0.5);
			Real2 from = line.getEuclidLine().getFrom().plus(getEuclidLine().getFrom()).multiplyBy(0.5);
			midLine = new SVGChemLine(new SVGLine(to, from));
			midLine.setStroke("#777777");
			getParent().appendChild(midLine);
		} catch (NullPointerException npe) {
			System.err.println("Unexplained Null pointer in averageLine");
		}
		return midLine;
	}

	public static void debugLine(String msg, SVGChemLine line) {
		LOG.debug(msg);
		CMLAtom.debugAtom("L0 ",line.getAtoms()[0]);
		CMLAtom.debugAtom("L1 ",line.getAtoms()[1]);
	}
}
