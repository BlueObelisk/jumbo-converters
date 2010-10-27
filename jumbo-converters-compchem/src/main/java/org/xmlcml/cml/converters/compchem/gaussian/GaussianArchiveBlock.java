package org.xmlcml.cml.converters.compchem.gaussian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.Util;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.molutil.ChemicalElement;

public class GaussianArchiveBlock extends AbstractBlock {
	private static Logger LOG = Logger.getLogger(GaussianArchiveBlock.class);
	
	private static final String TITLE = "title";
	private static final String THERMAL = "thermal";
	private static final String BASIS1 = "basis1";
	private static final String KEYWORD = "keyword";
	private static final String BASIS = "basis";
	private static final String METHOD = "method";
	private static final String CALCTYPE = "calctype";
	
	private static final String GINC    = "ginc";
	private static final String SYMBOLS = "symbols";
	private static final String DATE = "date";
	private static final String USER = "user";
	private static final String RAWFORMULA = "rawformula";
	private static final String GINC_MINUS = "GINC-";
	
	private static final String BLOCK1 = "block1";
	private static final String BLOCK0 = "block0";
	private static final String BLOCK2 = "block2";
	private static final String BLOCK3 = "block3";
	private static final String BLOCK4 = "block4";
	private static final String BLOCK5 = "block5";
	private static final String BLOCK6 = "block6";

	final static String ATOM_SEPARATOR = S_COMMA;
	final static String KEYWORD_SEPARATOR = S_SPACE;
	final static String NAME_VALUE_SEPARATOR = S_EQUALS;
	final static String SYMBOL_SEPARATOR = S_EQUALS;
	public final static String GAUSS_PREFIX = "gauss";
	public final static String GAUSS_IOP = "IOP";
	public final static String GAUSS_ONIOM = "ONIOM";
	public final static String GAUSS_SACCI = "SAC-CI";
	public final static String VALUE_SUFFIX = "value";
	private static String[] metadataNames = {
	      GINC,
	      CALCTYPE, // SP or FOpt
	      METHOD,
	      BASIS,
	      RAWFORMULA,
	      USER,
	      DATE,
	      SYMBOLS,};
	private static int METHOD_SER = 2;
	private static int BASIS_SER = 3;
	/**
	 * parameterTypes
	 * 
	 */
	public final static String[] PARAMETERTYPES = { 
		METHOD, BASIS, BASIS1, THERMAL };
	private CMLMolecule molecule;
	private Map<String, String> symbolMap;

