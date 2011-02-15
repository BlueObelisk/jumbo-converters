package org.xmlcml.cml.converters.compchem.nwchem.log;

import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;

public class NWChemLogBlock extends AbstractBlock {

	public NWChemLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}

}
