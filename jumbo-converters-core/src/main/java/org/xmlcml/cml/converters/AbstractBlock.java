package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.tools.DictionaryTool;

/**
 * holds chunks of information (usually text) while processing
 * @author pm286
 *
 */
public abstract class AbstractBlock implements CMLConstants {

	/**
	 * useful lines (we may have skipped whitespace or
	 * consumed keywords)
	 */
	protected List<String> lines;
	protected CMLElement element = null;
	protected String blockName;
	protected AbstractCommon abstractCommon;
	protected boolean validateDictRef;
	protected CMLMolecule molecule;
	protected BlockContainer blockContainer;

	protected AbstractBlock(BlockContainer blockContainer) {
		lines = new ArrayList<String>();
		validateDictRef = true;
		this.blockContainer = blockContainer;
		abstractCommon = getCommon();
	}
	
	public boolean isValidateDictRef() {
		return validateDictRef;
	}
	public void setValidateDictRef(boolean validateDictRef) {
		this.validateDictRef = validateDictRef;
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
	
	protected abstract AbstractCommon getCommon();

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	
	protected DictionaryTool getDictionaryTool() {
		return (abstractCommon == null) ? null : abstractCommon.getDictionaryTool();
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}

	public void setBlockContainer(BlockContainer blockContainer) {
		this.blockContainer = blockContainer;
	}

	protected void addNamespacedAttribute(CMLElement el, String localName,
			String value) {
				el.addAttribute(new Attribute(
						abstractCommon.getPrefix()+":"+localName, 
						abstractCommon.getNamespace(), value));
			}

	protected void addDictRefTo(HasDictRef element, String value) {
		element.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), value));
	}

}
