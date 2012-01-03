package org.xmlcml.cml.converters.compchem.gamessuk;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the GamessUK system
 * @author pm286
 *
 */
public class GamessukCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GamessukCommon.class);
	
	private static final String GAMESSUK_PREFIX = "gamessuk";
	private static final String GAMESSUK_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessuk";

	public static final String GAMESSUK_LOG = "gamessuk_log";
	public static final String GAMESSUK_LOG_XML = "gamessuk_log_xml";

	public static final String PUNCH = "punch";
	public static final String PUNCH_XML = "gamessuk_punch";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/gamessuk/gamessukDict.xml";
    }
    
	public String getPrefix() {
		return GAMESSUK_PREFIX;
	}
	
	public String getNamespace() {
		return GAMESSUK_URI;
	}
	
}
