package org.xmlcml.cml.converters.text;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;


public class TemplateProcessor extends LegacyProcessor {

	private Template template;
	
	public TemplateProcessor(Template template) {
		super();
		this.template = template;
	}
	
	@Override
	protected void readTemplates() {
		// no-op
	}

	
	public Element applyMarkup(String s) {
		template.applyMarkup(s);
		return createLinesElement();
	}

	private Element createLinesElement() {
		Element linesElement = template.getLineContainer().getLinesElement();
		return linesElement;
	}

	public Element applyMarkup(List<String> lines) {
		template.applyMarkup(new LineContainer(lines));
		return createLinesElement();
	}

	@Override
	protected void preprocessBlocks(CMLElement rootElement) {
	}

	@Override
	protected void postprocessBlocks() {
	}

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return null;
	}
}
