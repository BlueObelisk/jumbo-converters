package org.xmlcml.cml.converters.dicts;

import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.tools.DictionaryTool;

public class ValidatorProcessor {

	private Element in;
	private CMLElement cmlElement;
	private List<CMLDictionary> dictionaryList;
	private CMLDictionary dictionary;
	private DictionaryTool dictionaryTool;
	public ValidatorProcessor() {
	}
	public Element getCmlElement() {
		return cmlElement;
	}
	public void read(Element in) {
		this.in = in;
		process();
	}
	private void process() {
		cmlElement = CMLBuilder.ensureCML(in);
		Nodes nodes = cmlElement.query("//*[@dictRef]");
		for (int i = 0; i < nodes.size(); i++) {
			validate((HasDictRef)nodes.get(i));
		}
	}
	private void validate(HasDictRef element) {
//		DictRefAttribute dictRef = (DictRefAttribute) element.getDictRefAttribute();
		dictionaryTool.validateDictRefsInCML((CMLElement)element);
	}
//	public void addDictionary(CMLDictionary dictionary) {
//		this.dictionaryList.add(dictionary);
//	}

	public void addDictionary(CMLDictionary dictionary) {
		this.dictionary = dictionary;
		dictionaryTool = DictionaryTool.getOrCreateTool(dictionary);
	}

}
