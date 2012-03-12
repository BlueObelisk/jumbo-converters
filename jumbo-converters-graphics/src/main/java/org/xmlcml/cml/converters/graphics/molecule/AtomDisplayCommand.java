package org.xmlcml.cml.converters.graphics.molecule;


import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.graphics.GraphicsCommand;
import org.xmlcml.cml.tools.AtomDisplay;

/** supports displayList and subclasses
 * 
 * @author pm286
 *
 */
public class AtomDisplayCommand extends GraphicsCommand {

	private static final Logger LOG = Logger.getLogger(AtomDisplayCommand.class);

	private AtomDisplay defaultAtomDisplay;
	
	private Double fontSize;
	private String fontStyle;
	private String fontWeight;
	private String fontFamily;

	public AtomDisplayCommand(AtomDisplay atomDisplay) {
		super(atomDisplay);
		this.defaultAtomDisplay = atomDisplay;
	}
	
	@Override
	protected void init() {
		setDefaults();
		applyValues();
	}
	
	private void setDefaults() {
		if (defaultAtomDisplay != null) {
			fontSize 		= 12.0;
			fontStyle 		= "italic";
		 	fontWeight 		= "normal";
			fontFamily 		= "helvetica";
		}
	}
	
	private void applyValues() {
		if (defaultAtomDisplay != null) {
		    defaultAtomDisplay.setFontFamily(fontFamily);
		    defaultAtomDisplay.setFontSize(fontSize);
		    defaultAtomDisplay.setFontStyle(fontStyle);
		    defaultAtomDisplay.setFontWeight(fontWeight);
		}
	}
	
	@Override
	protected void createImplicitValues() {
		super.createImplicitValues();

	}
	
	@Override
	protected void summarize() {
		super.summarize();
		LOG.debug("FONTSIZE  : "+ fontSize);
		LOG.debug("FONTSTYLE : "+ fontStyle);
		LOG.debug("FONTWEIGHT: "+ fontWeight);
		LOG.debug("FONTFAMILY: "+ fontFamily);
// ...		
	}
	
}
