package org.xmlcml.cml.converters.compchem.amber.mdout;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;

/**
 * @author pm286
 *
 */
public class AmberMdoutProcessor extends LegacyProcessor {
	
	public AmberMdoutProcessor() {
		super();
	}
	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		return null;
	}

	public AbstractBlock createAbstractBlock() {
		return new AmberMdoutBlock(blockContainer);
	}

	@Override
	protected void preprocessBlocks(CMLElement element) {
	}
	

	@Override
	protected void postprocessBlocks() {
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new AmberMdoutBlock(blockContainer);
	}
	
}

