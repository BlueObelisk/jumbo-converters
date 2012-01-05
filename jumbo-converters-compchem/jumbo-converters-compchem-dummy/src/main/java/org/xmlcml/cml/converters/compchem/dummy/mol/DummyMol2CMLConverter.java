package org.xmlcml.cml.converters.compchem.dummy.mol;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.dummy.DummyCommon;
import org.xmlcml.cml.converters.CMLCommon;

public class DummyMol2CMLConverter extends CompchemText2XMLTemplateConverter {

	public static final String DUMMY_MOL_TO_CML_CONVERTER = "Dummy Molecule to CML Converter";

	public DummyMol2CMLConverter() {
		this(getDefaultTemplate("dummy", "mol", "topTemplate.xml", DummyMol2CMLConverter.class));
	}

	public DummyMol2CMLConverter(Element templateElement) {
		super(templateElement);
	}

	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new DummyMol2CMLConverter();
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