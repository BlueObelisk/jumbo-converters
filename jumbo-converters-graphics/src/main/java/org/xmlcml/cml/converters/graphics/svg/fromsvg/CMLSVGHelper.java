package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.dicts.ChemicalType;
import org.xmlcml.cml.converters.dicts.GroupType;
import org.xmlcml.cml.converters.dicts.ChemicalType.Type;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemSVG;
import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemText;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.GeometryTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.molutil.ChemicalElement;

public class CMLSVGHelper implements CMLConstants {
	private static Logger LOG = Logger.getLogger(CMLSVGHelper.class);

	private GraphicsConverterTool svgConverter;
	private Map<String, ChemicalType> groupMap = null;
	private List<CMLAtom> atomList;
	private SVGChemSVG svgChem;
	private CMLMoleculeList groupList;
	private double averageLength;
	private CMLFormula givenFormula;
	protected CMLMolecule molecule;
	protected CMLCml cmlCml;
	
	public CMLSVGHelper(SVG2CMLTool svgConverter) {
//		super(svgConverter);
		this.svgConverter = svgConverter;
		this.atomList = svgConverter.getAtomList();
		this.svgChem = svgConverter.getSvgChem();
		this.groupList = null;
		this.averageLength = svgConverter.getAverageLength();
		this.givenFormula = svgConverter.getGivenFormula();

	}
	
    protected void processAfterParsing() {
		// because drawing uses LH axes.
		labelAtomsAndGroups();
		getOrCreateBondsAndAddOrders();
		flipYCoordinates();
    }
    
    protected void process1() {
    	joinGroupsAndRenumberAtoms();
    }
    
	public void labelAtomsAndGroups() {
    	List<SVGChemText> textList = SVGChemText.getTextList(svgChem);
		for (CMLAtom atom : atomList) {
//			String id = atom.getId();
    		molecule.addAtom(atom);
    		// crude - we need a map
    		for (SVGChemText text : textList) {
    			CMLAtom atom1 = text.getAtom();
    			if (atom.equals(atom1)) {
        			double x = text.getX();
        			double y = text.getY();
        			atom.setX2(x);
        			atom.setY2(y);
    				String textS = text.getValue();
    				if (textS.equals("C")) {
    					// chemical element
    				} else if (textS.equals("R")) {
    					throw new RuntimeException("Unexpected R: "+textS);
    				} else if (ChemicalElement.getChemicalElement(textS) != null) {
    					// chemical element
    				} else if (SVGChemText.mayBeSubscriptedHydrogen(textS)) {
    					// chemical element
    				} else {
	    				createAndProcessLabel(atom, textS);
    				}
    			}
    		}
    	}
//		molecule.debug("XXXXXXXX");
	}

    public void flipYCoordinates() {
    	double ymax = Double.NEGATIVE_INFINITY;
    	double ymin = Double.POSITIVE_INFINITY;
    	for (CMLAtom atom : atomList) {
    		double y = atom.getY2();
    		ymax = Math.max(ymax, y);
    		ymin = Math.min(ymin, y);
    	}
    	for (CMLAtom atom : atomList) {
    		double y = atom.getY2();
    		y = (ymax - y) + ymin;
    		atom.setY2(y);
    	}
    }
    
	private void getOrCreateBondsAndAddOrders() {
    	List<SVGChemLine> lineList = SVGChemLine.getLineList(svgChem);
		for (SVGChemLine line : lineList) {
    		if (line.getOrder() == null) {
    			line.setOrder(CMLBond.SINGLE);
    		}
    		CMLBond bond = line.getOrCreateBond(atomList, averageLength);
    		if (bond != null) {
    			try {
    				molecule.addBond(bond);
    			} catch (RuntimeException e) {
    				System.err.println("BUG: "+e);
    			}
    		} else {
    			if (false) {
//    				tryExtendedBond(line);
    			}
    			SVGChemLine.debugLine("NULL BOND IN CMLHELPER", line);
    		}
    	}
	}
	
