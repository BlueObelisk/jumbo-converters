package org.xmlcml.cml.converters.molecule.cml;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.CMLConverterCommand;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.Morgan;
import org.xmlcml.cml.tools.SMILESTool;

public class CML2CMLConverter extends AbstractConverter implements
		Converter {
	public static final Logger LOG = Logger
	.getLogger(CML2CMLConverter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum CMLType {
		MOLECULE("molecule", CMLMolecule.class),
		REACTION("reaction", CMLReaction.class)
		;
		public String tag;
		public Class<?> classx;
		private CMLType(String tag, Class<?> classx) {
			this.tag = tag;
			this.classx = classx;
		}
	}
	
	private CMLElement inElement;
	private CMLElement outElement;

	// default is to select root
	private String  selectNodesXPath = "/*";
	
	public CML2CMLConverter() {
		LOG.trace("Init CML2CMLConverter");
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	private void executeCommand() {
		LOG.debug("executeCommand... "+command);
		if (selectNodesXPath != null) {
			LOG.debug("xpath: "+selectNodesXPath);
			Nodes nodes = inElement.query(selectNodesXPath, CML_XPATH);
			if (nodes.size() != 1) {
				throw new RuntimeException("Can only process one node");
			}
			if (!(nodes.get(0) instanceof CMLElement)) {
				throw new RuntimeException("processed node must be cml:*");
			}
			outElement = (CMLElement) nodes.get(0);
			LOG.debug("out... "+outElement);
		}
		// at present process all molecules
		if (outElement != null) {
			List<CMLMolecule> moleculeList = getMolecules(outElement);
			LOG.debug("moleculeList "+moleculeList.size());
			for (CMLMolecule molecule : moleculeList) {
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				cascadeCommands(moleculeTool);
			}
		}
	}

	private void cascadeCommands(MoleculeTool moleculeTool) {
		if (command == null || !(command instanceof CMLConverterCommand)) {
			throw new RuntimeException("must have non-null CMLConverterCommand");
		}
		CMLConverterCommand cmlConverterCommand = (CMLConverterCommand) command;
		if (cmlConverterCommand == null) {
			throw new RuntimeException("Must set command in converter");
		}
		LOG.debug("cascade");
		if (cmlConverterCommand.getRemoveNodesXPath() != null) {
			Nodes nodes = outElement.query(cmlConverterCommand.getRemoveNodesXPath(), CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
		if (cmlConverterCommand.isAdd3DCoordinates()) {
			add3DCoordinates(moleculeTool);
		} else if (cmlConverterCommand.isTransformFractionalToCartesian()) {
			transformFractionalToCartesian(moleculeTool);
		}
		if (cmlConverterCommand.isCalculateBonds()) {
			LOG.debug("join bonds");
			if (!moleculeTool.getMolecule().hasCoordinates(CoordinateType.CARTESIAN)) {
				throw new RuntimeException("Need coordinates to calculate bonds");
			}
			calculateBonds(moleculeTool);
		}
		if (cmlConverterCommand.isAddHydrogens()) {
			addHydrogens(moleculeTool);
		} else if (cmlConverterCommand.isAdjustBondOrdersToValency()) {
			LOG.debug("Adjust Bond Orders To Valency");
			adjustBondOrdersToValency(moleculeTool);
		}
		if (cmlConverterCommand.isAddBondOrders()) {
			// FIXME may be needed??
//			addBondOrders(moleculeTool);
		}
		if (cmlConverterCommand.isAdd2DCoordinates()) {
			LOG.debug("add 2d coordinates");
			add2DCoordinates(moleculeTool);
		}
		if (cmlConverterCommand.isAddAtomParityFromCoordinates()) {
			if (!moleculeTool.getMolecule().hasCoordinates(CoordinateType.CARTESIAN)) {
				throw new RuntimeException("Need coordinates to calculate atom parity");
			}
			addAtomParityFromCoordinates(moleculeTool);
		}
		if (cmlConverterCommand.isAddBondStereoFromCoordinates()) {
			if (!moleculeTool.getMolecule().hasCoordinates(CoordinateType.CARTESIAN)) {
				throw new RuntimeException("Need coordinates to calculate bond stereo");
			}
			addBondStereoFromCoordinates(moleculeTool);
		}
		if (cmlConverterCommand.isAddFormulaFromAtoms()) {
			LOG.debug("add formula");
			addFormulaFromAtoms(moleculeTool);
		}
		if (cmlConverterCommand.isAddSMILESFromFormula()) {
			addSMILESFromFormula(moleculeTool);
		}
		if (cmlConverterCommand.isAddMorgan()) {
			addMorgan(moleculeTool);
		}
	}
	
	private List<CMLMolecule> getMolecules(CMLElement element) {
		List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
		Nodes nodes = element.query("//cml:molecule[not(ancestor::cml:molecule)]", CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			moleculeList.add((CMLMolecule) nodes.get(i));
		}
		return moleculeList;
	}
	
	private void transformFractionalToCartesian(MoleculeTool moleculeTool) {
		throw new RuntimeException("fractional not yet implemented");
	}

	private void addHydrogens(MoleculeTool moleculeTool) {
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.USE_EXPLICIT_HYDROGENS);
	}
	private void calculateBonds(MoleculeTool moleculeTool) {
		moleculeTool.calculateBondedAtoms();
	}
	@SuppressWarnings("unused")
	private void addBondOrders(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void adjustBondOrdersToValency(MoleculeTool moleculeTool) {
		moleculeTool.adjustBondOrdersToValency();
	}
	private void add2DCoordinates(MoleculeTool moleculeTool) {
		throw new RuntimeException("ADD 2D NYI");
	}
	private void add3DCoordinates(MoleculeTool moleculeTool) {
		throw new RuntimeException("ADD 3D NYI");
	}
	private void addAtomParityFromCoordinates(MoleculeTool moleculeTool) {
//		List<CMLAtom> atomList = moleculeTool.getMolecule().getAtoms();
//		for (CMLAtom atom : atomList) {
//			AtomTool atomTool = AtomTool.getOrCreateTool(atom);
//		}
		throw new RuntimeException("atom parity NYI");
	}
	private void addBondStereoFromCoordinates(MoleculeTool moleculeTool) {
//		List<CMLBond> bondList = moleculeTool.getMolecule().getBonds();
//		for (CMLBond bond : bondList) {
////			BondTool bondTool = BondTool.getOrCreateTool(bond);
//		}
		throw new RuntimeException("bond parity NYI");
	}
	private void addFormulaFromAtoms(MoleculeTool moleculeTool) {
		CMLMolecule molecule = moleculeTool.getMolecule();
		CMLFormula formula = new CMLFormula(molecule);
		molecule.appendChild(formula);
	}
	private void addSMILESFromFormula(MoleculeTool moleculeTool) {
		CMLMolecule molecule = moleculeTool.getMolecule();
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.setMolecule(molecule);
		String smiles = smilesTool.write();
		CMLFormula formula = new CMLFormula();
		formula.setConvention("cml:smiles");
		formula.setInline(smiles);
		molecule.appendChild(formula);
	}
	
	private void addMorgan(MoleculeTool moleculeTool) {
		LOG.trace("addMorgan");
		CMLMolecule molecule = moleculeTool.getMolecule();
		Morgan morgan = new Morgan(molecule);
		String morganString = morgan.getEquivalenceString();
		CMLIdentifier identifier = new CMLIdentifier();
		identifier.setConvention("cml:morgan");
		identifier.setCMLValue(morganString);
		molecule.appendChild(identifier);
		List<CMLAtomSet> atomSetList = morgan.getAtomSetList();
		CMLList list = new CMLList();
		molecule.appendChild(list);
		for (CMLAtomSet atomSet : atomSetList) {
			list.appendChild(atomSet.copy());
		}
	}
	
	/** converts a CML object to CML.
	 * returns cml:cml/cml:molecule
	 * @param in input element
	 */
	public Element convertToXML(Element in) {
		LOG.debug("convert to XML");
		inElement = (CMLElement) in;
		executeCommand();
		
		CMLMolecule molecule = (CMLMolecule) in.query("//cml:molecule", CML_XPATH).get(0);
		CMLCml cml = new CMLCml();
		cml.appendChild(molecule);
		return cml;
	}
	
	public String getSelectNodesXPath() {
		return selectNodesXPath;
	}

	public void setSelectNodesXPath(String selectNodesXPath) {
		this.selectNodesXPath = selectNodesXPath;
	}

}
