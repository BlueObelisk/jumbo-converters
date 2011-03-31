package org.xmlcml.cml.converters.compchem.input;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;

public class CMLJob2CMLAbsJobConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CMLJob2CMLAbsJobConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}
	
	/**
	 * converts a CML object to XYZ. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in input stream
	 */
	@Override
	public Element convertToXML(Element jobXml) {
		CMLElement jobCml = CMLBuilder.ensureCML(jobXml);
		if (!(jobCml instanceof CMLCml)) {
			throw new RuntimeException("Job input must be a CMLCml element");
		}
//		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(true);
		AbstractCompchemInputProcessorNew processor = new AbstractCompchemInputProcessorNew();
		processor.setJobCml((CMLCml)jobCml);
		CMLMolecule molecule = this.getAuxMolecule();
		processor.setInputMolecule(molecule);
		processor.ensureCompchemDictionary();
		processor.process();
		CMLCml cml = processor.getAbstractJob();
		return cml;
	}

	private CMLMolecule getAuxMolecule()
	{
		CMLElement cmlElement = (CMLElement) this.getAuxElement();
		CMLMolecule molecule = new CMLSelector(cmlElement).getToplevelMoleculeDescendant(true);
		return molecule;
	}

}
