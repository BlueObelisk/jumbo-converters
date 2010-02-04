package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakList;
import org.xmlcml.cml.element.CMLPeakStructure;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSpectrum;

/*
<spectrum type="hnmr">
	1H NMR (
	<quantity type="frequency">
		<value>
			<point>400</point>
		</value>
		<units>MHz</units>
	</quantity>
	, DMSO) [delta]
	<peaks type="..">
		<peak>
			<quantity type="shift">
				<value>
					<point>13.2</point>
				</value>
			</quantity>
			(
			<quantity type="peaktype">s</quantity>
			,
			<quantity type="integral">
			...
			</quantity>
			)
		</peak>
		,
		<peak>
			<quantity type="shift">
				<value>
					<point>9.73</point>
				</value>
			</quantity>
			(
			<quantity type="peaktype">t</quantity>
			,
			<quantity type="integral">
				<value>
					<point>1</point>
				</value>
				<units>H</units>
			</quantity>
			)
		</peak>
		,
		<peak>
			<quantity type="shift">
				<value>
					<point>9.15</point>
				</value>
			</quantity>
			(
			<quantity type="peaktype">dd</quantity>
			,
			<quantity type="integral">
				<value>
					<point>1</point>
				</value>
				<units>H</units>
			</quantity>
			)
		</peak>
		,
		...
	</peaks>
</spectrum>

or 
    <peak>
      <quantity type="shift">
        <value>
          <point>6.96</point>
        </value>
      </quantity> (
      <quantity type="peaktype">td</quantity>, 
      <quantity type="integral">
        <value>
          <point>2</point>
        </value>
        <units>H</units>
      </quantity> J = 
      <quantity type="coupling">
        <value>
          <point>2.1</point>
        </value>, 
        <value>
          <point>8.8</point>
        </value>
        <units>Hz</units>
      </quantity>)
    </peak>, 

 */
public class OSCAR2CMLSpectHelper extends Object {

	private final static Logger LOG = Logger.getLogger(OSCAR2CMLSpectHelper.class);
	private CMLSpectrum cmlSpectrum;
	private Element oscarSpectrum;
	private CMLParameterList parameterList;
	
	public OSCAR2CMLSpectHelper() {
	}
	
	public CMLSpectrum parse(Element oscarSpectrum) {
		if (!"spectrum".equals(oscarSpectrum.getLocalName())) {
			throw new RuntimeException("must have spectrum element");
		}
		cmlSpectrum = new CMLSpectrum();
		this.oscarSpectrum = oscarSpectrum;
		processOscarSpectrum();
		return cmlSpectrum;
	}

	private void processOscarSpectrum() {
		CMLPeakList cmlPeakList = null;
		Elements peaks = oscarSpectrum.getChildElements("peaks");
		Elements quantityElements = oscarSpectrum.getChildElements("quantity");
		Nodes texts = oscarSpectrum.query("text()");
		if (peaks.size() + quantityElements.size() + texts.size() != oscarSpectrum.getChildCount()) {
			CMLUtil.debug(oscarSpectrum, "OSCAR");
			throw new RuntimeException("bad oscarSpectrum children");
		}
		processSpectrumQuantityElements(quantityElements);
		processPeaksElements(peaks);
		String type = oscarSpectrum.getAttributeValue("type");
		type = getCMLSpectrumType(type);
		if (type != null) {
			cmlSpectrum.setType(type);
		}
	}

	private String getCMLSpectrumType(String type) {
		String newType = null;
		if (type == null) {
			newType = null;
		} else if (type.equals("hnmr")) {
			newType = type;
		} else {
			newType = "unknown: "+type;
		}
		return newType;
	}

	private void processPeaksElements(Elements peaks) {
		if (peaks.size() != 1) {
			throw new RuntimeException("spectrum: only one <peaks> allowed; found "+peaks.size());
		}
		processPeaks((Element)peaks.get(0));
	}

	private void processSpectrumQuantityElements(Elements quantityElements) {
		for (int i = 0; i < quantityElements.size(); i++) {
			Element quantityElement = quantityElements.get(i);
			String type = quantityElement.getAttributeValue("type");
			if (type == null) {
				CMLUtil.debug(quantityElement, "QUANTITY");
				throw new RuntimeException("quantity must have type");
			} else if (type.equals("solvent")) {
				processSolvent(quantityElement);
			} else if (type.equals("frequency")) {
				processFrequency(quantityElement);
			} else {
				throw new RuntimeException("unknown type in spectrum: "+type);
			}
			
		}
	}

