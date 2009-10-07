package org.xmlcml.cml.converters;

import java.util.List;

import nu.xom.Element;

/**
 * Classes that implement this interface aggregate several XML files based on 
 * a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a> query {@link String}s.
 * 
 * @author dfh24
 */
public interface MergeDiff {

	public Element merge(Element element1, Element element2);
	
	public Element diff(Element element1, Element element2);
	
}
