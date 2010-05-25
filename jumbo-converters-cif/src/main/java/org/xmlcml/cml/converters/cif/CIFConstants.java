/**
 * 
 */
package org.xmlcml.cml.converters.cif;

import org.xmlcml.cml.base.CMLConstants;

/**
 * @author pm286
 *
 */
public interface CIFConstants extends CMLConstants {

	/** delimiter for CIF arrays/loops.
	 * best guess for a character that doesn't clash with CIF usage.
	 */
    /** output delimiter*/
	String DELIM = "|";
    /** */
	boolean NUMERIC = true;
    /** */
	boolean NON_NUMERIC = false;
    /** IUCR prefix */
	String IUCR_PREFIX = "iucr";
    /** category */
	String IUCR_CATEGORY = IUCR_PREFIX+S_COLON+"category";
	/** dictionary namespace */
	String IUCR_CIF_NAMESPACE = "http://www.iucr.org/cif";
	/** category overview */
	String CATEGORY_OVERVIEW = "category_overview";
	/** terminator for category overview */
	String CATEGORY_OVERVIEW_TERMINATOR = S_UNDER+S_LSQUARE+S_RSQUARE;
	/** */
	String CIFX_PREFIX = "cifx";
	/** */
	String CIFX_NS = "http://www.xml-cml.org/schema/cifx";

}
