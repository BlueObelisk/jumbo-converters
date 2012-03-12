package org.xmlcml.cml.converters;

import java.io.File;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;

/**
 * a base-level class for converters providing aggregation
 * Aggregator uses XPath to identify documents and parts of documents (i.e. search and index)
 * 
 * 
 * @author dfh24
 *
 */
public abstract class AbstractAggregator extends AbstractConverter implements Aggregator {

	private static final Logger LOG = Logger.getLogger( AbstractAggregator.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	/**
	 * 
	 */
	ConverterList converterList;
	/**
	 * A {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the aggregation.
	 */
	List<String> xPathList;
	
	/**
	 * Default constructor to create a new {@link AbstractAggregator}.
	 */
	public AbstractAggregator() {
		
	}
	
	/**
	 * 
	 */
	public void convert(File infile, File outfile) {
		ensureConverterListHeader();
		LOG.debug("CONVERT ............"+infile);
		Element element = null;
		try {
			element = (Element) new CMLBuilder().build(infile).getRootElement();
		} catch (Exception e) {
			runtimeException("Cannot read infile XML (could be many reasons)", e);
		}
		LOG.debug("ELEMENT "+element);
		Element elem = (Element) getSubElement(element).copy();
		LOG.debug("ELEM "+elem);
		Element ll = converterList.getList();
//		CMLUtil.debug(ll, "XXXX");
		ll.appendChild(elem);
	}
	
	/**
	 * Ensure th
	 */
	protected void ensureConverterListHeader() {
		if (converterList.getList().getChildElements().size() == 0) {
			Element header = getHeader();
			if (header != null) {
				converterList.getList().appendChild(header);
			}
		}
	}
	
	/**
	 * <em>Currently always return <code>null</code></em> 
	 * 
	 * @return <code>null</code>
	 */
	protected Element getHeader() {
		return null;
	}
	
	/**
	 * 
	 * @param element
	 * @return element
	 */
	protected abstract Element getSubElement(Element element);

	/**
	 * 
	 */
	public Type getOutputType() {
		return Type.LIST;
	}
	
	/**
	 * Get the {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the aggregation.
	 * 
	 * @return a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a>
	 *  {@link String}s 
	 */
	public List<String> getXPathList() {
		return xPathList;
	}

	/**
	 * Set the {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the aggregation.
	 * 
	 * @param xPathList a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a>
	 *  {@link String}s 
	 */
	public void setXPathList(List<String> xPathList) {
		this.xPathList = xPathList;
	}

	/**
	 * 
	 * @return converterList
	 */
	public ConverterList getConverterList() {
		return converterList;
	}

	/**
	 * 
	 * @param converterList
	 */
	public void setConverterList(ConverterList converterList) {
		this.converterList = converterList;
	}

}
