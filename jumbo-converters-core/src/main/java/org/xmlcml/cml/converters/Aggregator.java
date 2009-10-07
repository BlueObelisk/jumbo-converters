package org.xmlcml.cml.converters;

import java.util.List;

/**
 * Classes that implement this interface aggregate several XML files based on 
 * a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a> query {@link String}s.
 * 
 * @author dfh24
 */
public interface Aggregator {

	/**
	 * Get the {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the aggregation.
	 * 
	 * @return a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a>
	 *  {@link String}s 
	 */
	public List<String> getXPathList() ;

	/**
	 * Set the {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the aggregation.
	 * 
	 * @param xPathList a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a>
	 *  {@link String}s 
	 */
	public void setXPathList(List<String> xPathList) ;
	
}
