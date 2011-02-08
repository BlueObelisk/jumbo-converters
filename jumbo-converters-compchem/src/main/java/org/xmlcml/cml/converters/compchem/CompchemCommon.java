package org.xmlcml.cml.converters.compchem;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class CompchemCommon extends AbstractCommon {
	private final static Logger LOG = Logger.getLogger(CompchemCommon.class);
	
	private static final String COMPCHEM_PREFIX = "compchem";
	private static final String COMPCHEM_URI = "http://wwmm.ch.cam.ac.uk/dict/compchem";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/compchemDict.xml";
    }
    
	public String getPrefix() {
		return COMPCHEM_PREFIX;
	}
	
	public String getNamespace() {
		return COMPCHEM_URI;
	}
	
}
