package org.xmlcml.cml.converters.registry;

import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

public class StubHtml2TxtConverter extends AbstractConverter {

	public Type getInputType() {
		return Type.HTML;
	}

	public Type getOutputType() {
		return Type.TXT;
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
