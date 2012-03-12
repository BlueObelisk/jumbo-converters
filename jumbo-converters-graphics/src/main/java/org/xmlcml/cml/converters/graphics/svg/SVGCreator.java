package org.xmlcml.cml.converters.graphics.svg;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Command;
//import org.xmlcml.cml.converters.graphics.molecule.MoleculeDisplayCommand;
import org.xmlcml.cml.converters.graphics.molecule.MoleculeDisplayCommand;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;

public class SVGCreator {

	private static final Logger LOG = Logger.getLogger(SVGCreator.class);
	private CMLElement cmlElement;
	private SVGSVG svg;
	private CMLScalerStyler cmlScaler;
	private GraphicsHelper graphicsHelper = new GraphicsHelper();
	private String fileId = null;
	private Command command;
	
    public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	private SVGCreator() {
    	init();
    }

    private void init() {
    }
	
    public SVGCreator(CMLElement cmlElement, CMLScalerStyler cmlScaler, Command command) {
    	this();
    	this.cmlElement = cmlElement;
    	this.cmlScaler = cmlScaler;
    	this.command = command;
		svg = new SVGSVG();
		fileId = cmlElement.getId();
		if (fileId != null) {
			svg.setId(fileId);
		}
    }
    
	public void createSVG() {
		if (cmlElement == null) {
	    	SVGText text = new SVGText(new Real2(20., 30.), "no CML object found");
	    	text.setFontSize(10.);
	    	svg.appendChild(text);
    	} else {
    		// FIXME this is for single molecules only
    		CMLMolecule molecule = (CMLMolecule) cmlElement;
    		MoleculeTool moleculeTool = (MoleculeTool) MoleculeTool.getOrCreateTool(molecule);
    		MoleculeDisplay moleculeDisplay = moleculeTool.getMoleculeDisplay();
    		LOG.debug("MT "+moleculeDisplay);
/**
 -infile D:\workspace\jumbo-converters\src\test\resources\cml\crystal.cml
-outfile D:\workspace\jumbo-converters\src\test\resources\svg1\crystal.svg
--
--display -omith true
--atoms -fsize 0.7 
--molecules -labels true -groups true -bondlen 50 -hlenf 0.5 
--bonds -multcol yellow -width 0.15
 */    		
    		Command command = this.getCommand();
    		if (command == null) {
    			LOG.warn("Null command in SVGCreator");
    		}
    		String argString = (command == null) ? null : command.getArgString();
    		MoleculeDisplayCommand moleculeDisplayCommand = 
    			new MoleculeDisplayCommand(moleculeDisplay, cmlScaler);
    		moleculeDisplayCommand.getCmlScaler().adjustCoordinatesAddLabels(molecule);
    		graphicsHelper.getMoleculeDisplayList().setMoleculeDisplay(moleculeDisplay);
    		SVGElement element = moleculeTool.createGraphicsElement(
    				graphicsHelper.getMoleculeDisplayList());
    		
    		SVGText text = new SVGText(new Real2(10., 10.), fileId);
    		text.setFontSize(10.0);
    		svg.appendChild(text);
    		SVGElement gt = new SVGG();
    		Real2Range rr = moleculeTool.getUserBoundingBox();
    		// a minimal offset so we can see it
    		double xoffset = graphicsHelper.getXOffset();
    		double yoffset = graphicsHelper.getYOffset();
    		RealRange xr = rr.getXRange();
			xoffset += (xr == null) ? 0.0 : -rr.getXRange().getMin();
    		RealRange yr = rr.getXRange();
			yoffset += (yr == null) ? 0.0 : -rr.getYRange().getMin();
    		Transform2 transform = new Transform2(
        			new double[]{
        					1.0,  0.0, xoffset,
        					0.0,  1.0, yoffset,
        					0.0,  0.0, 1.0
        			}
        		);
    		gt.setTransform(transform);
    		svg.appendChild(gt);
    		element.detach();
    		gt.appendChild(element);
    	}
	}
    
//	private void setHydrogens(boolean show) {
//		graphicsHelper.getAtomDisplay().setOmitHydrogens(!show);
//	}
	
	public SVGSVG getSVG() {
		return svg;
	}

}
