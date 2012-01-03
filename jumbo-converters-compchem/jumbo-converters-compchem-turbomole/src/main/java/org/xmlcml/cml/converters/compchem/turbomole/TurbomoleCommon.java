package org.xmlcml.cml.converters.compchem.turbomole;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Turbomole system
 * @author pm286
 *
 */
public class TurbomoleCommon extends AbstractCommon {

	private static final String TURBOMOLE_PREFIX = "turbomole";
	private static final String TURBOMOLE_URI = "http://wwmm.ch.cam.ac.uk/dict/turbomole";

	public static final String INPUT = "turbomole_input";

	public static final String LOG = "turbomole_log";
	public static final String LOG_XML = "turbomole_log_xml";
	public static final String LOG_CML = "turbomole_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/turbomole/turbomoleDictionary.xml";
    }

	public String getPrefix() {
		return TURBOMOLE_PREFIX;
	}

	public String getNamespace() {
		return TURBOMOLE_URI;
	}

}
