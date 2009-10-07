/**
 * 
 */
package org.xmlcml.cml.converters.cif.dict;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.converters.cif.CIFConstants;
import org.xmlcml.cml.converters.cif.CIFEntry;

/**
 * @author pm286
 *
 */
public class CIFDictionary implements CIFConstants {

	/** create CIFDictionary with CIF namespace.
	 */
	public CIFDictionary() {
		super();
//		this.setNamespace(CIF_NAMESPACE);
	}

	/** get list of entries as CIFEntry.
	 * 
	 * @return list
	 */
	public List<CIFEntry> getEntryList() {
		List<CIFEntry> cifEntryList = new ArrayList<CIFEntry>();
		if (true) throw new RuntimeException("NYI");
//		CMLElements<CMLEntry> cmlEntryList = this.getEntryElements();
//		for (CMLEntry cmlEntry : cmlEntryList) {
//			CIFEntry cifEntry = new CIFEntry(cmlEntry);
//			cifEntryList.add(cifEntry);
//		}
		return cifEntryList;
	}
	
	public CIFEntry getCMLEntry(String foo) {
		return null;
	}
	
	public CIFEntry getEntry(String foo) {
		return null;
	}
}
