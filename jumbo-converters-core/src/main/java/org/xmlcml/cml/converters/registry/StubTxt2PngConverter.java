package org.xmlcml.cml.converters.registry;

import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

public class StubTxt2PngConverter extends AbstractConverter {

	public Type getInputType() {
		return Type.TXT;
	}

	public Type getOutputType() {
		return Type.PNG;
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
		return null;
	}
}
