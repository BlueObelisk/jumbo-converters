package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.element.CMLScalar;

/**
 * @author pm286
 *
 */
public class NWChemProcessor extends LegacyProcessor {
	
	public static final String AUTHORS = "AUTHORS";
	public static final String CITATION = "CITATION";
	public static final String GA_STATISTICS_FOR_PROCESS = "GA Statistics for process";
	public static final String SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS = "Summary of allocated global arrays";
	public static final String MULTIPOLE_ANALYSIS_OF_THE_DENSITY = "Multipole analysis of the density";
	public static final String MOMENTS_OF_INERTIA = "moments of inertia";
	public static final String DFT_FINAL_MOLECULAR_ORBITAL_ANALYSIS = "DFT Final Molecular Orbital Analysis";
	public static final String _3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION = "3 Center 2 Electron Integral Information";
	public static final String SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS = "Symmetry analysis of molecular orbitals";
	public static final String NON_VARIATIONAL_INITIAL_ENERGY = "Non.variational initial energy";
	public static final String SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS = "Superposition of Atomic Density Guess";
	public static final String SCREENING_TOLERANCE_INFORMATION = "Screening Tolerance Information";
	public static final String CONVERGENCE_INFORMATION = "Convergence Information";
	public static final String GRID_INFORMATION = "Grid Information";
	public static final String XC_INFORMATION = "XC Information";
	public static final String GENERAL_INFORMATION = "General Information";
	public static final String NW_CHEM_DFT_MODULE = "NWChem DFT Module";
	public static final String CASE = "case";
	public static final String SUMMARY_OF_AO_BASIS = "Summary of .ao basis.";
	public static final String BASIS = "Basis";
	public static final String CENTER_1 = "center 1";
	public static final String INTERNUCLEAR_ANGLES = "internuclear angles";
	public static final String INTERNUCLEAR_DISTANCES = "internuclear distances";
	public static final String XYZ_FORMAT_GEOMETRY = "XYZ format geometry";
	public static final String Z_MATRIX = "Z.matrix";
	public static final String SYMMETRY_INFORMATION = "Symmetry information";
	public static final String NUCLEAR_DIPOLE_MOMENT = "Nuclear Dipole moment";
	public static final String ATOMIC_MASS = "Atomic Mass";
	public static final String GEOMETRY = "Geometry";
	public static final String AUTO_Z = "auto.z";
	public static final String NW_CHEM_INPUT_MODULE = "NWChem Input Module";
	public static final String DIRECTORY_INFORMATION = "Directory information";
	public static final String MEMORY_INFORMATION = "Memory information";
	public static final String JOB_INFORMATION = "Job information";
	public static final String ACKNOWLEDGMENT = "ACKNOWLEDGMENT";
	public static final String NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE = "Northwest Computational Chemistry Package";
	public static final String ARGUMENT = "argument";
	
	static List<String> blockStringList;
	static {
		blockStringList = new ArrayList<String>();
		blockStringList.add(ARGUMENT);
		blockStringList.add(NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE);
		blockStringList.add(ACKNOWLEDGMENT);
		blockStringList.add(JOB_INFORMATION);
		blockStringList.add(MEMORY_INFORMATION);
		blockStringList.add(DIRECTORY_INFORMATION);
		blockStringList.add(NW_CHEM_INPUT_MODULE);
		blockStringList.add(AUTO_Z);
		blockStringList.add(GEOMETRY);
		blockStringList.add(ATOMIC_MASS);
		blockStringList.add(NUCLEAR_DIPOLE_MOMENT);
		blockStringList.add(SYMMETRY_INFORMATION);
		blockStringList.add(Z_MATRIX);
		blockStringList.add(XYZ_FORMAT_GEOMETRY);
		blockStringList.add(INTERNUCLEAR_DISTANCES);
		blockStringList.add(INTERNUCLEAR_ANGLES);
		blockStringList.add(CENTER_1);
		blockStringList.add(BASIS);
		blockStringList.add(SUMMARY_OF_AO_BASIS);
		blockStringList.add(CASE);
		blockStringList.add(NW_CHEM_DFT_MODULE);
		blockStringList.add(GENERAL_INFORMATION);
		blockStringList.add(XC_INFORMATION);
		blockStringList.add(GRID_INFORMATION);
		blockStringList.add(CONVERGENCE_INFORMATION);
		blockStringList.add(SCREENING_TOLERANCE_INFORMATION);
		blockStringList.add(SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS);
		blockStringList.add(NON_VARIATIONAL_INITIAL_ENERGY);
		blockStringList.add(SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS);
		blockStringList.add(_3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION);
		blockStringList.add(DFT_FINAL_MOLECULAR_ORBITAL_ANALYSIS);
		blockStringList.add(MOMENTS_OF_INERTIA);
		blockStringList.add(MULTIPOLE_ANALYSIS_OF_THE_DENSITY);
		blockStringList.add(SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS);
		blockStringList.add(GA_STATISTICS_FOR_PROCESS);
		blockStringList.add(CITATION);
		blockStringList.add(AUTHORS);
	}
	static List<Pattern> patternList;
	static {
		patternList = new ArrayList<Pattern>();
		for (String blockString : blockStringList) {
			patternList.add(Pattern.compile("("+blockString+").*"));
		}
	}
	public NWChemProcessor() {
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		return null;
	}

	@Override
	protected AbstractBlock readBlock(CMLScalar scalar) {
		String[] lineArray = scalar.getXMLContent().split(CMLConstants.S_NEWLINE);		
		lines = new ArrayList<String>();
		// skip first line
		for (int i = 1; i < lineArray.length; i++) {
			lines.add(lineArray[i]);
		}
		AbstractBlock block = createBlock(scalar.getAttributeValue("line"));
		block.convertToRawCML();
		return block;
	}
	
	public Element processIntoBlocks(Element element) {
		List<Node> scalarNodes = CMLUtil.getQueryNodes(element,"./*[local-name()='scalar']");
		for (Node scalarNode : scalarNodes) {
			Element scalar = (CMLScalar) scalarNode;
		}
		return element;
	}
	
	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private NWChemBlock createBlock(String line0) {
		NWChemBlock block = new NWChemBlock(blockContainer);
		setBlockName(block, line0);
		for (String line : lines) {
			block.add(line);
		}
		return block;
	}

	private void setBlockName(NWChemBlock block, String line) {
		String name = "unknown";
		if (line != null) {
			for (Pattern pattern : patternList) {
				Matcher matcher = pattern.matcher(line.trim());
				if (matcher.matches()) {
					name = line;
					if (matcher.groupCount() >= 1) {
						name = matcher.group(1);
					}
					break;
				}
			}
			if (name == null) {
				System.err.println("Cannot match "+line);
			}
		}
		block.setBlockName(name);
	}

	@Override
	protected void preprocessBlocks() {
		// not required
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
	}
	
}
