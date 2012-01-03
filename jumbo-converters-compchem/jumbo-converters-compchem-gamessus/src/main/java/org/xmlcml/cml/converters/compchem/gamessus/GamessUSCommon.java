package org.xmlcml.cml.converters.compchem.gamessus;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the GamessUS system
 * @author pm286
 *
 */
public class GamessUSCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GamessUSCommon.class);
	
	public static final String GAMESSUS_PREFIX = "gamessus";
	public static final String GAMESSUS_URI = "http://wwmm.ch.cam.ac.uk/dict/gamessus";

	public static final String NSERCH = "NSERCH";
	public static final String STEP = "STEP";
	public static final String RESULTS = "RESULTS";
	public static final String KEYWORD = " $";
	public static final String END = " $END";
	public static final String NCYC = "ncyc";

	public static final String GAMESSUS_LOG = "gamesuss_log";
	public static final String GAMESSUS_LOG_XML = "gamess_log_xml";
	public static final String GAMESSUS_LOG_CML = "gamess_log_cml";

	public static final String PUNCH = "gamessus_punch";
	public static final String PUNCH_XML = "gamessus_punch_xml";
	
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
