package org.xmlcml.cml.converters.compchem.qespresso;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class QespressoCommon extends AbstractCommon {

	private static final String QE_PREFIX = "qespresso";
	private static final String QE_URI = "http://wwmm.ch.cam.ac.uk/dict/qespresso";

	public static final String LOG = "qespresso_logfile";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/qespresso/qespressoDict.xml";
    }

	public String getPrefix() {
		return QE_PREFIX;
	}

	public String getNamespace() {
		return QE_URI;
	}

}
