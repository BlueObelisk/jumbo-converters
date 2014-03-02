package org.xmlcml.cml.converters.compchem.nwchem;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the NWChem system
 * @author pm286
 *
 */
public class NWChemCommon extends AbstractCommon {

	private static final String NWCHEM_PREFIX = "nwchem";
	private static final String NWCHEM_URI = "http://wwmm.ch.cam.ac.uk/dict/nwchem";

	public static final String INPUT = "nwchem_input";

	public static final String LOG = "nwchem_log";
	public static final String LOG_XML = "nwchem_log_xml";
	public static final String LOG_CML = "nwchem_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/nwchem/nwchemDictionary.xml";
    }

	public String getPrefix() {
		return NWCHEM_PREFIX;
	}

	public String getNamespace() {
		return NWCHEM_URI;
	}

}
