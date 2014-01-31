package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemSVG;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.fonts.FontWidths;

/**
 * contains some common font material and references to commonly used
 * objects such as fileId
 * 
 * @author pm286
 *
 */
public abstract class GraphicsConverterTool {

	private static Logger LOG = Logger.getLogger(GraphicsConverterTool.class);

    static Logger logger = Logger.getLogger(GraphicsConverterTool.class);

//    private static Map<String, Double> charMap = new HashMap<String, Double>();
    
    // this can be changed if required
    public static double[] fontWidths = FontWidths.SANS_SERIF;
    
	private double charHeightAdjustmentFactor = 0.70;
	public double getCharHeightAdjustmentFactor() {
		return charHeightAdjustmentFactor;
	}
	
	public void setCharHeightAdjustmentFactor(double charHeightAdjustmentFactor) {
		this.charHeightAdjustmentFactor = charHeightAdjustmentFactor;
	}
	
	private double fontWidthAdjustmentFactor = 1.1;
	protected String fileId;
	protected Element svgElement;
	protected SVGChemSVG svgChem;

	protected SVGSVG svg;
	
	public double getFontWidthAdjustmentFactor() {
		return fontWidthAdjustmentFactor;
	}
	
	public void setFontWidthAdjustmentFactor(double fontWidthAdjustmentFactor) {
		this.fontWidthAdjustmentFactor = fontWidthAdjustmentFactor;
	}

	public SVGChemSVG getSvgChem() {
		return svgChem;
	}

	public String getFileId() {
		return fileId;
	}

	public void processSVG() {
		svg = (SVGSVG) SVGElement.readAndCreateSVG(svgElement);
		svgChem = new SVGChemSVG(svg);
		processAfterParsing();
	}
	
	protected abstract void processAfterParsing();

}
class JXPanel extends JPanel {
	private static Logger LOG = Logger.getLogger(JXPanel.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1842088542803189714L;

	public void paint(Graphics g) {
		writeMetrics(g, "sans-serif");
		writeMetrics(g, "serif");
	}

	/**
	 * @param g
	 * @param fontFamily
	 */
	private void writeMetrics(Graphics g, String fontFamily) {
		g.setFont(new Font(fontFamily, Font.BOLD, 100));
		System.out.println("// "+fontFamily);
		for (int i = 0; i < 256; i++) {
			char ch = (char)i;
			g.drawString(""+(char)i, 16 * (i % 16), 16 * (i/16));
			System.out.println("        "+Util.format((double) g.getFontMetrics().charWidth(ch) * 0.01, 2)+", // "+((i < 32) ? "[]" : ch)+" "+i);
		}
	}
}

    
    
