package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.euclid.Util;

/**
 * holds chunks of information (usually text) while processing
 * @author pm286
 *
 */
public abstract class AbstractBlock implements CMLConstants {
	protected static final String UNKNOWN_BLOCK = "unknownBlock";
	protected static final String UNKNOWN = "unknown";

	public final static Logger LOG = Logger.getLogger(AbstractBlock.class);
	
	protected static final String A_ = "A.";
	protected static final String D_ = "D.";
	protected static final String E_ = "E.";
	protected static final String F_ = "F.";
	protected static final String I_ = "I.";
	protected static final String DICTREF = DictRefAttribute.NAME;

	protected static final boolean ADD = true;
	public static final String DICT_REF = "dictRef";
	public static final String TITLE = "title";

	public static CMLArray createAndAddArray(CMLArrayList arrayList, String type,
		String prefix, String dictRef) {
		CMLArray array = new CMLArray();
		array.setDataType(type);
		array.setDictRef(DictRefAttribute.createValue(prefix, dictRef));
		arrayList.addArray(array);
		return array;
	}

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
	protected LegacyProcessor legacyProcessor;

	protected AbstractBlock(BlockContainer blockContainer) {
		lines = new ArrayList<String>();
		validateDictRef = true;
		this.blockContainer = blockContainer;
		this.legacyProcessor = blockContainer.getLegacyProcessor();
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
		this.createBlockNameFromLine(UNKNOWN_BLOCK);
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

	public void createBlockNameFromLine(String line) {
		if (line == null) {
			throw new RuntimeException("null block name");
		}
		line = line.trim();
		String name = UNKNOWN_BLOCK;
		for (Template template : legacyProcessor.templateList) {
			Pattern templatePattern = template.getPattern();
			LOG.debug(templatePattern + "] ["+ line+"]");
			Matcher matcher = templatePattern.matcher(line);
			if (matcher.matches()) {
				name = line;
				LOG.debug("matches block: "+templatePattern+"|"+line);
				if (matcher.groupCount() >= 1) {
					name = matcher.group(1);
				} else {
					name = template.getName();
				}
				LOG.debug("template ("+template.getId()+") matched name: "+name+" in line: ["+line+"]");
				break;
			}
		}
		if (UNKNOWN_BLOCK.equals(name)) {
			LOG.warn("Cannot match line as block: "+line);
		}
		setBlockName(name);
	}

	public void setBlockName(String name) {
		this.blockName = name;
	}

	protected void skipBlock() {
		CMLModule module = new CMLModule();
		module.setTitle((lines.size() == 0) ? TITLE : lines.get(0));
		element = module;
	}

	protected void summarizeBlock0() {
		CMLModule module = new CMLModule();
		module.setTitle((lines.size() == 0) ? TITLE : lines.get(0));
		element = module;
		CMLScalar scalar = new CMLScalar(preserveText().getValue());
		module.appendChild(scalar);
	}

	protected void summarizeBlock() {
		summarizeBlock0();
	}

	protected CMLModule makeModule() {
		CMLModule module = new CMLModule();
		jumboReader.setParentElement(module);
		module.setRole(abstractCommon.getPrefix());
		element = module;
		return module;
	}

	protected CMLModule makeSubModule(CMLElement parent, String token, String line) {
		CMLModule subModule = new CMLModule();
		parent.appendChild(subModule);
		subModule.setTitle(line);
		subModule.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), token));
		return subModule;
	}

	protected void addScalar(CMLElement parent, String line, String token) {
		CMLScalar scalar = new CMLScalar(line);
		scalar.setDictRef(DictRefAttribute.createValue(abstractCommon.getPrefix(), token));
		parent.appendChild(scalar);
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCMLDefault() {
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
