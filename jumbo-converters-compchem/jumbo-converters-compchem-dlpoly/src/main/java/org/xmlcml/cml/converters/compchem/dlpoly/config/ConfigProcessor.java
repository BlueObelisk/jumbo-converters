package org.xmlcml.cml.converters.compchem.dlpoly.config;

import java.util.List;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomType;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLattice;
import org.xmlcml.cml.element.CMLLatticeVector;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Point3;
import org.xmlcml.molutil.ChemicalElement;

public class ConfigProcessor {

	
	private List<String> lines;
	private CMLCml cml;
	private int var1; // don't know what this is
	private int var2; // don't know what this is
	private int iline;
	private CMLCrystal crystal;
	private CMLMolecule molecule;

	public CMLCml create(List<String> lines) {
		this.lines = lines;
		cml = new CMLCml();
		try {
			process(lines);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse file", e);
		}
		return cml;
	}

	/**
A DL_Poly CONFIG file generated from a gulp output file.                        
         0         3
  18.009484000000000   0.000000000000000   0.000000000000000
   0.000000000000000  18.530232000000002   0.000000000000000
   0.000000000000000   0.000000000000000   6.570309000000000
     Sic         1
         0.000000000         0.000000000         0.000000000
     Sic         2
        18.009484000         9.265116000         3.285154500
     Sic         3
         9.004742000        18.530232000         3.285154500
	 */
	private void process(List<String> lines) {
		iline = 0;
		cml.setTitle(lines.get(iline++));
		processLine1();
		processCell();
		processMolecule();
	}

	private void processLine1() {
		String line = lines.get(iline++);
		cml.appendChild(createIntProperty("dlpoly:var1", line.substring(0,10).trim()));
		cml.appendChild(createIntProperty("dlpoly:var1", line.substring(10,20).trim()));
	}

	private CMLProperty createIntProperty(String dictRef, String value) {
		CMLScalar scalar = new CMLScalar(Integer.parseInt(value));
		CMLProperty property = new CMLProperty();
		property.setDictRef(dictRef);
		property.addScalar(scalar);
		return property;
	}

	private void processCell() {
		CMLLatticeVector[] vv = new CMLLatticeVector[3];
		for (int i = 0; i < 3; i++) {
			String s = lines.get(iline++);
			double[] xyz = new double[3];
			xyz[0] = new Double(s.substring(0,20).trim());
			xyz[1] = new Double(s.substring(20,40).trim());
			xyz[2] = new Double(s.substring(40,60).trim());
			vv[i] = new CMLLatticeVector(xyz);
		}
		CMLLattice lattice = new CMLLattice(vv);
		crystal = new CMLCrystal(lattice);
		crystal.appendChild(lattice);
		cml.appendChild(crystal);
	}

	/**
     Sic         1
         0.000000000         0.000000000         0.000000000
     Sic         2
        18.009484000         9.265116000         3.285154500
	 */
	private void processMolecule() {
		molecule = new CMLMolecule();
		while (iline < lines.size()) {
			String line1 = lines.get(iline++);
			if (line1 == null || line1.trim() == "") {
				break;
			}
			String atomS = line1.substring(5, 13).trim();
			// elements seem to be symbol+label
			// try for 2-letter elements
			String elemS = atomS.substring(0,2);
			String labelS = atomS.substring(2);
			ChemicalElement elem = ChemicalElement.getChemicalElement(elemS);
			if (elem == null) {
				elemS = atomS.substring(0,1);
				labelS = atomS.substring(1);
				elem = ChemicalElement.getChemicalElement(elemS);
			}
			if (elem == null) {
				throw new RuntimeException("Cannot parse line as element "+line1);
			}
			int ser = Integer.parseInt((line1+"       ").substring(13, 20).trim());
			String line2 = lines.get(iline++);
			double[] xyz = new double[3];
			xyz[0] = new Double(line2.substring(0,20).trim());
			xyz[1] = new Double(line2.substring(20,40).trim());
			xyz[2] = new Double(line2.substring(40,60).trim());
			CMLAtom atom = new CMLAtom("a"+ser, elem);
			CMLAtomType atomType = new CMLAtomType();
			atomType.setName(labelS);
			atom.appendChild(atomType);
			atom.setXYZ3(new Point3(xyz));
			molecule.addAtom(atom);
		}
		cml.appendChild(molecule);
	}

}