	public GaussianArchiveBlock(BlockContainer blockContainer) {
		super(blockContainer);
		this.abstractCommon = new GaussianCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (BLOCK0.equals(getBlockName())) {
			makeMetadataLine1();
		} else if (BLOCK1.equals(getBlockName())) {
			makeParametersLine2();
		} else if (BLOCK2.equals(getBlockName())) {
			makeTitleLine2();
		} else if (BLOCK3.equals(getBlockName())) {
			makeCartesianMolecule();
		} else if (BLOCK4.equals(getBlockName()) && blockContainer.getMolecule() == null) {
			makeZmatMolecule();
		} else if ((element = makeRealArray()) != null) {
		} else if ((element = makePropertyList()) != null) {
		} else {
			CMLModule module = new CMLModule();
			module.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), getBlockName()));
			System.err.println("Unknown blockname: "+getBlockName());
			element = module;
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			abstractCommon.addDictRef(element, getBlockName(), validateDictRef);
		} else {
			System.err.println("null element: "+getBlockName());
		}
		blockContainer.setCurrentBlockNumber(blockContainer.getCurrentBlockNumber()+1);
	}

	private CMLElement makeRealArray() {
		if (lines.size() == 1) {
			RealArray realArray = new RealArray(lines.get(0).split(CMLConstants.S_COMMA));
			element = new CMLArray(realArray);
		}
		return element;
	}

	private void makeMetadataLine1() {
		if (lines.size() != 10) {
			LOG.debug("Expected 10 metadata fields: ");
		}
		CMLModule module = new CMLModule();
		for (int i = 2; i < lines.size(); i++) {
			String name = lines.get(i);
			if (i == 2) {
				// strip GINC-
				name = lines.get(i).substring(GINC_MINUS.length());
			}
			String dictRef = metadataNames[i - 2];
			if (RAWFORMULA.equals(dictRef)) {
				CMLFormula formula = new CMLFormula();
				formula.setInline(name);
				element = formula;
			} else if (DATE.equals(dictRef)) {
				System.out.println("date "+name);
				// 30-Mar-2009
				DateTime dateTime = JodaDate.parseDate(name, "dd-MMM-yyyy");
				element = new CMLScalar(dateTime);
			} else {
				element = new CMLScalar(name);
			}
			abstractCommon.addDictRef(element, dictRef, validateDictRef);
			module.appendChild(element);
		}
		blockContainer.setCurrentBlockNumber(0);
		element = module;
	}
	
	private void makeParametersLine2() {
		CMLModule module = new CMLModule();
		String s = lines.get(0);
		// remove #
		s = s.replaceAll(S_HASH, S_EMPTY);
		// normalize spaces
		s = s.replaceAll(S_SPACE + S_SPACE, S_SPACE);
		// sometimes whitespace creeps in
		s = s.replaceAll(S_SPACE + S_EQUALS, S_EQUALS);
		s = s.replaceAll(S_EQUALS + S_SPACE, S_EQUALS);
		String[] keys = s.split(KEYWORD_SEPARATOR);
		List<String> keywordList = new ArrayList<String>();
		for (String key : keys) {
			if (key.equalsIgnoreCase("P")) {
			} else if (key.equalsIgnoreCase(S_EMPTY)) {
			} else if (key.toUpperCase().startsWith(GAUSS_IOP)) {
			} else if (key.toUpperCase().startsWith(GAUSS_SACCI)) {
			} else {
				keywordList.add(key);
			}
		}
		for (int i = 0; i < keywordList.size(); i++) {
			String value = keywordList.get(i);
			String[] values = value.split(S_EQUALS);
			String dictRef = KEYWORD;
			if (values.length == 2) {
				dictRef = values[0].toLowerCase();
				value = values[1];
			}
			CMLScalar scalar = new CMLScalar(value);
			abstractCommon.addDictRef(scalar, dictRef, validateDictRef);
			module.appendChild(scalar);
		}
		blockContainer.setCurrentBlockNumber(1);
		element = module;
	}

	private void makeTitleLine2() {
	    CMLModule module = new CMLModule();
	    CMLScalar title = new CMLScalar(lines.get(0));
        abstractCommon.addDictRef(title, TITLE, validateDictRef);
        module.appendChild(title);
        element = module;
        blockContainer.setCurrentBlockNumber(2);
	}

	private void makeCartesianMolecule() {
		AbstractBlock block0 = blockContainer.getBlockList().get(0);
		Nodes nodes = block0.getElement().query(".//*[local-name()='scalar' and contains(@dictRef, ':"+SYMBOLS+"') and .='1']");
		if (nodes.size() != 1) {
			molecule = this.makeCartesianMolecule0();
			blockContainer.setCurrentBlockNumber(3);
		}
		element = molecule;
	}

	private CMLMolecule makeCartesianMolecule0() throws RuntimeException {
		/**
\\0,2\
C,0.9166762042,0.0730268241,4.9909560473\
H,1.8271445526,-0.2241902314,5.5413327124\
H,1.0043564838,1.1498285377,4.8066561748\
H,0.9550579512,-0.4459962779,4.0264362923\
C,-0.321223388,-0.2601308563,5.7505945921\
H,-0.7206191756,0.4228986482,6.4931268102\
H,-0.7720826283,-1.2444366444,5.678007371\\		 
*/
		CMLMolecule molecule = new CMLMolecule();
		setChargeMultiplicity(molecule);
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(ATOM_SEPARATOR);
			if (fields.length != 4) {
				throw new RuntimeException("expected el x y z; found "+lines.get(i));
			}
			try {
				String el = fields[0];
				el = org.xmlcml.euclid.Util.capitalise(el.toLowerCase());
				ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(el);
				CMLAtom atom = new CMLAtom("a"+i, chemicalElement);
				atom.setX3(new Double(fields[1]));
				atom.setY3(new Double(fields[2]));
				atom.setZ3(new Double(fields[3]));
				molecule.addAtom(atom);
			} catch (NumberFormatException e) {
				throw new RuntimeException("NFE", e);
			} catch (Exception e) {
				// this may happen with symbolic coordinates
				throw new RuntimeException(e);
			}
		}
		blockContainer.setMolecule(molecule);
		return molecule;
	}

	private void setChargeMultiplicity(CMLMolecule molecule) {
		String[] keys = lines.get(0).split(ATOM_SEPARATOR);
		if (keys.length != 2) {
			throw new RuntimeException("Expected 2 keys in atoms: " + lines.get(0));
		}
		molecule.setFormalCharge(Integer.parseInt(keys[0].trim()));
		molecule.setSpinMultiplicity(Integer.parseInt(keys[1].trim()));
	}

	private CMLMolecule makeZmatMolecule() {
		/**
\\0,1\P\O,1,r2\C,1,1.49751094,2
 ,a3\O,1,r4,2,a4,3,d4,0\C,4,r5,1,a5,2,d5,0\O,1,r6,2,a6,3,d6,0\C,6,r7,1,
 a7,2,d7,0\H,3,r8,1,a8,2,d8,0\H,3,r9,1,a9,2,d9,0\H,3,r10,1,a10,2,d10,0\
 H,5,r11,4,a11,1,d11,0\H,5,r12,4,a12,1,d12,0\H,5,r13,4,a13,1,d13,0\H,7,
 r14,6,a14,1,d14,0\H,7,r15,6,a15,1,d15,0\H,7,r16,6,a16,1,d16,0\\r2=1.45
 918885\a3=116.62263234\r4=1.59995561\a4=113.65066774\d4=-120.71597819\
 ...
 5=111.11184789\d15=-51.0557592\r16=1.08494465\a16=110.96958777\d16=70.
 96371703\\		 */
		AbstractBlock block3 = blockContainer.getBlockList().get(3);
		symbolMap = makeSymbolMap();
		// atoms as before, except for symbols
		lines = block3.getLines();
		CMLMolecule molecule = new CMLMolecule();
		setChargeMultiplicity(molecule);
		List<String> atomStringList = new ArrayList<String>();
		for (int i = 1; i < lines.size(); i++) {
			atomStringList.add(lines.get(i).trim());
		}
		makeMolecule1(atomStringList, symbolMap, molecule);
		element = molecule;
		return molecule;
	}

	private Map<String, String> makeSymbolMap() {
		Map<String, String> symbolMap = new HashMap<String, String>();
		for (String line : lines) {
			int idx = line.indexOf(SYMBOL_SEPARATOR);
			if (idx == -1) {
				throw new RuntimeException(
						"Cannot interpret symbol assignation: " + line);
			}
			String name = line.substring(0, idx).trim();
			String value = line.substring(idx + 1).trim();
			// may be followed by scan values ???
			int idx0 = value.indexOf(S_COMMA);
			if (idx0 != -1) {
				value = value.substring(0, idx);
			}
			symbolMap.put(name, value);
		}
		return symbolMap;
	}

	private void makeMolecule1(List<String> atomStringList, Map<String, String> symbolMap, CMLMolecule molecule) {
		List<String> idList = new ArrayList<String>();
		CMLZMatrix zMatrix = null;
		if (atomStringList.size() == 0) {
			throw new RuntimeException("No atoms in log file");
		}
		int natoms = 0;
		for (String line : atomStringList) {
			// must be z-matrix or coordinates
			String[] fields = line.split(ATOM_SEPARATOR);
			if (fields.length == 0) {
				throw new RuntimeException("must have >= 1 field for atom");
			}
			String symbol = fields[0];
			CMLAtom atom = new CMLAtom();
			// replace X by Dummy
			if (symbol.equals("X") || symbol.equals("TV")) {
				symbol = "Dummy";
				atom.setElementType(symbol);
			}
			idList.add(Util.createAtomId(natoms + 1));
			atom.setId(idList.get(natoms));
			molecule.addAtom(atom);
			ChemicalElement chemicalElement = ChemicalElement
					.getChemicalElement(symbol);
			if (chemicalElement == null) {
				throw new RuntimeException(
						"Cannot interpret as chemical element: " + symbol
								+ " (" + natoms + S_SLASH + line + ")");
			}
			natoms++;
			// fields seem to be either:
			// el, x, y, z // cartesian
			// OR
			// el, unknownInt, x, y, z // alternative cartesian
			// OR zMatrix
			// el // first atom
			// el serial, length // second atom
			// el serial, length, serial angle // third atom
			// el serial, length, serial angle, serial torsion // fourth and
			// later
			atom.setElementType(chemicalElement.getSymbol());
			if (natoms == 1) {
				if (fields.length == 1) {
					zMatrix = new CMLZMatrix();
					molecule.appendChild(zMatrix);
					Point3 xyz = new Point3(0, 0, 0);
					atom.setXYZ3(xyz);
				} else {
					addCartesians(atom, fields, natoms, line);
				}
			} else if (natoms == 2) {
				if (zMatrix != null) {
					if (fields.length != 3) {
						throw new RuntimeException("Bad z matrix line for length : " + line);
					}
					addLength(zMatrix, fields, natoms);
				} else {
					addCartesians(atom, fields, natoms, line);
				}
			} else if (natoms == 3) {
				if (zMatrix != null) {
					if (fields.length != 5) {
						throw new RuntimeException("Bad z matrix line for angle: " + line);
					}
					addLength(zMatrix, fields, natoms);
					addAngle(zMatrix, fields, natoms);
				} else {
					addCartesians(atom, fields, natoms, line);
				}
			} else {
				// some torsions have a trailing integer (normally 0)
				// representing format (for ONIOM)
				if (zMatrix != null) {
					if (fields.length < 7 || fields.length > 8) {
						throw new RuntimeException("Bad z matrix line: " + line);
					}

					addLength(zMatrix, fields, natoms);
					addAngle(zMatrix, fields, natoms);
					addTorsion(zMatrix, fields, natoms);
				} else {
					addCartesians(atom, fields, natoms, line);
				}
			}
		}
		if (zMatrix != null) {
			try {
				zMatrix.addCartesiansTo(molecule);
			} catch (RuntimeException e) {
				LOG.debug("Bad ZMAT");
				molecule.debug("MOL");
				throw e;
			}
		}
		// remove any dummies
		List<Node> dummyNodes = CMLUtil.getQueryNodes(molecule, ".//"
				+ CMLAtom.NS + "[@elementType='Dummy']", CML_XPATH);
		for (Node dummyNode : dummyNodes) {
			dummyNode.detach();
		}
	}

	private CMLPropertyList makePropertyList() {
		CMLPropertyList propertyList = new CMLPropertyList();
		for (String line : lines) {
			line = line.trim();
			if (line.length() != 0) {
				String[] nv = line.split(NAME_VALUE_SEPARATOR);
				if (nv.length != 2) {
//					throw new RuntimeException("Expected name=value: " + line);
				}
				CMLProperty property = new CMLProperty();
				abstractCommon.addDictRef(property, nv[0].toLowerCase(), true);
				// we don't know the type so keep trying until it fits
				CMLScalar scalar = new CMLScalar(nv[1]);
				try {
					scalar = new CMLScalar(new Double(nv[1]));
					scalar = new CMLScalar(new Integer(nv[1]));
				} catch (Exception e) {
					//
				}
				property.addScalar(scalar);
				propertyList.addProperty(property);
			}
		}
		element = propertyList;
		return propertyList;
	}

