package org.xmlcml.cml.converters.compchem.amber;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class AmberCommon extends AbstractCommon {
	
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(AmberCommon.class);
	
	private static final String AMBER_PREFIX = "amber";
	private static final String AMBER_URI = "http://wwmm.ch.cam.ac.uk/dict/amber";
	
	public static final String AMBER_FF                 = "amber-ff";
	public static final String AMBER_FF_XML             = "amber-ff-xml";
	public static final String AMBER_FF_CML             = "amber-ff-cml";

	public static final String AMBER_MD_XML = "amber-mdout-xml";
	public static final String AMBER_MD_CML = "amber-mdout-cml";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/amber/amberDict.xml";
    }
    
	public String getPrefix() {
		return AMBER_PREFIX;
	}
	
	public String getNamespace() {
		return AMBER_URI;
	}
	
}
