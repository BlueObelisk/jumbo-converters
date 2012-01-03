package org.xmlcml.cml.converters.compchem.molcas;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Molcas system
 * @author pm286
 *
 */
public class MolcasCommon extends AbstractCommon {

	private static final String MOLCAS_PREFIX = "molcas";
	private static final String MOLCAS_URI = "http://wwmm.ch.cam.ac.uk/dict/molcas";

	public static final String INPUT = "molcas_input";

	public static final String LOG = "molcas_log";
	public static final String LOG_XML = "molcas_log_xml";
	public static final String LOG_CML = "molcas_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/molcas/molcasDictionary.xml";
    }

	public String getPrefix() {
		return MOLCAS_PREFIX;
	}

	public String getNamespace() {
		return MOLCAS_URI;
	}

}
