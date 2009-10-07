package org.xmlcml.cml.converters.cli;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.molecule.cml.CML2CMLConverter;

/** a command to edit a CMLObject
 * 
 * @author pm286
 *
 */
public class CMLConverterCommand extends ConverterCommandLine {

	private static final Logger LOG = 
		Logger.getLogger(CMLConverterCommand.class);

//	public enum Command {
//		TRANSFORM_FRACTIONAL,
//		ADD_HYDROGENS,
//		CALCULATE_BONDS,
//		ADD_BOND_ORDERS,
//		ADJUST_BOND_ORDERS,
//		ADD_2D,
//		ADD_3D,
//		ADD_ATOM_PARITY,
//		ADD_BOND_STEREO,
//		ADD_FORMULA,
//		ADD_SMILES,
//		REMOVE,
//	}
	

	private Option removeNodesOption;
	private Option fract2cartOption;
	private Option addhOption;
	private Option joinBondsOption;
	private Option bondOrderOption;
	private Option abovOption;
	private Option add2dOption;
	private Option add3dOption;
	private Option atomParityOption;
	private Option bondStereoOption;
	private Option formulaOption;
	private Option smilesOption;
	private Option morganOption;

	private String  removeNodesXPath;
	private boolean transformFractionalToCartesian;
	private boolean addHydrogens;
	private boolean calculateBonds;
	private boolean addBondOrders;
	private boolean adjustBondOrdersToValency;
	private boolean add2DCoordinates;
	private boolean add3DCoordinates;
	private boolean addAtomParityFromCoordinates;
	private boolean addBondStereoFromCoordinates;
	private boolean addFormulaFromAtoms;
	private boolean addSMILESFromFormula;
	private boolean addMorgan;
	
	public CMLConverterCommand() {
		LOG.debug("new CML2CMLConverter");
		converterInstance = new CML2CMLConverter();
		((AbstractConverter)converterInstance).setCommand(this);
	}

	
		
	protected void assembleOptions() throws OptionException {
		super.assembleOptions();

		removeNodesOption = defaultOptionBuilder
		    .withShortName("remove")
		    .withShortName("os")
		    .withDescription("remove nodes created by xpath expressions")
		    .withArgument(
		        argumentBuilder
		            .withName("xpath")
		            .withMinimum(1)
//		            .withMaximum(3)
		            .create())
		    .create();
		
		fract2cartOption = defaultOptionBuilder
	    .withShortName("fract2cart")
	    .withDescription("convert fractional coordinates to cartesians")
	    .create();
	
		addhOption = defaultOptionBuilder
	    .withShortName("addh")
	    .withShortName("ah")
	    .withDescription("add hydrogen atoms from valency")
	    .create();

		joinBondsOption = defaultOptionBuilder
	    .withShortName("joinBonds")
	    .withShortName("join")
	    .withDescription("join bonds on 3d length criterion (tolerance is optional)")
	    .withArgument(
	        argumentBuilder
	            .withName("tolerance")
	            .withMinimum(0)
	            .withMaximum(1)
	            .create())
	    .create();

		abovOption = defaultOptionBuilder
	    .withShortName("adjustBondOrdersToValency")
	    .withShortName("abov")
	    .withDescription("adjust bond orders to valency assuming all atoms are present and joined")
	    .create();

		bondOrderOption = defaultOptionBuilder
	    .withShortName("bondOrders")
	    .withShortName("order")
	    .withDescription("not quite sure what this does")
	    .create();

		add2dOption = defaultOptionBuilder
	    .withShortName("add2d")
	    .withDescription("add 2D coordinates (layout)")
	    .create();

		add3dOption = defaultOptionBuilder
	    .withShortName("add3d")
	    .withDescription("add 3D coordinates")
	    .create();

		atomParityOption = defaultOptionBuilder
	    .withShortName("atomParity")
	    .withShortName("ap")
	    .withDescription("add atomParity from 3D coordinates")
	    .create();

		bondStereoOption = defaultOptionBuilder
	    .withShortName("bondStereo")
	    .withShortName("bs")
	    .withDescription("add bondStereo from 3D coordinates")
	    .create();

		formulaOption = defaultOptionBuilder
	    .withShortName("formula")
	    .withDescription("add formula from atom counts")
	    .create();
		
		smilesOption = defaultOptionBuilder
	    .withShortName("smiles")
	    .withDescription("add SMILES from connection table")
	    .create();
		
		morganOption = defaultOptionBuilder
	    .withShortName("morgan")
	    .withDescription("add Morgan from connection table")
	    .create();
		
		
//==========================
		GroupBuilder editorGroupBuilder = new GroupBuilder();
		Group editorGroupChildren = editorGroupBuilder
			.withName("CML2CML editor")
			.withDescription("options to edit CML to CML")
			.withOption(removeNodesOption)
			.withOption(morganOption)
		    .create()
		    ;
		
		GroupBuilder cmlGroupBuilder = new GroupBuilder();
		cmlGroupBuilder
		.withName("CML Molecule Options")
		.withOption(fract2cartOption)
		.withOption(addhOption)
		.withOption(joinBondsOption)
		.withOption(bondOrderOption)
		.withOption(abovOption)
		.withOption(add2dOption)
		.withOption(add3dOption)
		.withOption(atomParityOption)
		.withOption(bondStereoOption)
		.withOption(formulaOption)
		.withOption(smilesOption)
	    ;
		
		Group cmlOptions =  cmlGroupBuilder.create();
		
		groupBuilder
		.withOption(cmlOptions)
		;
		
		    
		
		Option editorOption = defaultOptionBuilder
		.withLongName("edit")
    	.withChildren(editorGroupChildren)
    	.create();
		
		groupBuilder
		.withOption(editorOption)
		;
		
		
	}

