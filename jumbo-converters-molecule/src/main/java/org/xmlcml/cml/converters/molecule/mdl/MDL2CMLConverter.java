package org.xmlcml.cml.converters.molecule.mdl;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;

public class MDL2CMLConverter extends AbstractConverter implements
		Converter {
	private static final Logger LOG = Logger.getLogger(MDL2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/mdl",
		"-odir", "../temp",
		"-is", "mol",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter"
	};
	
	public Type getInputType() {
		return Type.MDL;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in
	 *            input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = new CMLCml();
		if (lines != null && lines.size() > 0) {
			MDLConverter converter = new MDLConverter();
			CMLMolecule molecule = converter.readMOL(lines);
			cml.appendChild(molecule);
		}
		return cml;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
