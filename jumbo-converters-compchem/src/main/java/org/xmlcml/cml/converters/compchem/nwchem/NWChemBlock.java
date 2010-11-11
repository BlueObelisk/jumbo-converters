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
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

public class NWChemBlock extends AbstractBlock {

	private static final String SUMMARY_OF_AO_BASIS = "Summary of \"ao basis\"";
	private static final String Z_MATRIX = "Z-matrix";
	private static final String NON_VARIATIONAL_INITIAL_ENERGY = "Non-variational initial energy";
	private static final String AUTO_Z = "auto-z";
	
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
			summarizeBlock0();
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
			summarizeBlock();
		} else if (NWChemProcessor.CITATION.equals(blockName)) {
			skipBlock();
		} else if (NWChemProcessor.CONVERGENCE_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.DFT_FINAL_MOLECULAR_ORBITAL_ANALYSIS.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.DIRECTORY_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.GA_STATISTICS_FOR_PROCESS.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.GEOMETRY.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.GENERAL_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.GRID_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.INTERNUCLEAR_DISTANCES.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.INTERNUCLEAR_ANGLES.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.JOB_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.MEMORY_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.MOMENTS_OF_INERTIA.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.MULTIPOLE_ANALYSIS_OF_THE_DENSITY.equals(blockName)) {
			summarizeBlock();
		} else if (NON_VARIATIONAL_INITIAL_ENERGY.equals(blockName)) {
			makeNonVariational();
		} else if (NWChemProcessor.NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.NUCLEAR_DIPOLE_MOMENT.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.NW_CHEM_DFT_MODULE.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.NW_CHEM_INPUT_MODULE.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.SCREENING_TOLERANCE_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS.equals(blockName)) {
			summarizeBlock();
		} else if (SUMMARY_OF_AO_BASIS.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS.equals(blockName)) {
			makeSuperposition();
		} else if (NWChemProcessor.SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.SYMMETRY_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.XC_INFORMATION.equals(blockName)) {
			summarizeBlock();
		} else if (NWChemProcessor.XYZ_FORMAT_GEOMETRY.equals(blockName)) {
			makeXYZ();
		} else if (Z_MATRIX.equals(blockName)) {
			makeZMatrix();
		} else if (UNKNOWN.equals(blockName)) {
			summarizeBlock();
		} else {
			System.err.println("Unknown blockname: "+blockName);
			throw new RuntimeException("add name");
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
		// skip memory stuff... (I'm bored)
//		jumboReader.addDouble("(' Total energy =',F15.6)", "totener");
		jumboReader.readLinesWhile(Pattern.compile(
				"   convergence    iter        energy       DeltaE   RMS-Dens  Diis\\-err    time"), false);
		jumboReader.readLines(2);
//		// d= 0,ls=0.0,diis',     18  -1823.6801959292 -3.28D-06  8.81D-06  3.23D-06     8.0
//		String format = "(1x,A15,1x,I5,F18.10,E10.2,E10.2,E10.2,F8.1)";
//		jumboReader.parseTableColumnsAsArrayList(
//				format, -1, new String[]{A_+"converg", I_+"iter", F_+"energy", F_+"deltae", F_+"rmsdens", F_+"diiserr", F_+"time"}, true);
////        Total DFT energy =    -1823.680196170071
//		jumboReader.addDouble("('         Total DFT energy =',F22.12)", "totaldft");
//		jumboReader.addDouble("('	   One electron energy =',F22.12)", "oneelecener");
//		jumboReader.addDouble("('           Coulomb energy =',F22.12)", "coulomb");
//		jumboReader.addDouble("('    Exchange-Corr. energy =',F22.12)", "echangecorr");
//		jumboReader.addDouble("(' Nuclear repulsion energy =',F22.12)", "nucrepener");

//	      One electron energy =    -4105.874590431184
//          Coulomb energy =     1601.844762163483
//   Exchange-Corr. energy =     -118.145985996560
//Nuclear repulsion energy =      798.495618094190
	}

	private void makeBasis() {
		/** this will read blocks according to the scheme:
		 * the blocks will have been created already. This routine assumes
		 * strict order and will finish after the summary
                      Basis "cd basis" -> "" (cartesian)
                      -----
  o (Oxygen)
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
		System.out.println(blockContainer.getCurrentBlockNumber());
		System.out.println(blockContainer.getBlockList().size());
		blockContainer.getBlockList().get(0).debug();
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
		processBasisBlocks();
	}

	private void processBasisBlocks() {
//		int currentBlockNumber = blockContainer.getCurrentBlockNumber();
//		System.out.println(blockContainer.getBlockList().size());
//		Pattern atomPattern = Pattern.compile("  ([^\\s]*)\\s+\\((.*)\\).*");
//		Matcher matcher;
//		for (int i = currentBlockNumber; i < blockContainer.getBlockList().size(); i++) {
//			AbstractBlock block = blockContainer.getBlockList().get(i);
//			String blockName = block.getBlockName();
//			matcher = atomPattern.matcher(blockName);
//			if (!matcher.matches()) {
//				CMLElement subElement = makeBasisSummary();
//				element.appendChild(subElement);
//				break;
//			}
//			String elementSymbol = Util.capitalise(matcher.group(1));
//			String elementName = matcher.group(2);
//			element.appendChild(createAtomBasisBlock(elementSymbol, elementName));
//		}
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
		 */
		String line = lines.get(0);
		Pattern pattern = Pattern.compile(" Summary of \"([^\"]*)\" -> \"(.*)\"\\s*(.*)");
		if (!pattern.matcher(line).matches()) {
			throw new RuntimeException("failed to match summary block: "+line);
		}
		makeModule();
		jumboReader.readLines(4);
		jumboReader.parseTableColumnsAsArrayList("()", -1, 
				new String[]{A_+"elsym", A_+"desc", I_+"shells", I_+"functions", I_+"types"}, true);
		return element;
	}

	private CMLElement createAtomBasisBlock(String elementSymbol, String elementName) {
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
		CMLModule subModule = new CMLModule();
		jumboReader.setParentElement(subModule);
		subModule.setRole(abstractCommon.getPrefix());
		jumboReader.readLines(4);
		CMLArrayList arrayList = new CMLArrayList();
		CMLArray spdArray = createAndAddArray(
				arrayList, CMLConstants.XSD_STRING, abstractCommon.getPrefix(), "spd");
		CMLArray expArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), "exp");
		CMLArray coeffArray = createAndAddArray(
				arrayList, CMLConstants.XSD_DOUBLE, abstractCommon.getPrefix(), "coeff");
		while (true) {
			try {
				List<Object> objects = JumboReader.parseFortranLine("(I3,A2, E15.7,  10F10.6)", jumboReader.readLine());
				spdArray.append(objects.get(0).toString());
				expArray.append((Double)objects.get(1));
				coeffArray.append((Double)objects.get(2));
				jumboReader.readLines(1);
			} catch (Exception e) {
				break;
			}
		}
		return subModule;
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

	private void makeModule() {
		CMLModule module = new CMLModule();
		jumboReader.setParentElement(module);
		module.setRole(abstractCommon.getPrefix());
		element = module;
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
		jumboReader.parseMoleculeAsColumns(atomCount, "A3,15X,3F15.8", new int[]{0, -1, -1, 1, 2, 3}, JumboReader.ADD);
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
