package org.xmlcml.cml.converters.compchem.foo;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSPunchXMLProcessor;
import org.xmlcml.cml.element.CMLCml;

public class FooXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(FooXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new FooXMLProcessor();
		return convert(xml);
	}

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(Foo2XMLConverter.FOO_PREFIX, Foo2XMLConverter.FOO_URI);
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
