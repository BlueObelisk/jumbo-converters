package org.xmlcml.cml.converters.compchem.qespresso.log;

import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.qespresso.QespressoCommon;

public class QuantumEspressoLogBlock extends AbstractBlock {

	public QuantumEspressoLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new QespressoCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}

}
