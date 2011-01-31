package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.Template;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.euclid.Util;

public class NWChemLogBlock extends AbstractBlock {

	public static final String _3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION = "3 Center 2 Electron Integral Information";
	public static final String ACKNOWLEDGMENT = "ACKNOWLEDGMENT";
	public static final String ARGUMENT = "argument";
	public static final String ATOMIC_MASS = "Atomic Mass";
	public static final String AUTHORS = "AUTHORS";
	public static final String AUTO_Z = "auto-z";
	public static final String BASIS = "Basis";
	public static final String CASE = "case";
	public static final String CENTER_1 = "center 1";
	public static final String CENTER_OF_MASS = "center of mass";
	public static final String CENTER_ONE = "center one";
	public static final String CITATION = "CITATION";
	public static final String CONVERGENCE_INFORMATION = "Convergence Information";
	public static final String DIRECTORY_INFORMATION = "Directory information";
	public static final String FINAL_EIGENVALUES = "Final eigenvalues";
	public static final String FINAL_MOLECULAR_ORBITAL_ANALYSIS = "Final Molecular Orbital Analysis";
	public static final String FINAL_RHF_RESULTS = "Final RHF  results";
	public static final String GA_STATISTICS_FOR_PROCESS = "GA Statistics for process";
	public static final String GENERAL_INFORMATION = "General Information";
	public static final String GEOMETRY = "Geometry";
	public static final String GRID_INFORMATION = "Grid Information";
	public static final String INTERNUCLEAR_ANGLES = "internuclear angles";
	public static final String INTERNUCLEAR_DISTANCES = "internuclear distances";
	public static final String JOB_INFORMATION = "Job information";
	public static final String MEMORY_INFORMATION = "Memory information";
	public static final String MOMENTS_OF_INERTIA = "moments of inertia";
	public static final String MULTIPOLE_ANALYSIS_OF_THE_DENSITY = "Multipole analysis of the density";
	public static final String MULLIKEN_ANALYSIS_OF_THE_TOTAL_DENSITY = "Mulliken analysis of the total density";
	public static final String NON_VARIATIONAL_INITIAL_ENERGY = "Non-variational initial energy";
	public static final String NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE = "Northwest Computational Chemistry Package";
	public static final String NUCLEAR_DIPOLE_MOMENT = "Nuclear Dipole moment";
	public static final String NW_CHEM_DFT_MODULE = "NWChem DFT Module";
	public static final String NW_CHEM_CPHF_MODULE = "NWChem CPHF Module";
	public static final String NW_CHEM_SCF_MODULE = "NWChem SCF Module";
	public static final String NW_CHEM_INPUT_MODULE = "NWChem Input Module";
	public static final String NW_CHEM_PROPERTY_MODULE = "NWChem Property Module";
	public static final String SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS = "Summary of allocated global arrays";
	public static final String SUMMARY_OF_BASIS = "Summary of";
	public static final String SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS = "Superposition of Atomic Density Guess";
	public static final String SCREENING_TOLERANCE_INFORMATION = "Screening Tolerance Information";
	public static final String SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS = "Symmetry analysis of molecular orbitals";
	public static final String SYMMETRY_INFORMATION = "Symmetry information";
	public static final String XC_INFORMATION = "XC Information";
	public static final String XYZ_FORMAT_GEOMETRY = "XYZ format geometry";
	public static final String Z_MATRIX = "Z-matrix";


	private static final String NATOMS = "natoms";
	private static final String SUM_ATOM_ENER = "sumAtomEner";
	private static final String LUMO = "lumo";
	private static final String HOMO = "homo";
	private static final String ENER2E = "ener2e";
	private static final String ENER1E = "ener1e";
	private static final String TOTENER = "totener";
	private static final String ATOMMASS = "atommass";
	private static final String ATOMSYM = "atomsym";
	private static final String EXP = "exp";
	private static final String SPD = "spd";
	private static final String SHELL = "shell";
	private static final String ELEM_NAME = "elemName";
	private static final String COORDTYPE = "coordtype";
	private static final String BASIS1 = "basis1";
	private static final String TYPES = "types";
	private static final String FUNCTIONS = "functions";
	private static final String SHELLS = "shells";
	private static final String TOTITERTIME = "totitertime";
	private static final String NUMITERDENS = "numiterdens";
	private static final String NUCREPENER = "nucrepener";
	private static final String ECHANGECORR = "echangecorr";
	private static final String COULOMB = "coulomb";
	private static final String ONEELECENER = "oneelecener";
	private static final String TOTALDFT = "totaldft";
	private static final String DIISERR = "diiserr";
	private static final String RMSDENS = "rmsdens";
	private static final String DELTAE = "deltae";
	private static final String ENERGY = "energy";
	private static final String ORB = "orb";
	private static final String COEFF = "coeff";
	private static final String BFN = "bfn";
	private static final String RSQUARED = "rsquared";
	private static final String V3 = "v3";
	private static final String V2 = "v2";
	private static final String V1 = "v1";
	private static final String E = "e";
	private static final String OCC = "occ";
	private static final String VECTOR = "vector";
	private static final String EIGENVAL = "eigenval";
	private static final String NUCLEAR = "nuclear";
	private static final String BETA = "beta";
	private static final String ALPHA = "alpha";
	private static final String TOTAL = "total";
	private static final String L = "l";
	private static final String SHELLCHARGE = "shellcharge";
	private static final String ATNUM = "atnum";
	private static final String ATSYM = "atsym";
	private static final String MOMINT = "momint";
	private static final String ORBSYM = "orbsym";
	private static final String IRRREP = "irrrep";
	private static final String CHARGE = "charge";
	private static final String Z = "z";
	private static final String Y = "y";
	private static final String X = "x";
	private static final String SYMMETRY_UNIQUE = "symmetryUnique";
	private static final String GROUP_UNIQUECENTER = "group.uniquecenter";
	private static final String GROUP_ORDER = "group.order";
	private static final String GROUP_NUMBER = "group.number";
	private static final String GROUP_NAME = "group.name";
	private static final String ANGPTS = "angpts";
	private static final String RADCUT = "radcut";
	private static final String RADPTS = "radpts";
	private static final String BSRAD = "bsrad";
	private static final String TAG = "tag";
	private static final String MAG_C = "mag.c";
	private static final String MAG_B = "mag.b";
	private static final String MAG_A = "mag.a";
	private static final String TOTAL_SHIELDING_TENSOR = "TotalShieldingTensor";
	private static final String PARAMAGNETIC = "Paramagnetic";
	private static final String DIAMAGNETIC = "Diamagnetic";
	private static final String MAGNETIC = "magnetic";
	private static final String ELSYM = "elsym";
	private static final String SERIAL = "serial";
	private static final String TIME = "time";
	private static final String RESIDUAL = "residual";
	private static final String NSUB = "nsub";
	private static final String ITER = "iter";
	//	private static final String BASIS = "basis";
	private static final String ECHO = "echo";
	private static final String INPUT = "input";
	private static final String ECHO_INPUT = "echoInput";
	private static final String ATOM_BASIS = "atomBasis";
	private static final Pattern ATOM_BASIS_PATTERN = Pattern.compile("\\s*([A-za-z][a-z]?)\\s*\\((.*)\\).*");
	private static final String SUMMARY_OF_BASIS1 = "Summary of \".* basis\"";
	private static final String ROHF = "ROHF";
	private static final String DFT = "DFT";
	private static final String START = "start";
	private static final String SYMMETRY = "symmetry";
	private static final String END = "end";
	private static final String PROPERTY = "property";

