package org.xmlcml.cml.converters.compchem.gaussian.logold;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

public class GaussianLogProcessor extends LegacyProcessor {

	@Override
	protected void preprocessBlocks(CMLElement rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void postprocessBlocks() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getTemplateResourceName() {
		return "org/xmlcml/cml/converters/compchem/gaussian/log/templateList.xml";
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractBlock readBlock(CMLElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new GaussianLogBlock(blockContainer);
	}

}
