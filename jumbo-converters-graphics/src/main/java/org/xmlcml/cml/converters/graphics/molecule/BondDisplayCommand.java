package org.xmlcml.cml.converters.graphics.molecule;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.GraphicsCommand;
import org.xmlcml.cml.tools.BondDisplay;

/** supports displayList and subclasses
 * 
 * @author pm286
 *
 */
public class BondDisplayCommand extends GraphicsCommand {

	private static final Logger LOG = Logger.getLogger(BondDisplayCommand.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private BondDisplay defaultBondDisplay;

	public BondDisplayCommand(BondDisplay bondDisplay) {
		super(bondDisplay);
		this.defaultBondDisplay = bondDisplay;;
	}


}
