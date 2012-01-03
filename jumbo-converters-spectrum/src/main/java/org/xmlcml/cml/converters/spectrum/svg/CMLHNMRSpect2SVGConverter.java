package org.xmlcml.cml.converters.spectrum.svg;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.graphics.SVGG;

/**
 * 
 * @author pm286
 *
 */
public class CMLHNMRSpect2SVGConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(CMLHNMRSpect2SVGConverter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/spectrum/svgin",
		"-odir", "../cmlsvg",
		"-is", "cml",
		"-os", "svg",
		"-converter", "org.xmlcml.cml.converters.graphics.svg.CMLSpect2SVGConverter",
	};
    
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.SVG;
	}

	private CMLBuilder builder = new CMLBuilder();

	public Builder getBuilder() {
		return builder;
	}

	/** use only for instantiation
	 * 
	 */
	public CMLHNMRSpect2SVGConverter() {
		LOG.trace("CMLSpect2SVG");
	}
		  
	protected void init() {
		
	}
	/**
	 * Converts a SVG object to CML.
	 * 
	 * @param xml
	 * @return the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element}
	 */
	public Element convertToXML(Element xml) {
		CMLSpect2SVGHelper helper = new CMLSpect2SVGHelper();
		SVGG svgg = helper.parse(xml);
		return svgg;

	}

	@Override
	public String getRegistryInputType() {
		return SpectrumCommon.CML_HNMR;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.SVG;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}

    
    
