package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
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
	
	public static final String _3_CENTER_2_ELECTRON_INTEGRAL_INFORMATION = "3 Center 2 Electron Integral Information";
	public static final String ACKNOWLEDGMENT = "ACKNOWLEDGMENT";
	public static final String ARGUMENT = "argument";
	public static final String ATOMIC_MASS = "Atomic Mass";
	public static final String AUTHORS = "AUTHORS";
	public static final String AUTO_Z = "auto.z";
	public static final String BASIS = "Basis";
	public static final String CASE = "case";
	public static final String CENTER_1 = "center 1";
	public static final String CENTER_OF_MASS = "center of mass";
	public static final String CENTER_ONE = "center one";
	public static final String CITATION = "CITATION";
	public static final String CONVERGENCE_INFORMATION = "Convergence Information";
	public static final String FINAL_MOLECULAR_ORBITAL_ANALYSIS = "Final Molecular Orbital Analysis";
	public static final String DIRECTORY_INFORMATION = "Directory information";
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
	public static final String NON_VARIATIONAL_INITIAL_ENERGY = "Non.variational initial energy";
	public static final String NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE = "Northwest Computational Chemistry Package";
	public static final String NUCLEAR_DIPOLE_MOMENT = "Nuclear Dipole moment";
	public static final String NW_CHEM_DFT_MODULE = "NWChem DFT Module";
	public static final String NW_CHEM_CPHF_MODULE = "NWChem CPHF Module";
	public static final String NW_CHEM_INPUT_MODULE = "NWChem Input Module";
	public static final String SUMMARY_OF_ALLOCATED_GLOBAL_ARRAYS = "Summary of allocated global arrays";
	public static final String SUMMARY_OF_BASIS = "Summary of .* basis.";
	public static final String SUPERPOSITION_OF_ATOMIC_DENSITY_GUESS = "Superposition of Atomic Density Guess";
	public static final String SCREENING_TOLERANCE_INFORMATION = "Screening Tolerance Information";
	public static final String SYMMETRY_ANALYSIS_OF_MOLECULAR_ORBITALS = "Symmetry analysis of molecular orbitals";
	public static final String SYMMETRY_INFORMATION = "Symmetry information";
	public static final String XC_INFORMATION = "XC Information";
	public static final String XYZ_FORMAT_GEOMETRY = "XYZ format geometry";
	public static final String Z_MATRIX = "Z.matrix";
	static List<String> blockStringList;
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
		blockStringList.add(FINAL_MOLECULAR_ORBITAL_ANALYSIS);
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
		blockStringList.add(NON_VARIATIONAL_INITIAL_ENERGY);
		blockStringList.add(NORTHWEST_COMPUTATIONAL_CHEMISTRY_PACKAGE);
		blockStringList.add(NUCLEAR_DIPOLE_MOMENT);
		blockStringList.add(NW_CHEM_DFT_MODULE);
		blockStringList.add(NW_CHEM_CPHF_MODULE);
		blockStringList.add(NW_CHEM_INPUT_MODULE);
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
	static List<Pattern> patternList;
	static {
		patternList = new ArrayList<Pattern>();
		for (String blockString : blockStringList) {
			patternList.add(Pattern.compile(".*("+blockString+").*"));
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
		for (int i = 0; i < lineArray.length; i++) {
			lines.add(lineArray[i]);
		}
		AbstractBlock block = createBlock(lines.get(0));
		block.convertToRawCML();
		return block;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private AbstractBlock createBlock(String line0) {
		AbstractBlock block = new NWChemBlock(blockContainer);
		
		setBlockName(block, line0);
		for (String line : lines) {
			block.add(line);
		}
		return block;
	}

	private void setBlockName(AbstractBlock block, String line) {
		String name = "unknown";
		if (line != null) {
			for (Pattern pattern : patternList) {
				Matcher matcher = pattern.matcher(line.trim());
				if (matcher.matches()) {
					name = line;
					if (matcher.groupCount() >= 1) {
						name = matcher.group(1);
					}
//					System.out.println(">>>"+line);
					break;
				}
			}
			if (name == null) {
				System.err.println("Cannot match "+line);
				throw new RuntimeException("Cannot find block name "+line);
			}
		}
		block.setBlockName(name);
	}

	@Override
	protected void preprocessBlocks(CMLElement element) {
		// shift last line of previous block to this
		List<Node> scalarNodes = CMLUtil.getQueryNodes(element, "*");
		for (int i = 1; i < scalarNodes.size(); i++) {
			CMLScalar previousScalar = (CMLScalar) scalarNodes.get(i-1);
			String previousLine = removeLastLine(previousScalar);
			CMLScalar scalar = (CMLScalar) scalarNodes.get(i);
			prefixWithLine(previousLine, scalar);
		}
		System.out.println("finished preprocess");
	}
	
	private String removeLastLine(CMLScalar scalar) {
		String content = scalar.getXMLContent().trim();
		int idx = content.lastIndexOf(CMLConstants.S_NEWLINE);
		String lastLine = content.substring(idx+1);
		content = content.substring(0, idx)+CMLConstants.S_NEWLINE;
		scalar.setXMLContent(content);
		return lastLine;
	}

	private void prefixWithLine(String line, CMLScalar scalar) {
		String content = scalar.getXMLContent();
		content = line+CMLConstants.S_NEWLINE+content;
		scalar.setXMLContent(content);
	}

	@Override
	protected void postprocessBlocks() {
		// not required
	}
	
}

