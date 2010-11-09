package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;

public abstract class LegacyProcessor {
	private static final Logger LOG = Logger.getLogger(LegacyProcessor.class);

	public static final String _ANONYMOUS = "_anonymous_";

	protected BlockContainer blockContainer;
	protected List<String> lines;
	protected int lineCount = 0;
	protected CMLElement cmlElement;
	protected AbstractCommon abstractCommon;
	
	protected LegacyProcessor() {
		this.blockContainer = new BlockContainer();
		abstractCommon = getCommon();
	}
	
	public void read(List<String> lines) {
		this.lines = lines;
		preprocessBlocks();
		lineCount = 0;
		while (lineCount < this.lines.size()) {
			AbstractBlock block = readBlock(this.lines);
			if (block != null) {
				blockContainer.add(block);
			}
		}
		postprocessBlocks();
		LOG.debug("Finished reading blocks: "+blockContainer.size());
	}

	public void read(CMLElement element) {
		List<Node> scalarNodes = CMLUtil.getQueryNodes(element, "*");
//		preprocessBlocks();
		for (Node scalarNode : scalarNodes) {
			AbstractBlock block = readBlock((CMLScalar) scalarNode);
			if (block != null) {
				blockContainer.add(block);
			}
		}
//		postprocessBlocks();
		LOG.debug("Finished reading blocks: "+blockContainer.size());
	}

	/** processing before blocks are read
	 * often null
	 */
	protected abstract void preprocessBlocks();

	/** processing after blocks are read
	 * often null
	 */
	protected abstract void postprocessBlocks();

	protected abstract AbstractCommon getCommon();
	
	public List<CMLElement> getBlockList() {
		List<CMLElement> cmlList = new ArrayList<CMLElement>();
		if (blockContainer != null) {
			for (AbstractBlock block : blockContainer.getBlockList()) {
				cmlList.add(block.getElement());
			}
		}
		return cmlList;
	}

	/**
	 * must update lineCount
	 * @param lines
	 * @return
	 */
	protected abstract AbstractBlock readBlock(List<String> lines);
	
	protected abstract AbstractBlock readBlock(CMLScalar scalar);
	
	public CMLElement getCMLElement() {
		cmlElement = new CMLCml();
		for (AbstractBlock block : blockContainer.getBlockList()) {
			CMLElement element = block.getElement();
			if (element != null) {
				cmlElement.appendChild(element);
			}
		}
		return cmlElement;
	}

	protected void processAnonymousBlocks() {
		for (AbstractBlock block : blockContainer.getBlockList()) {
			if (_ANONYMOUS.equals(block.getBlockName())) {
				block.convertToRawCML();
			}
		}
	}
}
