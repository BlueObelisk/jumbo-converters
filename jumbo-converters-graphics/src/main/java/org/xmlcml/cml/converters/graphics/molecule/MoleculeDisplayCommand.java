package org.xmlcml.cml.converters.graphics.molecule;


import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.GraphicsCommand;
import org.xmlcml.cml.converters.graphics.svg.CMLScalerStyler;
import org.xmlcml.cml.tools.AtomDisplay;
import org.xmlcml.cml.tools.BondDisplay;
import org.xmlcml.cml.tools.MoleculeDisplay;

/** supports displayList and subclasses
 * 
 * @author pm286
 *
 */
public class MoleculeDisplayCommand extends GraphicsCommand {

	private static final Logger LOG = Logger.getLogger(MoleculeDisplayCommand.class);
	
	private CMLScalerStyler cmlScaler;
	
	private MoleculeDisplay moleculeDisplay;
	private AtomDisplayCommand atomDisplayCommand;
	private AtomDisplay defaultAtomDisplay;
	private BondDisplayCommand bondDisplayCommand;
	private BondDisplay defaultBondDisplay;
	
	public MoleculeDisplayCommand(MoleculeDisplay moleculeDisplay) {
		this(moleculeDisplay, null);
	}

	public MoleculeDisplayCommand(MoleculeDisplay moleculeDisplay, CMLScalerStyler cmlScaler) {
		super(moleculeDisplay);
		setMoleculeDisplay(moleculeDisplay);
		setCmlScaler(cmlScaler);
	}

	private void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
		LOG.debug("MMMM "+moleculeDisplay);
		if (moleculeDisplay != null) {
			this.defaultAtomDisplay = moleculeDisplay.getDefaultAtomDisplay();
			LOG.debug("DEFAULTATOMDISPLAY "+defaultAtomDisplay);
			this.defaultBondDisplay = moleculeDisplay.getDefaultBondDisplay();
			this.atomDisplayCommand = new AtomDisplayCommand(defaultAtomDisplay);
			this.bondDisplayCommand = new BondDisplayCommand(defaultBondDisplay);
		}
	}
	
	
	
	@Override
	protected void createImplicitValues() {
		super.createImplicitValues();
		if (atomDisplayCommand != null) {
			atomDisplayCommand.createImplicitValues();
		}
	}
	
	@Override
	protected void summarize() {
		LOG.debug("Summarize");
		super.summarize();
//		LOG.debug("COLOR     : "+ color);
// ...		
		if (atomDisplayCommand != null) {
			atomDisplayCommand.summarize();
		}
   }

	public CMLScalerStyler getCmlScaler() {
		return cmlScaler;
	}

	public void setCmlScaler(CMLScalerStyler cmlScaler) {
		this.cmlScaler = cmlScaler;
	}

}
