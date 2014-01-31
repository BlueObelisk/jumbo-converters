package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_LBRAK;
import static org.xmlcml.euclid.EuclidConstants.S_RBRAK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine.StereoBondType;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemPath;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemRect;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemSVG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemText;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
/**
 * 
 * @author pm286
 *
 */
public class SVG2CMLTool extends GraphicsConverterTool {

    private static Logger LOG = Logger.getLogger(SVG2CMLTool.class);
    
	public enum Directory {
		END,
		START,
	}
	
    static Level MYFINE = Level.TRACE;
    final static String INSUFF_DEFAULT = ".svg";
//    final static String OUTSUFF_DEFAULT = ".cml";
    final static String OUTSUFF_DEFAULT = ".svg";
    final static String PREFIX = "cml";
    
    public final static double INTER_WEDGE_DIST_RATIO = 0.4;
    public final static double INTER_LINE_JOIN_RATIO = 0.25;
    public final static double WIGGLE_RATIO = 0.15;
//    public final static double SHORT_LINE_RATIO = 0.23;
    public final static double INTER_PATH_DIST = 0.1;
    public final static double DOUBLE_SEPARATION_RATIO = 0.3;
    public final static double DOUBLE_BOND_DELTA_RATIO = 0.05;
    public final static double CARBON_FONT_SIZE = 6.0;
    public final static String CARBON_FILL = "black";
    public final static double MAXUNJOINEDFACTOR = 0.3;
    public final static double PARALLEL_FACTOR = 0.3;
    public final static String BOND_PROCESSED = "red";
    public final static double SINGLE_BOND_WIDTH = 1.0;
    public final static double DOUBLE_BOND_WIDTH = 2.0;
    public final static double TRIPLE_BOND_WIDTH = 3.0;
    public final static double HATCH_BOND_OPACITY = 0.3;
    public final static double EXTENDED_BOND_OPACITY = 0.3;
    public final static String EXTENDED_BOND_COLOR = "#77ff77";
    public final static double IMPLICIT_ATOM_OPACITY = 0.5;
    public final static String IMPLICIT_ATOM_COLOR = "blue";
    public final static double TERMINAL_CARBON_OPACITY = 0.5;
    public final static String TERMINAL_CARBON_COLOR = "pink";
    public final static double ELEMENT_OPACITY = 0.3;
    public final static String ELEMENT_COLOR = "orange";
    public final static double R_OPACITY = 0.3;
    public final static String R_COLOR = "cyan";
    public final static String SHORT_BOND_COLOR = "blue";
    public final static String LONG_BOND_COLOR = "green";
    public final static double MAX_DOUBLE_BOND_ANGLE_DEVIATION = 0.02;
    public final static double PARALLEL_BOND_ANGLE = Math.PI * 0.98;
    public final static double EXTENDED_JOIN_RATIO = 0.45;
    public final static String BOND_LIKE_COLOR = "#ff00ff";
    public final static String WEDGE_LIKE_COLOR = "#00ffff";
    public final static String UNKNOWN_COLOR = "#000000";

    public final static double MAX_WIGGLE_LENGTH = 3.0;
    public final static double MAX_WIGGLE_SEPARATION = 0.2;
    public final static double MIN_WIGGLE_SEGMENTS = 10;
    
	private SVGChemSVG svgChem;
	private List<SVGChemLine> shortLines;
	private List<SVGChemLine> normalLines;
	private List<SVGChemLine> longLines;
	
	private List<CMLAtom> atomList;
	private List<SVGChemText> unjoinedGroups;
	private List<SVGChemLine> unjoinedBonds;
	private double averageLength;
//	private List<SVGChemLine> untreatedBonds;
	private SVGChemRect newBoundingBox;
//	private Element summaryTable;
	private Map<String, Row> rowMap;
	private CMLFormula givenFormula;
	private Row row;
	private SVGText boxTitle;
	private String fileId;
	private CMLCml cmlCml;
	private CMLScaler cmlScaler;
	private Element svgElement;
	private boolean unjoinedBondsMissingOneOrBothAtoms = false;
	private boolean createListOfUnjoinedAtomsAndGroups = false;
	private boolean joinUnjoinedBondsExactlyGeometrically = false;
	private SVGChemSVG svg;
	
	private double deltay;
	private double deltayy;
	private double deltax;
	private double maxdeltax;
	private double deltax0;
	private double deltaxx;
	private int npict;

    public SVG2CMLTool(Element svgElement, String fileId) {
	    init();
	    this.fileId = fileId;
	    this.svgElement = svgElement;
	}
		  
    
    public CMLCml getCML() {
//    	CMLCml cml = null;
    	return cmlCml;
    }
	private void init() {
//	    super.init();
		clearVars();
	}
	
