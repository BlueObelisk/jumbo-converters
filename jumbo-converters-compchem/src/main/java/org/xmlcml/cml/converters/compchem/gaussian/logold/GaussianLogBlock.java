package org.xmlcml.cml.converters.compchem.gaussian.logold;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.molutil.ChemicalElement;


public class GaussianLogBlock extends AbstractBlock {
	private static final String OPTIMIZED_RHF = "--- OPTIMIZED RHF";

	private static final String COORDS_SYMMETRY = " COORDINATES OF SYMMETRY UNIQUE ATOMS (ANGS)\n"+
			"   ATOM   CHARGE       X              Y              Z\n"+
			" ------------------------------------------------------------";

	public static Logger LOG = Logger.getLogger(GaussianLogBlock.class);
	
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
//	public static final String CISVEC = "CISVEC";
//	public static final String DATA = "DATA";
//	public static final String DIPDR = "DIPDR";
//	public static final String GRAD = "GRAD";
//	public static final String GRAD1 = "GRAD1";
//	public static final String GRAD2 = "GRAD2";
//	public static final String HESSX = "HESS";
//	public static final String IRC = "IRC";
//	public static final String SCF  = "SCF";
//	public static final String SOLEV  = "SOLEV";
//	public static final String SPNORB  = "SPNORB";
//	public static final String SUBCOR  = "SUBCOR";
//	public static final String SUBSCF  = "SUBSCF";
//	public static final String TDIPOLE  = "TDIPOLE";
//	public static final String TVELOCITY  = "TVELOCITY";
//	public static final String TWOEI  = "TWOEI";
//	public static final String VEC  = "VEC";
//	public static final String VEC1  = "VEC1";
//	public static final String VEC2  = "VEC2";
//	public static final String VIB  = "VIB";
//	public static final String ZMAT = "ZMAT";
//
//	private static final String POP1 = "pop1";
//	private static final String POP2 = "pop2";
//	private static final String POP3 = "pop3";
//	private static final String POP4 = "pop4";
//
//	private static final String DATE = "date";
//
//	private static final String DICTREF_NEXT = "dictRefNext";

	public GaussianLogBlock(BlockContainer blockContainer) {
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
		jumboReader = new JumboReader(this.getDictionary(), abstractCommon.getPrefix(), lines);
		if (getBlockName() == null) {
			throw new RuntimeException("null blockName");
//		} else if (CISVEC.equals(getBlockName())) {
//			makeCisvec();
//		} else if (DATA.equals(getBlockName())) {
//			makeData();
//		} else if (DIPDR.equals(getBlockName())) {
//			makeDipdr();
//		} else if (GRAD.equals(getBlockName())) {
//			makeGrad();
//		} else if (GRAD1.equals(getBlockName())) {
//			makeGrad1();
//		} else if (GRAD2.equals(getBlockName())) {
//			makeGrad2();
//		} else if (HESSX.equals(getBlockName())) {
//			makeHess();
//		} else if (IRC.equals(getBlockName())) {
//			makeIrc();
//		} else if (SCF.equals(getBlockName())) {
//			makeScf();
//		} else if (SOLEV.equals(getBlockName())) {
//			makeSolev();
//		} else if (SPNORB.equals(getBlockName())) {
//			makeSpnorb();
//		} else if (SUBCOR.equals(getBlockName())) {
//			makeSubcor();
//		} else if (SUBSCF.equals(getBlockName())) {
//			makeSubscf();
//		} else if (TDIPOLE.equals(getBlockName())) {
//			makeTdipole();
//		} else if (TVELOCITY.equals(getBlockName())) {
//			makeTvelocity();
//		} else if (TWOEI.equals(getBlockName())) {
//			makeTwoei();
//		} else if (VEC.equals(getBlockName())) {
//			makeVec();
//		} else if (VEC1.equals(getBlockName())) {
//			makeVec1();
//		} else if (VEC2.equals(getBlockName())) {
//			makeVec2();
//		} else if (VIB.equals(getBlockName())) {
//			makeVib();
//		} else if (ZMAT.equals(getBlockName())) {
//			makeZmat();
		} else if (LegacyProcessor._ANONYMOUS.equals(getBlockName())) {
			makeAnonymous();
		} else {
//			debug(UNKNOWN);
			System.err.println("Unknown blockname: "+getBlockName());
			throw new RuntimeException("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			checkIdAndAdd(element, getBlockName().toLowerCase());
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	
	private void makeAnonymous() {
		CMLModule module = new CMLModule();
		jumboReader.setParentElement(module);
		String line = jumboReader.peekLine();
//		if (line.startsWith(RESULTS_FROM)) {
//			anonResultsFrom();
//		} else if (line.startsWith(COORDS_ORBS)) {
//			anonCoordsOrbs();
//		} else if (line.startsWith(DATA_FROM_NSERCH)) {
//			anonNserch();
//		} else if (line.startsWith(CLOSED_SHELL) || line.startsWith(OPEN_SHELL)) {
//			anonClosedOpenShell();
//		} else if (line.startsWith(OPTIMIZED_RHF)) {
//			anonOptimisedRHF();
//		} else if (line.startsWith(" POPULATION ANALYSIS")) {
//			anonPopulationAnalysis();
//		} else {
//			preserveText();
//		}
		element = module;
	}
	
}
