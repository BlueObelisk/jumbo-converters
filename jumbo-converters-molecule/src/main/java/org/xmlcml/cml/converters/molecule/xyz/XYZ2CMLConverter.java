package org.xmlcml.cml.converters.molecule.xyz;

import java.util.List;
import java.util.StringTokenizer;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Util;

public class XYZ2CMLConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(XYZ2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.molecule.xyz.CML2CMLIdConverter"
	};
	
	public Type getInputType() {
		return Type.XYZ;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	
	/**
	 * converts an XYZ object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in
	 *            input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLMolecule molecule = new CMLMolecule();
		int nline = 0;
		int natoms = 0;
		String s = lines.get(nline++);
		try {
			natoms = Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			throw new RuntimeException("Bad atom count: " + s);
		}

		String title = lines.get(nline++);
		molecule.setTitle(title);
		for (int i = 0; i < natoms; i++) {
			String line = lines.get(nline++);
			if (line == null || line.trim().length() == 0) {
				throw new RuntimeException("too few lines; ended after "
						+ nline + "; expected " + natoms + 2);
			}
			CMLAtom atom = new CMLAtom();
			atom.setId("a" + (i + 1));
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() != 4) {
				throw new RuntimeException("Bad line (" + (i + 1)
						+ ") - found (" + line + ")");
			}
			atom.setElementType(st.nextToken());
			atom.setX3(Util.getDouble(st.nextToken()));
			atom.setY3(Util.getDouble(st.nextToken()));
			atom.setZ3(Util.getDouble(st.nextToken()));
			molecule.addAtom(atom);
		}
		CMLCml cml = new CMLCml();
		cml.appendChild(molecule);
//		cml.debug("CML2XYZ");
		return cml;
	}


	@Override
	public int getConverterVersion() {
		return 0;
	}

}
