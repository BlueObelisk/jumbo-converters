package org.xmlcml.cml.converters.compchem.mopac;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Mopac system
 * @author pm286
 *
 */
public class MopacCommon extends AbstractCommon {

	private static final String MOPAC_PREFIX = "mopac";
	private static final String MOPAC_URI = "http://wwmm.ch.cam.ac.uk/dict/mopac";

	public static final String INPUT = "mopac_input";

	public static final String LOG = "mopac_log";
	public static final String LOG_XML = "mopac_log_xml";
	public static final String LOG_CML = "mopac_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/mopac/mopacDictionary.xml";
    }

	public String getPrefix() {
		return MOPAC_PREFIX;
	}

	public String getNamespace() {
		return MOPAC_URI;
	}

}
