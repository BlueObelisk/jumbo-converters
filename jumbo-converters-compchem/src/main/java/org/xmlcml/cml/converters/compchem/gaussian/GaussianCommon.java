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
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GaussianCommon.class);
	
	private static final String GAUSSIAN_PREFIX = "gaussian";
	private static final String GAUSSIAN_URI = "http://wwmm.ch.cam.ac.uk/dict/gaussian";
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
