package org.xmlcml.cml.converters.cml;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.lensfield.api.LensfieldParameter;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;

import org.xmlcml.cml.tools.MoleculeLayout;
import org.xmlcml.cml.tools.MoleculeTool;


/** allows editing of CMLObjects
 * not yet finished
 * 
 * @author pm286
 *
 */
public class CMLTransformMolecule extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(CMLTransformMolecule.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	@LensfieldParameter(name="command", optional=false)
	private String command = "";
	
	@LensfieldParameter(name="xpath", optional=true)
	private String xpath = ".//cml:molecule";

	private CMLElement cmlElement;

	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}
	
	public CMLTransformMolecule() {
	}

	@Override
	public void convert(InputStream inputStream, OutputStream outputStream) {
		try {
			cmlElement = (CMLElement) new CMLBuilder().build(inputStream).getRootElement();
			transformMolecules();
			CMLUtil.debug(cmlElement, outputStream, 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void transformMolecules() {
		TransformMoleculeCommand commandx = TransformMoleculeCommand.getCommand(command);
		if (commandx == null) {
			throw new RuntimeException("cannot find command: "+command);
		}
		Nodes nodes = cmlElement.query(xpath, CMLConstants.CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof CMLMolecule) {
				transform((CMLMolecule)nodes.get(i), commandx);
			}
		}
	}
	
	private void transform(CMLMolecule molecule, TransformMoleculeCommand commandx) {
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		if (TransformMoleculeCommand.fractionalToCartesian.equals(commandx)) {
			transformFractionalToCartesian(moleculeTool);
		} else if (TransformMoleculeCommand.addHydrogens.equals(commandx)) {
			addHydrogens(moleculeTool);
		} else if (TransformMoleculeCommand.addHydrogen2D.equals(commandx)) {
			addHydrogen2D(moleculeTool);
		} else if (TransformMoleculeCommand.addHydrogen3D.equals(commandx)) {
			addHydrogen3D(moleculeTool);
		} else if (TransformMoleculeCommand.calculateBonds.equals(commandx)) {
			calculateBonds(moleculeTool);
		} else if (TransformMoleculeCommand.addBondOrders.equals(commandx)) {
			addBondOrders(moleculeTool);
		} else if (TransformMoleculeCommand.adjustBondOrdersToValency.equals(commandx)) {
			adjustBondOrdersToValency(moleculeTool);
		} else if (TransformMoleculeCommand.add2DCoordinates.equals(commandx)) {
			add2DCoordinates(moleculeTool);
		} else if (TransformMoleculeCommand.add3DCoordinates.equals(commandx)) {
			add3DCoordinates(moleculeTool);
		} else if (TransformMoleculeCommand.addAtomParityFromCoordinates.equals(commandx)) {
			addAtomParityFromCoordinates(moleculeTool);
		} else if (TransformMoleculeCommand.addBondStereoFromCoordinates.equals(commandx)) {
			addBondStereoFromCoordinates(moleculeTool);
		} else if (TransformMoleculeCommand.addFormulaFromAtoms.equals(commandx)) {
			addFormulaFromAtoms(moleculeTool);
		} else if (TransformMoleculeCommand.addSMILESFromFormula.equals(commandx)) {
			addSMILESFromFormula(moleculeTool);
		} else if (TransformMoleculeCommand.addMorgan.equals(commandx)) {
			addMorgan(moleculeTool);
		} else {
			throw new RuntimeException("No routine implemented for: "+command);
		}
	}
	
	private void transformFractionalToCartesian(MoleculeTool moleculeTool) {
		throw new RuntimeException("transformFractionalToCartesian NYI");
	}
	
	private void addHydrogens(MoleculeTool moleculeTool) {
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		moleculeTool.removeHydrogenCountAttributes();
	}
	
	private void addHydrogen2D(MoleculeTool moleculeTool) {
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		moleculeTool.removeHydrogenCountAttributes();
	}
	
	private void addHydrogen3D(MoleculeTool moleculeTool) {
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		moleculeTool.addCalculated3DCoordinatesForExistingHydrogens();
		moleculeTool.removeHydrogenCountAttributes();
		LOG.info("REMOVED HYDROGENCOUNT ATTS");
	}
	
	private void calculateBonds(MoleculeTool moleculeTool) {
		moleculeTool.calculateBondedAtoms();
	}
	private void addBondOrders(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void adjustBondOrdersToValency(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void add2DCoordinates(MoleculeTool moleculeTool) {
		MoleculeLayout moleculeLayout = 
			new MoleculeLayout(moleculeTool);
		moleculeLayout.create2DCoordinates();
	}
	private void add3DCoordinates(MoleculeTool moleculeTool) {
		throw new RuntimeException("ADD 3D NYI");
	}
	private void addAtomParityFromCoordinates(MoleculeTool moleculeTool) {
		throw new RuntimeException("atom parity NYI");
	}
	private void addBondStereoFromCoordinates(MoleculeTool moleculeTool) {
		throw new RuntimeException("bond stereo NYI");
	}
	private void addFormulaFromAtoms(MoleculeTool moleculeTool) {
		throw new RuntimeException("addFormulaFromAtoms not yet implemented");
	}
	private void addSMILESFromFormula(MoleculeTool moleculeTool) {
		throw new RuntimeException("addSMILESFromFormula not yet implemented");
	}
	private void addMorgan(MoleculeTool moleculeTool) {
		throw new RuntimeException("addMorgan not yet implemented");
	}

}
