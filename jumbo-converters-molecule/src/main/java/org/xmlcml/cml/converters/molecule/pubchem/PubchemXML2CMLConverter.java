package org.xmlcml.cml.converters.molecule.pubchem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.Util;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

public class PubchemXML2CMLConverter extends AbstractConverter implements
        Converter {

   private static final Logger LOG = Logger.getLogger(
           PubchemXML2CMLConverter.class);
   public final static String CID = "pubchem:cid";

   public PubchemXML2CMLConverter(String configResource) {
      InputStream in = null;
      try {
         in = getClass().getClassLoader().getResourceAsStream(configResource);
         auxElement = new Builder().build(in).getRootElement();
      } catch (ValidityException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (ParsingException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(in);
      }
   }

   @Override
   public boolean isCMLLiteOutput() {
      boolean outputCMLLite = (auxElement != null) ? "cml:lite".equals(auxElement.
              getAttributeValue("output")) : false;
      return outputCMLLite;
   }
   private Element pcCompound;
   private CMLMolecule molecule;
   private List<CMLAtom> atomList;
   private Element compoundId;
   private Element compoundAtoms;
   private Element compoundBonds;
   private Element compoundStereo;
   private Element compoundCoords;
   private Element compoundCharge;
   private Element compoundProps;
   private Element compoundCount;
   private CMLCml cml;

   public Type getInputType() {
      return Type.XML;
   }

   public Type getOutputType() {
      return Type.CML;
   }

   /**
    * converts an MDL object to CML. returns cml:cml/cml:molecule
    *
    * @param pubchemXML
    */
   @Override
   public Element convertToXML(Element pubchemXML) {
      molecule = new CMLMolecule();
      molecule.addNamespaceDeclaration("pubchem", PubchemUtils.NIH_NS);
      cml = new CMLCml();
      cml.appendChild(molecule);
      convert(pubchemXML);
      Util.sanitizeOutput(this, cml);
      return cml;
   }

   private void convert(Element pubchemXML) {
      makePCCompound(pubchemXML);
      compoundId = CMLUtil.getSingleElement(pcCompound, "p:PC-Compound_id",
                                            PubchemUtils.NIH_XPATH);
      compoundAtoms = CMLUtil.getSingleElement(pcCompound, "p:PC-Compound_atoms",
                                               PubchemUtils.NIH_XPATH);
      compoundBonds = CMLUtil.getSingleElement(pcCompound, "p:PC-Compound_bonds",
                                               PubchemUtils.NIH_XPATH);
      compoundStereo = CMLUtil.getSingleElement(pcCompound,
                                                "p:PC-Compound_stereo",
                                                PubchemUtils.NIH_XPATH);
      compoundCoords = CMLUtil.getSingleElement(pcCompound,
                                                "p:PC-Compound_coords",
                                                PubchemUtils.NIH_XPATH);
      compoundCharge = CMLUtil.getSingleElement(pcCompound,
                                                "p:PC-Compound_charge",
                                                PubchemUtils.NIH_XPATH);
      compoundProps = CMLUtil.getSingleElement(pcCompound, "p:PC-Compound_props",
                                               PubchemUtils.NIH_XPATH);
      compoundCount = CMLUtil.getSingleElement(pcCompound, "p:PC-Compound_count",
                                               PubchemUtils.NIH_XPATH);
      PubchemUtils.checkUnknowns(pcCompound, new String[]{
                 "PC-Compound_id",
                 "PC-Compound_atoms",
                 "PC-Compound_bonds",
                 "PC-Compound_stereo",
                 "PC-Compound_coords",
                 "PC-Compound_charge",
                 "PC-Compound_props",
                 "PC-Compound_count",});
      addMoleculeId();
      addAtoms();
      addBonds();
      addAtomStereo();
      addCoords();
      addCharge();
      addInfoData();
      new CompoundCount(pcCompound, molecule);
      cleanNames();
      molecule.normalizeFormulas();
   }

   private void cleanNames() {
      /**
      <property pubchem:label="IUPAC Name"
      pubchem:name="Allowed"
      pubchem:software="LexiChem"
      pubchem:source="openeye.com"
      pubchem:release="2008.01.11"
      pubchem:version="1.6.0">
      <scalar dataType="xsd:string">methylsulfinylmethane</scalar>
      </property>

      converted to

      <name convention="pubchem:Allowed">methylsulfinylmethane</property>

       */
      Nodes nodes = cml.query("//cml:property[@*='IUPAC Name' and cml:scalar]",
                              CMLConstants.CML_XPATH);
      for (int i = 0; i < nodes.size(); i++) {
         CMLProperty property = (CMLProperty) nodes.get(i);
         CMLScalar scalar = (CMLScalar) property.query("./cml:scalar",
                                                       CMLConstants.CML_XPATH).
                 get(0);
         String name = scalar.getValue();
         String pubchemName = property.getAttributeValue("name",
                                                         PubchemUtils.NIH_NS);
         pubchemName = pubchemName.replaceAll(CMLConstants.S_SPACE,
                                              CMLConstants.S_EMPTY);
         CMLName cmlName = new CMLName();
         cmlName.setXMLContent(name);
         cmlName.setConvention("pubchem:" + pubchemName);
         property.getParent().replaceChild(property, cmlName);
      }
   }

   /**
    *
    */
   private void addInfoData() {
      Element propElement = CMLUtil.getSingleElement(pcCompound,
                                                     "p:PC-Compound_props",
                                                     PubchemUtils.NIH_XPATH);
      if (propElement != null) {
         Nodes infoDataNodes = propElement.query("p:PC-InfoData",
                                                 PubchemUtils.NIH_XPATH);
         for (int i = 0; i < infoDataNodes.size(); i++) {
            new InfoData((Element) infoDataNodes.get(i), molecule);
         }
      }
   }

   /**
    *
    */
   private void addCharge() {
      /**
      <PC-Compound_charge>0</PC-Compound_charge>
       */
      Nodes chargeNodes = pcCompound.query("p:PC-Compound_charge",
                                           PubchemUtils.NIH_XPATH);
      if (chargeNodes.size() == 1) {
         String val = null;
         try {
            val = chargeNodes.get(0).getValue();
            int charge = Integer.parseInt(val);
            molecule.setFormalCharge(charge);
         } catch (NumberFormatException nfe) {
            throw new RuntimeException("Charge is not an integer: " + val);
         }
      }
   }

   /**
    *
    */
   private void addCoords() {
      /**
      <PC-Compound_coords>
      <PC-Coordinates>
      <PC-Coordinates_type>
      <PC-CoordinateType value="twod">1</PC-CoordinateType>
      <PC-CoordinateType value="computed">5</PC-CoordinateType>
      <PC-CoordinateType value="units-unknown">255</PC-CoordinateType>
      </PC-Coordinates_type>
      <PC-Coordinates_aid>
      <PC-Coordinates_aid_E>1</PC-Coordinates_aid_E>
      <PC-Coordinates_aid_E>2</PC-Coordinates_aid_E>
      ...
      <PC-Coordinates_aid_E>54</PC-Coordinates_aid_E>
      </PC-Coordinates_aid>
      <PC-Coordinates_conformers>
      <PC-Conformer>
      <PC-Conformer_x>
      <PC-Conformer_x_E>8.6499662399292</PC-Conformer_x_E>
      <PC-Conformer_x_E>2</PC-Conformer_x_E>
      <PC-Conformer_x_E>10.29631614685</PC-Conformer_x_E>
      ...
      <PC-Conformer_x_E>9.3498115539551</PC-Conformer_x_E>
      </PC-Conformer_x>
      <PC-Conformer_y>
      <PC-Conformer_y_E>1.5983420610428</PC-Conformer_y_E>
      <PC-Conformer_y_E>-2.7092418670654</PC-Conformer_y_E>
      ...
      <PC-Conformer_y_E>2.9476916790009</PC-Conformer_y_E>
      </PC-Conformer_y>
       */
      Element coordinatesElement = CMLUtil.getSingleElement(
              pcCompound, "p:PC-Compound_coords/p:PC-Coordinates",
              PubchemUtils.NIH_XPATH);
      if (coordinatesElement != null) {
         @SuppressWarnings("unused")
         Coordinates coordinates = new Coordinates(coordinatesElement, molecule);
      }
   }

   /**
    *
    */
   private void addAtomStereo() {
      /**
      <PC-Compound_stereo>
      <PC-StereoCenter>
      <PC-StereoCenter_tetrahedral>
      <PC-StereoTetrahedral>
      <PC-StereoTetrahedral_center>4</PC-StereoTetrahedral_center>
      <PC-StereoTetrahedral_above>5</PC-StereoTetrahedral_above>
      <PC-StereoTetrahedral_top>12</PC-StereoTetrahedral_top>
      <PC-StereoTetrahedral_bottom>6</PC-StereoTetrahedral_bottom>
      <PC-StereoTetrahedral_below>25</PC-StereoTetrahedral_below>
      <PC-StereoTetrahedral_parity value="counterclockwise">2</PC-StereoTetrahedral_parity>
      <PC-StereoTetrahedral_type value="tetrahedral">1</PC-StereoTetrahedral_type>
      </PC-StereoTetrahedral>
      </PC-StereoCenter_tetrahedral>
      </PC-StereoCenter>
      ...
      <PC-StereoTetrahedral_parity value="clockwise">1</PC-StereoTetrahedral_parity>
      <PC-StereoTetrahedral_type value="tetrahedral">1</PC-StereoTetrahedral_type>
      </PC-StereoTetrahedral>
      </PC-StereoCenter_tetrahedral>
      </PC-StereoCenter>
      </PC-Compound_stereo>
       */
      if (compoundStereo != null) {
         Nodes stereoCenterNodes = compoundStereo.query("p:PC-StereoCenter",
                                                        PubchemUtils.NIH_XPATH);
         PubchemUtils.checkUnknowns(compoundStereo, new String[]{
                    "PC-StereoCenter",});
         for (int i = 0; i < stereoCenterNodes.size(); i++) {
            processStereoCenter((Element) stereoCenterNodes.get(i));
         }
      }
   }

   private void processStereoCenter(Element stereoCenter) {
      PubchemUtils.checkUnknowns(stereoCenter, new String[]{
                 "PC-StereoCenter_tetrahedral",
                 "PC-StereoCenter_planar"
              });
      Element stereoCenterTetrahedral = CMLUtil.getSingleElement(
              stereoCenter, "p:PC-StereoCenter_tetrahedral",
              PubchemUtils.NIH_XPATH);
      if (stereoCenterTetrahedral != null) {
         Nodes stereoTetrahedrals = stereoCenterTetrahedral.query(
                 "p:PC-StereoTetrahedral", PubchemUtils.NIH_XPATH);
         for (int i = 0; i < stereoTetrahedrals.size(); i++) {
            processTetrahedral((Element) stereoTetrahedrals.get(i));
         }
      }
      Element stereoCenterPlanar = CMLUtil.getSingleElement(
              stereoCenter, "p:PC-StereoCenter_planar", PubchemUtils.NIH_XPATH);
      if (stereoCenterPlanar != null) {
         Nodes stereoPlanars = stereoCenterPlanar.query(
                 "p:PC-StereoPlanar", PubchemUtils.NIH_XPATH);
         for (int i = 0; i < stereoPlanars.size(); i++) {
            processPlanar((Element) stereoPlanars.get(i));
         }
      }
   }

   private void processTetrahedral(Element stereoCenter) {
      Element center = CMLUtil.getSingleElement(stereoCenter,
                                                "p:PC-StereoTetrahedral_center",
                                                PubchemUtils.NIH_XPATH);
      Element above = CMLUtil.getSingleElement(stereoCenter,
                                               "p:PC-StereoTetrahedral_above",
                                               PubchemUtils.NIH_XPATH);
      Element top = CMLUtil.getSingleElement(stereoCenter,
                                             "p:PC-StereoTetrahedral_top",
                                             PubchemUtils.NIH_XPATH);
      Element bottom = CMLUtil.getSingleElement(stereoCenter,
                                                "p:PC-StereoTetrahedral_bottom",
                                                PubchemUtils.NIH_XPATH);
      Element below = CMLUtil.getSingleElement(stereoCenter,
                                               "p:PC-StereoTetrahedral_below",
                                               PubchemUtils.NIH_XPATH);
      String parity = CMLUtil.getSingleValue(stereoCenter,
                                             "p:PC-StereoTetrahedral_parity/@value",
                                             PubchemUtils.NIH_XPATH);
      @SuppressWarnings("unused")
      String type = CMLUtil.getSingleValue(stereoCenter,
                                           "p:PC-StereoTetrahedral_type/@value",
                                           PubchemUtils.NIH_XPATH);
      PubchemUtils.checkUnknowns(stereoCenter, new String[]{
                 "PC-StereoTetrahedral_center",
                 "PC-StereoTetrahedral_above",
                 "PC-StereoTetrahedral_top",
                 "PC-StereoTetrahedral_bottom",
                 "PC-StereoTetrahedral_below",
                 "PC-StereoTetrahedral_parity",
                 "PC-StereoTetrahedral_type",});
      String id = PubchemUtils.createAtomId(center);
      CMLAtom atom = molecule.getAtomById(id);
      if (atom == null) {
         throw new RuntimeException("Cannot find atom: " + id);
      }
      String[] atomRefs4 = new String[4];
      atomRefs4[0] = PubchemUtils.createAtomId(above);
      atomRefs4[1] = PubchemUtils.createAtomId(top);
      atomRefs4[2] = PubchemUtils.createAtomId(bottom);
      atomRefs4[3] = PubchemUtils.createAtomId(below);
      CMLAtomParity atomParity = new CMLAtomParity();
      atomParity.setAtomRefs4(atomRefs4);
      double value = (parity.equals("counterclockwise")) ? -1 : 1;
      atomParity.setXMLContent(value);
      atom.addAtomParity(atomParity);
   }

   private void processPlanar(Element stereoCenter) {
      /*
       *       <PC-StereoCenter>
      <PC-StereoCenter_planar>
      <PC-StereoPlanar>
      <PC-StereoPlanar_left>7</PC-StereoPlanar_left>
      <PC-StereoPlanar_ltop>-1</PC-StereoPlanar_ltop>
      <PC-StereoPlanar_lbottom>13</PC-StereoPlanar_lbottom>
      <PC-StereoPlanar_right>8</PC-StereoPlanar_right>
      <PC-StereoPlanar_rtop>-1</PC-StereoPlanar_rtop>
      <PC-StereoPlanar_rbottom>9</PC-StereoPlanar_rbottom>
      <PC-StereoPlanar_parity value="any">3</PC-StereoPlanar_parity>
      <PC-StereoPlanar_type value="planar">1</PC-StereoPlanar_type>
      </PC-StereoPlanar>
      </PC-StereoCenter_planar>
      </PC-StereoCenter>
       */
//		Element center = CMLUtil.getSingleElement(stereoCenter, "p:PC-StereoTetrahedral_center", PubchemUtils.NIH_XPATH);
//		@SuppressWarnings("unused")
//		String type = CMLUtil.getSingleValue(stereoCenter, "p:PC-StereoTetrahedral_type/@value", PubchemUtils.NIH_XPATH);
//		PubchemUtils.checkUnknowns(stereoCenter, new String[] {
//			"PC-StereoTetrahedral_center",
//		});
   }

   /**
    * @throws RuntimeException
    */
   private void addBonds() throws RuntimeException {
      /**
      <PC-Compound_bonds>
      <PC-Bonds>
      <PC-Bonds_aid1>
      <PC-Bonds_aid1_E>1</PC-Bonds_aid1_E>
      <PC-Bonds_aid1_E>1</PC-Bonds_aid1_E>
      ...
      <PC-Bonds_aid1_E>4</PC-Bonds_aid1_E>
      <PC-Bonds_aid1_E>4</PC-Bonds_aid1_E>
      </PC-Bonds_aid1>
      <PC-Bonds_aid2>
      <PC-Bonds_aid2_E>2</PC-Bonds_aid2_E>
      <PC-Bonds_aid2_E>3</PC-Bonds_aid2_E>
      ...
      <PC-Bonds_aid2_E>10</PC-Bonds_aid2_E>
      </PC-Bonds_aid2>
      <PC-Bonds_order>
      <PC-BondType value="double">2</PC-BondType>
      <PC-BondType value="single">1</PC-BondType>
      ...
      <PC-BondType value="single">1</PC-BondType>
      </PC-Bonds_order>
      </PC-Bonds>
      </PC-Compound_bonds>
       */
      if (compoundBonds != null) {
         Element bondsElement = CMLUtil.getSingleElement(compoundBonds,
                                                         "p:PC-Bonds",
                                                         PubchemUtils.NIH_XPATH);
         PubchemUtils.checkUnknowns(compoundBonds, new String[]{
                    "PC-Bonds",});
         if (bondsElement != null) {
//				Element aid1Element = CMLUtil.getSingleElement(bondsElement, "p:PC-Bonds_aid1", PubchemUtils.NIH_XPATH);
//				Element aid2Element = CMLUtil.getSingleElement(bondsElement, "p:PC-Bonds_aid2", PubchemUtils.NIH_XPATH);
//				Element orderElement = CMLUtil.getSingleElement(bondsElement, "p:PC-Bonds_order", PubchemUtils.NIH_XPATH);
            PubchemUtils.checkUnknowns(bondsElement, new String[]{
                       "PC-Bonds_aid1",
                       "PC-Bonds_aid2",
                       "PC-Bonds_order",});

            Nodes aid1Nodes = bondsElement.query(
                    "p:PC-Bonds_aid1/p:PC-Bonds_aid1_E", PubchemUtils.NIH_XPATH);
            Nodes aid2Nodes = bondsElement.query(
                    "p:PC-Bonds_aid2/p:PC-Bonds_aid2_E", PubchemUtils.NIH_XPATH);
            Nodes orderNodes = bondsElement.query(
                    "p:PC-Bonds_order/p:PC-BondType", PubchemUtils.NIH_XPATH);
            int size = aid1Nodes.size();
            if (size != aid2Nodes.size()) {
               throw new RuntimeException("atomRefs2 different sizes");
            }
            if (size != orderNodes.size()) {
               throw new RuntimeException("bond order different sizes");
            }
            for (int i = 0; i < aid1Nodes.size(); i++) {
               CMLBond bond = new CMLBond();
               bond.setAtomRefs2(new String[]{
                          PubchemUtils.createAtomId((Element) aid1Nodes.get(i)),
                          PubchemUtils.createAtomId((Element) aid2Nodes.get(i))
                       });
               bond.setOrder(convertOrder(orderNodes.get(i).getValue()));
               molecule.addBond(bond);
            }
         }
      }
   }

   /**
    * @throws RuntimeException
    */
   private void addAtoms() throws RuntimeException {
      /**
      <PC-Compound_atoms>
      <PC-Atoms>
      <PC-Atoms_aid>
      <PC-Atoms_aid_E>1</PC-Atoms_aid_E>
      <PC-Atoms_aid_E>2</PC-Atoms_aid_E>
      ...
      <PC-Atoms_aid_E>54</PC-Atoms_aid_E>
      </PC-Atoms_aid>
       */
      /**
      <PC-Atoms_element>
      <PC-Element value="o">8</PC-Element>
      <PC-Element value="o">8</PC-Element>
      ...
      <PC-Element value="h">1</PC-Element>
      </PC-Atoms_element>
      </PC-Atoms>
      </PC-Compound_atoms>
       */
      if (compoundAtoms == null) {
         throw new RuntimeException("no compound atoms");
      }
      Element atomsElement = CMLUtil.getSingleElement(compoundAtoms,
                                                      "p:PC-Atoms",
                                                      PubchemUtils.NIH_XPATH);
      PubchemUtils.checkUnknowns(compoundAtoms, new String[]{
                 "PC-Atoms",});
      if (atomsElement != null) {
         Element aidElement = CMLUtil.getSingleElement(atomsElement,
                                                       "p:PC-Atoms_aid",
                                                       PubchemUtils.NIH_XPATH);
         Element elemElement = CMLUtil.getSingleElement(atomsElement,
                                                        "p:PC-Atoms_element",
                                                        PubchemUtils.NIH_XPATH);
         Element isotopeElement = CMLUtil.getSingleElement(atomsElement,
                                                           "p:PC-Atoms_isotope",
                                                           PubchemUtils.NIH_XPATH);
         Element chargeElement = CMLUtil.getSingleElement(atomsElement,
                                                          "p:PC-Atoms_charge",
                                                          PubchemUtils.NIH_XPATH);
         PubchemUtils.checkUnknowns(atomsElement, new String[]{
                    "PC-Atoms_aid",
                    "PC-Atoms_charge",
                    "PC-Atoms_element",
                    "PC-Atoms_isotope",});
         if (aidElement != null && elemElement != null) {
            Nodes aidNodes = aidElement.query("p:PC-Atoms_aid_E",
                                              PubchemUtils.NIH_XPATH);
            int size = aidNodes.size();
            Nodes elemNodes = elemElement.query("p:PC-Element",
                                                PubchemUtils.NIH_XPATH);
            if (size != elemNodes.size()) {
               throw new RuntimeException("ids and elements different sizes");
            }
            atomList = new ArrayList<CMLAtom>();
            for (int i = 0; i < aidNodes.size(); i++) {
               CMLAtom atom = new CMLAtom();
               atomList.add(atom);
               String id = PubchemUtils.createAtomId((Element) aidNodes.get(i));
               atom.setId(id);
               String value = ((Element) elemNodes.get(i)).getAttributeValue(
                       "value");
               atom.setElementType(org.xmlcml.euclid.Util.capitalise(value));
               molecule.addAtom(atom);
            }
            if (isotopeElement != null) {
               processIsotopes(isotopeElement);
            }
            if (chargeElement != null) {
               processCharges(chargeElement);
            }
         }
      }
   }

   private void processCharges(Element chargeElement) {
      /**
      <PC-Atoms_charge>
      <PC-AtomInt>
      <PC-AtomInt_aid>2</PC-AtomInt_aid>
      <PC-AtomInt_value>2</PC-AtomInt_value>
      </PC-AtomInt>
      <PC-AtomInt>
      <PC-AtomInt_aid>3</PC-AtomInt_aid>
      <PC-AtomInt_value>-1</PC-AtomInt_value>
      </PC-AtomInt>
      <PC-AtomInt>
      <PC-AtomInt_aid>4</PC-AtomInt_aid>
      <PC-AtomInt_value>-1</PC-AtomInt_value>
      </PC-AtomInt>
      </PC-Atoms_charge>
       */
      // appears to use common structure between charge and isotope
      Nodes atomIntNodes = chargeElement.query("p:PC-AtomInt",
                                               PubchemUtils.NIH_XPATH);
      for (int i = 0; i < atomIntNodes.size(); i++) {
         Element atomInt = (Element) atomIntNodes.get(i);
         Element atomIntAid = CMLUtil.getSingleElement(atomInt,
                                                       "p:PC-AtomInt_aid",
                                                       PubchemUtils.NIH_XPATH);
         Element atomIntValue = CMLUtil.getSingleElement(atomInt,
                                                         "p:PC-AtomInt_value",
                                                         PubchemUtils.NIH_XPATH);
         if (atomIntAid != null && atomIntValue != null) {
            String id = PubchemUtils.createAtomId(atomIntAid);
            CMLAtom atom = molecule.getAtomById(id);
            if (atom == null) {
               molecule.debug("CHARGE");
               throw new RuntimeException("Cannot find atom for isotope: " + id);
            }
            atom.setFormalCharge(Integer.parseInt(atomIntValue.getValue()));
         }
      }
   }

   /**
    * @param isotopeElement
    * @throws RuntimeException
    * @throws NumberFormatException
    */
   private void processIsotopes(Element isotopeElement)
           throws RuntimeException, NumberFormatException {
      /**
      <PC-Atoms_isotope>
      <PC-AtomInt>
      <PC-AtomInt_aid>2</PC-AtomInt_aid>
      <PC-AtomInt_value>2</PC-AtomInt_value>
      </PC-AtomInt>
      <PC-AtomInt>
      <PC-AtomInt_aid>3</PC-AtomInt_aid>
      <PC-AtomInt_value>2</PC-AtomInt_value>
      </PC-AtomInt>
      </PC-Atoms_isotope>
       */
      Nodes atomIntNodes = isotopeElement.query("p:PC-AtomInt",
                                                PubchemUtils.NIH_XPATH);
      for (int i = 0; i < atomIntNodes.size(); i++) {
         Element atomInt = (Element) atomIntNodes.get(i);
         Element atomIntAid = CMLUtil.getSingleElement(atomInt,
                                                       "p:PC-AtomInt_aid",
                                                       PubchemUtils.NIH_XPATH);
         Element atomIntValue = CMLUtil.getSingleElement(atomInt,
                                                         "p:PC-AtomInt_value",
                                                         PubchemUtils.NIH_XPATH);
         if (atomIntAid != null && atomIntValue != null) {
            String id = PubchemUtils.createAtomId(atomIntAid);
            CMLAtom atom = molecule.getAtomById(id);
            if (atom == null) {
               molecule.debug("ISOTOPE");
               throw new RuntimeException("Cannot find atom for isotope: " + id);
            }
            atom.setIsotopeNumber(Integer.parseInt(atomIntValue.getValue()));
         }
      }
   }

   /**
    * @param molecule
    * @throws RuntimeException
    */
   private void addMoleculeId() throws RuntimeException {
      /**
      <PC-Compound_id>
      <PC-CompoundType>
      <PC-CompoundType_id>
      <PC-CompoundType_id_cid>92145</PC-CompoundType_id_cid>
      </PC-CompoundType_id>
      </PC-CompoundType>
      </PC-Compound_id>
       */
      if (compoundId == null) {
         throw new RuntimeException("no compound id");
      }
      Element compoundType = CMLUtil.getSingleElement(compoundId,
                                                      "p:PC-CompoundType",
                                                      PubchemUtils.NIH_XPATH);
      if (compoundType == null) {
         throw new RuntimeException("no compound type");
      }
      PubchemUtils.checkUnknowns(compoundId, new String[]{
                 "PC-CompoundType",});
      Element compoundTypeId = CMLUtil.getSingleElement(compoundType,
                                                        "p:PC-CompoundType_id",
                                                        PubchemUtils.NIH_XPATH);
      if (compoundTypeId == null) {
         throw new RuntimeException("no compound type id");
      }
      PubchemUtils.checkUnknowns(compoundType, new String[]{
                 "PC-CompoundType_id",});
      Element compoundTypeIdCid = CMLUtil.getSingleElement(compoundTypeId,
                                                           "p:PC-CompoundType_id_cid",
                                                           PubchemUtils.NIH_XPATH);
      if (compoundTypeIdCid == null) {
         throw new RuntimeException("no compound type id cid");
      }
      PubchemUtils.checkUnknowns(compoundTypeId, new String[]{
                 "PC-CompoundType_id_cid",});
      String id = compoundTypeIdCid.getValue();
      CMLName name = new CMLName();
      name.setConvention(CID);
      name.setXMLContent(id);
      molecule.addName(name);
      molecule.setId(createMoleculeId(id));
   }

   /**
    * @param id
    * @return
    */
   private String createMoleculeId(String id) {
      return "m" + id;
   }

   /**
    * @param pubchemXML
    * @return
    */
   private void makePCCompound(Element pubchemXML) {
      /**
      <?xml version="1.0"?>
      <PC-Compound
      xmlns="http://www.ncbi.nlm.nih.gov"
      xmlns:xs="http://www.w3.org/2001/XMLSchema-instance"
      xs:schemaLocation="http://www.ncbi.nlm.nih.gov
      ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem.xsd"
      >

      OR...

      <?xml version="1.0" encoding="UTF-8"?>
      <PC-Compounds
      xmlns="http://www.ncbi.nlm.nih.gov"
      xmlns:xs="http://www.w3.org/2001/XMLSchema-instance"
      xs:schemaLocation="http://www.ncbi.nlm.nih.gov
      ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem.xsd">
      <PC-Compound>
      <PC-Compound_id>
      <PC-CompoundType>
      <PC-CompoundType_id>
      <PC-CompoundType_id_cid>2519</PC-CompoundType_id_cid>
      </PC-CompoundType_id>
      </PC-CompoundType>
      </PC-Compound_id>

       */
      Nodes nodes = pubchemXML.query("/p:PC-Compound", PubchemUtils.NIH_XPATH);
      Nodes nodes1 = pubchemXML.query("/p:PC-Compound1", PubchemUtils.NIH_XPATH);
      if (nodes1.size() == 1) {
         throw new RuntimeException("TODO make this read multiple files");
      } else if (nodes.size() == 1) {
         pcCompound = (Element) nodes.get(0);
      } else {
         throw new RuntimeException("Probably not a PubchemXML file");
      }
   }

   private String convertOrder(String v) {
      return v;
   }

}
