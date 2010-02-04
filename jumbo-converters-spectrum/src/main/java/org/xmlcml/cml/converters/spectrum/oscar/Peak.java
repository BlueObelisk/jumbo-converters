package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakStructure;
import org.xmlcml.cml.tools.PeakTool;

public class Peak {

	private final static Logger LOG = Logger.getLogger(Peak.class);
	private Elements quantityElements;
	private CMLPeak cmlPeak;
	private Nodes texts;
	
	public Peak(Element peakElement) {
		cmlPeak = new CMLPeak();
		quantityElements = peakElement.getChildElements("quantity");
		texts = peakElement.query("text()");
		if (quantityElements.size() != peakElement.getChildElements().size()) {
			throw new RuntimeException("peak must only have quantity children");
		}
//		List<Quantity> quantityList = new ArrayList<Quantity>();
		for (int i = 0; i < quantityElements.size(); i++) {
			Quantity quantity = new Quantity(quantityElements.get(i));
//			Elements valueElements = quantity.valueElements;
//			Units units = quantity.getUnits();
			String type = quantity.getType();
			if (type == null) {
				throw new RuntimeException("quantity cannot have null type");
			} else if (type.equals("shift")) {
				processShift(quantity);
			} else if (type.equals("integral")) {
				processIntegral(quantity);
			} else if (type.equals("coupling")) {
				processCoupling(quantity);
			} else if (type.equals("peaktype")) {
				processPeakType(quantity);
			} else if (type.equals("comment")) {
				processComment(quantity);
			} else {
				throw new RuntimeException("spectrum: unknown type: "+type);
			}
		}
	}

	private void processComment(Quantity quantity) {
		if (quantity.getTextValue() != null) {
			CMLMetadata metadata = new CMLMetadata();
			metadata.setContent(quantity.getTextValue());
			cmlPeak.appendChild(metadata);
		}
	}

	private void processPeakType(Quantity quantity) {
		String peakType = quantity.getTextValue();
		if (peakType != null) {
			
			String peakShape = PeakTool.guessShape(peakType);
			if (peakShape != null) {
				cmlPeak.setPeakShape(peakShape);
			}
			String peakMultiplicity = PeakTool.guessMultiplicity(peakType);
			if (peakMultiplicity != null) {
				cmlPeak.setPeakMultiplicity(peakMultiplicity);
			}
			if (peakShape == null && peakMultiplicity == null) {
//				throw new RuntimeException("Cannot interpret peakType: "+peakType);
				LOG.error("Cannot interpret peakType: "+peakType);
			}
		}
	}

	private void processShift(Quantity quantity) {
		Double point = getDouble(quantity.getPoint());
		boolean isSet = false;
		if (!Double.isNaN(point)) {
			cmlPeak.setXValue(point);
			isSet = true;
		}
		Double min = getDouble(quantity.getMin());
		if (!Double.isNaN(min)) {
			cmlPeak.setXMin(min);
			isSet = true;
		}
		Double max = getDouble(quantity.getMax());
		if (!Double.isNaN(max)) {
			cmlPeak.setXMax(max);
			isSet = true;
		}
		if (!isSet) {
			throw new RuntimeException("No value found for shift");
		}
		Units units = quantity.getUnits();
		if (units != null) {
			cmlPeak.setXUnits(units.getQname());
		}
	}

	private void processIntegral(Quantity quantity) {
		Double point = getDouble(quantity.getPoint());
		boolean isSet = false;
		if (!Double.isNaN(point)) {
			cmlPeak.setIntegral(point.toString());
			isSet = true;
		}
		if (!isSet) {
			throw new RuntimeException("No value found for integral");
		}
		Units units = quantity.getUnits();
		if (units != null) {
			cmlPeak.setYUnits(units.getQname());
		}
	}
	
	private void processCoupling(Quantity quantity) {
		Double point = getDouble(quantity.getPoint());
		boolean isSet = false;
		if (!Double.isNaN(point)) {
			CMLPeakStructure peakStructure = new CMLPeakStructure();
			peakStructure.setType("coupling");
			peakStructure.setCMLValue(point.toString());
			if (quantity.getUnits() != null) {
				peakStructure.setUnits(quantity.getUnits().getQname());
			}
			cmlPeak.addPeakStructure(peakStructure);
			isSet = true;
		}
		if (!isSet) {
			throw new RuntimeException("No value found for coupling");
		}
	}
	private static Double getDouble(String s) {
		Double d = Double.NaN;
		if (s != null) {
			try {
				d = new Double(s);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse double: "+s);
			}
		}
		return d;
	}

	public CMLPeak getCMLPeak() {
		return cmlPeak;
	}
}
