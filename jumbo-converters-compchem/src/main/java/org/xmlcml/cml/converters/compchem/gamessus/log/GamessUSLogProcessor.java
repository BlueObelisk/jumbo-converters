package org.xmlcml.cml.converters.compchem.gamessus.log;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.element.CMLScalar;

/**
 * @author pm286
 *
 */
public class GamessUSLogProcessor extends LegacyProcessor {

	public GamessUSLogProcessor() {
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		AbstractBlock block = null;
		String line = lines.get(lineCount);
		if (line.startsWith(GamessUSCommon.KEYWORD)) {
			block = createBlock();
//		} else if (line.startsWith(MINUS3)) {
//			block = createAnonymousBlock();
		} else {
			block = createAnonymousBlock();
		}
		block.convertToRawCML();
		return block;
	}
	
	protected AbstractBlock readBlock(CMLScalar scalar) {
		return null;
	}

	private AbstractBlock createAnonymousBlock() {
		AbstractBlock block = new GamessUSLogBlock(blockContainer);
//		int lineCount0 = lineCount;
		while (lineCount < lines.size()) {
			String line = lines.get(lineCount);
			if (line.startsWith(GamessUSCommon.KEYWORD)) {
				break;
//			} else if (lineCount > lineCount0 && line.startsWith(MINUS3)) {
//				break;
			}
			block.add(line);
			lineCount++;
		}
		block.setBlockName(_ANONYMOUS);
		return block;
	}
	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private AbstractBlock createBlock() {
		AbstractBlock block = new GamessUSLogBlock(blockContainer);
		// modification logic goes here
		return block;
	}
	
	@Override
	protected void preprocessBlocks(CMLElement element) {
		// unused
	}
	@Override
	protected void postprocessBlocks() {
		// unused
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new GamessUSLogBlock(blockContainer);
	}
	
}
