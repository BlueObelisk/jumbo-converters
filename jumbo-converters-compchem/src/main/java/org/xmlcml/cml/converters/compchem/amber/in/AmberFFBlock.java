package org.xmlcml.cml.converters.compchem.amber.in;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.Template;
import org.xmlcml.cml.converters.compchem.amber.AmberCommon;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.interfacex.HasDictRef;

public class AmberFFBlock extends AbstractBlock {

	
	
	public AmberFFBlock(BlockContainer blockContainer) {
		super(blockContainer);
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new AmberCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		jumboReader = new JumboReader(this.getDictionary(), abstractCommon.getPrefix(), lines);
		String blockName = getBlockName();
		if (blockName == null) {
			throw new RuntimeException("null block name");
		} else if (UNKNOWN_BLOCK.equals(blockName)){
			LOG.warn("Unknown block");
		}
		Template blockTemplate = legacyProcessor.getTemplateByBlockName(blockName.trim());
		if (blockTemplate != null) {
			blockTemplate.markupBlock(this);
			blockName = blockTemplate.getId();
			LOG.info("BLOCK: "+blockName);
		} else if (UNKNOWN_BLOCK.equals(blockName)) {
			CMLModule module = this.makeModule();
			Template.tidyUnusedLines(jumboReader, module);
		} else {
			System.err.println("Unknown blockname: "+blockName);
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			((HasDictRef)element).setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), blockName));
		} else {
			System.err.println("null element: "+blockName);
		}
	}


}
