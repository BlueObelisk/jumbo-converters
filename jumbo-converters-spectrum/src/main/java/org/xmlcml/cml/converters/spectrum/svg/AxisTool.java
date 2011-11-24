package org.xmlcml.cml.converters.spectrum.svg;

import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Util;

public class AxisTool {
	private static Logger LOG = Logger.getLogger(AxisTool.class);
	
	// not necessarily X and Y as spectrum may be portrait or landscape
	public enum Orientation {
		HORIZONTAL,
		VERTICAL
	}

	public final static int ISCALE = 100;
	private double eps = 0.01;
	private double length = Double.NaN;
	private Orientation orientation;
	private SVGLine axialLine;
	Map<Integer, List<SVGLine>> lengthLineMap = null;
	private SVGElement g;
	private Integer majorTickLength;
	private Integer minorTickLength;
	private TickInfo minorTickInfo;
	private TickInfo majorTickInfo;
	private TickInfo textValueInfo;
	private TickInfo textCoordInfo;
	private Real2Range boundingBox;
	private double screenCoordPerUnitTick;
	private double originInScreenCoordinates;
	private boolean dataAxis;
	
	public AxisTool(Orientation orientation) {
		this.orientation = orientation; 
	}
	
	public void addAndAnalyzeG(SVGElement g) {
		this.g = g;
		boundingBox = g.getBoundingBox();
		getLengthBinsAndAnnotateTicks();
//		getStepSizes();
//		List<String> textList = new ArrayList<String>();
		
	}

	/**
	 * @param nodes
	 * @throws RuntimeException
	 */
	private void getLengthBinsAndAnnotateTicks() throws RuntimeException {
		// interpret lines
		Nodes nodes = g.query(".//svg:line", CMLConstants.SVG_XPATH);
		if (nodes.size() == 0) {
			throw new RuntimeException("no lines in axis");
		}
		axialLine = null;
		lengthLineMap = new HashMap<Integer, List<SVGLine>>();
		for (int i = 0; i < nodes.size(); i++) {
			SVGLine line = (SVGLine) nodes.get(i);
			if (orientation.equals(Orientation.HORIZONTAL) && line.isHorizontal(eps) ||
				orientation.equals(Orientation.VERTICAL) && line.isVertical(eps)) {
				// get axial line
				if (axialLine == null) {
					axialLine = line;
					Real2Range bbox = line.getBoundingBox();
					length = (orientation.equals(Orientation.HORIZONTAL)) ?
							bbox.getXRange().getRange() : bbox.getYRange().getRange();
				} else {
					throw new RuntimeException("Duplicate line on "+orientation+" axis");
				}
			} else {
				LOG.trace("BINNING "+line);
				binTickLinesByLength(line);
			}
		}
		if (axialLine == null) {
			throw new RuntimeException("NO axial line");
		} 
		identifyTickTypesAndLengths();
		LOG.trace("\nAXNEW ------------------------\n"+toString()+"\n------------------------");
		
	}

	/**
	 * @throws RuntimeException
	 */
	private void identifyTickTypesAndLengths() throws RuntimeException {
		int nTickTypes = lengthLineMap.size();
		if (nTickTypes == 0) {
			throw new RuntimeException("No ticks");
		} else if (nTickTypes > 2) {
			throw new RuntimeException("Too many tick types");
		}
		majorTickLength = null;
		minorTickLength = null;
		for (Integer iLength : lengthLineMap.keySet()) {
//			LOG.debug("LEN "+iLength);
			if (majorTickLength == null) {
				majorTickLength = iLength;
			} else if (iLength < majorTickLength) {
				minorTickLength = iLength;
			} else {
				minorTickLength = majorTickLength;
				majorTickLength = iLength;
			}
		}
		
		
		textCoordInfo = new TickInfo();
		analyzeTextCoords(textCoordInfo);
		LOG.debug("textTick "+textCoordInfo);
		
		textValueInfo = new TickInfo();
		analyzeTickTexts(textValueInfo);
//		LOG.debug("textTick "+textTickInfo);
		
		minorTickInfo = new TickInfo();
		analyzeTicks(minorTickLength, minorTickInfo);
//		LOG.debug("minorTick "+minorTickInfo);
		
		if (majorTickLength != null) {
			majorTickInfo = new TickInfo();
			analyzeTicks(majorTickLength, majorTickInfo);
//			LOG.debug("majorTick "+majorTickInfo);
		}
		if (majorTickInfo != null && textValueInfo != null) {
			int majorCount = majorTickInfo.getStepCount();
			int textCount = textValueInfo.getStepCount();
			if (majorCount == textCount) {
				getOriginAndScale();
			} else {
//				throw new RuntimeException("BAD MAJOR TICK: "+majorTickInfo.getStepCount() +" != "+textTickInfo.getStepCount());
				LOG.warn("BAD MAJOR TICK: "+majorCount +" != "+textCount);
				// one extra tick (quite common)
				if (majorCount == textCount + 1) {
					LOG.debug("..."+textCoordInfo.getStart() +" .. "+ majorTickInfo.getStart());
					LOG.debug("..."+textCoordInfo.getEnd() +" .. "+ majorTickInfo.getEnd());
					double deltaStart = textCoordInfo.getStart() - majorTickInfo.getStart();
					double deltaEnd = textCoordInfo.getEnd() - majorTickInfo.getEnd();
					LOG.debug("..."+deltaStart+"/"+deltaEnd);
					if (Math.abs(deltaStart) < Math.abs(deltaEnd)) {
						majorTickInfo.deleteLast();
						LOG.info("adjust end");
					} else {
						majorTickInfo.deleteFirst();
						LOG.info("adjust start)");
					}
					getOriginAndScale();
				}
			}
		}
	}

