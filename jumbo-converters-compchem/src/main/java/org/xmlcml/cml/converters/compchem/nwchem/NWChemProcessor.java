package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.List;

import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.LegacyProcessor;

/**
 * @author pm286
 *
 */
public class NWChemProcessor extends LegacyProcessor {

	
	public NWChemProcessor() {
	}
	
	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		String line = lines.get(lineCount);
//		if (!line.startsWith(BLOCK)) {
//			throw new RuntimeException("expected block at line "+lineCount+"; found: "+line);
//		}
		NWChemBlock block = createBlock();
		block.convertToRawCML();
		return block;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private NWChemBlock createBlock() {
		NWChemBlock block = new NWChemBlock(blockContainer);
		String line = lines.get(lineCount);
		block.setBlockName(line.substring(1));
		lineCount++;
		while (lineCount < lines.size() /*&& !lines.get(lineCount).startsWith(BLOCK)*/) {
			block.add(lines.get(lineCount++));
		}
		return block;
	}

	@Override
	protected void preprocessBlocks() {
		// not required
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
	}
	
}
