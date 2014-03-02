package org.xmlcml.cml.converters.compchem.foo;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class FooCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(FooCommon.class);
	
	private static final String FOO_PREFIX = "foo";
	private static final String FOO_URI = "http://wwmm.ch.cam.ac.uk/dict/foo";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/foo/fooDictionary.xml";
    }
    
	public String getPrefix() {
		return FOO_PREFIX;
	}
	
	public String getNamespace() {
		return FOO_URI;
	}
	
}
