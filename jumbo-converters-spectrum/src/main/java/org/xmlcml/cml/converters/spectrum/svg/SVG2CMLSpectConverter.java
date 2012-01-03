package org.xmlcml.cml.converters.spectrum.svg;


import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.Util;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.element.CMLCml;

/**
 * 
 * @author pm286
 *
 */
public class SVG2CMLSpectConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(SVG2CMLSpectConverter.class);
   
	// input must be bytes to filter out broken <!DOCTYPE>
	public Type getInputType() {
		return Type.SVGBYTES;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	private CMLBuilder builder = new CMLBuilder();

	public Builder getBuilder() {
		return builder;
	}

	/** use only for instantiation
	 * 
	 */
	public SVG2CMLSpectConverter() {
		LOG.trace("SVG2CMLSpect");
	}
	
	/**
	 * Converts a SVG object to CML.
	 * 
	 * @param bytes
	 * @return element
	 */
	@Override
	public Element convertToXML(byte[] bytes) {
		Element xml = Util.stripPrologAndParse(bytes);
		CMLCml cml = null;
		SVG2CMLSpectTool svg2CmlSpectTool = new SVG2CMLSpectTool(xml, fileId);
		svg2CmlSpectTool.processSVG();
		cml = svg2CmlSpectTool.getCML();
		return cml;

	}   
	
	@Override
	public String getRegistryInputType() {
		return SpectrumCommon.SVG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert svg spectrum to CML";
	}

}

    
    