	private void processSolvent(Element solventElement) {
		if (solventElement.getChildElements().size() > 0) {
			throw new RuntimeException("solvent cannot have children");
		}
		String solvent = solventElement.getValue();
		CMLParameter solventParameter = new CMLParameter();
		CMLMolecule solventMolecule = new CMLMolecule();
		CMLName name = new CMLName();
		name.setXMLContent(solvent);
		solventMolecule.addName(name);
		solventParameter.appendChild(solventMolecule);
		solventParameter.setDictRef("cmlx:solvent");
		ensureParameterList();
		parameterList.addParameter(solventParameter);
	}

	private void ensureParameterList() {
		CMLElements parameterListElements = cmlSpectrum.getParameterListElements();
		if (parameterListElements.size() == 0) {
			parameterList = new CMLParameterList();
			cmlSpectrum.addParameterList(parameterList);
		}
	}

	private void processFrequency(Element frequencyElement) {
		Quantity frequencyQuantity = new Quantity(frequencyElement);
		if (frequencyQuantity.getValueElements().size() != 1 || frequencyQuantity.getPoint() == null) {
			throw new RuntimeException("frequency must have one value/point child");
		}
		String point = frequencyQuantity.getPoint();
		Double frequencyDouble = new Double(point);
		if (Double.isNaN(frequencyDouble)) {
			throw new RuntimeException("frequency value is not a double: "+point);
		}
		CMLScalar scalar = new CMLScalar(frequencyDouble);
		Units units = frequencyQuantity.getUnits();
		if (units == null) {
			throw new RuntimeException("frequency must have units");
		}
		String qunits = units.getQname();
		scalar.setUnits(qunits);
		CMLParameter frequencyParameter = new CMLParameter();
		frequencyParameter.setDictRef("cmlx:frequency");
		frequencyParameter.addScalar(scalar);
		ensureParameterList();
		parameterList.addParameter(frequencyParameter);
	}

	private void processPeaks(Element peaksElement) {
		CMLPeakList cmlPeakList = new CMLPeakList();
		for (int i = 0; i < peaksElement.getChildCount(); i++) {
			Node child = peaksElement.getChild(i);
			if (child instanceof Text) {
				String text = child.getValue().trim();
				if (!(text.equals("") || text.equals(CMLConstants.S_COMMA))) {
					LOG.error("unusual text child in <peaks> ["+text+"]");
				}
			} else if (child instanceof Element) {
				Element childElement = (Element) child;
				if ("peak".equals(childElement.getLocalName())) {
					Peak peak = new Peak(childElement);
					CMLPeak cmlPeak = peak.getCMLPeak();
					cmlPeakList.addPeak(cmlPeak);
				} else {
					throw new RuntimeException("Cannot process element: "+childElement.getLocalName());				
				}
			}
		}
		cmlSpectrum.addPeakList(cmlPeakList);
	}

	/**
		<peak>
			<quantity type="shift">
				<value>
					<point>13.2</point>
				</value>
			</quantity>
			(
			<quantity type="peaktype">s</quantity>
			,
			<quantity type="integral">
			...
			</quantity>
			)
		</peak>
	 * @param peakElement
	 */
	private CMLPeak processPeak(Element peakElement) {
		Elements quantityElements = peakElement.getChildElements("quantity");
		Elements unitsElements = peakElement.getChildElements("units");
		Elements childElements = peakElement.getChildElements();
		if (childElements.size() != quantityElements.size() + unitsElements.size()) {
			throw new RuntimeException("spectrum: bad element children of peak");
		}
		CMLPeak cmlPeak = new CMLPeak();
		for (int i = 0; i < quantityElements.size(); i++) {
			Quantity quantity = new Quantity(quantityElements.get(i));
			quantity.addToPeak(cmlPeak);
		}
		return cmlPeak;
	}
	/*
	<quantity type="shift">
	<value>
		<point>13.2</point>
	</value>
    </quantity>
	<quantity type="peaktype">dd</quantity>
	,
	<quantity type="integral">
		<value>
			<point>1</point>
		</value>
		<units>H</units>
	</quantity>
*/
	private void processQuantity(Element quantityElement, CMLPeak cmlPeak) {
		String type = quantityElement.getAttributeValue("type");
		Elements valueElements = quantityElement.getChildElements("value");
		Elements unitsElements = quantityElement.getChildElements("units");
		Elements childElements = quantityElement.getChildElements();
		if (childElements.size() != valueElements.size() + unitsElements.size()) {
			throw new RuntimeException("spectrum: bad element children of quantity");
		}
		if (type == null) {
			throw new RuntimeException("oscar quantity must have type");
		} else if (type.equals("shift") || type.equals("integral")) {
			if (valueElements.size() != 1) {
				CMLUtil.debug(quantityElement, "QUANTITY");
				throw new RuntimeException("spectrum: quantity requires 1 value element");
			}
			processValue(cmlPeak, valueElements.get(0), unitsElements, type);
		} else if (type.equals("coupling")) {
			processValues(cmlPeak, valueElements, unitsElements, type);
		} else if (type.equals("peaktype")) {
			if (quantityElement.getChildCount() == 1 &&
				quantityElement.getChild(0) instanceof Text) {
				String peaktype = quantityElement.getValue();
				cmlPeak.setPeakShape(peaktype);
			} else {
				throw new RuntimeException("bad peaktype in oscar quantity");
			}
		} else {
			CMLUtil.debug(oscarSpectrum, "SPECTRUMDEBUG");
			CMLUtil.debug(quantityElement, "QUANTITY");
			throw new RuntimeException("spectrum: unknown type: "+type);
		}
	}

