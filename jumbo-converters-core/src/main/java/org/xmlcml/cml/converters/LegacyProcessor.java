package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLCml;

public abstract class LegacyProcessor {
	private static final Logger LOG = Logger.getLogger(LegacyProcessor.class);

	private List<AbstractBlock> blockList;
	protected List<String> lines;
	protected int lineCount = 0;
	protected CMLElement cmlElement;
	
	protected LegacyProcessor() {
		this.blockList = new ArrayList<AbstractBlock>();
	}
	
	public void read(List<String> lines) {
		this.lines = lines;
		lineCount = 0;
		while (lineCount < lines.size()) {
			AbstractBlock block = readBlock(lines);
			if (block != null) {
				blockList.add(block);
			}
		}
		LOG.debug("Finished reading blocks: "+blockList.size());
	}

	public List<CMLElement> getBlockList() {
		List<CMLElement> cmlList = new ArrayList<CMLElement>();
		if (blockList != null) {
			for (AbstractBlock block : blockList) {
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
	
	public CMLElement getCMLElement() {
		cmlElement = new CMLCml();
		for (AbstractBlock block : blockList) {
			CMLElement element = block.getElement();
			cmlElement.appendChild(element);
		}
		return cmlElement;
	}
}
