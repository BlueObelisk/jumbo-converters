package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;

import fortran.format.JumboFormat;

public class GamessUSPunchBlock extends AbstractBlock {
	private static final String OPTIMIZED_RHF = "--- OPTIMIZED RHF";

	private static final String COORDS_SYMMETRY = " COORDINATES OF SYMMETRY UNIQUE ATOMS (ANGS)\n"+
			"   ATOM   CHARGE       X              Y              Z\n"+
			" ------------------------------------------------------------";

	private static Logger LOG = Logger.getLogger(GamessUSPunchBlock.class);
	
	private static final String ERHF = "erhf";
	private static final String ENUC = "enuc";
	private static final String ITERS = "iters";

	private static final String GRMS = "grms";
	private static final String GMAX = "gmax";
	private static final String HESS_ENERGY = "F.hess.energy";
	private static final String HESS_ENUC = "F.hess.enuc";
	private static final String HESS = "hess";
	private static final String E = "e";
	private static final String COORD = "coord";
	private static final String IATOM = "iatom";
	private static final String IVIB = "ivib";
	private static final String VECTOR = "vector";
	private static final String UNKNOWN = "unknown";
	private static final String Z = "z";
	private static final String Y = "y";
	private static final String X = "x";
	private static final String ATNUM = "atnum";
	private static final String ELEM = "elem";
	private static final String DATA_FROM_NSERCH = "-------------------- DATA FROM NSERCH=";
	private static final String RESULTS_FROM = "----- RESULTS FROM";
	private static final String COORDS_ORBS= "----- COORDS, ORBS,"; 
	private static final String CLOSED_SHELL= "--- CLOSED SHELL ORBITALS"; 
	private static final String OPEN_SHELL= "--- OPEN SHELL ORBITALS"; 
	/*
	 * keywords in legacy input
	 */
	public static final String CISVEC = "CISVEC";
	public static final String DATA = "DATA";
	public static final String DIPDR = "DIPDR";
	public static final String GRAD = "GRAD";
	public static final String GRAD1 = "GRAD1";
	public static final String GRAD2 = "GRAD2";
	public static final String HESSX = "HESS";
	public static final String IRC = "IRC";
	public static final String SCF  = "SCF";
	public static final String SOLEV  = "SOLEV";
	public static final String SPNORB  = "SPNORB";
	public static final String SUBCOR  = "SUBCOR";
	public static final String SUBSCF  = "SUBSCF";
	public static final String TDIPOLE  = "TDIPOLE";
	public static final String TVELOCITY  = "TVELOCITY";
	public static final String TWOEI  = "TWOEI";
	public static final String VEC  = "VEC";
	public static final String VEC1  = "VEC1";
	public static final String VEC2  = "VEC2";
	public static final String VIB  = "VIB";
	public static final String ZMAT = "ZMAT";

	private static final String POP1 = "pop1";
	private static final String POP2 = "pop2";
	private static final String POP3 = "pop3";
	private static final String POP4 = "pop4";

	private static final String DATE = "date";

