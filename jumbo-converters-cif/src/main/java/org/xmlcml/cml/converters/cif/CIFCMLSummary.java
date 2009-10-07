package org.xmlcml.cml.converters.cif;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

public class CIFCMLSummary extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CIFCMLSummary.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}
	
	public final static String X_PUBL_SECTION_TITLE = ".//cml:scalar[@dictRef='iucr:_publ_section_title']";
	public final static String X_PUBL_AUTHORS = ".//cml:array[@dictRef='iucr:_publ_author_name']";
	
	/**
	 * converts a CML object to XYZ. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element xml) {
		CMLElement summary = null;
		return summary;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
