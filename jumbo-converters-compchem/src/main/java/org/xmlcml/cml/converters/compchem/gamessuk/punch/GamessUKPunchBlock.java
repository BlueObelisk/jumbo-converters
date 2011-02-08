package org.xmlcml.cml.converters.compchem.gamessuk.punch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessukCommon;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomicBasisFunction;
import org.xmlcml.cml.element.CMLBasisSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.euclid.Util;

public class GamessUKPunchBlock extends AbstractBlock {

	private static final String INDEX = "index";
	private static final String VECTORS = "vectors";
	private static final String BASIS = "basis";
	private static final String GRID_MAPPING = "grid_mapping";
	private static final String GRID_AXES = "grid_axes";
	private static final String GRID_TITLE = "grid_title";
	private static final String VIBRATIONAL_FREQUENCY = "vibrational_frequency";
	private static final String SCF_ENERGY = "scf_energy";
	private static final String OCCUPATIONS = "occupations";
	private static final String MULLIKEN_ATOMIC_CHARGES = "mulliken_atomic_charges";
	private static final String MULLIKEN_ATOM_POPULATIONS = "mulliken_atom_populations";
	private static final String MULLIKEN_AO_POPULATIONS = "mulliken_ao_populations";
	private static final String LOWDIN_ATOMIC_CHARGES = "lowdin_atomic_charges";
	private static final String LOWDIN_ATOM_POPULATIONS = "lowdin_atom_populations";
	private static final String LOWDIN_AO_POPULATIONS = "lowdin_ao_populations";
	private static final String GRID_DATA = "grid_data";
	private static final String CONNECTIVITY = "connectivity";
	private static final String NORMAL_COORDINATES = "normal_coordinates";
	private static final String UPDATE_COORDINATES = "update_coordinates";
	private static final String COORDINATES = "coordinates";
	private static final String DATA = "data";
	private static final String FRAGMENT_SEQUENCE = "fragment.sequence";
	private static final String FRAGMENT = "fragment";
	public static final Double AU_TO_ANGSTROM = 0.52917721;
	Integer records;
	Integer index;
	String unit;
	int elements;
	
	public GamessUKPunchBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	protected AbstractCommon getCommon() {
		return new GamessukCommon();
	}
	
	public void add(String s) {
		lines.add(s);
	}

