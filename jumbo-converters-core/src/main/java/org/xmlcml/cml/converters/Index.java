package org.xmlcml.cml.converters;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Util;

/** an index for CMLObjects
 * normally uses a key already in a CMLElement
 * 
<?xml version="1.0" encoding="UTF-8"?>
<index>
  <entry key="28245328Cl6/82517868Cl2/83626838Se6/185844947Re6/">
    <ref file="D:\projects\projects\nj\test\b005231ksup1_tbarese_moiety_2.complete.cml.xml"/>
  </entry>
  <entry key="1238051050H2/1238964284H2/1240921214H2/1335099236H2/1336795242H2/1363344607H2/1381075098O2/1402414798H2/3076533461C2/3077903312C2/3106409607C2/3362175325C2/3364393179N2/3494533223C2/3539474204C2/3642287400N2/4052708304C2/4486712928C2/">
    <ref file="D:\projects\projects\nj\test\b511963dsup1_2005sot0886_moiety_5.complete.cml.xml"/>
    <ref file="D:\projects\projects\nj\test\b511963dsup1_2005sot0886_moiety_3.complete.cml.xml"/>
    <ref file="D:\projects\projects\nj\test\b511963dsup1_2005sot0607_moiety_1.complete.cml.xml"/>
  </entry>
 * 
 * @author pm286
 *
 */
public class Index {
	
	private static final Logger LOG = Logger.getLogger(Index.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public enum Type {
		FILE,
		ID
		;
	}
	private Map<String, Set<IndexEntry>> indexMap; 
	private String xPath;
	private boolean allowDuplicateKeys;
	private boolean copyElement = false;
	
	public Index(boolean allowDuplicateKeys) {
		indexMap = new HashMap<String, Set<IndexEntry>>();
		this.setIndexMap(indexMap);
		this.setAllowDuplicateKeys(allowDuplicateKeys);
	}
	
	public boolean isAllowDuplicateKeys() {
		return allowDuplicateKeys;
	}

	public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
		this.allowDuplicateKeys = allowDuplicateKeys;
	}

	public boolean containsKey(String key) {
		return indexMap.containsKey(key);
	}

	public Set<String> keySet() {
		return indexMap.keySet();
	}
	
	public void putUsingXPathKey(CMLElement element) {
		if (xPath != null) {
			Nodes nodes = element.query(xPath, CML_XPATH);
			if (nodes.size() == 1) {
				String key = nodes.get(0).getValue();
				IndexEntry entry = new IndexEntry(key, element);
				this.put(key, entry);
			}
		}
	}

	public void put(String key, String filename) {
		IndexEntry entry = new IndexEntry(key, filename);
		this.put(key, entry);
	}

	@SuppressWarnings("unchecked")
	public void put(String key, IndexEntry entry) {
		Set entrySet = (Set) indexMap.get(key);
		if (!allowDuplicateKeys) {
			if (entrySet != null) {
				throw new RuntimeException("duplicate entry for: "+key);
			}
		}
		if (entrySet == null) {
			entrySet = new HashSet();
			indexMap.put(key, entrySet);
		}
		entrySet.add(entry);
	}

	public int size() {
		return indexMap.size();
	}

	public Map<String, Set<IndexEntry>> getIndex() {
		return indexMap;
	}

	public String getXPath() {
		return xPath;
	}

	public void setXPath(String path) {
		xPath = path;
	}

	public Map<String, Set<IndexEntry>> getIndexMap() {
		return indexMap;
	}

	public void setIndexMap(Map<String, Set<IndexEntry>> indexMap) {
		this.indexMap = indexMap;
	}
	
