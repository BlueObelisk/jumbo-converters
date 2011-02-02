package org.xmlcml.cml.converters.util;

import org.xmlcml.cml.converters.format.Field;
import org.xmlcml.cml.converters.format.LineReader;
import org.xmlcml.cml.element.CMLScalar;

public class PatternField extends Field {

	public PatternField(LineReader lineReader) {
		super("pattern", lineReader);
	}
	
	public CMLScalar createScalar(String value) {
		CMLScalar scalar = null;
		return scalar;
	}

}
