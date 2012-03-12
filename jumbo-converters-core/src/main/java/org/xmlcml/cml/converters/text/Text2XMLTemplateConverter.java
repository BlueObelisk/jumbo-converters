package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.xmlcml.cml.converters.LegacyProcessor;

import nu.xom.Builder;
import nu.xom.Element;

/** this is messy and is breaking away from LegacyProcessor machinery
 * However they still co-exist and so some subclassed methods are no-ops or override
 * @author pm286
 *
 */
public class Text2XMLTemplateConverter extends Text2XMLConverter {

	protected Template template;

	public Text2XMLTemplateConverter() {
		super();
	}

	public Text2XMLTemplateConverter(Element templateElement) {
		init(templateElement);
	}

	public Text2XMLTemplateConverter(InputStream templateStream) {
		init(makeTemplateElement(templateStream));
	}

	public Text2XMLTemplateConverter(File templateFile) {
		try {
			FileInputStream templateStream = new FileInputStream(templateFile);
			init(makeTemplateElement(templateStream));
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template", e);
		}
	}
	
	private void init(Element templateElement) {
		legacyProcessor = createLegacyProcessor();
		this.setTemplate(new Template(templateElement));
	}

	protected void setTemplate(Template template) {
		this.template = template;
	}


	private Element makeTemplateElement(InputStream templateStream) {
		try {
			Element templateElement = new Builder().build(templateStream).getRootElement();
			init(templateElement);
			return templateElement;
		} catch (Exception e) {
			throw new RuntimeException("Cannot build template: ", e);
		}
	}


	@Override
	public Element convertToXML(List<String> lines) {
		lines = convertCharactersInLines(lines);
		TemplateProcessor glp = new TemplateProcessor(template);
		Element cmlElement = glp.applyMarkup(lines);
		// because we may have added parents
		Element cmlTop = (Element) cmlElement.query("ancestor-or-self::*").get(0);
		return cmlTop;
	
	}
	
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

}
