package org.xmlcml.cml.converters.molecule.fragments;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.molecule.fragments.ChemistryUtils.CompoundClass;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.ConnectionTableTool;
import org.xmlcml.cml.tools.DisorderTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.cml.tools.ValencyTool;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;

public class FragmentGenerator  {

	private static final String ATOM_NUC_SPROUT_1 = "atom-nuc-sprout-1";
	private static final String _ATOM_NUC_SPROUT_1 = "_atom-nuc-sprout-1_";
	private static final String R = "R";
	private static final String SPROUT_2 = "-sprout-2";
	private static final String SPROUT_1 = "-sprout-1";
	private static final String LIGAND = "ligand";
	private static final String CLUSTER_NUCLEUS = "cluster-nuc";
	private static final String RING_NUCLEUS = "ring-nuc";
 	private static final String CHAIN_NUCLEUS = "chain-nuc";

	private static final Logger LOG = Logger.getLogger(FragmentGenerator.class);
	private static final String NED24_NS = "ned24";

	private String doi;
	private List<CMLMolecule> fragmentList;
	private boolean sprout;
	private CMLMolecule currentMoiety;
	private int moietySerial;
	private CMLMolecule currentSubMolecule;
	private String compoundClass;
	private CMLMolecule molecule;
	private String moleculeId = "mol";
	private File outputDir;
	private File currentMoleculeDirectory;
	private File currentMoietyDirectory;
	private int currentFragmentSerial;
	private String currentFragmentType;
	private File currentFragmentDirectory;

	public FragmentGenerator() {
		;
	}

	/** main entry point
	 * 
	 * @param cml requires a molecule and a scalar with @compoundClass
	 */
	public void processCml(CMLCml cml) {
		Nodes classNodes = cml.query(".//cml:scalar[@dictRef='iucr:compoundClass']", CMLConstants.CML_XPATH);
		compoundClass = "";
		if (classNodes.size() > 0) {
			compoundClass = ((Element)classNodes.get(0)).getValue();
		} else {
			throw new RuntimeException("Molecule should have a compoundClass scalar set.");
		}
		boolean isPolymeric = false;
		Nodes polymericNodes = cml.query(".//"+CMLMetadata.NS+"[@dictRef='"+
				CrystalEyeConstants.POLYMERIC_FLAG_DICTREF+"']", CMLConstants.CML_XPATH);
		if (polymericNodes.size() > 0) {
			isPolymeric = true;
		}

		if (!(compoundClass.equals(CompoundClass.INORGANIC.toString()) || isPolymeric)) {
			CMLMolecule molecule = (CMLMolecule)cml.getFirstCMLChild(CMLMolecule.TAG);
	
			if (molecule.getAtomCount() < 1000) {
				createMoietiesAndFragments(molecule);
			}
		}
	}

	/** creates moieties and fragments for molecule.
	 * 
	 * @param molecule
	 */
	public void createMoietiesAndFragments(CMLMolecule molecule) {
		setMolecule(molecule);
		createMoietiesAndFragments();
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	public void createMoietiesAndFragments() {
		if (this.molecule == null) {
			throw new RuntimeException("Null molecule");
		}
		detachExplicitBondLengthElements(molecule);

		//don't generate image if the molecule is disordered
		List<CMLMolecule> uniqueMolList = CrystalEyeUtils.getUniqueSubMolecules(molecule);	
		int count = 1;
		for (CMLMolecule subMol : uniqueMolList) {	
			count = processDisorder(count, subMol);
		}
		writeStructureAndImages(currentMoleculeDirectory, molecule, "total");

		try {
			generateMoieties(molecule);
		} catch(Exception e) {
			throw new RuntimeException("Error while generating moieties: ", e);
		}
	}

	private void detachExplicitBondLengthElements(CMLMolecule molecule) {
		List<Node> bondLengthNodes = CMLUtil.getQueryNodes(molecule, ".//cml:length", CMLConstants.CML_XPATH);
		for (Node bondLengthNode : bondLengthNodes) {
			bondLengthNode.detach();
		}
	}

	private int processDisorder(int count, CMLMolecule subMol) {
		Nodes nonUnitOccNodes = subMol.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CMLConstants.CML_XPATH);
		if (!DisorderTool.isDisordered(subMol) && !subMol.hasCloseContacts() && nonUnitOccNodes.size() == 0
				&& CMLUtils.hasBondOrdersAndCharges(subMol)) {
			if (ChemistryUtils.isBoringMolecule(subMol)) {
				return count;
			}
			output2dImages(subMol, currentMoleculeDirectory, "submol");
			count++;
		}
		return count;
	}

