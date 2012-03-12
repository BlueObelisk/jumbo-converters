package org.xmlcml.cml.converters.graphics;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.tools.AbstractDisplay;

/** supports displayList and subclasses
 * 
 * @author pm286
 *
 */
public class GraphicsCommand extends Command {

	private static final Logger LOG = Logger.getLogger(GraphicsCommand.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private String  color;
	private String  fill;
	private Boolean omitHydrogens;
	private Double  opacity;
	private Boolean showChildLabels;
	private String  stroke;
	private String  backgroundColor;
	
	private AbstractDisplay abstractDisplay;

//	public Group displayOptions;

	public GraphicsCommand(AbstractDisplay abstractDisplay) {
		this.abstractDisplay = abstractDisplay;
//		init();
	}
	
	protected void init() {
		setDefaults();
		applyValues();
	}
	
	private void setDefaults() {
		color          	= "black";
		fill           	= "black";
		omitHydrogens 	= false;
		opacity 		= 1.0;
		showChildLabels = false;
		stroke 			= "red";
		backgroundColor = "#cccccc";
	}
	
	private void applyValues() {
		if (abstractDisplay != null) {
		    abstractDisplay.setColor(color);
		    abstractDisplay.setFill(fill);
		    abstractDisplay.setStroke(stroke);
		    abstractDisplay.setBackgroundColor(backgroundColor);
		    abstractDisplay.setOpacity(opacity);
		}
	}
	
	@Override
	protected void createImplicitValues() {
		super.createImplicitValues();

	}
	
	@Override
	protected void summarize() {
		super.summarize();
		LOG.debug("COLOR     : "+ color);
		LOG.debug("FILL      : "+ fill);
		LOG.debug("OMITHYD   : "+ omitHydrogens);
		LOG.debug("OPACITY   : "+ opacity);
		LOG.debug("LABELS    : "+ showChildLabels);
		LOG.debug("STROKE    : "+ stroke);
		LOG.debug("BACKGROUND: "+ backgroundColor);
	}
		


}
