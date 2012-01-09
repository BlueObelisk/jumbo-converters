package org.xmlcml.cml.converters.spectrum.jdx.jdx2cml;

import java.util.List;


import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.element.CMLCml;

public class JDX2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(JDX2CMLConverter.class);
	private JDX2CMLParser jdx2cmlParser;
	static {
		LOG.setLevel(Level.INFO);
	};
	
	public JDX2CMLConverter() {
		this.jdx2cmlParser = new JDX2CMLParser();
	}
	public Type getInputType() {
		return Type.JDX;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an JDX object to CMLSpect. returns cml:cml
	 * 
	 * @param lines JCAMP in any ASCII format
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = jdx2cmlParser.convertToCML(lines);
		return cml;
	}

	@Override
	public String getRegistryInputType() {
		return SpectrumCommon.JDX;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert JCAMP-DX to CML";
	}


}
