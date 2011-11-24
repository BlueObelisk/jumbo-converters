package org.xmlcml.cml.converters;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An {@link AbstractConverter} that aggragates several files to create indexes.
 * 
 * @author dfh24
 *
 */
public abstract class AbstractIndexer extends AbstractConverter implements Aggregator {

	private static final Logger LOG = Logger.getLogger(AbstractIndexer.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	/**
	 * 
	 */
	protected Index index;
	/**
	 * A {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the index.
	 */
	protected List<String> xPathList;
	
	/**
	 * 
	 * @return
	 */
	protected abstract String getIndexKey();
	
	/**
	 * 
	 */
	public Type getOutputType() {
		return Type.INDEX;
	}
	
	/**
	 * 
	 * @param cml
	 * @param filename
	 */
	public void addToIndex(Element cml, String filename) {
		
		checkIndex(index);
		IndexEntry entry = this.createIndexEntry(cml);
		entry.setFilename(filename);
		String key = entry.getKey();
		LOG.debug("key...... "+key);
		index.put(key, entry);
	}

	/**
	 * 
	 * @param index
	 */
	protected void checkIndex(Index index) {
		if (index == null) {
			throw new IllegalArgumentException("null index; must create before indexing");
		}
		if (index.getIndexMap() == null) {
			throw new IllegalArgumentException("null indexMap; should be created as part of index");
		}
	}

	/**
	 * 
	 * @return index
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * 
	 * @param index
	 */
	public void setIndex(Index index) {
		this.index = index;
	}

	/**
	 * 
	 * @param cml
	 * @return indexEntry
	 */
	public IndexEntry createIndexEntry(Element cml) {
		IndexEntry entry = new IndexEntry(this.getIndexKey(), cml);
		return entry;
	}

	/**
	 * 
	 * @param file
	 * @return indexEntry
	 */
	public IndexEntry createIndexEntry(File file) {
		IndexEntry entry = new IndexEntry(this.getIndexKey(), file.getAbsolutePath());
		return entry;
	}

	/**
	 * Get the {@link List} of {@link String}s that are the
	 * <a href="http://www.w3.org/TR/xpath">XPath</a> queries used
	 * to create the index.
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
	 * to create the index.
	 * 
	 * @param xPathList a {@link List} of <a href="http://www.w3.org/TR/xpath">XPath</a>
	 *  {@link String}s 
	 */
	public void setXPathList(List<String> xPathList) {
		this.xPathList = xPathList;
	}

}
