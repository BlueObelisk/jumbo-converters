package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.element.CMLMolecule;

public class BlockContainer {
	private List<AbstractBlock> blockList;
	private CMLMolecule molecule;
	private int currentBlockNumber = -1;
	private LegacyProcessor legacyProcessor;
	
	public BlockContainer(LegacyProcessor legacyProcessor) {
		this.blockList = new ArrayList<AbstractBlock>();
		this.legacyProcessor = legacyProcessor;
	}
	
	public LegacyProcessor getLegacyProcessor() {
		return legacyProcessor;
	}

	public List<AbstractBlock> getBlockList() {
		return blockList;
	}
	
	public void add(AbstractBlock block) {
		this.blockList.add(block);
		block.setBlockContainer(this);
		if (block.getMolecule() != null) {
			this.molecule = block.getMolecule();
		}
	}
	public int getCurrentBlockNumber() {
		return currentBlockNumber;
	}
	public void setCurrentBlockNumber(int currentBlockNumber) {
		this.currentBlockNumber = currentBlockNumber;
	}
	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}
	public CMLMolecule getMolecule() {
		return molecule;
	}
	public int size() {
		return blockList.size();
	}
	
	public AbstractBlock getBlock(String blockName) {
		AbstractBlock theBlock = null;
		if (blockName != null) {
			for (AbstractBlock block : blockList) {
				if (blockName.equals(block.getBlockName())) {
					theBlock = block;
				}
			}
		}
		return theBlock;
	}
}
