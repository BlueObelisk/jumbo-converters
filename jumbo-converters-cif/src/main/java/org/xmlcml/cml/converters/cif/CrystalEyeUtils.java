package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.converters.cif.CrystalEyeConstants.CRYSTALEYE_DATE_FORMAT;
import static org.xmlcml.cml.converters.cif.CrystalEyeConstants.TITLE_MIME;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cif.CIFUtil;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.Type;

public class CrystalEyeUtils implements CMLConstants {

	public final static int MAX_RINGS = 15;
	
	public static enum FragmentType {
		LIGAND ("ligand"),
		CHAIN_NUC ("chain-nuc"),
		RING_NUC ("ring-nuc"),
		RING_NUC_SPROUT_1 ("ring-nuc-sprout-1"),
		RING_NUC_SPROUT_2 ("ring-nuc-sprout-2"),
		CLUSTER_NUC ("cluster-nuc"),
		CLUSTER_NUC_SPROUT_1 ("cluster-nuc-sprout-1"),
		CLUSTER_NUC_SPROUT_2 ("cluster-nuc-sprout-2"),
		MOIETY ("moiety"),
		ATOM_NUC ("atom-nuc"),
		ATOM_NUC_SPROUT_1 ("atom-nuc-sprout-1"),
		ATOM_NUC_SPROUT_2 ("atom-nuc-sprout-2");

		private FragmentType(String name) {
			this.name = name;
		}

		private final String name;

		public String toString() {
			return name;
		}
	}

	public static enum DisorderType {
		UNPROCESSED,
		PROCESSED,
		NONE;
	}

	public static enum CompoundClass {
		ORGANIC("organic"),
		INORGANIC("inorganic"),
		ORGANOMETALLIC("organometallic");

		private CompoundClass(String name) {
			this.name = name;
		}

		private final String name;

		public String toString() {
			return name;
		}
	}

	public static CompoundClass getCompoundClass(CMLMolecule molecule) {
		boolean hasMetal = false;
		boolean hasC = false;
		boolean hasH = false;
		for (CMLAtom atom : molecule.getAtoms()) {
			if (atom.getChemicalElement().isChemicalElementType(Type.METAL)) {
				hasMetal = true;
			}
			String elType = atom.getElementType();
			if ("H".equals(elType)) {
				hasH = true;
			} else if ("C".equals(elType)) {
				hasC = true;
			}
		}
		if (!hasMetal) {
			return CompoundClass.ORGANIC;
		} else if (hasMetal) {
			if (hasH && hasC) {
				return CompoundClass.ORGANOMETALLIC;
			} else {
				return CompoundClass.INORGANIC;
			}
		}
		return null;
	}

	public static List<File> getSummaryDirFileList(String issueDir, String regex) {
		List<File> fileList = new ArrayList<File>();
		issueDir += File.separator+"data"+File.separator;
		File[] parents = new File(issueDir).listFiles();
		for (File articleParent : parents) {
			File[] articleFiles = articleParent.listFiles();
			for (File structureParent : articleFiles) {
				if (structureParent.isDirectory()) {
					File[] structureFiles = structureParent.listFiles();
					for (File structureFile : structureFiles) {
						String structurePath = structureFile.getName();
						if (structurePath.matches(regex)) {
							fileList.add(structureFile);
						}
					}
				}
			}
		}
		return fileList;
	}

	public static List<File> getDataDirFileList(String issueDir, String regex) {
		List<File> fileList = new ArrayList<File>();
		File[] parents = new File(issueDir).listFiles();
		for (File articleParent : parents) {
			File[] articleFiles = articleParent.listFiles();
			for (File structureParent : articleFiles) {
				if (structureParent.isDirectory()) {
					File[] structureFiles = structureParent.listFiles();
					for (File structureFile : structureFiles) {
						String structurePath = structureFile.getName();
						if (structurePath.matches(regex)) {
							fileList.add(structureFile);
						}
					}
				}
			}
		}
		return fileList;
	}

