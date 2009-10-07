/**
 * 
 */
package org.xmlcml.cml.converters.cif;

import java.util.List;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLScalar;

/**
 * @author pm286
 *
 */
public class CIFEntry extends CMLEntry implements CIFConstants {
	
	private String categoryName;
	
	/** create empty CIFEntry.
	 */
	public CIFEntry() {
		super();
	}
	
	/** wrap CML entry if contains CIF fields.
	 * 
	 * @param entry
	 */
	public CIFEntry(CMLEntry entry) {
		super(entry);
		addDictionaryFields(entry);
	}
	
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	private void addDictionaryFields(CMLEntry entry) {
		List<Node> categoryNodes = CMLUtil.getQueryNodes(
			entry, ".//"+CMLScalar.NS+"[@dictRef='"+IUCR_CATEGORY+"']", CML_XPATH);
		this.categoryName = (categoryNodes.size() == 0) ? 
			null : categoryNodes.get(0).getValue();
	}
}