	public String getRemoveNodesXPath() {
		return removeNodesXPath;
	}

	public void setRemoveNodesXPath(String removeNodesXPath) {
		this.removeNodesXPath = removeNodesXPath;
	}

	public boolean isTransformFractionalToCartesian() {
		return transformFractionalToCartesian;
	}

	public void setTransformFractionalToCartesian(
			boolean transformFractionalToCartesian) {
		this.transformFractionalToCartesian = transformFractionalToCartesian;
	}


	public boolean isAddHydrogens() {
		return addHydrogens;
	}


	public void setAddHydrogens(boolean addHydrogens) {
		this.addHydrogens = addHydrogens;
	}


	public boolean isCalculateBonds() {
		return calculateBonds;
	}


	public void setCalculateBonds(boolean calculateBonds) {
		LOG.debug("set calculate bonds "+calculateBonds+" "+this);
		this.calculateBonds = calculateBonds;
	}


	public boolean isAddBondOrders() {
		return addBondOrders;
	}


	public void setAddBondOrders(boolean addBondOrders) {
		this.addBondOrders = addBondOrders;
	}


	public boolean isAdjustBondOrdersToValency() {
		return adjustBondOrdersToValency;
	}


	public void setAdjustBondOrdersToValency(boolean adjustBondOrdersToValency) {
		this.adjustBondOrdersToValency = adjustBondOrdersToValency;
	}


	public boolean isAdd2DCoordinates() {
		return add2DCoordinates;
	}


	public void setAdd2DCoordinates(boolean add2DCoordinates) {
		this.add2DCoordinates = add2DCoordinates;
	}


	public boolean isAdd3DCoordinates() {
		return add3DCoordinates;
	}


	public void setAdd3DCoordinates(boolean add3DCoordinates) {
		this.add3DCoordinates = add3DCoordinates;
	}


	public boolean isAddAtomParityFromCoordinates() {
		return addAtomParityFromCoordinates;
	}


	public void setAddAtomParityFromCoordinates(boolean addAtomParityFromCoordinates) {
		this.addAtomParityFromCoordinates = addAtomParityFromCoordinates;
	}


	public boolean isAddBondStereoFromCoordinates() {
		return addBondStereoFromCoordinates;
	}


	public void setAddBondStereoFromCoordinates(boolean addBondStereoFromCoordinates) {
		this.addBondStereoFromCoordinates = addBondStereoFromCoordinates;
	}


	public boolean isAddFormulaFromAtoms() {
		return addFormulaFromAtoms;
	}


	public void setAddFormulaFromAtoms(boolean addFormulaFromAtoms) {
		this.addFormulaFromAtoms = addFormulaFromAtoms;
	}


	public boolean isAddSMILESFromFormula() {
		return addSMILESFromFormula;
	}

	public void setAddSMILESFromFormula(boolean addSMILESFromFormula) {
		this.addSMILESFromFormula = addSMILESFromFormula;
	}
		
	public boolean isAddMorgan() {
		return addMorgan;
	}

