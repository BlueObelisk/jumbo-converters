package org.xmlcml.cml.converters.compchem.foo;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.element.CMLScalar;

/**
 * supports a simple typical structure such as:
 * 
 * $molecule
 *   3  2
 *  C    0.0   0.0   0.0
 *  O    1.2   0.0   0.0
 *  S   -1.4   0.0   0.0
 *   1  2
 *   1  3
 * $dipole
 *   1.234
 * 
 * @author pm286
 *
 */
public class FooProcessor extends LegacyProcessor {

	public static final String BLOCK = "$";
	
	public FooProcessor() {
	}
	
	@Override
	protected String getTemplateResourceName() {
		return "org/xmlcml/cml/converters/compchem/foo/templateList.xml";
	}

	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		String line = lines.get(lineCount);
		if (!line.startsWith(BLOCK)) {
			throw new RuntimeException("expected block at line "+lineCount+"; found: "+line);
		}
		FooBlock block = createBlock();
		block.convertToRawCML();
		return block;
	}

	protected AbstractBlock readBlock(CMLScalar scalar) {
		return null;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private FooBlock createBlock() {
		FooBlock block = new FooBlock(blockContainer);
		String line = lines.get(lineCount);
		block.setBlockName(line.substring(1));
		lineCount++;
		while (lineCount < lines.size() && !lines.get(lineCount).startsWith(BLOCK)) {
			block.add(lines.get(lineCount++));
		}
		return block;
	}

	@Override
	protected void preprocessBlocks(CMLElement element) {
		// not required
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new FooBlock(blockContainer);
	}
	
}
