package org.xmlcml.cml.converters.compchem.gaussian;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Gaussian system
 * @author pm286
 *
 */
public class GaussianCommon extends AbstractCommon {
	
	private static final String GAUSSIAN_PREFIX = "gaussian";
	private static final String GAUSSIAN_URI = "http://wwmm.ch.cam.ac.uk/dict/gaussian";

	public static final String INPUT = "gaussian_input";

	public static final String ARCHIVE_XML = "gaussian_archive_xml";
	public static final String ARCHIVE_CML = "gaussian_archive_cml";

	public static final String LOG = "gaussian_log";
	public static final String LOG_XML = "gaussian_log_xml";
	public static final String LOG_CML = "gaussian_log_compchem";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/gaussian/gaussianDictionary.xml";
    }
    
	public String getPrefix() {
		return GAUSSIAN_PREFIX;
	}
	
	public String getNamespace() {
		return GAUSSIAN_URI;
	}
	
}