	private static final Pattern SUMMARY_OF_BASIS1PATTERN = Pattern.compile(SUMMARY_OF_BASIS1);
	private static final Pattern NAME_COLON_VALUE = Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*(.*)");
	private static final Pattern NAME_SPACE_NUMBER = Pattern.compile("\\s*(.*)\\s\\s+([0-9\\.DEde\\-\\+]+).*");
	private static final Pattern NAME_EQUALS_VALUE = Pattern.compile("\\s*([^=]*)\\s*=\\s*(.*)");
	
	public final static List<String> blockStringList;
	static {
		
		blockStringList = new ArrayList<String>();
		blockStringList.add(_3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION);
		blockStringList.add(ACKNOWLEDGMENT);
		blockStringList.add(ARGUMENT);
		blockStringList.add(ATOMIC_MASS);
		blockStringList.add(AUTHORS);
		blockStringList.add(AUTO_Z);
		blockStringList.add(BASIS);
		blockStringList.add(CASE);
		blockStringList.add(CENTER_1); // a mess
		blockStringList.add(CENTER_OF_MASS); 
		blockStringList.add(CENTER_ONE); // another mess
		blockStringList.add(CITATION);
		blockStringList.add(CONVERGENCE_INFORMATION);
		blockStringList.add(FINAL_EIGENVALUES);
		blockStringList.add(FINAL_MOLECULAR_ORBITAL_ANALYSIS);
		blockStringList.add(FINAL_RHF_RESULTS);
		blockStringList.add(DIRECTORY_INFORMATION);
		blockStringList.add(GA_STATISTICS_FOR_PROCESS);
		blockStringList.add(GENERAL_INFORMATION);
		blockStringList.add(GEOMETRY);
		blockStringList.add(GRID_INFORMATION);
		blockStringList.add(INTERNUCLEAR_ANGLES);
		blockStringList.add(INTERNUCLEAR_DISTANCES);
		blockStringList.add(JOB_INFORMATION);
		blockStringList.add(MEMORY_INFORMATION);
		blockStringList.add(MOMENTS_OF_INERTIA);
		blockStringList.add(MULTIPOLE_ANALYSIS_OF_THE_DENSITY);
		blockStringList.add(MULLIKEN_ANALYSIS_OF_THE_TOTAL_DENSITY);
		blockStringList.add(NON_VARIATIONAL_INITIAL_ENERGY);
		blockStringList.add(NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE);
		blockStringList.add(NUCLEAR_DIPOLE_MOMENT);
		blockStringList.add(NW_CHEM_CPHF_MODULE);
		blockStringList.add(NW_CHEM_DFT_MODULE);
		blockStringList.add(NW_CHEM_INPUT_MODULE);
		blockStringList.add(NW_CHEM_PROPERTY_MODULE);
		blockStringList.add(NW_CHEM_SCF_MODULE);
		blockStringList.add(SCREENING_TOLERANCE_INFORMATION);
		blockStringList.add(SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS);
		blockStringList.add(SUMMARY_OF_BASIS);
		blockStringList.add(SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS);
		blockStringList.add(SYMMETRY_INFORMATION);
		blockStringList.add(SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS);
		blockStringList.add(XC_INFORMATION);
		blockStringList.add(XYZ_FORMAT_GEOMETRY);
		blockStringList.add(Z_MATRIX);
		
	}
	private CMLMolecule inputMolecule;
	
	public NWChemLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		jumboReader = new JumboReader(this.getDictionary(), abstractCommon.getPrefix(), lines);
		String blockName = getBlockName();
		if (blockName == null) {
			throw new RuntimeException("null block name");
		} else if (UNKNOWN_BLOCK.equals(blockName)){
			LOG.warn("Unknown block");
		}
		Template blockTemplate = legacyProcessor.getTemplateByBlockName(blockName.trim());
		if (blockTemplate != null) {
			blockTemplate.markupBlock(this);
			blockName = blockTemplate.getId();
			LOG.info("BLOCK: "+blockName);
//		} else if (_3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION.equals(blockName)) {
//			make3Center();
//		} else if (ACKNOWLEDGMENT.equals(blockName)) {
//			skipBlock();
//		} else if (ARGUMENT.equals(blockName)) {
//			makeArgument();
//		} else if (ATOMIC_MASS.equals(blockName)) {
//			makeAtomicMass();
//		} else if (AUTHORS.equals(blockName)) {
//			skipBlock();
//		} else if (AUTO_Z.equals(blockName)) {
//			makeAutoZ();
//		} else if (blockName.startsWith(BASIS)) {
//			makeBasis();
//		} else if (CASE.equals(blockName)) {
//			summarizeBlock0();
//		} else if (CENTER_1.equals(blockName)) {
//			skipBlock();
//		} else if (CENTER_OF_MASS.equals(blockName)) {
//			makeCenterOfMass();
//		} else if (CENTER_ONE.equals(blockName)) {
//			skipBlock();
//		} else if (CITATION.equals(blockName)) {
//			skipBlock();
//		} else if (CONVERGENCE_INFORMATION.equals(blockName)) {
//			makeConvergence();
//		} else if (DIRECTORY_INFORMATION.equals(blockName)) {
//			makeDirectoryInformation();
//		} else if (FINAL_EIGENVALUES.equals(blockName)) {
//			makeFinalEigenvalues();
//		} else if (blockName.contains(FINAL_MOLECULAR_ORBITAL_ANALYSIS)) {
//			makeFinalMolecularOrbitalAnalysis();
//		} else if (blockName.contains(FINAL_RHF_RESULTS)) {
//			makeFinalRhfResults();
//		} else if (GA_STATISTICS_FOR_PROCESS.equals(blockName)) {
//			makeGaStatistics();
//		} else if (GEOMETRY.equals(blockName)) {
//			makeGeometry();
//		} else if (GENERAL_INFORMATION.equals(blockName)) {
//			makeGeneralInformation();
//		} else if (GRID_INFORMATION.equals(blockName)) {
//			makeGridInformation();
//		} else if (INTERNUCLEAR_DISTANCES.equals(blockName)) {
//			skipBlock();
//		} else if (INTERNUCLEAR_ANGLES.equals(blockName)) {
//			skipBlock();
//		} else if (JOB_INFORMATION.equals(blockName)) {
//			makeJobInformation();
//		} else if (MEMORY_INFORMATION.equals(blockName)) {
//			makeMemoryInformation();
//		} else if (MOMENTS_OF_INERTIA.equals(blockName)) {
//			makeMomentsOfInertia();
//		} else if (MULLIKEN_ANALYSIS_OF_THE_TOTAL_DENSITY.equals(blockName)) {
//			makeMullikenAnalysis();
//		} else if (MULTIPOLE_ANALYSIS_OF_THE_DENSITY.equals(blockName)) {
//			makeMultipoleAnalysis();
//		} else if (NON_VARIATIONAL_INITIAL_ENERGY.equals(blockName)) {
//			makeNonVariational();
//		} else if (NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE.equals(blockName)) {
//			makeNWCOMP();
//		} else if (NUCLEAR_DIPOLE_MOMENT.equals(blockName)) {
//			makeNuclearDipole();
//		} else if (NW_CHEM_CPHF_MODULE.equals(blockName)) {
//			makeNWChemCphfModule();
//		} else if (NW_CHEM_DFT_MODULE.equals(blockName)) {
//			makeNWChemDftModule();
//		} else if (NW_CHEM_INPUT_MODULE.equals(blockName)) {
//			makeNWChemInputModule();
//		} else if (NW_CHEM_PROPERTY_MODULE.equals(blockName)) {
//			makeNWChemPropertyModule();
//		} else if (NW_CHEM_SCF_MODULE.equals(blockName)) {
//			makeNWChemScfModule();
//		} else if (SCREENING_TOLERANCE_INFORMATION.equals(blockName)) {
//			makeScreeningTolerance();
//		} else if (SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS.equals(blockName)) {
//			makeGlobalArrays();
//		} else if (SUMMARY_OF_BASIS.equals(blockName)) {
//			makeBasisSummary();
//		} else if (SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS.equals(blockName)) {
//			makeSuperposition();
//		} else if (SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS.equals(blockName)) {
//			makeSymmetryAnalysis();
//		} else if (SYMMETRY_INFORMATION.equals(blockName)) {
//			makeSymmetryInfo();
//		} else if (XC_INFORMATION.equals(blockName)) {
//			makeXCInformation();
//		} else if (XYZ_FORMAT_GEOMETRY.equals(blockName)) {
//			makeXYZ();
//		} else if (Z_MATRIX.equals(blockName)) {
//			makeZMatrix();
		} else if (UNKNOWN_BLOCK.equals(blockName)) {
			CMLModule module = this.makeModule();
			Template.tidyUnusedLines(jumboReader, module);
		} else {
			System.err.println("Unknown blockname: "+blockName);
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			((HasDictRef)element).setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), blockName));
		} else {
			System.err.println("null element: "+blockName);
		}
	}

