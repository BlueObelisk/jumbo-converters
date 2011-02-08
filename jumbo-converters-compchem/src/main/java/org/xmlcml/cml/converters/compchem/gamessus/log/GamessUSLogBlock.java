package org.xmlcml.cml.converters.compchem.gamessus.log;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;


public class GamessUSLogBlock extends AbstractBlock {
	public static Logger LOG = Logger.getLogger(GamessUSLogBlock.class);
	
	public GamessUSLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}
	
//	private void makeAnonymous() {
//		CMLModule module = new CMLModule();
//		jumboReader.setParentElement(module);
//		String line = jumboReader.peekLine();
//		if (line.startsWith("foo")) {
////			anonResultsFrom();
////		} else if (line.startsWith(COORDS_ORBS)) {
////			anonCoordsOrbs();
////		} else if (line.startsWith(DATA_FROM_NSERCH)) {
////			anonNserch();
////		} else if (line.startsWith(CLOSED_SHELL) || line.startsWith(OPEN_SHELL)) {
////			anonClosedOpenShell();
////		} else if (line.startsWith(OPTIMIZED_RHF)) {
////			anonOptimisedRHF();
////		} else if (line.startsWith(" POPULATION ANALYSIS")) {
////			anonPopulationAnalysis();
//		} else {
//			preserveText();
//		}
//		element = module;
//	}
	

}