	private void clearVars() {
//		super.clearVars();
		svgChem = null;
		atomList = new ArrayList<CMLAtom>();
		newBoundingBox = null;
		averageLength = Double.NaN;
		deltax0 = 0.0;
		deltay = 0.0;
    	deltaxx = 150.0;
		deltayy = 100.0;
		deltax = 0.0;
		deltay = 0;
		maxdeltax = 590.;
		npict = 0;
	}

   @Override
    public void processSVG() {
		row = (rowMap == null) ? null : rowMap.get(fileId);
		if (row != null) {
			givenFormula = row.formula;
			LOG.debug(fileId+": mol"+row.molId+" ... "+
					((givenFormula==null) ? null : givenFormula.getConcise())+" ... "+row.name);
		} else {
			LOG.debug(fileId+": no row.............................");
		}
		SVGSVG newsvg = (SVGSVG) SVGElement.readAndCreateSVG(svgElement);
		svgChem = new SVGChemSVG(newsvg);
		processAfterParsing();
		
		boolean debug = true;
		if (debug) {
			CMLUtil.debug(newsvg, "SVG");
		}
    }
    
    
    
    protected void processAfterParsing() {
		svg = new SVGChemSVG(svgChem);
    	// create a bounding box (will be continuously adjusted)
		addBoundingBoxAndDisplay("start");
		
		// add edges to polygonal paths
		processPathsAndCreateEdges();
		
		// normalize 2-point paths into lines
    	convertTwoPointPathsToLines();
    	
    	SVGChemPath.concatenateShortTouchingLinesIntoPaths(MAX_WIGGLE_LENGTH, MAX_WIGGLE_SEPARATION, svgChem);
		addBoundingBoxAndDisplay("shortLines");
    	
		// process wiggly bonds
		createWiggles();
		
		// assume text is single characters
		processTextAndSingleCharactersAndAddBoxesAndScales();
		
		// generally all characters are single
		mergeSingleCharactersIntoTextStrings();
		
		// add chemistry to any textStrings, including groups and atoms
		interpretTextStrings();
		
    	extractAtomsFromTextAddToListAndNumber();
    	
		createWedgesFromShortLines();		
		
		createWedgesOrArrowsFromPaths();
		
		makeMultiples();
		
		addImplicitAtomsAndRecordSingleBonds();
		
		unjoinedBondsMissingOneOrBothAtoms = false;
		if (unjoinedBondsMissingOneOrBothAtoms) {
			getUnjoinedBondsMissingOneOrBothAtoms();
		}
		
		createListOfUnjoinedAtomsAndGroups = false;
		if (createListOfUnjoinedAtomsAndGroups) {
			createListOfUnjoinedAtomsAndGroups();
		}
		
		if (joinUnjoinedBondsExactlyGeometrically) {
			joinUnjoinedBondsExactlyGeometrically();
			createOrAdjustBoundingBox("after joined");	
			addSnapshotSVG(svg);
		}
		
		extendShortUnjoinedBondsToAverageLengthAndLookForAnchors();
		
		clickLineEndsToAnchors();
		
		boolean shortGaps = false;
		if (shortGaps) {
			closeShortGapsBetweenLinesAndGroups();
		}
		rescaleBonds();
		createOrAdjustBoundingBox("before terminal");
		addSnapshotSVG(svg);
		
		try {
			guessTerminalCarbons();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		analyzePaths();
		
		cmlCml = new CMLSVGHelper(this).createCML();
		ensureCMLScaler();
		cmlScaler.process(cmlCml);
		addBoundingBoxAndDisplay("after making CML");
		
		svgChem = svg;
    }


	/**
	 * 
	 */
	private void createWedgesFromShortLines() {
		ShortLineList.makeHatches(this);
		addBoundingBoxAndDisplay("hatches");
	}


	/**
	 * @param deltay
	 * @param svg
	 * @param title
	 * @return
	 */
	private void addBoundingBoxAndDisplay(String title) {
		createOrAdjustBoundingBox(title);		
		addSnapshotSVG(svg);
	}
    
	private void ensureCMLScaler() {
		if (this.cmlScaler == null) {
			cmlScaler = new CMLScaler();
		}
	}
	

	private void addSnapshotSVG(SVGChemSVG svg) {
		SVGElement g = new SVGG();
		svg.appendChild(g);
		Transform2 transform2 = new Transform2(new double[]{
				1.0, 0.0, deltax,
				0.0, 1.0, deltay,
				0.0, 0.0, 1.0}
				);
		g.setTransform(transform2);
		SVGChemG gNew = new SVGChemG(svgChem);
		g.appendChild(gNew);
		deltax += deltaxx;
		if (deltax > maxdeltax) {
			deltay += deltayy;
			deltax = deltax0;
		}
		npict++;
	}
	
	private Real2Range createOrAdjustBoundingBox(String title) {
		Real2Range range2 = new Real2Range();
		List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
		for (SVGChemLine line : lineList) {
			range2.add(line.getXY(0));
			range2.add(line.getXY(1));
		}
		List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
		for (SVGChemText text : textList) {
			range2.add(text.getXY());
		}
		if (newBoundingBox == null) {
			boxTitle = new SVGText(new Real2(10., 10.), "boxTitle");
			boxTitle.setFontSize(4.0);
			SVGElement boundingBox = new SVGRect();
			boundingBox.addAttribute(new Attribute("class", "boundingBox"));
			boundingBox.setStrokeWidth(1.0);
			boundingBox.setStroke("pink");
			newBoundingBox = new SVGChemRect(boundingBox);
//			svgChem.debug("TITLE");
			svgChem.appendChild(newBoundingBox);
			svgChem.appendChild(boxTitle);
		}
		double xx = range2.getXRange().getMin();
		double yy = range2.getYRange().getMin();
		double dx = range2.getXRange().getRange();
		double dy = range2.getYRange().getRange();
		
		boxTitle.setXY(new Real2(xx+1, yy+1));
		boxTitle.setText(S_LBRAK+npict+S_RBRAK+title);
		newBoundingBox.setX(xx);
		newBoundingBox.setY(yy);
		newBoundingBox.setWidth(dx);
		newBoundingBox.setHeight(dy);
		deltayy = 2*dy + 20;
		return range2;
	}

    private void extractAtomsFromTextAddToListAndNumber() {
    	List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
    	for (SVGChemText text : textList) {
    		CMLAtom atom = text.getAtom();
    		if (atom != null) {
    			atomList.add(atom);
    			atom.setId("a"+atomList.size());
    		}
    	}
		addBoundingBoxAndDisplay("atoms and number");
    }
    
    private void closeShortGapsBetweenLinesAndGroups() {
    	binLines();
    	List<SVGChemText> groups = SVGChemText.getTextList(svgChem);
    	ParentNode node = groups.get(0).getParent();
    	plotAtoms(node);
    	for (SVGChemLine line : normalLines) {
    		CMLAtom[] atoms = line.getAtoms();
    		closeShortGaps(0, groups, line, atoms);
    		closeShortGaps(1, groups, line, atoms);
    		
    	}
    	// manage hatches
    	List<SVGChemLine> lines = SVGChemLine.getLineList(svgChem);
    	for (SVGChemLine line : lines) {
    		String ss = line.getSvgClass();
    		if (ss != null && ss.indexOf("unjoined") != -1) {
    			for (CMLAtom atom : atomList) {
    				Real2 atomXY = atom.getXY2();
    				clickToAtom(line, ss, atomXY, 0);
    				clickToAtom(line, ss, atomXY, 1);
    			}
    		}
    	}
		addBoundingBoxAndDisplay("close short gaps");

    }

	private void clickToAtom(SVGChemLine line, String ss, Real2 atomXY,
			int serial) {
		Real2 lineXY = line.getXY(serial);
		if (lineXY.getDistance(atomXY) < 5.0) {
			line.setXY(atomXY, serial);
			String newsvg = line.getSvgClass();
			if (newsvg != null) {
				newsvg = newsvg.replace("unjoined", "");
				line.setSvgClass(ss.trim());
			}
		}
	}

	private void closeShortGaps(int serial, List<SVGChemText> groups, SVGChemLine line,
			CMLAtom[] atoms) {
		if (atoms[serial] == null) {
			line.closeShortGapsBetweenLineEndAndGroups(groups, serial, atomList, this);
		} else {
//			double dist = line.getXY(serial).getDistance(atoms[serial].getXY2());
			line.setXY(atoms[serial].getXY2(), serial);
		}
	}
    
    private void plotAtoms(ParentNode node) {
    	for (CMLAtom atom : atomList) {
    		Real2 xy = atom.getXY2();
    		SVGElement circle = new SVGCircle(xy, 5.0);
    		circle.setOpacity(0.3);
    		node.appendChild(circle);
    	}
    }
    
    private void extendShortUnjoinedBondsToAverageLengthAndLookForAnchors() {
    	if (createListOfUnjoinedAtomsAndGroups) {
	    	createListOfUnjoinedAtomsAndGroups();
	    	rescaleBonds();
    	}
    	List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
    	for (SVGChemLine line : lineList) {
    		double length = line.getLength();
    		if (length < averageLength) {
    			CMLAtom[] atoms = line.getAtoms();
    			if (atoms[0] == null) {
    				line.extendLineToAverageLength(averageLength, 0);
    				line.joinToCloseAnchor(0, this);
    			} 
    			if (atoms[1] == null) {
        			line.extendLineToAverageLength(averageLength, 1);
    				line.joinToCloseAnchor(1, this);
    			}
    			if (line.getOrder() == null) {
	    			line.setStroke(EXTENDED_BOND_COLOR);
	    			line.setStrokeWidth(1.0);
    			}
    		}
    	}
		addBoundingBoxAndDisplay("extend short unjoined");
    }
    
    private void clickLineEndsToAnchors() {
    	List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
    	for (SVGChemLine line : lineList) {
			line.clickToAtom(0);
			line.clickToAtom(1);
    	}
		addBoundingBoxAndDisplay("click line ends");
    }
    
    private void rescaleBonds() {
    	List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
    	calculateAverageLength(lineList);
    	binLines();
//    	untreatedBonds = new ArrayList<SVGChemLine>();
    	calculateAverageLength(normalLines);
    }


	/**
	 * @param lineList
	 */
	void calculateAverageLength(List<SVGChemLine> lineList) {
		averageLength = SVGChemLine.calculateAverageLength(lineList, averageLength);
	}
    
    
    private void guessTerminalCarbons() {
//    	LOG.debug("guess terminal C");
    	binLines();
    	List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
    	for (SVGChemLine line : lineList) {
    		CMLAtom atom0 = line.getAtoms()[0];
    		CMLAtom atom1 = line.getAtoms()[1];
    		if ("arrow".equals(((SVGElement)line.getParent()).getSvgClass())) {
    			// omit reaction arrows
    		} else if (atom0 == null && atom1 == null) {
    			boolean isolated = true;
    			if (false) {
	    			// this is very messy to avoid concurrent modification
					Set<CMLAtom> atomSet = new HashSet<CMLAtom>();
					List<CMLAtom> atomCopy = new ArrayList<CMLAtom>();
	    			for (CMLAtom atom : atomList) {
	    				atomCopy.add(atom);
	    			}
	    			while (true) {
	    				boolean change = false;
		    			for (CMLAtom atom : atomCopy) {
		    				if (!atomSet.contains(atom)) {
			    				Real2 xy2 = atom.getXY2();
			    				if (xy2 == null) {
			    					LOG.debug("NULL COORDS "+atom.getId());
			    				} else {
				    				if (extendAndJoinOrMakeAndAddTerminalCarbon(line, atom, xy2, 0)) {
				    					isolated = false;
				    					change = true;
				    					atomSet.add(atom);
									}
				    				if (extendAndJoinOrMakeAndAddTerminalCarbon(line, atom, xy2, 1)) {
				    					isolated = false;
	//			    					LOG.debug("Added2 "+atom.getId());
				    					change = true;
				    					atomSet.add(atom);
									}
			    				}
		    				}
		    				atomSet.add(atom);
		    			}
	//	    			LOG.debug("===================");
		    			if (!change) {
		    				break;
		    			} else {
	//	    				debugLine("LL ",line);
		    			}
	    			}
    			}
    			if (isolated) {
    				LOG.debug("Ignore pseudoEthanes");
    				line.setStroke("blue");
    				line.setStrokeWidth(10.0);
    			} else {
    				line.setStroke("pink");
    				line.setStrokeWidth(10.0);
    			}
    		} else if (atom0 != null && atom1 != null) {
    			// already processed
    		} else {
    			int end = (atom0 == null) ? 1 : 0;
    			double length = line.getLength();
    			if (length < averageLength * 1.4 && length > averageLength * 0.7) {
    				makeAndAddNewTerminalCarbon(line, end);
				}
    		}
    	}
		addBoundingBoxAndDisplay("guess terminal");
    }
    
    private boolean extendAndJoinOrMakeAndAddTerminalCarbon(SVGChemLine line, CMLAtom atom, Real2 xy2, int end) {
		boolean added = false;
		if (xy2.getDistance(line.getXY(end)) < 1.1) {
			line.getAtoms()[end] = atom;			
			added = true;
			CMLAtom atom1 = getAtomCloseToNormalLengthBond(line);
			if (atom1 == null) {
//				LOG.debug("could not find a near atom; creating terminal Carbon");
				makeAndAddNewTerminalCarbon(line, end);
			} else {
				line.getAtoms()[end] = atom1;
				line.debug("Made bond between existing atoms");
			}
			line.setStrokeWidth(1.0);
//			untreatedBonds.remove(line);
		}
		return added;
	}
	
	private CMLAtom getAtomCloseToNormalLengthBond(SVGChemLine line) {
		Line2 line2 = line.getEuclidLine();
		Vector2 vector2 = line2.getVector();
		Real2 bondVector = vector2.getUnitVector().multiplyBy(averageLength);
		Real2 xy2 = line2.getXY(0).plus(bondVector);
		CMLAtom atom = getNearestAtom(xy2, atomList, 0.3*averageLength);
		return atom;
	}
	
	private CMLAtom getNearestAtom(Real2 xy2, List<CMLAtom> atomList, double maxDist) {
		CMLAtom atom1 = null;
		double mind = Double.MAX_VALUE;
		for (CMLAtom atom : atomList) {
			Real2 atomXY2 = atom.getXY2();
			if (atomXY2 != null) {
				double d = atomXY2.getDistance(xy2);
				if (d < maxDist && d < mind) {
					mind = d;
					atom1 = atom;
				}
			}
		}
		return atom1;
	}

	private void makeAndAddNewTerminalCarbon(SVGChemLine line, int end) {
		int serial = atomList.size()+1;
		CMLAtom newAtom = new CMLAtom(); 
		newAtom.setId("a"+serial);
		line.getAtoms()[1-end] = newAtom;
		Real2 xy2 = line.getXY(1-end);
		if (line.getOrder() == null) {
			line.setOrder(CMLBond.SINGLE_S);
			line.detachPath(svgChem);
		}
		atomList.add(newAtom);
		ParentNode lineParent = line.getParent();
		SVGChemSVG.plotAtom((SVGElement) lineParent, newAtom, TERMINAL_CARBON_COLOR, xy2, "C", TERMINAL_CARBON_OPACITY);
		SVGChemText text = new SVGChemText(new SVGText(xy2, "C"+serial));
		lineParent.appendChild(text);
	}

	private void processTextAndSingleCharactersAndAddBoxesAndScales() {
		List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
		int serial = 0;
    	for (SVGChemText chemText : textList) {
    		chemText.processTextAndSingleCharactersAndAddBoxesAndScales(serial++, this);
    	}
		addBoundingBoxAndDisplay("text and chars");
	}

	/** for wiggly lines.
	 * 
	 */
	private void createWiggles() {
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
//		List<SVGChemPath> wiggleList = SVGChemPath.createWiggles(pathList, WIGGLE_RATIO*averageLength);
		for (SVGChemPath path : pathList) {
			Real2Array ra = path.getVertexR2Array();
			if (ra.size() > MIN_WIGGLE_SEGMENTS) {
				SVGChemLine chemLine = WiggleBond.findCrudeWiggleAxis(ra, this);
				path.getParent().appendChild(chemLine);
				path.detach();
				chemLine.setOrder(CMLBond.SINGLE_S);
			}
		}
		addBoundingBoxAndDisplay("wiggles");
	}
	
	private void createWedgesOrArrowsFromPaths() {
		rescaleBonds();
		List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
		double eps = 1;
		double wedgeFactor = 0.3;
		Angle deltaTheta = new Angle(0.01, Angle.Units.RADIANS);
		for (int ipath = 0; ipath < pathList.size(); ipath++) {
			SVGChemPath path = pathList.get(ipath);
			path.setVertexR2Array(null);
			path.getLongEdges(averageLength);
//			List<Line2> distinctLineList = path.extractDistinctEdges(eps, deltaTheta);
			List<Real2> distinctPointList = path.extractDistinctPoints(eps, deltaTheta);
			path.debug("PATH");
			if (path.isZeroLength(0.1)) {
//				path.detach();
//				pathList.remove(path);
			} else if (SVGChemPath.isWedgeAtStart(distinctPointList, wedgeFactor)) {
				makeBond(lineList, pathList, ipath);
			} else if (path.isBondLike()) {
				makeBond(lineList, pathList, ipath);
			} else {
				if (path.isArrowHeadLike()) {
					makeArrow(lineList, path);
				}
			}
		}
		List<Arrow> arrowList = Arrow.getArrowList(svgChem);
		if (arrowList.size() > 0) {
			LOG.debug("Reaction arrows: "+arrowList.size());
		}
		addBoundingBoxAndDisplay("wedges and arrows");
	}


	/**
	 * @param lineList
	 * @param path
	 */
	private void makeArrow(List<SVGChemLine> lineList, SVGChemPath path) {
		path.setFill("#00ff00");
		// find line
		for (SVGChemLine line : lineList) {
			if (line.getXY(1).getDistance(path.getArrowTail()) < 1.0) {
				line.getEuclidLine().flipCoordinates();
			}
			if (line.getXY(0).getDistance(path.getArrowTail()) < 1.0) {
				double rad = line.getEuclidLine().getVector().getAngleMadeWith(path.getArrowDirection()).getRadian();
				if (Math.abs(rad) > Math.PI * 0.95 ) {
					ParentNode parent = line.getParent();
					Arrow arrow = new Arrow(line, path);
					parent.appendChild(arrow);
				}
			}
		}
	}


	/**
	 * @param lineList
	 * @param path
	 */
	private void makeBond(List<SVGChemLine> lineList, List<SVGChemPath> pathList, int ipath) {
//		path.setFill(BOND_LIKE_COLOR);
//		if (path.isWedgeLike()) {
		SVGChemPath path = pathList.get(ipath);
		path.setFill(WEDGE_LIKE_COLOR);
		SVGChemLine bondLine = null;
		for (SVGChemLine line : lineList) {
			Real2 xy0 = line.getEuclidLine().getXY(0);
			Real2 xy1 = line.getEuclidLine().getXY(1);
			if (path.encloses(xy0) && path.encloses(xy1)) {
				bondLine = line;
				break;
			}
		}
		if (bondLine == null) {
			List<Real2> pointList = path.getDistinctPointList();
			// check for distinctness from preceding
			boolean overlap = false;
			if (ipath > 0) {
				SVGChemPath previousPath = pathList.get(ipath-1);
				List<Real2> previousPointList = previousPath.getDistinctPointList();
				overlap = overlaps(pointList, previousPointList, 1.0); 
			}
			if (!overlap) {
				Real2 midPointShort = pointList.get(1).getMidPoint(pointList.get(2));
				Real2 sharp = pointList.get(0);
				org.xmlcml.euclid.Line2 line = new org.xmlcml.euclid.Line2(sharp, midPointShort);
				bondLine = new SVGChemLine(new SVGLine(line));
				// add line to graphics tree
				path.getParent().appendChild(bondLine);
				lineList.add(bondLine);
				LOG.debug("Bond LINES "+lineList.size());
			} else {
				LOG.info("OMITTED OVERLAPPING BOND");
			}
		}
		if (bondLine != null) {
			bondLine.setStereoBondType(StereoBondType.WEDGE);
			bondLine.setOrder(CMLBond.SINGLE_S);
	//					path.setFill("#77ff00");
			path.setOpacity(0.4);
		}
	}
	
	private boolean overlaps(List<Real2> pointList1, List<Real2> pointList2, double delta) {
		boolean overlap = pointList1.size() == pointList2.size();
		if (overlap) {
			for (int i = 0; i < pointList1.size(); i++) {
				Real2 point1 = pointList1.get(i);
				Real2 point2 = pointList2.get(i);
				if (point1.getDistance(point2) > delta) {
					overlap = false;
					break;
				}
			}
		}
		return overlap;
	}

	private void convertTwoPointPathsToLines() {
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
    	for (SVGChemPath chemPath : pathList) {
    		try {
    			chemPath.convertTwoPointPathToLine();
    		} catch (Exception e) {
    			LOG.warn("Skipped "+e);
    		}
    	}
		addBoundingBoxAndDisplay("points2Lines");
	}
	
	private void analyzePaths() {
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
		List<SVGChemPath> bondLikePolyList = new ArrayList<SVGChemPath>();
		for (SVGChemPath path : pathList) {
			if (path.isPolygon(0.1)) {
				if (path.isBondLike()) {
					bondLikePolyList.add(path);
				}
			}
		}
		addBoundingBoxAndDisplay("analyze paths");

	}
	
	private void mergeSingleCharactersIntoTextStrings() {
		List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
		SVGChemText.mergeSingleCharactersIntoTextStrings(textList);
		addBoundingBoxAndDisplay("mergeChars");
	}

	private void interpretTextStrings() {
		List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
		for (SVGChemText text : textList) {
			text.interpretText();
		}
		addBoundingBoxAndDisplay("text");
	}


	private List<SVGChemLine> getUnjoinedBondsMissingOneOrBothAtoms() {
		unjoinedBonds = new ArrayList<SVGChemLine>();
		for (SVGChemLine line : normalLines) {
			if (line.getAtoms()[0] == null || line.getAtoms()[1] == null) {
				unjoinedBonds.add(line);
				line.setStroke("#ff00ff");
				String ss = line.getSvgClass();
				line.setSvgClass("unjoined"+((ss == null) ? "" : " "+ss));
			}
		}
		addBoundingBoxAndDisplay("unjoined bonds");
		return unjoinedBonds;
	}
	
	private void joinUnjoinedBondsExactlyGeometrically() {
		double eps = averageLength * MAXUNJOINEDFACTOR;
		for (SVGChemText group : unjoinedGroups) {
			for (SVGChemLine line : unjoinedBonds) {
				tryJoin(line, group, 0, eps);
				tryJoin(line, group, 1, eps);
			}
		}
		// now get exact intersections
		for (SVGChemText group : unjoinedGroups) {
			List<SVGChemLine> lineList = group.getLineList();
			if (lineList != null && lineList.size() > 1) {
				Real2Vector intersections = new Real2Vector();
				for (int i = 0; i < lineList.size()-1; i++) {
					Line2 linei = lineList.get(i).getEuclidLine();
					for (int j = i+1; j < lineList.size(); j++) {
						Line2 linej = lineList.get(j).getEuclidLine();
						// avoid almost colinear intersections
						Angle angle = linei.getVector().getAngleMadeWith(linej.getVector());
						double rads = Math.abs(angle.getRadian());
						if (Math.abs(rads - Math.PI) > 0.1) {
							Real2 point = linei.getIntersection(linej);
							intersections.add(point);
						}
					}
				}
				Real2 commonPoint = intersections.getCentroid();
				group.setCentroid(commonPoint);
				for (SVGChemLine line : lineList) {
					adjustEnd(line, commonPoint, group.getAtom());
				}
			}
		}
	}
	
	private void adjustEnd(SVGChemLine line, Real2 point, CMLAtom atom) {
		Line2 line2 = line.getEuclidLine();
		double dist0 = line2.getXY(0).getDistance(point);
		double dist1 = line2.getXY(1).getDistance(point);
		if (dist0 < dist1) {
			line.setXY(point, 0);
		} else {
			line.setXY(point, 1);
		}
		line.setStroke("#000000");
	}
	
	private void tryJoin(SVGChemLine line, SVGChemText group, int end, double eps) {
		if (line.getAtoms()[end] == null) {
			Line2 line2 = line.getEuclidLine();
			Real2 xy = line2.getXY(end);
			Real2 cxy = group.getCircle().getXY();
			Real2 delta = cxy.subtract(xy);
			double dist = delta.getLength();
			if (dist < eps) {
				group.getCircle().setFill("#ffff00");
				line.setStroke("#ffff00");
				Real2 cxy2 = line2.getNearestPointOnLine(cxy);
				line.setXY(cxy2, end);
				line.setStroke("#000000");
				group.addLine(line);
				CMLAtom atom = group.getAtom();
				if (atom != null) {
					line.getAtoms()[end] = atom;
				}
			}
		}
	}

	private List<SVGChemText> createListOfUnjoinedAtomsAndGroups () {
		unjoinedGroups = new ArrayList<SVGChemText>();
		List<SVGChemText> groups = SVGChemText.getTextList(svgChem);
		for (SVGChemText group : groups) {
			if (group.getCircle() != null) {
				unjoinedGroups.add(group);
				group.getCircle().setFill("#ff00ff");
			}
		}
		return unjoinedGroups;
	}

	void binLines() {
		List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
		shortLines = new ArrayList<SVGChemLine>();
		normalLines = new ArrayList<SVGChemLine>();
		longLines = new ArrayList<SVGChemLine>();
		// do this twice
		calculateAverageLength(lineList);
		calculateAverageLength(lineList);
//		LOG.debug("AV "+averageLength);
		for (SVGChemLine chemLine : lineList) {
			if (chemLine.getParent() == null) {
				throw new RuntimeException("NO parent");
			}
    		if (chemLine.isShort(averageLength)) {
    			shortLines.add(chemLine);
    		} else if (chemLine.isLong(averageLength)) {
    			longLines.add(chemLine);
    		} else {
    			normalLines.add(chemLine);
    		}
    		chemLine.colorBondByLength(averageLength);
    	}
	}
	
	private void makeMultiples() {
		binLines();
		rescaleBonds();
		if (normalLines.size() > 0) {
			List<Multiple> multipleList = new ArrayList<Multiple>();
			SVGChemLine currentLine = normalLines.get(0);
			// assume lines are ordered...
			Multiple multiple = new Multiple(this);
			multiple.add(currentLine);
			multipleList.add(multiple);
			for (int i = 1; i < normalLines.size(); i++) {
				SVGChemLine nextLine = normalLines.get(i);
				double dist = currentLine.getMidPoint().getDistance(nextLine.getMidPoint());
				boolean aligned = currentLine.isParallelOrAntiparallel(nextLine, MAX_DOUBLE_BOND_ANGLE_DEVIATION);
				if (dist > DOUBLE_SEPARATION_RATIO*averageLength || !aligned) {
					multiple = new Multiple(this);
					multipleList.add(multiple);
				}
				multiple.add(nextLine);
				currentLine = nextLine;
			}
			ParentNode parent = normalLines.get(0).getParent();
			for (Multiple multiplex : multipleList) {
				if (multiplex.size() > 1) {
					SVGChemLine multipleLine = multiplex.getMultipleBond();
					if (multipleLine == null) {
						LOG.warn("Null  multipleLine");
					} else {
						if (multipleLine.getParent() == null) {
							parent.appendChild(multipleLine);
						}
					}
				}
			}
		}
		addBoundingBoxAndDisplay("multiples");
	}
	

	private void addImplicitAtomsAndRecordSingleBonds() {
		binLines();

		for (int i = 0; i < normalLines.size()-1; i++) {
			SVGChemLine bondi = normalLines.get(i); 
			for (int j = i+1; j < normalLines.size(); j++) {
				SVGChemLine bondj = normalLines.get(j);
				if (false) {
				} else if (joinAndAddAtom(bondi, bondj, 0, 0, 1.0)) {
				} else if (joinAndAddAtom(bondi, bondj, 0, 1, 1.0)) {
				} else if (joinAndAddAtom(bondi, bondj, 1, 0, 1.0)) {
				} else if (joinAndAddAtom(bondi, bondj, 1, 1, 1.0)) {
				}
			}
		}
		for (int i = 0; i < normalLines.size()-1; i++) {
			SVGChemLine bond = normalLines.get(i); 
			if (bond.getAtoms()[0] != null &&
				bond.getAtoms()[1] != null &&
				bond.getOrder() == null) {
				bond.setOrder(CMLBond.SINGLE_S);
				bond.detachPath(svgChem);
			}
		}
		addBoundingBoxAndDisplay("add implicit atoms");
	}
	
	private boolean joinAndAddAtom(SVGChemLine bondi, SVGChemLine bondj, 
			int endi, int endj, double eps) {
		double dist = bondi.getEuclidLine().getXY(endi).getDistance(
				bondj.getEuclidLine().getXY(endj));
		boolean touches = dist < eps;
		if (touches) {
			CMLAtom atomi = bondi.getAtoms()[endi];
			CMLAtom atomj = bondj.getAtoms()[endj];
			if (atomi != null) {
				if (atomj == null) {
					bondj.getAtoms()[endj] = atomi;
				}
			} else {
				if (atomj != null) {
					bondi.getAtoms()[endi] = atomj;
				} else {
					int serial = atomList.size()+1;
					CMLAtom atom = new CMLAtom();
					atom.setId("a"+serial);
					atomList.add(atom);
					bondi.getAtoms()[endi] = atom;
					bondj.getAtoms()[endj] = atom;
					Real2 xymid = bondi.getEuclidLine().getXY(endi).getMidPoint(
							bondj.getEuclidLine().getXY(endj));
					ParentNode bondiParent = bondi.getParent();
					SVGChemSVG.plotAtom((SVGElement)bondiParent, atom, IMPLICIT_ATOM_COLOR, xymid, "C", IMPLICIT_ATOM_OPACITY);
					SVGChemText atomText = new SVGChemText(new SVGText(xymid, "C"/*+serial*/));
					atomText.setAtom(atom);
					atomText.setFill(CARBON_FILL);
					atomText.setFontSize(CARBON_FONT_SIZE);
					bondiParent.appendChild(atomText);
					atomText.setCentroid(xymid);
					atomText.setLeftAnchor(new SVGCircle(xymid, 1.0));
					atomText.setRightAnchor(new SVGCircle(xymid, 1.0));
				}
			}
		}
		return touches;
	}

	private void processPathsAndCreateEdges() {
		List<SVGChemPath> pathList = 
			new ArrayList<SVGChemPath>(SVGChemPath.getPathList(svgChem));
		for (SVGChemPath path : pathList) {
			try {
				path.createEdges();
			} catch (Exception e) {
				LOG.error("bad path: "+e.getMessage());
			}
		}
		addBoundingBoxAndDisplay("edges");
	}
	
//	public static void usage() {
//		AbstractLegacyConverter.usage();
//		LOG.debug("SVGConverter <options>");
//		LOG.debug("   -SUMMARY summaryFile");
//	}
	
//	/**
//	 * @param args
//	 */
//	private void processArgs(String[] args) {
//		int i = 0;
//
//        while (i < args.length) {
//            if (false) {
//            } else if (args[i].equalsIgnoreCase("-SUMMARY")) {
//                this.setSummaryFile(args[++i]); i++;
//            } else {
//            	i = this.processArgs(args, i);
//            }
//        }
//        try {
//        	this.runIterator();
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.severe("Exception: " + e);
//        }
//	}

//    /**
//     * Provides a command line and graphical user interface to WMFConverter.
//     * @param args
//     */
//    public static void main(String[] args) {
//
//        if (args.length == 0) {
////            usage();
//            System.exit(0);
//        }
//
////        SVG2CMLTool svg = new SVG2CMLTool();
////        svg.processArgs(args);
//    }

//	@Override
//	protected AbstractLegacyConverter getNewLegacyConverter(
//			AbstractLegacyConverter abstractLegacyConverter) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//

	protected String getPrefix() {
		return PREFIX;
	}

//	/** read stream
//	 * returns first parse (XML not yet CDXML or CML)
//	 * @param inputStream
//	 * @param fileId
//	 * @return document
//	 * @throws Exception
//	 */
//	public void parseLegacy(InputStream inputStream, String fileId) throws Exception {
//		this.fileId = fileId;
//		readAndProcessXML(inputStream);
//	}

	
	public List<SVGChemLine> getShortLines() {
		return shortLines;
	}

	public List<CMLAtom> getAtomList() {
		return atomList;
	}

   @Override
	public SVGChemSVG getSvgChem() {
		return svgChem;
	}

	public double getAverageLength() {
		return averageLength;
	}

	public CMLFormula getGivenFormula() {
		return givenFormula;
	}

	public void setGivenFormula(CMLFormula givenFormula) {
		this.givenFormula = givenFormula;
	}

   @Override
	public String getFileId() {
		return fileId;
	}


	public void setAverageLength(double averageLength) {
		this.averageLength = averageLength;
	}
	
}

class Row {
	String fileId;
	String molId;
	CMLFormula formula;
	String name;
	
	public Row(Elements tds) {
		fileId = tds.get(0).getValue();
		molId = tds.get(1).getValue();
		String s = tds.get(2).getValue();
		formula = (S_EMPTY.equals(s)) ? null : CMLFormula.createFormula(s);
		name = tds.get(3).getValue();
	}
}
