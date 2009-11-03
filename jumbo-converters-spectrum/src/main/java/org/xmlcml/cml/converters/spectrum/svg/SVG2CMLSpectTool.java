package org.xmlcml.cml.converters.spectrum.svg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.GraphicsConverterTool;
import org.xmlcml.cml.converters.spectrum.svg.AxisTool.Orientation;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.graphics.SVGConstants;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPoly;
import org.xmlcml.cml.graphics.SVGPolyline;
import org.xmlcml.cml.graphics.SVGRect;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Axis.Axis2;


/**
 * @author pm286
 */
public class SVG2CMLSpectTool extends GraphicsConverterTool {

    public static Logger LOG = Logger.getLogger(SVG2CMLSpectTool.class);
    
	public enum Directory {
		END,
		START,
	}
	
    static Level MYFINE = Level.TRACE;
 
    static String BOX = "box";
    static String BRACKET = "bracket";
    private static String CLUSTER = "cluster";
    private static String PAGENUMBER = "pageNumber";
    static String PEAK = "peak";
    static String PEAKLIST = "peakList";
    private static String SPECTRUM = "spectrum";
    private static String UNKNOWN = "unknown";
    static String VALUES = "values";
    private static String VIEWBOX = "viewBox";
    
    private CMLCml cmlCml;
	private List<LineCluster> clusterList;
	private List<SVGLine> horizontalVerticalList;
	private double eps;

	private Orientation spectrumDataOrientation;
	private List<SVGG> spectrumValuesList;
	private Orientation boxOrientation;
	private List<SVGRect> boxList;
	private List<SpectrumAnalysisTool> spectrumToolList;

	private int spectrumCount;

	private SVGG topG;

	private double SUBSCRIPT_FACTOR = 0.5;
	private double SUPERSCRIPT_FACTOR = -0.5;

    
	public SVG2CMLSpectTool(Element svgElement, String fileId) {
	    init();
	    this.fileId = fileId;
	    this.svgElement = svgElement;
	}
    
    public CMLCml getCML() {
    	cmlCml = new CMLCml();
    	for (SpectrumAnalysisTool spectrumTool : spectrumToolList) {
    		cmlCml.appendChild(spectrumTool.getCmlSpectrum());
    	}
    	return cmlCml;
    }
    
	private void init() {
//	    super.init();
		clearVars();
	}
	
	private void clearVars() {
		eps = 0.01;
	}
	
	protected void processAfterParsing() {
    	CMLUtil.removeWhitespaceNodes(svg);
		svg.getAttribute(VIEWBOX).detach();
    	SVGElement.applyTransformsWithinElementsAndFormat(svg);
    	joinTexts();
    	joinPolyLines();
    	horizontalVerticalList = SVGLine.findHorizontalOrVerticalLines(svg, eps);
    	markDataPolylines();
    	getBoundingBoxAndPageNumber();
    	makeClusters();
    	interpretClusters();
    	groupRemainingNonGroupsAsUnknown();
    	interpretUnknownsAndRemoveEmptyUnknowns();
    	
    	splitIntoSpectra();
    	int i = 0;
    	for (SpectrumAnalysisTool spectrumTool : spectrumToolList) {
    		spectrumTool.process();
        	try {
    			CMLUtil.debug(spectrumTool.getCmlSpectrum(), new FileOutputStream("spectrum"+(++i)+".xml"), 1);
    		} catch (Exception e) {
    			throw new RuntimeException("Cannot debug", e);
    		}
    	}
    }
	
