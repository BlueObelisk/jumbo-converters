package org.xmlcml.cml.converters.graphics.svg.elements;


import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.dicts.ChemicalType;
import org.xmlcml.cml.converters.dicts.ChemicalTypeMap;
import org.xmlcml.cml.converters.dicts.GroupType;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.GraphicsConverterTool;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVG2CMLTool;
import org.xmlcml.cml.converters.graphics.svg.fromsvg.SVGChem;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.molutil.ChemicalElement;

/** manages raw text
 * includes concatenation and font metrics
 * 
 * @author pm286
 *
 */
public class SVGChemText extends SVGText implements SVGChemElement {
	private static Logger LOG = Logger.getLogger(SVGChemText.class);

	private static double BOXDIFF = 3.0;
	private static double SERIAL_FONT_SIZE = 6.0;	
	private double charHeight;
	private double charWidth;

	private List<SVGChemText> textList;
	private SVGChemCircle circle;
	private SVGText serialText;
	private SVGElement rect;

	private ChemicalElement chemicalElement;
	private CMLAtom atom;
	private Integer number;
//	private GroupType group;
	private int romanNumber;
//	private String fontColor;
	
	private List<SVGChemLine> lineList;
	private Real2 centroid;
	
	private SVGCircle leftAnchor;
	private SVGCircle rightAnchor;
	
	public Real2 getCentroid() {
		return centroid;
	}

	public void setCentroid(Real2 centroid) {
		this.centroid = centroid;
	}
	
	public SVGChemText() {
		init();
	}
	
	protected void init() {
		SVGText.setDefaultStyle(this);
	}

	public SVGChemText(SVGText text) {
		SVGChem.deepCopy(text, this);
	}
	
//	SVGChemText(SVGText text) {
//		SVGChem.deepCopy(text, this);
//	}
	
	/**
	 * 
	 * @param xy
	 * @param s
	 * @deprecated
	 */
	public SVGChemText(Real2 xy, String s) {
	}
	
	SVGElement getParentSVG() {
		SVGElement parentSVG = null;
		ParentNode parentNode = this.getParent();
		if (parentNode != null && parentNode instanceof SVGElement) {
			parentSVG = (SVGElement) parentNode;
		}
		return parentSVG;
	}

	public void processTextAndSingleCharactersAndAddBoxesAndScales(int serial, GraphicsConverterTool svg2CmlTool) {
		this.addCharHeightWidthBoxAndAddFontScales(svg2CmlTool);
		this.addCharacterSWAnchor(serial);
	}
	
	public void addCharHeightWidthBoxAndAddFontScales(GraphicsConverterTool svg2CMLTool) {
		charHeight = svg2CMLTool.getCharHeightAdjustmentFactor();
		charWidth = 0.0;
		String text = this.getText();
		if (text.length() == 1) {
			char ch = text.charAt(0);
			Double charWidthD = GraphicsConverterTool.fontWidths[(int)ch];
			if (charWidthD != null) {
				charWidth = charWidthD.doubleValue(); 
			}
		} else {
			LOG.debug("multiple character? "+text+"/"+(int)text.charAt(0)+"/"+(int)text.charAt(1));
		}
		double x = this.getX();
		double y = this.getY();
		double fontHeight = this.getOrCreateFontHeight();
		double fontScale = fontHeight * 
		    svg2CMLTool.getFontWidthAdjustmentFactor(); // 
		charWidth *= fontScale;
		charHeight *= fontScale;
		SVGElement parentSVG = this.getParentSVG();
		if (parentSVG != null) {
			addCharacterRectangle(x, y, charWidth, charHeight, (SVGElement) parentSVG, text);
		}
	}
	
	public double getOrCreateFontHeight() {
		double fontHeight = this.getFontSize();
		if (Double.isNaN(fontHeight)) {
			fontHeight = 10.0;
		}
		return fontHeight;
	}

