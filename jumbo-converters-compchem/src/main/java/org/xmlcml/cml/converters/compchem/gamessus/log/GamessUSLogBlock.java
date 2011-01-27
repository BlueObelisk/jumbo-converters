package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.molutil.ChemicalElement;


public class GamessUSLogBlock extends AbstractBlock {
	public static Logger LOG = Logger.getLogger(GamessUSLogBlock.class);
	
	private static final String GMAX = "gmax";
	/*
	 * keywords in legacy input
	 */
	public static final String CISVEC = "CISVEC";

	private static final String DICTREF_NEXT = "dictRefNext";

	public GamessUSLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		jumboReader = new JumboReader(this.getDictionary(), abstractCommon.getPrefix(), lines);
		if (getBlockName() == null) {
			throw new RuntimeException(" Null blockname");
		} else if (CISVEC.equals(getBlockName())) {
//			makeCisvec();
		} else {
//			debug(UNKNOWN);
			System.err.println("Unknown blockname: "+getBlockName());
//			throw new RuntimeException("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			checkIdAndAdd(element, getBlockName().toLowerCase());
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	
	private void makeAnonymous() {
		CMLModule module = new CMLModule();
		jumboReader.setParentElement(module);
		String line = jumboReader.peekLine();
		if (line.startsWith("foo")) {
//			anonResultsFrom();
//		} else if (line.startsWith(COORDS_ORBS)) {
//			anonCoordsOrbs();
//		} else if (line.startsWith(DATA_FROM_NSERCH)) {
//			anonNserch();
//		} else if (line.startsWith(CLOSED_SHELL) || line.startsWith(OPEN_SHELL)) {
//			anonClosedOpenShell();
//		} else if (line.startsWith(OPTIMIZED_RHF)) {
//			anonOptimisedRHF();
//		} else if (line.startsWith(" POPULATION ANALYSIS")) {
//			anonPopulationAnalysis();
		} else {
			preserveText();
		}
		element = module;
	}
	

}