	public void convertToRawCML() {
		if (blockName.equals(FRAGMENT) ||
				blockName.equals(FRAGMENT_SEQUENCE) ||
				blockName.equals(DATA) ||
			false) {
			makeMarkerBlock();
		} else if (
				blockName.equals(COORDINATES) ||
				blockName.equals(UPDATE_COORDINATES) ||
				blockName.equals(NORMAL_COORDINATES) ||
				false) {
			makeCoordinates();
		} else if (blockName.equals(CONNECTIVITY)) {
			makeConnectivity();
		} else if (
				blockName.equals(GRID_DATA) ||
				blockName.equals(LOWDIN_AO_POPULATIONS) ||
				blockName.equals(LOWDIN_ATOM_POPULATIONS) ||
				blockName.equals(LOWDIN_ATOMIC_CHARGES) ||
				blockName.equals(MULLIKEN_AO_POPULATIONS) ||
				blockName.equals(MULLIKEN_ATOM_POPULATIONS) ||
				blockName.equals(MULLIKEN_ATOMIC_CHARGES) ||
				blockName.equals(OCCUPATIONS) ||
				blockName.equals(SCF_ENERGY) ||
				blockName.equals(VIBRATIONAL_FREQUENCY) ||
			
			false) {
			element = createDoubleArray(blockName);
		} else if (
				blockName.equals(GRID_TITLE) ||
				blockName.equals(GRID_AXES) ||	// need an expert to decode
				blockName.equals(GRID_MAPPING) || // need an expert to decode
			false) {
			element = createScalarArray(blockName);
		} else if (
				blockName.equals(BASIS) ||
				false) {
			makeBasis();
		} else if (
				blockName.equals(VECTORS) ||
				false) {
			/**
			block = vectors records =    88 index =   1 elements =  22
			  -0.000199  -0.000556   0.000000   0.000000   0.002516  -0.018231
			   0.000000   0.000000   0.013900  -0.983533  -0.098056   0.000000
			   0.000000  -0.004511   0.047908   0.000000   0.000000   0.013258
			   0.000801  -0.000450   0.000801  -0.000450
			   0.986393   0.093379   0.000000   0.000000  -0.001529  -0.044674
			   0.000000   0.000000  -0.007510   0.000032   0.000586   0.000000
			   0.000000  -0.000655  -0.000404   0.000000   0.000000  -0.004582
			  -0.001848   0.013064  -0.001848   0.013064
					 * 
					 */
				element = createDoubleMatrix(6, 11, blockName);
		} else {
			System.err.println("Unknown blockname: "+blockName);
		}
		if (element != null) {
			if (index != null) {
				addNamespacedAttribute(element, INDEX, ""+index);
			}
			String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), blockName);
			element.setAttribute("dictRef", dictRef);
		} else {
			System.err.println("null element: "+blockName);
		}
	}

	private CMLElement createDoubleMatrix(int fieldsPerLine, int charsPerField, String local) {
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		if (elements == 0) {
			throw new RuntimeException(local+" expects multiple elements");
		}
		int cols = elements;
		int linesPerRow = (cols-1)/fieldsPerLine+1;
		int rows = records/linesPerRow;
		int row = 0;
		int col = 0;
		double[][] mat = new double[rows][cols];
		int fieldsLeft = cols;
		mat[row] = new double[cols];
		for (int irecord = 0; irecord < records; irecord++) {
			String line = lines.get(irecord);
			double[] fieldsFound = getDoubles(line, fieldsPerLine, fieldsLeft, charsPerField);
			fieldsLeft -= fieldsFound.length;
			System.arraycopy(fieldsFound, 0, mat[row], col, fieldsFound.length);
			col += fieldsFound.length;
			if (fieldsLeft <= 0 && irecord < records-1) {
				fieldsLeft = cols;
				mat[++row] = new double[cols];
				col = 0;
			}
		}
		CMLMatrix matrix = new CMLMatrix(mat);
		matrix.setDictRef(dictRef);
		return matrix;
	}

	private double[] getDoubles(String line, int fieldsPerLine, int fieldsLeft, int charsPerField) {
		int fieldsToRead = Math.min(fieldsPerLine, fieldsLeft);
		if (fieldsToRead * charsPerField > line.length()) {
			throw new RuntimeException("matrix: line too short :"+line+":");
		}
		double[] fields = new double[fieldsToRead];
		int start = 0;
		int end = start+charsPerField;
		for (int i = 0; i < fieldsToRead; i++) {
			fields[i] = new Double(line.substring(start, end));
			start += charsPerField;
			end += charsPerField;
		}
		return fields;
	}

	private void makeBasis() {
		/**
block = basis records =  21
c          1  s   1   1      172.256000       0.061767
c          1  s   1   2       25.910900       0.358794
c          1  s   1   3        5.533350       0.700713
c          1  s   2   1        3.664980      -0.395897
c          1  s   2   2        0.770545       1.215840
c          1  p   3   1        3.664980       0.236460
c          1  p   3   2        0.770545       0.860619
c          1  s   4   1        0.195857       1.000000
c          1  p   5   1        0.195857       1.000000
o          2  s   1   1      322.037000       0.059239
o          2  s   1   2       48.430800       0.351500
o          2  s   1   3       10.420600       0.707658
o          2  s   2   1        7.402940      -0.404453
o          2  s   2   2        1.576200       1.221560
o          2  p   3   1        7.402940       0.244586
o          2  p   3   2        1.576200       0.853955
o          2  s   4   1        0.373684       1.000000
o          2  p   5   1        0.373684       1.000000
h          3  s   1   1        5.447178       0.156285
h          3  s   1   2        0.824547       0.904691
h          3  s   2   1        0.183192       1.000000

into

   <basisSet>
	   <atomicBasisFunction id="a1" n="1" l="0" lm="s" symbol="S" atomRef="a1"/>
	   <atomicBasisFunction id="a2" n="2" l="0" lm="s" symbol="S" atomRef="a2"/>
	   <atomicBasisFunction id="a3" n="2" l="1" lm="px" symbol="PX" atomRef="a2"/>

		 */
		element = new CMLBasisSet();
		for (int i = 0; i < records; i++) {
			String line = lines.get(i);
//			String elementType = Util.capitalise(line.substring(0, 2)).trim();
			int i1 = Integer.parseInt(line.substring(2, 12).trim());
			String s2 = line.substring(12, 15).trim();
			int i3 = Integer.parseInt(line.substring(15, 19).trim());
			int i4 = Integer.parseInt(line.substring(19, 23).trim());
			double f5 = new Double(line.substring(24, 39).trim());
			double f6 = new Double(line.substring(39, 54).trim());
			CMLAtomicBasisFunction abf = new CMLAtomicBasisFunction();
			addNamespacedAttribute(abf, "i1", ""+i1);
			addNamespacedAttribute(abf, "s2", ""+s2);
			addNamespacedAttribute(abf, "i3", ""+i3);
			addNamespacedAttribute(abf, "i4", ""+i4);
			addNamespacedAttribute(abf, "f5", ""+f5);
			addNamespacedAttribute(abf, "f6", ""+f6);
			((CMLBasisSet)element).addAtomicBasisFunction(abf);
		}
	}

	private CMLArray createDoubleArray(String local) {
		/**
		block = mulliken_ao_populations records =   22
		  1.986719
		  0.426035
		  0.334886
		  0.588760
		...
				 */
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		double[] arr = new double[records];
		for (int i = 0; i < records; i++) {
			arr[i] = new Double(lines.get(i));
		}
		CMLArray array = new CMLArray(arr);
		array.setDictRef(dictRef);
		//array.debug("SSS");
		return array;
	}

	private CMLArray createScalarArray(String local) {
		/**
block=grid_title records= 1 index =   1
formaldehyde total charge density                                               
				 */
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		String[] arr = new String[records];
		for (int i = 0; i < records; i++) {
			arr[i] = lines.get(i);
		}
		CMLArray array = new CMLArray(arr, CMLConstants.S_PIPE);
		array.setDictRef(dictRef);
		return array;
	}

	private void makeConnectivity() {
		/**
block = connectivity records =    3
    1    2
    1    3
    1    4
*/
		if (records == 0) {
			throw new RuntimeException("Connectivity expect multiple records");
		}
		CMLBondArray bondArray = new CMLBondArray();
		for (int i = 0; i < records; i++) {
			String a1 = "a"+lines.get(i).substring(0,5).trim();
			String a2 = "a"+lines.get(i).substring(5,10).trim();
			CMLBond bond = new CMLBond();
			bond.setAtomRefs2(a1+" "+a2);
			bond.setId(a1+CMLConstants.S_UNDER+a2);
			Element bondElement = bond;	// to avoid type checking in CMLXOM
			bondArray.appendChild(bondElement);
		}
		element = bondArray;
	}

	private void makeCoordinates() {
		/*
block = coordinates records =     4 unit = au
  c             0.0000000      0.0000000      0.9998719
  o             0.0000000      0.0000000     -1.2734685
  h             0.0000000      1.7650647      2.0942583
  h             0.0000000     -1.7650647      2.0942583

		 */
		// don't know whether it's formatted - guess so
		Pattern pattern = Pattern.compile("( [a-z\\s][a-z])\\s{8}(\\s+\\-?\\d*\\.\\d{7})(\\s+\\-?\\d*\\.\\d{7})(\\s+\\-?\\d*\\.\\d{7})");
		if (records == 0) {
			throw new RuntimeException("Coordinates expect multiple records");
		}
		Double conversion = AU_TO_ANGSTROM;
		if (unit == null) {
			conversion = AU_TO_ANGSTROM;
		} else if ("au".equals(unit)) {
			conversion = AU_TO_ANGSTROM;
		} else {
			throw new RuntimeException("Coordinates unknown units: "+unit);
		} 
		CMLAtomArray atomArray = new CMLAtomArray();
		for (int i = 0; i < records; i++) {
			Matcher matcher = pattern.matcher(lines.get(i));
			if (!matcher.matches()) {
				throw new RuntimeException("coordinate: cannot parse: "+lines.get(i));
			}
			if (matcher.groupCount() != 4) {
				throw new RuntimeException("coordinate: expected 4 fields: "+lines.get(i));
			}
			CMLAtom atom = new CMLAtom();
			atom.setId("a"+(i+1));
			String elementType = Util.capitalise(matcher.group(1).trim());
			atom.setElementType(elementType);
			atom.setX3(new Double(matcher.group(2))*conversion);
			atom.setY3(new Double(matcher.group(3))*conversion);
			atom.setZ3(new Double(matcher.group(4))*conversion);
			atomArray.addAtom(atom);
		}
		//atomArray.debug("AAA");
		element = atomArray;
	}

	private void makeMarkerBlock() {
		// block=fragment records=0
		if (records > 0) {
			throw new RuntimeException("Cannot process multiple records");
		}
		element = new CMLModule();
		((CMLModule) element).setTitle(blockName);
	}

}