	/**
	 * 
	 */
	private void getOriginAndScale() {
		screenCoordPerUnitTick = 
		(textValueInfo.getStart() - textValueInfo.getEnd()) /
		(majorTickInfo.getStart() - majorTickInfo.getEnd());
		originInScreenCoordinates = majorTickInfo.getStart() - textValueInfo.getStart() / screenCoordPerUnitTick;
		LOG.debug("TICK Value: "+screenCoordPerUnitTick + " origin "+originInScreenCoordinates);
	}
	
	private void analyzeTextCoords(TickInfo tickInfo) {
		Nodes texts = g.query(".//svg:text", CMLConstants.SVG_XPATH);

		// screen out title by value
		List<SVGText> textList = new ArrayList<SVGText>();
		for (int i = 0; i < texts.size(); i++) {
			SVGText text = (SVGText) texts.get(i);
			try {
				new Double(text.getValue());
			} catch (NumberFormatException  nfe) {
				continue;
			}
			textList.add(text);
		}

		for (int i = 0; i < textList.size(); i++) {
			SVGText text = (SVGText) textList.get(i);
			Real2 xy = text.getXY();
			LOG.debug("T "+xy+" / "+text.getValue());
			double d = (orientation.equals(Orientation.HORIZONTAL)) ? xy.getX() : xy.getY();
			tickInfo.addValue(d);
		}
	}
	
	private void analyzeTickTexts(TickInfo tickInfo) {
		Nodes texts = g.query(".//svg:text", CMLConstants.SVG_XPATH);
		for (int i = 0; i < texts.size(); i++) {
			SVGText text = (SVGText) texts.get(i);
			String value = text.getValue();
			Double d = null;
			try {
				d = new Double(value);
			} catch (Exception e) {
				if (tickInfo.getTitle() == null) {
					LOG.info("Taking annotation as title "+value);
				} else {
					throw new RuntimeException("Cannot interpret axial annotation(s) as number: "+value+ "( also ... "+tickInfo.getTitle()+")");
				}
				continue;
			}
			tickInfo.addValue(d);
		}
		tickInfo.checkSteps(0.1);
	}
	
	private void analyzeTicks(Integer tickLength, TickInfo tickInfo) {
		if (tickLength == null) {
			LOG.warn("NULL TICKLENGTH");
		} else {
			List<SVGLine> lineList = lengthLineMap.get(tickLength);
			if (lineList == null) {
				LOG.warn("no axis of length: "+(double)tickLength/(double) ISCALE);
			} else {
				LOG.trace("LINES..."+lineList.size());
				for (SVGLine line : lineList) {
					double posx = (orientation == Orientation.HORIZONTAL) ?
							line.getReal2Range().getXRange().getMax() :
							line.getReal2Range().getYRange().getMax();
							LOG.trace("POSX "+posx);
							tickInfo.addValue(posx);
				}
				tickInfo.checkSteps(0.5);
			}
		}
	}

	/**
	 * @param line
	 */
	private void binTickLinesByLength(SVGLine line) {
		// put into bins
		// PERPENDICULAR to axial direction
		double length = (orientation.equals(Orientation.HORIZONTAL)) ?
				line.getBoundingBox().getYRange().getRange() :
				line.getBoundingBox().getXRange().getRange();
//		double axialPos = (orientation.equals(Orientation.HORIZONTAL)) ?
//						line.getBoundingBox().getXRange().getRange() :
//						line.getBoundingBox().getYRange().getRange();
		Integer iLength = new Integer((int)(Util.format(length, 2) * ISCALE));
		List<SVGLine> lines = lengthLineMap.get(iLength);
		if (lines == null) {
			lines = new ArrayList<SVGLine>();
			lengthLineMap.put(iLength, lines);
		}
		lines.add(line);
	}

