package org.xmlcml.cml.converters.spectrum.bruker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.spectrum.jdx.jdx2cml.JDX2CMLParser;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLSpectrum.SpectrumType;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Util;

public class Bruker2XMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(Bruker2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	
	private CMLSpectrum cmlSpectrum = null;
	private SpectrumType spectrumType = null;

	private JDX2CMLParser jdx2cmlParser;
	private CMLCml topCml;
	public Bruker2XMLConverter() {
		this.jdx2cmlParser = new JDX2CMLParser();
	}
	
	public Type getInputType() {
		return Type.JDX;
	}

	public Type getOutputType() {
		return Type.XML;
	}

	/**
	 * converts an Bruker object to CMLSpect. returns cml:cml
	 * 
	 * @param lines JCAMP in any ASCII format
	 */
	public Element convertToXML(List<String> lines) {
		topCml = new CMLCml();
		BrukerProcessor brukerProcessor = new BrukerProcessor(topCml);
		lines = brukerProcessor.extractAndProcessBrukerFiles(lines);
		CMLCml cml = jdx2cmlParser.convertToCML(lines);
		topCml.appendChild(cml);
		return topCml;
	}

	@Override
	public String getRegistryInputType() {
		return SpectrumCommon.BRUKER_JDX;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.XML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert Bruker JCAMP-DX to XML";
	}


}
