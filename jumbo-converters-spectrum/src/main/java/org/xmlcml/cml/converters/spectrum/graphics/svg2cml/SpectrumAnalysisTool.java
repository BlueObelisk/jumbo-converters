package org.xmlcml.cml.converters.spectrum.graphics.svg2cml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.spectrum.graphics.svg2cml.AxisTool.Orientation;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakList;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumData;
import org.xmlcml.cml.element.CMLXaxis;
import org.xmlcml.cml.element.CMLYaxis;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPoly;
import org.xmlcml.cml.graphics.SVGPolyline;
import org.xmlcml.cml.graphics.SVGRect;
import org.xmlcml.cml.tools.PeakTool;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Util;

public class SpectrumAnalysisTool {
	private final static Logger LOG = Logger.getLogger(SpectrumAnalysisTool.class);
//	static {
//		LOG.setLevel(Level.TRACE);
//	}
	
	private SVGG spectrumG;
	private AxisTool horizontalAxis;
	private AxisTool verticalAxis;
	private SVGRect box;
	private Orientation spectrumDataOrientation;
	private SVGPolyline spectrumValues;
	private double eps;

	private AxisTool dataAxis;
	private AxisTool intensityAxis;

	private SVGG svgGPeakListContainer;

	private CMLPeakList cmlPeakList;

	private CMLSpectrum cmlSpectrum;
	
	public SpectrumAnalysisTool(SVGG g) {
		this.spectrumG = g;
		init();
	}
	
	private void init() {
		this.eps = 0.01;
		cmlSpectrum = new CMLSpectrum();
	}
	
	public void process() {
		getSpectrumDataAndOrientation();
    	interpretAxesAndGetAxisToolLists();
    	checkBoxMatchesAxes();
    	calculatePeakListValues();
		addDataToSpectrum();
	}
	
	private void getSpectrumDataAndOrientation() {
		List<SVGElement> valuesList = SVGElement.generateElementList(spectrumG, "./svg:g[@class='"+SVG2CMLSpectTool.VALUES+"']/svg:polyline");
		spectrumValues = (valuesList.size() == 0) ? null : (SVGPolyline) valuesList.get(0);
		String xMonotonicity = (spectrumValues == null) ? null : spectrumValues.getAttributeValue(SVGPoly.MONOTONIC+"X");
		String yMonotonicity = (spectrumValues == null) ? null : spectrumValues.getAttributeValue(SVGPoly.MONOTONIC+"Y");
		LOG.trace("MX MY "+xMonotonicity+"/"+yMonotonicity);
		if (xMonotonicity == null) {
			spectrumDataOrientation = (yMonotonicity == null) ? null : Orientation.VERTICAL;
		} else {
			spectrumDataOrientation = (yMonotonicity != null) ? null : Orientation.HORIZONTAL;
		}
		if (spectrumDataOrientation == null) {
			throw new RuntimeException("Cannot find data orientation");
		}

	}

	/**
	 * 
	 */
	private void addDataToSpectrum() {
		/*
	    <xaxis>
	        <array dataType="xsd:double" units="unit:nm">
	 */		
		CMLSpectrumData spectrumData = new CMLSpectrumData();
		Real2Array real2Array = spectrumValues.getReal2Array();
		RealArray horizontalUserValues = scaleScreenToUser(real2Array.getXArray(), horizontalAxis);
		RealArray verticalUserValues = scaleScreenToUser(real2Array.getYArray(), verticalAxis);
		RealArray xArray = (dataAxis.equals(horizontalAxis)) ?
				horizontalUserValues : verticalUserValues;
		xArray.format(2);
		RealArray yArray = (dataAxis.equals(horizontalAxis)) ?
				verticalUserValues : horizontalUserValues;
		yArray.format(1);
		CMLXaxis xAxis = new CMLXaxis();
		xAxis.addArray(new CMLArray(xArray));
		spectrumData.addXaxis(xAxis);
		CMLYaxis yAxis = new CMLYaxis();
		yAxis.addArray(new CMLArray(yArray));
		spectrumData.addYaxis(yAxis);
		cmlSpectrum.addSpectrumData(spectrumData);
	}

	/**
	 * @param screenValues
	 */
	private RealArray scaleScreenToUser(RealArray screenValues, AxisTool axis) {
		
//		calcDataValue = (dataCoord  - dataOrigin) * screenToUser;
		RealArray userValues = null;
		if (axis == null) {
			userValues = screenValues;
		} else {
			double dataOrigin = axis.getOriginInScreenCoordinates();
			double screenToUser = axis.getScreenCoordPerUnitTick();
			userValues = screenValues.addScalar(-dataOrigin);
			userValues = userValues.multiplyBy(screenToUser);
		}
		return userValues;
	}
	
	void interpretAxesAndGetAxisToolLists() {
		horizontalAxis = interpretAxisAndGetAxisTool(Orientation.HORIZONTAL);
		verticalAxis = interpretAxisAndGetAxisTool(Orientation.VERTICAL);
		dataAxis = null;
		intensityAxis = null;
		if (horizontalAxis != null && Orientation.HORIZONTAL.equals(spectrumDataOrientation)) {
			horizontalAxis.setDataAxis(true);
			if (verticalAxis != null) {
				verticalAxis.setDataAxis(false);
			}
			dataAxis = horizontalAxis;
			intensityAxis = verticalAxis;
		}
		if (verticalAxis != null && Orientation.VERTICAL.equals(spectrumDataOrientation)) {
			verticalAxis.setDataAxis(true);
			if (horizontalAxis != null) {
				horizontalAxis.setDataAxis(false);
			}
			dataAxis = verticalAxis;
			intensityAxis = horizontalAxis;
		}
		if (dataAxis == null) {
			throw new RuntimeException("Cannot find axis aligned with data");
		}
	}

