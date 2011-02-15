package org.xmlcml.cml.converters.compchem.foo;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

public class Foo2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(Foo2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public Foo2XMLConverter() {
	}

	public Type getInputType() {
		return Type.FOO;
	}

	public Type getOutputType() {
		return Type.FOO_XML;
	}

	/**
	 * converts an Foo object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		legacyProcessor = new FooProcessor();
		return readAndProcess(lines);
	}

}
