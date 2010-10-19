package org.xmlcml.cml.converters;

import java.io.InputStream;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.euclid.Util;

public abstract class AbstractCommon {

	public DictionaryTool dictionaryTool;

	public DictionaryTool getDictionaryTool() {
		if (dictionaryTool == null) {
			
			String dictionaryResource = getDictionaryResource();
			try {
				InputStream inputStream = Util.getResourceUsingContextClassLoader(dictionaryResource, this.getClass());
				CMLElement cmlElement = (CMLElement) CMLUtil.parseQuietlyIntoCML(inputStream);
				Nodes dictionaryNodes = cmlElement.query(".//*[local-name()='dictionary']");
				CMLDictionary dictionary = (dictionaryNodes.size() == 1) ?
						(CMLDictionary) dictionaryNodes.get(0) : null;
				dictionaryTool = DictionaryTool.getOrCreateTool(dictionary);
			} catch (Exception e) {
				throw new RuntimeException("cannot read dictionary: "+dictionaryResource, e);
			}
		}
		return dictionaryTool;
	}

	protected abstract String getDictionaryResource();
	
	public abstract String getPrefix();
	
	public abstract String getNamespace();

	public void addNamespaceDeclaration(CMLElement cml) {
		cml.addNamespaceDeclaration(this.getPrefix(), this.getNamespace());
	}
	
}
