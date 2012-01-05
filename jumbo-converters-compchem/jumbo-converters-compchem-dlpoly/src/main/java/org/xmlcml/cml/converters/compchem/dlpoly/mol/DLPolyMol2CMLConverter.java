package org.xmlcml.cml.converters.compchem.dlpoly.mol;

import java.io.IOException;

import nu.xom.Element;

import org.xmlcml.cml.converters.compchem.CompchemText2XMLTemplateConverter;
import org.xmlcml.cml.converters.compchem.dlpoly.DLPolyCommon;
import org.xmlcml.cml.converters.CMLCommon;

public class DLPolyMol2CMLConverter extends CompchemText2XMLTemplateConverter {

	public static final String DLPOLY_MOL_TO_CML_CONVERTER = "DLPoly Molecule to CML Converter";

	public DLPolyMol2CMLConverter() {
		this(getDefaultTemplate("dlpoly", "mol", "topTemplate.xml", DLPolyMol2CMLConverter.class));
	}

	public DLPolyMol2CMLConverter(Element templateElement) {
		super(templateElement);
	}

	public static void main(String[] args) throws IOException {
		CompchemText2XMLTemplateConverter converter = new DLPolyMol2CMLConverter();
	}

	@Override
	public String getRegistryInputType() {
		return DLPolyCommon.DLPOLY_MOL;
	}

	@Override
	public String getRegistryOutputType() {
		return CMLCommon.CML;
	}

	@Override
	public String getRegistryMessage() {
		return DLPOLY_MOL_TO_CML_CONVERTER;
	}
}