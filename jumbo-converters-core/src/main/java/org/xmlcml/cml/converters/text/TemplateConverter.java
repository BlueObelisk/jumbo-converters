package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.euclid.Util;

/** this is messay and is breaking away from LegacyProcessor machinery
 * However they still co-exist and so some subclassed methods are no-ops or override
 * @author pm286
 *
 */
public class TemplateConverter extends Text2XMLConverter {

	protected Template template;

	public TemplateConverter(Element templateElement) {
		super();
		legacyProcessor = createLegacyProcessor();
		this.template = new Template(templateElement);
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new TemplateProcessor(template);
	}


	@Override
	public Element convertToXML(List<String> lines) {
		this.lines = lines;
		convertCharactersInLines();
		TemplateProcessor glp = (TemplateProcessor) createLegacyProcessor();
		Element cmlElement = glp.applyMarkup(lines);
		return cmlElement;
	
	}
}
