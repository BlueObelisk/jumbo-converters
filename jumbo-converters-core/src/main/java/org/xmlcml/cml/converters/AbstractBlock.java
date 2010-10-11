package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.base.CMLElement;

/**
 * holds chunks of information (usually text) while processing
 * @author pm286
 *
 */
public abstract class AbstractBlock {

	/**
	 * useful lines (we may have skipped whitespace or
	 * consumed keywords)
	 */
	protected List<String> lines;
	protected CMLElement element = null;
	protected String blockName;

	protected AbstractBlock() {
		lines = new ArrayList<String>();
	}
	// ???
	public void setElement(CMLElement element) {
		this.element = element;
	}

	public String getBlockName() {
		return blockName;
	}

	public List<String> getLines() {
		return lines;
	}

	public CMLElement getElement() {
		return element;
	}

	protected Double createDouble(String field) {
		Double value = null;
		try {
			value = new Double(field);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Cannot read "+getBlockName()+": "+field);
		}
		return value;
	}

	public void add(String s) {
		lines.add(s);
	}

	public abstract void convertToRawCML();

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

}