	public void setAddMorgan(boolean addMorgan) {
		LOG.debug("setMorgan: " + addMorgan);
		this.addMorgan = addMorgan;
	}
	
//	@Override
	protected void parseSpecific(CommandLine commandLine) {
		super.parseSpecific(commandLine);
		LOG.debug("parse specific");
		String s;

		if(commandLine.hasOption(fract2cartOption)) {
		    s = (String)commandLine.getValue(fract2cartOption);
		    this.setTransformFractionalToCartesian(true);
		}
		
		if(commandLine.hasOption(addhOption)) {
		    s = (String)commandLine.getValue(addhOption);
			this.setAddHydrogens(true);
		}
		
		if(commandLine.hasOption(joinBondsOption)) {
		    s = (String)commandLine.getValue(joinBondsOption);
		    LOG.info("set calculate true");
			this.setCalculateBonds(true);
		}
		
		if(commandLine.hasOption(bondOrderOption)) {
		    s = (String)commandLine.getValue(bondOrderOption);
			this.setAddBondOrders(true);
		}
		
		if(commandLine.hasOption(abovOption)) {
		    s = (String)commandLine.getValue(abovOption);
			this.setAdjustBondOrdersToValency(true);
		}
		
		if(commandLine.hasOption(add2dOption)) {
		    s = (String)commandLine.getValue(add2dOption);
			this.setAdd2DCoordinates(true);
		}
		
		if(commandLine.hasOption(add3dOption)) {
		    s = (String)commandLine.getValue(add3dOption);
			this.setAdd3DCoordinates(true);
		}
		
		if(commandLine.hasOption(atomParityOption)) {
		    s = (String)commandLine.getValue(atomParityOption);
			this.setAddAtomParityFromCoordinates(true);
		}
		
		if(commandLine.hasOption(bondStereoOption)) {
		    s = (String)commandLine.getValue(bondStereoOption);
			this.setAddBondStereoFromCoordinates(true);
		}
		
		if(commandLine.hasOption(formulaOption)) {
		    s = (String)commandLine.getValue(formulaOption);
			this.setAddFormulaFromAtoms(true);
		}
		
		if(commandLine.hasOption(smilesOption)) {
		    s = (String)commandLine.getValue(smilesOption);
			this.setAddSMILESFromFormula(true);
		}
		
		if(commandLine.hasOption(morganOption)) {
		    s = (String)commandLine.getValue(morganOption);
			LOG.debug("set morgan ");
			this.setAddMorgan(true);
		}
		
		if(commandLine.hasOption(removeNodesOption)) {
		    s = (String)commandLine.getValue(removeNodesOption);
			this.setRemoveNodesXPath(s);
		}
	}

	public static void main(String[] args) {
		CMLConverterCommand cmlConverterCommand = new CMLConverterCommand();
		cmlConverterCommand.processCommandLine(args);
	}

	public Option getRemoveNodesOption() {
		return removeNodesOption;
	}

	public void setRemoveNodesOption(Option removeNodesOption) {
		this.removeNodesOption = removeNodesOption;
	}

	public Option getFract2cartOption() {
		return fract2cartOption;
	}

	public void setFract2cartOption(Option fract2cartOption) {
		this.fract2cartOption = fract2cartOption;
	}

	public Option getAddhOption() {
		return addhOption;
	}

	public void setAddhOption(Option addhOption) {
		this.addhOption = addhOption;
	}

	public Option getJoinBondsOption() {
		return joinBondsOption;
	}

	public void setJoinBondsOption(Option joinBondsOption) {
		this.joinBondsOption = joinBondsOption;
	}

	public Option getBondOrderOption() {
		return bondOrderOption;
	}

	public void setBondOrderOption(Option bondOrderOption) {
		this.bondOrderOption = bondOrderOption;
	}

	public Option getAbovOption() {
		return abovOption;
	}

	public void setAbovOption(Option abovOption) {
		this.abovOption = abovOption;
	}

	public Option getAdd2dOption() {
		return add2dOption;
	}

	public void setAdd2dOption(Option add2dOption) {
		this.add2dOption = add2dOption;
	}

	public Option getAdd3dOption() {
		return add3dOption;
	}

	public void setAdd3dOption(Option add3dOption) {
		this.add3dOption = add3dOption;
	}

	public Option getAtomParityOption() {
		return atomParityOption;
	}

	public void setAtomParityOption(Option atomParityOption) {
		this.atomParityOption = atomParityOption;
	}

	public Option getBondStereoOption() {
		return bondStereoOption;
	}

	public void setBondStereoOption(Option bondStereoOption) {
		this.bondStereoOption = bondStereoOption;
	}

	public Option getFormulaOption() {
		return formulaOption;
	}

	public void setFormulaOption(Option formulaOption) {
		this.formulaOption = formulaOption;
	}

	public Option getSmilesOption() {
		return smilesOption;
	}

	public void setSmilesOption(Option smilesOption) {
		this.smilesOption = smilesOption;
	}

	public Option getMorganOption() {
		return morganOption;
	}

	public void setMorganOption(Option morganOption) {
		this.morganOption = morganOption;
	}
	
}
