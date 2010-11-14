package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.euclid.Util;

/**
 * holds chunks of information (usually text) while processing
 * @author pm286
 *
 */
public abstract class AbstractBlock implements CMLConstants {
	public final static Logger LOG = Logger.getLogger(AbstractBlock.class);
	
	protected static final String A_ = "A.";
	protected static final String D_ = "D.";
	protected static final String E_ = "E.";
	protected static final String F_ = "F.";
	protected static final String I_ = "I.";
	protected static final String DICTREF = DictRefAttribute.NAME;

	public static final String UNKNOWN = "unknown";

	protected static final boolean ADD = true;
	/**
	 * useful lines (we may have skipped whitespace or
	 * consumed keywords)
	 */
	protected List<String> lines;
	protected CMLElement element = null;
	protected String blockName;
	protected AbstractCommon abstractCommon;
	protected boolean validateDictRef;
	protected BlockContainer blockContainer;
	protected JumboReader jumboReader;
	protected CMLMolecule molecule;
	protected Integer natoms;

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
	
	protected CMLDictionary getDictionary() {
		return (abstractCommon == null) ? null : abstractCommon.getDictionary();
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

//	protected void addDictRefTo(HasDictRef element, String value) {
//		Attribute dictRef = ((Element)element).getAttribute("dictRef");
//		if (dictRef != null) {
//			dictRef.detach();
//		}
//		
//		element.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), value));
//	}

	protected void checkIdAndAdd(CMLElement element, String entryId) {
		DictionaryTool dictionaryTool = abstractCommon.getDictionaryTool();
		if (!dictionaryTool.isIdInDictionary(entryId)) {
			LOG.warn("entryId "+entryId+" not found in dictionary: "+dictionaryTool);
		}
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), entryId);
		element.addAttribute(new Attribute(DictRefAttribute.NAME, dictRef));
	}

	protected int getAtomCount() {
		natoms = blockContainer.getMolecule().getAtomCount();
		return natoms;
	}

	protected CMLElement preserveText() {
		String line = Util.concatenate((String[])lines.toArray(new String[0]), CMLConstants.S_NEWLINE);
		CMLScalar scalar = new CMLScalar(line);
		jumboReader.setParentElement(scalar);
		this.setBlockName(UNKNOWN);
		return scalar;
	}

	protected void ensureMolecule() {
		molecule = blockContainer.getMolecule();
		if (molecule == null) {
			throw new RuntimeException("No molecule");
		}
	}

	protected CMLScalar createPackedLines() {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
			sb.append(CMLConstants.S_NEWLINE);
		}
		return new CMLScalar(sb.toString().trim());
	}

	public void debug() {
		System.out.println("blockname "+blockName+" lines "+lines.size());
		if (lines.size() > 0) {
			System.out.println(lines.get(0));
			System.out.println(lines.get(lines.size()-1));
		}
	}
}
