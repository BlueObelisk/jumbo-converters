package org.xmlcml.cml.converters.spectrum.jdx;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.jcamp.parser.JCAMPException;
import org.jcamp.parser.JCAMPWriter;
import org.jcamp.spectrum.ArrayData;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.OrderedArrayData;
import org.jcamp.spectrum.Spectrum1D;
import org.jcamp.units.DerivedUnit;
import org.jcamp.units.Unit;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrumData;
import org.xmlcml.cml.element.CMLXaxis;
import org.xmlcml.cml.element.CMLYaxis;

/**
 * 
 * @author pm286
 *
 */
public class CMLSpect2JDXConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(CMLSpect2JDXConverter.class);
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/spectrum/jdxin",
		"-odir", "../cmljdx",
		"-is", "cml",
		"-os", "jdx",
		"-converter", "org.xmlcml.cml.converters.graphics.svg.CMLSpect2JDXConverter",
	};
    
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.JDX;
	}

	/**
	 * Converts a SVG object to CML.
	 * 
	 * @param in input stream
	 * @return the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element}
	 */
	public List<String> convertToText(Element xml) {
		CMLCml cml = (CMLCml) ensureCML(xml);
		Nodes spectrumNodes = cml.query(".//cml:spectrum", CMLConstants.CML_XPATH);
		CMLSpectrum spectrum = (CMLSpectrum) spectrumNodes.get(0);
		CMLSpectrumData data = spectrum.getSpectrumDataElements().get(0);
		CMLXaxis xAxis = data.getXaxisElements().get(0);
		CMLYaxis yAxis = data.getYaxisElements().get(0);
		
/*
Spectrum1D(IOrderedDataArray1D x, IDataArray1D y)
   */		
		CMLArray xArray = xAxis.getArrayElements().get(0);
		Unit xUnit = new DerivedUnit(); // FIXME later
		OrderedArrayData xData = new OrderedArrayData(xArray.getDoubles(), xUnit);
		
		CMLArray yArray = yAxis.getArrayElements().get(0);
		Unit yUnit = new DerivedUnit(); // FIXME later
		ArrayData yData = new ArrayData(yArray.getDoubles(), yUnit);
		
		String nucleus ="1H";
		double freq = 400000000; // FIXME
		double ref = 0.0; // FIXME ?
		Spectrum1D nmrSpectrum = new NMRSpectrum(xData, yData, nucleus, freq, ref);
		JCAMPWriter writer = JCAMPWriter.getInstance();
		String jcampS = null;
		try {
//			jcampS = writer.toSimpleJCAMP(spectrum);
			jcampS = writer.toJCAMP(nmrSpectrum);
		} catch (JCAMPException e) {
			throw new RuntimeException("JCAMP exc", e);
		}
		String[] lines = jcampS.split(CMLConstants.S_WHITEREGEX);
		List<String> stringList = new ArrayList<String>();
		for (String line : lines) {
			stringList.add(line);
		}
		return stringList;
	}

	/**
	 * Returns the current version number of this {@link AbstractConveter}.
	 */
	@Override
	public int getConverterVersion() {
		return 0;
	}
   
}

    
    
