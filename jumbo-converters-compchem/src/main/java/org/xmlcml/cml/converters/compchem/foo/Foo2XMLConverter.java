package org.xmlcml.cml.converters.compchem.foo;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class Foo2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(Foo2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String FOO_PREFIX = "foo";
	public static final String FOO_URI = "http://wwmm.ch.cam.ac.uk/dict/foo";
	
	public Type getInputType() {
		return Type.FOO;
	}

	public Type getOutputType() {
		return Type.FOO_XML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		legacyProcessor = new FooProcessor();
		CMLElement cmlElement = readAndProcess(lines);
		return cmlElement;
	}

	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		cml.addNamespaceDeclaration(FOO_PREFIX, FOO_URI);
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}
	
}
