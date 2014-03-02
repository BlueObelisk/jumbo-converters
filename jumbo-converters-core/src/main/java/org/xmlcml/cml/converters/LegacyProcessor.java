package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.element.CMLCml;

public abstract class LegacyProcessor {
	private static final Logger LOG = Logger.getLogger(LegacyProcessor.class);

	public static final String _ANONYMOUS = "_anonymous_";

	protected List<String> lines;
	protected int lineCount = 0;
	protected CMLElement cmlElement;
	protected AbstractCommon abstractCommon;
	protected List<Template> templateList;
	protected boolean dtdValidate = true;
	
	public Template getTemplateByBlockName(String blockName) {
		Template matchedTemplate = null;
		for (Template template : templateList) {
			String templateName = template.getName();
			LOG.trace(templateName + " / " + blockName);
			if (templateName.equals(blockName)) {
				matchedTemplate = template;
				break;
			}
		}
		return matchedTemplate;
	}

	
	protected LegacyProcessor() {
		abstractCommon = getCommon();
		readTemplates();
//		debugTemplates();
	}

	@SuppressWarnings("unused")
	private void debugTemplates() {
		LOG.debug("TEMPLATELIST "+templateList.size());
		for (Template template : templateList) {
			template.debug();
		}
	}

	protected void readTemplates() {
		LOG.trace("readTemplates");
		String templateResource = this.getTemplateResourceName();
		if (templateResource != null) {
			LOG.debug("readTemplates from: "+templateResource);
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
		} else {
			LOG.info("skipped reading teplates");
		}
	}

	protected void processAndAddTemplate(Element element) {
		Template newTemplate = new Template(element);
		String newId = newTemplate.getId();
		for (Template template : templateList) {
			if (template.getId().equals(newId)) {
				throw new RuntimeException("Duplicate id: "+newId);
			}
		}
		templateList.add(newTemplate);
	}
	
	public void read(List<String> lines) {
		this.lines = lines;
		preprocessBlocks(null);
		lineCount = 0;
		while (lineCount < this.lines.size()) {
		}
		postprocessBlocks();
	}

	public void read(CMLElement element) {
		preprocessBlocks(element);
		List<Node> childNodes = CMLUtil.getQueryNodes(element, "*");
		for (Node childNode : childNodes) {
//			AbstractBlock block = null;
//			block = readBlock((CMLElement) childNode);
//			if (block != null) {
//				blockContainer.add(block);
//			}
		}
//		postprocessBlocks();
	}

	/** processing before blocks are read
	 * often null
	 */
	protected abstract void preprocessBlocks(CMLElement rootElement);

	/** processing after blocks are read
	 * often null
	 */
	protected abstract void postprocessBlocks();

	/** form class name for Common and instantiate
	 * assumes package of form: package org.xmlcml.cml.converters.gaussian.log
	 * can always be overridden if names are more tricky
	 * @return
	 */
	protected AbstractCommon getCommon() {
		return null;
	}
	
	public CMLElement getCMLElement() {
		cmlElement = new CMLCml();
//		for (AbstractBlock block : blockContainer.getBlockList()) {
//			CMLElement element = block.getElement();
//			if (element != null) {
//				cmlElement.appendChild(element);
//			}
//		}
		return cmlElement;
	}

	protected String getTemplateResourceName() {
		String templateResourceName = getResourceRootFromPackage(this.getClass())+"/templateList.xml";
		LOG.info("template resource: "+templateResourceName);
		return templateResourceName;
	}

	public static String getResourceRootFromPackage(Class<?> clazz) {
		return clazz.getPackage().getName().replaceAll("\\.", CMLConstants.S_SLASH);
	}
}