//	private void makeNWChemCphfModule() {
//		CMLModule module = makeModule();
//		jumboReader.readLines(2);
//		jumboReader.readEmptyLines();
///*
//                                NWChem CPHF Module
//                                ------------------
// 
// 
//  scftype          =     RHF 
//  nclosed          =        9
//  nopen            =        0
//  variables        =      234
//  # of vectors     =        3
//  tolerance        = 0.10D-03
//  level shift      = 0.00D+00
//  max iterations   =       50
//  max subspace     =       30
//
//*/
//		jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
//
///*		
// #quartets = 3.714D+04 #integrals = 1.406D+05 #direct =  0.0% #cached =100.0%
//
//
// Integral file          = ./ch3f_trans.aoints.0
// Record size in doubles =  65536        No. of integs per rec  =  43688
// Max. records in memory =      3        Max. records in file   =   5708
// No. of bits per label  =      8        No. of bits per value  =     64
//
//
//File balance: exchanges=     0  moved=     0  time=   0.0
//
//*/
//		jumboReader.readLinesWhile(Pattern.compile(
//				"Iterative solution of linear equations"), false);
///*
//
//Iterative solution of linear equations
//  No. of variables      234
//  No. of equations        3
//  Maximum subspace       30
//        Iterations       50
//       Convergence  1.0D-04
//        Start time      4.5
//
//*/
//		jumboReader.makeNameValues(NAME_SPACE_NUMBER, ADD);
//		
///*
//
//   iter   nsub   residual    time
//   ----  ------  --------  ---------
//     1      3    8.41D-01       5.4
//     2      6    2.77D-02       6.3
//     3      9    7.44D-04       7.2
//     4     12    2.16D-05       8.0
//
//*/
//		jumboReader.readLinesWhile(Pattern.compile("\\s*iter   nsub   residual    time"), false);
//		jumboReader.readLines(1);
//		jumboReader.readTableColumnsAsArrayList(
//				"(I6,I7,E12.2,F10.1)", -1, 
//				new String[]{I_+ITER,I_+NSUB,F_+RESIDUAL,F_+TIME}, ADD);
//
///*
// Parallel integral file used       4 records with       0 large values
//*/
//
///*
//      Atom:    1  C 
//        Diamagnetic
//    257.9652     -0.0001     -0.0430
//      0.0000    257.9400      0.0000
//     -3.6400    -10.9048    254.4877
//
//        Paramagnetic
//   -171.5341      0.0001      0.0262
//     -0.0001   -171.5059      0.0008
//      3.6241     10.9053    -66.6073
//
//        Total Shielding Tensor
//     86.4312      0.0000     -0.0168
//     -0.0001     86.4341      0.0008
//     -0.0159      0.0005    187.8804
//
//           isotropic =     120.2486
//          anisotropy =     101.4478
//
//          Principal Components and Axis System
//                 1           2           3
//              187.8804     86.4341     86.4312
//
//      1        -0.0002     -0.0142      0.9999
//      2         0.0000      0.9999      0.0142
//      3         1.0000      0.0000      0.0002
//
//*/
//		while (jumboReader.hasMoreLines()) {
//			try {
//				addMagneticTensors(module);
//			} catch (Exception e) {
//				break;
//			}
//		}
//
//	}

//	private void addMagneticTensors(CMLModule module) {
//		jumboReader.readLinesWhile(Pattern.compile("      Atom:\\s*\\d+.*"), false);
//		jumboReader.increment(-1);
//		CMLModule atomModule = new CMLModule();
//		module.appendChild(atomModule);
//		jumboReader.setParentElement(atomModule);
//		jumboReader.parseScalars("(11X,I5,A4)", new String[]{I_+SERIAL, A_+ELSYM}, ADD);
//		readAndAddMatrix(DIAMAGNETIC);
//		readAndAddMatrix(PARAMAGNETIC);
//		readAndAddMatrix(TOTAL_SHIELDING_TENSOR);
//		jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
//		jumboReader.readLines(2);
//		jumboReader.readArray("(10X,3F12.4)", CMLConstants.XSD_DOUBLE, MAGNETIC, ADD);
//		jumboReader.readLines(1);
//		jumboReader.readTableColumnsAsArrayList("(I7,3X,F12.4,F12.4,F12.4)", 3, 
//				new String[]{I_+SERIAL, F_+MAG_A, F_+MAG_B, F_+MAG_C}, ADD);
//	}

