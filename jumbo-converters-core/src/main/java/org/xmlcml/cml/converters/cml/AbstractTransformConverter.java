package org.xmlcml.cml.converters.cml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TemplateProcessor;

/** this is messy and is breaking away from LegacyProcessor machinery
 * However they still co-exist and so some subclassed methods are no-ops or override
 * @author pm286
 *
 */
public abstract class AbstractTransformConverter extends AbstractConverter {

	private static Logger LOG = Logger.getLogger(AbstractTransformConverter.class);

	protected static Template transformTemplate = null;
	
	public Type getInputType() {
		return Type.XML;
	}
	
	public Type getOutputType() {
		return Type.XML;
	}
	

	public AbstractTransformConverter() {
		super();
	}

	public AbstractTransformConverter(Element templateElement) {
		init(templateElement);
	}

	public AbstractTransformConverter(InputStream templateStream) {
		init(makeTemplateElement(templateStream));
	}

	public AbstractTransformConverter(File templateFile) {
		try {
			FileInputStream templateStream = new FileInputStream(templateFile);
			init(makeTemplateElement(templateStream));
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template", e);
		}
	}
	
	private void init(Element templateElement) {
		this.setTemplate(new Template(templateElement));
	}

	protected void setTemplate(Template template) {
		this.transformTemplate = template;
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
}
