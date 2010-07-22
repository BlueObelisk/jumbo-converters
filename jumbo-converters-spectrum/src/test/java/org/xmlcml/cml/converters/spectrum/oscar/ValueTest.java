package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;

import org.junit.Test;

public class ValueTest {

	@Test (expected = RuntimeException.class)
	public void valueMustHaveMinMaxOrPoint()  {
		Element valueElement = new Element("value");
		new Value(valueElement, null);
	}
	
}
