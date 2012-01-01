package org.xmlcml.cml.converters;

/**
 * dummy class for testing
 * allows instantiation of a converter
 * @author axiomsofchoice
 *
 */
public class DummyConverter extends AbstractConverter {

	
	public Type getInputType() {
		return null;
	}

	public Type getOutputType() {
		return null;
	}
	
	public DummyConverter() {
		
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
		return "does nothing";
	}
}
