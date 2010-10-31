package org.xmlcml.cml.converters.util;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.interfacex.HasDictRef;

public class ConverterUtils {

	public static void addPrefixedDictRef(HasDictRef element, String prefix, String value) {
		element.setDictRef(DictRefAttribute.createValue(prefix, value));
	}

}
