package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.S_UNDER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cif.CrystalEyeUtils.CompoundClass;
import org.xmlcml.cml.converters.cif.dict.CifDictionaryBuilder;
import org.xmlcml.cml.converters.cif.dict.units.UnitDictionaries;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.ConnectionTableTool;
import org.xmlcml.cml.tools.CrystalTool;
import org.xmlcml.cml.tools.DisorderTool;
import org.xmlcml.cml.tools.DisorderToolControls;
import org.xmlcml.cml.tools.DisorderToolControls.ProcessControl;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.StereochemistryTool;
import org.xmlcml.cml.tools.ValencyTool;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.molutil.ChemicalElement;

public class RawCML2CompleteCMLConverter extends AbstractConverter {

    private static final Logger LOG = Logger.getLogger(RawCML2CompleteCMLConverter.class);

    public static final String POLYMERIC_FLAG_DICTREF = "ned24:isPolymeric";
    public static final String NO_BONDS_OR_CHARGES_FLAG_DICTREF = "ned24:noBondsOrChargesSet";

	public static final String REG_MESSAGE = "Raw CIF CML to CML converter";

    private CIFDictionary cifDict = CIFDictionary.getInstance();


    public Type getInputType() {
        return Type.CML;
    }

    public Type getOutputType() {
        return Type.CML;
    }

    /**
     * converts a CIF object to CML. returns cml:cml/cml:molecule
     *
     * @param rawCml
     */
    public Element convertToXML(Element rawCml) {
        return processCif(rawCml);
    }

    /**
     * Process a single non-splitable cif.
     *
     * @param rawCml
     */
    private Element processCif(Element rawCml) {
        CMLCml cml = readRawCml(rawCml);

        List<CMLModule> globalModules = new ArrayList<CMLModule>();
        List<CMLModule> structureModules = new ArrayList<CMLModule>();
        for (CMLModule module : getModules(cml)) {
            String role = module.getRole();
            if ("global".equals(role)) {
                globalModules.add(module);
            }
            else if ("structure".equals(role)) {
                structureModules.add(module);
            } else {
                LOG.warn("Unknown module role: "+role);
            }
        }

        List<CMLModule> crystals = processCrystalStructures(globalModules, structureModules);
        CMLCml root = generateRoot(crystals);
        return root;
    }

    private CMLCml generateRoot(List<CMLModule> crystals) {
        CMLCml root = new CMLCml();
        for (CMLModule module : crystals) {
            for (int i = 0; i < module.getNamespaceDeclarationCount(); i++) {
                String prefix = module.getNamespacePrefix(i);
                String uri = module.getNamespaceURI(prefix);
                root.addNamespaceDeclaration(prefix, uri);
            }
            root.appendChild(module);
        }
        return root;
    }

    private List<CMLModule> processCrystalStructures(List<CMLModule> globalModules, List<CMLModule> structureModules) {
        List<CMLModule> crystals = new ArrayList<CMLModule>();
        for (CMLModule module : structureModules) {

            CMLModule output = createModules(module, globalModules);
            if (output != null) {
                crystals.add(output);
            }

        }
        return crystals;
    }

