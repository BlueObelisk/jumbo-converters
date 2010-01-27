package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakList;
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
 */
public class OSCAR2CMLSpectHelper extends Object {

	private final static Logger LOG = Logger.getLogger(OSCAR2CMLSpectHelper.class);
	public OSCAR2CMLSpectHelper() {
		
	}
	
	public CMLSpectrum parse(Element oscarSpectrum) {
		if (!"spectrum".equals(oscarSpectrum.getLocalName())) {
			throw new RuntimeException("must have spectrum element");
		}
		CMLSpectrum cmlSpectrum = new CMLSpectrum();
		CMLPeakList cmlPeakList = processOscarSpectrum(oscarSpectrum, cmlSpectrum);
		return cmlSpectrum;
	}

	private CMLPeakList processOscarSpectrum(Element oscarSpectrum, CMLSpectrum cmlSpectrum) {
		CMLPeakList cmlPeakList = null;
		Elements peaks = oscarSpectrum.getChildElements("peaks");
		Elements quantity = oscarSpectrum.getChildElements("quantity");
		Nodes texts = oscarSpectrum.query("text()");
		if (peaks.size() + quantity.size() + texts.size() != oscarSpectrum.getChildCount() ||
				quantity.size() > 1 || peaks.size() != 1 ) {
			throw new RuntimeException("bad oscarSpectrum children");
		}
		cmlPeakList = processPeaks((Element)peaks.get(0));
		processNMRFrequency(cmlSpectrum, quantity);
		cmlSpectrum.addPeakList(cmlPeakList);
		return cmlPeakList;
	}

	private void processNMRFrequency(CMLSpectrum cmlSpectrum, Elements quantity) {
		if (quantity.size() == 1) {
//			processQuantity(quantity.get(0), cmlSpectrum);
		}
	}

	private CMLPeakList processPeaks(Element peaksElement) {
		CMLPeakList cmlPeakList = new CMLPeakList();
		for (int i = 0; i < peaksElement.getChildCount(); i++) {
			Node child = peaksElement.getChild(i);
			if (child instanceof Text) {
				; // cannot process text at this stage
			} else if (child instanceof Element) {
				Element childElement = (Element) child;
				if ("peak".equals(childElement.getLocalName())) {
					CMLPeak peak = processPeak(childElement);
					cmlPeakList.addPeak(peak);
				} else {
					throw new RuntimeException("Cannot process element: "+childElement.getLocalName());				
				}
			}
		}
		return cmlPeakList;
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
		CMLPeak cmlPeak = new CMLPeak();
		for (int i = 0; i < peakElement.getChildCount(); i++) {
			Node child = peakElement.getChild(i);
			if (child instanceof Text) {
				; // cannot process text at this stage
			} else if (child instanceof Element) {
				Element childElement = (Element) child;
				if ("quantity".equals(childElement.getLocalName())) {
					processQuantity(childElement, cmlPeak);
				} else {
					throw new RuntimeException("Cannot process element: "+childElement.getLocalName());				
				}
			}
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
		if (type == null) {
			throw new RuntimeException("oscar quantity must have type");
		} else if (type.equals("shift")) {
			String value = getValue(quantityElement,type);
			cmlPeak.setXValue(value);
			setUnits(quantityElement, cmlPeak);
		} else if (type.equals("integral")) {
			String value = getValue(quantityElement, type);
			cmlPeak.setIntegral(value);
		} else if (type.equals("peaktype")) {
			if (quantityElement.getChildCount() == 1 &&
				quantityElement.getChild(0) instanceof Text) {
				String peaktype = quantityElement.getValue();
				cmlPeak.setPeakShape(peaktype);
			} else {
				throw new RuntimeException("bad peaktype in oscar quantity");
			}
		} else {
			throw new RuntimeException("oscar quantity has unknown type: "+type);
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

	private String getValue(Element quantityElement, String type) {
		Nodes points = quantityElement.query("./*[local-name()='value']/*[local-name()='point']");
		LOG.trace(points.size());
		Nodes units = quantityElement.query("./*[local-name()='units']");
		LOG.trace(units.size());
		if (points.size() != 1 || (units.size() + points.size()) != quantityElement.getChildElements().size()) {
			CMLUtil.debug(quantityElement, "TYPE "+type);
			throw new RuntimeException("bad contents of "+type);
		}
		return points.get(0).getValue();
	}
}
