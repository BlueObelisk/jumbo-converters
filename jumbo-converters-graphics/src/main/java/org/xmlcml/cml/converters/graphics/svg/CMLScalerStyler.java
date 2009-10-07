package org.xmlcml.cml.converters.graphics.svg;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;

/** helps with scale, validation, etc.
 * will evolve and may be moved
 * @author pm286
 *
 */
public class CMLScalerStyler implements CMLConstants {

	private static final Logger LOG = Logger.getLogger(CMLScalerStyler.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	protected Boolean centered;
	protected Double bondScale;
	protected Double hydrogenLengthFactor;
	protected Boolean addGroupLabels;
	protected Boolean addFormula;
	
	public CMLScalerStyler() {
		init();
	}
	
	private void init() {
		centered = true;
	}
	
	public Boolean getAddFormula() {
		return addFormula;
	}

	public void setAddFormula(Boolean addFormula) {
		this.addFormula = addFormula;
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
	
	public void scaleBonds(List<CMLMolecule> moleculeList) {
		for (CMLMolecule molecule : moleculeList) {
			scaleBonds(molecule);
		}
	}
	
	public void scaleBondsAndCenter(List<CMLMolecule> moleculeList) {
		scaleBonds(moleculeList);
		centerMolecules(moleculeList);
	}
	
	public void centerMolecules(List<CMLMolecule> moleculeList) {
		for (CMLMolecule molecule : moleculeList) {
			center(molecule);
		}
	}
	
	public void adjustCoordinatesAddLabels(CMLMolecule molecule) {
		scaleBonds(molecule);
		scaleHydrogens(molecule);
		center(molecule);
		addGroupLabels(molecule);
		addFormula(molecule);
	}
	
	public void addFormula(CMLMolecule molecule) {
		if (addFormula != null && addFormula &&
				molecule != null) {
			LOG.debug("add formula");
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.addFormula();
		}
	}
	
	public void addGroupLabels(CMLMolecule molecule) {
		if (addGroupLabels != null && addGroupLabels &&
				molecule != null) {
			LOG.debug("add groups");
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.addGroupLabels();
		}
	}
	
	public void scaleBonds(CMLMolecule molecule) {
		if (bondScale != null && molecule != null) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			double averagelength = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
			CMLAtomSet atomSet = moleculeTool.getAtomSet();
			atomSet.scale2D(bondScale / averagelength);
		}
	}
	
	public void scaleHydrogens(CMLMolecule molecule) {
		LOG.debug("hlenf "+hydrogenLengthFactor);
		if (hydrogenLengthFactor != null && molecule != null) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.adjustHydrogenLengths(hydrogenLengthFactor);
		}
	}
	
	public void center(CMLMolecule molecule) {
		LOG.debug("centered "+centered);
		if (centered != null && centered) {
			Real2 xy = molecule.calculateCentroid2D();
			if (xy != null) {
				LOG.debug("translated molecule to "+xy);
				molecule.translate2D(new Real2(xy.multiplyBy(-1.0)));
				LOG.debug("centroid now: "+molecule.calculateCentroid2D());
			} else {
				throw new RuntimeException("Cannot translate "+molecule.getId());
			}
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

	public Double getHydrogenLengthFactor() {
		return hydrogenLengthFactor;
	}

	public void setHydrogenLengthFactor(Double hydrogenLengthFactor) {
		this.hydrogenLengthFactor = hydrogenLengthFactor;
	}

	public Boolean getContractMethyls() {
		return addGroupLabels;
	}

	public void setContractMethyls(Boolean contractMethyls) {
		LOG.debug("contract methyls");
		this.addGroupLabels = contractMethyls;
	}

	public Boolean getAddGroupLabels() {
		return addGroupLabels;
	}

	public void setAddGroupLabels(Boolean addGroupLabels) {
		LOG.debug("addGroupLabels");
		this.addGroupLabels = addGroupLabels;
	}
	
}
