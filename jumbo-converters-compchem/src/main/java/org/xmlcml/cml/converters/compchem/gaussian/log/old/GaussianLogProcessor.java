package org.xmlcml.cml.converters.compchem.gaussian.log.old;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

/**
 * @author pm286
 *
 */
public class GaussianLogProcessor extends LegacyProcessor {
	
	public GaussianLogProcessor() {
		super();
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
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
		return new GaussianLogBlock(blockContainer);
	}

	@Override
	protected void preprocessBlocks(CMLElement element) {
	}
	

	@Override
	protected void postprocessBlocks() {
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new GaussianLogBlock(blockContainer);
	}
	
}

