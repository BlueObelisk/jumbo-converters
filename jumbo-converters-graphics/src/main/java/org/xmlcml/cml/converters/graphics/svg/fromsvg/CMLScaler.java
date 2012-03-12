package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;

/** helps with scale, validation, etc.
 * will evolve and may be moved
 * @author pm286
 *
 */
public class CMLScaler implements CMLConstants {
	private static Logger LOG = Logger.getLogger(CMLScaler.class);
	
	protected Boolean centered;
	protected Double bondScale;
	private List<CMLMolecule> molecules;
	
	public CMLScaler() {
	}
	
	public Boolean getCentered() {
		return centered;
	}

	public void setCentered(Boolean centered) {
		this.centered = centered;
	}

	public Double getBondScale() {
		return bondScale;
	}

	public void setBondScale(Double bondScale) {
		this.bondScale = bondScale;
	}
	
	public void process(CMLCml cmlCml) {
		molecules = new ArrayList<CMLMolecule>();
		Nodes nodes = cmlCml.query("./cml:moleculeList | cml:molecule | cml:reaction", CML_XPATH);
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				if (node instanceof CMLMoleculeList) {
					for (CMLMolecule molecule : ((CMLMoleculeList) node).getMoleculeElements()) {
						molecules.add(molecule);
					}
				}
				if (node instanceof CMLMolecule) {
					molecules.add((CMLMolecule)node);
				}
				if (node instanceof CMLReaction) {
					LOG.debug("Reaction not yet processed");
				}
			}
		}
		if (bondScale != null) {
			scaleBonds();
		}
		if (centered != null && centered) {
			center();
		}
	}
	
	private void scaleBonds() {
		for (CMLMolecule molecule : molecules) {
			if (molecule == null) {
				throw new RuntimeException("null molecule");
			}
			scaleBonds(molecule);
		}
	}
	
	private void scaleBonds(CMLMolecule molecule) {
		List<CMLBond> bonds = molecule.getBonds();
		double averagelength = 0.0;
		int count = 0;
		for (CMLBond bond : bonds) {
			if (bond.containsElement("H")) {
				continue;
			}
			double length = Double.NaN;
			try {
				length = bond.calculateBondLength(CoordinateType.TWOD);
				averagelength += length;
				count++;
			} catch (RuntimeException e) {
				// 
			}
		}
		averagelength /= (double) count;
		CMLAtomSet atomSet = new CMLAtomSet(molecule);
		double newScale = bondScale;
		atomSet.scale2D(newScale / averagelength);
	}
	
	private void center() {
		for (CMLMolecule molecule : molecules) {
			try {
				center(molecule);
			} catch (RuntimeException e) {
//				LOG.debug("Cannot translate molecule "+molecule.getId());
//				molecule.debug(""+e);
			}
		}
	}

	private void center(CMLMolecule molecule) {
		Real2 xy = molecule.calculateCentroid2D();
		if (xy != null) {
			molecule.translate2D(new Real2(xy.multiplyBy(-1.0)));
		} else {
			throw new RuntimeException("Cannot translate "+molecule.getId());
		}
	}
	
	protected static void usage() {
		Util.println();
		Util.println(" Molecule scale options :");
		Util.println("    -SCALE (bond length pixels)");
		Util.println("    -CENTER");
		Util.println();
	}
	
	/**
	 *
	 * @param args
	 * @param i
	 * @return increase i if args found
	 */
	public int processArgs(String[] args, int i) {
		if (false) {
		} else if ("-SCALE".equalsIgnoreCase(args[i])) {
			try {
				setBondScale(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad bondscale "+args[i]);
			}
		} else if ("-CENTER".equalsIgnoreCase(args[i])) {
   			setCentered(true); i++;
		}
		return i;
	}
	
}
