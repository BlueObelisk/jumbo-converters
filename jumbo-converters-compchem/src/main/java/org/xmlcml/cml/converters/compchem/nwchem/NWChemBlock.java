package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

public class NWChemBlock extends AbstractBlock {

	private static final String ECHO_INPUT = "echoInput";
	private static final String ATOM_BASIS = "atomBasis";
	private static final Pattern ATOM_BASIS_PATTERN = Pattern.compile("\\s*([A-za-z][a-z]?)\\s*\\((.*)\\).*");
	private static final Pattern SUMMARY_OF_BASIS = Pattern.compile("Summary of \".* basis\"");
	private static final String Z_MATRIX = "Z-matrix";
	private static final String NON_VARIATIONAL_INITIAL_ENERGY = "Non-variational initial energy";
	private static final String AUTO_Z = "auto-z";
	private static final String ROHF = "ROHF";
	private static final String DFT = "DFT";
	public NWChemBlock(BlockContainer blockContainer) {
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
			
		} else if (NWChemProcessor._3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION.equals(blockName)) {
			make3Center();
		} else if (NWChemProcessor.ACKNOWLEDGMENT.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.ARGUMENT.equals(blockName)) {
			makeArgument();
		} else if (NWChemProcessor.ATOMIC_MASS.equals(blockName)) {
			makeAtomicMass();
		} else if (NWChemProcessor.AUTHORS.equals(blockName)) {
			skipBlock();
		} else if (AUTO_Z.equals(blockName)) {
			makeAutoZ();
		} else if (blockName.startsWith(NWChemProcessor.BASIS)) {
			makeBasis();
		} else if (NWChemProcessor.CASE.equals(blockName)) {
			summarizeBlock0();
		} else if (NWChemProcessor.CENTER_1.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.CENTER_OF_MASS.equals(blockName)) {
			makeCenterOfMass();
		} else if (NWChemProcessor.CENTER_ONE.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.CITATION.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.CONVERGENCE_INFORMATION.equals(blockName)) {
			makeConvergence();
		} else if (NWChemProcessor.DIRECTORY_INFORMATION.equals(blockName)) {
			makeDirectoryInformation();
		} else if (NWChemProcessor.FINAL_EIGENVALUES.equals(blockName)) {
			makeFinalEigenvalues();
		} else if (blockName.contains(NWChemProcessor.FINAL_MOLECULAR_ORBITAL_ANALYSIS)) {
			makeFinalMolecularOrbitalAnalysis();
		} else if (blockName.contains(NWChemProcessor.FINAL_RHF_RESULTS)) {
			makeFinalRhfResults();
		} else if (NWChemProcessor.GA_STATISTICS_FOR_PROCESS.equals(blockName)) {
			makeGaStatistics();
		} else if (NWChemProcessor.GEOMETRY.equals(blockName)) {
			makeGeometry();
		} else if (NWChemProcessor.GENERAL_INFORMATION.equals(blockName)) {
			makeGeneralInformation();
		} else if (NWChemProcessor.GRID_INFORMATION.equals(blockName)) {
			makeGridInformation();
		} else if (NWChemProcessor.INTERNUCLEAR_DISTANCES.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.INTERNUCLEAR_ANGLES.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.JOB_INFORMATION.equals(blockName)) {
			makeJobInformation();
		} else if (NWChemProcessor.MEMORY_INFORMATION.equals(blockName)) {
			makeMemoryInformation();
		} else if (NWChemProcessor.MOMENTS_OF_INERTIA.equals(blockName)) {
			makeMomentsOfInertia();
		} else if (NWChemProcessor.MULLIKEN_ANALYSIS_OF_THE_TOTAL_DENSITY.equals(blockName)) {
			makeMullikenAnalysis();
		} else if (NWChemProcessor.MULTIPOLE_ANALYSIS_OF_THE_DENSITY.equals(blockName)) {
			makeMultipoleAnalysis();
		} else if (NON_VARIATIONAL_INITIAL_ENERGY.equals(blockName)) {
			makeNonVariational();
		} else if (NWChemProcessor.NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE.equals(blockName)) {
			makeNWCOMP();
		} else if (NWChemProcessor.NUCLEAR_DIPOLE_MOMENT.equals(blockName)) {
			makeNuclearDipole();
		} else if (NWChemProcessor.NW_CHEM_CPHF_MODULE.equals(blockName)) {
			makeNWChemCphfModule();
		} else if (NWChemProcessor.NW_CHEM_DFT_MODULE.equals(blockName)) {
			makeNWChemDftModule();
		} else if (NWChemProcessor.NW_CHEM_INPUT_MODULE.equals(blockName)) {
			makeNWChemInputModule();
		} else if (NWChemProcessor.NW_CHEM_PROPERTY_MODULE.equals(blockName)) {
			makeNWChemPropertyModule();
		} else if (NWChemProcessor.NW_CHEM_SCF_MODULE.equals(blockName)) {
			makeNWChemScfModule();
		} else if (NWChemProcessor.SCREENING_TOLERANCE_INFORMATION.equals(blockName)) {
			makeScreeningTolerance();
		} else if (NWChemProcessor.SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS.equals(blockName)) {
			makeGlobalArrays();
		} else if (SUMMARY_OF_BASIS.matcher(blockName).matches()) {
			makeBasisSummary();
		} else if (NWChemProcessor.SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS.equals(blockName)) {
			makeSuperposition();
		} else if (NWChemProcessor.SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS.equals(blockName)) {
			makeSymmetryAnalysis();
		} else if (NWChemProcessor.SYMMETRY_INFORMATION.equals(blockName)) {
			makeSymmetryInfo();
		} else if (NWChemProcessor.XC_INFORMATION.equals(blockName)) {
			makeXCInformation();
		} else if (NWChemProcessor.XYZ_FORMAT_GEOMETRY.equals(blockName)) {
			makeXYZ();
		} else if (Z_MATRIX.equals(blockName)) {
			makeZMatrix();
		} else if (UNKNOWN.equals(blockName)) {
			String line = lines.get(0);
			if (ATOM_BASIS_PATTERN.matcher(line).matches()) {
				makeAtomBasisBlock();
			} else {
				System.err.println("UNKNOWN>>"+line);
				summarizeBlock();
			}
		} else {
			System.err.println("Unknown blockname: "+blockName);
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), blockName);
			element.setAttribute("dictRef", dictRef);
		} else {
			System.err.println("null element: "+blockName);
		}
	}

	private void makeNWChemCphfModule() {
		CMLModule module = makeModule();
		jumboReader.readLines(2);
		jumboReader.readEmptyLines();
/*
                                NWChem CPHF Module
                                ------------------
 
 
  scftype          =     RHF 
  nclosed          =        9
  nopen            =        0
  variables        =      234
  # of vectors     =        3
  tolerance        = 0.10D-03
  level shift      = 0.00D+00
  max iterations   =       50
  max subspace     =       30

*/
		jumboReader.makeNameValues(Pattern.compile("\\s*([^=]*)\\s*=\\s*(.*)"), ADD);

/*		
 #quartets = 3.714D+04 #integrals = 1.406D+05 #direct =  0.0% #cached =100.0%


 Integral file          = ./ch3f_trans.aoints.0
 Record size in doubles =  65536        No. of integs per rec  =  43688
 Max. records in memory =      3        Max. records in file   =   5708
 No. of bits per label  =      8        No. of bits per value  =     64


File balance: exchanges=     0  moved=     0  time=   0.0

*/
		jumboReader.readLinesWhile(Pattern.compile(
				"Iterative solution of linear equations"), false);
/*

Iterative solution of linear equations
  No. of variables      234
  No. of equations        3
  Maximum subspace       30
        Iterations       50
       Convergence  1.0D-04
        Start time      4.5

*/
		jumboReader.makeNameValues(Pattern.compile("\\s*(.*)\\s\\s+([0-9\\.DEde\\-\\+]+).*"), ADD);
		
/*

   iter   nsub   residual    time
   ----  ------  --------  ---------
     1      3    8.41D-01       5.4
     2      6    2.77D-02       6.3
     3      9    7.44D-04       7.2
     4     12    2.16D-05       8.0

*/
		jumboReader.readLinesWhile(Pattern.compile(
		"\\s*iter   nsub   residual    time"), false);
		jumboReader.readLines(1);
		jumboReader.parseTableColumnsAsArrayList(
				"(I6,I7,E12.2,F10.1)", -1, 
				new String[]{I_+"iter",I_+"nsub",F_+"residual",F_+"time"}, ADD);

/*
 Parallel integral file used       4 records with       0 large values
*/

/*
      Atom:    1  C 
        Diamagnetic
    257.9652     -0.0001     -0.0430
      0.0000    257.9400      0.0000
     -3.6400    -10.9048    254.4877

        Paramagnetic
   -171.5341      0.0001      0.0262
     -0.0001   -171.5059      0.0008
      3.6241     10.9053    -66.6073

        Total Shielding Tensor
     86.4312      0.0000     -0.0168
     -0.0001     86.4341      0.0008
     -0.0159      0.0005    187.8804

           isotropic =     120.2486
          anisotropy =     101.4478

          Principal Components and Axis System
                 1           2           3
              187.8804     86.4341     86.4312

      1        -0.0002     -0.0142      0.9999
      2         0.0000      0.9999      0.0142
      3         1.0000      0.0000      0.0002

*/
		while (jumboReader.hasMoreLines()) {
			try {
				addMagneticTensors(module);
			} catch (Exception e) {
				break;
			}
		}

	}

	private void addMagneticTensors(CMLModule module) {
		jumboReader.readLinesWhile(Pattern.compile(
		"      Atom:\\s*\\d+.*"), false);
		jumboReader.increment(-1);
		CMLModule atomModule = new CMLModule();
		module.appendChild(atomModule);
		jumboReader.setParentElement(atomModule);
		jumboReader.parseScalars("(11X,I5,A4)", new String[]{I_+"serial", A_+"elsym"}, ADD);
		readAndAddMatrix("Diamagnetic");
		readAndAddMatrix("Paramagnetic");
		readAndAddMatrix("TotalShieldingTensor");
		jumboReader.makeNameValues(Pattern.compile("\\s*([^=]*)\\s*=\\s*(.*)"), ADD);
		jumboReader.readLines(2);
		jumboReader.parseArray("(10X,3F12.4)", CMLConstants.XSD_DOUBLE, "magnetic", ADD);
		jumboReader.readLines(1);
		jumboReader.parseTableColumnsAsArrayList("(I7,3X,F12.4,F12.4,F12.4)", 3, 
				new String[]{I_+"serial", F_+"mag.a", F_+"mag.b", F_+"mag.c"}, ADD);
	}

	private void readAndAddMatrix(String dictRef) {
		jumboReader.readLines(1);
		CMLMatrix matrix = jumboReader.parseMatrix(3, 3, "(F12.4,F12.4,F12.4)", 
				CMLConstants.XSD_DOUBLE, dictRef, ADD);
		jumboReader.readLines(1);
	}

	private void makeArgument() {
/*
 argument  1 = dft_feco5.nw



============================== echo of input deck ==============================
echo

start dft_feco5

# test of DFT with Fe(CO)5 using cd basis and xc basis

...
dft
  xc b3lyp
end
title "case t23 --- (b3lyp) DFT (energy)"
task dft energy



================================================================================
 */
		CMLModule module = makeModule();
		jumboReader.makeNameValues(Pattern.compile(".*(argument *\\d+)\\s*=\\s*(.*)"), ADD);
		List<String> lines = jumboReader.readLinesWhile(Pattern.compile(".*=== echo of input deck ==.*"), false);
		jumboReader.readLine();
		NWChemBlock inputBlock = new NWChemBlock(blockContainer);
		inputBlock.setBlockName(ECHO_INPUT);
		blockContainer.getBlockList().add(0, inputBlock);
		lines = jumboReader.readLinesWhile(Pattern.compile(".*===========.*"), false);
		for (String line : lines) {
			inputBlock.add(line);
		}
		inputBlock.makeInput();
	}
	
	private void makeInput() {
		
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
		jumboReader.readLineAsScalar("title", ADD);
		jumboReader.readLines(3);
		jumboReader.makeNameValues(Pattern.compile("\\s*([^=]*)\\s*=\\s*([^=]*).*"), ADD);
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
		//TODO
	}

	private void makeNWChemInputModule() {
/*
                                NWChem Input Module
                                -------------------


  perdew86 is a nonlocal functional; adding perdew81 local functional. 
 */
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readLineAsScalar("title", ADD);
	}

	private void makeNWChemPropertyModule() {
/*
                              NWChem Property Module
                              ----------------------


              Methylamine...rhf/3-21g//Pople-Gordon standard geometry

 */
		makeModule();
		jumboReader.readLines(4);
		jumboReader.readLineAsScalar("title", ADD);
		
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^=]*)\\s*=\\s*([^=]*).*"), ADD);
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^=]*)\\s*=\\s*(.*)"), ADD);
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*(.*)"), ADD);
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*(.*)"), ADD);
		jumboReader.readLines(1);
		jumboReader.parseTableColumnsAsArrayList("(10X,A2,15X,F8.2,I9,6X,F8.1,I10)", -1,
				new String[]{A_+"tag", F_+"bsrad", I_+"radpts", F_+"radcut", I_+"angpts"}, ADD);
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*(.*)"), ADD);
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
		jumboReader.parseScalars("(24X, A)", new String[]{A_+"group.name"}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+"group.number"}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+"group.order"}, ADD);
		jumboReader.parseScalars("(24X, I4)", new String[]{I_+"group.uniquecenter"}, ADD);
		jumboReader.readLines(3);
		// doesn't yet work
		CMLArray array = jumboReader.readArrayGreedily("(1X, 10I4)", CMLConstants.XSD_INTEGER);
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), "symmetryUnique"));
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
		jumboReader.parseScalars("(F17.10,F17.10,F17.10)", new String[]{F_+"x", F_+"y", F_+"z"}, ADD);
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
		jumboReader.parseTableColumnsAsArrayList("(5X,A17,F11.4,F15.8,F15.8,F15.8)", -1, new String[]{A_+"tag", F_+"charge", F_+"x", F_+"y", F_+"z"}, ADD);
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
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), "irrrep"));
		module.appendChild(array);
		jumboReader.readLines(5);
		array = jumboReader.readArrayGreedily("5(6X,A8)", CMLConstants.XSD_STRING);
		array.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), "orbsym"));
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*([^\\s]*).*"), ADD);
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*([^\\s]*).*"), ADD);
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
		jumboReader.parseMatrix(3, 3, "(3(F25.12))", CMLConstants.XSD_DOUBLE, "momint", ADD);
	}

	private void makeCenterOfMass() {
/**
 center of mass
 --------------
 x =   0.00000000 y =   0.00000000 z =   0.00000000

 */
		makeModule();
		jumboReader.readLines(2);
		jumboReader.parseScalars("(3(' x =',F13.7))", new String[]{F_+"x",F_+"y",F_+"z"}, ADD);
		
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
		String[] names1 = {I_+"serial", A_+"atsym", I_+"atnum", F_+"charge"};
		String name2 = F_+"shellcharge";
		while(jumboReader.hasMoreLines()) {
			if (jumboReader.peekLine().trim().length() == 0) {
				break;
			}
			try {
				jumboReader.parseScalars(format1, names1, ADD);
				jumboReader.increment(-1);
				jumboReader.parseArray(format2, CMLConstants.XSD_DOUBLE, name2, ADD);
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
		String[] names = {I_+"l", I_+"x", I_+"y", I_+"z", F_+"total", F_+"alpha", F_+"beta", F_+"nuclear"};
		while(jumboReader.hasMoreLines()) {
			if (jumboReader.peekLine().trim().length() == 0) {
				break;
			}
			try {
				jumboReader.parseTableColumnsAsArrayList(format, -1, names, ADD);
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
		String type = jumboReader.readLine().split(CMLConstants.S_WHITEREGEX)[0];
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
			jumboReader.makeNameValues(Pattern.compile("\\s*([^\\=]*)\\s*\\=\\s*([^\\s]*).*"), ADD);
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
		jumboReader.parseTableColumnsAsArrayList("(I5,F10.4)", -1, new String[]{I_+"serial", F_+"eigenval"}, ADD);
	}
			

	private void readVector(String type) {
		if (ROHF.equalsIgnoreCase(type)) {
			jumboReader.parseScalars("(' Vector',I5,'  Occ=',E12.6,'  E=',E12.6)",
				new String[]{I_+"vector", F_+"occ", F_+"e"}, ADD);
		} else if (DFT.equalsIgnoreCase(type)) {
			jumboReader.parseScalars("(' Vector',I5,'  Occ=',E12.6,'  E=',E12.6,'  Symmetry=',A)",
				new String[]{I_+"vector", F_+"occ", F_+"e", A_+"symmetry"}, ADD);
		} else {
			throw new RuntimeException("Unknown Final Molecular Orbital type");
		}
		jumboReader.parseScalars("('              MO Center=',3(E9.1,','),' r^2=',E8.1)",
				new String[]{F_+"v1", F_+"v2", F_+"v3", F_+"rsquared"}, ADD);
		jumboReader.readLines(2);
		String format = "I6,F14.6,I4,A3,A14";
		String[] names2 = new String[]{I_+"bfn", F_+"coeff", I_+"atnum", A_+"atsym", A_+"orb", I_+"bfn", F_+"coeff", I_+"atnum", A_+"atsym", A_+"orb"};
		String[] names = new String[]{I_+"bfn", F_+"coeff", I_+"atnum", A_+"atsym", A_+"orb"};
		
		try {
			jumboReader.parseTableColumnsAsArrayList("(2("+format+"))", -1, names2, ADD);
		} catch (Exception e) {
			// maybe only one line
		}
		// straggling line at end?
		if (jumboReader.hasMoreLines() && jumboReader.peekLine().trim().length() > 0) {
			jumboReader.parseTableColumnsAsArrayList("("+format+")", -1, names, ADD);
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
		jumboReader.makeNameValues(Pattern.compile("\\s*([^\\:]*)\\s*\\:\\s*([^\\s]*).*"), ADD);
		jumboReader.readLinesWhile(Pattern.compile(
				"   convergence    iter        energy       DeltaE   RMS-Dens  Diis\\-err    time"), false);
		jumboReader.readLines(2);
		// d= 0,ls=0.0,diis     1  -1822.8247493675 -2.62D+03  6.20D-02  9.95D+00     1.9

		String format = "(18X,I5,F18.10,E10.2,E10.2,E10.2,F8.1)";
		jumboReader.parseTableColumnsAsArrayList(
				format, -1, new String[]{I_+"iter", F_+"energy", F_+"deltae", F_+"rmsdens", F_+"diiserr", F_+"time"}, true);
		jumboReader.readLines(2);
		jumboReader.addDouble("('         Total DFT energy =',F22.12)", "totaldft");
		jumboReader.addDouble("('       One electron energy =',F22.12)", "oneelecener");
		jumboReader.addDouble("('           Coulomb energy =',F22.12)", "coulomb");
		jumboReader.addDouble("('    Exchange-Corr. energy =',F22.12)", "echangecorr");
		jumboReader.addDouble("(' Nuclear repulsion energy =',F22.12)", "nucrepener");
		jumboReader.readEmptyLines();
		jumboReader.addDouble("(' Numeric. integr. density =',F22.12)", "numiterdens");
		jumboReader.readEmptyLines();
		jumboReader.addDouble("('     Total iterative time =',F9.1,'s')", "totitertime");
		
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
		Pattern basisPattern = Pattern.compile("Basis \"([^\"]*)\" -> \"([^\"]*)\"\\s*(.*)");
		Matcher matcher = basisPattern.matcher(lines.get(0));
		if (!matcher.matches()) {
			throw new RuntimeException("cannot match basis line: "+lines.get(0));
		}
		String basis = matcher.group(1);
		jumboReader.addDictRef(new CMLScalar(basis), "basis");
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
				new String[]{A_+"basis", A_+"basis1", A_+"coordtype"},ADD);
		jumboReader.readLines(3);
		
		try {
			jumboReader.parseTableColumnsAsArrayList("(A3,48X,I4,4X,I5,3X,A)", -1, 
					new String[]{A_+"elsym", /*A_+"desc",*/ I_+"shells", I_+"functions", A_+"types"}, true);
		} catch (Exception e) {
			jumboReader.parseTableColumnsAsArrayList("(A3,48X,A)", -1, 
					new String[]{A_+"elsym", /*A_+"desc",*/ A_+"functions"}, true);
		}
		return element;
	}

	private void makeAtomBasisBlock() {
		CMLModule module = makeModule();
		module.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), ATOM_BASIS));
		jumboReader.parseScalars(ATOM_BASIS_PATTERN, new String[]{A_+"elsym", A_+"elemName"}, true);
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
				arrayList, CMLConstants.XSD_INTEGER, abstractCommon.getPrefix(), "shell");
		CMLArray spdArray = createAndAddArray(
				arrayList, CMLConstants.XSD_STRING, abstractCommon.getPrefix(), "spd");
		CMLArray expArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), "exp");
		CMLArray coeffArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), "coeff");
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

	public static CMLArray createAndAddArray(CMLArrayList arrayList, String type, String prefix, String dictRef) {
		CMLArray array = new CMLArray();
		array.setDataType(type);
		array.setDictRef(DictRefAttribute.createValue(prefix, dictRef));
		arrayList.addArray(array);
		return array;
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
//		System.out.println("CURR>>"+jumboReader.peekLine());
		jumboReader.parseTableColumnsAsArrayList("(6X,A2,10X,F14.6)", 3, 
				new String[]{A_+"atomsym", F_+"atommass"}, JumboReader.ADD);
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
		jumboReader.addDouble("(' Total energy =',F15.6)", "totener");
		jumboReader.addDouble("(' 1-e energy   =',F15.6)", "ener1e");
		jumboReader.addDouble("(' 2-e energy   =',F15.6)", "ener2e");
		jumboReader.addDouble("(' HOMO         =',F15.6)", "homo");
		jumboReader.addDouble("(' LUMO         =',F15.6)", "lumo");
	}

	private void makeSuperposition() {
		/*
      Superposition of Atomic Density Guess
      -------------------------------------

 Sum of atomic energies:       -1824.13130503
		 */
		makeModule();
		jumboReader.readLines(3);
		jumboReader.addDouble("(' Sum of atomic energies:      'F15.8)", "sumAtomEner");
	}

	private CMLModule makeModule() {
		CMLModule module = new CMLModule();
		jumboReader.setParentElement(module);
		module.setRole(abstractCommon.getPrefix());
		element = module;
		return module;
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
			"(I6)", new String[]{I_+"natoms"}, JumboReader.DONT_ADD);
		int atomCount = ((CMLScalar)scalar).getInt();
		jumboReader.readLines(1);
		jumboReader.parseMoleculeAsColumns(atomCount, "A3,15X,3F15.8", new int[]{0, -1, -1, 1, 2, 3}, ADD);
	}

	private void summarizeBlock0() {
		CMLModule module = new CMLModule();
		module.setTitle((lines.size() == 0) ? "title" : lines.get(0));
		element = module;
		CMLScalar scalar = new CMLScalar(preserveText().getValue());
		module.appendChild(scalar);
	}

	private void summarizeBlock() {
		summarizeBlock0();
	}

	private void skipBlock() {
		CMLModule module = new CMLModule();
		module.setTitle((lines.size() == 0) ? "title" : lines.get(0));
		element = module;
	}

}