	private void addSmiles2Molecule(String smiles, CMLMolecule mol) {
		if (smiles != null && !"".equals(smiles)) {
			Element identifier = new Element("identifier", CMLConstants.CML_NS);
			identifier.addAttribute(new Attribute("convention", "daylight:smiles"));
			identifier.appendChild(new Text(smiles));
			mol.appendChild(identifier);
		}
	}

	private void write2dImage(String path, CMLMolecule molecule, int width, int height, boolean showH) {
		try {
			if (molecule.isMoleculeContainer()) {
				throw new RuntimeException("Molecule should not contain molecule children.");
			}
			File file = new File(path).getParentFile();
			if (file != null && !file.exists()) {
				file.mkdirs();
			}
			add2DCoordsAndRender(path, molecule, width, height);
		} catch (Exception e) {
			throw new RuntimeException("Could not produce 2D image for molecule", e);
		}
	}

	private void add2DCoordsAndRender(String path, CMLMolecule molecule, int width,
			int height) {
		CDKUtils.add2DCoords(molecule);
		Cml2PngTool cp = new Cml2PngTool(molecule);
		cp.setWidthAndHeight(width, height);
		cp.renderMolecule(path);
	}

	private String getOutPath(File writeDir, String id, 
			String fragType, String typePrefix, int subMol, int serial, String mime) {
		String s = writeDir.getAbsolutePath();
		File dir = new File(s + "/" + fragType);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!"".equalsIgnoreCase(typePrefix)) {
			fragType = fragType+"-"+typePrefix;
		}
		s = dir + "/" + id+"_"+fragType;
		if (subMol > 0) {
			s+= CMLConstants.S_UNDER + subMol;
		}
		s += CMLConstants.S_UNDER+serial+mime;

