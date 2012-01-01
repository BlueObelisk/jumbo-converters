package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrum.SpectrumType;

public class OSCAR2CMLSpectConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(OSCAR2CMLSpectConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	public final static String JDX_PREFIX = "jdx";
	
	private CMLSpectrum cmlSpectrum = null;
	private SpectrumType spectrumType = null;
	
	
	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * 
	 * 
	 * @param oscarSpectrum
	 * @return spectrum
	 */
	public Element convertToXML(Element oscarSpectrum) {
		CMLCml cml = null;
		OSCAR2CMLSpectHelper oscarHelper = new OSCAR2CMLSpectHelper();
		CMLSpectrum spectrum = oscarHelper.parse(oscarSpectrum);
		if (spectrum != null) {
			cml = new CMLCml();
			cml.appendChild(spectrum);
		}
		return cml;
	}

	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}
