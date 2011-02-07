package org.xmlcml.cml.converters.compchem.gaussian.log;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

public class GaussianLogBlock extends AbstractBlock {
	private final static Logger LOG = Logger.getLogger(GaussianLogBlock.class);

	public GaussianLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}

}
