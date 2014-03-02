package org.xmlcml.cml.converters.compchem.jaguar;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Jaguar system
 * @author pm286
 *
 */
public class JaguarCommon extends AbstractCommon {

	private static final String JAGUAR_PREFIX = "jaguar";
	private static final String JAGUAR_URI = "http://wwmm.ch.cam.ac.uk/dict/jaguar";

	public static final String INPUT = "jaguar_input";

	public static final String LOG = "jaguar_log";
	public static final String LOG_XML = "jaguar_log_xml";
	public static final String LOG_CML = "jaguar_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/jaguar/jaguarDictionary.xml";
    }

	public String getPrefix() {
		return JAGUAR_PREFIX;
	}

	public String getNamespace() {
		return JAGUAR_URI;
	}

}
