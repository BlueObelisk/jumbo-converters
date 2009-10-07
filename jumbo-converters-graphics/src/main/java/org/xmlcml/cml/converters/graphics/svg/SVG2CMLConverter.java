package org.xmlcml.cml.converters.graphics.svg;

import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVG2CMLTool;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.graphics.SVGSVG;

/**
 * 
 * @author pm286
 *
 */
public class SVG2CMLConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(SVG2CMLConverter.class);
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/svgin",
		"-odir", "../svgcml",
		"-is", "svg",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.graphics.svg.SVG2CMLConverter",
	};
    
	public Type getInputType() {
		return Type.SVG;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/** use only for instantiation
	 * 
	 */
	public SVG2CMLConverter() {
		
	}
		  
	protected void init() {
		
	}
	/**
	 * Converts a SVG object to CML.
	 * 
	 * @param in input stream
	 * @return the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element}
	 */
	public Element convertToXML(Element xml) {
		CMLCml cml = null;
		SVG2CMLTool svg2CmlTool = new SVG2CMLTool(xml, fileId, this.getCommand());
		svg2CmlTool.processSVG();
		cml = svg2CmlTool.getCML();
		SVGSVG svg = svg2CmlTool.getSvgChem();
		debugSVG(svg);
		return cml;

	}

	private void debugSVG(SVGSVG svg) {
		File f = new File("temp.svg");
		LOG.debug("F "+f.getAbsolutePath());
		try {
			CMLUtil.debug(svg, new FileOutputStream(f), 1);
		} catch (Exception e) {
			LOG.error("ERROR "+e);
		}
	}

	/**
	 * Returns the current version number of this {@link AbstractConveter}.
	 */
	@Override
	public int getConverterVersion() {
		return 0;
	}
   
}

    
    