	/**
	 * 
	 * @param element
	 * @return texts
	 */
	public static List<SVGChemText> getTextList(SVGChemElement element) {
		Nodes nodes = ((Element) element).query(".//svg:text", SVG_XPATH);
		List<SVGChemText> textList = new ArrayList<SVGChemText>();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof SVGChemText) {
				SVGChemText text =(SVGChemText) nodes.get(i);
//				LOG.debug(text.getFontSize());
				textList.add(text);
			}
		}
		return textList;
	}
	
	public static void mergeSingleCharactersIntoTextStrings(List<SVGChemText> textList1) {
		List<List<SVGChemText>> textListList = new ArrayList<List<SVGChemText>>();
		// assign each text to a separate list
		for (SVGChemText text : textList1) {
			List<SVGChemText> textList = new ArrayList<SVGChemText>();
			textList.add(text);
			textListList.add(textList);
			text.setTextList(textList);
		}
		// now merge neighbouring lists
		boolean change = true;
		while (change) {
			change = false;
			iloop:
			for (int i = 0; i < textListList.size(); i++) {
				List<SVGChemText> listi = textListList.get(i);
				for (int j = i+1; j < textListList.size(); j++) {
					List<SVGChemText> listj = textListList.get(j);
					if (leftSideOfArg1touchesRightSideOfArg2(listj, listi)) {
						mergeAndDeleteArg1(listj, listi, textListList);
						change = true;
						break iloop;
					}
				}
			}
		}
		
		for (List<SVGChemText> textList : textListList) {
			// have to do these before merging
			SVGChemText firstCharacter = textList.get(0);
			SVGCircle leftAnchorCircle = firstCharacter.getMidBoxCircle("#77ff77", "#ff0000");
			SVGChemText lastCharacter = textList.get(textList.size()-1);
			SVGCircle rightAnchorCircle = lastCharacter.getMidBoxCircle("#7777ff", "#ff0000");
			textList.get(0).mergeString(textList);
			textList.get(0).setLeftAnchor(leftAnchorCircle);
			textList.get(0).setRightAnchor(rightAnchorCircle);
		}
	}
	
	
	
	private void mergeString(List<SVGChemText> textList) {
		String s = this.getText();
		int i = 0;
		for (SVGChemText text : textList) {
			if (i++ != 0) {
				s += text.getText();
				text.detach();
			}
			detach(text.rect);
			detach(text.circle);
			detach(text.serialText);
		}
		this.setText(s);
//		LOG.debug(">"+s+"<>"+this.getText());
		SVGChemText last = textList.get(textList.size()-1);
		this.charWidth = last.getX()+last.charWidth - this.getX();
		this.addCharacterRectangle(this.getX(), this.getY(), charWidth, charHeight, 
				this.getParentSVG(), this.getText());
	}
	
	private void detach(Node node) {
		if (node != null) {
			node.detach();
		} else {
//			LOG.debug("NULL DETACH........");
		}
	}
	
	public void interpretText() {
		String s = this.getText().trim();
		if (processElement(s)) {
			setFill("#ff0000");
		} else if (processSubscriptedHydrogens(s)) {
			setFill("#77ff77");
		} else if (processInteger(s)) {
			setFill("#ff00ff");
		} else if (processRomanNumeral(s)) {
			setFill("#00ffff");
		} else if (processGroup(s, ChemicalType.getTypeMap("group"))) {
			setFill("#0000ff");
		} else if (s.equals(S_PLUS)) {
			setFill("#00ff00");
		} else if (s.equals(S_MINUS)) {
			setFill("#00ff00");
		} else if (s.equals(S_EMPTY)) {
			LOG.debug("Empty text");
		} else {
			LOG.debug("UNKNOWN text: "+s);
			setFill("#777777");
		}
	}

	private boolean processElement(String text) {
		chemicalElement = ChemicalElement.getChemicalElement(text);
		boolean isAtom = (chemicalElement != null);
		if (isAtom) {
			createAndPlotAtom(text, hydrogenSubscripts);
		}
		return isAtom;
	}

	private CMLAtom createAndPlotAtom(String text, boolean hydrogenSubscripts) {
		SVGCircle circle = this.getMidBoxCircle("#77ff77", "#ff0000");
		CMLAtom atom = new CMLAtom();
		this.setAtom(atom);
		SVGChemSVG.plotAtom((SVGElement)this.getParent(), atom, SVG2CMLTool.ELEMENT_COLOR, 
				circle.getXY(), text, SVG2CMLTool.ELEMENT_OPACITY);
		return atom;
	}

	private boolean processSubscriptedHydrogens(String text) {
		boolean isAtom = false;
		if (mayBeSubscriptedHydrogen(text)) {
			if (hydrogenSubscripts || ChemicalElement.getChemicalElement(text) == null) {
	    		hydrogenSubscripts = true;
				chemicalElement = ChemicalElement.getChemicalElement("H");
				CMLAtom atom = createAndPlotAtom("H", hydrogenSubscripts);
				CMLLabel label = new CMLLabel();
				label.setCMLValue(text);
				atom.addLabel(label);
				isAtom = true;
			}
		}
		return isAtom;
	}

	/** is string of form Ha... Hd, etc.
	 * 
	 * @param text
	 * @return true if satisfied
	 */
	public static boolean mayBeSubscriptedHydrogen(String text) {
		boolean isSH = false;
		if (text.length() == 2 && text.startsWith("H")) {
			char h2 = text.charAt(1);
			isSH = (h2  >= 'a' && h2 <= 'z') ||
			    h2 >= '0' && h2 <= '9' ||
			    h2 == '\'';
		}
		return isSH;
	}

	private boolean processInteger(String text) {
		try {
			number = new Integer(text);
		} catch (NumberFormatException e) {
			//
		}
		return number != null;
	}
	
	private boolean processRomanNumeral(String text) {
		romanNumber = 0;
		if (text.matches(Util.LOWER_ROMAN_REGEX)) {
			getRomanNumeral(text);
		} else if (text.matches(Util.UPPER_ROMAN_REGEX)) {
			text = text.toLowerCase();
			getRomanNumeral(text);
		}
		return romanNumber != 0;
	}

	private void getRomanNumeral(String text) {
		for (int i = 0; i < Util.LOWER_ROMAN_NUMERAL.length; i++) {
			if (Util.LOWER_ROMAN_NUMERAL[i].equals(text)) {
				romanNumber = i+1;
				break;
			}
		}
	}
	
	private boolean processGroup(String text, ChemicalTypeMap groupMap) {
		if (groupMap == null) {
			LOG.debug("NULL group Map");
		}
		GroupType group = (GroupType) groupMap.get(text);
		boolean found = (group != null);
		if (found) {
			SVGCircle circle = null;
			if (text.equals(group.getLeftString())) {
				circle = this.getLeftAnchor();
			} else {
				this.setLeftAnchor(null);
			}
			if (text.equals(group.getRightString())) {
				circle = this.getRightAnchor();
			} else {
				this.setRightAnchor(null);
			}
			if (circle == null) {
				throw new RuntimeException("Cannot find circle/groupText ...");
			}
			Real2 xy2 = circle.getXY();
			CMLAtom atom = new CMLAtom();
			this.setAtom(atom);
			SVGChemSVG.plotAtom(this.getParentSVG(), atom, SVG2CMLTool.R_COLOR, xy2, "R", SVG2CMLTool.R_OPACITY);
		}
		return found;
	}
	
	private SVGCircle getMidBoxCircle(String fill, String stroke) {
		double x = this.getX()+charWidth*0.5;
		double y = this.getY()-charHeight*0.5;
//		SVGElement parentSVG = this.getParentSVG();
//		circle = null;
//		if (parentSVG != null) {
			circle = new SVGChemCircle(new SVGCircle(
					new Real2(x, y), 2.0));
//			parentSVG.appendChild(circle);
			circle.setFill(fill);
			circle.setStroke(stroke);
			circle.setStrokeWidth(0.6);
//		}
		return circle;
	}

	private static boolean leftSideOfArg1touchesRightSideOfArg2(
			List<SVGChemText> listI, List<SVGChemText> listJ) {
		boolean touch = false;
		SVGChemText firstI = listI.get(0);
		SVGChemText lastJ = listJ.get(listJ.size()-1);
		double firstIleft = firstI.getX();
		double firstIbot = firstI.getY();
		double firstItop = firstI.getY() - firstI.charHeight;
		double lastJright = lastJ.getX()+lastJ.charWidth;
		double lastJbot = lastJ.getY();
		double lastJtop = lastJ.getY() - lastJ.charHeight;
		if (firstIbot < lastJtop ||
			firstItop > lastJbot) {
		} else {
			double j2Ix = firstIleft - lastJright;
			if (Math.abs(j2Ix) < BOXDIFF) {
				touch = true;
			}
		}
		return touch;
	}

	private static void mergeAndDeleteArg1(
			List<SVGChemText> listi, List<SVGChemText> listj, List<List<SVGChemText>> textListList) {
		listj.addAll(listi);
		textListList.remove(listi);
		listi.removeAll(listi);
	}
	
	private void addCharacterSWAnchor(int serial) {
		double x = this.getX();
		double y = this.getY();

		circle = new SVGChemCircle(new SVGCircle(new Real2(x, y), 0.15));
		circle.setFill("#000000");
		circle.setStroke("#000000");
        serialText = new SVGText(new Real2(x, y), ""+serial);
		serialText.setFontSize(SERIAL_FONT_SIZE);
		SVGElement parentSVG = this.getParentSVG();
		if (parentSVG != null) {
			parentSVG.appendChild(circle);
			parentSVG.appendChild(serialText);
		}
	}

	private void addCharacterRectangle(double x, double y, 
			double charWidth, double charHeight, 
			SVGElement parentSVG, String text) {
		if (charHeight > 0 && charWidth > 0) {
	        rect = new SVGRect(new Real2(x, y-charHeight), 
	        		new Real2(x+charWidth, y));
			rect.setFill("none");
			rect.setStroke("#000000");
			rect.setStrokeWidth(0.3);
	        parentSVG.appendChild(rect);
        } else {
        	LOG.debug("No height or width: "+text+" / "+(int)text.charAt(text.length()-1));
        }
	}

	/**
	 * 
	 * @return textlist
	 */
	public List<SVGChemText> getTextList() {
		return textList;
	}

	/**
	 * 
	 * @param textList
	 */
	public void setTextList(List<SVGChemText> textList) {
		this.textList = textList;
	}

	/**
	 * 
	 * @return circle
	 */
	public SVGChemCircle getCircle() {
		return circle;
	}

	/**
	 * 
	 * @param line
	 */
	public void addLine(SVGChemLine line) {
		if (lineList == null) {
			lineList = new ArrayList<SVGChemLine>();
		}
		lineList.add(line);
	}

	/**
	 * 
	 * @return linelist
	 */
	public List<SVGChemLine> getLineList() {
		return lineList;
	}

	/**
	 * 
	 * @return atom
	 */
	public CMLAtom getAtom() {
		return atom;
	}

	/**
	 * 
	 * @param atom
	 */
	public void setAtom(CMLAtom atom) {
		this.atom = atom;
	}
	

	private boolean hydrogenSubscripts;

	/**
	 * 
	 * @return leftAnchor
	 */
	public SVGCircle getLeftAnchor() {
		return leftAnchor;
	}

	/**
	 * @return left anchor coordinates
	 */
	public Real2 getLeftAnchorXY() {
		return (leftAnchor == null) ? null : leftAnchor.getXY();
	}

	/**
	 * @param leftAnchor
	 */
	public void setLeftAnchor(SVGCircle leftAnchor) {
		addCircle(leftAnchor, this.leftAnchor);
		this.leftAnchor = leftAnchor;
	}

	/**
	 * 
	 * @return get right anchor
	 */
	public SVGCircle getRightAnchor() {
		return rightAnchor;
	}

	/**
	 * get right anchor coordinates
	 * @return coords or null
	 */
	public Real2 getRightAnchorXY() {
		return (rightAnchor == null) ? null : rightAnchor.getXY();
	}

	/**
	 * @param rightAnchor
	 */
	public void setRightAnchor(SVGCircle rightAnchor) {
		addCircle(rightAnchor, this.rightAnchor);
		this.rightAnchor = rightAnchor;
	}
	
	private void addCircle(SVGElement circle, SVGElement oldCircle) {
		ParentNode parent = this.getParent();
		if (oldCircle != null) {
			oldCircle.detach();
		}
		if (circle != null) {
			parent.appendChild(circle);
		}
	}
}