	private void processValue(CMLPeak cmlPeak, Element valueElement,
			Elements unitsElements, String type) {
		Value value = new Value(valueElement);
		if ("integral".equals(type)) {
			if (value.point == null) {
				CMLUtil.debug(valueElement, "INTEGRAL");
				throw new RuntimeException("spectrum: missing point for integral");
			}
			cmlPeak.setIntegral(value.point);
		} else if ("value".equals(type)) {
			processXValue(cmlPeak, unitsElements, value);
		}
	}

	private void processValues(CMLPeak cmlPeak, Elements valueElements,
			Elements unitsElements, String type) {
		for (int i = 0; i < valueElements.size(); i++) {
			Element valueElement = valueElements.get(i);
			Value value = new Value(valueElement);
			if ("coupling".equals(type)) {
				if (value.point == null) {
					CMLUtil.debug(valueElement, "INTEGRAL");
					throw new RuntimeException("spectrum: missing point for integral");
				}
				processCouplings(cmlPeak, unitsElements, value);
				cmlPeak.setIntegral(value.point);
			} else {
				throw new RuntimeException("spectrum: cannot process "+type);
			}
		}
	}

	private void processCouplings(CMLPeak cmlPeak, Elements unitsElements,
			Value value) {
		if (value.point == null && value.min == null && value.max == null) {
			throw new RuntimeException("spectrum: no children of coupling");
		}
		if (value.point != null) {
			CMLPeakStructure peakStructure = new CMLPeakStructure();
			peakStructure.setType("coupling");
			peakStructure.setCMLValue(value.point);
			if (unitsElements.size() == 1) {
				peakStructure.setUnits(getUnits(unitsElements.get(0).getValue()));
			} else if (unitsElements.size() > 0) {
				throw new RuntimeException("spectrum: coupling must have only 1 units element");
			}
			cmlPeak.addPeakStructure(peakStructure);
		}
	}

	private String getUnits(String value) {
		return "unit:"+value;
	}

	private void processXValue(CMLPeak cmlPeak, Elements unitsElements,
			Value value) {
		if (value.point == null && value.min == null && value.max == null) {
			throw new RuntimeException("spectrum: no children of value");
		}
		if (value.point != null) {
			cmlPeak.setXValue(value.point);
		}
		if (value.min != null) {
			cmlPeak.setXMin(value.min);
		}
		if (value.max != null) {
			cmlPeak.setXMax(value.max);
		}
		if (unitsElements.size() == 1) {
			setUnits(unitsElements.get(0), cmlPeak);
		} else if (unitsElements.size() > 0) {
			throw new RuntimeException("spectrum: must have only 1 units element");
		}
	}


	private void setUnits(Element quantityElement, CMLPeak cmlPeak) {
		Element units = quantityElement.getFirstChildElement("units");
		if (units != null) {
			cmlPeak.setPeakUnits(units.getValue());
		}
	}

//	private void setUnits(Element quantityElement, CMLSpectrum cmlSpectrum) {
//		Element units = quantityElement.getFirstChildElement("units");
//		if (units != null) {
//			cmlSpectrum.set (units.getValue());
//		}
//	}

//	private String getValue(Element quantityElement, String type) {
//		Nodes points = quantityElement.query("./*[local-name()='value']/*[local-name()='point']");
//		LOG.trace(points.size());
//		Nodes units = quantityElement.query("./*[local-name()='units']");
//		LOG.trace(units.size());
//		if (points.size() != 1 || (units.size() + points.size()) != quantityElement.getChildElements().size()) {
//			CMLUtil.debug(quantityElement, "TYPE "+type);
//			throw new RuntimeException("bad contents of "+type);
//		}
//		return points.get(0).getValue();
//	}
}