	private CMLMolecule molecule;
	public GamessUSPunchBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (false) {
		} else if (CISVEC.equals(getBlockName())) {
			makeCisvec();
		} else if (DATA.equals(getBlockName())) {
			makeData();
		} else if (DIPDR.equals(getBlockName())) {
			makeDipdr();
		} else if (GRAD.equals(getBlockName())) {
			makeGrad();
		} else if (GRAD1.equals(getBlockName())) {
			makeGrad1();
		} else if (GRAD2.equals(getBlockName())) {
			makeGrad2();
		} else if (HESSX.equals(getBlockName())) {
			makeHess();
		} else if (IRC.equals(getBlockName())) {
			makeIrc();
		} else if (SCF.equals(getBlockName())) {
			makeScf();
		} else if (SOLEV.equals(getBlockName())) {
			makeSolev();
		} else if (SPNORB.equals(getBlockName())) {
			makeSpnorb();
		} else if (SUBCOR.equals(getBlockName())) {
			makeSubcor();
		} else if (SUBSCF.equals(getBlockName())) {
			makeSubscf();
		} else if (TDIPOLE.equals(getBlockName())) {
			makeTdipole();
		} else if (TVELOCITY.equals(getBlockName())) {
			makeTvelocity();
		} else if (TWOEI.equals(getBlockName())) {
			makeTwoei();
		} else if (VEC.equals(getBlockName())) {
			makeVec();
		} else if (VEC1.equals(getBlockName())) {
			makeVec1();
		} else if (VEC2.equals(getBlockName())) {
			makeVec2();
		} else if (VIB.equals(getBlockName())) {
			makeVib();
		} else if (ZMAT.equals(getBlockName())) {
			makeZmat();
		} else if (LegacyProcessor._ANONYMOUS.equals(getBlockName())) {
			makeAnonymous();
		} else {
			debug(UNKNOWN);
			System.err.println("Unknown blockname: "+getBlockName());
			throw new RuntimeException("Unknown blockname: "+getBlockName());
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

	
	private void makeTwoei() {
		/*
 $TWOEI
  9.38230741E+00 3.59976772E-01 4.84824999E+00 2.58194131E+00 3.69772753E-01
  2.26208416E+00 2.58194146E+00 3.69775829E-01 1.75438331E+00 2.26208429E+00
...
  3.50401191E-01 3.56418458E-01 3.21214602E-01 3.50398946E-01 6.05502942E-01
  2.35039664E-01 2.46416459E-01 2.46416796E-01 6.25819176E-01 6.25819121E-01
  8.04754778E-01
  9.38230741E+00 5.33706794E-06 4.84824999E+00 7.31854723E-02 4.94486827E-05
  2.26208416E+00 7.31854734E-02 4.94572956E-05 1.63533585E-01 2.26208429E+00
...
  3.96746625E-03 1.32000268E-03 1.32003348E-03 5.62533556E-02 5.62533408E-02
  8.04754778E-01
 $END
		 */
		// NYI
		element = preserveText();
	}


	
	private void makeCisvec() {
		/*
 $CISVEC
       272 SAPS,   1 STATES
STATE   1 ENERGY=     -113.7017742428
  8.829791E-21 -1.869456E-21  3.675079E-21 -3.584421E-08 -7.603602E-21
  2.703906E-21  2.873688E-21  8.179421E-08 -1.759198E-20  7.454616E-21
...
 -3.198973E-05 -1.805055E-05 -5.164041E-04 -1.152809E-19  6.155463E-06
  4.573113E-06 -2.025012E-06
 $END
		 */
		// NYI
		element = preserveText();
	}

	private void makeTdipole() {
		/**
		 * 		 $TDIPOLE
		         1         1        18
		      -75.0101113553      -75.0101113553
		        0.0000000000        0.0000000000       -0.6389554616
		         1         2        18
		      -75.0101113553      -74.3945819389
		        0.0000000000        0.0000000000        0.3926137047
		         2         2        18
		      -74.3945819389      -74.3945819389
		        0.0000000000        0.0000000000        0.1297954762
		 $END
		 */
				// NYI
				element = preserveText();
			}

	private void makeTvelocity() {
		/**
 $TVELOCITY
         1         2        18
      -75.0101113553      -74.3945819389
        0.0000000000        0.0000000000        0.3682047219
 $END
		 */
				// NYI
				element = preserveText();
			}

	private void makeIrc() {
		/**
 $IRC   PACE=GS2        STRIDE= 0.30000   NPOINT=??
        STOTAL=  0.299939   NEXTPT=   2
        NPRT= 0   NPUN= 0   SADDLE=.FALSE.
        GA(  1)=-5.949328598E-03,-1.396681562E-02, 0.000000000E+00
        GA(  4)=-8.414775838E-03, 1.241853708E-02, 0.000000000E+00
        GA(  7)= 1.436410444E-02, 1.548278538E-03, 0.000000000E+00
 $END   
*/
		// NYI
		element = preserveText();
	}

	private void makeDipdr() {
		/**
		$DIPDR
		 -2.68829961E+00 0.00000000E+00 0.00000000E+00
		  0.00000000E+00-1.49612845E-01 0.00000000E+00
...
		  0.00000000E+00 7.48204205E-02 7.54407241E-01
		  0.00000000E+00 1.06457228E+00-2.00610985E-01
		 $END	 */
		// NYI
		element = preserveText();
	}
	
	private void makeSpnorb() {
		/*
 $SPNORB ---   CI   SPIN-ORBIT MATRIX ELEMENTS.
    1 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
      0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
    1 0.00000000E+00 0.00000000E+00
    2 0.00000000E+00 0.00000000E+00 1.55948760E-09 0.00000000E+00 0.00000000E+00
      0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
    2 0.00000000E+00 0.00000000E+00
    3 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 1.52964318E+04
      0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 6.49682982E+01
    3 0.00000000E+00 0.00000000E+00
    4 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
      0.00000000E+00-1.52964318E+04 0.00000000E+00 0.00000000E+00 0.00000000E+00
    4 0.00000000E+00 0.00000000E+00
    5 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
     -6.49682982E+01 0.00000000E+00 0.00000000E+00-1.52964318E+04 0.00000000E+00
    5 0.00000000E+00 0.00000000E+00
    6 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
      0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00 0.00000000E+00
    6-1.52964318E+04 0.00000000E+00
  CI   ADIABATIC STATES---
          0.000          0.000      15296.432     -15296.432     -15296.432
     -15296.432
  CI   SPIN-MIXED STATES---
     -15296.570     -15296.432     -15296.432          0.000          0.000
      15296.570
 $END
		 */
		// NYI
		element = preserveText();
	}
	
	private void makeSolev() {
		/*
		 $SOLEV
		    1  0.0       0.0000    -54.9382263296
		    2  1.0       0.1380    -54.9382257010
		    3  1.0       0.1380    -54.9382257010
		    4  2.0   15296.5697    -54.8685312164
		    5  2.0   15296.5697    -54.8685312164
		    6  0.0   30593.1395    -54.7988361032
		 $END
		 */
		// NYI
		element = preserveText();
	}
	
	private void makeScf() {
		//  $SCF    CICOEF( 1)=  0.97750533, -0.21091073 $END   
		// NYI
		element = preserveText();
	}
	
	private void makeSubscf() {
/**
 $SUBSCF
! SUBSYSTEM     1
  1  7  0
  2  8  3  9  0
  0
! SUBSYSTEM     2
   2   8   0
   1   7   3   9   4  10   0
   0
! SUBSYSTEM     3
   3   9   0
   1   7   2   8   4  10   5  11   0
   0
! SUBSYSTEM     4
   4  10   0
   2   8   3   9   5  11   6  12   0
   0
! SUBSYSTEM     5
   5  11   0
   3   9   4  10   6  12   0
   0
! SUBSYSTEM     6
   6  12   0
   4  10   5  11   0
   0
 $END
 */
		// NYI
		element = preserveText();
	}
	
	private void makeSubcor() {
/**
 $SUBCOR
! SUBSYSTEM     1
  1  7  0
  2  8  0
  0
! SUBSYSTEM     2
  2  8  0
  1  7  3  9  0
  0
! SUBSYSTEM     3
   3   9   0
   2   8   4  10   0
   0
! SUBSYSTEM     4
   4  10   0
   3   9   5  11   0
   0
! SUBSYSTEM     5
   5  11   0
   4  10   6  12   0
   0
! SUBSYSTEM     6
   6  12   0
   5  11   0
   0
 $END
 */
	}
	
	private void makeAnonymous() {
		lineCount = 0;
		CMLModule module = new CMLModule();
		String line = lines.get(lineCount);
		if (line.startsWith(RESULTS_FROM)) {
			results0(module);
		} else if (line.startsWith(COORDS_ORBS)) {
			results(module);
		} else if (line.startsWith(DATA_FROM_NSERCH)) {
			nserch(module);
		} else if (line.startsWith(CLOSED_SHELL) || line.startsWith(OPEN_SHELL)) {
			closedOpenShell(module);
		} else if (line.startsWith(OPTIMIZED_RHF)) {
			optimizedRHF(module);
		} else if (line.startsWith(" POPULATION ANALYSIS")) {
			populationAnalysis(module);
		} else {
			preserveText(module);
		}
		element = module;
	}
	
	private void optimizedRHF(CMLModule module) {
		/**
--- OPTIMIZED RHF      MO-S --- GENERATED AT Mon Oct 25 13:07:35 2010
E=      -37.2380397698, E(NUC)=    5.9560361192
		 */
		JumboFormat jumboFormat = new JumboFormat();
		jumboFormat.addParsedScalars(module, abstractCommon.getPrefix(),
				"'--- OPTIMIZED RHF      MO-S --- GENERATED AT 'A24", lines.get(lineCount++),
				new String[]{D_+DATE});
		jumboFormat.addParsedScalars(module, abstractCommon.getPrefix(),
				"'E=     'F15.10', E(NUC)='F15.10", lines.get(lineCount++),
				new String[]{F_+E, F_+ENUC});
	}

	private void populationAnalysis(CMLModule module) {
		/**
  POPULATION ANALYSIS
 C            5.99315   0.00685   5.95532   0.04468
 H            1.00342  -0.00342   1.02234  -0.02234
 H            1.00342  -0.00342   1.02234  -0.02234
  MOMENTS AT POINT    1 X,Y,Z=  0.000000  0.000000  0.000000
  DIPOLE       0.000000  0.000000  1.515763
		 */
		int atomCount = blockContainer.getMolecule().getAtomCount();
		lineCount++;
		CMLArrayList columns = new JumboFormat().createTableColumnsAsArrayList(
				abstractCommon.getPrefix(), "A11,4F10.5",
				lineCount, lines, atomCount, new String[]{A_+ELEM, F_+POP1, F_+POP2, F_+POP3, F_+POP4});
		module.appendChild(columns);
	}

	private void makeVec() {
		/**
 $VEC   
 1  1 9.83733867E-01 6.32729846E-02 0.00000000E+00 0.00000000E+00 1.13580482E-02
 1  2-1.64394234E-02-1.64394234E-02
 2  1-2.43674945E-01 6.41661903E-01 0.00000000E+00 0.00000000E+00 1.70910229E-01
 2  2 2.85808918E-01 2.85808918E-01
 .. 7 lines overall
 7  1 0.00000000E+00 0.00000000E+00 1.12514651E+00 0.00000000E+00 0.00000000E+00
 7  2 8.36936903E-01-8.36936903E-01
		 */
		element = new CMLModule();
		element.appendChild(makePackedLines());
	}

	private void makeVec1() {
		/**
 $VEC1  
 1  1 1.00173154E+00-4.42355961E-04-8.94950868E-03 0.00000000E+00 0.00000000E+00
 1  2 1.48836591E-04 0.00000000E+00 0.00000000E+00 8.83998840E-04-3.30678346E-04
 1  3 5.47496681E-04-2.16818336E-04 0.00000000E+00 0.00000000E+00 0.00000000E+00
 1  4-2.39910669E-03 1.14463450E-03 0.00000000E+00-5.27016825E-04-4.94322215E-04
 1  5-2.39910669E-03 1.14463450E-03 0.00000000E+00 5.27016825E-04-4.94322215E-04
 2  1-6.40850833E-03 3.70026464E-01 2.83584610E-01 0.00000000E+00 0.00000000E+00
...
 7  4 6.44523230E-01 2.20422384E-01 0.00000000E+00-1.46637220E-02-6.72413304E-03
 7  5-6.44523230E-01-2.20422384E-01 0.00000000E+00-1.46637220E-02 6.72413304E-03
		 */
		element = new CMLModule();
		element.appendChild(makePackedLines());
	}

	private void makeVec2() {
		/**
 $VEC2
 1  1 1.00173154E+00-4.42355961E-04-8.94950868E-03 0.00000000E+00 0.00000000E+00
 1  2 1.48836591E-04 0.00000000E+00 0.00000000E+00 8.83998840E-04-3.30678346E-04
 1  3 5.47496681E-04-2.16818336E-04 0.00000000E+00 0.00000000E+00 0.00000000E+00
 1  4-2.39910669E-03 1.14463450E-03 0.00000000E+00-5.27016825E-04-4.94322215E-04
 1  5-2.39910669E-03 1.14463450E-03 0.00000000E+00 5.27016825E-04-4.94322215E-04
 2  1-6.40850833E-03 3.70026464E-01 2.83584610E-01 0.00000000E+00 0.00000000E+00
...
 7  4 6.44523230E-01 2.20422384E-01 0.00000000E+00-1.46637220E-02-6.72413304E-03
 7  5-6.44523230E-01-2.20422384E-01 0.00000000E+00-1.46637220E-02 6.72413304E-03
		 */
		element = new CMLModule();
		element.appendChild(makePackedLines());
	}

	private CMLScalar makePackedLines() {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
			sb.append(CMLConstants.S_NEWLINE);
		}
		return new CMLScalar(sb.toString().trim());
	}