    private List<CMLModule> getModules(CMLCml cml) {
        Nodes nodes = cml.query("cml:module", CML_XPATH);
        List<CMLModule> modules = new ArrayList<CMLModule>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            modules.add((CMLModule) nodes.get(i));
        }
        return modules;
    }


    private CMLCml readRawCml(Element rawCml) {
        ByteArrayOutputStream os = null;
        ByteArrayInputStream is = null;
        CMLCml cml = null;

        try {
            os = new ByteArrayOutputStream();
            CMLUtil.debug(rawCml, os, 0);
            is = new ByteArrayInputStream(os.toByteArray());
            cml = (CMLCml) new CMLBuilder().build(is).getRootElement();
        } catch (Exception e1) {
            runtimeException("bad CML element: " + e1.getMessage(), e1);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
        return cml;
    }

    private CMLModule createModules(CMLModule module, List<CMLModule> globalModules) {
        CMLMolecule molecule = (CMLMolecule) module.getFirstCMLChild("molecule");
        if (molecule.getAtomCount() > 1000) {
            return null;
        }

        CMLModule crystallographyExperiment = new CMLModule();
        crystallographyExperiment.addNamespaceDeclaration("convention", "http://www.xml-cml.org/convention/");
        crystallographyExperiment.setConvention("convention:crystallographyExperiment");
        crystallographyExperiment.addNamespaceDeclaration(CifDictionaryBuilder.PREFIX, CifDictionaryBuilder.URI);
        for(UnitDictionaries dict : UnitDictionaries.values()) {
            crystallographyExperiment.addNamespaceDeclaration(dict.prefix, dict.URI);
        }

        for (CMLModule global : globalModules) {
            copyChildElements(global, crystallographyExperiment);
        }
        Elements children = module.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element element = children.get(i);
            if ("molecule".equals(element.getLocalName()) && CML_NS.equals(element.getNamespaceURI())) {
                continue;
            }
            if ("crystal".equals(element.getLocalName()) && CML_NS.equals(element.getNamespaceURI())) {
                continue;
            }
            Element copy = (Element) element.copy();
            crystallographyExperiment.appendChild(copy);
        }

        CMLCrystal crystal = (CMLCrystal) module.getFirstCMLChild("crystal");

        CompoundClass compoundClass = CIF2CMLUtils.getCompoundClass(molecule);
        addCompoundClass(crystallographyExperiment, compoundClass);

        addSpaceGroupMultiplicities(molecule, crystal);
        try {
            processDisorder(molecule, crystal, compoundClass);
            CMLMolecule mergedMolecule = null;
            try {
                mergedMolecule = createFinalStructure(molecule, crystal, compoundClass);
            } catch (Exception e) {
                e.printStackTrace();
                warn("Could not calculate final structure.");
                return null;
            }


            boolean isPolymeric = false;
            if (!compoundClass.equals(CompoundClass.INORGANIC)) {
                // if the structure is a polymeric organometal then we want
                // to add
                // all atoms to the unit cell1
                isPolymeric = isPolymericOrganometal(molecule, mergedMolecule, compoundClass);
                if (!isPolymeric) {
                    isPolymeric = isSiO4(mergedMolecule);
                }
                if (isPolymeric) {
                    CrystalTool crystalTool = new CrystalTool(molecule, crystal);
                    mergedMolecule = crystalTool.addAllAtomsToUnitCell(true);
                    addPolymericFlag(mergedMolecule);
                }
                if (!isPolymeric) {
                    calculateBondsAnd3DStereo(crystallographyExperiment, mergedMolecule);
                    rearrangeChiralAtomsInBonds(mergedMolecule);
                    add2DStereoSMILESAndInChI(mergedMolecule, compoundClass);
                }
            }

            // need to replace the molecule created from atoms explicit in
            // the CIF with mergedMolecule.

            mergedMolecule.setConvention("convention:molecular");

            CMLModule crystalStructure = new CMLModule();
            crystalStructure.setConvention("convention:crystalStructure");

            crystal.detach();
            crystalStructure.appendChild(crystal);
            crystalStructure.appendChild(mergedMolecule);
            crystallographyExperiment.appendChild(crystalStructure);

        } catch (RuntimeException e) {
            runtimeException("Error creating complete CML: ", e);
        }

        updateProperties(crystallographyExperiment);
        makeCMLLiteCompatible(crystallographyExperiment);

        return crystallographyExperiment;
    }

    private void updateProperties(CMLModule crystallographyExperiment) {
        Nodes nodes = crystallographyExperiment.query("//cml:property", CML_XPATH);
        for (int i = 0; i < nodes.size(); i++) {
            CMLProperty property = (CMLProperty) nodes.get(i);
            fetchDataType(property);

//            String id = property.getDictRef();
//            if (id != null && id.startsWith("iucr:")) {
//                id = id.substring(5);
//                CMLEntry dictEntry = idMap.get(id);
//                if (dictEntry != null && dictEntry.getTerm() != null) {
//                    property.setTitle(dictEntry.getTerm());
//                }
//            }
        }
    }

    protected void fetchDataType(CMLElement element) {
        Attribute dictURI = element.getAttribute("dictRef");
        // If no dictRef, don't do anything
        if (dictURI == null) {
            return;
        }
        String dictRef = dictURI.getValue();
        int pos = dictRef.indexOf(':');
        if (pos == -1) {
//            logger.error("Cannot dereference non-namespaced reference: " + dictRef);
            return;
        }
        String prefix = dictRef.substring(0, pos);
        String id = dictRef.substring(pos + 1);

        if (!CifDictionaryBuilder.PREFIX.equals(prefix)) {
//            logger.error("Dictionary Prefix: " + CifDictionaryBuilder.PREFIX + " does not match " + prefix + " from file");            return;
        }

        String unit = cifDict.getUnits(id);
        String type = cifDict.getDataType(id);

        Elements elems = element.getChildCMLElements("scalar");
        for (int x = 0; x < elems.size(); x++) {
            CMLScalar scalar = (CMLScalar) elems.get(x);
            if(scalar.getValue().equals(".")){
                scalar.detach();
            }

            if(unit!=null){
                scalar.setUnits(unit);
            }
            if ("xsd:float".equals(type) || "xsd:double".equals(type)) {
                try {
                    double d = getDoubleFromString(scalar.getValue());
                    Double e = getErrorFromString(scalar.getValue());
                    scalar.setValue(d);
                    if (e != null) {
                        scalar.setErrorValue(e);
                    }
                } catch (NumberFormatException ex) {
                    warn("Error parsing number: "+scalar.getValue()+ " ["+dictRef+"]");
                }

            } else if ("xsd:string".equals(type)) {
                String s = scalar.getValue();
                scalar.setValue(s);
            }
        }
        if(element.getCMLChildCount("scalar")==0){
            if (element instanceof CMLProperty) {
                element.detach();
            }
        }
    }

    protected Double getErrorFromString(String value) {
        int x = value.indexOf('(');
        int y = value.indexOf('.');
        if(x==-1){
            return null;
        }
        int error = Integer.parseInt(value.substring(x+1,value.indexOf(')')));

        int exponent;
        if(y==-1){
            exponent=0;
        }
        else{
            exponent=y-(x-1);
        }
        double d= error*Math.pow(10, exponent);
        return d;
    }

    protected double getDoubleFromString(String value) {
        int x = value.indexOf('(');
        String number = value.substring(0, (x == -1) ? value.length() : x);
        double d = Double.parseDouble(number);
        return d;
    }





    private void copyChildElements(CMLElement source, CMLElement target) {
        Elements elements = source.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Element copy = (Element) element.copy();
            target.appendChild(copy);
        }
    }

    private void makeCMLLiteCompatible(CMLModule cml) {
        removeAtomArraysFromFormulae(cml);
        removeUserCyclicsFromBonds(cml);
        normaliseBondOrders(cml);
        addMoleculeCounts(cml);
        addDictionaryNamespaces(cml);
        addBondStereoConventions(cml);
    }

    private void addBondStereoConventions(CMLElement cml) {
        for (Node node : CMLUtil.getQueryNodes(cml, ".//cml:bondStereo", CML_XPATH)) {
            CMLBondStereo bs = (CMLBondStereo) node;
            bs.setConvention("cmlDict:wedgehatch");
        }
    }

    private void addDictionaryNamespaces(CMLElement cml) {
        cml.addNamespaceDeclaration("cmlDict", "http://www.xml-cml.org/dictionary/cml/");
    }

    private void addMoleculeCounts(CMLElement cml) {
        for (Node node : CMLUtil.getQueryNodes(cml, ".//cml:molecule/cml:molecule", CML_XPATH)) {
            CMLMolecule mol = (CMLMolecule) node;
            mol.setCount("1");
        }
    }

    private void normaliseBondOrders(CMLElement cml) {
        for (Node node : CMLUtil.getQueryNodes(cml, ".//cml:bond/@order", CML_XPATH)) {
            Attribute att = (Attribute) node;
            String order = att.getValue();
            if ("1".equals(order)) {
                att.setValue("S");
            } else if ("2".equals(order)) {
                att.setValue("D");
            }
            if ("3".equals(order)) {
                att.setValue("T");
            }
        }
    }

    private void removeUserCyclicsFromBonds(CMLElement cml) {
        for (Node node : CMLUtil.getQueryNodes(cml, ".//cml:bond[@userCyclic]", CML_XPATH)) {
            CMLBond bond = (CMLBond) node;
            bond.removeAttribute("userCyclic");
        }
    }

    private void removeAtomArraysFromFormulae(CMLElement cml) {
        for (Node node : CMLUtil.getQueryNodes(cml, ".//cml:formula", CML_XPATH)) {
            CMLFormula formula = (CMLFormula) node;
            Element atomArray = formula.getFirstCMLChild(CMLAtomArray.TAG);
            if (atomArray != null) {
                formula.removeChild(atomArray);
            }
        }
    }

    /**
     * add spg. multiplicities to atoms. only include if > 1
     */
    private void addSpaceGroupMultiplicities(CMLMolecule molecule, CMLCrystal crystal) {
        CrystalTool crystalTool = new CrystalTool(molecule, crystal);
        crystalTool.annotateSpaceGroupMultiplicities();
    }

    /**
     * Getter for the molecule
     *
     * @param cml
     * @return
     * @throws ConverterException
     */
    private CMLMolecule getMolecule(CMLElement cml) {
        Nodes moleculeNodes = cml.query(CMLMolecule.NS, CML_XPATH);
        if (moleculeNodes.size() != 1) {
            this.getConverterLog().addToLog(Level.ERROR, "No molecule found");
            throw new RuntimeException("No molecule found");
        }
        return (CMLMolecule) moleculeNodes.get(0);
    }

    /**
     * Appends to CML
     *
     * @param target
     * @param compoundClass
     */
    private void addCompoundClass(CMLElement target, CompoundClass compoundClass) {
        CMLProperty property = new CMLProperty();
        property.setDictRef("iucr:compoundClass");
        CMLScalar scalar = new CMLScalar();
        property.appendChild(scalar);
        scalar.setDataType("xsd:string");
        scalar.appendChild(compoundClass.toString());
        target.appendChild(property);
    }

    /**
     * Tries to remove disorder from the structure
     *
     */
    private void processDisorder(CMLMolecule molecule, CMLCrystal crystal, CompoundClass compoundClass) {
        // sort disorder out per molecule rather than per crystal. This way if
        // the disorder is
        // invalid for one molecule, we may be able to resolve others within the
        // crystal.
        MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
        moleculeTool.createCartesiansFromFractionals(crystal);
        moleculeTool.calculateBondedAtoms();
        ConnectionTableTool ct = new ConnectionTableTool(molecule);
        // don't want to partition inorganics before resolving disorder as
        // chance is that atoms related by disorder won't be connected so
        // partitioning
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
                warn("disorder problem " + e.getLocalizedMessage());
            }
        }
        ct.flattenMolecules();
    }

    /**
     * Calculates the molecular skeleton from the RawCML data
     *
     */
    private CMLMolecule createFinalStructure(CMLMolecule molecule, CMLCrystal crystal, CompoundClass compoundClass){
        CrystalTool crystalTool = new CrystalTool(molecule, crystal);
        CMLMolecule mergedMolecule;
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
                childMol.setId(moietyPrefix + count);
                count++;
            }
        }
    }

    /**
     * Calculate whether organometallic structure is polymeric
     *
     */
    private boolean isPolymericOrganometal(CMLMolecule originalMolecule, CMLMolecule mergedMolecule, CompoundClass compoundClass) {
        boolean isPolymeric = false;
        if (compoundClass.equals(CompoundClass.ORGANOMETALLIC)) {
            /*
             * checking for polymeric organometallic structures to be polymeric
             * we test for the following things: 1. after symmetry molecule
             * generation, a new metal position must have been generated. 2. if
             * so, we check for either of the following: a. one of the metals in
             * the original molecule having a new bond to an atom with a new id
             * b. one of the 'new' bonds generated is also found elsewhere in
             * the generated molecules where both atoms have new IDs. If either
             * of the last two points are true, then the structure is polymeric.
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
                // check old atoms for bonds to atoms with new IDs (as described
                // in point 'a' above)
                for (CMLAtom atom : originalMolecule.getAtoms()) {
                    if (isPolymeric)
                        break;
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
     *
     */
    private String getPreUnderscoreStringInAtomId(String id) {
        if (id.contains(S_UNDER)) {
            return id.substring(0, id.indexOf(S_UNDER));
        } else {
            return null;
        }
    }

    /**
     * Special case of SiO4 requires iterating twice
     *
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
                if (count == 4)
                    overall++;
            }
            if (overall >= 5)
                is = true;
        }
        return is;
    }

    /**
     * Add flag to say whether the structure is polymeric or not
     *
     */
    private void addPolymericFlag(CMLMolecule molecule) {
        CMLMetadataList ml = (CMLMetadataList) molecule.getFirstCMLChild(CMLMetadataList.TAG);
        if (ml == null) {
            ml = new CMLMetadataList();
            molecule.appendChild(ml);
        }
        CMLMetadata met = new CMLMetadata();
        ml.appendChild(met);
        met.setAttribute("dictRef", POLYMERIC_FLAG_DICTREF);
    }

    /**
     * Runs methods that bond orders and charges to molecules. Also adds stereo
     * chemistry.
     *
     */
    private void calculateBondsAnd3DStereo(CMLModule crystallographyExperiment, CMLMolecule mergedMolecule) {
        for (CMLMolecule subMol : mergedMolecule.getDescendantsOrMolecule()) {
            boolean success = true;
            Nodes nonUnitOccNodes = subMol.query(".//" + CMLAtom.NS + "[@occupancy[. < 1]]", CML_XPATH);
            if (!DisorderTool.isDisordered(subMol) && !subMol.hasCloseContacts() && nonUnitOccNodes.size() == 0) {
                ValencyTool subMolTool = new ValencyTool(subMol);
                int molCharge = ValencyTool.UNKNOWN_CHARGE;
                if (mergedMolecule.getDescendantsOrMolecule().size() == 1) {
                    // if there is only one moiety in the crystal, then must
                    // have a charge of 0.
                    molCharge = 0;
                } else {
                    // if more than one moiety, try and get the charge from the
                    // formula provided by the CIF.
                    getMoietyChargeFromFormula(crystallographyExperiment, subMol);
                }
                success = subMolTool.adjustBondOrdersAndChargesToValency(molCharge);
                if (!success) {
                    // tag molecule to say we couldn't find a reasonable set of
                    // charges/bond orders for it
                    addNoBondsOrChargesSetFlag(subMol);
                }
            } else {
                setAllBondOrders(subMol, CMLBond.SINGLE_S);
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
     *
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
                    newBond.setAtomRefs2(bond.getAtom(1).getId() + " " + bond.getAtom(0).getId());
                    newBond.resetId(bond.getAtom(1).getId() + "_" + bond.getAtom(0).getId());
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
     *
     */
    private void add2DStereoSMILESAndInChI(CMLMolecule molecule, CompoundClass compoundClass) {
        if (containsUnknownBondOrder(molecule)) {
            return;
        }

        List<CMLMolecule> molList = molecule.getDescendantsOrMolecule();
        /*
         * if (molList.size() > 1) { Nodes nonUnitOccNodes =
         * molecule.query(".//"+CMLAtom.NS+"[@occupancy[. < 1]]", CML_XPATH); if
         * (!DisorderTool.isDisordered(molecule) && !molecule.hasCloseContacts()
         * && nonUnitOccNodes.size() == 0 && hasBondOrdersAndCharges(molecule))
         * { // if mol contains submols (all of which are not disordered!) //
         * then we need to generate InChI/SMILES for the containing mol too if
         * (CrystalEyeUtils.getNumberOfRings(molecule) <
         * CrystalEyeUtils.MAX_RINGS) {
         * calculateAndAddSmilesToMoleculeContainer(molecule); }
         * InChIGeneratorTool.addInchiToMolecule(molecule); } }
         */
        for (CMLMolecule cmlMol : molList) {
            // calculate formula and add in
            try {
                CMLFormula formula = new CMLFormula(cmlMol);
                formula.normalize();
                cmlMol.appendChild(formula);
            } catch (RuntimeException e) {
                warn("Could not generate CMLFormula: " + e.getMessage());
            }

            // FIXME - remove this section and fix CDK instead!
            // calculate the inchi for each sub-molecule and append
            CMLCrystal cryst = (CMLCrystal) cmlMol.getFirstCMLChild(CMLCrystal.TAG);
            CMLCrystal crystCopy = null;
            if (cryst != null) {
                crystCopy = (CMLCrystal) cryst.copy();
                cryst.detach();
            }
            CMLFormula form = (CMLFormula) cmlMol.getFirstCMLChild(CMLFormula.TAG);
            CMLFormula formCopy = null;
            if (form != null) {
                formCopy = (CMLFormula) form.copy();
                form.detach();
            }
            /*-----end section to be removed-----*/

            Nodes nonUnitOccNodes = cmlMol.query(".//" + CMLAtom.NS + "[@occupancy[. < 1]]", CML_XPATH);
            if (!DisorderTool.isDisordered(cmlMol) && !cmlMol.hasCloseContacts() && nonUnitOccNodes.size() == 0
                    && hasBondOrdersAndCharges(cmlMol)) {
                try {// TODO don't need 2D co-ordinates at this point.
                    // CDKUtils.add2DCoords(cmlMol);
                    // new StereochemistryTool(cmlMol).addWedgeHatchBonds();
                } catch (Exception e) {
                    warn("Exception adding wedge/hatch bonds to molecule " + cmlMol.getId());
                }
                /*
                 * if (!compoundClass.equals(CompoundClass.INORGANIC) &&
                 * (CrystalEyeUtils.getNumberOfRings(cmlMol) <
                 * CrystalEyeUtils.MAX_RINGS)) { calculateAndAddSmiles(cmlMol);
                 * } InChIGeneratorTool.addInchiToMolecule(molecule);
                 */
            }
            // FIXME - remove this section and fix CDK instead!
            if (formCopy != null)
                cmlMol.appendChild(formCopy);
            if (crystCopy != null)
                cmlMol.appendChild(crystCopy);
            // ------------------------------
            /*-----end section to be removed------*/
        }
    }

    /**
     * Repositions the crystal element to be the first child of the molecule
     *
     * @param crystalStructure
     * @param molecule
     */
    private void repositionCMLCrystalElement(CMLModule crystalStructure, CMLMolecule molecule) {
        CMLCrystal crystal = (CMLCrystal) molecule.getFirstCMLChild("crystal");
        crystal.detach();
        crystalStructure.insertChild(crystal, 0);
    }

    /**
     * Looks for flag that says if processing for bond orders and charges has
     * been successful
     *
     */
    public static boolean hasBondOrdersAndCharges(CMLMolecule molecule) {
        boolean hasBOAC = true;
        Nodes flagNodes = molecule.query(".//" + CMLMetadata.NS + "[@dictRef='" + NO_BONDS_OR_CHARGES_FLAG_DICTREF + "']", CML_XPATH);
        if (flagNodes.size() > 0) {
            hasBOAC = false;
        }
        return hasBOAC;
    }

    /**
     * Add flag says if processing for bond orders and charges has been
     * successful
     *
     */
    private void addNoBondsOrChargesSetFlag(CMLMolecule molecule) {
        CMLMetadataList ml = (CMLMetadataList) molecule.getFirstCMLChild(CMLMetadataList.TAG);
        if (ml == null) {
            ml = new CMLMetadataList();
            molecule.appendChild(ml);
        }
        CMLMetadata met = new CMLMetadata();
        ml.appendChild(met);
        met.setAttribute("dictRef", NO_BONDS_OR_CHARGES_FLAG_DICTREF);
    }

    /**
     * Does the molecule contain a bond order that hasn't been found
     *
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
     * Compares a molecule against a formula to find what the charge is for the
     * molecule
     *
     */
    private int getMoietyChargeFromFormula(CMLModule crystallographyExperiment, CMLMolecule molecule) {
        int molCharge = ValencyTool.UNKNOWN_CHARGE;
        Nodes moiFormNodes = crystallographyExperiment.query(".//" + CMLFormula.NS + "[@dictRef='iucr:_chemical_formula_moiety']", CML_XPATH);
        CMLFormula moietyFormula = null;
        MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
        if (moiFormNodes.size() > 0) {
            moietyFormula = (CMLFormula) moiFormNodes.get(0);
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
     *
     */
    private void setAllBondOrders(CMLMolecule molecule, String order) {
        for (CMLBond bond : molecule.getBonds()) {
            bond.setOrder(order);
        }
    }

	@Override
	public String getRegistryInputType() {
		return CIFCommon.REG_CIF_RAW_CML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CIFCommon.REG_CIF_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return REG_MESSAGE;
	}
}
