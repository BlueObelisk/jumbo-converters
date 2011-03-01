package org.xmlcml.cml.converters.compchem.gaussian.log.old;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;

public class GaussianLogBlock extends AbstractBlock {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GaussianLogBlock.class);

	public GaussianLogBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}

}
