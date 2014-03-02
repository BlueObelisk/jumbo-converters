package org.xmlcml.cml.converters.reaction;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Reaction system
 * @author pm286
 *
 */
public class ReactionCommon extends AbstractCommon {

	private static final String REACTION_PREFIX = "reaction";
	private static final String REACTION_URI = "http://wwmm.ch.cam.ac.uk/dict/reaction";

	public static final String INPUT = "reaction_input";

	public static final String LOG = "reaction_log";
	public static final String LOG_XML = "reaction_log_xml";
	public static final String LOG_CML = "reaction_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/reaction/reactionDictionary.xml";
    }

	public String getPrefix() {
		return REACTION_PREFIX;
	}

	public String getNamespace() {
		return REACTION_URI;
	}

}