		return s;
	}

	private void addAtomSequenceNumbers(CMLMolecule mol) {
//		if (mol.isMoleculeContainer()) {
//			LOG.warn("Molecule should be a molecule container.");
//		}
		int i = 0;
		for (CMLAtom atom : mol.getAtoms()) {
			CMLElement.addCMLXAttribute(atom, "sequenceNumber", String.valueOf(i+1));
			i++;
		}
	}

	private CMLMolecule addAndMarkFragmentRAtoms(CMLMolecule mol, CMLAtomSet atomSet) {
		CMLAtomSet newAS = new CMLAtomSet();
		List<CMLAtom> atoms = atomSet.getAtoms();
		List<String> rGroupIds = new ArrayList<String>();
		for (CMLAtom atom : atoms) {
			newAS.addAtom(atom);
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			for (CMLAtom ligand : ligandList) {
				if (!atomSet.contains(ligand)) {
					newAS.addAtom(ligand);	
					rGroupIds.add(ligand.getId());
					mol.getBond(atom, ligand).setOrder(CMLBond.SINGLE_S);
				}
			}
		}
		CMLBondSet newBS = MoleculeTool.getOrCreateTool(mol).getBondSet(newAS);
		CMLMolecule newMol = MoleculeTool.createMolecule(newAS, newBS);
		List<CMLBond> removeList = new ArrayList<CMLBond>();
		for (String id : rGroupIds) {
			CMLAtom at = newMol.getAtomById(id);
			at.setElementType(R);
			at.setFormalCharge(0);
			// set single bonds to all R groups
			List<CMLBond> bonds = at.getLigandBonds();
			for (CMLBond bond : bonds) {
				// remove bonds between two R groups
				List<CMLAtom> ats = bond.getAtoms();
				if (R.equalsIgnoreCase(ats.get(0).getElementType()) && R.equalsIgnoreCase(ats.get(1).getElementType())) {
					if (!removeList.contains(bond)) {
						removeList.add(bond);
					}
				}
				bond.setAttribute("order", CMLBond.SINGLE_S);
			}
		}
		for (CMLBond bond : removeList) {
			bond.detach();
		}
		return newMol;
	}


	private void outputFragments(String fragType, int depth) {
		List<CMLMolecule> moietyList = currentMoiety.getDescendantsOrMolecule();
		int subMolCount = 0;
		this.currentFragmentType = fragType;
		sprout = false;
		for (CMLMolecule moiety : moietyList) {
			currentSubMolecule = moiety;
			MoleculeTool mt = MoleculeTool.getOrCreateTool(currentSubMolecule);
			fragmentList = new ArrayList<CMLMolecule>();
			if (CHAIN_NUCLEUS.equals(currentFragmentType)) {
				List<CMLMolecule> chainList = createChainNuclei();
				fragmentList.addAll(chainList);
			} else if (RING_NUCLEUS.equals(currentFragmentType)) {
				List<CMLMolecule> ringList = createRingNuclei();
				fragmentList.addAll(ringList);
				sprout = true;
			} else if (CLUSTER_NUCLEUS.equals(currentFragmentType)) {
				List<Type> typeList = new ArrayList<Type>();
				typeList.add(ChemicalElement.Type.METAL);
				fragmentList = mt.createClusters(typeList);
				sprout = true;
			} else if (LIGAND.equals(currentFragmentType)) {
				List<Type> typeList = new ArrayList<Type>();
				typeList.add(ChemicalElement.Type.METAL);
				fragmentList = mt.createLigands(typeList);
			} else {
				throw new IllegalArgumentException("Illegal type of fragment.");
			}
			debugFragmentList(fragmentList);
			subMolCount++;
			int count = 0;
			if (sprout) {
				writeFragmentFilesWithSprout(currentSubMolecule, currentFragmentType, depth,
					subMolCount, mt, count);
			}
		}
	}

	private List<CMLMolecule> createChainNuclei() {
		List<CMLMolecule> chainList = new ArrayList<CMLMolecule>();
		CMLMolecule copyMolecule = new CMLMolecule(currentSubMolecule);
		ValencyTool.removeMetalAtomsAndBonds(copyMolecule);
		new ConnectionTableTool(copyMolecule).partitionIntoMolecules();
		for (CMLMolecule subSubMol : copyMolecule.getDescendantsOrMolecule()) {
			chainList.addAll(MoleculeTool.getOrCreateTool(subSubMol).getChainMolecules());
		}
		return chainList;
	}

	private List<CMLMolecule> createRingNuclei() {
		List<CMLMolecule> chainList = new ArrayList<CMLMolecule>();
		CMLMolecule copyMolecule  = new CMLMolecule(currentSubMolecule);
		ValencyTool.removeMetalAtomsAndBonds(copyMolecule);
		new ConnectionTableTool(copyMolecule).partitionIntoMolecules();
		for (CMLMolecule subSubMol : copyMolecule.getDescendantsOrMolecule()) {
			fragmentList.addAll(MoleculeTool.getOrCreateTool(subSubMol).getRingNucleiMolecules());
		}
		return chainList;
	}

	private void debugFragmentList(List<CMLMolecule> fragmentList) {
		currentFragmentSerial = 0;
		for (CMLMolecule fragment : fragmentList) {
			makeCurrentFragmentDirectory();
			writeStructureAndImages(currentFragmentDirectory, fragment, "fragment");
			currentFragmentSerial++;
		}
	}

	private void writeStructureAndImages(File directory, CMLMolecule fragment, String basename) {
		if (directory == null) {
			throw new RuntimeException("Please set output directory");
		}
		File f = new File(directory, basename+".cml");
		LOG.trace("writing "+directory+" "+f.getAbsolutePath());
		CMLUtils.debugToFile(fragment, f.toString());
		output2dImages(fragment, directory, basename);
	}

	private void makeCurrentFragmentDirectory() {
		currentFragmentDirectory = new File(currentMoietyDirectory, currentFragmentType+"."+currentFragmentSerial);
		currentFragmentDirectory.mkdirs();
	}

	private void writeFragmentFilesWithSprout(CMLMolecule theMol, String fragType, int depth, 
			int subMolCount, MoleculeTool moleculeTool, int count) {
		for (CMLMolecule fragment : fragmentList) {
			count++;
			CMLAtomSet atomSet = new CMLAtomSet(currentSubMolecule, MoleculeTool.getOrCreateTool(fragment).getAtomSet().getAtomIDs());
			decorateMoleculeAndOutput(theMol, null);
			if (sprout) {
				CMLMolecule sproutMol = moleculeTool.sprout(atomSet);
				int nucCount = fragment.getAtomCount();
				int sproutCount = sproutMol.getAtomCount();
				if (nucCount < sproutCount) {
					decorateMoleculeAndOutput(sproutMol, /*fragType+SPROUT_1, subMolCount, count, 
							dir, id, depth*/ null);
					CMLAtomSet spAtomSet = new CMLAtomSet(currentSubMolecule, MoleculeTool.getOrCreateTool(sproutMol).getAtomSet().getAtomIDs());
					CMLMolecule sprout2Mol = moleculeTool.sprout(spAtomSet);
					int sprout2Count = sprout2Mol.getAtomCount();
					if (sproutCount < sprout2Count && sprout2Count < currentSubMolecule.getAtomCount()) {
						decorateMoleculeAndOutput(sprout2Mol, /*fragType+SPROUT_2, subMolCount, count, 
								depth*/ null);
					}
				}	
			}
		}
	}

	private void removeStereoInformation(CMLMolecule molecule) {
		Nodes atomParityNodes = molecule.query(".//"+CMLAtomParity.NS, CMLConstants.CML_XPATH);
		for (int i = 0; i < atomParityNodes.size(); i++) {
			atomParityNodes.get(i).detach();
		}
		Nodes bondStereoNodes = molecule.query(".//"+CMLBondStereo.NS, CMLConstants.CML_XPATH);
		for (int i = 0; i < bondStereoNodes.size(); i++) {
			bondStereoNodes.get(i).detach();
		}
	}

	private void decorateMoleculeAndOutput(CMLMolecule subMolecule, CMLMolecule fragment) {
		CMLMolecule subMolCopy = new CMLMolecule(subMolecule);
		if (fragment != null) {
			CMLMolecule fragCopy = new CMLMolecule(fragment);
			CMLAtomSet atomSet = new CMLAtomSet(subMolCopy, MoleculeTool.getOrCreateTool(fragCopy).getAtomSet().getAtomIDs());
			CMLMolecule molR = addAndMarkFragmentRAtoms(subMolCopy, atomSet);
			changeRtoXx(molR);
			addInChIToRGroupMolecule(molR);
			addSMILESForNonInorganicMolecule(compoundClass, fragCopy, molR);
			addAtomSequenceNumbers(molR);
			addDoi(molR);
	//		outputCMLAndImages(subMolecule, fragType, subMolCount, count, dir, id, molR);
		}
	}

	private void outputCMLAndImages(CMLMolecule subMolecule, String fragType,
			int subMolCount, int count, File dir, String id, CMLMolecule molR) {
		
		molR.setId(subMolecule.getId()+"_"+fragType+"_"+subMolCount+"_"+count);
		String outPath = getOutPath(dir, id, fragType, "", subMolCount, count, CrystalEyeConstants.COMPLETE_CML_MIME);
		Utils.writeXML(new File(outPath), new Document(molR));
		String pathMinusMime = Utils.getPathMinusMimeSet(outPath);
		throw new RuntimeException ("not used");
//		output2dImages(molR, pathMinusMime);
	}

	private void addSMILESForNonInorganicMolecule(String compoundClass, CMLMolecule fragCopy,
			CMLMolecule molR) {
		if (!compoundClass.equals(CompoundClass.INORGANIC.toString())) {
			if (getNumberOfRings(fragCopy) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
				String smiles = SmilesTool.generateSmiles(fragCopy);
				if (smiles != null) {
					addSmiles2Molecule(smiles, molR);
				}
			}
		}
	}

	private void generateMoieties(CMLMolecule mergedMolecule) {
		moietySerial = 0;
		List<CMLMolecule> moietyList = mergedMolecule.getDescendantsOrMolecule();
		LOG.debug("distinct molecules "+moietyList.size());
		for (CMLMolecule moiety : moietyList) {
			this.currentMoiety = moiety;
			LOG.debug("moiety "+moietySerial);
			makeCurrentMoietyDirectory();
			Nodes nonUnitOccNodes = currentMoiety.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CMLConstants.CML_XPATH);
			if (nonUnitOccNodes.size() != 0) {
				LOG.warn("some disordered atoms");
			}
			if (DisorderTool.isDisordered(currentMoiety)) {
				LOG.debug("skipped disordered");
			} else if (moiety.hasCloseContacts()) {
				LOG.debug("has close contacts");
//			} else if (nonUnitOccNodes.size() != 0) {
//				LOG.debug("nonUnitOcc nodes non-zero (must find out what this means!)");
			} else if (!CMLUtils.hasBondOrdersAndCharges(currentMoiety)) {
				LOG.debug("no bond orders or charges");
			} else {
				if (ChemistryUtils.isBoringMolecule(currentMoiety)) continue;
				detachAnyCrystalNodesFromMolecule(currentMoiety);
				addDoi(currentMoiety);
				if (!ChemistryUtils.containsMetal(currentMoiety)) {
					String smiles = new SMILESTool(currentMoiety).write();
					LOG.debug(smiles);
				}
//				CMLUtils.calculateAndAddInchi(currentMoiety);
				CMLUtils.debugToFile(currentMoiety, new File(currentMoietyDirectory, "molecule.cml").toString());
				output2dImages(currentMoiety, currentMoietyDirectory, "moiety");
				addAtomSequenceNumbers(currentMoiety);
				// remove atom sequence numbers from mol now it has been written so they
				// don't interfere with the fragments that are written
				removeAtomsWithoutSequenceNumbers(currentMoiety);
				generateFragments();
			}
			moietySerial++;
		}
	}

	private void makeCurrentMoietyDirectory() {
		this.currentMoietyDirectory = new File(currentMoleculeDirectory, "m."+moietySerial);
		currentMoietyDirectory.mkdirs();
	}

	private void maKeCurrentMoleculeDirectory() {
		this.currentMoleculeDirectory = new File(getOutputDir(), moleculeId);
		currentMoleculeDirectory.mkdirs();
	}

	private Nodes detachAnyCrystalNodesFromMolecule(CMLMolecule mol) {
		// remove crystal nodes from molecule if they exist
		Nodes crystNodes = mol.query(".//"+CMLCrystal.NS, CMLConstants.CML_XPATH);
		for (int i = 0; i < crystNodes.size(); i++) {
			crystNodes.get(i).detach();
		}
		return crystNodes;
	}

	private void removeAtomsWithoutSequenceNumbers(CMLMolecule mol) {
		for (CMLAtom atom : mol.getAtoms()) {
			Attribute att = atom.getAttribute("sequenceNumber", NED24_NS);
			if (att != null) {
				att.detach();
			}
		}
	}

	private void output2dImages(CMLMolecule mol, File imageDirectory, String basename) {
		String smallPngPath = new File(imageDirectory, basename+".s.png").toString();
		String pngPath = new File(imageDirectory, basename+".png").toString();
		try {
			write2dImage(pngPath, mol, 600, 600, true);
			write2dImage(smallPngPath, mol, 358, 278, true);
		} catch (Exception e) {
			LOG.warn("Cannot write images "+e);
		}
	}

	private void generateFragments() {
		removeStereoInformation(currentMoiety);
		outputFragments(CHAIN_NUCLEUS, 7);
		outputFragments(RING_NUCLEUS, 7);
		outputFragments(CLUSTER_NUCLEUS, 7);
		outputFragments(LIGAND, 7);
		outputAtomCenteredSpecies();
	}

	private void outputAtomCenteredSpecies() {
		currentFragmentType = "atomCentre";
		List<CMLMolecule> subMoleculeList = currentMoiety.getDescendantsOrMolecule();
		int subMol = 0;
		for (CMLMolecule subMolecule : subMoleculeList) {
			CMLMolecule subMolCopy = new CMLMolecule(subMolecule);
			MoleculeTool subMoleculeTool = MoleculeTool.getOrCreateTool(subMolCopy);
			subMol++;
			List<CMLAtom> atoms = subMolecule.getAtoms();
			int atomCount = 0;
			for (CMLAtom atom : atoms) {
				ChemicalElement element = atom.getChemicalElement();
				if (element.isChemicalElementType(Type.METAL)) {
					currentFragmentSerial = atomCount;
					atomCount++;
					Set<CMLAtom> atomSet = new HashSet<CMLAtom>();
					atomSet.add(atom);
					CMLAtomSet singleAtomSet = new CMLAtomSet(atomSet);
					CMLMolecule atomR = addAndMarkFragmentRAtoms(subMolCopy, singleAtomSet);
					CMLMolecule atomMol = new CMLMolecule();
					atomMol.addAtom((CMLAtom)atom.copy());

					if (isNotInorganic()) {
						addInchiAndSmiles(atomR, atomMol);
					}

					addAtomSequenceNumbers(atomR);
					addDoi(atomR);
					atomR.setId(subMolecule.getId()+"_atom-nuc_"+subMol+"_"+atomCount);
					changeRtoXx(atomR);
					makeCurrentFragmentDirectory();
					writeStructureAndImages(currentFragmentDirectory, atomR, "atomCentre");
					
//
//					String outfile = new File(currentFragmentDirectory, "metal.cml");
//					String outPath = getOutPath(dir, id, "atom-nuc", "", subMol, atomCount, CrystalEyeConstants.COMPLETE_CML_MIME);
//					Utils.writeXML(new File(outPath), new Document(atomR));
//					String pathMinusMime = Utils.getPathMinusMimeSet(outPath);
//					output2dImages(atomR, new File(pathMinusMime));

					// NOT YET TESTED from here down
					//sprout once
					CMLMolecule sprout = subMoleculeTool.sprout(singleAtomSet);
					CMLAtomSet spAtomSet = new CMLAtomSet(subMolecule, MoleculeTool.getOrCreateTool(sprout).getAtomSet().getAtomIDs());

					CMLMolecule sproutR = addAndMarkFragmentRAtoms(subMolCopy, spAtomSet);
					if (sproutR.getAtomCount() == sprout.getAtomCount()) {
						continue;
					}
					// need to calculated inchi and smiles before R groups added
					addInChIToRGroupMolecule(sproutR);
					if (compoundClass != null && !compoundClass.equals(CompoundClass.INORGANIC.toString())) {
						if (getNumberOfRings(sprout) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
							String sproutSmiles= SmilesTool.generateSmiles(sprout);
							addSmiles2Molecule(sproutSmiles, sproutR);
						}
					}

					addAtomSequenceNumbers(sproutR);
					addDoi(sproutR);
					sproutR.setId(subMolecule.getId()+_ATOM_NUC_SPROUT_1+subMol+"_"+atomCount);
					String outPath = "bar";
//					outPath = getOutPath(dir, id, ATOM_NUC_SPROUT_1, "", subMol, atomCount, 
//							CrystalEyeConstants.COMPLETE_CML_MIME);
//					String pathMinusMime = Utils.getPathMinusMimeSet(outPath);	          

//					Utils.writeXML(new File(outPath), new Document(sproutR));
					changeRtoXx(sproutR);
//					output2dImages(sproutR, new File(pathMinusMime));
					writeStructureAndImages(currentFragmentDirectory, sproutR, "sproutR");

					// sprout2
					CMLMolecule sprout2 = subMoleculeTool.sprout(spAtomSet);
					int sprout2Count = sprout2.getAtomCount();
					int sproutCount = sprout.getAtomCount();
					if (sproutCount < sprout2Count) {
						CMLAtomSet sprout2AtomSet = new CMLAtomSet(subMolecule, MoleculeTool.getOrCreateTool(sprout2).getAtomSet().getAtomIDs());
						CMLMolecule sprout2R = addAndMarkFragmentRAtoms(subMolCopy, sprout2AtomSet);
						if (sprout2R.getAtomCount() == sprout2.getAtomCount()) {
							continue;
						}

						addInChIToRGroupMolecule(sprout2R);
						if (compoundClass != null && !compoundClass.equals(CompoundClass.INORGANIC.toString())) {
							// need to calculated inchi and smiles before R groups added
							if (getNumberOfRings(sprout2) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
								String sprout2Smiles = SmilesTool.generateSmiles(sprout2);
								addSmiles2Molecule(sprout2Smiles, sprout2R);
							}
						}

						addAtomSequenceNumbers(sprout2R);
						addDoi(sprout2R);
						sprout2R.setId(subMolecule.getId()+"_atom-nuc-sprout-2_"+subMol+"_"+atomCount);

//						outPath = getOutPath(dir, id, "atom-nuc-sprout-2", "", subMol, atomCount, CrystalEyeConstants.COMPLETE_CML_MIME);
//						outPath = "plunk";
//						pathMinusMime = Utils.getPathMinusMimeSet(outPath);
//						Utils.writeXML(new File(outPath), new Document(sprout2R));
						changeRtoXx(sprout2R);
//						output2dImages(sprout2R, new File(pathMinusMime));
						writeStructureAndImages(currentFragmentDirectory, sprout2R, "sprout2R");
					}
				}
			}
		}
	}

	private boolean isNotInorganic() {
		return compoundClass != null && CompoundClass.INORGANIC.toString().equals(compoundClass);
	}

	private void changeRtoXx(CMLMolecule atomR) {
		for (CMLAtom at : atomR.getAtoms()) {
			if (R.equals(at.getChemicalElement().getSymbol())) {
				at.setElementType("Xx");
			}
		}
	}

	private void addInchiAndSmiles(CMLMolecule atomR, CMLMolecule atomMol) {
		// need to calculate inchi and smiles before R groups added
		addInChIToRGroupMolecule(atomR);
		MoleculeTool atomMolTool = MoleculeTool.getOrCreateTool(atomMol);
		SMILESTool smilesTool = new SMILESTool(atomMol);
		String atomMolSmiles = smilesTool.toString();
//						String atomMolSmiles = SmilesTool.generateSmiles(atomMol);
		addSmiles2Molecule(atomMolSmiles, atomR);
	}

	public static int getNumberOfRings(CMLMolecule mol) {
		int atomCount = mol.getAtomCount();
		int bondCount = mol.getBondCount();
		return (bondCount-atomCount)+1;
	}

	private void addDoi(Element element) {
		Element doi = new Element("scalar", CMLConstants.CML_NS);
		doi.addAttribute(new Attribute("dictRef", "idf:doi"));
		doi.addAttribute(new Attribute("dataType", "xsd:string"));
		doi.appendChild(new Text(this.doi));
		element.appendChild(doi);
	}

	private void addInChIToRGroupMolecule(CMLMolecule molecule) {
		// need to calculated inchi and smiles before R groups added
		CMLMolecule copy = new CMLMolecule(molecule);
		List<CMLAtom> detachAtomList = new ArrayList<CMLAtom>();
		List<CMLBond> detachBondList = new ArrayList<CMLBond>();
		for (CMLAtom atom : copy.getAtoms()) {
			if (R.equals(atom.getElementType())) {
				for (CMLBond bond : atom.getLigandBonds()) {
					if (!detachBondList.contains(bond)) {
						detachBondList.add(bond);
					}
				}
				detachAtomList.add(atom);
			}
		}
		for (CMLAtom atom : detachAtomList) {
			atom.detach();
		}
		for (CMLBond bond : detachBondList) {
			bond.detach();
		}
//		String inchi = InchiTool.generateInchi(molecule, "");
//		CMLIdentifier identifier = new CMLIdentifier();
//		identifier.setConvention("iupac:inchi");
//		identifier.appendChild(new Text(inchi));
//		molecule.appendChild(identifier);
	}

	public void readMolecule(File file) {
		this.moleculeId  = FilenameUtils.getBaseName(file.getName());
		CMLElement element = CMLUtil.parseQuietlyIntoCML(file);
		Nodes nodes = element.query("//cml:molecule", CMLConstants.CML_XPATH);
		molecule = (nodes.size() == 0) ? null : (CMLMolecule) nodes.get(0);
	}

	public void setOutputDir(File output) {
		this.outputDir = output;
		maKeCurrentMoleculeDirectory();
		try {
			outputDir.mkdirs();
		} catch (Exception e) {
			throw new RuntimeException("Cannot generate output directory", e);
		}
	}
	
	public File getOutputDir() {
		return outputDir;
	}

}
