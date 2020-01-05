package org.xmlcml.cml.converters.chemdraw;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDXML2CMLProcessor;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class CDXML2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CDXML2CMLConverter.class);
	public static final String REG_MESSAGE = "Chemdraw: CDXML to CML conversion";
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
	 * @param cdxml
	 */
	@Override
	public Element convertToXML(Element cdxml) {
		LOG.debug("CDXML2CML");
		CDXML2CMLProcessor cd = new CDXML2CMLProcessor();
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
	public String getRegistryInputType() {
		return CDXCommon.REG_CDXML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CDXCommon.REG_CDX_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return REG_MESSAGE;
	}



}
