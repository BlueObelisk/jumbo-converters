package org.xmlcml.cml.converters.filter;

import org.xmlcml.cml.base.CMLElement;

public interface AbstractCMLFilter {
	
	boolean accept(CMLElement element);
}