//	private void readAndAddMatrix(String dictRef) {
//		jumboReader.readLines(1);
//		CMLMatrix matrix = jumboReader.parseMatrix(3, 3, "(F12.4,F12.4,F12.4)", 
//				CMLConstants.XSD_DOUBLE, dictRef, ADD);
//		jumboReader.readLines(1);
//	}

//	private void makeArgument() {
///*
// argument  1 = dft_feco5.nw
//
//
//
//============================== echo of input deck ==============================
//echo
//
//start dft_feco5
//
//# test of DFT with Fe(CO)5 using cd basis and xc basis
//
//...
//dft
//  xc b3lyp
//end
//title "case t23 --- (b3lyp) DFT (energy)"
//task dft energy
//
//
//
//================================================================================
// */
//		CMLModule module = makeModule();
//		System.err.println("..."+jumboReader.peekLine());
//		jumboReader.makeNameValues(Pattern.compile(".*(argument *\\d+)\\s*=\\s*(.*)"), ADD);
//		List<String> lines = jumboReader.readLinesWhile(Pattern.compile(".*=== echo of input deck ==.*"), false);
//		jumboReader.readLine();
//		NWChemLogBlock inputBlock = new NWChemLogBlock(blockContainer);
//		inputBlock.setBlockName(ECHO_INPUT);
//		blockContainer.getBlockList().add(0, inputBlock);
//		lines = jumboReader.readLinesWhile(Pattern.compile(".*===========.*"), false);
//		for (String line : lines) {
//			inputBlock.add(line);
//		}
//		inputBlock.makeInput();
//	}
	
