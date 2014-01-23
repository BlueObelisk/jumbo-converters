package org.xmlcml.cml.converters.molecule.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2FragmentConverter extends AbstractConverter implements
		Converter {
	
	private static final Logger LOG = Logger.getLogger(CML2FragmentConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.DIRECTORY;
	}

	public CML2FragmentConverter() {
	}

	/**
	 * converts a CML object to fragemnts. 
	 * 
	 * @param xml
	 */
	@Override
	public void convert(File input, File output) {
		LOG.info("*** input="+input+", output="+output);
	    checkOutputFile(output);  // manages output directory creation
		FragmentGenerator fragmentGenerator = new FragmentGenerator();
		fragmentGenerator.readMolecule(input);
		fragmentGenerator.setOutputDir(output);
		fragmentGenerator.createMoietiesAndFragments();
	}

	@Override
	public String getRegistryInputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
//		return MoleculeCommon.MDL;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert CML to fragments";
	}
	
	@Override
	protected String[] getExtensions() {
		return new String[] {"cml", "CML"};
	}
	

	public static void main(String[] args) {
		CML2FragmentConverter converter = new CML2FragmentConverter();
		converter.runArgs(args);
	}

}
