package org.xmlcml.cml.converters.spectrum.agilent;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumData;
import org.xmlcml.cml.element.CMLXaxis;
import org.xmlcml.cml.element.CMLYaxis;
import org.xmlcml.euclid.RealArray;

public class AgilentLCMS2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(AgilentLCMS2CMLConverter.class);
	private static final String REG_IN = "agilent-in";
	private static final String REG_OUT = "agilent-cml";
	private static final String REG_MESSAGE = "Agilent lab MS to CML";
	static {
		LOG.setLevel(Level.INFO);
	};
	
	public Type getInputType() {
		return Type.RPT;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an XYZ object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = null;
		cml = parseRPT(lines);
		return cml;
	}
	
	/*--
[SAMPLE]
{
Sample	1
Well	4
FileName	Mark_Hopkins699-1
SampleID	mh3-040
SampleDescription	ps-nco stirred over wkend
Date	12-Jan-2009
Time	10:02:34
JobCode	Mark_Hopkins699
TaskCode
UserName	Mark_Hopkins
LabName
Instrument
Conditions
Submitter	Mark_Hopkins
SampleType	0
Plate
Method	C:\MassLynx\ESI-fullscan_100_1000.olp
Process	C:\MassLynx\OpenLynx.exe
MSTune	OpenAccessESI
MSMethod	8minMS-esiNO-divert_100_1000
InletMethod	8min
PreRunMethod
PostRunMethod
SwitchMethod
FractionMethod
AcquisitionProcess
AcquisitionProcessParameters
AcquisitionProcessOptions
InjectionVolume	1.0000
ReportScheme	C:\MassLynx\OpenAccess.ors
SampleReportEnable	0
SampleReportFormat
AnalysisTime	8.0000
[COMPOUND]
{
;Mono Mass		Formula
}
[FRACTIONRESULTS]
{
}
[FRACTION]
{
}
[FUNCTION]
{
Function	1
IonMode	ES+ 
Type	MS 
Description	(100:1000) 
[SPECTRUM]
{
ProcDesc	Combine (1:6-21:24)
State	OK
Peak ID	1
Peak Ref	1
Time	0.04
TIC	6987175
BPI	2361437
BPM	113.2040
Continuum	FALSE
Saved	TRUE
[MS]
{
;Mass	% BPI
101.91	1.744
104.30	6.428
107.78	4.309
108.50	7.210
111.36	4.550
113.20	100.000
115.89	9.224
116.68	1.411
120.69	2.284
124.47	3.856
128.88	1.020
131.37	41.801
133.02	2.152
134.66	5.920
136.82	1.517
138.64	1.232	 */
	
	private CMLCml parseRPT(List<String> lines) {
		CMLCml cml = null;
		int nline = 0;
		Element root = new Element("root");
		ParentNode parent = root;
		while (nline < lines.size()) {
			String s = lines.get(nline++);
			s = s.trim();
			if (s.equals(CC.S_EMPTY)) {
				continue;
			}
			if (s.startsWith(CC.S_LSQUARE) && s.endsWith(CC.S_RSQUARE)) {
				String title = s.substring(1, s.length()-1);
				s = lines.get(nline++).trim();
				if (!s.equals(CC.S_LCURLY)) {
					throw new RuntimeException("expected "+CC.S_LCURLY+"; found: "+s);
				}
				Element section = new Element("section");
				section.addAttribute(new Attribute("title", title));
				parent.appendChild(section);
				parent = section;
			} else if (s.equals(CC.S_RCURLY)) {
				parent = parent.getParent();
			} else {
				int idx = s.indexOf(CC.S_TAB);
				String name = null;
				String value = null;
				if (idx == -1) {
					name = s;
				} else {
					name = s.substring(0, idx);
					value = s.substring(idx+1).trim();
				}
				Element datum = new Element("datum");
				datum.addAttribute(new Attribute("name", name));
				if (value != null) {
					datum.appendChild(value);
				}
				parent.appendChild(datum);
			}
		}
		Nodes spectrumNodes = root.query(".//section[@title='SPECTRUM']");
		for (int i = 0; i < spectrumNodes.size(); i++) {
			processSpectrum((Element) spectrumNodes.get(i));
		}
		Nodes chromatogramNodes = root.query(".//section[@title='CHROMATOGRAM']");
		for (int i = 0; i < chromatogramNodes.size(); i++) {
			processChromatogram((Element) chromatogramNodes.get(i));
		}
		Nodes emptyDatumNodes = root.query(".//datum[.='']");
		for (int i = 0; i < emptyDatumNodes.size(); i++) {
			emptyDatumNodes.get(i).detach();
		}
//		CMLUtil.debug(root, "root");
		cml = new CMLCml();
		return cml;
	}
	
	private void processSpectrum(Element spectrumElement) {
		Nodes sectionNodes = spectrumElement.query("./section");
		for (int i = 0; i < sectionNodes.size(); i++) {
			Element section = (Element) sectionNodes.get(i);
			CMLSpectrum spectrum = new CMLSpectrum();
			spectrum.setTitle(section.getAttributeValue("title"));
			ParentNode parent = section.getParent();
			parent.replaceChild(section, spectrum);
			Nodes datumNodes = section.query("./datum");
			spectrum.setType(section.getAttributeValue("title"));
			Element datum0 = (Element) datumNodes.get(0);
			CMLSpectrumData spectrumData = new CMLSpectrumData();
			spectrum.addSpectrumData(spectrumData);
			RealArray xarray = new RealArray();
			CMLXaxis xAxis = new CMLXaxis();
			xAxis.setTitle(datum0.getAttributeValue("name"));
			spectrumData.addXaxis(xAxis);
			RealArray yarray = new RealArray();
			CMLYaxis yAxis = new CMLYaxis();
			yAxis.setTitle(datum0.getValue());
			spectrumData.addYaxis(yAxis);
			for (int j = 1; j < datumNodes.size(); j++) {
				Element datum = (Element) datumNodes.get(j);
				double x = new Double(datum.getAttributeValue("name")).doubleValue();
				xarray.addElement(x);
				double y = new Double(datum.getValue()).doubleValue();
				yarray.addElement(y);
			}
			xAxis.addArray(new CMLArray(xarray));
			yAxis.addArray(new CMLArray(yarray));
		}
	}
	
	private void processChromatogram(Element spectrumChromatogram) {
//		CMLUtil.debug(spectrumChromatogram, "XXXXXXXXXXXXXXXXX");
		Nodes traceNodes = spectrumChromatogram.query("./section[@title='TRACE']");
		Element trace = (Element) traceNodes.get(0);
		CMLSpectrum spectrum = new CMLSpectrum();
		spectrum.setTitle(trace.getAttributeValue("title"));
		ParentNode parent = trace.getParent();
		parent.replaceChild(trace, spectrum);
		Nodes datumNodes = trace.query("./datum");
		spectrum.setType(trace.getAttributeValue("title"));
		CMLSpectrumData spectrumData = new CMLSpectrumData();
		spectrum.addSpectrumData(spectrumData);
		RealArray xarray = new RealArray();
		CMLXaxis xAxis = new CMLXaxis();
		spectrumData.addXaxis(xAxis);
		RealArray yarray = new RealArray();
		CMLYaxis yAxis = new CMLYaxis();
		spectrumData.addYaxis(yAxis);
		for (int j = 0; j < datumNodes.size(); j++) {
			Element datum = (Element) datumNodes.get(j);
			double x = new Double(datum.getAttributeValue("name")).doubleValue();
			xarray.addElement(x);
			double y = new Double(datum.getValue()).doubleValue();
			yarray.addElement(y);
		}
		xAxis.addArray(new CMLArray(xarray));
		yAxis.addArray(new CMLArray(yarray));
		
		Nodes peakNodes = spectrumChromatogram.query("./section[@title='PEAK']");
		for (int i = 0; i < peakNodes.size(); i++) {
			Element peak = (Element) peakNodes.get(i);
			processPeak(peak, spectrum);
		}
	}
	
	private void processPeak(Element peakx, CMLSpectrum spectrum) {

		/*
  <section title="PEAK">
    <datum name="Peak ID">2</datum>
    <datum name="Peak Ref">2</datum>
    <datum name="Time">0.40</datum>
    <datum name="Peak">0.24 1.22</datum>
    <datum name="Intensity">9268738.00 12472990.00</datum>
    <datum name="Height">37568428.00</datum>
    <datum name="AreaAbs">10121945.00</datum>
    <datum name="Area %BP">44.80</datum>
    <datum name="Area %Total">21.70</datum>
    <datum name="Width">0.98</datum>
    <datum name="Response Test">0</datum>
  </section>
		 */
		CMLPeak peak =new CMLPeak();
		Nodes datumNodes = peakx.query("./datum");
		for (int i = 0; i < datumNodes.size(); i++) {
			Element datum = (Element) datumNodes.get(i);
			String name = datum.getAttributeValue("name");
			String value = datum.getValue();
			if (name.equals("Peak ID")) {
				peak.setId(value);
			} else if (name.equals("Time")) {
				peak.setXValue(new Double(value).doubleValue());
				peak.setXUnits("mins");
			} else if (name.equals("Width")) {
				peak.setXWidth(value);
			} else if (name.equals("Height")) {
				peak.setYValue(new Double(value).doubleValue());
			}
		}
		peakx.detach();
//		ParentNode parent = peakx.getParent();
//		parent.replaceChild(peakx, peak);
		spectrum.appendChild(peak);
	}
	
	@Override
	public String getRegistryInputType() {
		return SpectrumCommon.AGILENT;
	}
	
	@Override
	public String getRegistryOutputType() {
		return SpectrumCommon.CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return REG_MESSAGE;
	}

}
