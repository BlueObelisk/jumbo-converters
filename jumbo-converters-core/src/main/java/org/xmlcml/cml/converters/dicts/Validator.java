package org.xmlcml.cml.converters.dicts;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLDictionary;

public class Validator extends AbstractConverter {

	
	private Element dictionaryElement;
	private CMLDictionary dictionary;
	
	public Element getDictionaryElement() {
		return dictionaryElement;
	}

	public void setDictionaryElement(Element dictionaryElement) {
		this.dictionaryElement = dictionaryElement;
		try {
			dictionary = (CMLDictionary) CMLBuilder.ensureCML(dictionaryElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/build dictionary: ", e);
		}
	}

	public Type getOutputType() {
		return Type.XML;
	}

	public Type getInputType() {
		return Type.XML;
	}
	
	@Override
	public Element convertToXML(Element in) {
		ValidatorProcessor processor = new ValidatorProcessor();
		processor.addDictionary(dictionary);
		processor.read(in);
		Element element = processor.getCmlElement();
		return element;
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

}
