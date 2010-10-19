package org.xmlcml.cml.converters.compchem.gamessus;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the GamessUS system
 * @author pm286
 *
 */
public class GamessUSCommon extends AbstractCommon {
	private final static Logger LOG = Logger.getLogger(GamessUSCommon.class);
	
	private static final String GAMESSUS_PREFIX = "gamessus";
	private static final String GAMESSUS_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessus";
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/gamessus/gamessusDict.xml";
    }
    
	public String getPrefix() {
		return GAMESSUS_PREFIX;
	}
	
	public String getNamespace() {
		return GAMESSUS_URI;
	}
	
}
