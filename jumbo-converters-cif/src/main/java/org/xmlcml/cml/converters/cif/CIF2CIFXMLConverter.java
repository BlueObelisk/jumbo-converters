package org.xmlcml.cml.converters.cif;

import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

/** 
 * Converts a CIF into CIFXML
 * 
 * @author pm286, ned24
 *
 */
public class CIF2CIFXMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(CIF2CIFXMLConverter.class);

	public CIF2CIFXMLConverter() {
	}
	
	public Type getInputType() {
		return Type.CIF;
	}

	public Type getOutputType() {
		return Type.XML;
	}

	/**
	 * converts a CIF object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CIF cifxml = parseLegacy(lines);
		if (fileId != null) {
			try {
				cifxml.setId(fileId);
			} catch (CIFException e) {
				LOG.error("CIF ID is bad: " + e.getMessage()) ;
			}
		}
		return cifxml;
	}

	/** 
	 * read CIF.
	 * 
	 * @param stringList
	 *
	 * @return CIF
	 */
	public CIF parseLegacy(List<String> stringList) {
		init();
		StringBuilder sb = new StringBuilder();
		for (String s : stringList) {
			sb.append(s).append(S_NEWLINE);
		}
		CIF cif = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(sb.toString()));
            CIFParser parser = new CIFParser();
			Document document = parser.parse(br);
			cif = (CIF) document.getRootElement();
		} catch (IOException e) {
			this.getConverterLog().addToLog(Level.ERROR, "parse CIF fail"+e.getMessage());
			throw new RuntimeException("parse CIF fail", e);
		} catch (CIFException e) {
			this.getConverterLog().addToLog(Level.ERROR, "parse CIF fail"+e.getMessage());
			throw new RuntimeException("Cannot parse CIF: "+e);
		} finally {
			IOUtils.closeQuietly(br);
		}
		return cif;
	}

}