	@SuppressWarnings("unused")
	private void debug() {
/*		
	    private CMLCml cmlCml;
		private List<LineCluster> clusterList;
		private List<SVGLine> horizontalVerticalList;
		private double eps;

		private Orientation spectrumDataOrientation;
		private List<AxisTool> horizontalAxisList;
		private List<AxisTool> verticalAxisList;
		private Map<Integer, List<AxisTool>> horizontalAxisMap;
		private Map<Integer, List<AxisTool>> verticalAxisMap;
		private List<SVGG> spectrumValuesList;
		private Orientation boxOrientation;
		private List<SVGRect> boxList;
*/
		System.out.println("---------------------debug--------------------->>>>");
		System.out.println("clusters "+ ((clusterList == null) ? "null" : clusterList.size()));
//		System.out.println("spectrumDataOrientation "+ spectrumDataOrientation);
//		System.out.println("horizontalAxisList "+ ((horizontalAxisList == null) ? "null" : ""+horizontalAxisList.size()));
//		if (horizontalAxisList != null && horizontalAxisList.size() > 0) {
//			for (AxisTool axisTool : horizontalAxisList) {
//				System.out.println("HOR..."+axisTool.toString());
//			}
//		}
//		System.out.println("horizontalAxisMap "+ ((horizontalAxisMap == null) ? "null" :horizontalAxisMap.size()));
//		System.out.println("verticalAxisList "+ ((verticalAxisList == null) ? "null" : ""+verticalAxisList.size()));
//		if (verticalAxisList != null && verticalAxisList.size() > 0) {
//			for (AxisTool axisTool : verticalAxisList) {
//				System.out.println("VER..."+axisTool.toString());
//			}
//		}
//		System.out.println("verticalAxisMap "+ ((verticalAxisMap == null) ? "null" :verticalAxisMap.size()));
//		System.out.println("spectrumValuesList "+ ((spectrumValuesList == null) ? "null" : spectrumValuesList.size()));
//		if (spectrumValuesList != null && spectrumValuesList.size() > 0) {
//			for (SVGG spectrum : spectrumValuesList) {
//				System.out.println("SPECTRUM..."+spectrum.toString());
//			}
//		}
//		System.out.println("boxOrientation "+ boxOrientation);
//		System.out.println("boxList "+ ((boxList == null) ? "null" : boxList.size()));
//		if (boxList != null && boxList.size() > 0) {
//			for (SVGRect box : boxList) {
//				System.out.println("BOX..."+box.toString());
//			}
//		}
    	try {
			CMLUtil.debug(this.svg, new FileOutputStream("src/test/resources/graphics/svg2cmlspect/temp/temp.svg.xml"), 1);
			CMLUtil.debug(this.svg, new FileOutputStream("src/test/resources/graphics/svg2cmlspect/temp/temp.svg"), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("<<<<-----------------debug-------------------------");
	}

	private void joinTexts() {
		// assume linear
		// change this later
		double fontWidthScale = 1.1;
		double fontHeightScale = 1.3;
		//		double subEps = 5.;
		List<SVGElement> textList = SVGElement.getElementList(svg, ".//svg:text");
		if (textList.size() > 0) {
			SVGText text0 = (SVGText) textList.get(0);
			text0.setCurrentFontSize(text0.getFontSize());
			for (int i = 1; i < textList.size(); i++) {
				SVGText text1 = (SVGText) textList.get(i);
//				String newText = null;
				if (text0.concatenateText(fontWidthScale, fontHeightScale, 
						text1, SUBSCRIPT_FACTOR, SUPERSCRIPT_FACTOR, eps)) {
					text1.detach();
				} else {
					LOG.debug("accepted..............."+text0.getText());
					text0 = text1;
				}
			}
		}
		LOG.debug("... joined texts");
	}

//	/** crude
//	 * counts 1 for each full font character and 0.7 for each script
//	 * @param s
//	 * @return
//	 */
//	private double getLength(String s) {
//		double l = 0;
//		int idx0 = s.indexOf(SUP0);
//		int idx1 = s.indexOf(SUP1);
//		// add missing start
//		if (idx1 != -1 && idx0 == -1 || idx0 > idx1) {
//			s = SUP0 + s;
//		}
//		idx0 = s.indexOf(SUB0);
//		idx1 = s.indexOf(SUB1);
//		// add missing start
//		if (idx1 != -1 && idx0 == -1 || idx0 > idx1) {
//			s = SUB0 + s;
//		}
//		s = s.trim();
//		boolean inscript = false;
//		while (s.length() > 0) {
//			if (s.startsWith(SUB0) || s.startsWith(SUP0)) {
//				s = s.substring(2);
//				inscript = true;
//			} else if (inscript) {
//				if (s.startsWith(SUB0) || s.startsWith(SUP1)) {
//					s = s.substring(2);
//					inscript = false;
//				} else {
//					s = s.substring(1);
//					l += 0.7;
//				}
//			} else {
//				s = s.substring(1);
//				l += 1.0;
//			}
//		}
//		return l;
//	}
		  
// ---------------------joinPolyLines------------------------------	
	
    private void joinPolyLines() {
//    	Nodes lines = svg.query(".//svg:line | .//svg:polyline", SVGConstants.SVG_XPATH);
    	Nodes nodes = svg.query(".//svg:*", SVGConstants.SVG_XPATH);
    	List<List<SVGElement>> lineListList = new ArrayList<List<SVGElement>>();
    	boolean inLines = false;
		List<SVGElement> lineListx = null;
    	for (int i = 0; i < nodes.size(); i++) {
    		Node node = nodes.get(i);
    		if (node instanceof SVGLine || node instanceof SVGPolyline) {
    			if (!inLines) {
    				lineListx = new ArrayList<SVGElement>();
    				lineListList.add(lineListx);
    				inLines = true;
    			}
    			if (lineListx.size() > 0) {
    				if (!canBeJoined(lineListx.get(lineListx.size()-1), (SVGElement) node)) {
        				lineListx = new ArrayList<SVGElement>();
        				lineListList.add(lineListx);
    				}
    			}
				lineListx.add((SVGElement) node);
    		} else {
    			inLines = false;
    		}
    	}
    	
		LOG.debug("... joining lineList "+lineListList.size());
		// skip single lines
    	for (List<SVGElement> lineList : lineListList) {
    		if (lineList.size() > 1) {
				LOG.debug("... joining lines "+lineList.size());
		    	SVGPolyline newPolyline = mergeLinesInList(lineList);
		    	ParentNode parent = lineList.get(0).getParent();
		    	parent.replaceChild(lineList.get(0), newPolyline);
		    	for (SVGElement line : lineList) {
		    		line.detach();
		    	}
				LOG.debug("... joined lines");
    		}
    	}
    }
    
    private boolean canBeJoined(SVGElement first, SVGElement second) {
    	Real2 xy0 = getLast(first);
    	Real2 xy1 = getFirst(second);
    	return xy0.isEqualTo(xy1, eps);
    }
    
    private Real2 getFirst(SVGElement element) {
    	Real2 first = null;
    	if (element instanceof SVGLine) {
    		first = ((SVGLine) element).getXY(0);
    	} else if (element instanceof SVGPolyline) {
    		first = ((SVGPolyline) element).getFirst();
    	}
    	return first;
    }
    
    private Real2 getLast(SVGElement element) {
    	Real2 last = null;
    	if (element instanceof SVGLine) {
    		last = ((SVGLine) element).getXY(1);
    	} else if (element instanceof SVGPolyline) {
    		last = ((SVGPolyline) element).getLast();
    	}
    	return last;
    }

    /**
	 * @param lineList
	 */
	private SVGPolyline mergeLinesInList(List<SVGElement> lineList) {
		List<SVGPolyline> polylineList = new ArrayList<SVGPolyline>();
		for (SVGElement line : lineList) {
			polylineList.add(SVGPolyline.getOrCreatePolyline(line));
		}
		while (polylineList.size() > 1) {
			polylineList = SVGPolyline.binaryMergePolylines(polylineList, eps);
			LOG.debug("POLY "+polylineList.size());
		}
		return polylineList.get(0);
	}
	
	// ---------------------getBoundingBoxAndPageNumber------------------------------	
	private void getBoundingBoxAndPageNumber() {
		Real2Range bbox = svg.getBoundingBox();
		Nodes texts = svg.query(".//svg:text", CMLConstants.SVG_XPATH);
		double minval = Double.MAX_VALUE;
		double maxval = -minval;
		SVGText maxText = null;
		SVGText minText = null;
		for (int i = 0; i < texts.size(); i++) {
			SVGText text = (SVGText) texts.get(i);
			double yval = text.getXY().getY();
			if (yval < minval) {
				minText = text;
				minval = yval;
			}
			if (yval > maxval) {
				maxText = text;
				maxval = yval;
			}
		}
		examineAsPage(minText);
		examineAsPage(maxText);
		LOG.debug("BB "+bbox);
	}
	
	private void examineAsPage(SVGText text) {
		String t = text.getValue().trim();
		if (t.matches("S?\\-?\\s*\\d\\d*\\s*\\-?")) {
			getTopG();
			ParentNode parent = text.getParent();
			SVGG textG = new SVGG();
			if (parent.equals(topG)) {
				parent.replaceChild(text, textG);
			} else {
				int idx = topG.indexOf(parent);
				if (idx == -1) {
					throw new RuntimeException("bad hierarchy for text");
				}
				text.detach();
				parent.insertChild(textG, idx);
			}
			textG.appendChild(text);
			textG.setClassName(PAGENUMBER);
			LOG.debug("PAGE: "+text.getValue());
		}
	}
	
// ---------------------markDataPolylines------------------------------	
	private void markDataPolylines() {
		Nodes polylines = svg.query(".//svg:polyline", CMLConstants.SVG_XPATH);
		spectrumDataOrientation = null;
		spectrumValuesList = new ArrayList<SVGG>();
		for (int i = 0; i <polylines.size(); i++) {
			SVGPolyline polyline = (SVGPolyline) polylines.get(i);
			polyline.addMonotonicityAttributes();
			/*SVGElement g = */markAsDataG(polyline);
		}
		LOG.debug("data orientation: "+spectrumDataOrientation+" ... "+spectrumValuesList.size());
		
	}
	private SVGElement markAsDataG(SVGPolyline polyline) {
		SVGG g = null;
		CMLUtil.debug(polyline, "markAsDataG polyline");
		Real2Range r2r = polyline.getBoundingBox();
		if (r2r.getXRange().getRange() > 300 || r2r.getYRange().getRange() > 300) {
			if (polyline.getReal2Array().size() > 100) {
				g = new SVGG();
				g.setClassName(VALUES);
				ParentNode parent = polyline.getParent();
				parent.replaceChild(polyline, g);
				g.appendChild(polyline);
				Orientation spectrumOrientation = analyzeMonotonicity(polyline);
				if (spectrumOrientation != null) {
					if (spectrumDataOrientation == null) {
						spectrumDataOrientation = spectrumOrientation;
					} else if (spectrumDataOrientation != spectrumOrientation) {
						throw new RuntimeException("More than one orientation of spectrum");
					}
					spectrumValuesList.add(g);
				} else {
					LOG.debug("Cannot analyze spectrumdata orientation from monotonicity");
				}
			}
		}
		return g;
	}

	/**
	 * @param polyline
	 * @param monotonicX
	 * @param monotonicY
	 */
	private static Orientation analyzeMonotonicity(SVGPolyline polyline) {
		Orientation spectrumOrientation = null;
		String monoX = polyline.getAttributeValue(SVGPoly.MONOTONIC+Axis2.X);			
		String monoY = polyline.getAttributeValue(SVGPoly.MONOTONIC+Axis2.Y);
		if (monoX == null && monoY == null) {
			LOG.warn("No monotonic values found");
		} else if (monoX != null && monoY != null) {
			LOG.warn("Monotonic values for both X and Y");
		} else {
			spectrumOrientation = (monoX != null) ?
					Orientation.HORIZONTAL : Orientation.VERTICAL;
		}
		return spectrumOrientation;
	}
    
	private void makeClusters() {
		clusterList = new ArrayList<LineCluster>();
		while (true) {
			boolean change = false;
			for (int i = 0; i < horizontalVerticalList.size(); i++) {
				SVGLine linei = horizontalVerticalList.get(i);
				// does it touch a cluster?
				change = addLinesToClusters(change, linei);
				if (change) {
					break;
				}
				// do two lines meet to form a  cluster?
				change = checkJoinedLinesAndAddNewCluster(change, i, linei);
				if (change) {
					break;
				}
			}
			if (!change) {
				break;
			}
		}
		LOG.debug("CL "+clusterList.size());
		LOG.debug("HV "+horizontalVerticalList.size());
		
		mergeClusters();
		
		LOG.debug("CL "+clusterList.size());
		LOG.debug("HV "+horizontalVerticalList.size());
		annotateLinesByCluster();
		createDebugBoundingSVGRects();
	}

	/**
	 * 
	 */
	private void annotateLinesByCluster() {
		for (int i = 0; i < clusterList.size(); i++) {
			LineCluster cluster = clusterList.get(i);
			for (SVGLine line : cluster.getLineList()) {
				line.addAttribute(new Attribute("cluster", ""+i));
			}
		}
	}

	/**
	 * @param colors
	 * @param color
	 */
	private void createDebugBoundingSVGRects() {
		String[] colors = {"red", "yellow", "green", "cyan", "blue", "purple"};
		int color = 0;
		for (LineCluster cl : clusterList) {
			LOG.debug("LINES in cluster "+cl.getLineList().size());
			String col = colors[color++ % colors.length];
			for (SVGLine line : cl.getLineList()) {
				line.setStroke(col);
				line.setStrokeWidth(2.5);
			}
			createDebugBoundingSVGRect(cl);
		}
	}

	/**
	 * @param cl
	 */
	private void createDebugBoundingSVGRect(LineCluster cl) {
		Real2Range r2r = cl.getBoundingBox();
		SVGRect rect = new SVGRect(r2r.getCorners()[0], r2r.getCorners()[1]);
		rect.setStroke("red");
		rect.setStrokeWidth(1.5);
		rect.setOpacity(0.4);
		svg.appendChild(rect);
		rect.format(2);
	}

//-----------------------------------------------	
	private void interpretClusters() {
		for (LineCluster cluster : clusterList) {
			boolean breakAtEnd = false;
			Real2Range bbox = cluster.getBoundingBox();
			Orientation axisOrientation = null;
			if (bbox.getXRange().getRange() > 200 && bbox.getYRange().getRange() > 200) {
				breakAtEnd = true;
				SVGG g = makeGroupFromCluster(cluster, BOX, breakAtEnd,
						new Class[]{SVGLine.class});
				makeAndReplace4LinesByRect(g);
			} else if (bbox.getXRange().getRange() > 200 && bbox.getYRange().getRange() < 50) {
				// possible axis
				axisOrientation = Orientation.HORIZONTAL;
			} else if (bbox.getXRange().getRange() < 50 && bbox.getYRange().getRange() > 200) {
				// possible axis
				axisOrientation = Orientation.VERTICAL;
			} else {
				makeGroupFromCluster(cluster, CLUSTER, breakAtEnd,
						new Class[]{SVGLine.class, SVGText.class, SVGPolyline.class});
			}
			if (axisOrientation != null) {
				breakAtEnd = true;
				SVGElement g = makeGroupFromCluster(cluster, axisOrientation.toString(), breakAtEnd,
						new Class[]{SVGLine.class, SVGText.class});
				tidyAxisGroup(g, cluster.getBoundingBox());
			}
		}
		// find minimum cluster size (assume it's peaks)
		Map<Integer, List<SVGG>> clusterListMap = classifyClustersByLineContent();
		Integer min = Integer.MAX_VALUE;
		for (Integer nline : clusterListMap.keySet()) {
			List<SVGG> gList = clusterListMap.get(nline);
			LOG.debug("nline "+nline+"; "+gList.size());
			if (nline < min) {
				min = nline;
			}
		}
		analyzeClustersAsPeaks(clusterListMap, min);
	}

	private void makeAndReplace4LinesByRect(SVGG g) {
		Nodes lines = g.query("./svg:line", CMLConstants.SVG_XPATH);
		if (lines.size() == 4) {
			Real2Range r2r = g.getBoundingBox();
			SVGRect rect = SVGRect.createFromReal2Range(r2r);
			for (int i = 0; i < lines.size(); i++) {
				lines.get(i).detach();
			}
			g.appendChild(rect);
		}
	}
	
//	@SuppressWarnings("class")
	private SVGG makeGroupFromCluster(LineCluster cluster, String className,
			boolean breakAtEnd, Class[] allowedClasses) {
		SVGLine line0 = cluster.getLineList().get(0);
		String clusterNumber = line0.getAttributeValue(CLUSTER);
		ParentNode parent = line0.getParent();
		int idx = parent.indexOf(line0);
		SVGG g = new SVGG();
		g.setClassName(className);
		parent.replaceChild(line0, g);
		g.appendChild(line0);
		// add nodes till we get to new cluster
		List<Node> nodeList = new ArrayList<Node>();
		for (int i = idx+1; i < parent.getChildCount(); i++) {
			Node node = parent.getChild(i);
			Class nodeClass = node.getClass();
			// break on any subsequent g node
			if (nodeClass.equals(SVGG.class)) {
				break;
			}
			if (node instanceof Element) {
				// always break for non-allowed element
				if (!isContainedInArray(allowedClasses, nodeClass)) {
					break;
				}
				Element element = (Element) node;
				String clusterNumber0 = element.getAttributeValue(CLUSTER);
				// predetermined break
				if (breakAtEnd && isContainedInArray(new Class[]{SVGLine.class, SVGPolyline.class}, nodeClass) && clusterNumber0 == null) {
					break;
				}
				// break at clusterNumber change
				if (clusterNumber0 != null && !clusterNumber0.equals(clusterNumber)) {
					break;
				}
			}
			nodeList.add(node);
		}
		// move nodes to children of group
		for (Node node : nodeList) {
			node.detach();
			g.appendChild(node);
		}
		return g;
	}
	
	private Map<Integer, List<SVGG>> classifyClustersByLineContent() {
		List<SVGElement> gList = SVGElement.getElementList(topG, "./svg:g[@class='"+CLUSTER+"']");
		Map<Integer, List<SVGG>> clusterListMap = new HashMap<Integer, List<SVGG>>();
		for (SVGElement g : gList) {
			Integer nline = SVGElement.getElementList(g, "./svg:line").size();
			List<SVGG> clusterList = clusterListMap.get(nline);
			if (clusterList == null) {
				clusterList = new ArrayList<SVGG>();
				clusterListMap.put(nline, clusterList);
			}
			clusterList.add((SVGG) g);
		}
		return clusterListMap;
	}
	/**
	 * @param clusterListMap
	 * @param min
	 */
	private void analyzeClustersAsPeaks(Map<Integer, List<SVGG>> clusterListMap,
			Integer min) {
		for (Integer nline : clusterListMap.keySet()) {
			List<SVGG> gList = clusterListMap.get(nline);
			for (SVGG g : gList) {
				if (nline == min) {
					analyzeClusterAsPeaks(g, min, 1);
				} else if (nline % min == 0) {
					analyzeClusterAsPeaks(g, min, nline / min );
				}
			}
		}
		List<SVGElement> peakList = SVGElement.getElementList(topG, "./svg:g[@class='"+CLUSTER+"']/svg:g[@class='"+PEAK+"']");
		if (peakList.size() > 0) {
			SVGG peakListG = new SVGG();
			peakListG.setClassName(PEAKLIST);
			topG.replaceChild(peakList.get(0).getParent(), peakListG);
			for (SVGElement peak : peakList) {
				peak.getParent().detach();
				peak.detach();
				peakListG.appendChild(peak);
			}
		}
	}

	private void analyzeClusterAsPeaks(SVGG g, int nline, int npeaks) {
		
		List<SVGElement> childList = SVGElement.getElementList(g, "./svg:*");
		int ichild = 0;
		LOG.trace("analyze "+nline+"/"+npeaks);
		for (int ipeak = 0; ipeak < npeaks && ichild < childList.size(); ipeak++) {
			SVGG peak = new SVGG();
			peak.setClassName(PEAK);
			g.replaceChild(childList.get(ichild), peak);
			SVGG gg = new SVGG();
			peak.appendChild(gg);
			gg.setClassName(BRACKET);
			for (int i = 0; i < nline; i++) {
				SVGLine line = (SVGLine) childList.get(ichild++);
				line.detach();
				gg.appendChild(line);
			} 
			while (ichild < childList.size()) {
				SVGElement child = childList.get(ichild++);
				LOG.trace(""+ichild+"/"+child.getClass());
				if (child instanceof SVGLine) {
					ichild--;
					break;
				}
				child.detach();
				peak.appendChild(child);
			}
		}
	}

	private void findFieldStrengths() {
		getTopG();
		List<SVGElement> fieldList = SVGElement.getElementList(
				topG, "./svg:g[@class='"+UNKNOWN+"']/svg:text");
		for (SVGElement text : fieldList) {
			if (text.getValue().indexOf("MHz") != -1) {
				ParentNode parent = text.getParent();
				int idx = topG.indexOf(parent);
				SVGG fieldG = new SVGG();
				fieldG.setClassName("field");
				topG.insertChild(fieldG, idx+1);
				text.detach();
				fieldG.appendChild(text);
			}
		}
	}
	
	private void findMolecules() {
		getTopG();
		List<SVGElement> unknownList = SVGElement.getElementList(
				topG, "./svg:g[@class='"+UNKNOWN+"']");
		for (SVGElement unknown : unknownList) {
			Real2Range r2r = unknown.getBoundingBox();
			LOG.debug("R2R "+r2r);
			if (r2r != null) {
				double xRangeRange = r2r.getXRange().getRange();
				double yRangeRange = r2r.getYRange().getRange();
				if ((xRangeRange > 50 && xRangeRange < 200) &&
					(yRangeRange > 50 && yRangeRange < 200)) {
					List<SVGElement> lineList = SVGElement.getElementList(
							unknown, "./svg:line");
					List<SVGElement> textList = SVGElement.getElementList(
							unknown, "./svg:text");
					if (lineList.size() > 3 && textList.size() > 0) {
						unknown.setClassName("molecule");
					}
				}
			}
		}
	}
	
	private void removeEmptyUnknowns() {
		getTopG();
		List<SVGElement> unknownList = SVGElement.getElementList(
				topG, "./svg:g[@class='"+UNKNOWN+"' and count(*) = 0]");
		for (SVGElement unknown : unknownList) {
			LOG.debug("DETACH EMPTY UNKNOWN");
			unknown.detach();
		}
	}
	
// -----------------------------------------------------------------
	
	private void tidyAxisGroup(SVGElement g, Real2Range lineBoundingBox) {
		Nodes lines = g.query(".//svg:line", CMLConstants.SVG_XPATH);
		SVGLine lastLine = (SVGLine) lines.get(lines.size()-1);
		Nodes childNodes = g.query(".//svg:*", CMLConstants.SVG_XPATH);
		boolean readFirstLine = false;
		boolean readLastLine = false;
		Real2 delta00 = new Real2(0, 0);
		List<SVGText> trailingTextList = new ArrayList<SVGText>();
		for (int i = 0; i < childNodes.size(); i++) {
			SVGElement childElement = (SVGElement) childNodes.get(i);
			if (childElement instanceof SVGLine) {
				if (!readFirstLine) {
					readFirstLine = true;
				}
				if (childElement.equals(lastLine)) {
					readLastLine = true;
				}
			} else if (childElement instanceof SVGText) {
				SVGText text = (SVGText) childElement;
				Real2 xy = text.getXY();
				Real2 translationBox = lineBoundingBox.distanceOutside(xy);
				LOG.trace("TR "+translationBox);
				if (!delta00.isEqualTo(translationBox, 0.1)) {
					//text.debug("OUTSIDE "+translationBox);
					if (!readFirstLine) {
						// maybe split off previous
					} else if (readLastLine) {
						trailingTextList.add(text);
					} else {
						// need to increase BBox??
						LOG.warn("INCREASED LINE BOX with TEXT "+translationBox);
						lineBoundingBox.add(xy);
					}
				}
			}
		}
		if (trailingTextList.size() > 0) {
			SVGElement textG = new SVGG();
			textG.setClassName(UNKNOWN);
			ParentNode parent = g.getParent();
			int idx = parent.indexOf(g);
			parent.insertChild(textG, idx+1);
			for (SVGText text : trailingTextList) {
				text.detach();
				textG.appendChild(text);
			}
//			g.debug("G");
//			textG.debug("TEXTG");
		}
		
	}
	
	private boolean isContainedInArray(Class[] classes, Class clazz) {
		boolean contained = false;
		for (Class c : classes) {
			if (c.equals(clazz)) {
				contained = true;
				break;
			}
		}
		return contained;
	}

	/**
	 * @param change
	 * @param linei
	 * @return
	 */
	private boolean addLinesToClusters(boolean change, SVGLine linei) {
		for (int k = 0; k < clusterList.size(); k++) {
			LineCluster cluster = clusterList.get(k);
			boolean commonPoint = cluster.containsCommonPoint(linei);
			boolean tJoint = cluster.makesTJointWith(linei);
			if (commonPoint || tJoint) {
				cluster.addLine(linei);
				LOG.trace("added line to cluster "+k+" .. "+linei.getXYString()+" .. "+commonPoint+" ... "+tJoint);
				horizontalVerticalList.remove(linei);
				change = true;
				break;
			}
		}
		return change;
	}

	/**
	 * @param change
	 * @param i
	 * @param linei
	 * @return
	 */
	private boolean checkJoinedLinesAndAddNewCluster(boolean change, int i,
			SVGLine linei) {
		for (int j = i+1; j < horizontalVerticalList.size(); j++) {
			SVGLine linej = horizontalVerticalList.get(j);
			if (linei.getCommonPoint(linej, eps) != null ||
					linei.makesTJointWith(linej, eps) ||
					linej.makesTJointWith(linei, eps)) {
				LineCluster cluster = new LineCluster(eps);
				clusterList.add(cluster);
				cluster.addLine(linei);
				cluster.addLine(linej);
				horizontalVerticalList.remove(linei);
				horizontalVerticalList.remove(linej);
				LOG.debug("made new cluster "+clusterList.size()+" .. "+linei+" .. "+linej);
				change = true;
				break;
			}
		}
		return change;
	}

	/**
	 * 
	 */
	private void mergeClusters() {
		while (true) {
			boolean change = false;
			for (int i = 0; i < clusterList.size()-1; i++) {
				LineCluster clusteri = clusterList.get(i);
				for (int j = i+1; j < clusterList.size(); j++) {
					LineCluster clusterj = clusterList.get(j);
					if (clusteri.containsCommonPoint(clusterj) ||
						clusteri.makesTJointWith(clusterj) ||
						clusterj.makesTJointWith(clusteri)) {
						clusteri.merge(clusterj);
						clusterList.remove(j);
						change = true;
						break;
					}
				}
			}
			if (!change) {
				break;
			}
		}
	}
	
// ------------------------------------------------

	private void groupRemainingNonGroupsAsUnknown() {
		getTopG();	
		List<SVGElement> elementList = SVGElement.getElementList(topG, "svg:*");
		SVGG newG = null;
		for (SVGElement child : elementList) {
			if (child instanceof SVGG) {
				newG = null;
			} else if (newG == null) {
				newG = new SVGG();
				newG.setClassName(UNKNOWN);
				topG.replaceChild(child, newG);
			} else {
				child.detach();
			}
			if (newG != null) {
				newG.appendChild(child);
			}
		}
		
	}

// ------------------------------------------------
	private void interpretUnknownsAndRemoveEmptyUnknowns() {
		getTopG();
		Nodes gNodes = topG.query("./svg:g[@class='"+UNKNOWN+"']", CMLConstants.SVG_XPATH);
		for (int i = 0; i < gNodes.size(); i++) {
			interpretUnknown((SVGG) gNodes.get(i));
		} 
		findFieldStrengths();
		findMolecules();
		removeEmptyUnknowns();
	}
	
	private void interpretUnknown(SVGG unknown) {
		getPeakAnnotations(unknown);
	}
	
	private void getPeakAnnotations(SVGG unknown) {
		// look for line*n (text, line|polyline) * n
		List<SVGElement> elementList = SVGElement.getElementList(unknown, "./svg:*");
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGElement element : elementList) {
			if (!(element instanceof SVGLine)) {
				break;
			}
			lineList.add((SVGLine) element);
		}
		LOG.debug("LIST "+lineList.size());
		if (lineList.size() > 0) {
			List<List<SVGElement>> gListList = new ArrayList<List<SVGElement>>();
			int i = lineList.size();
			while (i < elementList.size()) {
				SVGElement element = elementList.get(i++);
				List<SVGElement> gList = new ArrayList<SVGElement>();
				// add one or more texts
				while(element instanceof SVGText) {
					gList.add(element);
					element = elementList.get(i++);
				}
				// didn't find one
				if (gList.size() == 0) {
					break;
				}
				if (element instanceof SVGLine || element instanceof SVGPolyline) {
					gList.add(element);
				} else {
					break;
				}
				gListList.add(gList);
				// got enough?
				if (gListList.size() >= lineList.size()) {
					break;
				}
			}
			if (gListList.size() == lineList.size()) {
				SVGG g = new SVGG();
				int idx = topG.indexOf(unknown);
				topG.insertChild(g, idx);
				for (i = 0; i < lineList.size(); i++) {
					SVGG gg = new SVGG();
					gg.setClassName("peak");
					g.appendChild(gg);
					lineList.get(i).detach();
					gg.appendChild(lineList.get(i));
					for (SVGElement element : gListList.get(i)) {
						element.detach();
						gg.appendChild(element);
					}
				}
				LOG.debug("made peaklist");
				g.setClassName(PEAKLIST);
			}
		}
	}
	
// ------------------------------------------------
	/**
	 * 
	 */
	private void getTopG() {
		topG = (SVGG) svg.query("/svg:svg/svg:g", CMLConstants.SVG_XPATH).get(0);
	}
	
//------------------------------------------------	
	
	private void splitIntoSpectra() {
		getTopG();
		List<SVGElement> gList = SVGElement.getElementList(topG, "svg:g");
		spectrumToolList = new ArrayList<SpectrumAnalysisTool>();
		SVGG spectrumG = null;
		for (SVGElement g : gList) {
			if (VALUES.equals(g.getClassName())) {
				spectrumG = new SVGG();
				spectrumG.setClassName(SPECTRUM);
				topG.replaceChild(g, spectrumG);
				SpectrumAnalysisTool spectrumTool = new SpectrumAnalysisTool(spectrumG);
				spectrumToolList.add(spectrumTool);
			}
			if (spectrumG != null) {
				g.detach();
				spectrumG.appendChild(g);
			}
		}
	}

//------------------------------------------------	
}