	private void debug(String msg) {
		System.out.println("=========="+msg+"==========");
		for (String line : lines) {
			System.out.println(">>"+line);
		}
		System.out.println("=========="+msg+"==========");
	}

	/**
 $DATA  
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16                            
C1       0
H1          1.0     -2.9805271364       .9147039208       .1059830464
   CCD     0
        
	 */
	private void makeData() {
		lineCount = 0;
		molecule = new CMLMolecule();
		String title = lines.get(lineCount++);
		molecule.setTitle(title);
		CMLSymmetry symmetry = new CMLSymmetry();
		//C1       0
		String pgline = lines.get(lineCount++);
		String[] tokens = pgline.split(CMLConstants.WHITESPACE);
		symmetry.setPointGroup(tokens[0]);
		molecule.appendChild(symmetry);
		// unexpected lines here. possibly controlled by symmetry card
		// currently fudge by eating white
		eatEmptyLines();
		int id = 1;
		while (lineCount < lines.size()) {
			addAtom(id++);
		}
		element = molecule;
		blockContainer.setMolecule(molecule);
	}

	private void eatEmptyLines() {
		while (lineCount < lines.size()) {
			if (lines.get(lineCount).trim().length() != 0) {
				break;
			}
			lineCount++;
		}
	}

	private void addAtom(int id) {
		String line = lines.get(lineCount);
//			H1          1.0     -2.9805271364       .9147039208       .1059830464
		CMLAtom atom = new CMLAtom();
		CMLLabel label = new CMLLabel();
		label.setCMLValue(line.substring(0, 10));
		atom.addLabel(label);
		int atomicNumber = new Integer(line.substring(10, 13).trim()); 
		atom.setElementType(ChemicalElement.getElement(atomicNumber).getSymbol());
		atom.setX3(new Double(line.substring(15, 33).trim()));
		atom.setY3(new Double(line.substring(33, 51).trim()));
		atom.setZ3(new Double(line.substring(51, 69).trim()));
		atom.setId("a"+(id++));
		molecule.addAtom(atom);
		lineCount++;
		CMLScalar basis = readBasis(atom);
		atom.appendChild(basis);
	}

	
	private CMLScalar readBasis(CMLAtom atom) {
		// read until blannk
		StringBuffer sb = new StringBuffer();
		while (lineCount < lines.size()) {
			String line = lines.get(lineCount++);
			if (line.trim().length() == 0) {
				break;
			}
			sb.append(line);
			sb.append(CMLConstants.S_NEWLINE);
		}
		CMLScalar basis = new CMLScalar(sb.toString().trim());
		basis.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), "basis"));
		atom.appendChild(basis);
		return basis;
	}

	public static final Pattern ATOM_BOND_COUNT = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	public static final Pattern ATOM = Pattern.compile("\\s*([A-Z][a-z]?)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)");
	public static final Pattern BOND = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	
	private void makeGrad() {
		CMLModule module = new CMLModule();
		module.setRole(abstractCommon.getPrefix());
		/*
E=     -417.0071979209  GMAX=    .0518798  GRMS=    .0144469
H1           1.    1.4236802098E-02   -1.2652467333E-02   -2.0583432080E-03
C2           6.   -1.7931214639E-03   -2.3258102960E-02    2.7744336442E-03
N3           7.   -1.5142322939E-02    4.8724521568E-03   -3.9199279015E-03
		 */
		lineCount = 0;
		
		String flags = lines.get(lineCount++);
		JumboFormat jumboFormat = new JumboFormat();
		List<CMLScalar> scalars = jumboFormat.addParsedScalars(
			module,
			abstractCommon.getPrefix(), 
			"('E='F20.10'  GMAX='F12.7'  GRMS='F12.7)", 
			flags, 
			new String[]{F_+E, F_+GMAX, F_+GRMS});
		addMoleculeAsColumns(module, "A10F5.1F20.10F20.10F20.10");
		element = module;
	}

	private void makeGrad1() {
		CMLModule module = new CMLModule();
		module.setRole(abstractCommon.getPrefix());
		/*
E=     -417.0071979209  GMAX=    .0518798  GRMS=    .0144469
H1           1.    1.4236802098E-02   -1.2652467333E-02   -2.0583432080E-03
C2           6.   -1.7931214639E-03   -2.3258102960E-02    2.7744336442E-03
N3           7.   -1.5142322939E-02    4.8724521568E-03   -3.9199279015E-03
		 */
		lineCount = 0;
		
		String flags = lines.get(lineCount++);
		JumboFormat jumboFormat = new JumboFormat();
		List<CMLScalar> scalars = jumboFormat.addParsedScalars(
			module,
			abstractCommon.getPrefix(), 
			"('E='F20.10'  GMAX='F12.7'  GRMS='F12.7)", 
			flags, 
			new String[]{F_+E, F_+GMAX, F_+GRMS});
		addMoleculeAsColumns(module, "A10F5.1F20.10F20.10F20.10");
		element = module;
	}

	private void makeGrad2() {
		CMLModule module = new CMLModule();
		module.setRole(abstractCommon.getPrefix());
		/*
E=     -417.0071979209  GMAX=    .0518798  GRMS=    .0144469
H1           1.    1.4236802098E-02   -1.2652467333E-02   -2.0583432080E-03
C2           6.   -1.7931214639E-03   -2.3258102960E-02    2.7744336442E-03
N3           7.   -1.5142322939E-02    4.8724521568E-03   -3.9199279015E-03
		 */
		lineCount = 0;
		
		String flags = lines.get(lineCount++);
		JumboFormat jumboFormat = new JumboFormat();
		List<CMLScalar> scalars = jumboFormat.addParsedScalars(
			module,
			abstractCommon.getPrefix(), 
			"('E='F20.10'  GMAX='F12.7'  GRMS='F12.7)", 
			flags, 
			new String[]{F_+E, F_+GMAX, F_+GRMS});
		addMoleculeAsColumns(module, "A10F5.1F20.10F20.10F20.10");
		element = module;
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
		module.setDictRef("gammesus:hess");
		lineCount = 0;
		int coordCount = molecule.getAtomCount()*3;
		String flags = lines.get(lineCount++);
		List<CMLScalar> scalars = new JumboFormat().addParsedScalars(
				module,
				abstractCommon.getPrefix(), 
				"('ENERGY IS'F20.10' E(NUC) IS'F20.10)", 
				flags, 
				new String[]{F_+HESS_ENERGY, F_+HESS_ENUC});
		JumboFormat jumboFormat = new JumboFormat();
		CMLMatrix matrix = readMatrix(coordCount, jumboFormat);
		this.addDictRefTo(matrix, HESS);
		module.appendChild(matrix);
		element = module;
	}

	private CMLMatrix readMatrix(int coordCount, JumboFormat jumboFormat) {
		double[][] matrixx = new double[coordCount][];
	    for (int i = 0; i < coordCount; i++) {
	    	CMLArray array = jumboFormat.parseMultipleLinesToArray(
	    			"(5X,5E15.8)", lines, lineCount, coordCount, CMLConstants.XSD_DOUBLE);
	    	lineCount += jumboFormat.getLinesRead();
	    	matrixx[i] = array.getDoubles();
	    }
		CMLMatrix matrix = new CMLMatrix(matrixx);
		return matrix;
	}
	
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
		lineCount = 0;
		int coordCount = molecule.getAtomCount()*3;
		String flags = lines.get(lineCount++);
		JumboFormat jumboFormat = new JumboFormat();
		jumboFormat.addParsedScalars(
				module,
				abstractCommon.getPrefix(), 
				"('         IVIB=',I4,' IATOM=',I4,' ICOORD=',I4,' E='F20.10)", 
				flags, 
				new String[]{I_+IVIB, I_+IATOM, I_+COORD, F_+E});
		jumboFormat = new JumboFormat();
		CMLArray vector = jumboFormat.parseMultipleLinesToArray(
				"(5F16.9)", lines, lineCount, coordCount, CMLConstants.XSD_DOUBLE);
		this.addDictRefTo(vector, VECTOR);
		module.appendChild(vector);
		lineCount += jumboFormat.getLinesRead();
		CMLArray unk = new JumboFormat().parseToSingleLineArray("(3F16.9)", lines.get(lineCount++), CMLConstants.XSD_DOUBLE);
		this.addDictRefTo(unk, UNKNOWN);
		module.appendChild(unk);
		element = module;
	}
	private void ensureMolecule() {
		molecule = blockContainer.getMolecule();
		if (molecule == null) {
			throw new RuntimeException("No molecule; cannot analyse vibs");
		}
	}

	private void makeZmat() {
		/**
 $ZMAT   IZMAT(1)=
         3,   2,   3,   5,   7,
         3,   3,   5,   7,  13,
         1,   1,   2,
         1,   2,   4,
         ...
         1,  13,  15,
         2,   1,   2,   3,
         2,   1,   2,   4,
         ...
         2,  15,  13,  16,
         3,   1,   2,   3,   6,
         3,   1,   2,   3,   5,
         ...
         3,  14,   7,  13,  15,
 $END
		 */
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
		CMLZMatrix matrix = new CMLZMatrix();
		// dont skip first two lines
		lineCount = 0;
		while (lineCount < lines.size()) {
			String line = lines.get(lineCount++).trim();
			// remove trailing comma
			if (line.endsWith(",")) {
				line = line.substring(0, line.length()-1);
			}
			//remove whitespace and create 
			// assume fields are comma-separated
			String fields[] = line.replaceAll(" ", "").split(",");
			IntArray intArray = new IntArray(fields);
			int key = intArray.elementAt(0);
			intArray.deleteElement(0);
			int size = intArray.size();
			if (size < 2) {
				throw new RuntimeException("too few fields for zmat: "+line);
			} else if (size > 6) {
				throw new RuntimeException("too many fields for zmat: "+line);
			}
			if (key == 1 && size == 2) {
				CMLLength length = new CMLLength();
				length.setAtomRefs2(new String[]{
				"a"+intArray.elementAt(0), 
				"a"+intArray.elementAt(1)});
				matrix.addLength(length);
			} else if (key == 2 && size == 3) {
				CMLAngle angle = new CMLAngle();
				angle.setAtomRefs3(new String[]{
				"a"+intArray.elementAt(0),
				"a"+intArray.elementAt(1),
				"a"+intArray.elementAt(2)});
				matrix.addAngle(angle);
			} else if (key == 3 && size == 4) {
				CMLTorsion torsion = new CMLTorsion();
				torsion.setAtomRefs4(new String[]{
				"a"+intArray.elementAt(0),
				"a"+intArray.elementAt(1),
				"a"+intArray.elementAt(2),
				"a"+intArray.elementAt(3)});
				matrix.addTorsion(torsion);
			} else if (key == 4 && size == 4) {
				LOG.warn("JUMBO does not support zmat field: "+line);
			} else if (key == 5 && size == 3) {
				LOG.warn("JUMBO does not support zmat field: "+line);
			} else if (key == 6 && size == 5) {
				LOG.warn("JUMBO does not support zmat field: "+line);
			} else if (key == 7 && size == 6) {
				LOG.warn("JUMBO does not support zmat field: "+line);
			} else {
				throw new RuntimeException("bad zmat line: "+line);
			}
		}
		element = matrix;
	}
	
	private void closedOpenShell(CMLModule module) {
		/**
--- CLOSED SHELL ORBITALS --- GENERATED AT Mon Oct 25 13:07:35 2010
Methylene...1-A-1 state...RHF/STO-2G                                            
E(RHF)=      -37.2322678015, E(NUC)=    6.1221376700,    8 ITERS
or 
E(ROHF)=      -37.2778767090, E(NUC)=    6.1450367257,    7 ITERS
		 */
		lineCount++;
		Pattern pattern = Pattern.compile("E\\(([^\\)]+)\\)=\\s*([\\-\\.\\d]+), E\\(([^\\)]+)\\)=\\s*([\\-\\.\\d]+),\\s+(\\d+) ITERS");
//		Pattern pattern = Pattern.compile("E\\(([^\\)]+)\\)=\\s*([\\-\\.\\d]+), E\\(([^\\)]+)\\)=\\s*([\\-\\.\\d]+),\\s+.*");
		System.out.println(pattern);
		CMLScalar title = new CMLScalar(lines.get(lineCount++));
		module.appendChild(title);
		System.out.println(lines.get(lineCount));
		new JumboFormat().addParsedScalars(module, abstractCommon.getPrefix(),  pattern, 
		lines.get(lineCount++), new String[]{A_+DICTREF, F_+ERHF, A_+DICTREF, F_+ENUC, I_+ITERS});
	}

	private CMLModule preserveText(CMLModule module) {
		String line = Util.concatenate((String[])lines.toArray(new String[0]), CMLConstants.S_NEWLINE);
		CMLScalar scalar = new CMLScalar(line);
		module.appendChild(scalar);
		this.setBlockName(UNKNOWN);
		return module;
	}
	
	private CMLModule preserveText() {
		return preserveText(new CMLModule());
	}

	private void nserch(CMLModule module) {
		/**
-------------------- DATA FROM NSERCH=  12 --------------------
 COORDINATES OF SYMMETRY UNIQUE ATOMS (ANGS)
   ATOM   CHARGE       X              Y              Z
 ------------------------------------------------------------
 H1          1.0  -3.0274208244    .9305579239   -.0180318347
 C2          6.0  -2.1178295124    .2873036749   -.0637304065
		 */
		lineCount = 0;
		readNserch(module);
		readLines(COORDS_SYMMETRY);
		addMoleculeAsColumns(module, "' 'A8,F7.1,3F15.10");
		setBlockName(GamessUSCommon.NSERCH);
	}

	private void readNserch(CMLModule module) {
		JumboFormat jumboFormat = new JumboFormat();
		List<CMLScalar> scalars = jumboFormat.parseToScalars(
				abstractCommon.getPrefix(), 
				"('-------------------- DATA FROM NSERCH= 'I3' --------------------')",
				lines.get(lineCount++), new String[] {I_+GamessUSCommon.NCYC});
		module.appendChild(scalars.get(0));
	}

	private void readLines(String toRead) {
		String[] linesToRead = toRead.split(CMLConstants.S_NEWLINE);
		for (String lineToRead : linesToRead) {
			if (!lineToRead.equals(lines.get(lineCount++))) {
				throw new RuntimeException("cannot find line :"+lineToRead+"\nfound :"+lines.get(lineCount-1));
			}
		}
	}

	private void addMoleculeAsColumns(CMLModule module, String format) {
		int natoms = blockContainer.getMolecule().getAtomCount();
		CMLMolecule molecule = new CMLMolecule();
		JumboFormat jumboFormat = new JumboFormat();
		for (int i = 0; i < natoms; i++) {
			List<Object> scalars = jumboFormat.parseFortranLine(format, lines.get(lineCount++));
			CMLAtom atom = new CMLAtom("a"+(i+1));
			int atNum = (int) Math.round((Double)scalars.get(1));
			atom.setElementType(ChemicalElement.getElement(atNum).getSymbol());
			atom.setXYZ3(new Point3((Double)scalars.get(2), (Double)scalars.get(3), (Double)scalars.get(4)));
			CMLLabel label = new CMLLabel();
			label.setCMLValue(scalars.get(0).toString());
			atom.addLabel(label);
			molecule.addAtom(atom);
		}
		module.appendChild(molecule);
	}

	private void results0(CMLModule module) {
		if (lines.size() != 1) {
			throw new RuntimeException("expected 1 lines, found: "+lines.size());
		}
		// no=op
	}
	
	private void results(CMLModule module) {
		lineCount++;
		readLines(COORDS_SYMMETRY);
		addMoleculeAsColumns(module, "' 'A8,F7.1,3F15.10");
		setBlockName(GamessUSCommon.RESULTS);
	}


}
