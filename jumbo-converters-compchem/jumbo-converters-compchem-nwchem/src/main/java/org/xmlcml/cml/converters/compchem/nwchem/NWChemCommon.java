package org.xmlcml.cml.converters.compchem.nwchem;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class NWChemCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(NWChemCommon.class);
	
	private static final String NWCHEM_PREFIX = "nwchem";
	private static final String NWCHEM_URI = "http://wwmm.ch.cam.ac.uk/dict/nwchem";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/nwchem/nwchemDict.xml";
    }
    
	public String getPrefix() {
		return NWCHEM_PREFIX;
	}
	
	public String getNamespace() {
		return NWCHEM_URI;
	}
	
}
