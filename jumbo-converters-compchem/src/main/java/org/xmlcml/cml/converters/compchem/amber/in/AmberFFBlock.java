package org.xmlcml.cml.converters.compchem.amber.in;

import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;

public class AmberFFBlock extends AbstractBlock {

	public AmberFFBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new AmberCommon();
	}

	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}
}
