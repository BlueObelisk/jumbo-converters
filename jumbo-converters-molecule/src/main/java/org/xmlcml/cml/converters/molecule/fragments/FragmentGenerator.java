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

	public FragmentGenerator() {
		;
	}

	public void processCml(CMLCml cml) {
		Nodes classNodes = cml.query(".//cml:scalar[@dictRef='iucr:compoundClass']", CMLConstants.CML_XPATH);
		String compClass = "";
		if (classNodes.size() > 0) {
			compClass = ((Element)classNodes.get(0)).getValue();
		} else {
			throw new RuntimeException("Molecule should have a compoundClass scalar set.");
		}
		boolean isPolymeric = false;
		Nodes polymericNodes = cml.query(".//"+CMLMetadata.NS+"[@dictRef='"+
				CrystalEyeConstants.POLYMERIC_FLAG_DICTREF+"']", CMLConstants.CML_XPATH);
		if (polymericNodes.size() > 0) {
			isPolymeric = true;
		}

		if (!(compClass.equals(CompoundClass.INORGANIC.toString()) || isPolymeric)) {
			CMLMolecule molecule = (CMLMolecule)cml.getFirstCMLChild(CMLMolecule.TAG);
	
			if (molecule.getAtomCount() < 1000) {
				processStructure(cml, compClass, molecule);
			}
		}
	}

	public void processStructure(CMLCml cml,
			String compClass, CMLMolecule molecule) {
		detachExplicitBondLengthElements(molecule);

		//don't generate image if the molecule is disordered
		List<CMLMolecule> uniqueMolList = CrystalEyeUtils.getUniqueSubMolecules(molecule);	
		int count = 1;
		for (CMLMolecule subMol : uniqueMolList) {	
			count = processDisorder(count, subMol);
		}

		try {
			outputMoieties(molecule, compClass);
		} catch(Exception e) {
			throw new RuntimeException("Error while outputting moieties: ", e);
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
			write2dImages(subMol, "foo.png");
			count++;
		}
		return count;
	}

	private void write2dImages(CMLMolecule molecule, String filename) {
		write2dImage(filename+"small.png", molecule, 358, 278, false);
		write2dImage(filename+".png", molecule, 600, 600, false);
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
		//throw new RuntimeException("fix CDK");
//		CDKUtils.add2DCoords(molecule);
//		Cml2PngTool cp = new Cml2PngTool(molecule);
//		cp.setWidthAndHeight(width, height);
//		cp.renderMolecule(path);
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


	private void outputFragments(int moiCount, File dir, String id, CMLMolecule molecule, String compoundClass,
			String fragType, int depth) {
		removeStereoInformation(molecule);
		List<CMLMolecule> subMoleculeList = molecule.getDescendantsOrMolecule();
		int subMolCount = 0;
		boolean sprout = false;
		for (CMLMolecule subMolecule : subMoleculeList) {
			MoleculeTool mt = MoleculeTool.getOrCreateTool(subMolecule);
			fragmentList = new ArrayList<CMLMolecule>();
			if (CHAIN_NUCLEUS.equals(fragType)) {
				molecule = new CMLMolecule(molecule);
				subMolecule = new CMLMolecule(subMolecule);
				ValencyTool.removeMetalAtomsAndBonds(subMolecule);
				new ConnectionTableTool(subMolecule).partitionIntoMolecules();
				for (CMLMolecule subSubMol : subMolecule.getDescendantsOrMolecule()) {
					fragmentList.addAll(MoleculeTool.getOrCreateTool(subSubMol).getChainMolecules());
				}
			} else if (RING_NUCLEUS.equals(fragType)) {
				molecule = new CMLMolecule(molecule);
				subMolecule = new CMLMolecule(subMolecule);
				ValencyTool.removeMetalAtomsAndBonds(subMolecule);
				new ConnectionTableTool(subMolecule).partitionIntoMolecules();
				for (CMLMolecule subSubMol : subMolecule.getDescendantsOrMolecule()) {
					fragmentList.addAll(MoleculeTool.getOrCreateTool(subSubMol).getRingNucleiMolecules());
				}
				sprout = true;
			} else if (CLUSTER_NUCLEUS.equals(fragType)) {
				List<Type> typeList = new ArrayList<Type>();
				typeList.add(ChemicalElement.Type.METAL);
				fragmentList = mt.createClusters(typeList);
				sprout = true;
			} else if (LIGAND.equals(fragType)) {
				List<Type> typeList = new ArrayList<Type>();
				typeList.add(ChemicalElement.Type.METAL);
				fragmentList = mt.createLigands(typeList);
			} else {
				throw new IllegalArgumentException("Illegal type of fragment.");
			}
			debugFragmentList(moiCount, fragType, fragmentList);
			subMolCount++;
			int count = 0;
			writeFragmentFiles(dir, id, molecule, compoundClass, fragType, depth,
					subMolCount, sprout, subMolecule, mt, count);
		}
	}

	private void debugFragmentList(int moiCount, String fragType, List<CMLMolecule> fragmentList) {
		int serial = 0;
		for (CMLMolecule fragment : fragmentList) {
			File f = new File("target/moiety"+moiCount+"/");
			f.mkdirs();
			CMLUtils.debugToFile(fragment, "target/moiety"+moiCount+"/"+fragType+serial+".cml");
			serial++;
		}
	}

	private void writeFragmentFiles(File dir, String id, CMLMolecule molecule,
			String compoundClass, String fragType, int depth, int subMolCount,
			boolean sprout, CMLMolecule subMolecule, MoleculeTool mt, int count) {
		for (CMLMolecule fragment : fragmentList) {
			count++;
			CMLAtomSet atomSet = new CMLAtomSet(subMolecule, MoleculeTool.getOrCreateTool(fragment).getAtomSet().getAtomIDs());
			writeFragmentFiles(molecule, subMolecule, compoundClass, fragment, fragType, subMolCount, count, 
					dir, id, depth);
			if (sprout) {
				CMLMolecule sproutMol = mt.sprout(atomSet);
				int nucCount = fragment.getAtomCount();
				int spCount = sproutMol.getAtomCount();
				if (nucCount < spCount) {
					writeFragmentFiles(molecule, subMolecule, compoundClass, sproutMol, fragType+SPROUT_1, subMolCount, count, 
							dir, id, depth);
					CMLAtomSet spAtomSet = new CMLAtomSet(subMolecule, MoleculeTool.getOrCreateTool(sproutMol).getAtomSet().getAtomIDs());
					CMLMolecule sprout2Mol = mt.sprout(spAtomSet);
					int sp2Count = sprout2Mol.getAtomCount();
					if (spCount < sp2Count && sp2Count < molecule.getAtomCount()) {
						writeFragmentFiles(molecule, subMolecule, compoundClass, sprout2Mol, fragType+SPROUT_2, subMolCount, count, 
								dir, id, depth);
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

	private void writeFragmentFiles(CMLMolecule originalMolecule, CMLMolecule subMolecule, 
			String compoundClass, CMLMolecule fragment, String fragType, int subMolCount, int count,
			File dir, String id, int depth) {
		CMLMolecule subMolCopy = new CMLMolecule(subMolecule);
		CMLMolecule fragCopy = new CMLMolecule(fragment);
		CMLAtomSet atomSet = new CMLAtomSet(subMolCopy, MoleculeTool.getOrCreateTool(fragCopy).getAtomSet().getAtomIDs());
		CMLMolecule molR = addAndMarkFragmentRAtoms(subMolCopy, atomSet);

		addInChIToRGroupMolecule(molR);
		if (!compoundClass.equals(CompoundClass.INORGANIC.toString())) {
			if (getNumberOfRings(fragCopy) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
				String smiles = SmilesTool.generateSmiles(fragCopy);
				if (smiles != null) {
					addSmiles2Molecule(smiles, molR);
				}
			}
		}

		addAtomSequenceNumbers(molR);
		addDoi(molR);
		molR.setId(subMolecule.getId()+"_"+fragType+"_"+subMolCount+"_"+count);
		String outPath = getOutPath(dir, id, fragType, "", subMolCount, count, CrystalEyeConstants.COMPLETE_CML_MIME);
		Utils.writeXML(new File(outPath), new Document(molR));
		String pathMinusMime = Utils.getPathMinusMimeSet(outPath);
		changeRtoXx(molR);
		write2dImages(molR, pathMinusMime);
	}

	private void outputMoieties(CMLMolecule mergedMolecule, String compoundClass) {
		int moiCount = 0;
		List<CMLMolecule> distinctMolecules = mergedMolecule.getDescendantsOrMolecule();
		LOG.debug("distinct molecules "+distinctMolecules.size());
		for (CMLMolecule mol : distinctMolecules) {
			LOG.debug("moiety "+moiCount);
			Nodes nonUnitOccNodes = mol.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CMLConstants.CML_XPATH);
			if (DisorderTool.isDisordered(mol)) {
				LOG.debug("skipped disordered");
			} else if (mol.hasCloseContacts()) {
				LOG.debug("has close contacts");
			} else if (nonUnitOccNodes.size() != 0) {
				LOG.debug("nonUnitOcc nodes non-zero (must find out what this means!)");
			} else if (!CMLUtils.hasBondOrdersAndCharges(mol)) {
				LOG.debug("no bond orders or charges");
			} else {
				moiCount++;
				if (ChemistryUtils.isBoringMolecule(mol)) continue;
				detachAnyCrystalNodesFromMolecule(mol);
				addDoi(mol);
//				mol.debug("moiety "+moiCount);
				CMLUtils.debugToFile(mol, "target/moiety"+moiCount+".cml");
				outputImages(mol);
				addAtomSequenceNumbers(mol);
				// remove atom sequence numbers from mol now it has been written so they
				// don't interfere with the fragments that are written
				removeAtomsWithoutSequenceNumbers(mol);

				outputFragments(moiCount, compoundClass, mol);
			}
			
		}
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

	private void outputImages(CMLMolecule mol) {
		String filename = "foo";
		String smallPngPath = filename+".small.png";
		String pngPath = filename+".png";
		write2dImage(pngPath, mol, 600, 600, true);
		write2dImage(smallPngPath, mol, 358, 278, true);
	}

	private void outputFragments(int moiCount, String compoundClass, CMLMolecule mol) {
		String id = "bar";
		File moiDir = null;
		File fragmentDir = new File(moiDir, "fragments");
		outputFragments(moiCount, fragmentDir, id, mol, compoundClass, CHAIN_NUCLEUS, 7);
		outputFragments(moiCount, fragmentDir, id, mol, compoundClass, RING_NUCLEUS, 7);
		outputFragments(moiCount, fragmentDir, id, mol, compoundClass, CLUSTER_NUCLEUS, 7);
		outputFragments(moiCount, fragmentDir, id, mol, compoundClass, LIGAND, 7);
		outputAtomCenteredSpecies(moiCount, fragmentDir, id, mol, compoundClass);
	}

	private void outputAtomCenteredSpecies(int moiCount, File dir, String id, CMLMolecule mergedMolecule, String compoundClass) {
		List<CMLMolecule> subMoleculeList = mergedMolecule.getDescendantsOrMolecule();
		int subMol = 0;
		for (CMLMolecule subMolecule : subMoleculeList) {
			CMLMolecule subMolCopy = new CMLMolecule(subMolecule);
			MoleculeTool subMoleculeTool = MoleculeTool.getOrCreateTool(subMolecule);
			subMol++;
			List<CMLAtom> atoms = subMolecule.getAtoms();
			int atomCount = 0;
			for (CMLAtom atom : atoms) {
				ChemicalElement element = atom.getChemicalElement();
				if (element.isChemicalElementType(Type.METAL)) {
					atomCount++;
					Set<CMLAtom> atomSet = new HashSet<CMLAtom>();
					atomSet.add(atom);
					CMLAtomSet singleAtomSet = new CMLAtomSet(atomSet);
					CMLMolecule atomR = addAndMarkFragmentRAtoms(subMolCopy, singleAtomSet);
					CMLMolecule atomMol = new CMLMolecule();
					atomMol.addAtom((CMLAtom)atom.copy());

					if (!compoundClass.equals(CompoundClass.INORGANIC.toString())) {
						addInchiAndSmiles(atomR, atomMol);
					}

					addAtomSequenceNumbers(atomR);
					addDoi(atomR);
					atomR.setId(subMolecule.getId()+"_atom-nuc_"+subMol+"_"+atomCount);
					String outPath = getOutPath(dir, id, "atom-nuc", "", subMol, atomCount, CrystalEyeConstants.COMPLETE_CML_MIME);
					Utils.writeXML(new File(outPath), new Document(atomR));
					String pathMinusMime = Utils.getPathMinusMimeSet(outPath);
					changeRtoXx(atomR);
					write2dImages(atomR, pathMinusMime);

					//sprout once
					CMLMolecule sprout = subMoleculeTool.sprout(singleAtomSet);
					CMLAtomSet spAtomSet = new CMLAtomSet(subMolecule, MoleculeTool.getOrCreateTool(sprout).getAtomSet().getAtomIDs());

					CMLMolecule sproutR = addAndMarkFragmentRAtoms(subMolCopy, spAtomSet);
					if (sproutR.getAtomCount() == sprout.getAtomCount()) {
						continue;
					}
					// need to calculated inchi and smiles before R groups added
					addInChIToRGroupMolecule(sproutR);
					if (!compoundClass.equals(CompoundClass.INORGANIC.toString())) {
						if (getNumberOfRings(sprout) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
							String sproutSmiles= SmilesTool.generateSmiles(sprout);
							addSmiles2Molecule(sproutSmiles, sproutR);
						}
					}

					addAtomSequenceNumbers(sproutR);
					addDoi(sproutR);
					sproutR.setId(subMolecule.getId()+_ATOM_NUC_SPROUT_1+subMol+"_"+atomCount);
					outPath = getOutPath(dir, id, ATOM_NUC_SPROUT_1, "", subMol, atomCount, 
							CrystalEyeConstants.COMPLETE_CML_MIME);
					pathMinusMime = Utils.getPathMinusMimeSet(outPath);	          

					Utils.writeXML(new File(outPath), new Document(sproutR));
					changeRtoXx(sproutR);
					write2dImages(sproutR, pathMinusMime);

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
						if (!compoundClass.equals(CompoundClass.INORGANIC.toString())) {
							// need to calculated inchi and smiles before R groups added
							if (getNumberOfRings(sprout2) < CrystalEyeConstants.MAX_RINGS_FOR_SMILES_CALCULATION) {
								String sprout2Smiles = SmilesTool.generateSmiles(sprout2);
								addSmiles2Molecule(sprout2Smiles, sprout2R);
							}
						}

						addAtomSequenceNumbers(sprout2R);
						addDoi(sprout2R);
						sprout2R.setId(subMolecule.getId()+"_atom-nuc-sprout-2_"+subMol+"_"+atomCount);

						outPath = getOutPath(dir, id, "atom-nuc-sprout-2", "", subMol, atomCount, CrystalEyeConstants.COMPLETE_CML_MIME);
						pathMinusMime = Utils.getPathMinusMimeSet(outPath);
						Utils.writeXML(new File(outPath), new Document(sprout2R));
						changeRtoXx(sprout2R);
						write2dImages(sprout2R, pathMinusMime);
					}
				}
			}
		}
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
		String inchi = InchiTool.generateInchi(molecule, "");
		CMLIdentifier identifier = new CMLIdentifier();
		identifier.setConvention("iupac:inchi");
		identifier.appendChild(new Text(inchi));
		molecule.appendChild(identifier);
	}

}
