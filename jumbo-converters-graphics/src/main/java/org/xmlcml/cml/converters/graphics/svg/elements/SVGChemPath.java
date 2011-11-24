package org.xmlcml.cml.converters.graphics.svg.elements;


import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine.StereoBondType;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.HasStereoBondType;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVG2CMLTool;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Angle.Range;

public class SVGChemPath extends SVGPath implements SVGChemElement, HasStereoBondType {
	private static Logger LOG = Logger.getLogger(SVGChemPath.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public enum PathType {
		SHORT_SEGMENTS;
	}
//	public static StyleBundle DEFAULT_STYLE = new StyleBundle(
//			"#ffff00", // fill
//			"#ff0000", // stroke
//			0.15, // strokeWidth
//			"",
//			1.0, // fontSize
//			"",
//			0.3
//			);
	public final static int MINWIGGLE = 20;
	public final static double LONG_EDGE_MAX = 1.4;
	public final static double LONG_EDGE_MIN = 0.4;
	
	
	private Real2Array vertexR2Array;
	private List<Line2> edgeList;
	private List<Line2> distinctEdgeList;
	private Boolean closed = false;
	private List<Line2> longEdgeList;
	private boolean isBondLike;
	private boolean isWedgeLike;
	private List<Line2> arrowEdge;
	private boolean isArrowHeadLike;
	private Real2 arrowPoint;
	private Real2 arrowTail;
	private Vector2 arrowDirection;
	private Boolean isPoly;
	private StereoBondType stereoBondType;
	private PathType pathType;
	private List<Real2> distinctPointList;

	public SVGChemPath() {
		init();
	}
	
	protected void init() {
		SVGPath.setDefaultStyle(this);
		closed = null;
	}
	/**
	 * 
	 * @param path
	 */
	public SVGChemPath(SVGPath path) {
		this();
		SVGChem.deepCopy(path, this);
	}

	/**
	 * 
	 * @param element
	 * @return paths
	 */
	public static List<SVGChemPath> getPathList(SVGChemElement element) {
		Nodes nodes = ((Element) element).query(".//svg:path", SVG_XPATH);
		List<SVGChemPath> pathList = new ArrayList<SVGChemPath>();
		for (int i = 0; i < nodes.size(); i++) {
			pathList.add((SVGChemPath) nodes.get(i));
		}
		return pathList;
	}

	/**
	 * tests whether > 2 distinct points (i.e. separated more than epsilon
	 * need not be closed
	 * @return isClosed
	 */
	public boolean isClosedPolygon(double eps) {
		return isPolygon(eps) && isClosed();
	}
	
	/**
	 * tests whether > 2 distinct points (i.e. separated more than epsilon
	 * need not be closed
	 * @return isOpen
	 */
	public boolean isOpenPolygon(double eps) {
		return isPolygon(eps) && !isClosed();
	}

	/**
	 * tests whether > 2 distinct points (i.e. separated more than epsilon
	 * need not be closed
	 * @return isTriangle
	 */
	public boolean isTriangle(double eps, Angle theta) {
		return countDistinctPoints(eps, theta) == 3;
	}
	
	/**
	 * tests whether > 2 distinct points (i.e. separated more than epsilon
	 * need not be closed
	 * does not test for colinearity yet
	 * @return isPolygon
	 */
	public boolean isPolygon(double eps) {
		this.getVertexR2Array();
		if (isPoly == null) {
			isPoly = false;
			if (vertexR2Array != null) {
				double sum = 0.0;
				int npoints = 1;
				for (int i = 1; i < vertexR2Array.size(); i++) {
					double dist = vertexR2Array.elementAt(i).getDistance(
						vertexR2Array.elementAt(i-1));
					if (dist < eps) {
						continue;
					}
					sum += dist;
					npoints++;
				}
				isPoly = sum > eps && npoints > 2;
			}
		}
		return isPoly;
	}

	
	/**
	 * tests whether 2 distinct points (i.e. separated more than epsilon)
	 * does not test for colinearity yet so 1,2 2,3 3,4 is a polygon, not a line
	 * @return isSingleLine
	 */
	public boolean isSingleLine(double eps, Angle theta) {
		this.getVertexR2Array();
		return countDistinctPoints(eps, theta) == 2;
	}

	/**
	 * distinct points are neighbours and separated by > eps
	 * includes return distance to M
	 * @param eps
	 * @return points
	 */
	public int countDistinctPoints(double eps) {
		int np = 0;
		this.getVertexR2Array();
		if (vertexR2Array != null) {
			int size = vertexR2Array.size();
			if (size == 0) {
				// np = 0;
			} else if (size == 1) {
				np = 1;
			} else {
				for (int i = 0; i < size; i++) {
					double dist = vertexR2Array.elementAt(i).getDistance(
						vertexR2Array.elementAt((i+1) % size));
					if (dist > eps) {
						np++;
					}
				}
				if (np == 0) {
					np = 1;
				}
			}
		}
		return np;
	}

	/**
	 * distinct points are neighbours and separated by > eps
	 * includes return distance to M
	 * @param eps min distance for points to be distinct
	 * @param deltaTheta minimum deviation from linearity
	 * @return points
	 */
	public int countDistinctPoints(double eps, Angle deltaTheta) {
		int np = 0;
		this.getVertexR2Array();
		if (vertexR2Array != null) {
			int size = vertexR2Array.size();
			if (size == 0) {
				// np = 0;
			} else if (size == 1) {
				np = 1;
			} else {
				for (int i = 0; i < size; i++) {
					Real2 v1 = vertexR2Array.elementAt((i+1) % size).subtract(vertexR2Array.elementAt(i));
					double dist = v1.getLength();
					if (dist > eps) {
						if (deltaTheta == null || i == 0) {
							np++;
						} else {
							double delta = Math.abs(deltaTheta.getRadian());
							int ii = (size+i-1) % size;
							Real2 v0 = vertexR2Array.elementAt(i).subtract(vertexR2Array.elementAt(ii));
							Angle angle = new Vector2(v0).getAngleMadeWith(new Vector2(v1));
							if (Math.abs(angle.getRadian()) > delta) {
								np++;
							}
						}
					}
				}
				if (np == 0) {
					np = 1;
				}
			}
		}
		return np;
	}

	/**
	 * distinct points are neighbours and separated by > eps
	 * includes return distance to M
	 * @param eps min distance for points to be distinct
	 * @param deltaTheta minimum deviation from linearity
	 * @return points
	 */
	public List<Real2> extractDistinctPoints(double eps, Angle deltaTheta) {
		distinctPointList = new ArrayList<Real2>();
		this.getVertexR2Array();
		if (vertexR2Array != null) {
			int size = vertexR2Array.size();
			if (size == 0) {
				// no points
			} else {
				distinctPointList.add(vertexR2Array.get(0));
				Real2 start = vertexR2Array.elementAt(0);
				for (int i = 1; i < size; i++) {
					int i0 = (i-1) % size;
					int i1 = (i+1) % size;
					Real2 current = vertexR2Array.elementAt(i);
					Real2 prev = vertexR2Array.elementAt(i0);
					Real2 next = vertexR2Array.elementAt(i1);
					Real2 vPrev = current.subtract(prev);
					double dist = vPrev.getLength();
					if (dist > eps) {
						// closed?
						if (start.getDistance(current) < eps) {
							// overlaps start, skip
						} else if (deltaTheta == null) {
							distinctPointList.add(current);
						} else {
							double delta = Math.abs(deltaTheta.getRadian());
							Real2 vNext = next.subtract(current);
							Angle angle = new Vector2(vPrev).getAngleMadeWith(new Vector2(vNext));
							if (Math.abs(angle.getRadian()) > delta) {
								distinctPointList.add(current);
							}
						}
					}
				}
			}
		}
		return distinctPointList;
	}

	@Deprecated
	public boolean isSingleLine() {
		this.getVertexR2Array();
		return vertexR2Array != null && vertexR2Array.size() == 2;
	}

	/**
	 * 
	 * @param eps
	 * @return isZero
	 */
	public boolean isZeroLength(double eps) {
		return countDistinctPoints(eps) < 2;
	}
	
	/**
	 * 
	 * @param averageLength
	 * @return edges
	 */
	public List<Line2> getLongEdges(double averageLength) {
		if (edgeList != null) {
			addFinalEdge();
			longEdgeList = new ArrayList<Line2>();
			for (Line2 edge : edgeList) {
				double length = edge.getLength();
				if (length < averageLength*LONG_EDGE_MAX && length > averageLength * LONG_EDGE_MIN) {
					longEdgeList.add(edge);
				}
			}
			isBondLike = false;
			isWedgeLike = false;
			if (longEdgeList.size() == 2) {
				Angle angle = longEdgeList.get(0).getVector().getAngleMadeWith(
						longEdgeList.get(1).getVector());
				angle.setRange(Range.SIGNED);
				double angleR = Math.abs(angle.getRadian());
				if (angleR > SVG2CMLTool.PARALLEL_BOND_ANGLE) {
					isBondLike = true;
					this.setFill(SVG2CMLTool.BOND_LIKE_COLOR);
				} else if (angleR > 0.8*Math.PI) {
					isWedgeLike = true;
					this.setFill(SVG2CMLTool.WEDGE_LIKE_COLOR);
				}
			} else {
				this.setFill(SVG2CMLTool.UNKNOWN_COLOR);
//				for (Line2 line : longEdgeList) {
//					LOG.debug("LLLLLL "+line);
//				}
//				for (Line2 edge : edgeList) {
//					LOG.debug(edge);
//				}
//				LOG.debug("==================");
			}
		}
		return longEdgeList;
	}
	
	private void addFinalEdge() {
		try {
			Line2 finalEdge = new Line2(
					vertexR2Array.elementAt(vertexR2Array.size()-1), vertexR2Array.elementAt(0));
			edgeList.add(finalEdge);
		} catch (RuntimeException e) {
			// there may already be a final point...
		}
	}
	
	/**
	 * 
	 * @return isBondlike
	 */
	public boolean isBondLike() {
		return isBondLike;
	}

	// arrowhead with barbs
	public boolean isArrowHeadLike() {
		if (arrowEdge == null && edgeList != null && edgeList.size() == 4) {
			isArrowHeadLike = false;
			double avlength = 0.0;
			for (Line2 edge : edgeList) {
				avlength += edge.getLength();
			}
			avlength /= 4.0;
			arrowEdge = new ArrayList<Line2>();
			// get long edges
			for (Line2 edge : edgeList) {
				if (edge.getLength() > 1.5 * avlength) {
					arrowEdge.add(edge);
				}
			}
			Angle pointAngle = null;
			if (arrowEdge.size() == 2) {
				int edge0 = edgeList.indexOf(arrowEdge.get(0));
				int edge1 = edgeList.indexOf(arrowEdge.get(1));
				// swap to normalize order
				int delta1 = 1;
				int delta2 = 2;
				if((edge1+1) % 4 == edge0) {
					int temp = edge0;
					edge0 = edge1;
					edge1 = temp;
					delta1 = 2;
					delta2 = 1;
				}
				// do the long adges meet?
				if((edge0+1) % 4 == edge1) {
					pointAngle = arrowEdge.get(0).getAngleMadeWith(arrowEdge.get(1));
					arrowEdge.add(edgeList.get((edge1+delta1) % 4));
					arrowEdge.add(edgeList.get((edge1+delta2) % 4));
					Angle tailAngle = arrowEdge.get(2).getAngleMadeWith(arrowEdge.get(3));
					// is the tail angle concave?
					if (pointAngle.getRadian() * tailAngle.getRadian() > 0) {
						isArrowHeadLike = true;
						arrowPoint = arrowEdge.get(0).getIntersection(arrowEdge.get(1));
						arrowTail = arrowEdge.get(2).getIntersection(arrowEdge.get(3));
						arrowDirection = new Vector2(arrowPoint.subtract(arrowTail));
					}
				}
			}
		}
		return isArrowHeadLike;
	}
	/**
	 * 
	 * @return arry of vertices
	 */
	public Real2Array getVertexR2Array() {
		if (vertexR2Array == null) {
			String d = this.getAttributeValue("d");
			LOG.debug("VERTICES D "+d);
			if (d != null) {
				d = d.trim();
				if (d.startsWith("M")) {
					d = d.substring(1);
					int idx = d.indexOf("L");
					String m = (idx == -1) ? d.trim() : d.substring(0, idx).trim();
					m = m.replace(S_COMMA, S_SPACE);
					Real2 r2 = new Real2(m);
					vertexR2Array = new Real2Array();
					vertexR2Array.add(r2);
					LOG.trace("M "+m);
					if (idx != -1) {
						d = d.substring(idx).trim();
						LOG.trace("D "+d);
						while (d.length() > 0) {
							d = parseLandAddToVertexArray(d);
						}
					}
				}
			}
		}
		LOG.debug("VERTICES SIZE "+vertexR2Array.size()+" "+vertexR2Array);
		return vertexR2Array;
	}

	/**
	 * @param d
	 * @throws RuntimeException
	 * @throws EuclidRuntimeException
	 */
	private String parseLandAddToVertexArray(String d) throws RuntimeException,
			EuclidRuntimeException {
		LOG.trace("D>>>>>>"+d+"<<");
		if (d.length() == 0) {
			throw new RuntimeException("Zero length string");
		} else if (!d.substring(0, 1).toLowerCase().equals("l")) {
			throw new RuntimeException("BUG should start with L");
		}
		d = d.substring(1);
		LOG.trace("D-----"+d+"--");
		int idx = d.toUpperCase().indexOf("L");
		String l = null;
		if (idx != -1) {
			l = d.substring(0, idx).trim();
			d = d.substring(idx).trim();
		} else {
			l = d.trim();
			d = S_EMPTY;
		}
			
		// TODO fix variable syntax in SVG
		
		Real2Array real2Array1 = Real2Array.createFromPairs(l, S_COMMA+"|"+S_SPACE+"+");
		LOG.trace("RA1 "+real2Array1);
		vertexR2Array.add(real2Array1);
		LOG.trace("VERTICES SIZE "+vertexR2Array.size()+" "+vertexR2Array);
		LOG.trace("DDDDDD "+d);
		return d.trim();
	}
	
	/** requires >= 2 distinct points and final one
	 * overlaps start
	 * @param eps
	 * @param deltaTheta
	 * @return isClosed
	 */
	public boolean isClosed(double eps, Angle deltaTheta) {
		getVertexR2Array();
		boolean closed = false;
		int npoints = extractDistinctPoints(eps, deltaTheta).size();
		if (npoints > 1) {
		    double dist = vertexR2Array.get(0).getDistance
		    (vertexR2Array.get(vertexR2Array.size()-1));
		    closed = dist < eps;
		}
		return closed;
	}
	
	/** must have origin at sharp end of wedge.
	 * 
	 * @param eps
	 * @param deltaTheta
	 * @param wedgeFactor maximum ratio of short to long
	 * @return isWedge
	 */
	public boolean isWedge(double eps, Angle deltaTheta, double wedgeFactor) {
		boolean isWedge = false;
		List<Real2> points = extractDistinctPoints(eps, deltaTheta);
		isWedge = isWedgeAtStart(points, wedgeFactor);
		return isWedge;
	}

	/**
	 * @param wedgeFactor
	 * @param points
	 * @return isWedgeAtStart
	 */
	public static boolean isWedgeAtStart( 
			List<Real2> points, double wedgeFactor) {
		boolean isWedge = false;
		if (points.size() == 3) {
			Real2 pt1 = points.get(1);
			Real2 pt0 = points.get(0);
			Real2 pt2 = points.get(2);
			LOG.trace("RR "+pt1+"/"+pt0+"/"+pt2+"//"+pt1.getDistance(pt2)/pt1.getDistance(pt0));
			Angle wedgeAngle = Real2.getAngle(pt1, pt0, pt2);
			isWedge = Math.abs(wedgeAngle.getRadian()) < wedgeFactor;
		}
		return isWedge;
	}

	/** converts path to equivalent line.
	 * if child of node, replaces path with line
	 * @return line
	 */
	public SVGChemLine convertTwoPointPathToLine() {
		SVGChemLine chemLine = null;
		getVertexR2Array();
		if (isSingleLine()) {
			LOG.debug("VVV "+vertexR2Array);
			SVGLine line = new SVGLine(vertexR2Array.elementAt(0), vertexR2Array.elementAt(1));
			chemLine = new SVGChemLine(line);
			chemLine.copyAttributesFrom(this);
			chemLine.copyChildrenFrom(this);
			chemLine.removeAttribute(chemLine.getAttribute("d"));
			ParentNode parentNode = this.getParent();
			if (parentNode != null) {
				parentNode.replaceChild(this, chemLine);
			}
		}
		return chemLine;
	}

	/**
	 * 
	 * @param element
	 */
	public static void convertTwoPointPathToLines(SVGChemElement element) {
		List<SVGChemPath> pathList = getPathList(element);
		for (SVGChemPath path : pathList) {
			path.convertTwoPointPathToLine();
		}
	}
	
	public void createEdges() {
		if (edgeList == null) {
			getVertexR2Array();
			int size = vertexR2Array.size();
			edgeList = new ArrayList<Line2>();
			for (int i = 0; i < size-1; i++) {
				int j = (i + 1) % size;
				try {
					Line2 edge = new Line2(vertexR2Array.elementAt(i), vertexR2Array.elementAt(j));
					edgeList.add(edge);
				} catch (RuntimeException e) {
					// coincident points ignore
				}
			}
		}
	}
	
	public List<Line2> extractDistinctEdges(double eps, Angle deltaTheta) {
		if (distinctEdgeList == null) {
			List<Real2> pointList = extractDistinctPoints(eps, deltaTheta);
			int size = pointList.size();
			distinctEdgeList = new ArrayList<Line2>();
			if (pointList.size() > 1) {
				for (int i = 0; i < pointList.size(); i++) {
					int j = (i + 1) % size;
					Line2 edge = new Line2(pointList.get(i), pointList.get(j));
					distinctEdgeList.add(edge);
				}
			}
		}
		return distinctEdgeList;
	}
	
	public void tidyDAttribute() {
		if (vertexR2Array.size() > 2) {
			StringBuilder sb = new StringBuilder();
			sb.append("M ");
			appendPoint(sb, 0);
			sb.append(" L");
			for (int i = 1; i < vertexR2Array.size(); i++) {
				sb.append(S_SPACE);
				appendPoint(sb, i);
			}
			this.setDString(sb.toString());
			this.setStroke("#00ff00");
			this.setFill("none");
			this.setStrokeWidth(0.5);
		}
	}
	
	public boolean encloses(Real2 point) {
		boolean contains = false;
		if (vertexR2Array != null) {
			Real2Vector r2v = new Real2Vector(vertexR2Array);
			contains = r2v.encloses(point);
		}
		return contains;
	}
	
	public boolean encloses(SVGLine line) {
		return encloses(line.getXY(0)) && encloses(line.getXY(1));
	}
	
	private void appendPoint(StringBuilder sb, int i) {
		appendPointToD(sb, vertexR2Array.elementAt(i));
	}
	public static void appendPointToD(StringBuilder sb, Real2 xy) {
		sb.append(xy.getX()+S_COMMA+xy.getY()+S_SPACE);
	}
	public boolean isClosed() {
		return closed;
	}
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	public List<Line2> getEdgeList() {
		return edgeList;
	}
	public void setEdgeList(List<Line2> edgeList) {
		this.edgeList = edgeList;
	}
	public void setPoints(Real2Array points) {
		this.vertexR2Array = points;
	}

	@SuppressWarnings("unused")
	private static List<SVGChemPath> createWiggles(List<SVGChemPath> pathList, double pathLength) {
		SVGChemPath currentPath = null;
//		@SuppressWarnings("unused")
		SVGElement parent = null;
		List<SVGChemPath> wiggleList = new ArrayList<SVGChemPath>();
    	for (int i = 0; i < pathList.size(); i++) {
			SVGChemPath nextPath = pathList.get(i); 
			if (currentPath == null) {
				parent = (SVGElement) nextPath.getParent();
			}
			Real2Array nextVertices = nextPath.getVertexR2Array();
			LOG.debug(nextVertices.size());
    		if (nextVertices.size() == 2) {
    			double length = nextVertices.elementAt(0).getDistance(nextVertices.elementAt(1));
    			LOG.debug("LLLL "+length);
    			if (length > pathLength) {
    				// don't join long lines
    			} else if (currentPath == null) {
    				currentPath = nextPath;
    			} else {
    				Real2Array vertices = currentPath.getVertexR2Array();
    				double dist = vertices.elementAt(vertices.size()-1).
    				    getDistance(nextVertices.elementAt(0));
				    if (dist < SVG2CMLTool.INTER_PATH_DIST) {
				    	vertices.add(nextVertices.elementAt(1));
//				    	LOG.debug("Added wiggle"+vertices.size());
				    	nextPath.detach();
				    } else {
				    	currentPath.tidyDAttribute();
//				    	LOG.debug("ended");
				    	currentPath = nextPath;
				    }
    			}
    		} else {
    			if (currentPath != null) {
			    	currentPath.tidyDAttribute();
			    	// can this be a wiggle?
			    	if (currentPath.getVertexR2Array().size() > MINWIGGLE) {
			    		wiggleList.add(currentPath);
//			    		parent.appendChild(currentPath);
			    	}
    			}
    			currentPath = null;
    		}
    	}
    	return wiggleList;
	}
	
	public static List<SVGChemPath> concatenateShortTouchingLinesIntoPaths(double maxLength, double maxSeparation, SVGChemSVG svgChem) {
		// assume they are ordered...
		List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
		SVGChemLine currentLine = null;
		SVGChemPath currentPath = null;
		List<SVGChemPath> pathList = new ArrayList<SVGChemPath>();
		ParentNode parent = null;
    	for (SVGChemLine chemLine : lineList) {
    		if (parent == null) {
    			parent = chemLine.getParent();
    		}
    		if (chemLine.getLength() > maxLength) {
				currentPath = tidy(currentPath, pathList);
    			// skip
    		} else if (currentLine != null) {
    			double dist = currentLine.getXY(1).getDistance(chemLine.getXY(0));
    			if (dist < maxSeparation) {
    				if (currentPath == null) {
    					currentPath = SVGChemPath.createPath(currentLine);
    					currentLine.detach();
    				}
					currentPath.addLine(chemLine);
					chemLine.detach();
    			} else {
    				currentPath = tidy(currentPath, pathList);
    			}
    		}
			currentLine = chemLine;
    	}
		currentPath = tidy(currentPath, pathList);
    	for (SVGChemPath path : pathList) {
    		parent.appendChild(path);
    		path.setPathType(PathType.SHORT_SEGMENTS);
    	}
    	return pathList;
	}

	private static SVGChemPath tidy(SVGChemPath currentPath,
			List<SVGChemPath> pathList) {
		if (currentPath != null) {
			pathList.add(currentPath);
		}
		return null;
	}
	
	public void setStereoBondType(StereoBondType type) {
		this.stereoBondType = type;
	}
	
	public StereoBondType getStereoBondType() {
		return this.stereoBondType;
	}
	
	private static SVGChemPath createPath(SVGChemLine line) {
		SVGPath path = new SVGPath();
		path.setDString("M "+makeD(line, 0)+" L "+makeD(line, 1));
		SVGChemPath chemPath = new SVGChemPath(path);
		return chemPath;
	}
	
	private static String makeD(SVGChemLine line, int index) {
		return line.getXY(index).getX()+S_COMMA+line.getXY(index).getY();
	}
	
	private void addLine(SVGChemLine line) {
		this.setDString(this.getDString()+" "+SVGChemPath.makeD(line, 1));
	}

	public boolean isWedgeLike() {
		return isWedgeLike;
	}
	
	public Vector2 getArrowDirection() {
		return arrowDirection;
	}
	
	public Vector2 getArrowDirection(double eps, Angle doubleTheta) {
		throw new RuntimeException("not yet implemented");
//		return arrowDirection;
	}
	public void setArrowTail(Real2 arrowTail) {
		this.arrowTail = arrowTail;
	}
	public Real2 getArrowPoint() {
		return arrowPoint;
	}
	public Real2 getArrowTail() {
		return arrowTail;
	}
	public PathType getPathType() {
		return pathType;
	}

	public void setPathType(PathType pathType) {
		this.pathType = pathType;
	}

	public void setVertexR2Array(Real2Array vertexR2Array) {
		this.vertexR2Array = vertexR2Array;
	}

	public List<Line2> getDistinctEdgeList() {
		return distinctEdgeList;
	}

	public List<Real2> getDistinctPointList() {
		return distinctPointList;
	}
}
