package org.xmlcml.cml.converters.spectrum.svg;


import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.Util;
import org.xmlcml.cml.element.CMLCml;

/**
 * 
 * @author pm286
 *
 */
public class SVG2CMLSpectConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(SVG2CMLSpectConverter.class);
   
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/spectrum/svgin",
		"-odir", "../svgcml",
		"-is", "svg",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.graphics.svg.SVG2CMLConverter",
	};

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
	 * @param in input stream
	 * @return the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element}
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
}

    
    
