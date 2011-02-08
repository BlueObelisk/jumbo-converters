package org.xmlcml.cml.converters.compchem.amber.mdout;

import org.xmlcml.cml.converters.AbstractBlock;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;

public class AmberMdoutBlock extends AbstractBlock {

	public AmberMdoutBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	public void convertToRawCML() {
		super.convertToRawCMLDefault();
	}
}
