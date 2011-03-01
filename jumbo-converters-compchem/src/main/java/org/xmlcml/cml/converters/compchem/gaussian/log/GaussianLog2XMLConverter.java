package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.TemplateConverter;

public class GaussianLog2XMLConverter extends TemplateConverter {
	
	private GaussianLog2XMLConverter() {
		super(null);
		throw new RuntimeException("Cannot use zero-arg constructor");
	}
	
	public GaussianLog2XMLConverter(Element templateElement) {
		super(templateElement);
	}
	
	public static GaussianLog2XMLConverter createGaussianLog2XMLConverter(InputStream templateStream) {
		Element templateElement = null;
		GaussianLog2XMLConverter converter = null;
		try {
			templateElement = new Builder().build(templateStream).getRootElement();
			converter = new GaussianLog2XMLConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create template: ", e);
		}
		return converter;
	}
	
	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new GaussianLogProcessor(template);
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		this.lines = lines;
		convertCharactersInLines();
		GaussianLogProcessor glp = (GaussianLogProcessor) createLegacyProcessor();
		Element cmlElement = glp.applyMarkup(lines);
//		legacyProcessor.read((CMLElement)element);
//		Element cmlElement = glp.getCMLElement();
		return cmlElement;

	}
}
