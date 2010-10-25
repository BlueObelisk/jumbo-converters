package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.CompchemUtils;
import org.xmlcml.cml.converters.compchem.NumericFormat;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.molutil.ChemicalElement;

public class GamessUSPunchBlock extends AbstractBlock {
	private static Logger LOG = Logger.getLogger(GamessUSPunchBlock.class);
	/*
	 * keywords in legacy input
	 */
	public static final String DATA = "DATA";
	public static final String GRAD = "GRAD";
	public static final String HESS = "HESS";
	public static final String VIB  = "VIB";
	public static final String ZMAT = "ZMAT";

	private CMLMolecule molecule;

	public GamessUSPunchBlock(BlockContainer blockContainer) {
		super(blockContainer);
		this.abstractCommon = new GamessUSCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (DATA.equals(getBlockName())) {
			makeData();
		} else if (GRAD.equals(getBlockName())) {
			makeGrad();
		} else if (HESS.equals(getBlockName())) {
			makeHess();
		} else if (VIB.equals(getBlockName())) {
			makeVib();
		} else if (ZMAT.equals(getBlockName())) {
			makeZmat();
		} else {
			System.err.println("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			String entryId = getBlockName().toLowerCase();
			if (validateDictRef) {
				DictionaryTool dictionaryTool = abstractCommon.getDictionaryTool();
				if (!dictionaryTool.isIdInDictionary(entryId)) {
					LOG.warn("entryId "+entryId+" not found in dictionary: "+dictionaryTool);
				}
				String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), entryId);
				element.setAttribute("dictRef", dictRef);
			}
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	/**
 $DATA  
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16                            
C1       0
H1          1.0     -2.9805271364       .9147039208       .1059830464
   CCD     0
        
	 */
	private void makeData() {
		int lineCount = 0;
		molecule = new CMLMolecule();
		String title = lines.get(lineCount++);
		molecule.setTitle(title);
		CMLSymmetry symmetry = new CMLSymmetry();
		//C1       0
		String pgline = lines.get(lineCount++);
		String[] tokens = pgline.split(CMLConstants.WHITESPACE);
		symmetry.setPointGroup(tokens[0]);
		molecule.appendChild(symmetry);
		
		int id = 1;
		for (; lineCount < lines.size(); lineCount += 3) {
			String line = lines.get(lineCount);
//			H1          1.0     -2.9805271364       .9147039208       .1059830464
			CMLAtom atom = new CMLAtom();
			CMLLabel label = new CMLLabel();
			label.setCMLValue(line.substring(0, 8));
			atom.addLabel(label);
			int atomicNumber = new Integer(line.substring(8, 13).trim()); 
			atom.setElementType(ChemicalElement.getElement(atomicNumber).getSymbol());
			atom.setX3(new Double(line.substring(15, 33).trim()));
			atom.setY3(new Double(line.substring(33, 51).trim()));
			atom.setZ3(new Double(line.substring(51, 69).trim()));
			atom.setId("a"+(id++));
			molecule.addAtom(atom);
		}
		element = molecule;
		blockContainer.setMolecule(molecule);
	}

	public static final Pattern ATOM_BOND_COUNT = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	public static final Pattern ATOM = Pattern.compile("\\s*([A-Z][a-z]?)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)");
	public static final Pattern BOND = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	
	private void makeGrad() {
		element = new CMLScalar();
	}

	private final static Pattern hessPattern = Pattern.compile(
			"ENERGY IS(....................) E\\(NUC\\) IS(....................).*");
	private void makeHess() {
		/*
		ENERGY IS     -417.0146230240 E(NUC) IS      384.0190725767
		 1  1 2.64800944E-01-1.13848557E-01-8.77575186E-03-2.79100487E-01 9.26552102E-02
		 1  2 6.37962242E-03-1.45252521E-02 1.95434248E-02 4.42901143E-03 2.87107862E-02
		 ...
		 */
		ensureMolecule();
		CMLModule module = new CMLModule();
		module.setRole("gammesus:hess");
		int lineCount = 0;
		int coordCount = molecule.getAtomCount()*3;
		String flags = lines.get(lineCount++);
		Matcher matcher = hessPattern.matcher(flags);
		if (!matcher.matches()) {
			throw new RuntimeException("bad flags match:"+flags+":");
		}
		addScalar(module, new Double(matcher.group(1).trim()), "hessenergy");
		addScalar(module, new Double(matcher.group(2).trim()), "hessenuc");
		double[][] matrix = new double[coordCount][];
		for (int i = 0; i < coordCount; i++) {
			CompchemUtils compchemUtils = new CompchemUtils();
			// add array
			NumericFormat numericFormat = new NumericFormat();
			numericFormat.setF(15, 8);
			int leftMargin = 5;
			matrix[i] = compchemUtils.readFormattedNumericArray(lines, lineCount, leftMargin, numericFormat, 5, coordCount);
		}
		addMatrix(module, matrix, "hess");
		element = module;
	}

	private void addMatrix(CMLModule module, double[][] matrix, String dictId) {
		CMLProperty property = new CMLProperty();
		CMLMatrix cmlMatrix = new CMLMatrix(matrix); 
		property.appendChild(cmlMatrix);
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), dictId);
		property.setDictRef(dictRef);
		module.appendChild(property);
	}

	/*
         IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240
	 */
	private final static Pattern vibPattern = Pattern.compile("         IVIB=(....) IATOM=(....) ICOORD=(....) E=(....................).*");
	private void makeVib() {
		ensureMolecule();
		CMLModule module = new CMLModule();
		
/*
         IVIB=   0 IATOM=   0 ICOORD=   0 E=     -417.0146230240
-3.014817301E-06-5.316670868E-06-4.345305914E-06 1.221059187E-05 6.746840227E-06
-5.750877884E-06-2.129021293E-05 1.289122123E-05-4.221137717E-05 1.283919380E-07
...
 7.799249640E-06-1.877789578E-08 9.009626448E-06 5.782921410E-06 1.610009308E-05
-6.564999713E-06 1.749591384E-05 2.550531014E-06  (48 fields for 16 atoms)
 0.000000000E+00 0.000000000E+00 0.000000000E+00  (3 fields???)
 */
		int lineCount = 0;
		int coordCount = molecule.getAtomCount()*3;
		String flags = lines.get(lineCount++);
		Matcher matcher = vibPattern.matcher(flags);
		if (!matcher.matches()) {
			throw new RuntimeException("bad flags match");
		}
		addScalar(module, Integer.parseInt(matcher.group(1).trim()), "ivib");
		addScalar(module, Integer.parseInt(matcher.group(2).trim()), "iatom");
		addScalar(module, Integer.parseInt(matcher.group(3).trim()), "icoord");
		addScalar(module, new Double(matcher.group(4).trim()), "e");
		CompchemUtils compchemUtils = new CompchemUtils();
		// add array
		NumericFormat numericFormat = new NumericFormat();
		numericFormat.setF(16, 9);
		double[] dd = compchemUtils.readFormattedNumericArray(lines, lineCount, 0, numericFormat, 5, coordCount);
		addArray(module, dd, "vibs");
		lineCount += compchemUtils.getLinesRead();
		// add vector
		dd = compchemUtils.readFormattedNumericArray(lines, lineCount, 0, numericFormat, 5, 3);
		addArray(module, dd, "vector");
		lineCount++;
		element = module;
		
	}

	private void ensureMolecule() {
		molecule = blockContainer.getMolecule();
		if (molecule == null) {
			throw new RuntimeException("No molecule; cannot analyse vibs");
		}
	}

	private void addScalar(CMLModule module, int i, String dictId) {
		addScalar(module, dictId, new CMLScalar(i));
	}

	private void addScalar(CMLModule module, double d, String dictId) {
		addScalar(module, dictId, new CMLScalar(d));
	}

	private void addScalar(CMLModule module, String dictId, CMLScalar scalar) {
		CMLProperty property = new CMLProperty();
		property.addScalar(scalar);
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), dictId);
		property.setDictRef(dictRef);
		module.appendChild(property);
	}

	private void addArray(CMLModule module, double[] dd, String localName) {
		CMLArray array = new CMLArray(dd);
		CMLProperty property = new CMLProperty();
		property.addArray(array);
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), localName);
		property.setDictRef(dictRef);
		module.appendChild(property);
	}

	private void makeZmat() {
		/*
        
		IZMAT =1 followed by two atom numbers. (I-J bond length)                        
		      =2 followed by three numbers. (I-J-K bond angle)                          
		      =3 followed by four numbers. (dihedral angle)                             
		         Torsion angle between planes I-J-K and J-K-L.                          
		      =4 followed by four atom numbers. (atom-plane)                            
		         Out-of-plane angle from bond I-J to plane J-K-L.                       
		      =5 followed by three numbers. (I-J-K linear bend)                         
		         Counts as 2 coordinates for the degenerate bend,                       
		         normally J is the center atom.  See $LIBE.                             
		      =6 followed by five atom numbers. (dihedral angle)                        
		         Dihedral angle between planes I-J-K and K-L-M.                         
		      =7 followed by six atom numbers. (ghost torsion)                          
		         Let A be the midpoint between atoms I and J, and                       
		         B be the midpoint between atoms M and N.  This                         
		         coordinate is the dihedral angle A-K-L-B.  The                         
		         atoms I,J and/or M,N may be the same atom number.                      
		         (If I=J AND M=N, this is a conventional torsion).                      
		         Examples: N2H4, or, with one common pair, H2POH.                       
			 */
		element = new CMLScalar();
	}

}
