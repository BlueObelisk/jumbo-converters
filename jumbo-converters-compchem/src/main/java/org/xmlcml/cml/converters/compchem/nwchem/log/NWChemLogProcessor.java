package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;

/**
 * @author pm286
 *
 */
public class NWChemLogProcessor extends LegacyProcessor {
	
	public NWChemLogProcessor() {
		super();
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new NWChemCommon();
	}

	@Override
	protected String getTemplateResourceName() {
		return "org/xmlcml/cml/converters/compchem/nwchem/log/templateList.xml";
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
		return new NWChemLogBlock(blockContainer);
	}

	@Override
	protected void preprocessBlocks(CMLElement element) {
	}
	

	@Override
	protected void postprocessBlocks() {
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new NWChemLogBlock(blockContainer);
	}
	
}

