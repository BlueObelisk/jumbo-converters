package org.xmlcml.cml.converters.compchem.dlpoly.config;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.dlpoly.DLPolyCommon;
import org.xmlcml.cml.element.CMLCml;

public class DLPolyConfig2CMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(DLPolyConfig2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String DLPOLY_MOL_TO_CML_CONVERTER = "DLPoly Molecule to CML Converter";

	public Type getInputType() {
		return Type.MDL;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	public DLPolyConfig2CMLConverter() {
		
	}
	
	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = null;
		if (lines != null && lines.size() > 0) {
			ConfigProcessor processor = new ConfigProcessor();
			cml = processor.create(lines);
		}
		return cml;
	}

	private static void usage() {
		System.out.println("usage: MDL2CMLConverter <file.mdl> <file.xml>");
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		} else {
			DLPolyConfig2CMLConverter converter = new DLPolyConfig2CMLConverter();
			converter.convert(new File(args[0]), new File(args[1]));
		}
		
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