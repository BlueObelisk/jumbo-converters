package org.xmlcml.cml.converters.chemdraw;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDXML2CMLObject;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class CDXML2CMLConverter extends AbstractConverter implements
		Converter {

	static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cdxml",
		"-odir", " ../temp",
		"-is", "cdx.xml",
		"-it", "CDXML",
		"-os", "cdx.cml",
		"-ot", "CML",
		"-converter",
		"org.xmlcml.cml.converters.chemdraw.CDXML2CMLConverter"
	};
	
	public String[] getTestArgs() {
		return testArgs;
	}
	static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/cdxml",
		"-odir", " ../temp",
		"-is", "cdx.xml",
		"-it", "CDXML",
		"-os", "cdx.cml",
		"-ot", "CML",
		"-converter",
		"org.xmlcml.cml.converters.chemdraw.CDXML2CMLConverter"
	};
	
	private static final Logger LOG = Logger.getLogger(CDXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private boolean cleanMolecules = false;
	private boolean flatten = true;
	private boolean rescale = false;
	private boolean removeCDXAttributes = true;
	
	public Type getInputType() {
		return Type.CDXML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts a CDXML object to CML. returns cml:cml
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element cdxml) {
		LOG.debug("CDXML2CML");
		CDXML2CMLObject cd = new CDXML2CMLObject();
		cd.setCleanMolecules(cleanMolecules);
		cd.setFlatten(flatten);
		cd.setRescale(rescale);
		cd.setRemoveCDXAttributes(removeCDXAttributes);
		try {
			cd.convertParsedXMLToCML(cdxml);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse CDXML", e);
		}
		CMLElement cml = cd.getCML();
		if (LOG.isDebugEnabled()) {
			CMLUtil.debug(cml, "final CML");
		}
		return cml;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
