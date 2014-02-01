package org.xmlcml.cml.converters.graphics.image;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.molecule.MoleculeCommon;
import org.xmlcml.cml.converters.molecule.mdl.ImageConverter.CoordType;
import org.xmlcml.cml.element.CMLMolecule;

public class Image2ImageConverter extends AbstractConverter implements
		Converter {
	
	private static final Logger LOG = Logger.getLogger(Image2ImageConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.IMAGE;
	}

	public Type getOutputType() {
		return Type.IMAGE;
	}

	private ImageConverter mdlConverter;

	public ImageConverter getMdlConverter() {
		return mdlConverter;
	}

	public void setMdlConverter(ImageConverter mdlConverter) {
		this.mdlConverter = mdlConverter;
	}
	
	public Image2ImageConverter() {
		mdlConverter = new ImageConverter();
	}

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param xml
	 */
	public byte[] convertToBytes(byte[] bytes) {
		byte[] newBytes = null;
		return newBytes;
	}

	@Override
	public String getRegistryInputType() {
		return CMLCommon.PNG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.PNG;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert Image to Image";
	}

	public static void main(String[] args) {
		Image2ImageConverter converter = new Image2ImageConverter();
		converter.runArgs(args);
	}
	
	@Override 
	public void runArgs(String[] args) {
		LOG.warn("Add image args options here");
		super.runArgs(args);
	}

}