//	private void makeInput() {
//		jumboReader = new JumboReader(this.getDictionary(), abstractCommon.getPrefix(), lines);
//		CMLModule module = makeModule();
//		module.setRole(INPUT);
//		CMLElement parent = module;
//		boolean inGeometry = false;
//		boolean inBasis = false;
//		inputMolecule = null;
//		for (String line : lines) {
//			// remove comments
//			line = line.split(CMLConstants.S_HASH)[0];
//			line = line.trim();
//			String token = line.equals("") ? "" : line.split(CMLConstants.S_WHITEREGEX)[0];
//			if ("".equals(token)) {
//				// 
//			} else if (TITLE.equalsIgnoreCase(token)) {
//				module.setTitle(line);
//			} else if (GEOMETRY.equalsIgnoreCase(token)) {
//				inputMolecule = new CMLMolecule();
//				parent.appendChild(inputMolecule);
//				parent = inputMolecule;
//				addScalar(inputMolecule, line, token);
//				inGeometry = true;
//			} else if (inGeometry && SYMMETRY.equalsIgnoreCase(token)) {
//				addScalar(parent, line, token);
//			} else if (BASIS.equalsIgnoreCase(token)) {
//				parent = makeSubModule(parent, token, line);
//				inBasis = true;
//			} else if (DFT.equalsIgnoreCase(token)) {
//				parent = makeSubModule(parent, token, line);
//			} else if (PROPERTY.equalsIgnoreCase(token)) {
//				parent = makeSubModule(parent, token, line);
////			} else if (ECHO.equalsIgnoreCase(token)) {
////				addScalar(parent, line, token);
////			} else if (START.equalsIgnoreCase(token)) {
////				addScalar(parent, line, token);
//			} else if (END.equalsIgnoreCase(token)) {
//				parent = (CMLModule) parent.getParent();
//				inGeometry = false;
//				inBasis = true;
//			} else if (inGeometry && line.split(CMLConstants.S_WHITEREGEX).length == 4){
//				addAtom(line);
//			} else {
//				addScalar(parent, line, token);
//				System.out.println("Cannot process: "+line);
////				throw new RuntimeException("Cannot process token: "+token);
//			}
//		}
//	}

	private void addAtom(String line) {
		String[] tokens = line.split(CMLConstants.S_WHITEREGEX);
		CMLAtom atom = new CMLAtom();
		atom.setElementType(Util.capitalise(tokens[0]));
		atom.setId("a"+(inputMolecule.getAtomCount()+1));
		inputMolecule.addAtom(atom);
		atom.setX3(new Double(tokens[1]));
		atom.setY3(new Double(tokens[2]));
		atom.setZ3(new Double(tokens[3]));
	}

	private void makeNWCOMP() {
/*
              Northwest Computational Chemistry Package (NWChem) 6.0
              ------------------------------------------------------


                    Environmental Molecular Sciences Laboratory
                       Pacific Northwest National Laboratory
                                Richland, WA 99352

                              Copyright (c) 1994-2010
                       Pacific Northwest National Laboratory
                            Battelle Memorial Institute

             NWChem is an open-source computational chemistry package
                        distributed under the terms of the
                      Educational Community License (ECL) 2.0
             A copy of the license is included with this distribution
                              in the LICENSE.TXT file

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
	}

	private void makeNWChemScfModule() {
/*
                                 NWChem SCF Module
                                 -----------------


              Methylamine...rhf/3-21g//Pople-Gordon standard geometry



  ao basis        = "ao basis"
  functions       =    28
  atoms           =     7
  closed shells   =     9
  open shells     =     0
  charge          =   0.00
  wavefunction    = RHF 
  input vectors   = atomic
  output vectors  = ./methylamine.movecs
  use symmetry    = F
  symmetry adapt  = F
 */
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readLineAsScalar(TITLE, ADD);
		jumboReader.readLines(3);
		jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
	}
	
	private void makeNWChemDftModule() {
/*
                                 NWChem DFT Module
                                 -----------------


          case t21 --- Default DFT (energy) with Becke '88 and Perdew '91


  Caching 1-el integrals 
  Rotation of axis 

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		jumboReader.readLineAsScalar(TITLE, ADD);
	}

	private void makeNWChemInputModule() {
/*
                                NWChem Input Module
                                -------------------


  perdew86 is a nonlocal functional; adding perdew81 local functional. 
 */
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readLineAsScalar(TITLE, ADD);
	}

	private void makeNWChemPropertyModule() {
/*
                              NWChem Property Module
                              ----------------------


              Methylamine...rhf/3-21g//Pople-Gordon standard geometry

 */
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readLineAsScalar(TITLE, ADD);
		
	}

	private void makeXCInformation() {
/*
              XC Information
              --------------
                        Slater Exchange Functional  1.000 local    
                      VWN V Correlation Functional  1.000 local    
 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		//TODO
	}

	private void makeGlobalArrays() {
/*
 Summary of allocated global arrays
-----------------------------------
  No active global arrays

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		//TODO
	}

	private void makeMemoryInformation() {
/*
           Memory information
           ------------------

    heap     =   13107201 doubles =    100.0 Mbytes
    stack    =   13107201 doubles =    100.0 Mbytes
    global   =   26214400 doubles =    200.0 Mbytes (distinct from heap & stack)
    total    =   52428802 doubles =    400.0 Mbytes
    verify   = yes
    hardfail = no 


 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
	}

	private void makeJobInformation() {
/*
           Job information
           ---------------

    hostname      = arcen
    program       = /home/d3y133/nwchem-releases/nwchem-6.0-gfortran/bin/LINUX64/nwchem
    date          = Fri Aug  6 13:47:30 2010

    compiled      = Fri_Aug_06_13:42:42_2010
    source        = /home/d3y133/nwchem-releases/nwchem-6.0-gfortran
    nwchem branch = 6.0
    input         = dft_feco5.nw
    prefix        = dft_feco5.
    data base     = ./dft_feco5.db
    status        = startup
    nproc         =        4
    time left     =     -1s
 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
	}

	private void makeGeneralInformation() {
/*
            General Information
            -------------------
          SCF calculation type: DFT
          Wavefunction type:  closed shell.
          No. of atoms     :    11
          No. of electrons :    96
           Alpha electrons :    48
            Beta electrons :    48
          Charge           :     0
          Spin multiplicity:     1
          Use of symmetry is: on ; symmetry adaption is: on 
          Maximum number of iterations:  30
          AO basis - number of functions:   176
                     number of shells:    70
          A Charge density fitting basis will be used.
          CD basis - number of functions:   395
                     number of shells:   150
          Convergence on energy requested: 1.00D-06
          Convergence on density requested: 1.00D-05
          Convergence on gradient requested: 5.00D-04
 */
		makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
	}

	private void makeGaStatistics() {
/*
                         GA Statistics for process    0
                         ------------------------------

       create   destroy   get      put      acc     scatter   gather  read&inc
calls: 1590     1590     1.88e+05 1.35e+05 8.20e+04    0        0     2.96e+04 
number of processes/call 1.17e+00 1.06e+00 1.01e+00 0.00e+00 0.00e+00
bytes total:             6.13e+08 2.92e+08 1.44e+08 0.00e+00 0.00e+00 2.37e+05
bytes remote:            3.92e+08 1.79e+08 1.03e+08 0.00e+00 0.00e+00 0.00e+00
Max memory consumed for GA by this process: 2399456 bytes
MA_summarize_allocated_blocks: starting scan ...
MA_summarize_allocated_blocks: scan completed: 0 heap blocks, 0 stack blocks
MA usage statistics:

	allocation statistics:
					      heap	     stack
					      ----	     -----
	current number of blocks	         0	         0
	maximum number of blocks	        36	        63
	current total bytes		         0	         0
	maximum total bytes		  55925040	  36479104
	maximum total K-bytes		     55926	     36480
	maximum total M-bytes		        56	        37

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		//TODO
	}

	private void makeDirectoryInformation() {
/*
           Directory information
           ---------------------

  0 permanent = .
  0 scratch   = .

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		//TODO
	}

	private void makeGridInformation() {
/*
             Grid Information
             ----------------
          Grid used for XC integration:  medium    
          Radial quadrature: Mura-Knowles        
          Angular quadrature: Lebedev. 
          Tag              B.-S. Rad. Rad. Pts. Rad. Cut. Ang. Pts.
          ---              ---------- --------- --------- ---------
          fe                  1.40      112           8.0       590
          c                   0.70       49          11.0       434
          o                   0.60       49          13.0       434
          Grid pruning is: on 
          Number of quadrature shells:   308
          Spatial weights used:  Erf1

 */
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
		jumboReader.readLines(1);
		jumboReader.readTableColumnsAsArrayList("(10X,A2,15X,F8.2,I9,6X,F8.1,I10)", -1,
				new String[]{A_+TAG, F_+BSRAD, I_+RADPTS, F_+RADCUT, I_+ANGPTS}, ADD);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
	}

	private void makeSymmetryInfo() {
/*
      Symmetry information
      --------------------

 Group name             D3h       
 Group number             27
 Group order              12
 No. of unique centers     5

      Symmetry unique atoms

     1    2    4    6    9

 */
		CMLModule module = makeModule();
		jumboReader.readLines(3);
		jumboReader.parseScalars("(24X, A)", new String[]{A_+GROUP_NAME}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+GROUP_NUMBER}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+GROUP_ORDER}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+GROUP_UNIQUECENTER}, ADD);
		jumboReader.readLines(3);
		// doesn't yet work
		CMLArray array = jumboReader.readArrayGreedily("(1X, 10I4)", CMLConstants.XSD_INTEGER);
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), SYMMETRY_UNIQUE));
		module.appendChild(array);
	}

	private void makeNuclearDipole() {
/**
            Nuclear Dipole moment (a.u.) 
            ----------------------------
        X                 Y               Z
 ---------------- ---------------- ----------------
     0.0000000000     0.0000000000     0.0000000000

 */
		CMLModule module = makeModule();
		jumboReader.readLines(4);
		jumboReader.parseScalars("(F17.10,F17.10,F17.10)", new String[]{F_+X, F_+Y, F_+Z}, ADD);
	}

	private void makeGeometry() {
/*
                             Geometry "geometry" -> ""
                             -------------------------

 Output coordinates in a.u. (scale by  1.000000000 to convert to a.u.)

  No.       Tag          Charge          X              Y              Z
 ---- ---------------- ---------- -------------- -------------- --------------
    1 fe                  26.0000     0.00000000     0.00000000     0.00000000
    2 c                    6.0000     0.00000000     0.00000000     3.41435800
...
   11 o                    8.0000     1.45716734    -5.43822254     0.00000000

 */
		CMLModule module = makeModule();
		jumboReader.readLines(7);
		jumboReader.readTableColumnsAsArrayList("(5X,A17,F11.4,F15.8,F15.8,F15.8)", -1, 
				new String[]{A_+TAG, F_+CHARGE, F_+X, F_+Y, F_+Z}, ADD);
	}

	private void makeSymmetryAnalysis() {
/**
      Symmetry analysis of molecular orbitals - initial
      -------------------------------------------------

  Numbering of irreducible representations: 

     1 a1'         2 a1"         3 a2'         4 a2"         5 e'      
     6 e"      

  Orbital symmetries:

     1 a1'         2 a1'         3 a2"         4 e'          5 e'      
     6 a1'         7 a2"         8 a1'         9 e'         10 e'      
...
    51 e"         52 e'         53 e'         54 a2"        55 a2'     
    56 e'         57 e'         58 a1'     

 */
		CMLModule module = makeModule();
		jumboReader.readLines(5);
		CMLArray array = jumboReader.readArrayGreedily("5(6X,A8)", CMLConstants.XSD_STRING);
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), IRRREP));
		module.appendChild(array);
		jumboReader.readLines(5);
		array = jumboReader.readArrayGreedily("5(6X,A8)", CMLConstants.XSD_STRING);
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), ORBSYM));
		module.appendChild(array);
	}

	private void makeScreeningTolerance() {
/**		
      Screening Tolerance Information
      -------------------------------
          Density screening/tol_rho: 1.00D-10
          AO Gaussian exp screening on grid/accAOfunc:  14
          CD Gaussian exp screening on grid/accCDfunc:  20
          XC Gaussian exp screening on grid/accXCfunc:  20
          Schwarz screening/accCoul: 1.00D-08
*/	          
		makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
	}

	private void makeConvergence() {
/*
  Convergence Information
  -----------------------
  Convergence aids based upon iterative change in 
  total energy or number of iterations. 
  Levelshifting, if invoked, occurs when the 
  HOMO/LUMO gap drops below (HL_TOL): 1.00D-02
  DIIS, if invoked, will attempt to extrapolate 
  using up to (NFOCK): 10 stored Fock matrices.

            Damping( 0%)  Levelshifting(0.5)       DIIS
          --------------- ------------------- ---------------
  dE  on:    start            ASAP                start   
  dE off:    2 iters         30 iters            30 iters 


 */
		makeModule();
		jumboReader.readLines(11);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
	}

	private void makeMomentsOfInertia() {
/*
 moments of inertia (a.u.)
 ------------------
        2255.012792094188           0.000000000000           0.000000000000
           0.000000000000        2255.012792094188           0.000000000000
           0.000000000000           0.000000000000        1950.260605202691
 */
		makeModule();
		jumboReader.readLines(2);
		jumboReader.parseMatrix(3, 3, "(3(F25.12))", CMLConstants.XSD_DOUBLE, MOMINT, ADD);
	}

	private void makeCenterOfMass() {
/**
 center of mass
 --------------
 x =   0.00000000 y =   0.00000000 z =   0.00000000

 */
		makeModule();
		jumboReader.readLines(2);
		jumboReader.parseScalars("(3(' x =',F13.7))", new String[]{F_+X,F_+Y,F_+Z}, ADD);
		
	}

	private void makeMullikenAnalysis() {
/**
  Mulliken analysis of the total density
  --------------------------------------

    Atom       Charge   Shell Charges
 -----------   ------   -------------------------------------------------------
    1 C    6     6.39   1.99  0.36  1.59  1.06  1.39
    2 N    7     7.77   1.99  0.36  1.82  1.35  2.26
    3 H    1     0.83   0.48  0.35
...
    7 H    1     0.71   0.46  0.25
 */
		makeModule();
		jumboReader.readLines(5);
		String format1 = "(I5,A3,I4,3X,F6.2)";
		String format2 = "(22X,10F6.2)";
		String[] names1 = {I_+SERIAL, A_+ATSYM, I_+ATNUM, F_+CHARGE};
		String name2 = SHELLCHARGE;
		while(jumboReader.hasMoreLines()) {
			if (jumboReader.peekLine().trim().length() == 0) {
				break;
			}
			try {
				jumboReader.parseScalars(format1, names1, ADD);
				jumboReader.increment(-1);
				jumboReader.readArray(format2, CMLConstants.XSD_DOUBLE, name2, ADD);
			} catch (Exception e) {
				break;
			}
		}
		
	}

	private void makeMultipoleAnalysis() {
/**
     Multipole analysis of the density
     ---------------------------------

     L   x y z        total         alpha         beta         nuclear
     -   - - -        -----         -----         ----         -------
     0   0 0 0      0.000000    -48.000000    -48.000000     96.000000

     1   1 0 0      0.000000      0.000000      0.000000      0.000000
     1   0 1 0      0.000000      0.000000      0.000000      0.000000
 */
		makeModule();
		jumboReader.readLines(5);
		String format = "(I6,2X,3I2,4F14.6)";
		String[] names = {I_+L, I_+X, I_+Y, I_+Z, F_+TOTAL, F_+ALPHA, F_+BETA, F_+NUCLEAR};
		while(jumboReader.hasMoreLines()) {
			if (jumboReader.peekLine().trim().length() == 0) {
				break;
			}
			try {
				jumboReader.readTableColumnsAsArrayList(format, -1, names, ADD);
				jumboReader.readLines(1);
			} catch (Exception e) {
				break;
			}
		}
		
	}

	private void makeFinalMolecularOrbitalAnalysis() {
/**
                       DFT Final Molecular Orbital Analysis
                       ------------------------------------

 Vector   19  Occ=2.000000D+00  E=-2.132706D+00  Symmetry=e'
              MO Center=  2.3D-03, -4.3D-04,  3.4D-18, r^2= 2.5D-01
   Bfn.  Coefficient  Atom+Function         Bfn.  Coefficient  Atom+Function  
  ----- ------------  ---------------      ----- ------------  ---------------
    10      1.026049   1 Fe py                7     -0.387574   1 Fe py        
     9      0.317409   1 Fe px        
     
OR
                       ROHF Final Molecular Orbital Analysis
                       -------------------------------------

 Vector    3  Occ=2.000000D+00  E=-1.163995D+00
              MO Center= -9.1D-02, -4.6D-01,  8.3D-16, r^2= 9.2D-01
   Bfn.  Coefficient  Atom+Function         Bfn.  Coefficient  Atom+Function  
  ----- ------------  ---------------      ----- ------------  ---------------
    15      0.597567  2 N  s                  6      0.208631  1 C  s          
    10     -0.198832  2 N  s                 11      0.176693  2 N  s          



 */
		
		makeModule();
		String line = jumboReader.readLine();
		String type = line.trim().split(CMLConstants.S_WHITEREGEX)[0];
		jumboReader.readLines(2);
		while (jumboReader.hasMoreLines()) {
			readVector(type);
			jumboReader.readEmptyLines();
		}
	}

	private void makeFinalRhfResults() {
/**
       Final RHF  results 
       ------------------ 

         Total SCF energy =    -94.679444926652
      One-electron energy =   -210.716788531248
      Two-electron energy =     73.987179567659
 Nuclear repulsion energy =     42.050164036937

        Time for solution =      0.1s


 */
				
		makeModule();
		jumboReader.readLines(3);
		while (jumboReader.hasMoreLines()) {
			jumboReader.makeNameValues(NAME_EQUALS_VALUE, ADD);
		}
	}
	
