package org.xmlcml.cml.converters.molecule.mdl;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.CMLCommon;
import org.xmlcml.cml.converters.cml.CMLSelector;
import org.xmlcml.cml.converters.molecule.MoleculeCommon;
import org.xmlcml.cml.converters.molecule.mdl.MDLConverter.CoordType;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2MDLConverter extends AbstractConverter implements
		Converter {
	
	private static final Logger LOG = Logger.getLogger(CML2MDLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.MDL;
	}

	private MDLConverter mdlConverter;

	public MDLConverter getMdlConverter() {
		return mdlConverter;
	}

	public void setMdlConverter(MDLConverter mdlConverter) {
		this.mdlConverter = mdlConverter;
	}
	
	public CML2MDLConverter() {
		mdlConverter = new MDLConverter();
	}

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param xml
	 */
	public List<String> convertToText(Element xml) {
		CMLElement cml = CMLBuilder.ensureCML(xml);
		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(true);
		List<String> lines = new ArrayList<String>();
		if (molecule != null) {
			mdlConverter.setCoordType(CoordType.EITHER);
			mdlConverter.writeMOL(lines, molecule);
		}
		return lines;
	}

	@Override
	public String getRegistryInputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return MoleculeCommon.MDL;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert CML to MDL molfile";
	}

}
