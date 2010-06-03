package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.S_UNDER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cif.CrystalEyeUtils.CompoundClass;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.ConnectionTableTool;
import org.xmlcml.cml.tools.CrystalTool;
import org.xmlcml.cml.tools.DisorderTool;
import org.xmlcml.cml.tools.DisorderToolControls;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.cml.tools.StereochemistryTool;
import org.xmlcml.cml.tools.ValencyTool;
import org.xmlcml.cml.tools.DisorderToolControls.ProcessControl;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.molutil.ChemicalElement;


public class RawCML2CompleteCMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(RawCML2CompleteCMLConverter.class);

	private CMLMolecule molecule;
	
	public Type getInputType() {
		return Type.CML;
	}
	
	public Type getOutputType() {
		return Type.CML;
	}
	
	/**
	 * converts a CIF object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(Element rawCml) {
		return processCif(rawCml);
	}

	/**
	 * Process a single non-splitable cif.
	 * @param id molecule id string
	 * @param pathMinusMime path to root for this cif file
	 */
	private Element processCif(Element rawCml) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			CMLUtil.debug(rawCml, os, 0);
		} catch (IOException e1) {
			runtimeException("BUG", e1);
		}
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		CMLCml cml = null;
		try {
			cml = (CMLCml) new CMLBuilder().build(is).getRootElement();
		} catch (Exception e1) {
			runtimeException("bad CML element: "+e1.getMessage(), e1);
		}
		try {
			os.close();
			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		os = null;
		is = null;
		
		try {
			molecule = getMolecule(cml);
		} catch (Exception e) {
			LOG.error("Cannot get molecule", e);
			return null;
		}
		// don't want to do molecules that are too large, so if > 1000 atoms, then pass
		if (molecule.getAtomCount() > 1000) {
			return null;
		}

		CompoundClass compoundClass = CrystalEyeUtils.getCompoundClass(molecule);
		addCompoundClass((CMLCml)cml, compoundClass);
		addSpaceGroupMultiplicities();
		try {
			processDisorder(molecule, compoundClass);
			CMLMolecule mergedMolecule = null;
			try {
				mergedMolecule = createFinalStructure(molecule, compoundClass);
			} catch (Exception e) {
				warn("Could not calculate final structure.");
				return null;
			}
			boolean isPolymeric = false;
			if (!compoundClass.equals(CompoundClass.INORGANIC)) {
				// if the structure is a polymeric organometal then we want to add 
				// all atoms to the unit cell1
				isPolymeric = isPolymericOrganometal(molecule, mergedMolecule, compoundClass);
				if (!isPolymeric) {
					isPolymeric = isSiO4(mergedMolecule);
				}
				if (isPolymeric) {
					CrystalTool crystalTool = new CrystalTool(molecule);
					mergedMolecule = crystalTool.addAllAtomsToUnitCell(true);
					addPolymericFlag(mergedMolecule);
				}
				if (!isPolymeric) {
					calculateBondsAnd3DStereo(cml, mergedMolecule);
					rearrangeChiralAtomsInBonds(mergedMolecule);
					add2DStereoSMILESAndInChI(mergedMolecule, compoundClass);
				}
			}
			// need to replace the molecule created from atoms explicit in the CIF with mergedMolecule.
			molecule.detach();
			cml.appendChild(mergedMolecule);
			repositionCMLCrystalElement(cml);
			
		} catch (RuntimeException e) {
			runtimeException("Error creating complete CML: ", e);
		}
		return cml;
	}

	/** add spg. multiplicities to atoms.
	 * only include if > 1
	 */
	private void addSpaceGroupMultiplicities() {
		CrystalTool crystalTool = new CrystalTool(molecule);
		crystalTool.annotateSpaceGroupMultiplicities();
	}

	/**
	 * Getter for the molecule
	 * @param cml
	 * @return
	 */
	private CMLMolecule getMolecule(CMLElement cml) {
		Nodes moleculeNodes = cml.query(CMLMolecule.NS, CML_XPATH);
		if (moleculeNodes.size() != 1) {
			//cml.debug("NO MOL???");
			runtimeException("NO MOLECULE FOUND");
		}
		return (CMLMolecule) moleculeNodes.get(0);
	}	
	
	/**
	 * Appends to CML
	 * @param cml
	 * @param compoundClass
	 */
	private void addCompoundClass(CMLCml cml, CompoundClass compoundClass) {
		Element compClass = new Element("scalar", CML_NS);
		compClass.addAttribute(new Attribute("dataType", "xsd:string"));
		compClass.addAttribute(new Attribute("dictRef", "iucr:compoundClass"));
		compClass.appendChild(new Text(compoundClass.toString()));
		cml.appendChild(compClass);
	}
	
	/**
	 * Tries to remove disorder from the structure
	 * @param molecule
	 * @param compoundClass
	 */
	private void processDisorder(CMLMolecule molecule, CompoundClass compoundClass) {
		// sort disorder out per molecule rather than per crystal.  This way if the disorder is
		// invalid for one molecule, we may be able to resolve others within the crystal.
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.createCartesiansFromFractionals();
		moleculeTool.calculateBondedAtoms();
		ConnectionTableTool ct = new ConnectionTableTool(molecule);
		// don't want to partition inorganics before resolving disorder as
		// chance is that atoms related by disorder won't be connected so partitioning
		// and doing molecule by molecule is a bad idea.
		if (!CompoundClass.INORGANIC.equals(compoundClass)) {
			ct.partitionIntoMolecules();
		}
		for (CMLMolecule mo : molecule.getDescendantsOrMolecule()) {
			try {
				DisorderToolControls dm = new DisorderToolControls(ProcessControl.LOOSE);
				DisorderTool dt = new DisorderTool(mo, dm);
				dt.resolveDisorder();
			} catch (RuntimeException e) {
				warn("disorder problem "+e.getLocalizedMessage());
			}
		}
		ct.flattenMolecules();
	}

	/**
	 * Calculates the molecular skeleton from the RawCML data
	 * @param molecule
	 * @param compoundClass
	 * @return
	 * @throws Exception
	 */
	private CMLMolecule createFinalStructure(CMLMolecule molecule,
											CompoundClass compoundClass) throws Exception {
		CrystalTool crystalTool = new CrystalTool(molecule);
		CMLMolecule mergedMolecule = null;
		if (compoundClass.equals(CompoundClass.INORGANIC)) {
			mergedMolecule = crystalTool.addAllAtomsToUnitCell(true);
		} else {
			mergedMolecule = crystalTool.calculateCrystallochemicalUnit(new RealRange(0, 3.3 * 3.3));
		}
		setMoleculeIds(mergedMolecule);
		return mergedMolecule;
	}
	
	private void setMoleculeIds(CMLMolecule molecule) {
		List<CMLMolecule> molList = molecule.getDescendantsOrMolecule();
		String moietyPrefix = "moiety_";
		if (molList.size() == 1) {
			molList.get(0).setId("container");
		} else {
			molecule.setId("container");
			int count = 1;
			for (CMLMolecule childMol : molList) {
				childMol.setId(moietyPrefix+count);
				count++;
			}
		}
	}
	
	/**
	 * Calculate whether organometallic structure is polymeric
	 * @param originalMolecule
	 * @param mergedMolecule
	 * @param compoundClass
	 * @return
	 */
	private boolean isPolymericOrganometal(CMLMolecule originalMolecule,
											CMLMolecule mergedMolecule,
											CompoundClass compoundClass) {
		boolean isPolymeric = false;
		if (compoundClass.equals(CompoundClass.ORGANOMETALLIC)) {
			/*
			 * checking for polymeric organometallic structures
			 * to be polymeric we test for the following things:
			 * 1. after symmetry molecule generation, a new metal position must have been generated.
			 * 2. if so, we check for either of the following:
			 *    a. one of the metals in the original molecule having a new bond to an atom with a new id
			 *    b. one of the 'new' bonds generated is also found elsewhere in the generated molecules where 
			 *       both atoms have new IDs.
			 *    If either of the last two points are true, then the structure is polymeric. 
			 * 
			 */	
			List<CMLAtom> originalMetalAtomList = new ArrayList<CMLAtom>();
			if (compoundClass.equals(CompoundClass.ORGANOMETALLIC)) {
				for (CMLAtom atom : originalMolecule.getAtoms()) {
					if (atom.getChemicalElement().isChemicalElementType(ChemicalElement.Type.METAL)) {
						originalMetalAtomList.add(atom);
					}
				}
			}
			List<String> newMetalAtomIdList = new ArrayList<String>();
			for (CMLAtom atom : mergedMolecule.getAtoms()) {
				if (atom.getChemicalElement().isChemicalElementType(ChemicalElement.Type.METAL)) {
					newMetalAtomIdList.add(atom.getId());
				}
			}
			if (newMetalAtomIdList.size() > originalMetalAtomList.size()) {
				// check old atoms for bonds to atoms with new IDs (as described in point 'a' above)
				for (CMLAtom atom : originalMolecule.getAtoms()) {
					if (isPolymeric) break;
					String atomId = atom.getId();
					Set<String> origSet = new HashSet<String>();
					for (CMLAtom ligand : atom.getLigandAtoms()) {
						origSet.add(ligand.getId());
					}
					List<CMLAtom> ligandAtoms = mergedMolecule.getAtomById(atomId).getLigandAtoms();
					if (ligandAtoms.size() > origSet.size()) {
						List<String> idList = new ArrayList<String>();
						for (CMLAtom ligand : ligandAtoms) {
							String idStart = getPreUnderscoreStringInAtomId(ligand.getId());
							if (idStart == null) {
								idList.add(ligand.getId());
							} else {
								idList.add(idStart);
							}
						}
						Collections.sort(idList);
						for (CMLAtom a : mergedMolecule.getAtoms()) {
							String aStart = getPreUnderscoreStringInAtomId(a.getId());
							if (aStart != null) {
								if (aStart.equals(atomId)) {
									List<String> ligandIdList = new ArrayList<String>();
									for (CMLAtom l : a.getLigandAtoms()) {
										String lStart = getPreUnderscoreStringInAtomId(l.getId());
										if (lStart == null) {
											ligandIdList.add(l.getId());
										} else {
											ligandIdList.add(lStart);
										}
									}
									Collections.sort(ligandIdList);
									if (!idList.equals(ligandIdList)) {
										isPolymeric = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return isPolymeric;
	}

	/**
	 * get Pre Underscore String In Atom Id
	 * @param id
	 * @return
	 */
	private String getPreUnderscoreStringInAtomId(String id) {
		if (id.contains(S_UNDER)) {
			return id.substring(0,id.indexOf(S_UNDER));		
		} else {
			return null;
		}
	}
	
	/**
	 * Special case of SiO4 requires iterating twice
	 * @param molecule
	 * @return
	 */
	private boolean isSiO4(CMLMolecule molecule) {
		boolean is = false;
		int overall = 0;
		for (CMLAtom atom : molecule.getAtoms()) {
			if ("Si".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 4) {
				int count = 0;
				for (CMLAtom lig : atom.getLigandAtoms()) {
					if ("O".equals(lig.getElementType())) {
						count++;
					}
				}
				if (count == 4) overall++;
			}
			if (overall >= 5) is = true;
		}
		return is;
	}

	/**
	 * Add flag to say whether the structure is polymeric or not
	 * @param molecule
	 */
	private void addPolymericFlag(CMLMolecule molecule) {
		CMLMetadataList ml = (CMLMetadataList)molecule.getFirstCMLChild(CMLMetadataList.TAG);
		if (ml == null) {
			ml = new CMLMetadataList();
			molecule.appendChild(ml);
		}
		CMLMetadata met = new CMLMetadata();
		ml.appendChild(met);
		met.setAttribute("dictRef", CrystalEyeConstants.POLYMERIC_FLAG_DICTREF);
	}	

	/**
	 * Runs methods that bond orders and charges to molecules. Also adds stereo chemistry.
	 * @param cml
	 * @param mergedMolecule
	 */
	private void calculateBondsAnd3DStereo(CMLCml cml, CMLMolecule mergedMolecule) {
		for (CMLMolecule subMol : mergedMolecule.getDescendantsOrMolecule()) {
			boolean success = true;
			Nodes nonUnitOccNodes = subMol.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CML_XPATH);
			if (!DisorderTool.isDisordered(subMol) && !subMol.hasCloseContacts() && nonUnitOccNodes.size() == 0) {
				ValencyTool subMolTool = new ValencyTool(subMol);
				int molCharge = ValencyTool.UNKNOWN_CHARGE;
				if (mergedMolecule.getDescendantsOrMolecule().size() == 1) {
					// if there is only one moiety in the crystal, then must have a charge of 0.
					molCharge = 0;
				} else {
					// if more than one moiety, try and get the charge from the formula provided by the CIF.
					getMoietyChargeFromFormula(cml, subMol);
				}
				success = subMolTool.adjustBondOrdersAndChargesToValency(molCharge);
				if (!success) {
					// tag molecule to say we couldn't find a reasonable set of charges/bond orders for it
					addNoBondsOrChargesSetFlag(subMol);
				}
			} else {
				setAllBondOrders(subMol, CMLBond.SINGLE);
			}
			if (success) {
				// remove metals before adding stereochemistry - otherwise
				// bonds to metal confuse the tool
				Map<List<CMLAtom>, List<CMLBond>> metalMap = ValencyTool.removeMetalAtomsAndBonds(subMol);
				StereochemistryTool st = new StereochemistryTool(subMol);
				try {
					st.add3DStereo();
				} catch (RuntimeException e) {
					System.err.println("Error adding 3D stereochemistry.");
				}
				ValencyTool.addMetalAtomsAndBonds(subMol, metalMap);
			}
		}
	}	

	/**
	 * Puts chiral atom as the first atom in the bond
	 * @param molecule
	 */
	public void rearrangeChiralAtomsInBonds(CMLMolecule molecule) {
		for (CMLMolecule subMol : molecule.getDescendantsOrMolecule()) {
			StereochemistryTool st = new StereochemistryTool(subMol);
			List<CMLAtom> chiralAtoms = st.getChiralAtoms();
			List<CMLBond> toRemove = new ArrayList<CMLBond>();
			List<CMLBond> toAdd = new ArrayList<CMLBond>();
			for (CMLBond bond : subMol.getBonds()) {
				CMLAtom secondAtom = bond.getAtom(1);
				if (chiralAtoms.contains(secondAtom)) {
					CMLBond newBond = new CMLBond(bond);
					newBond.setAtomRefs2(bond.getAtom(1).getId()+" "+bond.getAtom(0).getId());
					newBond.resetId(bond.getAtom(1).getId()+"_"+bond.getAtom(0).getId());
					toAdd.add(newBond);
					toRemove.add(bond);
				}
			}
			for (CMLBond bond : toRemove) {
				bond.detach();
			}
			for (CMLBond bond : toAdd) {
				subMol.addBond(bond);
			}
		}
	}

	/**
	 * Calculate identifiers for molecule and add then in
	 * @param molecule
	 * @param compoundClass
	 */
	private void add2DStereoSMILESAndInChI(CMLMolecule molecule, CompoundClass compoundClass) {
		if (containsUnknownBondOrder(molecule)) {
			return;
		}

		List<CMLMolecule> molList = molecule.getDescendantsOrMolecule();
		/*
		if (molList.size() > 1) {
			Nodes nonUnitOccNodes = molecule.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CML_XPATH);
			if (!DisorderTool.isDisordered(molecule) && !molecule.hasCloseContacts() && nonUnitOccNodes.size() == 0
					&& hasBondOrdersAndCharges(molecule)) {
				// if mol contains submols (all of which are not disordered!)
				// then we need to generate InChI/SMILES for the containing mol too
				if (CrystalEyeUtils.getNumberOfRings(molecule) < CrystalEyeUtils.MAX_RINGS) {
					calculateAndAddSmilesToMoleculeContainer(molecule);
				}
				InChIGeneratorTool.addInchiToMolecule(molecule);
			}
		}
		*/
		for (CMLMolecule cmlMol : molList) {
			// calculate formula and add in
			try {
				CMLFormula formula = new CMLFormula(cmlMol);
				formula.normalize();
				cmlMol.appendChild(formula);
			} catch (RuntimeException e) {
				warn("Could not generate CMLFormula: "+e.getMessage());
			}

			//FIXME - remove this section and fix CDK instead!
			// calculate the inchi for each sub-molecule and append
			CMLCrystal cryst = (CMLCrystal)cmlMol.getFirstCMLChild(CMLCrystal.TAG);
			CMLCrystal crystCopy = null;
			if (cryst != null) {
				crystCopy = (CMLCrystal)cryst.copy();
				cryst.detach();
			}
			CMLFormula form = (CMLFormula)cmlMol.getFirstCMLChild(CMLFormula.TAG);
			CMLFormula formCopy = null;
			if (form != null) {
				formCopy = (CMLFormula)form.copy();
				form.detach();
			}
			/*-----end section to be removed-----*/

			Nodes nonUnitOccNodes = cmlMol.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CML_XPATH);
			if (!DisorderTool.isDisordered(cmlMol) && !cmlMol.hasCloseContacts() && nonUnitOccNodes.size() == 0
					&& hasBondOrdersAndCharges(cmlMol)) {
				try {
					CDKUtils.add2DCoords(cmlMol);
					new StereochemistryTool(cmlMol).addWedgeHatchBonds();
				} catch (Exception e) {
					warn("Exception adding wedge/hatch bonds to molecule "+cmlMol.getId());
				}
				/*
				if (!compoundClass.equals(CompoundClass.INORGANIC) && 
						(CrystalEyeUtils.getNumberOfRings(cmlMol) < CrystalEyeUtils.MAX_RINGS)) {
					calculateAndAddSmiles(cmlMol);
				}
				InChIGeneratorTool.addInchiToMolecule(molecule);
				*/
			}
			//FIXME - remove this section and fix CDK instead!
			if (formCopy != null) cmlMol.appendChild(formCopy);
			if (crystCopy != null) cmlMol.appendChild(crystCopy);
			//------------------------------
			/*-----end section to be removed------*/
		}	
	}

	/**
	 * Repositions the crystal element to be the first child of the molecule
	 * @param cml
	 */
	private void repositionCMLCrystalElement(CMLCml cml) {
		Nodes crystalNodes = cml.query(".//"+CMLCrystal.NS, CML_XPATH);
		if (crystalNodes.size() > 0) {
			CMLCrystal crystal = (CMLCrystal)crystalNodes.get(0);
			CMLCrystal crystalC = (CMLCrystal)crystal.copy();
			crystal.detach();
			CMLMolecule molecule = (CMLMolecule)cml.getFirstCMLChild(CMLMolecule.TAG);
			molecule.insertChild(crystalC, 0);
		} else {
			runtimeException("Should have found a CMLCrystal element as child of CMLCml.");
		}
	}

	/**
	 * Looks for flag that says if processing for bond orders and charges has been successful
	 * @param molecule
	 * @return
	 */
	public static boolean hasBondOrdersAndCharges(CMLMolecule molecule) {
		boolean hasBOAC = true;
		Nodes flagNodes = molecule.query(".//"+CMLMetadata.NS+"[@dictRef='"+CrystalEyeConstants.NO_BONDS_OR_CHARGES_FLAG_DICTREF+"']", CML_XPATH);
		if (flagNodes.size() > 0) {
			hasBOAC = false;
		}
		return hasBOAC;
	}

	/**
	 * Add flag says if processing for bond orders and charges has been successful
	 * @param molecule
	 */
	private void addNoBondsOrChargesSetFlag(CMLMolecule molecule) {
		CMLMetadataList ml = (CMLMetadataList)molecule.getFirstCMLChild(CMLMetadataList.TAG);
		if (ml == null) {
			ml = new CMLMetadataList();
			molecule.appendChild(ml);
		}
		CMLMetadata met = new CMLMetadata();
		ml.appendChild(met);
		met.setAttribute("dictRef", CrystalEyeConstants.NO_BONDS_OR_CHARGES_FLAG_DICTREF);
	}
	
	/**
	 * Does the molecule contain a bond order that hasn't been found
	 * @param molecule
	 * @return
	 */
	private boolean containsUnknownBondOrder(CMLMolecule molecule) {
		boolean b = false;
		for (CMLBond bond : molecule.getBonds()) {
			if (CMLBond.UNKNOWN_ORDER.equals(bond.getOrder())) {
				b = true;
				break;
			}
		}
		return b;
	}

	/**
	 * Compares a molecule against a formula to find what the charge is for the molecule
	 * @param cml
	 * @param molecule
	 * @return
	 */
	private int getMoietyChargeFromFormula(CMLCml cml, CMLMolecule molecule) {
		int molCharge = ValencyTool.UNKNOWN_CHARGE;
		Nodes moiFormNodes = cml.query(".//"+CMLFormula.NS+"[@dictRef='iucr:_chemical_formula_moiety']", CML_XPATH);
		CMLFormula moietyFormula = null;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		if (moiFormNodes.size() > 0) {
			moietyFormula = (CMLFormula)moiFormNodes.get(0);
			// get a list of formulas for the moieties. 
			List<CMLFormula> moietyFormulaList = new ArrayList<CMLFormula>();
			if (moietyFormula != null) {
				moietyFormulaList = moietyFormula.getFormulaElements().getList();
				if (moietyFormulaList.size() == 0) {
					moietyFormulaList.add(moietyFormula);
				}
				for (CMLFormula formula : moietyFormulaList) {
					CMLFormula molForm = moleculeTool.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
					if (molForm.getConciseNoCharge().equals(formula.getConciseNoCharge())) {
						molCharge = formula.getFormalCharge();
					}
				}
			}
		}
		return molCharge;
	}

	/**
	 * Set all bond orders in the molecule to the order provided
	 * @param molecule
	 * @param order
	 */
	private void setAllBondOrders(CMLMolecule molecule, String order) {
		for (CMLBond bond : molecule.getBonds()) {
			bond.setOrder(order);
		}
	}


	@Override
	public int getConverterVersion() {
		return 0;
	}
}