	private void createAndProcessLabel(CMLAtom atom, String textS) {
		GroupType group = lookupGroup(textS);
		if (group == null) {
			System.err.println(">>>>>>>"+svgConverter.getFileId()+">>>>>>>> Cannot find group: "+textS);
		} else {
			CMLLabel label = new CMLLabel();
			if (textS.equals(group.getRightString())) {
				label.addAttribute(new Attribute("convention", "cml:abbrevRight"));
			}
			label.setCMLValue(textS);
			atom.appendChild(label);
			CMLMolecule groupMolecule = group.getMolecule();
			if (groupMolecule != null) {
				// copy to avoid referencing primary object
				groupMolecule = new CMLMolecule(groupMolecule);
				addGroupToGroupList(groupMolecule, atom);
			}
		}
	}
	
    private void joinGroupsAndRenumberAtoms() {
    	CMLFormula formula = new CMLFormula(molecule);
    	Nodes groupAtoms = molecule.query(".//cml:atom[@elementType='R']", CML_XPATH);
    	
    	for (int i = 0; i < groupAtoms.size(); i++) {
    		CMLAtom groupAtom = (CMLAtom) groupAtoms.get(i);
    		String ref = groupAtom.getRef();
    		Nodes nodes = cmlCml.query("cml:moleculeList/cml:molecule[@id='"+ref+"']", CML_XPATH);
    		if (nodes.size() == 0) {
    			throw new RuntimeException("Missing reference for molecule: "+ref);
    		} else {
	    		CMLMolecule groupMolecule = (CMLMolecule) nodes.get(0);
	    		CMLFormula groupFormula = groupMolecule.getFormulaElements().get(0);
	    		if (groupFormula != null) {
	    			formula = formula.createAggregatedFormula(groupFormula);
	    		} else {
	    			LOG.debug("Cannot find formula for group: "+groupMolecule.getNameElements().get(0).getValue());
	    			// cannot find formula, exit
	    			formula = null;
	    			break;
	    		}
    		}
    	}
    	String conciseS = (formula == null) ? null : formula.getConcise();
    	if (conciseS != null) {
	    	conciseS = conciseS.replaceAll(" R [0-9]+", S_EMPTY);
	    	LOG.debug("FORMULA           "+conciseS);
	    	formula = CMLFormula.createFormula(conciseS);
	    	molecule.appendChild(formula);
	    	if (givenFormula != null) {
	    		String diff = givenFormula.getDifference(formula).getConcise();
	    		if (diff != null) {
	    			LOG.debug("................DIFFFFFFFFFFFFFFFFFFFFFFF    "+diff);
	    		}
	    	}
    	}
//		LOG.debug("Formula: "+((formula == null) ? null : formula.getConcise()));
    }
    
	private void addGroupToGroupList(CMLMolecule groupMolecule, CMLAtom atom) {
		if (groupList == null) {
			groupList = new CMLMoleculeList();
			cmlCml.appendChild(groupList);
		}
		groupList.addMolecule(groupMolecule);
		int size = groupList.getChildCMLElements().size();
		String id = "r"+size;
		groupMolecule.setId(id);
		atom.setRef(id);
	}
	
	public CMLCml createCML() {
		cmlCml = new CMLCml();
		molecule = new CMLMolecule();
		molecule.setId("foo");
		processAfterParsing();
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.NO_EXPLICIT_HYDROGENS);
		GeometryTool geometryTool = new GeometryTool(molecule);
		try {
			geometryTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, HydrogenControl.USE_EXPLICIT_HYDROGENS);
		} catch (Exception e) {
			System.err.println("ERROR "+e+" in "+svgConverter.getFileId());
		}
		cmlCml.appendChild(molecule);
		return cmlCml;
	}
    
	private GroupType lookupGroup(String groupName) {
		if (groupMap == null) {
			groupMap = ChemicalType.getTypeMap(Type.GROUP.value);
		}
		return (GroupType) groupMap.get(groupName);
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}
}
