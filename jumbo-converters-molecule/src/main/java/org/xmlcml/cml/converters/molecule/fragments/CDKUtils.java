package org.xmlcml.cml.converters.molecule.fragments;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Node;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Real2;

public class CDKUtils {
   
//    public static IMolecule getCdkMol(CMLMolecule cmlMol) {
//        CMLMolecule molCopy = (CMLMolecule)cmlMol.copy();
//        CMLCrystal cryst = (CMLCrystal)molCopy.getFirstCMLChild(CMLCrystal.TAG);
//        if (cryst != null) {
//            cryst.detach();
//        }
//       
//        BufferedInputStream bis = null;
//        try {
//            bis = new BufferedInputStream(new ByteArrayInputStream(molCopy.toXML().getBytes()));
//            IChemFile cf = (IChemFile) new CMLReader(bis).read(new ChemFile());
//            IMoleculeSet mols = cf.getChemSequence(0).getChemModel(0).getMoleculeSet();
//            if (mols.getMoleculeCount() > 1) {
//                throw new RuntimeException("CDK found more than one molecule in molecule.");
//            }
//           
//            IMolecule cdkMol = mols.getMolecule(0);
//            Map<String, IAtom> atomMap = new HashMap<String, IAtom>();
//            for (int i = 0; i < cdkMol.getAtomCount(); i++) {
//                IAtom atom = cdkMol.getAtom(i);
//                atomMap.put(atom.getID(), atom);
//                CMLAtom cmlAtom = molCopy.getAtomById(atom.getID());
//                Real2 xy2 = cmlAtom.getXY2();
//                if (xy2 != null) {
//                    atom.setPoint2d(new Point2d(xy2.x, xy2.y));
//                }
//            }
//
//            for (CMLBond bond : molCopy.getBonds())     {
//                List<Node> bondStereoNodes = CMLUtil.getQueryNodes(bond, ".//cml:bondStereo", CML_XPATH);
//                if (bondStereoNodes.size() == 1) {
//                    CMLBondStereo bs = ((CMLBondStereo)bondStereoNodes.get(0));
//                    String stereo = bs.getValue();
//                    if (CMLBond.WEDGE.equals(stereo)) {
//                        IAtom atom0 = atomMap.get(bond.getAtoms().get(0).getId());
//                        IAtom atom1 = atomMap.get(bond.getAtoms().get(1).getId());
//                        IBond iBond = cdkMol.getBond(atom0, atom1);
//                        iBond.setStereo(CDKConstants.STEREO_BOND_UP);
//                    } else if (CMLBond.HATCH.equals(stereo)) {
//                        IAtom atom0 = atomMap.get(bond.getAtoms().get(0).getId());
//                        IAtom atom1 = atomMap.get(bond.getAtoms().get(1).getId());
//                        IBond iBond = cdkMol.getBond(atom0, atom1);
//                        iBond.setStereo(CDKConstants.STEREO_BOND_DOWN);
//                    }
//                } else if (bondStereoNodes.size() > 1) {
//                    throw new RuntimeException("Error: CMLBond has more than one bondStereo set: "+bond);
//                }
//            }
//
//            return cdkMol;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Exception while creating CDK molecule: "+e.getMessage());
//        } finally {
//            IOUtils.closeQuietly(bis);
//        }
//    }
//
//    public static CMLMolecule add2DCoords(CMLMolecule molecule) {
//        CMLMolecule minimalMol = new MinimalCmlCrystalTool(molecule).getMinimalMol();
//        IMolecule mol = getCdkMol(minimalMol);
//        for (int i = 0; i < mol.getAtomCount(); i++) {
//            IAtom atom = mol.getAtom(i);
//            atom.setPoint2d(null);
//            atom.setPoint3d(null);
//        }
//        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
//        sdg.setMolecule(mol);
//        try {
//            sdg.generateCoordinates();
//        } catch (Exception e) {
//            throw new RuntimeException("Error generating molecule coordinates: "+e.getMessage());
//        }
//        mol = sdg.getMolecule();
//        for (int i = 0; i < mol.getAtomCount(); i++) {
//            IAtom atom = mol.getAtom(i);
//            Point2d p = atom.getPoint2d();
//            CMLAtom cmlAtom = molecule.getAtomById(atom.getID());
//            cmlAtom.setX2(p.x);
//            cmlAtom.setY2(p.y);
//        }
//        return molecule;
//    }
//   
}