//	private void storeRawKeywords(String s) {
//		CMLModule module = new CMLModule();
//		abstractCommon.addDictRef(module, "keywords", true);
//		// remove #
//		s = s.replaceAll(S_HASH, S_EMPTY);
//		// normalize spaces
//		s = s.replaceAll(S_SPACE + S_SPACE, S_SPACE);
//		// sometimes whitespace creeps in
//		s = s.replaceAll(S_SPACE + S_EQUALS, S_EQUALS);
//		s = s.replaceAll(S_EQUALS + S_SPACE, S_EQUALS);
//		String[] keys = s.split(KEYWORD_SEPARATOR);
//		for (String key : keys) {
//			if (key.equalsIgnoreCase("P")) {
//			} else if (key.equalsIgnoreCase(S_EMPTY)) {
//			} else if (key.toUpperCase().startsWith(GAUSS_IOP)) {
//			} else if (key.toUpperCase().startsWith(GAUSS_SACCI)) {
//			} else {
//				CMLScalar scalar = new CMLScalar(true);
//				abstractCommon.addDictRef(scalar, key, true);
//				module.appendChild(scalar);
//			}
//		}
//	}

	private void addCartesians(CMLAtom atom, String[] fields, int natoms,
			String line) {
		if (fields.length < 4 || fields.length > 5) {
			throw new RuntimeException("bad number of fields for atom ("
					+ natoms + ") / " + line);
		}
		int offset = fields.length - 3;
		Point3 xyz = new Point3(translateSymbolToDouble(fields[offset]),
				translateSymbolToDouble(fields[offset + 1]),
				translateSymbolToDouble(fields[offset + 2]));
		atom.setXYZ3(xyz);
		if (offset == 2) {
			if (!fields[1].equals("0")) {
				LOG.info("Skipped unknown atom field: " + fields[1]);
			}
		}
	}

	/** translates */
	private double translateSymbolToDouble(String s) {
		String ss = s;
		// may have symbolic values
		if (symbolMap != null) {
			// might start with '-'
			String prefix = S_EMPTY;
			if (s.startsWith(S_MINUS)) {
				s = s.substring(1);
				prefix = S_MINUS;
			}
			ss = symbolMap.get(s);
			if (ss == null) {
				ss = prefix + s;
			} else {
				ss = prefix + ss;
			}
			ss = ss.replace(S_MINUS + S_MINUS, S_EMPTY);
		}
		Double doubleX = null;
		try {
			doubleX = new Double(ss);
		} catch (Exception e) {
			throw new RuntimeException("Cannot interpret as coordinate: " + ss
					+ " {perhaps no symbol given)");
		}
		return doubleX.doubleValue();
	}

	private void addLength(CMLZMatrix zMatrix, String[] fields, int natoms) {
		Util.addLength(zMatrix,
				new String[] { Util.createAtomId(Integer.parseInt(fields[1])),
						Util.createAtomId(natoms), },
				translateSymbolToDouble(fields[2]));
	}

	private void addAngle(CMLZMatrix zMatrix, String[] fields, int natoms) {
		Util.addAngle(
				zMatrix,
				new String[] { Util.createAtomId(Integer.parseInt(fields[3])),
						Util.createAtomId(Integer.parseInt(fields[1])),
						Util.createAtomId(natoms) },
				translateSymbolToDouble(fields[4]));
	}

	private void addTorsion(CMLZMatrix zMatrix, String[] fields, int natoms) {
		Util.addTorsion(
				zMatrix,
				new String[] { Util.createAtomId(Integer.parseInt(fields[5])),
						Util.createAtomId(Integer.parseInt(fields[3])),
						Util.createAtomId(Integer.parseInt(fields[1])),
						Util.createAtomId(natoms) },
				translateSymbolToDouble(fields[6]));
	}
}
