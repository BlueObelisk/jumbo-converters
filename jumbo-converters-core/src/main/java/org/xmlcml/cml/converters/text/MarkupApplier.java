package org.xmlcml.cml.converters.text;

public interface MarkupApplier {

	final String ID = "id";
	void applyMarkup(LineContainer lineContainer);
	void debug();
	String getId();
}