//              Final eigenvalues
//    -----------------
	private void makeFinalEigenvalues() {
/**
             Final eigenvalues
             -----------------

              1      
    1  -15.4458
    2  -11.1826
..
   18    0.9873
   19    1.2592

 */
				
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readTableColumnsAsArrayList("(I5,F10.4)", -1, new String[]{I_+SERIAL, F_+EIGENVAL}, ADD);
	}
			

	private void readVector(String type) {
		if (ROHF.equalsIgnoreCase(type)) {
			jumboReader.parseScalars("(' Vector',I5,'  Occ=',E12.6,'  E=',E12.6)",
				new String[]{I_+VECTOR, F_+OCC, F_+E}, ADD);
		} else if (DFT.equalsIgnoreCase(type)) {
			jumboReader.parseScalars("(' Vector',I5,'  Occ=',E12.6,'  E=',E12.6,'  Symmetry=',A)",
				new String[]{I_+VECTOR, F_+OCC, F_+E, A_+SYMMETRY}, ADD);
		} else {
			throw new RuntimeException("Unknown Final Molecular Orbital type: "+type);
		}
		jumboReader.parseScalars("('              MO Center=',3(E9.1,','),' r^2=',E8.1)",
				new String[]{F_+V1, F_+V2, F_+V3, F_+RSQUARED}, ADD);
		jumboReader.readLines(2);
		String format = "I6,F14.6,I4,A3,A14";
		String[] names2 = new String[]{I_+BFN, F_+COEFF, I_+ATNUM, A_+ATSYM, A_+ORB, I_+BFN, F_+COEFF, I_+ATNUM, A_+ATSYM, A_+ORB};
		String[] names = new String[]{I_+BFN, F_+COEFF, I_+ATNUM, A_+ATSYM, A_+ORB};
		
		try {
			jumboReader.readTableColumnsAsArrayList("(2("+format+"))", -1, names2, ADD);
		} catch (Exception e) {
			// maybe only one line
		}
		// straggling line at end?
		if (jumboReader.hasMoreLines() && jumboReader.peekLine().trim().length() > 0) {
			jumboReader.readTableColumnsAsArrayList("("+format+")", -1, names, ADD);
		}
	}

	private void make3Center() {
		/**
      3 Center 2 Electron Integral Information
      ----------------------------------------
          Maximum number of 3-center 2e- integrals is:        12235520.
            This is reduced with Schwarz screening to:         5362125.
            Incore requires a per proc buffer size of:         1799667.
                  The minimum integral buffer size is:          113760
          Minimum dble words available (all nodes) is:        26199921
                   This is reduced (for later use) to:        25816300
                             Suggested buffer size is:         1799667

           1.800 MW buffer allocated for incore 3-center 
          2e- integral storage on stack. 
          The percent of 3c 2e- integrals held in-core is: 100.00

   Time prior to 1st pass:      1.5

 Grid_pts file          = ./dft_feco5.gridpts.0
 Record size in doubles =  12289        No. of grid_pts per rec  =   3070
 Max. records in memory =     18        Max. recs in file   =    140416


           Memory utilization after 1st SCF pass: 
           Heap Space remaining (MW):       12.87            12872215
          Stack Space remaining (MW):       11.31            11305115

   convergence    iter        energy       DeltaE   RMS-Dens  Diis-err    time
 ---------------- ----- ----------------- --------- --------- ---------  ------
 d= 0,ls=0.0,diis     1  -1822.8247493675 -2.62D+03  6.20D-02  9.95D+00     1.9
 d= 0,ls=0.0,diis     2  -1811.2270745263  1.16D+01  7.18D-02  6.78D+01     2.3
 ...
 d= 0,ls=0.0,diis    18  -1823.6801959292 -3.28D-06  8.81D-06  3.23D-06     8.0
 d= 0,ls=0.0,diis    19  -1823.6801961701 -2.41D-07  9.79D-07  1.05D-07     8.4


         Total DFT energy =    -1823.680196170071
      One electron energy =    -4105.874590431184
           Coulomb energy =     1601.844762163483
    Exchange-Corr. energy =     -118.145985996560
 Nuclear repulsion energy =      798.495618094190

 Numeric. integr. density =       95.999997053934

     Total iterative time =      6.9s


		 */
		makeModule();
		jumboReader.readLines(2);
		jumboReader.makeNameValues(NAME_COLON_VALUE, ADD);
		jumboReader.readLinesWhile(Pattern.compile(
				"   convergence    iter        energy       DeltaE   RMS-Dens  Diis\\-err    time"), false);
		jumboReader.readLines(2);
		// d= 0,ls=0.0,diis     1  -1822.8247493675 -2.62D+03  6.20D-02  9.95D+00     1.9

		String format = "(18X,I5,F18.10,E10.2,E10.2,E10.2,F8.1)";
		jumboReader.readTableColumnsAsArrayList(
				format, -1, new String[]{I_+ITER, F_+ENERGY, F_+DELTAE, F_+RMSDENS, F_+DIISERR, F_+TIME}, true);
		jumboReader.readLines(2);
		jumboReader.addDouble("('         Total DFT energy =',F22.12)", TOTALDFT);
		jumboReader.addDouble("('       One electron energy =',F22.12)", ONEELECENER);
		jumboReader.addDouble("('           Coulomb energy =',F22.12)", COULOMB);
		jumboReader.addDouble("('    Exchange-Corr. energy =',F22.12)", ECHANGECORR);
		jumboReader.addDouble("(' Nuclear repulsion energy =',F22.12)", NUCREPENER);
		jumboReader.readEmptyLines();
		jumboReader.addDouble("(' Numeric. integr. density =',F22.12)", NUMITERDENS);
		jumboReader.readEmptyLines();
		jumboReader.addDouble("('     Total iterative time =',F9.1,'s')", TOTITERTIME);
		
	}

	private void makeBasis() {
		/** this will read blocks according to the scheme:
		 * the blocks will have been created already. This routine assumes
		 * strict order and will finish after the summary
                      Basis "cd basis" -> "" (cartesian)
                      -----
  o (Oxygen)      // these are read separately 
  ----------
            Exponent  Coefficients 
       -------------- ---------------------------------------------------------
  1 S  2.00000000E+03  1.000000

  2 S  4.00000000E+02  1.000000
...  
 13 D  3.90000000E-01  1.000000

  c (Carbon)
  ----------
            Exponent  Coefficients 
       -------------- ---------------------------------------------------------
  1 S  1.11400000E+03  1.000000

  2 S  2.23000000E+02  1.000000
...
 13 D  2.20000000E-01  1.000000

  fe (Iron)
  ---------
            Exponent  Coefficients 
       -------------- ---------------------------------------------------------
  1 S  4.40000000E+04  1.000000

  2 S  8.80000000E+03  1.000000




 Summary of "cd basis" -> "" (cartesian)
 ------------------------------------------------------------------------------
       Tag                 Description            Shells   Functions and Types
 ---------------- ------------------------------  ------  ---------------------
 o                DGauss A1 DFT Coulomb Fitting     13       34   7s3p3d
 c                DGauss A1 DFT Coulomb Fitting     13       34   7s3p3d
 fe               DGauss A1 DFT Coulomb Fitting     20       55   10s5p5d

		 */
		makeModule();
//      Basis "cd basis" -> "" (cartesian)
		Pattern basisPattern = Pattern.compile("\\s*Basis \"([^\"]*)\" -> \"([^\"]*)\"\\s*(.*)");
		Matcher matcher = basisPattern.matcher(lines.get(0));
		if (!matcher.matches()) {
			throw new RuntimeException("cannot match basis line: "+lines.get(0));
		}
		String basis = matcher.group(1);
		jumboReader.addDictRef(new CMLScalar(basis), BASIS);
		jumboReader.readLines(1);
	}
	
	private CMLElement makeBasisSummary() {
		/**
 Summary of "cd basis" -> "" (cartesian)
 ------------------------------------------------------------------------------
       Tag                 Description            Shells   Functions and Types
 ---------------- ------------------------------  ------  ---------------------
 o                DGauss A1 DFT Coulomb Fitting     13       34   7s3p3d
 c                DGauss A1 DFT Coulomb Fitting     13       34   7s3p3d
 fe               DGauss A1 DFT Coulomb Fitting     20       55   10s5p5d
 
 o                     DZVP (DFT Orbital)            6       15   3s2p1d		 
 OR
  ------------------------------------------------------------------------------
       Tag                 Description            Shells   Functions and Types
 ---------------- ------------------------------  ------  ---------------------
 *                           6-311G                   on all atoms 

 */
		CMLModule module = makeModule();
		jumboReader.parseScalars(Pattern.compile("\\s*Summary of \"([^\"]*)\" \\-\\> \"([^\"]*)\"\\s*\\((.*)\\).*"), 
				new String[]{A_+BASIS, A_+BASIS1, A_+COORDTYPE},ADD);
		jumboReader.readLines(3);
		
		try {
			jumboReader.readTableColumnsAsArrayList("(A3,48X,I4,4X,I5,3X,A)", -1, 
					new String[]{A_+ELSYM, /*A_+"desc",*/ I_+SHELLS, I_+FUNCTIONS, A_+TYPES}, true);
		} catch (Exception e) {
			jumboReader.readTableColumnsAsArrayList("(A3,48X,A)", -1, 
					new String[]{A_+ELSYM, /*A_+"desc",*/ A_+FUNCTIONS}, true);
		}
		return element;
	}

	private void makeAtomBasisBlock() {
		CMLModule module = makeModule();
		module.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), ATOM_BASIS));
		jumboReader.parseScalars(ATOM_BASIS_PATTERN, new String[]{A_+ELSYM, A_+ELEM_NAME}, true);