	/** index axes by length*ISCALE
	 * 
	 * @param axisToolList
	 * @param axisToolMap
	 */
	public static void binAxesByLength(List<AxisTool> axisToolList, Map<Integer, List<AxisTool>> axisToolMap) {
		if (axisToolList == null) {
			throw new RuntimeException("Null axisToolList");
		}
		for (AxisTool axisTool : axisToolList) {
			Integer iLength = (int) axisTool.getLength()*ISCALE;
			List<AxisTool> axisToolListx = axisToolMap.get(iLength);
			if (axisToolListx == null) {
				axisToolListx = new ArrayList<AxisTool>();
				axisToolMap.put(iLength, axisToolListx);
			}
			axisToolListx.add(axisTool);
		}
	}
	
	public static void analyze(Map<Integer, List<AxisTool>> lengthAxisToolListMap) {
		LOG.debug("ANALYZE axis: lengths "+lengthAxisToolListMap.size());
		for (Integer iLength : lengthAxisToolListMap.keySet()) {
			double length = (double) iLength / (double) ISCALE;
			List<AxisTool> axisToolList = lengthAxisToolListMap.get(iLength);
			LOG.debug("AXES: length "+length+" size: "+axisToolList.size());
		}
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public double getLength() {
		return length;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public SVGLine getAxialLine() {
		return axialLine;
	}

	public Map<Integer, List<SVGLine>> getLengthLineMap() {
		return lengthLineMap;
	}

	public Real2Range getBoundingBox() {
		return boundingBox;
	}
	
	public String toString() {
		/*
			private double eps = 0.01;
			private double length = Double.NaN;
			private Orientation orientation;
			private SVGLine axialLine;
			Map<Integer, List<SVGLine>> lengthLineMap = null;
			private SVGElement g;
			private Integer majorTickLength;
			private Integer minorTickLength;
			private TickInfo minorTickInfo;
			private TickInfo majorTickInfo;
			private TickInfo textTickInfo;
			private Real2Range boundingBox;
		 */		
				String s = "";
				s += "length: "+length+S_NEWLINE;
				s += " orientation: "+orientation+S_NEWLINE;
				s += " axialLine: "+axialLine+S_NEWLINE;
				s += " lengthLineMap: "+((lengthLineMap == null) ? "null" : ""+lengthLineMap.size())+S_NEWLINE;
				s += " g: "+((g == null) ? "null" : g.getClassName()+" "+g.getBoundingBox())+S_NEWLINE;
				s += " majorTickLength "+majorTickLength+S_NEWLINE;
				s += " minorTickLength "+minorTickLength+S_NEWLINE;
				s += " majorTickInfo "+majorTickInfo+S_NEWLINE;
				s += " minorTickInfo "+minorTickInfo+S_NEWLINE;
				s += " textTickInfo "+textValueInfo+S_NEWLINE;
				s += " bbox "+boundingBox;
				
				return s;
		}

	public boolean isDataAxis() {
		return dataAxis;
	}

	public void setDataAxis(boolean dataAxis) {
		this.dataAxis = dataAxis;
	}

	public double getScreenCoordPerUnitTick() {
		return screenCoordPerUnitTick;
	}

	public double getOriginInScreenCoordinates() {
		return originInScreenCoordinates;
	}
}

class TickInfo {
	// fixme - should use realArray
	private double start = Double.NaN;
	private double end = Double.NaN;
	private double step = Double.NaN;
	private int stepCount = 0;
	private String title = null;
	private RealArray array;
	
	public TickInfo() {
		array = new RealArray();
	}
	
	public void addValue(double d) {
		array.addElement(d);
	}
	
	public void checkSteps(double eps) {
		double step = getStep();
		for (int i = 1; i < array.size(); i++) {
			double step0 = array.get(i) - array.get(i-1);
			if (!Real.isEqual(step, step0, eps)) {
				throw new RuntimeException("unequal step: "+(i-1)+" => "+i+" : "+step0 + " != " + step);
			}
		}
	}
	public String toString() {
		return ""+start+ "("+Util.format(step, 4)+")"+ end;
	}
	public double getStart() {
		return array.get(0);
	}
	
	public double getEnd() {
		return array.get(array.size() -1);
	}
	
	public RealArray getArray() {
		return array;
	}
	
	public double getStep() {
		return (getEnd() - getStart()) / getStepCount();
	}
	
	public int getStepCount() {
		return (array.size() - 1);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void deleteFirst() {
		array.deleteElement(0);
	}
	
	public void deleteLast() {
		array.deleteElement(array.size() - 1);
	}
}
