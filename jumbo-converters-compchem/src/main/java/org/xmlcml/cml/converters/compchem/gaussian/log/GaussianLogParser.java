package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.IOException;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.marker.AbstractParser;

/**
 * converts Dalton archive to molecule, metadata and properties
 * 
 * @author Peter Murray-Rust
 * 
 */
public class GaussianLogParser extends AbstractParser {

	private static Logger LOG = Logger.getLogger(GaussianLogParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String GAUSSIAN_TEMPLATE_LIST = 
		"org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml";
	
	public Element getAuxElement() {
		return getAuxElement(GAUSSIAN_TEMPLATE_LIST);
	}

}
