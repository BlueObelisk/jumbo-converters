package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;

public abstract class LegacyProcessor {
	private static final Logger LOG = Logger.getLogger(LegacyProcessor.class);

	public static final String _ANONYMOUS = "_anonymous_";

	protected BlockContainer blockContainer;
	protected List<String> lines;
	protected int lineCount = 0;
	protected CMLElement cmlElement;
	protected AbstractCommon abstractCommon;
	protected List<Template> templateList;
//	protected List<Pattern> blockNamePatternList;
	protected boolean dtdValidate = true;
//	private Map<String, Template> templateByNameMap;
	
//	public Map<String, Template> getTemplateByNameMap() {
//		return templateByNameMap;
//	}
	
	public Template getTemplateByPattern(String blockName) {
		Template matchedTemplate = null;
		for (Template template : templateList) {
			Pattern templatePattern = template.getPattern();
			if (templatePattern.matcher(blockName).matches()) {
				matchedTemplate = template;
				break;
			}
		}
		return matchedTemplate;
	}

	
	protected LegacyProcessor() {
		this.blockContainer = new BlockContainer(this);
		abstractCommon = getCommon();
//		ensureTemplateByNameMap();
		readTemplates();
//		makePatternList();
//		debugTemplates();
	}

	private void debugTemplates() {
		LOG.debug("TEMPLATELIST "+templateList.size());
		for (Template template : templateList) {
			template.debug();
		}
	}

	private void readTemplates() {
		LOG.debug("readTemplates");
		String templateResource = this.getTemplateResourceName();
		try {
			Element root = new Builder(dtdValidate).build(org.xmlcml.euclid.Util.getInputStreamFromResource(templateResource)).getRootElement();
			Elements childElements = root.getChildElements();
			templateList = new ArrayList<Template>();
			for (int i = 0; i < childElements.size(); i++) {
				Element childElement = childElements.get(i);
				processAndAddTemplate(childElement);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse/read/find templateList "+templateResource, e);
		}
	}

//	private void makePatternList() {
//		blockNamePatternList = new ArrayList<Pattern>();
//		for (Template template : templateList) {
//			Pattern pattern = template.getPattern();
//			blockNamePatternList.add(pattern);
//		}
//		System.out.println("made template patterns: "+templateList.size());
//	}

	private void processAndAddTemplate(Element element) {
		Template newTemplate = new Template(this, element);
		String newId = newTemplate.getId();
		System.out.println("ID "+newId+" tlist "+templateList.size());
		for (Template template : templateList) {
			if (template.getId().equals(newId)) {
				throw new RuntimeException("Duplicate id: "+newId);
			}
		}
//		ensureTemplateByNameMap();
//		templateByNameMap.put(newTemplate.getName(), newTemplate);
		templateList.add(newTemplate);
	}
	
//	private void ensureTemplateByNameMap() {
//		if (templateByNameMap == null) {
//			templateByNameMap = new HashMap<String, Template>();
//		}
//	}

	protected abstract String getTemplateResourceName();

	public void read(List<String> lines) {
		this.lines = lines;
		preprocessBlocks(null);
		lineCount = 0;
		while (lineCount < this.lines.size()) {
			AbstractBlock block = readBlock(this.lines);
			if (block != null) {
				blockContainer.add(block);
			}
		}
		postprocessBlocks();
		LOG.debug("Finished reading blocks: "+blockContainer.size());
	}

	public void read(CMLElement element) {
		preprocessBlocks(element);
		List<Node> childNodes = CMLUtil.getQueryNodes(element, "*");
		for (Node childNode : childNodes) {
			AbstractBlock block = null;
			block = readBlock((CMLElement) childNode);
			if (block != null) {
				blockContainer.add(block);
			}
		}
//		postprocessBlocks();
		LOG.debug("Finished reading blocks: "+blockContainer.size());
	}

	/** processing before blocks are read
	 * often null
	 */
	protected abstract void preprocessBlocks(CMLElement rootElement);

	/** processing after blocks are read
	 * often null
	 */
	protected abstract void postprocessBlocks();

	protected abstract AbstractCommon getCommon();
	
	public List<CMLElement> getBlockList() {
		List<CMLElement> cmlList = new ArrayList<CMLElement>();
		if (blockContainer != null) {
			for (AbstractBlock block : blockContainer.getBlockList()) {
				cmlList.add(block.getElement());
			}
		}
		return cmlList;
	}

	public CMLElement getCMLElement() {
		cmlElement = new CMLCml();
		for (AbstractBlock block : blockContainer.getBlockList()) {
			CMLElement element = block.getElement();
			if (element != null) {
				cmlElement.appendChild(element);
			}
		}
		return cmlElement;
	}

	protected void processAnonymousBlocks() {
		for (AbstractBlock block : blockContainer.getBlockList()) {
			if (_ANONYMOUS.equals(block.getBlockName())) {
				block.convertToRawCML();
			}
		}
	}

	protected AbstractBlock getPreviousBlock() {
		AbstractBlock previousBlock = null;
		if (blockContainer != null) {
			int currentBlockNumber = blockContainer.getCurrentBlockNumber();
			if (currentBlockNumber >= 0) {
				previousBlock = blockContainer.getBlockList().get(currentBlockNumber-1);
			}
		}
		return previousBlock;
	}

	protected AbstractBlock readBlock(List<String> lines) {
		String line = lines.get(lineCount).trim();
		AbstractBlock block = createBlock(line);
		block.convertToRawCML();
		return block;
	}
	

	protected AbstractBlock readBlock(CMLElement element) {
		AbstractBlock block = null;
		CMLScalar scalar = null;
		if (element instanceof CMLScalar) {
			scalar = (CMLScalar) element;
		} else {
			scalar = (CMLScalar) element.getFirstChildElement(CMLScalar.TAG);
		}
//		scalar.debug("BLOCK");
		if (scalar != null) {
			String[] lineArray = scalar.getXMLContent().split(CMLConstants.S_NEWLINE);		
			lines = new ArrayList<String>();
			for (int i = 0; i < lineArray.length; i++) {
				lines.add(lineArray[i]);
			}
			block = createBlock(lines.get(0));
			block.convertToRawCML();
		}
		return block;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	public AbstractBlock createBlock(String line0) {
		AbstractBlock block = createAbstractBlock(blockContainer);
		
		block.createBlockNameFromLine(line0);
		for (String line : lines) {
			block.add(line);
		}
		return block;
	}

	protected abstract AbstractBlock createAbstractBlock(
			BlockContainer blockContainer);
}