	@SuppressWarnings("unchecked")
	public void debug() {
		Map indexMap = this.getIndex();
		for (Object key : indexMap.keySet()) {
			Set indexSet = (Set) indexMap.get(key);
			Util.println(key+"...");
			for (Object ie : indexSet) {
				Element element = ((IndexEntry)ie).getEntryRef();
				if (element instanceof CMLMolecule) {
					CMLMolecule molecule = (CMLMolecule)element;
					Util.println("..."+molecule.getId()+" "+new CMLFormula(molecule).getConcise());
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
	public Element createIndexElement() {
		Element indexElement = new Element("index");
		Map<String, Set<IndexEntry>> indexMap = this.getIndex();
		for (String key : indexMap.keySet()) {
			if (key == null) {
				continue;
			}
			Set<IndexEntry> indexSet = indexMap.get(key);
			Element entry = new Element("entry");
			indexElement.appendChild(entry);
			entry.addAttribute(new Attribute("key", key.toString()));
			for (Object ie : indexSet) {
				Element indexEntry = ((IndexEntry)ie).getEntryRef();
//				LOG.debug("indexEntry "+indexEntry);
				if (copyElement) {
					entry.appendChild(indexEntry.copy());
				} else {
					Element ref = new Element("ref");
					addAttribute(indexEntry, ref, "id");
					addAttribute(indexEntry, ref, "file");
					entry.appendChild(ref);
				}
			}
		}
		return indexElement;
	}

	private void addAttribute(Element element, Element ref, String name) {
		String val = element.getAttributeValue(name);
		if (val != null && val.length() != 0) {
			ref.addAttribute(new Attribute(name, val));
		}
	}
	
	public void writeFile(String indexfileName) throws IOException {
		if (indexfileName != null) {
			File outfile = new File(indexfileName);
//			FileUtils.touch(outfile);
			FileOutputStream fos = FileUtils.openOutputStream(outfile);
			Element indexElement = createIndexElement();
			CMLUtil.debug(indexElement, fos, 0);
			fos.close();
			LOG.debug("wrote index: "+indexfileName);
		}
    }
	
	public void debugXML() {
		Element indexElement = createIndexElement();
		CMLUtil.debug(indexElement, "INDEX ");
    }
	
	public static Index createIndex(Element element) {
		boolean allowDuplicates = true;
		Index index = new Index(allowDuplicates);
		LOG.debug("index "+element.getChildElements().size());
		for (int i = 0; i < element.getChildElements().size(); i++) {
			Element entry = element.getChildElements().get(i);
			String key = entry.getAttributeValue("key");
			if (key == null) {
				throw new RuntimeException("NULL key in index");
			}
			for (int j= 0; j < entry.getChildElements().size(); j++) {
				Element ref = entry.getChildElements().get(j);
				IndexEntry indexEntry = new IndexEntry(key, ref);
				index.put(key, indexEntry);
			}
		}
//		Element indexElement = index.createIndexElement();
		return index;
	}
	
	public Set<IndexEntry> getIndexEntrySet(String key) {
		return indexMap.get(key);
	}
	
	public Set<String> getSet(String key, Type type) {
		Set<IndexEntry> indexEntrySet = indexMap.get(key);
		Set<String> set = new HashSet<String>();
		if (indexEntrySet != null) {
			for (IndexEntry indexEntry : indexEntrySet) {
				String ss = null;
				if (Type.ID == type) {
					ss = indexEntry.getId();
				} else if (Type.FILE == type) {
					ss = indexEntry.getFilename();
				}
				if (ss != null) {
					set.add(ss);
				}
			}
		}
		return set;
	}
	
	public Set<Element> getSetFromFiles(String key) {
		Set<Element> set = null;
		Set<String> filenameSet = getSet(key, Type.FILE);
		if (filenameSet != null) {
			set = new HashSet<Element>();
			for (String filename : filenameSet) {
				Element elem = null;
				try {
					elem = new CMLBuilder().build(new File(filename)).getRootElement();
				} catch (Exception e) {
					// FIXME
//					throw new RuntimeException("could not read XML file: "+filename, e);
					LOG.warn("could not read XML file: "+filename+" "+e.getMessage() );
					continue ;
				}
				if (elem != null) {
					set.add(elem);
				}
			}
		}
		return set;
	}
}