/**
  c (Carbon)
  ----------
            Exponent  Coefficients 
       -------------- ---------------------------------------------------------
  1 S  1.11400000E+03  1.000000

  2 S  2.23000000E+02  1.000000
...
 13 D  2.20000000E-01  1.000000
 */
		jumboReader.readLines(3);
		while (jumboReader.hasMoreLines()) {
			addShell(module);
		}
	}

	private void addShell(CMLModule module) {
		CMLArrayList arrayList = new CMLArrayList();
		CMLArray shellArray = createAndAddArray(
				arrayList, CMLConstants.XSD_INTEGER, abstractCommon.getPrefix(), SHELL);
		CMLArray spdArray = createAndAddArray(
				arrayList, CMLConstants.XSD_STRING, abstractCommon.getPrefix(), SPD);
		CMLArray expArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), EXP);
		CMLArray coeffArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), COEFF);
		String line = null;
		while (true) {
			try {
				line = jumboReader.readLine();
//				"  1 S  6.67000000E+02  1.000000"
				List<Object> objects = JumboReader.parseFortranLine("(I3,A3,E15.7,F10.6)", line);
				shellArray.append((Integer)objects.get(0));
				spdArray.append(objects.get(1).toString());
				expArray.append((Double)objects.get(2));
				coeffArray.append((Double)objects.get(3));
			} catch (Exception e) {
//				System.err.println("Failed:"+line+":"+e);
				break;
			}
		}
		module.appendChild(arrayList);
	}

	private void makeAutoZ() {
		/**
          ------
          auto-z
          ------
  Looking for out-of-plane bends


		 */
		makeModule();
	}

	private void makeAtomicMass() {
		/**
      Atomic Mass 
      ----------- 

      fe                55.934900
      c                 12.000000
      o                 15.994910
		 */
		makeModule();
		jumboReader.readLines(3);
		System.out.println("CURR>>"+jumboReader.peekLine());
		jumboReader.readTableColumnsAsArrayList("(6X,A2,10X,F15.6)", -1, 
				new String[]{A_+ATOMSYM, F_+ATOMMASS}, JumboReader.ADD);
//		System.out.println("CURRX>>"+jumboReader.peekLine());
	}

	private void makeZMatrix() {
/**		
        Z-matrix (autoz)
        -------- 

Units are Angstrom for bonds and degrees for angles

Type          Name      I     J     K     L     M      Value
----------- --------  ----- ----- ----- ----- ----- ----------
 1 Stretch                  1     2                       1.80680
 2 Stretch                  1     3                       1.80680
...
10 Stretch                  8    11                       1.15200
11 Bend                     2     1     6                90.00000
12 Bend                     2     1     7                90.00000
...
37 Bend                    11     1     7               120.00000
38 Torsion                  1     2     6     9         180.00000
39 Torsion                  1     2     7    10         180.00000
...
55 Torsion                  1     8     7    10         180.00000
*/
		makeModule();
		jumboReader.readLines(7);
		System.out.println("ZMATRIX NYI");
	}

	private void makeNonVariational() {
/**
      Non-variational initial energy
      ------------------------------

 Total energy =   -1827.679833
 1-e energy   =   -4099.408592
 2-e energy   =    1473.233141
 HOMO         =      -0.328823
 LUMO         =      -0.090738
 */
		makeModule();
		jumboReader.readLines(3);
		jumboReader.addDouble("(' Total energy =',F15.6)", TOTENER);
		jumboReader.addDouble("(' 1-e energy   =',F15.6)", ENER1E);
		jumboReader.addDouble("(' 2-e energy   =',F15.6)", ENER2E);
		jumboReader.addDouble("(' HOMO         =',F15.6)", HOMO);
		jumboReader.addDouble("(' LUMO         =',F15.6)", LUMO);
	}

	private void makeSuperposition() {
		/*
      Superposition of Atomic Density Guess
      -------------------------------------

 Sum of atomic energies:       -1824.13130503
		 */
		makeModule();
		jumboReader.readLines(3);
		jumboReader.addDouble("(' Sum of atomic energies:      'F15.8)", SUM_ATOM_ENER);
	}

	private void makeXYZ() {
		/*
            XYZ format geometry
            -------------------
    11
 geometry
 fe                    0.00000000     0.00000000     0.00000000
 c                     0.00000000     0.00000000     1.80680057
 c                     0.00000000     0.00000000    -1.80680057
 o                     0.00000000     0.00000000     2.95880092
 o                     0.00000000     0.00000000    -2.95880092
 c                     1.29209669     1.29209669     0.00000000
 c                    -1.76503691     0.47294021     0.00000000
 c                     0.47294021    -1.76503691     0.00000000
 o                     2.10668384     2.10668384     0.00000000
 o                    -2.87778364     0.77109980     0.00000000
 o                     0.77109980    -2.87778364     0.00000000
		 */
		makeModule();
		jumboReader.readLines(2);
		CMLElement scalar = jumboReader.parseScalars(
			"(I6)", new String[]{I_+NATOMS}, JumboReader.DONT_ADD);
		int atomCount = ((CMLScalar)scalar).getInt();
		jumboReader.readLines(1);
		jumboReader.readMoleculeAsColumns(atomCount, "A3,15X,3F15.8", new int[]{0, -1, -1, 1, 2, 3}, ADD);
	}

}
