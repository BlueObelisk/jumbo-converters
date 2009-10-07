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
    /** values for control of converter and parser.
     * the enum names can be used as String values,
     * e.g. "NO_GLOBAL"
     * @author pm286
     *
     */
    public enum Control {
        /** do not output global block.*/
        NO_GLOBAL,
        /** merges global block into all blocks; omits global.*/
        MERGE_GLOBAL,
        /** debug output*/
        DEBUG,
        /** echo input lines*/
        ECHO_INPUT,
        /** check for duplicate CIFItem and CIFLoop*/
        CHECK_DUPLICATES,
        /** trim elements (such as T14+) to PT symbols*/
        TRIM_ELEMENTS,
        /** skip header to first data_*/
        SKIP_HEADER,
        /** skip errors and try to recover*/
        SKIP_ERRORS,
        /** check numeric values*/
        CHECK_DOUBLES;
        
        private Control() {
        }
    }
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
	/** terminator for actegory overview */
	String CATEGORY_OVERVIEW_TERMINATOR = S_UNDER+S_LSQUARE+S_RSQUARE;
	/** */
	String CIFX_PREFIX = "cifx";
	/** */
	String CIFX_NS = "http://www.xml-cml.org/schema/cifx";

}
