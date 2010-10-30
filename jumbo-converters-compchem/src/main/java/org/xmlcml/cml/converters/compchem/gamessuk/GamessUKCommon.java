package org.xmlcml.cml.converters.compchem.gamessuk;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the GamessUK system
 * @author pm286
 *
 */
public class GamessUKCommon extends AbstractCommon {
	private final static Logger LOG = Logger.getLogger(GamessUKCommon.class);
	
	private static final String GAMESSUK_PREFIX = "gamessuk";
	private static final String GAMESSUK_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessuk";
	
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
