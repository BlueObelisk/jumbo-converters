package org.xmlcml.cml.converters.graphics.svg;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.tools.AbstractDisplay;
import org.xmlcml.cml.tools.AtomDisplay;
import org.xmlcml.cml.tools.BondDisplay;
import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.cml.tools.MoleculeDisplayList;
import org.xmlcml.euclid.Util;

public class GraphicsHelper implements CMLConstants {

	private MoleculeDisplayList moleculeDisplayList;
	private MoleculeDisplay moleculeDisplay;
	private AtomDisplay atomDisplay;
	private BondDisplay bondDisplay;
	private double xOffset;
	private double yOffset;
	private double duration;
	private double end;
	private double start;
	private String repeatCount;
//	private Converter converter;
	
	public GraphicsHelper() {
		init();
	}
	
	private void init() {
		ensureMoleculeDisplayList();
		setDefaults();
	}
	
	private void setDefaults() {
		xOffset = 20.;
		yOffset = 20.;
		duration = 5.0;
		repeatCount = "";
		start = 0.;
		end = 999999;
	}
	
	protected static void usage() {
		Util.println();
		Util.println(" Graphics options :");
		Util.println("    -XOFFSET (screen pixels)");
		Util.println("    -YOFFSET (screen pixels)");
		Util.println("    -DUR[ATION] (of SVG animation step)");
		Util.println("    -END (of SVG animation step)");
		Util.println("    -REPEAT[COUNT] (of SVG repeatCount)");
		Util.println("    -START (of SVG animation step)");
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
		} else if ("-XOFFSET".equalsIgnoreCase(args[i])) {
    	   try {
    			setXOffset(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad xOffset "+args[i]);
			}
		} else if ("-YOFFSET".equalsIgnoreCase(args[i])) {
    	   try {
    			setYOffset(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad yOffset "+args[i]);
			}
		} else if (args[i].equalsIgnoreCase("-DURATION") || 
				args[i].equalsIgnoreCase("-DUR")) {
    	   try {
    			setDuration(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad duration "+args[i]);
			}
		} else if (args[i].equalsIgnoreCase("-END")) {
    	   try {
    			setEnd(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad end "+args[i]);
			}
		} else if (args[i].equalsIgnoreCase("-REPEATCOUNT") || 
				args[i].equalsIgnoreCase("-REPEAT")) {
			setRepeatCount(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-START")) {
    	   try {
    			setStart(new Double(args[++i])); i++;
			} catch (NumberFormatException e) {
				System.err.println("Bad start "+args[i]);
			}
	}
		return i;
	}

	protected void ensureMoleculeDisplayList() {
		if (moleculeDisplayList == null) {
			moleculeDisplayList = new MoleculeDisplayList();
			moleculeDisplay = moleculeDisplayList.getMoleculeDisplay();
			atomDisplay = moleculeDisplay.getDefaultAtomDisplay();
			setDefaultAtomDisplay();
			bondDisplay = moleculeDisplay.getDefaultBondDisplay();
			setDefaultBondDisplay();
		}
	}

	public void setDefaultAtomDisplay() {
		atomDisplay.setFontSize(0.7);
		atomDisplay.setOmitHydrogens(true);
		atomDisplay.setShowChildLabels(true);
	}

	public void setDefaultBondDisplay() {
		bondDisplay.setFontSize(0.7);
	}

	public MoleculeDisplayList getMoleculeDisplayList() {
		return moleculeDisplayList;
	}
	public void setMoleculeDisplayList(MoleculeDisplayList moleculeDisplayList) {
		this.moleculeDisplayList = moleculeDisplayList;
	}
	public AbstractDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}
	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
	public AtomDisplay getAtomDisplay() {
		return atomDisplay;
	}
	public void setAtomDisplay(AtomDisplay atomDisplay) {
		this.atomDisplay = atomDisplay;
	}
	public BondDisplay getBondDisplay() {
		return bondDisplay;
	}
	public void setBondDisplay(BondDisplay bondDisplay) {
		this.bondDisplay = bondDisplay;
	}

	public double getXOffset() {
		return xOffset;
	}

	public void setXOffset(double offset) {
		xOffset = offset;
	}

	public double getYOffset() {
		return yOffset;
	}

	public void setYOffset(double offset) {
		yOffset = offset;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(String repeatCount) {
		this.repeatCount = repeatCount;
	}

	public double getEnd() {
		return end;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		this.start = start;
	}

}
