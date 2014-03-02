package org.xmlcml.cml.converters.molecule.fragments;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;

public class CrystalEyeUtils {

	public static enum DisorderType {
		UNPROCESSED,
		PROCESSED,
		NONE;
	}

	public static List<File> getSummaryDirFileList(String issueDir, String regex) {
		List<File> fileList = new ArrayList<File>();
		issueDir += "/"+"data"+"/";
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

}
