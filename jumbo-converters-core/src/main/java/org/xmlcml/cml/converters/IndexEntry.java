package org.xmlcml.cml.converters;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** an index for CMLObjects
 * normally uses a key already in a CMLElement
 * @author pm286
 *
 */
public class IndexEntry {

	static Logger LOG = Logger.getLogger(IndexEntry.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	private String key;
	private String filename;
	private String id;
	
	public IndexEntry(String key, Element element) {
		this.key = key;
		id = element.getAttributeValue("id");
		filename = element.getAttributeValue("file");
	}
	
	public IndexEntry(String key, String filename) {
		this.key = key;
		this.setFilename(filename);
	}
	
	public Element getEntryRef() {
		Element entryRef = new Element("entry");
		if (filename != null) {
			entryRef.addAttribute(new Attribute("file", filename));
		}
		if (id != null) {
			entryRef.addAttribute(new Attribute("id", id));
		}
		entryRef.addAttribute(new Attribute("key", key));
		return entryRef;
	}
	
	public String getFilename() {
		return filename;
	}

	public String getKey() {
		return key;
	}

	public String getId() {
		return id;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
