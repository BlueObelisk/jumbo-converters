package org.xmlcml.cml.converters.cif;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import nu.xom.Node;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Real2;

public class CDKUtils implements CMLConstants {

	public static IMolecule getCdkMol(CMLMolecule cmlMol) {
		CMLMolecule molCopy = (CMLMolecule)cmlMol.copy();
		CMLCrystal cryst = (CMLCrystal)molCopy.getFirstCMLChild(CMLCrystal.TAG);
		if (cryst != null) {
			cryst.detach();
		}
		
		ByteArrayInputStream bais = null;
		IMolecule cdkMol = null;
		try {
			bais = new ByteArrayInputStream(cmlMol.toXML().getBytes());
			IChemFile cf = (IChemFile) new CMLReader(bais).read(new ChemFile());
			bais.close();
			IMoleculeSet mols = cf.getChemSequence(0).getChemModel(0).getMoleculeSet();
			if (mols.getMoleculeCount() > 1) {
				throw new RuntimeException("CDK found more than one molecule in molecule.");
			}
			cdkMol = mols.getMolecule(0);
		} catch (IOException e) {
			throw new RuntimeException("Error reading molecule: "+e.getMessage());
		} catch (CDKException e) {
			throw new RuntimeException("CDK Error reading molecule: "+e.getMessage());
		} finally {
			if (bais != null)
				try {
					bais.close();
				} catch (IOException e) {
					System.err.println("Cannot close input stream: "+bais);
				}
		}
		return cdkMol;
	}

	public static IMolecule cmlMol2CdkMol(CMLMolecule cmlMol) {
		//FIXME - remove this section and fix CDK instead!
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
		/*----end section to remove----*/

		ByteArrayInputStream bais = null;
		IMolecule cdkMol = null;
		try {
			bais = new ByteArrayInputStream(cmlMol.toXML().getBytes());
			IChemFile cf = (IChemFile) new CMLReader(bais).read(new ChemFile());
			bais.close();
			IMoleculeSet mols = cf.getChemSequence(0).getChemModel(0).getMoleculeSet();
			if (mols.getMoleculeCount() > 1) {
				throw new RuntimeException("CDK found more than one molecule in molecule.");
			}
			cdkMol = mols.getMolecule(0);

			Map<String, IAtom> atomMap = new HashMap<String, IAtom>();
			for (int i = 0; i < cdkMol.getAtomCount(); i++) {
				IAtom atom = cdkMol.getAtom(i);
				atomMap.put(atom.getID(), atom);
				CMLAtom cmlAtom = cmlMol.getAtomById(atom.getID());
				Real2 xy2 = cmlAtom.getXY2();
				if (xy2 != null) {
					atom.setPoint2d(new Point2d(xy2.x, xy2.y));
				}
			}

			for (CMLBond bond : cmlMol.getBonds())	 {
				List<Node> bondStereoNodes = CMLUtil.getQueryNodes(bond, ".//cml:bondStereo", CML_XPATH);
				if (bondStereoNodes.size() == 1) {
					CMLBondStereo bs = ((CMLBondStereo)bondStereoNodes.get(0));
					String stereo = bs.getValue();
					if (CMLBond.WEDGE.equals(stereo)) {
						IAtom atom0 = atomMap.get(bond.getAtoms().get(0).getId());
						IAtom atom1 = atomMap.get(bond.getAtoms().get(1).getId());
						IBond iBond = cdkMol.getBond(atom0, atom1);
						iBond.setStereo(CDKConstants.STEREO_BOND_DOWN);
					} else if (CMLBond.HATCH.equals(stereo)) {
						IAtom atom0 = atomMap.get(bond.getAtoms().get(0).getId());
						IAtom atom1 = atomMap.get(bond.getAtoms().get(1).getId());
						IBond iBond = cdkMol.getBond(atom0, atom1);
						iBond.setStereo(CDKConstants.STEREO_BOND_UP);
					}
				} else if (bondStereoNodes.size() > 1) {
					throw new RuntimeException("Error: CMLBond has more than one bondStereo set: "+bond);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading molecule: "+e.getMessage());
		} catch (CDKException e) {
			throw new RuntimeException("CDK Error reading molecule: "+e.getMessage());
		} finally {
			if (bais != null)
				try {
					bais.close();
				} catch (IOException e) {
					System.err.println("Cannot close input stream: "+bais);
				}
		}
		//FIXME - remove this section and fix CDK instead!
		if (formCopy != null) cmlMol.appendChild(formCopy);
		if (crystCopy != null) cmlMol.appendChild(crystCopy);
		/*---end section to be removed-----*/
		return cdkMol;
	}

	public static CMLMolecule add2DCoords(CMLMolecule molecule) {
		IMolecule mol = cmlMol2CdkMol(molecule);
		for (int i = 0; i < mol.getAtomCount(); i++) {
			IAtom atom = mol.getAtom(i);
			atom.setPoint2d(null);
			atom.setPoint3d(null);
		}
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(mol);
		try {
			sdg.generateCoordinates();
		} catch (Exception e) {
			throw new RuntimeException("Error generating molecule coordinates: "+e.getMessage());
		}
		mol = sdg.getMolecule();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			IAtom atom = mol.getAtom(i);
			Point2d p = atom.getPoint2d();
			if (!Double.isNaN(new Double(p.x)) && !Double.isNaN(new Double(p.y))) {
				CMLAtom cmlAtom = molecule.getAtomById(atom.getID());
				cmlAtom.setX2(p.x);
				cmlAtom.setY2(p.y);
			}
		}
		return molecule;
	}
}
