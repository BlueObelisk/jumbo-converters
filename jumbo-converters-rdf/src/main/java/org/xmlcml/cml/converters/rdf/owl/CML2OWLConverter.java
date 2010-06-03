package org.xmlcml.cml.converters.rdf.owl;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

/*
 * probably obsolete
 */
public class CML2OWLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CML2OWLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/owl",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "cml.owl",
		"-converter", "org.xmlcml.cml.converters.cml.owl.CML2OWLConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.OWL;
	}

	/**
	 * converts a CDK object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element xml) {
		LOG.debug("CML 2 OWL...");
		Element outXML = null;
		// maybe input is not CML
		CML2OWL cml2owl = new CML2OWL();
		CMLElement cml = null;
		try {
			cml = CMLBuilder.ensureCML(xml);
		} catch (Exception e) {
			// was not CML
		}
		if (cml != null) {
			// convert CML file
			outXML = cml2owl.convertCMLElement(cml);
		} else {
			// assume list of classes
			outXML = cml2owl.convert2OWL(xml);
		}
		return outXML;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
