package org.xmlcml.cml.converters.molecule.xyz;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2XYZConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CML2XYZConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "xyz",
		"-converter", "org.xmlcml.cml.converters.molecule.xyz.CML2XYZConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "xyz",
		"-converter", "org.xmlcml.cml.converters.molecule.xyz.CML2XYZConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.XYZ;
	}

	/**
	 * converts a CML object to XYZ. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in input stream
	 */
	@Override
	public List<String> convertToText(Element xml) {
		CMLElement cml = CMLBuilder.ensureCML(xml);
		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(true);
		List<String> lines = new ArrayList<String>();
		if (molecule != null) {
			lines = convertToText(molecule);
		}
		return lines;
	}

	private List<String> convertToText(CMLMolecule molecule) {
		if (!molecule.hasCoordinates(CoordinateType.CARTESIAN)) {
			throw new RuntimeException("Molecule has no 3D coordinates");
		}
		List<String> lines = new ArrayList<String>();
		lines.add(str(molecule.getAtomCount()));
		lines.add(molecule.getTitle());
		for (CMLAtom atom : molecule.getAtoms()) {
			StringBuilder sb = new StringBuilder();
			sb.append(atom.getElementType()).append(" ").append(
					str(atom.getX3())).append(" ").append(str(atom.getY3()))
					.append(" ").append(str(atom.getZ3()));
			lines.add(sb.toString());
		}
		return lines;
	}

}
