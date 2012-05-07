package org.xmlcml.cml.converters.rdf.cml;



import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

/** converts to RDF
 * not yet stable
 * @author pm286
 *
 */
public class CML2N3Converter extends AbstractConverter {
	private static Logger LOG = Logger.getLogger(CML2N3Converter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.TXT;
	}

	/**
	 * converts a CML object to RDF-N3. 
	 * 
	 * @param in input stream
	 */
	@Override
	public List<String> convertToText(Element xml) {
		CMLElement cml = CMLBuilder.ensureCML(xml);
		CML2RDF cml2rdf = new CML2RDF();
		List<String> lines = cml2rdf.convertCMLElementToN3(cml);
		return lines;
	}

}
