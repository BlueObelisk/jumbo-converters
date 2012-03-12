package org.xmlcml.cml.converters.compchem.dummy.mol;

import java.io.IOException;
import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.CMLCommon;
import org.xmlcml.cml.converters.compchem.dummy.DummyCommon;
import org.xmlcml.cml.element.CMLCml;

public class DummyMol2CMLConverter extends AbstractConverter {

	public static final String DUMMY_MOL_TO_CML_CONVERTER = "Dummy Molecule to CML Converter";

	public DummyMol2CMLConverter() {
		super();
	}
	
	public Type getInputType() {
		return Type.TXT;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	public static void main(String[] args) throws IOException {
		AbstractConverter converter = new DummyMol2CMLConverter();
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		CMLCml cml = null;
		if (lines != null && lines.size() > 0) {
			MolProcessor molProcessor = new MolProcessor();
			cml = molProcessor.create(lines);
		}
		return cml;
		
	}

	@Override
	public String getRegistryInputType() {
		return DummyCommon.DUMMY_MOL;
	}

	@Override
	public String getRegistryOutputType() {
		return CMLCommon.CML;
	}

	@Override
	public String getRegistryMessage() {
		return DUMMY_MOL_TO_CML_CONVERTER;
	}
}