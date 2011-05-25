package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;

/** this is messay and is breaking away from LegacyProcessor machinery
 * However they still co-exist and so some subclassed methods are no-ops or override
 * @author pm286
 *
 */
public class Text2XMLTemplateConverter extends Text2XMLConverter {

	protected Template template;

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
		this.template = new Template(templateElement);
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
	protected LegacyProcessor createLegacyProcessor() {
		return new TemplateProcessor(template);
	}


	@Override
	public Element convertToXML(List<String> lines) {
		lines = convertCharactersInLines(lines);
		TemplateProcessor glp = (TemplateProcessor) createLegacyProcessor();
		Element cmlElement = glp.applyMarkup(lines);
		// because we may have added parents
		Element cmlTop = (Element) cmlElement.query("ancestor-or-self::*").get(0);
		return cmlTop;
	
	}
}
