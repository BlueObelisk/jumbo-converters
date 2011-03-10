package org.xmlcml.cml.converters.text;

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
public class TemplateConverter extends Text2XMLConverter {

	private String codeBase = "foox";
	private String fileType = "barx";

	protected Template template;

	public TemplateConverter(Element templateElement, String cBase, String fType) {
		super();
		codeBase = cBase;
		fileType = fType;
		legacyProcessor = createLegacyProcessor();
		this.template = new Template(templateElement);
	}

	
	public static TemplateConverter createTemplateConverter(InputStream templateStream, String codeBase, String fileType) {
		Element templateElement = null;
		TemplateConverter converter = null;
		try {
			templateElement = new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			converter = new TemplateConverter(templateElement, codeBase, fileType);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template: ", e);
		}
		return converter;
	}


	public static String createBaseURI(String codeBase, String fileType) {
		return "src/main/resources/org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/templates/";
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new TemplateProcessor(template);
	}


	@Override
	public Element convertToXML(List<String> lines) {
		// TODO raise to superclass
		this.lines = lines;
		convertCharactersInLines();
		TemplateProcessor glp = (TemplateProcessor) createLegacyProcessor();
		Element cmlElement = glp.applyMarkup(lines);
		return cmlElement;
	
	}
}