	/**
	 * 
	 */
	private AxisTool interpretAxisAndGetAxisTool(Orientation orientation) {
		AxisTool axisTool = null;
		List<SVGElement> axisGroups = SVGElement.generateElementList(spectrumG, "./svg:g[@class='"+orientation.toString()+"']");
		LOG.trace("AXES: "+axisGroups.size());
		SVGG axisGroup = (axisGroups.size() > 0) ? (SVGG) axisGroups.get(0) : null; 
		if (axisGroup != null) {
			axisTool = new AxisTool(orientation);
			axisTool.setEps(eps);
			axisTool.addAndAnalyzeG(axisGroup);
		}
		return axisTool;
	}

	private void checkBoxMatchesAxes() {
		List<SVGElement> boxList = SVGElement.generateElementList(spectrumG, "./svg:g/svg:rect[@class='"+SVG2CMLSpectTool.BOX+"']");
		box = (boxList.size() == 0) ? null : (SVGRect) boxList.get(0);
		if (box != null && dataAxis != null) {
			Real2Range bbox = box.getBoundingBox();
			RealRange boxXRange = bbox.getXRange();
			RealRange horizontalRange = horizontalAxis.getBoundingBox().getXRange();
			if (!boxXRange.isEqualTo(horizontalRange, 1.0)) {
				throw new RuntimeException("Horizontal axis does not match box limits");
			}
			RealRange boxYRange = bbox.getYRange();
			RealRange verticalRange = verticalAxis.getBoundingBox().getYRange();
			if (!boxYRange.isEqualTo(verticalRange, 1.0)) {
				throw new RuntimeException("Vertical axis does not match box limits");
			}
		}
	}
	
	private void calculatePeakListValues() {
		List<SVGElement> elements = SVGElement.generateElementList(
				spectrumG, "./svg:g[@class='"+SVG2CMLSpectTool.PEAKLIST+"']");
		svgGPeakListContainer = (elements.size() == 0) ? null : (SVGG) elements.get(0);
		cmlPeakList = new CMLPeakList();
		cmlSpectrum.addPeakList(cmlPeakList);
		if (svgGPeakListContainer != null) {
			elements = SVGElement.generateElementList(svgGPeakListContainer, "./svg:g[@class='"+SVG2CMLSpectTool.PEAK+"']");
			List<SVGG> svgPeakList = new ArrayList<SVGG>();
			for (int i = 0; i < elements.size(); i++) {
				SVGG svgPeak = (SVGG) elements.get(i);
				svgPeakList.add(svgPeak);
				CMLPeak cmlPeak = calculateValuesAndCreatePeak(svgPeak);
				cmlPeakList.addPeak(cmlPeak);
			}
		}
	}
	
	private CMLPeak calculateValuesAndCreatePeak(SVGG svgPeak) {
		CMLPeak cmlPeak = new CMLPeak();
		PeakTool peakTool = new PeakTool(cmlPeak);
		
		double dataCoord = Double.NaN;
		double dataValue = Double.NaN;
		double integralValue = Double.NaN;
		double calcDataValue = Double.NaN;

		List<SVGElement> textList = SVGElement.generateElementList(svgPeak, "./svg:text");
		String textValue = (textList.size() == 0) ? null : textList.get(0).getValue();
		if (textValue != null) {
			try {
				dataValue = new Double(textValue).doubleValue();
			} catch (NumberFormatException e) {
				throw new RuntimeException("Cannot parse data value as double: "+textValue);
			}
		}
		List<SVGElement> brackets = SVGElement.generateElementList(svgPeak, "./svg:g[@class='bracket']");
		SVGG bracket = (brackets.size() == 0) ? null : (SVGG) brackets.get(0);
		if (bracket != null) {
			Real2 bracketCentroid = bracket.getBoundingBox().getCentroid();
			dataCoord = (dataAxis.equals(horizontalAxis)) ? bracketCentroid.getX() : bracketCentroid.getY();
		} else {
			List<SVGElement> lines = SVGElement.generateElementList(svgPeak, "./svg:line");
			if (lines.size() > 0) {
				SVGLine line = (SVGLine) lines.get(0);
				dataCoord = (dataAxis.equals(horizontalAxis)) ? line.getXY(0).getX() : line.getXY(0).getY();
			}
		}
		if (bracket != null) {
			integralValue = dataValue;
			dataValue = Double.NaN;
		}
		if (!Double.isNaN(dataCoord)) {
			double dataOrigin = dataAxis.getOriginInScreenCoordinates();
			double screenToUser = dataAxis.getScreenCoordPerUnitTick();
			LOG.trace("SCREEN to user "+screenToUser + dataOrigin);
			calcDataValue = (dataCoord  - dataOrigin) * screenToUser;
			if (!Double.isNaN(dataValue) && !Real.isEqual(dataValue, calcDataValue, 0.1)) {
				LOG.warn("BAD PEAK: "+dataCoord+" / "+dataOrigin+" / "+dataValue +"/" + calcDataValue);
			}
			LOG.trace("PEAK: "+ calcDataValue + " ["+integralValue+"]");
		}
		cmlPeak.setXValue(Util.format(calcDataValue, 2));
		if (!Double.isNaN(integralValue)) {
			cmlPeak.setIntegral(""+Util.format(integralValue, 2));
		}
		//cmlPeak.debug("PK");
		return cmlPeak;
	}

	public CMLSpectrum getCmlSpectrum() {
		return cmlSpectrum;
	}

}