	public static boolean isBoringMolecule(CMLMolecule molecule) {
		// skip boring moieties
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		String formulaS = formula.getConcise();
		formulaS = CMLFormula.removeChargeFromConcise(formulaS);
		if (formulaS.equals("H 2 O 1") || 
				formulaS.equals("H 3 O 1") ||
				formulaS.equals("H 4 O 1") ||
				molecule.getAtomCount() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static List<CMLMolecule> getUniqueSubMolecules(CMLMolecule molecule) {
		List<CMLMolecule> outputList = new ArrayList<CMLMolecule>();
		if (molecule.isMoleculeContainer()) {
			List<String> inchiList = new ArrayList<String>();
			for (CMLMolecule subMol : molecule.getDescendantsOrMolecule()) {
				List<Node> inchiNodes = CMLUtil.getQueryNodes(subMol, ".//cml:identifier[@convention='iupac:inchi']", CML_XPATH);
				if (inchiNodes.size() > 0) {
					String inchi = inchiNodes.get(0).getValue();
					boolean got = false;
					for (String str : inchiList) {
						if (str.equals(inchi)) got = true;
					}
					if (!got) {
						inchiList.add(inchi);
					}
				}
			}
			for (String inchi : inchiList) {
				List<Node> molNodes = CMLUtil.getQueryNodes(molecule, ".//cml:molecule[cml:identifier[text()='"+inchi+"']]", CML_XPATH);
				if (molNodes.size() > 0) {
					outputList.add((CMLMolecule)molNodes.get(0));
				}
			}
		} else {
			outputList.add(molecule);
		}
		return outputList;
	}
	
	public static Date parseString2Date(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat(CRYSTALEYE_DATE_FORMAT);
		try {
			return formatter.parse(dateString);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing String into Date - "+dateString);
		}
	}

	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(CRYSTALEYE_DATE_FORMAT);
		return formatter.format(date);
	}
	
	public static String date2String(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(CRYSTALEYE_DATE_FORMAT);
		return formatter.format(date);
	}

	public static void writeDateStamp(String path) {
		String dNow = getDate();
		CIFIOUtils.writeText(dNow, path);
	}
	
	public static String getStructureTitleFromCml(CMLCml cml) {
		Nodes titleNodes = cml.query(".//cml:scalar[@dictRef=\"iucr:_publ_section_title\"]", CML_XPATH);
		String title = "";
		try {
			if (titleNodes.size() != 0) {
				title = titleNodes.get(0).getValue();
				title = CIFUtil.translateCIF2ISO(title);
				title = title.replaceAll("\\\\", "");

				String patternStr = "\\^(\\d+)\\^";
				String replaceStr = "<sup>$1</sup>";
				Pattern pattern = Pattern.compile(patternStr);
				Matcher matcher = pattern.matcher(title);
				title = matcher.replaceAll(replaceStr);

				patternStr = "~(\\d+)~";
				replaceStr = "<sub>$1</sub>";
				pattern = Pattern.compile(patternStr);
				matcher = pattern.matcher(title);
				title = matcher.replaceAll(replaceStr);
			}
		} catch (Exception e) {
			System.err.println("Could not translate CIF string to ISO: "+title);
			title = "";
		}
		return title;
	}
	
	public static String getStructureTitleFromTOC(File cmlFile) {
		File titleParent = cmlFile.getParentFile().getParentFile();
		String name = titleParent.getName();
		File titleFile = new File(titleParent, name+TITLE_MIME);
		String title = "";
		if (titleFile.exists()) {
			title = CrystalEyeUtils.file2String(titleFile).trim();
		}
		return title;
	}
	
	public static String file2String(File file) {
		String content = "";
		try {
			FileInputStream insr = new FileInputStream(file);

			byte[] fileBuffer = new byte[(int) file.length()];
			insr.read(fileBuffer);
			insr.close();
			content = new String(fileBuffer);
		} catch (Exception e) {
			System.err.println("Unhandled exception:");
			e.printStackTrace();
		}

		return content;
	}

	/**
	 * Get the content of a file and put it in a byte array
	 */
	public static String file2String(String fileName) {
		return file2String(new File(fileName));
	}

	public static int getNumberOfRings(CMLMolecule mol) {
		int atomCount = mol.getAtomCount();
		int bondCount = mol.getBondCount();
		return (bondCount-atomCount)+1;
	}

}
